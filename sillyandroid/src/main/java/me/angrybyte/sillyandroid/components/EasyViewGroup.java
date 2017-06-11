package me.angrybyte.sillyandroid.components;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.Px;
import android.support.annotation.RequiresApi;
import android.support.annotation.StringRes;
import android.support.annotation.UiThread;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import me.angrybyte.sillyandroid.SillyAndroid;
import me.angrybyte.sillyandroid.parsable.LayoutWrapper;

/**
 * An extension of the {@link android.view.ViewGroup} with applied extensions from the {@link SillyAndroid} extension set.
 */
@UiThread
@SuppressWarnings("unused")
public abstract class EasyViewGroup extends ViewGroup implements LayoutWrapper {

    // <editor-fold desc="Constructors">

    /**
     * {@inheritDoc}
     */
    public EasyViewGroup(@NonNull final Context context) {
        super(context);
    }

    /**
     * {@inheritDoc}
     */
    public EasyViewGroup(@NonNull final Context context, @NonNull final AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * {@inheritDoc}
     */
    public EasyViewGroup(@NonNull final Context context, @NonNull final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * {@inheritDoc}
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public EasyViewGroup(@NonNull final Context context, @NonNull final AttributeSet attrs, final int defStyleAttr, final int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
    // </editor-fold>

    //<editor-fold desc="Public API">

    /**
     * Returns the result from {@link SillyAndroid#findViewById(android.view.View, int)}.
     */
    public final <ViewType extends View> ViewType findView(@IdRes final int viewId) {
        return SillyAndroid.findViewById(this, viewId);
    }

    /**
     * Invokes {@link ViewCompat#setBackground(android.view.View, Drawable)} with the same arguments.
     */
    public final void setBackgroundCompat(@Nullable final Drawable drawable) {
        ViewCompat.setBackground(this, drawable);
    }

    /**
     * Invokes the {@link SillyAndroid#setPaddingVertical(View, int)} with the same arguments.
     */
    public final void setPaddingVertical(@Px final int padding) {
        SillyAndroid.setPaddingVertical(this, padding);
    }

    /**
     * Invokes the {@link SillyAndroid#setPaddingHorizontal(View, int)} with the same arguments.
     */
    public final void setPaddingHorizontal(@Px final int padding) {
        SillyAndroid.setPaddingHorizontal(this, padding);
    }

    /**
     * Invokes {@link SillyAndroid#setPadding(android.view.View, int, int, int, int)} with the same arguments.
     */
    public final void setPaddingR(@Px final int start, @Px final int top, @Px final int end, @Px final int bottom) {
        SillyAndroid.setPadding(this, start, top, end, bottom);
    }

    /**
     * Invokes {@link SillyAndroid#setPadding(android.view.View, int)} with the same arguments.
     */
    public final void setPaddingR(@Px final int padding) {
        SillyAndroid.setPadding(this, padding);
    }
    //</editor-fold>

    // <editor-fold desc="Internal methods">

    /**
     * Returns the result from {@link SillyAndroid#equal(Object, Object)}.
     */
    protected final boolean equal(@Nullable final Object first, @Nullable final Object second) {
        return SillyAndroid.equal(first, second);
    }

    /**
     * Returns the result from {@link ContextCompat#getDrawable(Context, int)}.
     */
    @Nullable
    protected final Drawable getDrawableCompat(@DrawableRes final int drawableId) {
        return ContextCompat.getDrawable(getContext(), drawableId);
    }
    // </editor-fold>

    // <editor-fold desc="Toasts">

    /**
     * Invokes {@link SillyAndroid#toastShort(Context, int)}.
     */
    protected final void toastShort(@StringRes final int stringId) {
        SillyAndroid.toastShort(getContext(), stringId);
    }

    /**
     * Invokes {@link SillyAndroid#toastShort(Context, String)}.
     */
    protected final void toastShort(@NonNull final String string) {
        SillyAndroid.toastShort(getContext(), string);
    }

    /**
     * Invokes {@link SillyAndroid#toastLong(Context, int)}.
     */
    protected final void toastLong(@StringRes final int stringId) {
        SillyAndroid.toastLong(getContext(), stringId);
    }

    /**
     * Invokes {@link SillyAndroid#toastLong(Context, String)}.
     */
    protected final void toastLong(@NonNull final String string) {
        SillyAndroid.toastLong(getContext(), string);
    }
    // </editor-fold>

}
