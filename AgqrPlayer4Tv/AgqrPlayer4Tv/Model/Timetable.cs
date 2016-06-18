using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net;
using System.Text;
using System.Text.RegularExpressions;
using System.Threading.Tasks;
using AgqrPlayer4Tv.Infrastracture;
using Android.Util;
using Newtonsoft.Json;

namespace AgqrPlayer4Tv.Model
{
    public class Timetable : DisposableModelBase
    {
        private const string Tag = "Timetable";
        private static object _syncObject = new object();

        private readonly string _cachePath;
        private TimetableStoredDataset _cachedTimetableStoredDataset;

        public Timetable(string cachePath)
        {
            this._cachePath = cachePath;
        }

        public async Task<TimetableStoredDataset> GetDatasetAsync()
        {
            TimetableStoredDataset timetableDataset = null;
            lock (_syncObject)
            {
                if (!(this._cachedTimetableStoredDataset?.IsExpired ?? true))
                {
                    return this._cachedTimetableStoredDataset;
                }

                if (File.Exists(this._cachePath))
                {
                    try
                    {
                        var json = File.ReadAllText(this._cachePath, Encoding.UTF8);
                        timetableDataset = JsonConvert.DeserializeObject<TimetableStoredDataset>(json);
                        if (timetableDataset.IsExpired)
                        {
                            timetableDataset = null;
                            Log.Debug(Tag, "LoadTimetableDataset: read from cached JSON, but a cache file is expired.");
                        }
                        else
                        {
                            Log.Debug(Tag, "LoadTimetableDataset: read from cached JSON.");
                            this._cachedTimetableStoredDataset = timetableDataset;
                        }
                    }
                    catch
                    {
                    }
                }
            }

            if (timetableDataset == null)
            {
                Log.Debug(Tag, "LoadTimetableDataset: Cache miss; fetch and store timetable dataset.");
                try
                {
                    timetableDataset = await this.FetchAndParseAsync();
                    lock (_syncObject)
                    {
                        this._cachedTimetableStoredDataset = timetableDataset;
                        File.WriteAllText(this._cachePath, JsonConvert.SerializeObject(timetableDataset), Encoding.UTF8);
                    }
                }
                catch (Exception ex)
                {
                    HockeyApp.Metrics.MetricsManager.TrackEvent("Failed.Timetable-FetchAndParseAsync");

                    Log.Error(Tag, ex.ToString());
                    var programsByDayofweek = new Dictionary<DayOfWeek, List<TimetableProgram>>();
                    for (var i = 0; i < 7; i++) { programsByDayofweek[(DayOfWeek)i] = new List<TimetableProgram>(); }

                    // 1時間後にまたExpireして取りに行き直す
                    timetableDataset = this._cachedTimetableStoredDataset = new TimetableStoredDataset() { UpdatedAt = DateTime.Now - TimeSpan.FromHours(23), Version = 1, Data = programsByDayofweek };
                }
            }

            return timetableDataset;
        }

        public async Task<TimetableStoredDataset> FetchAndParseAsync()
        {
            var timetableHtml = Encoding.UTF8.GetString(await new WebClient().DownloadDataTaskAsync(new Uri("http://www.agqr.jp/timetable/streaming.html")));

            var tableE = Regex.Match(timetableHtml, "<table [^>]*class=\"timetb-ag\"[^>]*>(.*?)</table>", RegexOptions.Singleline);
            var rows = Regex.Matches(tableE.Value, "<t[rh][^>]*>(.*?)</tr>", RegexOptions.Singleline); // なんとtrが抜けてるケースがある…
            var rowspans = new Dictionary<DayOfWeek, int>();
            for (var i = 0; i < 7; i++) { rowspans[(DayOfWeek)i] = 0; }
            var programsByDayofweek = new Dictionary<DayOfWeek, List<TimetableProgram>>();
            for (var i = 0; i < 7; i++) { programsByDayofweek[(DayOfWeek)i] = new List<TimetableProgram>(); }

            foreach (var row in rows.Cast<Match>().Skip(1))
            {
                //"----------".Dump();
                var cols = Regex.Matches(row.Value, "<td[^>]*(?:rowspan=\"(\\d+)\")?[^>]*>(.*?)</td>", RegexOptions.Singleline);
                var colIndex = 0;
                for (var dayOfWeekIndex = 0; dayOfWeekIndex < 7; dayOfWeekIndex++)
                {
                    var dayOfWeek = (DayOfWeek)(dayOfWeekIndex == 6 ? 0 : dayOfWeekIndex + 1);
                    var rowspan = rowspans[dayOfWeek];
                    if (rowspan > 0)
                    {
                        rowspans[dayOfWeek]--;
                        //$"{dayOfWeek}: -".Dump();
                        continue;
                    }
                    var col = cols[colIndex++];

                    var rowspanAttr = Regex.Match(col.Value, "<td[^>]*(?:rowspan=\"(\\d+)\")[^>]*>", RegexOptions.Singleline);
                    if (rowspanAttr.Groups[1].Success)
                    {
                        rowspans[dayOfWeek] = Int32.Parse(rowspanAttr.Groups[1].Value) - 1;
                    }
                    var titleE = Regex.Match(col.Value, "<div class=\"title-p\">(.*?)</div>", RegexOptions.Singleline);
                    var mailtoE = Regex.Match(col.Value, "<a href=\"mailto:([^\"]+)", RegexOptions.Singleline);
                    var timeE = Regex.Match(col.Value, "<div class=\"time\">(?:.*?)(\\d+:\\d+)", RegexOptions.Singleline);
                    var title = Regex.Replace(titleE.Groups[1].Value.Trim(), "<[^>]+>", "");
                    var time = TimeSpan.Parse(timeE.Groups[1].Value.Trim());
                    var mailto = mailtoE.Success ? Uri.UnescapeDataString(mailtoE.Groups[1].Value).Trim() : "";
                    //$"{dayOfWeek}: [{time}] {title} / {mailto}".Dump();

                    if (time.TotalHours >= 0 && time.TotalHours <= 5)
                    {
                        time = time.Add(TimeSpan.FromHours(24));
                    }

                    if (programsByDayofweek[dayOfWeek].Any())
                    {
                        programsByDayofweek[dayOfWeek].Last().End = time.Add(TimeSpan.FromSeconds(-1));
                    }
                    if (title == "放送休止")
                    {
                        continue;
                    }
                    programsByDayofweek[dayOfWeek].Add(new TimetableProgram { Start = time, Title = title, MailAddress = mailto });
                }
            }

            //programsByDayofweek[DateTime.Today.DayOfWeek].Dump();

            return new TimetableStoredDataset
            {
                Version = 1,
                UpdatedAt = DateTime.Now,
                Data = programsByDayofweek,
            };
        }
    }
}