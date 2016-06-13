using System;
using System.Diagnostics;

namespace AgqrPlayer4Tv.Model
{
    [DebuggerDisplay("TimetableProgram: {Title} <{MailAddress}> {Start}-{End},nq")]
    public class TimetableProgram
    {
        public TimeSpan Start { get; set; }
        public TimeSpan End { get; set; }
        public string Title { get; set; }
        public string MailAddress { get; set; }

        public bool IsNowPlaying
        {
            get { return this.Start <= LogicalDateTime.Now.Time && this.End >= LogicalDateTime.Now.Time; }
        }
    }
}