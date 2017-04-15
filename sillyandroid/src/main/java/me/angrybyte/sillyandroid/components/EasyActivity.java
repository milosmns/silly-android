package me.angrybyte.sillyandroid.components;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.CallSuper;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.Px;
import android.support.annotation.RequiresPermission;
import android.support.annotation.StringRes;
import android.support.annotation.UiThread;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.View;

import java.io.Closeable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import me.angrbyte.sillyandroid.BuildConfig;
import me.angrybyte.sillyandroid.SillyAndroid;
import me.angrybyte.sillyandroid.parsable.LayoutWrapper;

/**
 * An extension of {@link AppCompatActivity} with applied extensions from {@link SillyAndroid} extension set.
 */
@UiThread
@SuppressWarnings("unused")
public class EasyActivity extends AppCompatActivity implements LayoutWrapper {

    /**
     * Returns the result from {@link SillyAndroid#countIntentHandlers(Context, Intent)}.
     */
    @IntRange(from = 0)
    public int countIntentHandlers(@Nullable final Intent intent) {
        return SillyAndroid.countIntentHandlers(this, intent);
    }

    /**
     * Returns the result from {@link SillyAndroid#canHandleIntent(Context, Intent)}.
     */
    public boolean canHandleIntent(@Nullable final Intent intent) {
        return SillyAndroid.canHandleIntent(this, intent);
    }

    /**
     * Returns the result from {@link SillyAndroid#getContentView(Activity)}.
     */
    public <ViewType extends View> ViewType getContentView() {
        return SillyAndroid.getContentView(this);
    }

    /**
     * Returns the result from {@link SillyAndroid#findViewById(Activity, int)}.
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
     * Returns the result from {@link SillyAndroid#dismiss(Dialog)}.
     */
    public boolean dismiss(@Nullable final Dialog dialog) {
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
        return ContextCompat.getDrawable(this, drawableId);
    }

    /**
     * Invokes {@link ViewCompat#setBackground(View, Drawable)} with the same arguments.
     */
    public void setBackgroundCompat(@NonNull final View view, @Nullable final Drawable drawable) {
        ViewCompat.setBackground(view, drawable);
    }

    /**
     * Invokes {@link SillyAndroid#setPadding(View, int, int, int, int)} with the same arguments.
     */
    public void setPadding(@NonNull final View view, @Px final int start, @Px final int top, @Px final int end, @Px final int bottom) {
        SillyAndroid.setPadding(view, start, top, end, bottom);
    }

    /**
     * Invokes {@link SillyAndroid#setPadding(View, int)} with the same arguments.
     */
    public void setPadding(@NonNull final View view, @Px final int padding) {
        SillyAndroid.setPadding(view, padding);
    }

    /**
     * Returns the result from {@link SillyAndroid#isNetworkConnected(Context)}.
     */
    @RequiresPermission(allOf = {Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.ACCESS_NETWORK_STATE})
    public boolean isNetworkConnected() {
        return SillyAndroid.isNetworkConnected(this);
    }

    /**
     * Returns the result from {@link SillyAndroid#isVoiceInputAvailable(Context)}.
     */
    public boolean isVoiceInputAvailable() {
        return SillyAndroid.isVoiceInputAvailable(this);
    }

    /**
     * Invokes {@link SillyAndroid#toastShort(Context, int)}.
     */
    public void toastShort(@StringRes final int stringId) {
        SillyAndroid.toastShort(this, stringId);
    }

    /**
     * Invokes {@link SillyAndroid#toastShort(Context, String)}.
     */
    public void toastShort(@NonNull final String string) {
        SillyAndroid.toastShort(this, string);
    }

    /**
     * Invokes {@link SillyAndroid#toastLong(Context, int)}.
     */
    public void toastLong(@StringRes final int stringId) {
        SillyAndroid.toastLong(this, stringId);
    }

    /**
     * Invokes {@link SillyAndroid#toastLong(Context, String)}.
     */
    public void toastLong(@NonNull final String string) {
        SillyAndroid.toastLong(this, string);
    }

    /* Permissions */

    /**
     * Checks if given permission was granted by the user.
     *
     * @param permission Which permission to check
     * @return {@code True} if permission check {@link ContextCompat#checkSelfPermission(Context, String)} returns {@link PackageManager#PERMISSION_GRANTED}
     * for the given permission, {@code false} if it is {@code null} or not granted
     */
    protected boolean hasPermission(@Nullable String permission) {
        return permission != null && ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Forwards results from {@link #onRequestPermissionsResult(int, String[], int[])}, only in a much prettier and easier to use format.
     */
    @CallSuper
    protected void onPermissionsResult(final int requestCode, @NonNull Set<String> granted, @NonNull Set<String> denied) {
        if (BuildConfig.DEBUG) {
            // log results for debug purposes
            final String grantedText = Arrays.toString(granted.toArray());
            final String deniedText = Arrays.toString(denied.toArray());
            final String format = "Permissions request %d = [granted: %s; permissions denied: %s]";
            Log.d(getClass().getSimpleName(), String.format(format, requestCode, grantedText, deniedText));
        }
    }

    @Override
    @CallSuper
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // invalid case?
        if (permissions.length == 0 || grantResults.length == 0) {
            onPermissionsResult(requestCode, Collections.<String>emptySet(), Collections.<String>emptySet());
            return;
        }

        // divide into two piles, it's much cleaner
        final Set<String> granted = new HashSet<>();
        final Set<String> denied = new HashSet<>();
        for (int i = 0; i < permissions.length; i++) {
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                granted.add(permissions[i]);
            } else {
                denied.add(permissions[i]);
            }
        }

        // invoke the proper callback
        onPermissionsResult(requestCode, granted, denied);
    }

}
