using System;
using System.Linq;
using System.Reactive.Linq;
using AgqrPlayer4Tv.Infrastracture;
using Reactive.Bindings;
using Reactive.Bindings.Extensions;

namespace AgqrPlayer4Tv.Model
{
    public class NowPlaying : DisposableModelBase
    {
        public IReadOnlyReactiveProperty<TimetableProgram> Program { get; private set; }

        public IReadOnlyReactiveProperty<string> Title { get; private set; }
        public IReadOnlyReactiveProperty<string> Subtitle { get; private set; }
        public IReadOnlyReactiveProperty<string> Body { get; private set; }

        public NowPlaying(Timetable timetable)
        {
            this.Program = Observable.Return(0L).Merge(Observable.Interval(TimeSpan.FromSeconds(10)))
                .SelectMany(async x => await timetable.GetDatasetAsync())
                .Select(x => x.Data[LogicalDateTime.Now.DayOfWeek].FirstOrDefault(y => y.IsNowPlaying))
                .ToReadOnlyReactiveProperty()
                .AddTo(this);

            this.Title = this.Program.Select(x => x?.Title ?? "超A&G+").ToReadOnlyReactiveProperty().AddTo(this);
            this.Subtitle = this.Program.Select(x => x?.MailAddress ?? "").ToReadOnlyReactiveProperty().AddTo(this);
            this.Body = this.Program.Select(x => x != null ? $"{x.Start.TotalHours.ToString("00")}:{x.Start.Minutes.ToString("00")}～{x.End.TotalHours.ToString("00")}:{x.End.Minutes.ToString("00")}" : "")
                .ToReadOnlyReactiveProperty()
                .AddTo(this);
        }
    }
}