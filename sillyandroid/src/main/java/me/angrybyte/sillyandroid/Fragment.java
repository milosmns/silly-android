package me.angrybyte.sillyandroid;

import android.support.annotation.IdRes;
import android.support.annotation.UiThread;
import android.view.View;

/**
 * An extension of {@link android.support.v4.app.Fragment} with applied extensions from {@link SillyAndroid} extension set.
 */
@SuppressWarnings("unused")
@UiThread
public class Fragment extends android.support.v4.app.Fragment {

    /**
     * Returns the result from {@link SillyAndroid#findViewById(android.support.v4.app.Fragment, int)}.
     */
    public <ViewType extends View> ViewType findView(@IdRes final int viewId) {
        return SillyAndroid.findViewById(this, viewId);
    }

}
