package me.angrybyte.sillyandroid;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Looper;
import android.os.PowerManager;
import android.speech.RecognizerIntent;
import android.support.annotation.IdRes;
import android.support.annotation.IntDef;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.Px;
import android.support.annotation.RawRes;
import android.support.annotation.RequiresPermission;
import android.support.v4.app.Fragment;
import android.support.v7.widget.PopupMenu;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import java.io.Closeable;
import java.io.InputStream;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

import me.angrbyte.sillyandroid.R;

import static android.content.ContentValues.TAG;

/**
 * This is the library basis. It contains methods used to customize and adapt system-provided Android components.
 */
@SuppressWarnings("WeakerAccess")
public final class SillyAndroid {

    /**
     * A wrapper class to shorten the UI configuration queries.
     */
    @SuppressWarnings("unused")
    public static final class UI {

        @IntDef({ PHONE_PORT, PHONE_LAND, TAB_PORT, TAB_LAND, TABLET_PORT, TABLET_LAND, WATCH, TV })
        @Retention(RetentionPolicy.SOURCE)
        public @interface DeviceType {}

        public static final int PHONE_PORT = 1;
        public static final int PHONE_LAND = 2;
        public static final int TAB_PORT = 3;
        public static final int TAB_LAND = 4;
        public static final int TABLET_PORT = 5;
        public static final int TABLET_LAND = 6;
        public static final int WATCH = 7;
        public static final int TV = 8;

        /**
         * Checks if current configuration is detected as a phone.
         */
        public static boolean isPhone(@NonNull final Context context) {
            return UI.getDeviceType(context) == PHONE_PORT || UI.getDeviceType(context) == PHONE_LAND;
        }

        /**
         * Checks if current configuration is detected as a small tablet (tab).
         */
        public static boolean isTab(@NonNull final Context context) {
            return UI.getDeviceType(context) == TAB_PORT || UI.getDeviceType(context) == TAB_LAND;
        }

        /**
         * Checks if current configuration is detected as a tablet.
         */
        public static boolean isTablet(@NonNull final Context context) {
            return UI.getDeviceType(context) == TABLET_PORT || UI.getDeviceType(context) == TABLET_LAND;
        }

        /**
         * Checks if current configuration is detected as a watch.
         */
        public static boolean isWatch(@NonNull final Context context) {
            return UI.getDeviceType(context) == WATCH;
        }

        /**
         * Checks if current configuration is detected as a Television device.
         */
        public static boolean isTelevision(@NonNull final Context context) {
            return UI.getDeviceType(context) == TV;
        }

        /**
         * Checks whether the given activity is in PIP mode (picture-in-picture).
         */
        public static boolean isInPictureInPictureMode(@Nullable final Activity activity) {
            return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && activity != null && activity.isInPictureInPictureMode();
        }

        /**
         * Checks whether the given activity is in multi-window (split-screen) mode.
         */
        public static boolean isInMultiWindowMode(@Nullable final Activity activity) {
            return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && activity != null && activity.isInMultiWindowMode();
        }

        /**
         * Gets the current display's size in pixels.
         *
         * @param context Which context to use to check the size
         * @return A new {@link Point} object containing absolute screen width (x) and height (y), in pixels
         */
        @NonNull
        public static Point getScreenSize(@NonNull final Context context) {
            final int x = context.getResources().getDisplayMetrics().widthPixels;
            final int y = context.getResources().getDisplayMetrics().heightPixels;
            return new Point(x, y);
        }

        /**
         * Gets the current display's density expressed in DPI.
         *
         * @param context Which context to use to check the DPI density
         * @return The screen density expressed as dots-per-inch, either {@link DisplayMetrics#DENSITY_LOW},
         * {@link DisplayMetrics#DENSITY_MEDIUM}, or {@link DisplayMetrics#DENSITY_HIGH}
         */
        public static int getDensityDpi(@NonNull final Context context) {
            return context.getResources().getDisplayMetrics().densityDpi;
        }

        /**
         * Checks the device type. Integer values:<br>
         * <ol>
         * <li>Phone, portrait</li>
         * <li>Phone, landscape</li>
         * <li>7" to 9" Tablet (called Tab), portrait</li>
         * <li>7" to 9" Tablet (called Tab), landscape</li>
         * <li>9" or bigger Tablet, portrait</li>
         * <li>9" or bigger Tablet, landscape</li>
         * <li>Watch Wearable</li>
         * <li>Television unit</li>
         * </ol>
         *
         * <b>Note</b>: This relies on Android's resource configuration framework. Use with caution.
         *
         * @param context A context to use to detect device type
         * @return The current device type (integer), one of {@link DeviceType} constants
         */
        @DeviceType
        public static int getDeviceType(@NonNull final Context context) {
            // noinspection WrongConstant
            return context.getResources().getInteger(R.integer.config_device_type);
        }

        /**
         * Tries to get the status bar height for this device. Even if the value returned is larger than 0, that does not mean the status bar is visible.
         *
         * @param context A context to use to find the status bar height
         * @return Height of the status bar on the running device, in pixels
         */
        @IntRange(from = 0)
        public static int getStatusBarHeight(@NonNull final Context context) {
            int result = 0;
            int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                result = context.getResources().getDimensionPixelSize(resourceId);
            }
            return result;
        }

        /**
         * Tries to get the navigation bar height for this device. Even if the value returned is larger than 0, that does not mean the nav bar is visible.
         *
         * @param context A context to use to find the navigation bar height
         * @return Height of the navigation bar on the running device, in pixels
         */
        @IntRange(from = 0)
        public static int getNavigationBarHeight(@NonNull final Context context) {
            int resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
            if (resourceId > 0) {
                return context.getResources().getDimensionPixelSize(resourceId);
            }
            return 0;
        }
    }

    /**
     * Counts the available intent handlers in the OS for the given intent.
     *
     * @param context Which context to use
     * @param intent  Which intent to check
     * @return A positive number, representing the number of activities that could handle the given intents. A {@code null} intent results
     * in a '{@code 0}' result
     */
    @IntRange(from = 0)
    public static int countIntentHandlers(@NonNull final Context context, @Nullable final Intent intent) {
        if (intent == null) {
            return 0;
        }
        final List<ResolveInfo> activities = context.getPackageManager().queryIntentActivities(intent, 0);
        return activities == null ? 0 : activities.size();
    }

    /**
     * Checks if the OS can handle the given intent.
     *
     * @param context Which context to use
     * @param intent  Which intent to check
     * @return {@code True} if intent is not {@code null} and resolves to at least one activity, {@code false} otherwise
     */
    public static boolean canHandleIntent(@NonNull final Context context, @Nullable final Intent intent) {
        return intent != null && SillyAndroid.countIntentHandlers(context, intent) > 0;
    }

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
     * Does exactly the same thing as calling {@link Fragment#getView()}.{@link #findViewById(View, int)}, but casts the result to the
     * appropriate View sub-class.
     */
    @Nullable
    public static <ViewType extends View> ViewType findViewById(@NonNull final Fragment fragment, @IdRes final int viewId) {
        // noinspection unchecked
        return fragment.getView() == null ? null : (ViewType) fragment.getView().findViewById(viewId);
    }

    /**
     * Sets the padding to the given View.
     *
     * @param view   Which view to set the padding to, must not be {@code null}
     * @param start  Start padding ('left' for old devices)
     * @param top    Top padding
     * @param end    End padding ('right' for old devices)
     * @param bottom Bottom padding
     */
    public static void setPadding(@NonNull final View view, @Px final int start, @Px final int top, @Px final int end, @Px final int bottom) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            view.setPaddingRelative(start, top, end, bottom);
        } else {
            view.setPadding(start, top, end, bottom);
        }
    }

    /**
     * Sets the same padding for all sides to the given View.
     *
     * @param view    Which view to set the padding to, must not be {@code null}
     * @param padding The padding value
     */
    public static void setPadding(@NonNull final View view, @Px final int padding) {
        SillyAndroid.setPadding(view, padding, padding, padding, padding);
    }

    /**
     * Similar to {@link android.text.TextUtils#isEmpty(CharSequence)}, but also trims the String before checking. This means that checking
     * if {@code ' '} or {@code '\n'} are empty returns {@code true}.
     *
     * @param text Which String to test
     * @return {@code False} if the given text contains something other than whitespace, {@code true} otherwise
     */
    public static boolean isEmpty(@Nullable final String text) {
        return text == null || text.trim().isEmpty();
    }

    /**
     * Tries to dismiss the given {@link PopupMenu}.
     *
     * @param menu Which menu to dismiss
     * @return {@code True} if the given menu is not {@code null} and dismiss was invoked; {@code false} otherwise
     */
    public static boolean dismiss(@Nullable final PopupMenu menu) {
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
     * otherwise
     */
    public static boolean dismiss(@Nullable final Dialog dialog) {
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
    public static boolean close(@Nullable final Closeable closeable) {
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
    public static boolean close(@Nullable final Cursor cursor) {
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

    /**
     * Checks if given raw resource is empty or not.
     *
     * @param context       Which context to use for checking
     * @param rawResourceId The raw resource identifier
     * @return {@code True} if resource is empty, {@code false} otherwise
     */
    @SuppressWarnings("unused")
    public static boolean isRawResourceEmpty(@NonNull final Context context, @RawRes final int rawResourceId) {
        InputStream inputStream = null;
        try {
            inputStream = SillyAndroid.openRawResource(context, rawResourceId);
            return inputStream.available() <= 0;
        } catch (Exception e) {
            Log.e(TAG, "isRawResourceEmpty: FAILED!", e);
            return false;
        } finally {
            SillyAndroid.close(inputStream);
        }
    }

    /**
     * Reads the raw resource as plain text from the given resource ID.
     *
     * @param context       Which context to use for reading
     * @param rawResourceId The raw resource identifier
     * @return A text representation of the raw resource, never {@code null}
     */
    @NonNull
    @SuppressWarnings("unused")
    public static String readRawResource(@NonNull final Context context, @RawRes final int rawResourceId) {
        InputStream inputStream = null;
        try {
            inputStream = SillyAndroid.openRawResource(context, rawResourceId);
            byte[] b = new byte[inputStream.available()];
            // noinspection ResultOfMethodCallIgnored - don't care about number of bytes read
            inputStream.read(b);
            return new String(b);
        } catch (Exception e) {
            Log.e(TAG, "readRawResource: FAILED!", e);
            return "";
        } finally {
            SillyAndroid.close(inputStream);
        }
    }

    /**
     * Opens the input stream to the given raw resource.
     * <b>Note</b>: You need to close the stream manually.
     *
     * @param context       Which context to use for opening
     * @param rawResourceId The raw resource identifier
     * @return A new, open input stream to the requested raw resource
     */
    @NonNull
    public static InputStream openRawResource(@NonNull final Context context, @RawRes final int rawResourceId) {
        return context.getResources().openRawResource(rawResourceId);
    }

    /**
     * Converts a {@link Drawable} into a {@link Bitmap}. Includes an optimization in case the {@link Drawable} in question is already a
     * {@link BitmapDrawable}.
     *
     * @param drawable A Drawable instance to convert
     * @param width    The width of the new Bitmap
     * @param height   The height of the new Bitmap
     * @return A new {@link Bitmap} instance constraint to width and height dimensions supplied, never {@code null}
     */
    @NonNull
    @SuppressWarnings("unused")
    public static Bitmap drawableToBitmap(@NonNull final Drawable drawable, @Px final int width, @Px final int height) {
        final Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(result);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return result;
    }

    /**
     * Similarly to {@link java.util.Objects#equals(Object, Object)}, this compares two {@link Drawable}s' constant states.
     *
     * @param a Drawable one
     * @param b Drawable two
     * @return {@code True} if their constant states are equal, {@code false} otherwise
     */
    @SuppressWarnings("unused")
    public static boolean drawableEquals(@Nullable final Drawable a, @Nullable final Drawable b) {
        return (a == b) || ((a != null) && (b != null) && (a.getConstantState() != null) && a.getConstantState().equals(b.getConstantState()));
    }

    /**
     * Returns the number of pixels corresponding to the given number of device independent pixels in the given context.
     *
     * @param dips Number of dips to convert
     * @return Resulting number of pixels in the given context
     */
    @Px
    @SuppressWarnings("unused")
    public static int convertDipsToPixels(@NonNull final Context context, final int dips) {
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dips, context.getResources().getDisplayMetrics()));
    }

    /**
     * Checks if WiFi is enabled on the device.
     * <b>Note</b>: This does not check if WiFi is connected to a network.
     *
     * @param context Which context to use to check
     * @return {@code True} if WiFi is enabled, {@code false} otherwise
     */
    @SuppressWarnings("unused")
    public static boolean isWifiEnabled(@NonNull final Context context) {
        final WifiManager wiFiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        return wiFiManager != null && wiFiManager.isWifiEnabled();
    }

    /**
     * Checks if WiFi is enabled and connected to a network.
     * <b>Note</b>: This does not check access to the Internet.
     *
     * @param context Which context to use to check
     * @return {@code True} if WiFi is enabled and connected to a network, {@code false} otherwise
     */
    @RequiresPermission(Manifest.permission.ACCESS_WIFI_STATE)
    public static boolean isWifiConnected(@NonNull final Context context) {
        final WifiManager wiFiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wiFiManager != null && wiFiManager.isWifiEnabled()) {
            // Wi-Fi adapter is ON
            final WifiInfo wifiInfo = wiFiManager.getConnectionInfo();
            return wifiInfo != null && wifiInfo.getNetworkId() != -1;
        } else {
            // Wi-Fi adapter is OFF
            return false;
        }
    }

    /**
     * Checks if device is connected to other, non-WiFi networks.
     * <b>Note</b>: This does not check access to the Internet.
     *
     * @param context Which context to use to check
     * @return {@code True} if there is a non-WiFi network connected, {@code false} if not
     */
    @RequiresPermission(allOf = { Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.ACCESS_NETWORK_STATE })
    public static boolean isNonWifiNetworkConnected(@NonNull final Context context) {
        final ConnectivityManager connectivityManager = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = connectivityManager == null ? null : connectivityManager.getActiveNetworkInfo();
        boolean hasOtherNetwork = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            hasOtherNetwork = true;
        }
        return hasOtherNetwork && !SillyAndroid.isWifiConnected(context);
    }

    /**
     * Checks if device is connected to any network.
     * <b>Note</b>: This does not check access to the Internet.
     *
     * @param context Which context to use to check
     * @return {@code True} if there is any network connected, {@code false} if not
     */
    @RequiresPermission(allOf = { Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.ACCESS_NETWORK_STATE })
    public static boolean isNetworkConnected(@NonNull final Context context) {
        return SillyAndroid.isWifiConnected(context) || SillyAndroid.isNonWifiNetworkConnected(context);
    }

    /**
     * Checks if this call is executed on the app's main (UI) thread.
     *
     * @return {@code True} if execution is currently on the main thread, {@code false} otherwise
     */
    @SuppressWarnings("unused")
    public static boolean isThisMainThread() {
        return Looper.getMainLooper().getThread() == Thread.currentThread();
    }

    /**
     * Checks whether the app is currently on the system white-list, i.e. if the OS would allow execution even when in Doze mode.
     * Note that it makes sense to check this only on API 23 (Android 6.0) because the battery optimization API is not available in previous versions.
     * For all pre-Marshmallow APIs, this method will return {@code true}.
     *
     * @param context Which context to use to check
     * @return The value of {@link PowerManager#isIgnoringBatteryOptimizations(String)}
     */
    @SuppressWarnings("unused")
    public static boolean checkDozeModeWhiteList(@NonNull final Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        } else {
            final PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            return powerManager.isIgnoringBatteryOptimizations(context.getPackageName());
        }
    }

    /**
     * Checks if voice recognition service is present in the device.
     *
     * @param context Which context to use to check
     * @return {@code True} if there is a voice recognition service in the device, {@code false} otherwise
     */
    public static boolean isVoiceInputAvailable(@NonNull final Context context) {
        return SillyAndroid.canHandleIntent(context, new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH));
    }

}
