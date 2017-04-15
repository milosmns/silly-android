package me.angrybyte.sillyandroid.parsable;

import android.support.annotation.IdRes;
import android.view.View;

/**
 * A quick and easy way to denote all wrapper classes capable of looking up Views by their IDs.
 */
@FunctionalInterface
public interface LayoutWrapper {

    /**
     * Used for View lookup in the {@link AnnotationParser}. The easiest way to implement this is to return the result from
     * {@link me.angrybyte.sillyandroid.SillyAndroid#findViewById(View, int)} or one of its overloads.
     *
     * @param viewId     Which View to look for
     * @param <ViewType> The type of the View (note that this method is type-safe)
     * @return The View instance, or {@code null} if the View was not found
     */
    <ViewType extends View> ViewType findView(@IdRes final int viewId);

}
