using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using AgqrPlayer4Tv.Activities;
using Android.App;
using Android.Content;
using Android.OS;
using Android.Runtime;
using Android.Support.V17.Leanback.App;
using Android.Views;
using Android.Widget;

namespace AgqrPlayer4Tv.Components.Fragments
{
    public class ConnectionErrorFragment : ErrorFragment, View.IOnClickListener
    {
        public override void OnCreate(Bundle savedInstanceState)
        {
            base.OnCreate(savedInstanceState);

            this.Title = "AgqrPlayer for Android TV";
            this.SetErrorContent();
        }

        private void SetErrorContent()
        {
            ImageDrawable = Activity.GetDrawable(Resource.Drawable.lb_ic_sad_cloud);
            Message = "íZéûä‘ÇÃä‘Ç…òAë±Ç≈ê⁄ë±Ç…é∏îsÇµÇ‹ÇµÇΩÅB";
            SetDefaultBackground(true);

            ButtonText = "ê›íËÇäJÇ≠";
            ButtonClickListener = this;
        }

        public void OnClick(View v)
        {
            this.Activity.Finish();
            this.StartActivity(new Intent(this.Activity.ApplicationContext, typeof(SettingsActivity)));
        }
    }
}