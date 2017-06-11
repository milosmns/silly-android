package me.angrybyte.sillyandroid.demo;

import android.Manifest;
import android.content.Context;
import android.graphics.Point;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresPermission;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Set;

import me.angrybyte.sillyandroid.SillyAndroid;
import me.angrybyte.sillyandroid.parsable.Annotations;
import me.angrybyte.sillyandroid.parsable.LayoutWrapper;
import me.angrybyte.sillyandroid.parsable.components.ParsableActivity;

/**
 * The main activity of the demo app, parsed using {@link me.angrybyte.sillyandroid.parsable.AnnotationParser#parseType(Context, Object)}.
 */
@SuppressWarnings("unused")
@Annotations.Layout(R.layout.activity_main)
@Annotations.Menu(R.menu.activity_main)
public final class MainActivity extends ParsableActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    /**
     * The main layout container. (no, you don't have to do this, it's just an example).
     * Parsed using {@link me.angrybyte.sillyandroid.parsable.AnnotationParser#parseFields(Context, Object, LayoutWrapper)}.
     */
    @Annotations.FindView(R.id.container_main)
    private ViewGroup mMainContainer;

    /**
     * The main textual display TextView.
     * Parsed using {@link me.angrybyte.sillyandroid.parsable.AnnotationParser#parseFields(Context, Object, LayoutWrapper)}.
     */
    @Annotations.FindView(R.id.display_text_view)
    private TextView mDisplayView;

    /**
     * The "print info" button.
     * Parsed using {@link me.angrybyte.sillyandroid.parsable.AnnotationParser#parseFields(Context, Object, LayoutWrapper)}.
     */
    @Annotations.Clickable
    @Annotations.FindView(R.id.button_print_info)
    private Button mInfoButton;

    /**
     * The "apply random padding" button.
     * Parsed using {@link me.angrybyte.sillyandroid.parsable.AnnotationParser#parseFields(Context, Object, LayoutWrapper)}.
     */
    @Annotations.LongClickable
    @Annotations.FindView(R.id.button_random_padding)
    private Button mPaddingButton;

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_main_die: {
                toastShort(R.string.toast_die_done);
                finish();
                return true;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    /**
     * Gets the current device type, in text. One of {@link me.angrybyte.sillyandroid.SillyAndroid.UI.DeviceType} constants.
     *
     * @return The textual description of the current device
     */
    @NonNull
    private String getDeviceType() {
        final @SillyAndroid.UI.DeviceType int type = SillyAndroid.UI.getDeviceType(this);
        switch (type) {
            case SillyAndroid.UI.PHONE_LAND:
                return "Phone, landscape";
            case SillyAndroid.UI.PHONE_PORT:
                return "Phone, portrait";
            case SillyAndroid.UI.TABLET_LAND:
                return "Big tablet, landscape";
            case SillyAndroid.UI.TABLET_PORT:
                return "Big tablet, portrait";
            case SillyAndroid.UI.TAB_LAND:
                return "Small tablet, landscape";
            case SillyAndroid.UI.TAB_PORT:
                return "Small tablet, portrait";
            case SillyAndroid.UI.TV:
                return "Television";
            case SillyAndroid.UI.WATCH:
                return "Watch, wear device";
            default:
                return "WE DON'T KNOW!";
        }
    }

    /**
     * Displays some demo information on the {@link #mDisplayView}.
     */
    @RequiresPermission(allOf = { Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.ACCESS_NETWORK_STATE })
    private void printInfo() {
        final StringBuilder builder = new StringBuilder();

        // see DeviceType constants
        builder.append("Device type: ").append(getDeviceType()).append("\n");
        // screen size in pixels
        final Point screenSize = SillyAndroid.UI.getScreenSize(this);
        builder.append("Screen size: ").append(screenSize.x).append("x").append(screenSize.y).append("\n");
        // density in DPI (rounded)
        builder.append("Density DPI: ").append(SillyAndroid.UI.getDensityDpi(this)).append("\n");
        // native 7.0+ multi-window mode
        builder.append(SillyAndroid.UI.isInMultiWindowMode(this) ? "Multi-window mode" : "Single-window mode").append("\n");
        // any network connection, doesn't mean you have Internet connection
        builder.append("Network status: ").append(SillyAndroid.isNetworkConnected(this) ? "Connected" : "Disconnected").append("\n");
        // checks the thread. setting anything to a TextView from a non-UI thread would crash
        builder.append(SillyAndroid.isThisMainThread() ? "Running on UI thread" : "OMG! This is not running on a UI thread!");

        final String displayText = builder.toString();
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "printInfo: " + displayText);
        }
        mDisplayView.setText(displayText);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onClick(final View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.button_print_info: {
                // obviously you don't need to request these, but I need some demo code with permissions here..
                if (!hasPermission(Manifest.permission.ACCESS_WIFI_STATE) || !hasPermission(Manifest.permission.ACCESS_NETWORK_STATE)) {
                    final boolean requested = requestPermissions(10, Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.ACCESS_NETWORK_STATE);
                    if (!requested) {
                        Log.e(TAG, "onClick: Failed to request permissions");
                    }
                    return;
                }
                printInfo();
                break;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onPermissionsResult(@IntRange(from = 0, to = 127) final int code, @NonNull final Set<String> granted, @NonNull final Set<String> denied) {
        super.onPermissionsResult(code, granted, denied);
        switch (code) {
            case 10: {
                if (granted.contains(Manifest.permission.ACCESS_WIFI_STATE) && granted.contains(Manifest.permission.ACCESS_NETWORK_STATE)) {
                    printInfo();
                } else {
                    toastLong(R.string.toast_permission_missing);
                }
                break;
            }
            default: {
                Log.e(TAG, "onPermissionsResult: Unknown request code " + code);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onLongClick(final View v) {
        switch (v.getId()) {
            case R.id.button_random_padding: {
                final int screenWidth = SillyAndroid.UI.getScreenSize(this).x;
                // pick at random between 10% and 25% of the screen's width
                final int randomVerticalPadding = (int) (screenWidth / 10 + Math.random() * (screenWidth / 4));
                setPaddingVertical(mDisplayView, randomVerticalPadding);
                return true;
            }
            default: {
                return super.onLongClick(v);
            }
        }
    }

}
