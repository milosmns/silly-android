
package me.angrybyte.sillyandroid;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.database.Cursor;
import android.os.Build;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.View;

import java.io.Closeable;

/**
 * This is the library basis. It contains methods used to customize and adapt system-provided Android components.
 */
@SuppressWarnings("WeakerAccess")
public class SillyAndroid {

    /* View plugins */

    /**
     * Does exactly the same thing as calling {@link View#findViewById(int)}, but casts the result to the appropriate View sub-class.
     */
    @Nullable
    public static <ViewType extends View> ViewType findViewById(@NonNull View container, @IdRes int viewId) {
        // noinspection unchecked
        return (ViewType) container.findViewById(viewId);
    }

    /**
     * Does exactly the same thing as calling {@link Activity#findViewById(int)}, but casts the result to the appropriate View sub-class.
     */
    @Nullable
    public static <ViewType extends View> ViewType findViewById(@NonNull Activity activity, @IdRes int viewId) {
        // noinspection unchecked
        return (ViewType) activity.findViewById(viewId);
    }

    /**
     * Does exactly the same thing as calling {@link Fragment#getView()}.{@link #findViewById(View, int)}, but casts the result to the
     * appropriate View sub-class.
     */
    @Nullable
    public static <ViewType extends View> ViewType findViewById(@NonNull Fragment fragment, @IdRes int viewId) {
        // noinspection unchecked
        return fragment.getView() == null ? null : (ViewType) fragment.getView().findViewById(viewId);
    }

    /* Non-safe operations made safe */

    /**
     * Similar to {@link android.text.TextUtils#isEmpty(CharSequence)}, but also trims the String before checking. This means that checking
     * if {@code ' '} or {@code '\n'} are empty returns {@code true}.
     *
     * @param text Which String to test
     * @return {@code False} if the given text contains something other than whitespace, {@code true} otherwise
     */
    public static boolean isEmpty(@Nullable String text) {
        return text == null || text.trim().isEmpty();
    }

    /**
     * Tries to dismiss the given {@link PopupMenu}.
     *
     * @param menu Which menu to dismiss
     * @return {@code True} if the given menu is not {@code null} and dismiss was invoked; {@code false} otherwise
     */
    public static boolean dismiss(@Nullable PopupMenu menu) {
        if (menu != null) {
            menu.dismiss();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Tries to dismiss the given {@link Dialog}.
     *
     * @param dialog Which dialog to dismiss
     * @return {@code True} if the given dialog is not {@code null}, it is currently showing and dismiss was invoked; {@code false}
     *         otherwise
     */
    public static boolean dismiss(@Nullable Dialog dialog) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Tries to close the given {@link Closeable} object without crashing.
     *
     * @param closeable Which closeable to close
     * @return {@code True} if the given closeable is not {@code null}, and close was invoked successfully; {@code false} otherwise
     */
    public static boolean close(@Nullable Closeable closeable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && closeable instanceof Cursor) {
            return SillyAndroid.close((Cursor) closeable);
        }

        if (closeable != null) {
            try {
                closeable.close();
                return true;
            } catch (Throwable closeError) {
                Log.e(closeable.getClass().getSimpleName(), "Failed to close resource", closeError);
            }
        }
        return false;
    }

    /**
     * Tries to close the given {@link Cursor} object without crashing.
     *
     * @param cursor Which cursor to close
     * @return {@code True} if the given cursor is not {@code null}, not closed, and close was invoked successfully; {@code false} otherwise
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public static boolean close(@Nullable Cursor cursor) {
        if (cursor != null && !cursor.isClosed()) {
            try {
                cursor.close();
                return true;
            } catch (Throwable closeError) {
                Log.e(cursor.getClass().getSimpleName(), "Failed to close resource", closeError);
            }
        }
        return false;
    }

}
