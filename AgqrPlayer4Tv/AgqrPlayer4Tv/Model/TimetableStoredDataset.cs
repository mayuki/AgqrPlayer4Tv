using System;
using System.Collections.Generic;

namespace AgqrPlayer4Tv.Model
{
    public class TimetableStoredDataset
    {
        public Dictionary<DayOfWeek, List<TimetableProgram>> Data { get; set; }
        public int Version { get; set; }
        public DateTime UpdatedAt { get; set; }

        public bool IsExpired
        {
            get { return (DateTime.Now - this.UpdatedAt).TotalHours > 24; }
        }
    }
}