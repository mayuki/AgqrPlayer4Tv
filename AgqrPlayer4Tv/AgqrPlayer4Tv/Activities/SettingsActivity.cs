using AgqrPlayer4Tv.Components.Fragments.GuidedStep;
using Android.App;
using Android.OS;
using Android.Support.V17.Leanback.App;

namespace AgqrPlayer4Tv.Activities
{
    [Activity(Label = "SettingsActivity")]
    public class SettingsActivity : Activity
    {
        protected override void OnCreate(Bundle savedInstanceState)
        {
            base.OnCreate(savedInstanceState);

            if (savedInstanceState == null)
            {
                GuidedStepFragment.AddAsRoot(this, new SettingsGuidedStepFragment(), Android.Resource.Id.Content);
            }
        }
    }
}