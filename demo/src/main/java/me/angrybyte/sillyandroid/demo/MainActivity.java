package me.angrybyte.sillyandroid.demo;

import android.Manifest;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresPermission;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Set;

import me.angrybyte.sillyandroid.SillyAndroid;
import me.angrybyte.sillyandroid.extras.Coloring;
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
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
         * Prepare the special button coloring to demonstrate the Coloring class (you would have something like this happen generically in real apps):
         *
         * - IDLE state colors -
         *     Background: GRAY
         *     Text: Contrast to [IDLE.Background]
         *     Icon: Contrast to [IDLE.Background]
         *
         * - PRESSED state colors -
         *     Background: #FFCC00 (bright yellow)
         *     Text: Contrast to [PRESSED.Background]
         *     Icon: Contrast to [PRESSED.Background]
         *
         * Android does not recolor these to contrast colors when pressed, so we're doing that manually below.
         */
        final int idleBackgroundColor = Color.GRAY; // button background when not pressed
        final int idleContentColor = Coloring.contrastColor(idleBackgroundColor); // text and icon color when not pressed
        final int pressedBackgroundColor = 0xFFFFCC00; // button background highlight color when pressed
        final Drawable originalDrawable = ContextCompat.getDrawable(this, android.R.drawable.star_big_on); // load a random icon from android
        final StateListDrawable statefulDrawable = Coloring.createContrastStateDrawable(this, idleContentColor, pressedBackgroundColor, true, originalDrawable);
        final ColorStateList statefulTextColors = Coloring.createContrastTextColors(idleContentColor, pressedBackgroundColor);
        final Rect originalBounds = mPaddingButton.getBackground().copyBounds(); // copy original drawable's bounds so that the ripple is bordered
        final int cornerRoundness = SillyAndroid.convertDipsToPixels(this, 4);
        final Drawable backgroundDrawable = Coloring.createResponsiveDrawable(this, idleBackgroundColor, pressedBackgroundColor, idleBackgroundColor, true,
                cornerRoundness, originalBounds);
        setBackgroundCompat(mPaddingButton, backgroundDrawable);
        mPaddingButton.setCompoundDrawablesWithIntrinsicBounds(statefulDrawable, null, null, null);
        mPaddingButton.setTextColor(statefulTextColors);
    }

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

        // print current context
        builder.append("Current ").append(Context.class.getSimpleName()).append(": ").append(this.toString()).append("\n");
        // check instance of parsable layout wrapper
        builder.append(LayoutWrapper.class.isAssignableFrom(getClass()) ? "Assignable from LayoutWrapper" : "Not assignable from LayoutWrapper");
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
            default: {
                Log.w(TAG, "onClick: Unknown View clicked: " + getResources().getResourceName(v.getId()));
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
                break;
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
