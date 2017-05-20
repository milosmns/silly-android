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
import android.view.ViewGroup;

import me.angrybyte.sillyandroid.SillyAndroid;
import me.angrybyte.sillyandroid.parsable.LayoutWrapper;

/**
 * An extension of the {@link android.view.ViewGroup} with applied extensions from the {@link SillyAndroid} extension set.
 */
@UiThread
@SuppressWarnings("unused")
public abstract class EasyViewGroup extends ViewGroup implements LayoutWrapper {

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

    /**
     * Returns the result from {@link SillyAndroid#findViewById(android.view.View, int)}.
     */
    public final <ViewType extends android.view.View> ViewType findView(@IdRes final int viewId) {
        return SillyAndroid.findViewById(this, viewId);
    }

    /**
     * Returns the result from {@link ContextCompat#getDrawable(Context, int)}.
     */
    @Nullable
    public Drawable getDrawableCompat(@DrawableRes final int drawableId) {
        return ContextCompat.getDrawable(getContext(), drawableId);
    }

    /**
     * Invokes {@link ViewCompat#setBackground(android.view.View, Drawable)} with the same arguments.
     */
    public void setBackgroundCompat(@Nullable final Drawable drawable) {
        ViewCompat.setBackground(this, drawable);
    }

    /**
     * Invokes {@link SillyAndroid#setPadding(android.view.View, int, int, int, int)} with the same arguments.
     */
    public void setPadding(@Px final int start, @Px final int top, @Px final int end, @Px final int bottom) {
        SillyAndroid.setPadding(this, start, top, end, bottom);
    }

    /**
     * Invokes {@link SillyAndroid#setPadding(android.view.View, int)} with the same arguments.
     */
    public void setPadding(@Px final int padding) {
        SillyAndroid.setPadding(this, padding);
    }

    /**
     * Invokes {@link SillyAndroid#toastShort(Context, int)}.
     */
    public void toastShort(@StringRes final int stringId) {
        SillyAndroid.toastShort(getContext(), stringId);
    }

    /**
     * Invokes {@link SillyAndroid#toastShort(Context, String)}.
     */
    public void toastShort(@NonNull final String string) {
        SillyAndroid.toastShort(getContext(), string);
    }

    /**
     * Invokes {@link SillyAndroid#toastLong(Context, int)}.
     */
    public void toastLong(@StringRes final int stringId) {
        SillyAndroid.toastLong(getContext(), stringId);
    }

    /**
     * Invokes {@link SillyAndroid#toastLong(Context, String)}.
     */
    public void toastLong(@NonNull final String string) {
        SillyAndroid.toastLong(getContext(), string);
    }

}
