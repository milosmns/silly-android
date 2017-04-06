package me.angrybyte.sillyandroid;

import android.support.annotation.IdRes;
import android.support.annotation.UiThread;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * An extension of {@link AppCompatActivity} with applied extensions from {@link SillyAndroid} extension set.
 */
@UiThread
@SuppressWarnings("unused")
public class Activity extends AppCompatActivity {

    /**
     * Returns the result from {@link SillyAndroid#findViewById(android.app.Activity, int)}.
     */
    public <ViewType extends View> ViewType findView(@IdRes final int viewId) {
        return SillyAndroid.findViewById(this, viewId);
    }

}
