using Android.Support.V17.Leanback.Widget;

namespace AgqrPlayer4Tv.Infrastracture.Extensions
{
    public static class ClassPresenterSelectorExtensions
    {
        public static void AddClassPresenter<T>(this ClassPresenterSelector selector, Presenter presenter)
        {
            selector.AddClassPresenter(Java.Lang.Class.FromType(typeof(T)), presenter);
        }
    }
}