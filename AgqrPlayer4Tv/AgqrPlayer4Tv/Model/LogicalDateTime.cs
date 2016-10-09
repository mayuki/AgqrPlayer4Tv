using System;
using System.Diagnostics;
using System.Text.RegularExpressions;

namespace AgqrPlayer4Tv.Model
{
    /// <summary>
    /// 論理時刻と曜日を保持するクラスです。水曜25時(=木曜1時)といった値を扱います。
    /// </summary>
    [DebuggerDisplay("LogicalDateTime: {DayOfWeek} {Time},nq")]
    public struct LogicalDateTime
    {
        public DayOfWeek DayOfWeek { get; }
        public TimeSpan Time { get; }

        public LogicalDateTime(DayOfWeek dayOfWeek, TimeSpan time)
        {
            this.DayOfWeek = dayOfWeek;
            this.Time = time;
        }

        /// <summary>
        /// 論理時刻と曜日を取得します。
        /// </summary>
        /// <returns></returns>
        public static LogicalDateTime Now
        {
            get
            {
                var now = DateTime.Now;
                var nowTime = now.TimeOfDay;
                var nowDayOfWeek = now.DayOfWeek;
                if (nowTime.TotalHours >= 0 && nowTime.TotalHours <= 5)
                {
                    // 24時間を過ぎてカウントできるようにTimeSpanに。
                    nowTime = nowTime.Add(TimeSpan.FromHours(24));
                    // 前の曜日に戻す必要がある
                    nowDayOfWeek = (nowDayOfWeek == DayOfWeek.Sunday)
                        ? DayOfWeek.Saturday
                        : nowDayOfWeek - 1;
                }

                return new LogicalDateTime(nowDayOfWeek, nowTime);
            }
        }

        /// <summary>
        /// 30時間形式で文字列をパースしてTimeSpanを返します。
        /// </summary>
        /// <param name="value"></param>
        /// <returns></returns>
        public static TimeSpan ParseTimeSpanFor30Hours(string value)
        {
            var time = new TimeSpan(0);
            var match = Regex.Match(value, @"^(\d{1,2}):(\d{1,2})");
            if (!match.Success) throw new ArgumentException("invalid time format");

            var hours = Double.Parse(match.Groups[1].Value);

            return new TimeSpan()
                .Add(TimeSpan.FromHours((hours >= 0 && hours <= 5) ? 24 + hours : hours))
                .Add(TimeSpan.FromMinutes(Double.Parse(match.Groups[2].Value)));
        }
    }
}