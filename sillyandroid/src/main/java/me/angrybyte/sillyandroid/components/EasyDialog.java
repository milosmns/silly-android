package me.angrybyte.sillyandroid.components;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.Px;
import android.support.annotation.RequiresPermission;
import android.support.annotation.StringRes;
import android.support.annotation.StyleRes;
import android.support.annotation.UiThread;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.PopupMenu;
import android.view.View;

import java.io.Closeable;

import me.angrybyte.sillyandroid.SillyAndroid;
import me.angrybyte.sillyandroid.parsable.LayoutWrapper;

/**
 * An extension of {@link android.app.Dialog} with applied extensions from {@link SillyAndroid} extension set.
 */
@UiThread
@SuppressWarnings({"unused", "WeakerAccess"})
public class EasyDialog extends Dialog implements LayoutWrapper {

    /**
     * Creates a dialog window that uses the default dialog theme.
     * <p>
     * The supplied {@code context} is used to obtain the window manager and
     * base theme used to present the dialog.
     *
     * @param context The context in which the dialog should run
     */
    public EasyDialog(@NonNull final Context context) {
        super(context);
    }

    /**
     * Creates a dialog window that uses a custom dialog style.
     * <p>
     * The supplied {@code context} is used to obtain the window manager and
     * base theme used to present the dialog.
     * <p>
     * The supplied {@code theme} is applied on top of the context's theme. See
     * <a href="{@docRoot}guide/topics/resources/available-resources.html#stylesandthemes">
     * Style and Theme Resources</a> for more information about defining and
     * using styles.
     *
     * @param context    The context in which the dialog should run
     * @param themeResId A style resource describing the theme to use for the
     *                   window, or {@code 0} to use the default dialog theme
     */
    public EasyDialog(@NonNull final Context context, @StyleRes final int themeResId) {
        super(context, themeResId);
    }

    /**
     * @inheritDoc
     */
    protected EasyDialog(@NonNull final Context context, final boolean cancelable, @Nullable final OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    /**
     * Returns the result from {@link SillyAndroid#countIntentHandlers(Context, Intent)}.
     */
    @IntRange(from = 0)
    public int countIntentHandlers(@Nullable final Intent intent) {
        return SillyAndroid.countIntentHandlers(getContext(), intent);
    }

    /**
     * Returns the result from {@link SillyAndroid#canHandleIntent(Context, Intent)}.
     */
    public boolean canHandleIntent(@Nullable final Intent intent) {
        return SillyAndroid.canHandleIntent(getContext(), intent);
    }

    /**
     * Returns the result from {@link SillyAndroid#getContentView(android.app.Activity)}.
     */
    public <ViewType extends View> ViewType getContentView() {
        return SillyAndroid.getContentView(this);
    }

    /**
     * Returns the result from {@link SillyAndroid#findViewById(android.support.v4.app.Fragment, int)}.
     */
    public <ViewType extends View> ViewType findView(@IdRes final int viewId) {
        return SillyAndroid.findViewById(this, viewId);
    }

    /**
     * Returns the result from {@link SillyAndroid#isEmpty(String)}.
     */
    public boolean isEmpty(@Nullable final String text) {
        return SillyAndroid.isEmpty(text);
    }

    /**
     * Returns the result from {@link SillyAndroid#dismiss(PopupMenu)}.
     */
    public boolean dismiss(@Nullable final PopupMenu menu) {
        return SillyAndroid.dismiss(menu);
    }

    /**
     * Returns the result from {@link SillyAndroid#dismiss(android.app.Dialog)}.
     */
    public boolean dismiss(@Nullable final EasyDialog dialog) {
        return SillyAndroid.dismiss(dialog);
    }

    /**
     * Returns the result from {@link SillyAndroid#close(Closeable)}.
     */
    public boolean close(@Nullable final Closeable closeable) {
        return SillyAndroid.close(closeable);
    }

    /**
     * Returns the result from {@link SillyAndroid#close(Cursor)}.
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public boolean close(@Nullable final Cursor cursor) {
        return SillyAndroid.close(cursor);
    }

    /**
     * Returns the result from {@link ContextCompat#getDrawable(Context, int)}.
     */
    @Nullable
    public Drawable getDrawableCompat(@DrawableRes final int drawableId) {
        return ContextCompat.getDrawable(getContext(), drawableId);
    }

    /**
     * Invokes {@link ViewCompat#setBackground(View, Drawable)} with the same arguments.
     */
    public void setBackgroundCompat(@NonNull final EasyView view, @Nullable final Drawable drawable) {
        ViewCompat.setBackground(view, drawable);
    }

    /**
     * Invokes {@link SillyAndroid#setPadding(View, int, int, int, int)} with the same arguments.
     */
    public void setPadding(@NonNull final EasyView view, @Px final int start, @Px final int top, @Px final int end, @Px final int bottom) {
        SillyAndroid.setPadding(view, start, top, end, bottom);
    }

    /**
     * Invokes {@link SillyAndroid#setPadding(View, int)} with the same arguments.
     */
    public void setPadding(@NonNull final EasyView view, @Px final int padding) {
        SillyAndroid.setPadding(view, padding);
    }

    /**
     * Returns the result from {@link SillyAndroid#isNetworkConnected(Context)}.
     */
    @RequiresPermission(allOf = {Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.ACCESS_NETWORK_STATE})
    public boolean isNetworkConnected() {
        return SillyAndroid.isNetworkConnected(getContext());
    }

    /**
     * Returns the result from {@link SillyAndroid#isVoiceInputAvailable(Context)}.
     */
    public boolean isVoiceInputAvailable() {
        return SillyAndroid.isVoiceInputAvailable(getContext());
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
