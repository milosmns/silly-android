package me.angrybyte.sillyandroid;

import android.app.Activity;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

/**
 * This is the library basis. It contains methods used to customize and adapt system-provided Android components.
 */
@SuppressWarnings("WeakerAccess")
public final class SillyAndroid {

    /* View plugins */

    /**
     * Does exactly the same thing as calling {@link View#findViewById(int)}, but casts the result to the appropriate View sub-class.
     */
    @Nullable
    public static <ViewType extends View> ViewType findViewById(@NonNull final View container, @IdRes final int viewId) {
        // noinspection unchecked
        return (ViewType) container.findViewById(viewId);
    }

    /**
     * Does exactly the same thing as calling {@link Activity#findViewById(int)}, but casts the result to the appropriate View sub-class.
     */
    @Nullable
    public static <ViewType extends View> ViewType findViewById(@NonNull final Activity activity, @IdRes final int viewId) {
        // noinspection unchecked
        return (ViewType) activity.findViewById(viewId);
    }

    /**
     * Does exactly the same thing as calling {@link Fragment#getView()}.{@link #findViewById(View, int)},
     * but casts the result to the appropriate View sub-class.
     */
    @Nullable
    public static <ViewType extends View> ViewType findViewById(@NonNull final Fragment fragment, @IdRes final int viewId) {
        // noinspection unchecked
        return fragment.getView() == null ? null : (ViewType) fragment.getView().findViewById(viewId);
    }

}
