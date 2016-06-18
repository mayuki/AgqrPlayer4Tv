using System;
using Android.Util;

namespace AgqrPlayer4Tv.Infrastracture.Extensions
{
    public static class DisplayExtension
    {
        public static int ToDevicePixel(this DisplayMetrics metric, int value)
        {
            return (int)Math.Round((float)value * metric.Density);
        }
    }
}