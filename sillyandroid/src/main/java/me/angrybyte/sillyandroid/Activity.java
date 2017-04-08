
package me.angrybyte.sillyandroid;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.database.Cursor;
import android.os.Build;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.view.View;

import java.io.Closeable;

/**
 * An extension of {@link AppCompatActivity} with applied extensions from {@link SillyAndroid} extension set.
 */
@UiThread
@SuppressWarnings("unused")
public class Activity extends AppCompatActivity {

    /**
     * Returns the result from {@link SillyAndroid#findViewById(android.app.Activity, int)}.
     */
    public <ViewType extends View> ViewType findView(@IdRes int viewId) {
        return SillyAndroid.findViewById(this, viewId);
    }

    /**
     * Returns the result from {@link SillyAndroid#isEmpty(String)}.
     */
    public static boolean isEmpty(@Nullable String text) {
        return SillyAndroid.isEmpty(text);
    }

    /**
     * Returns the result from {@link SillyAndroid#dismiss(PopupMenu)}.
     */
    public static boolean dismiss(@Nullable PopupMenu menu) {
        return SillyAndroid.dismiss(menu);
    }

    /**
     * Returns the result from {@link SillyAndroid#dismiss(Dialog)}.
     */
    public static boolean dismiss(@Nullable Dialog dialog) {
        return SillyAndroid.dismiss(dialog);
    }

    /**
     * Returns the result from {@link SillyAndroid#close(Closeable)}.
     */
    public static boolean close(@Nullable Closeable closeable) {
        return SillyAndroid.close(closeable);
    }

    /**
     * Returns the result from {@link SillyAndroid#close(Cursor)}.
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public static boolean close(@Nullable Cursor cursor) {
        return SillyAndroid.close(cursor);
    }

}
