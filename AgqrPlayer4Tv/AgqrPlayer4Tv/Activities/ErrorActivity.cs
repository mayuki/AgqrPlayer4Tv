using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using AgqrPlayer4Tv.Components.Fragments;
using Android.App;
using Android.Content;
using Android.OS;
using Android.Runtime;
using Android.Views;
using Android.Widget;

namespace AgqrPlayer4Tv.Activities
{
    [Activity(Label = "ErrorActivity")]
    public class ErrorActivity : Activity
    {
        protected override void OnCreate(Bundle bundle)
        {
            SetContentView(Resource.Layout.Main);

            if (bundle == null)
            {
                var fragment = new ConnectionErrorFragment();
                var transaction = FragmentManager.BeginTransaction();
                transaction.Add(Resource.Id.MainFrame, fragment);
                transaction.Commit();
            }

            base.OnCreate(bundle);
        }
    }
}