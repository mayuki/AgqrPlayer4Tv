using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Http;
using System.Threading.Tasks;
using AgqrPlayer4Tv.Activities;
using AgqrPlayer4Tv.Infrastracture;
using AgqrPlayer4Tv.Model;
using Android.App;
using Android.Content;
using Android.Graphics;
using Android.Text;
using Android.Util;

namespace AgqrPlayer4Tv.Services
{
    /// <summary>
    /// Leanback Launcher (いわゆるホーム)のおすすめ一覧に表示するためのサービスです。
    /// </summary>
    [Service(Enabled = true)]
    public class UpdateRecommendationService : IntentService
    {
        private NotificationManager _notificationManager;
        private const string Tag = "UpdateRecommendationService";

        public override void OnCreate()
        {
            Log.Debug(Tag, "OnCreate");

            if (this._notificationManager == null)
            {
                this._notificationManager = (NotificationManager)this.GetSystemService(Context.NotificationService);
            }

            base.OnCreate();
        }

        protected override void OnHandleIntent(Intent intent)
        {
            Log.Debug(Tag, "OnHandleIntent");
            try
            {
                UpdateNotifications().Wait();
            }
            catch (Exception ex)
            {
                Log.Error(Tag, ex.ToString());
            }
        }

        private async Task<Dictionary<string, string>> GetProgramImageMappingAsync()
        {
            string csvData = "";
            try
            {
                using (var httpClient = new HttpClient())
                {
                    csvData = await httpClient.GetStringAsync($"https://raw.githubusercontent.com/mayuki/AgqrPlayer4Tv/master/Extra/AgqrProgramImageMapping.csv?{DateTime.Now.Ticks}");
                }
            }
            catch (Exception ex)
            {
                Log.Error(Tag, ex.ToString());
            }

            // Title,EmailAddress,ImageUrl のマッピングCSV
            return csvData.Split('\n')
                .Select(x => x.Trim())
                .Where(x => !x.StartsWith("#"))
                .Where(x => !String.IsNullOrWhiteSpace(x))
                .Select(x => x.Split(','))
                .Where(x => x.Length >= 3)
                .SelectMany(x => new[] {
                        new
                        {
                            // Title
                            Key = x[0],
                            TileImage = x[2],
                        },
                        new
                        {
                            // EmailAddress
                            Key = String.IsNullOrWhiteSpace(x[1]) ? x[0] + "-EmailAddress" : x[1], // メールアドレスがない場合もあるんじゃよ
                            TileImage = x[2],
                        }
                })
                .ToDictionary(k => k.Key, v => v.TileImage);
        }

        private async Task UpdateNotifications()
        {
            this._notificationManager.CancelAll();

            var res = Resources;
            var largeIcon = BitmapFactory.DecodeResource(res, Resource.Drawable.LargeIconEmpty);
            var colorAccent = Resources.GetColor(Resource.Color.Accent);
            var colorAccentDark = Resources.GetColor(Resource.Color.AccentDark);

            var timetable = ApplicationMain.ServiceLocator.GetInstance<Timetable>();
            var timetableDataset = timetable.GetDatasetAsync().Result;
            var now = LogicalDateTime.Now;

            // 番組と番組の画像のマッピング
            var mapping = await GetProgramImageMappingAsync().ConfigureAwait(false);

            // 最新と次の番組をとってくる
            foreach (var program in timetableDataset.Data[now.DayOfWeek].Where(x => x.End >= now.Time).Take(2))
            {
                var isNowPlaying = program.IsNowPlaying;
                var intent = new Intent(this.ApplicationContext, typeof(MainActivity));
                var builder = new Notification.Builder(this.ApplicationContext)
                    .SetContentTitle(program.Title)
                    .SetContentText(isNowPlaying ? $"現在放送中: {program.End.ToString("hh\\:mm")}まで" : $"もうすぐスタート: {program.Start.ToString("hh\\:mm")}から")
                    .SetPriority((int)(isNowPlaying ? NotificationPriority.Max : NotificationPriority.Default))
                    .SetLocalOnly(true)
                    .SetOngoing(true)
                    .SetCategory(Notification.CategoryRecommendation)
                    .SetLargeIcon(largeIcon)
                    .SetSmallIcon(Resource.Drawable.SmallIcon)
                    .SetContentIntent(PendingIntent.GetActivity(this.ApplicationContext, 0, intent, PendingIntentFlags.UpdateCurrent))
                    .SetColor(colorAccentDark);

                var builderBigPicture = new Notification.BigPictureStyle(builder);

                try
                {
                    // 画像があればそれを使うしなければ文字を描画するぞい
                    if (mapping.ContainsKey(program.MailAddress) || mapping.ContainsKey(program.Title))
                    {
                        var imageUrl = mapping.ContainsKey(program.MailAddress)
                            ? mapping[program.MailAddress]
                            : mapping[program.Title];

                        using (var httpClient = new HttpClient())
                        using (var stream = await httpClient.GetStreamAsync(imageUrl).ConfigureAwait(false))
                        {
                            var bitmap = BitmapFactory.DecodeStream(stream);
                            builder.SetLargeIcon(bitmap);
                            builderBigPicture.BigPicture(bitmap);
                        }
                    }
                    else
                    {
                        var width = Resources.GetDimensionPixelSize(Resource.Dimension.RecommendationEmptyWidth);
                        var height = Resources.GetDimensionPixelSize(Resource.Dimension.RecommendationEmptyHeight);
                        var textSize = Resources.GetDimensionPixelSize(Resource.Dimension.RecommendationTextSize);
                        textSize = Resources.DisplayMetrics.ToDevicePixel(textSize);
                        width = Resources.DisplayMetrics.ToDevicePixel(width);
                        height = Resources.DisplayMetrics.ToDevicePixel(height);
                        var bitmap = Bitmap.CreateBitmap(width, height, Bitmap.Config.Argb8888);
                        using (var paint = new Paint() { TextSize = textSize, Color = Color.White, AntiAlias = true })
                        using (var textPaint = new TextPaint() { TextSize = textSize, Color = Color.White, AntiAlias = true })
                        using (var canvas = new Canvas(bitmap))
                        {
                            canvas.Save();

                            paint.Color = colorAccent;
                            canvas.DrawRect(0, 0, bitmap.Width, bitmap.Height, paint);

                            paint.Color = Color.White;
                            var textWidth = paint.MeasureText(program.Title);

                            // 回転する
                            // canvas.Rotate(45);
                            //canvas.DrawText(program.Title, 0, 0, paint);

                            // 
                            // canvas.DrawText(program.Title, (bitmap.Width / 2) - (textWidth / 2), (bitmap.Height / 2) + (paint.TextSize / 2), paint);

                            // 折り返しつつ描画
                            var margin = 16;
                            var textLayout = new StaticLayout(program.Title, textPaint, canvas.Width - (margin * 2), Layout.Alignment.AlignCenter, 1.0f, 0.0f, false);
                            canvas.Translate(margin, (canvas.Height / 2) - (textLayout.Height / 2));
                            textLayout.Draw(canvas);

                            canvas.Restore();
                        }
                        builder.SetLargeIcon(bitmap);
                    }
                }
                catch (Exception ex)
                {
                    Log.Error(Tag, ex.ToString());
                }

                this._notificationManager.Notify(program.Start.Ticks.GetHashCode(), builderBigPicture.Build());
            }
        }
    }
}