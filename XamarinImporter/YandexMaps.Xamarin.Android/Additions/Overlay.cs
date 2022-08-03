using System;

namespace RU.Yandex.Yandexmapkit.Overlay
{
    public partial class Overlay : Java.Lang.Object, Java.Lang.IComparable
    {
        int  Java.Lang.IComparable.CompareTo(Java.Lang.Object obj)
        {
            return A((RU.Yandex.Yandexmapkit.Overlay.Overlay)obj);
        }
    }

    public partial class OverlayItem : Java.Lang.Object, Java.Lang.IComparable
    {
        int Java.Lang.IComparable.CompareTo(Java.Lang.Object obj)
        {
            return A((RU.Yandex.Yandexmapkit.Overlay.OverlayItem)obj);
        }
    }
}
