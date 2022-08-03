using System;
using Android.Runtime;

namespace RU.Yandex.Yandexmapkit.Overlay.Balloon
{
    public partial class BalloonRender : Java.Lang.Object, Java.Lang.IComparable
    {
        int Java.Lang.IComparable.CompareTo(Java.Lang.Object obj)
        {
            return (int)A();
        }

        public virtual unsafe void Draw (global::Android.Graphics.Canvas p0, global::RU.Yandex.Yandexmapkit.Overlay.OverlayItem p1)
        {
            A(p0, (BalloonItem)p1);
        }
    }
}
