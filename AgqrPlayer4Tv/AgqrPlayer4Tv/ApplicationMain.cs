using System.IO;
using AgqrPlayer4Tv.Model;
using AgqrPlayer4Tv.Model.Platform;
using Android.App;
using Microsoft.Practices.ServiceLocation;
using Microsoft.Practices.Unity;

namespace AgqrPlayer4Tv
{
    public static class ApplicationMain
    {
        static ApplicationMain()
        {
            var container = new UnityContainer();

            container.RegisterType<Timetable>(new ContainerControlledLifetimeManager(), new InjectionConstructor(Path.Combine(Application.Context.CacheDir.Path, "TimetableCache.json")));
            container.RegisterType<NowPlaying>(new ContainerControlledLifetimeManager());

            container.RegisterType<ApplicationPreference>(new ContainerControlledLifetimeManager());

            ServiceLocator = new UnityServiceLocator(container);
        }

        public static IServiceLocator ServiceLocator { get; private set; }
    }
}