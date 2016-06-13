using System;
using System.Diagnostics;

namespace AgqrPlayer4Tv.Model
{
    /// <summary>
    /// �_�������Ɨj����ێ�����N���X�ł��B���j25��(=�ؗj1��)�Ƃ������l�������܂��B
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
        /// �_�������Ɨj�����擾���܂��B
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
                    // 24���Ԃ��߂��ăJ�E���g�ł���悤��TimeSpan�ɁB
                    nowTime = nowTime.Add(TimeSpan.FromHours(24));
                    // �O�̗j���ɖ߂��K�v������
                    nowDayOfWeek = (nowDayOfWeek == DayOfWeek.Sunday)
                        ? DayOfWeek.Saturday
                        : nowDayOfWeek - 1;
                }

                return new LogicalDateTime(nowDayOfWeek, nowTime);
            }
        }
    }
}