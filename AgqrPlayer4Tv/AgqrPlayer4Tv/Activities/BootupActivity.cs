using AgqrPlayer4Tv.Services;
using Android.App;
using Android.Content;
using Android.Util;

namespace AgqrPlayer4Tv.Activities
{
    [BroadcastReceiver(Enabled = true, Exported = false)]
    [IntentFilter(new [] { Intent.ActionBootCompleted })]
    public class BootupActivity : BroadcastReceiver
    {
        private const int InitialDelay = 5000;
        private const string Tag = "BootupActivity";

        public override void OnReceive(Context context, Intent intent)
        {
            Log.Debug(Tag, "OnReceive");
            if (intent.Action.EndsWith(Intent.ActionBootCompleted))
            {
                ScheduleRecommendationUpdate(context);
            }
        }

        public static void ScheduleRecommendationUpdate(Context context)
        {
            Log.Debug(Tag, "ScheduleRecommendationUpdate");

            var alarmManager = context.GetSystemService(Context.AlarmService) as AlarmManager;
            var recommendationIndent = new Intent(context, typeof(UpdateRecommendationService));
            var alarmIntent = PendingIntent.GetService(context, 0, recommendationIndent, 0);

            alarmManager.SetInexactRepeating(AlarmType.ElapsedRealtimeWakeup, InitialDelay, AlarmManager.IntervalFifteenMinutes, alarmIntent); // 15•ª
        }
    }
}