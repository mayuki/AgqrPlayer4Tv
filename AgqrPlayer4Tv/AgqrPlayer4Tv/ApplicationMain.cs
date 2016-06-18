using System.IO;
using AgqrPlayer4Tv.Model;
using AgqrPlayer4Tv.Model.Platform;
using Android.App;
using Android.Content;
using HockeyApp;
using HockeyApp.Metrics;
using Microsoft.Practices.ServiceLocation;
using Microsoft.Practices.Unity;

namespace AgqrPlayer4Tv
{
    public static class ApplicationMain
    {
        public static bool _initialized = false;
        public static IServiceLocator ServiceLocator { get; private set; }

        static ApplicationMain()
        {
            var container = new UnityContainer();

            container.RegisterType<Timetable>(new ContainerControlledLifetimeManager(), new InjectionConstructor(Path.Combine(Application.Context.CacheDir.Path, "TimetableCache.json")));
            container.RegisterType<NowPlaying>(new ContainerControlledLifetimeManager());

            container.RegisterType<ApplicationPreference>(new ContainerControlledLifetimeManager());

            ServiceLocator = new UnityServiceLocator(container);
        }


        public static void InitializeIfNeeded(Context ctx, Application app)
        {
            if (_initialized) return;

            CrashManager.Register(ctx);
            MetricsManager.Register(ctx, app);


            _initialized = true;
        }
    }
}