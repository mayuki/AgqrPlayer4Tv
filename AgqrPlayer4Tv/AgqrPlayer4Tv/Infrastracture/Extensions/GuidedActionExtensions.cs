using System;
using System.Collections.Generic;
using Android.Support.V17.Leanback.Widget;

namespace AgqrPlayer4Tv.Infrastracture.Extensions
{
    public static class GuidedActionExtensions
    {
        public static IList<GuidedAction> AddInfo(this IList<GuidedAction> actions, int id, string title, string description)
        {
            var builder = new GuidedAction.Builder();
            builder = ((GuidedAction.Builder)builder.InfoOnly(true));
            builder = ((GuidedAction.Builder)builder.Id(id));
            builder = ((GuidedAction.Builder)builder.Title(title));
            builder = ((GuidedAction.Builder)builder.Description(description));
            builder = ((GuidedAction.Builder)builder.MultilineDescription(true));

            actions.Add(builder.Build());
            return actions;
        }
        public static IList<GuidedAction> AddAction(this IList<GuidedAction> actions, int id, string title, string description)
        {
            var builder = new GuidedAction.Builder();
            builder = ((GuidedAction.Builder)builder.Id(id));
            builder = ((GuidedAction.Builder)builder.Title(title));
            builder = ((GuidedAction.Builder)builder.Description(description));

            actions.Add(builder.Build());

            return actions;
        }
        public static IList<GuidedAction> AddCheckAction(this IList<GuidedAction> actions, int id, string title, string description, bool isChecked)
        {
            var builder = new GuidedAction.Builder();
            builder = ((GuidedAction.Builder)builder.Id(id));
            builder = ((GuidedAction.Builder)builder.Title(title));
            builder = ((GuidedAction.Builder)builder.Description(description));
            builder = ((GuidedAction.Builder)builder.CheckSetId(id));
            builder = ((GuidedAction.Builder)builder.Checked(isChecked));

            actions.Add(builder.Build());

            return actions;
        }
        public static IList<GuidedAction> AddActionWithSubActions(this IList<GuidedAction> actions, int id, string title, string description, Action<IList<GuidedAction>> subActionBuilder)
        {
            var builder = new GuidedAction.Builder();
            builder = ((GuidedAction.Builder)builder.Id(id));
            builder = ((GuidedAction.Builder)builder.Title(title));
            builder = ((GuidedAction.Builder)builder.Description(description));

            var subactions = new List<GuidedAction>();
            subActionBuilder(subactions);
            builder = ((GuidedAction.Builder)builder.SubActions(subactions));
            actions.Add(builder.Build());
            return actions;
        }
    }
}