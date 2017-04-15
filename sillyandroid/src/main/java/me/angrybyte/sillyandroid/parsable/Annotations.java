
package me.angrybyte.sillyandroid.parsable;

import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.MenuRes;
import android.support.annotation.Nullable;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Holds references to all available annotations that {@link AnnotationParser} recognizes.
 */
@SuppressWarnings("WeakerAccess")
public final class Annotations {

    /**
     * Denotes a clickable View where the {@link AnnotationParser} can set a click listener.
     */
    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Clickable {}

    /**
     * Denotes a long-clickable View where the {@link AnnotationParser} can set a long click listener.
     */
    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface LongClickable {}

    /**
     * Specifies the layout to be set using the {@link AnnotationParser}.
     */
    @Inherited
    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface Layout {

        /**
         * Android layout resource ID.
         */
        @LayoutRes int value() default -1;

        /**
         * Android layout resource name (slow, use only in libraries where ID is not final).
         */
        @Nullable String name() default "";
    }

    /**
     * Specifies the menu to be set using the {@link AnnotationParser}.
     */
    @Inherited
    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface Menu {

        /**
         * Android menu resource ID.
         */
        @MenuRes int value() default -1;

        /**
         * Android menu resource name (slow, use only in libraries where ID is not final).
         */
        @Nullable String name() default "";
    }

    /**
     * Specifies the View ID to be found and set using the {@link AnnotationParser}.
     */
    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface FindView {

        /**
         * Android View resource ID.
         */
        @IdRes int value() default -1;

        /**
         * Android View resource name (slow, use only in libraries where ID is not final).
         */
        @Nullable String name() default "";

        /**
         * Set to {@code true} to prevent crashing when something goes wrong. Check logs for details.
         */
        boolean safeFail() default false;
    }

}
