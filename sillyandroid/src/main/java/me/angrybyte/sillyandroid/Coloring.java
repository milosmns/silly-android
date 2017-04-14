package me.angrybyte.sillyandroid;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.VectorDrawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.graphics.drawable.DrawableWrapper;
import android.util.Log;

import static android.content.ContentValues.TAG;

/**
 * Enhanced color and drawable manipulation helpers.
 */
@SuppressWarnings({ "WeakerAccess", "unused" })
public final class Coloring {

    /**
     * Used as a bounds side when no drawable bounds are supplied within the original drawable
     */
    public static final int DEFAULT_BOUNDS = 1000;
    /**
     * Default duration (ms) for pre-Lollipop enter- and exit-fade drawable selectors
     */
    public static final int DEFAULT_FADE_DURATION = 200;
    /**
     * The light/dark threshold when calculating color brightness
     */
    public static final int BRIGHTNESS_THRESHOLD = 180;

    /**
     * Invokes {@link SillyAndroid#clamp(int, int, int)} with the given component (R,G,B) and 0 - 255 range.
     *
     * @param component R, G or B component of a color; potentially out of the [0, 255] range due to modifications
     * @return The component clamped to range [0, 255]
     */
    @IntRange(from = 0, to = 255)
    public static int clampRGB(final int component) {
        return SillyAndroid.clamp(component, 0, 255);
    }

    /**
     * Converts a hex color String value to an Android Integer color value.
     * <br>
     * Supported formats:
     * <br>
     * <ul>
     * <li>#aaRRggBb</li>
     * <li>0xaaRRggBb</li>
     * <li>0XaaRRggBb</li>
     * <li>#RRggBb</li>
     * <li>0xRRggBb</li>
     * <li>0XRRggBb</li>
     * </ul>
     * <i>* Plus all caps variants.</i>
     *
     * @param colorHex Hex value
     * @return Integer color, or plain gray if something goes wrong
     */
    @ColorInt
    public static int decodeColor(@Nullable String colorHex) {
        if (SillyAndroid.isEmpty(colorHex)) {
            return Color.GRAY;
        }

        final int length = colorHex.length();
        colorHex = colorHex.replace("#", "").replace("0x", "").replace("0X", "");
        if (length != 6 && length != 8) {
            return Color.GRAY;
        }

        int alpha = 255, red, green, blue;
        try {
            if (length == 8) {
                alpha = Integer.parseInt(colorHex.substring(0, 2), 16);
                red = Integer.parseInt(colorHex.substring(2, 4), 16);
                green = Integer.parseInt(colorHex.substring(4, 6), 16);
                blue = Integer.parseInt(colorHex.substring(6, 8), 16);
            } else {
                red = Integer.parseInt(colorHex.substring(0, 2), 16);
                green = Integer.parseInt(colorHex.substring(2, 4), 16);
                blue = Integer.parseInt(colorHex.substring(4, 6), 16);
            }
            return Color.argb(alpha, red, green, blue);
        } catch (Throwable ignored) {
            return Color.GRAY;
        }
    }

    /**
     * Blends the given color with a white background. This means that a full color with less-than-full alpha will be lightened to make
     * it look like it is rendered on top of a white background. The resulting color will be non-transparent.
     *
     * @param color Color to use for blending
     * @return Lightened color matching a white underlay
     */
    @ColorInt
    public static int alphaBlendWithWhite(@ColorInt final int color) {
        return alphaBlendColors(color, Color.WHITE);
    }

    /**
     * Blends the given color with a black background. This means that a full color with less-than-full alpha will be darkened to make
     * it look like it is rendered on top of a black background. The resulting color will be non-transparent.
     *
     * @param color Color to use for blending
     * @return Darkened color matching a dark underlay
     */
    @ColorInt
    public static int alphaBlendWithBlack(@ColorInt final int color) {
        return alphaBlendColors(color, Color.BLACK);
    }

    /**
     * Alpha blends the two given colors. Bottom color's alpha will be set to 100% prior to blending.
     *
     * @param topColor    Color that goes on top
     * @param bottomColor Color that goes below
     * @return The blended color with 100% alpha
     */
    @ColorInt
    public static int alphaBlendColors(@ColorInt final int topColor, @ColorInt final int bottomColor) {
        // extract top color's components
        final int topR = Color.red(topColor);
        final int topG = Color.green(topColor);
        final int topB = Color.blue(topColor);
        final float topA = Color.alpha(topColor) / 255f;

        // extract bottom color's components
        final int botR = Color.red(bottomColor);
        final int botG = Color.green(bottomColor);
        final int botB = Color.blue(bottomColor);

        // rule: outputComponent = (foregroundComponent * foregroundAlpha) + (backgroundComponent * (1.0 - foregroundAlpha))
        int r = clampRGB((int) ((topR * topA) + (botR * (1.0 - topA))));
        int g = clampRGB((int) ((topG * topA) + (botG * (1.0 - topA))));
        int b = clampRGB((int) ((topB * topA) + (botB * (1.0 - topA))));

        return Color.argb(255, r, g, b);
    }

    /**
     * Makes the given color a little bit darker (by a quarter of {@link #BRIGHTNESS_THRESHOLD}).
     *
     * @param color A color to darken
     * @return A darker result color
     */
    @ColorInt
    public static int darkenColor(@ColorInt final int color) {
        return shiftBrightness(color, -BRIGHTNESS_THRESHOLD / 4);
    }

    /**
     * Makes the given color a little bit lighter (by a quarter of {@link #BRIGHTNESS_THRESHOLD}).
     *
     * @param color A color to lighten
     * @return A lighter result color
     */
    @ColorInt
    public static int lightenColor(@ColorInt final int color) {
        return shiftBrightness(color, BRIGHTNESS_THRESHOLD / 4);
    }

    /**
     * Darkens or lightens the color by the specified amount.
     *
     * @param color  Which color to change
     * @param amount Negative to darken, positive to lighten. Must be in range [-255, 255]
     * @return The brightness-shifted color
     */
    @ColorInt
    public static int shiftBrightness(@ColorInt final int color, @IntRange(from = -255, to = 255) final int amount) {
        final int a = Color.alpha(color);
        final int r = clampRGB(Color.red(color) + amount);
        final int g = clampRGB(Color.green(color) + amount);
        final int b = clampRGB(Color.blue(color) + amount);
        return Color.argb(a, r, g, b);
    }

    /**
     * Reduces the color's opacity by 25%.
     * Other color components will be kept as they were (R, G, B).
     *
     * @param color Which color to dim
     * @return A dimmed version of the given color
     */
    @ColorInt
    public static int dimColor(@ColorInt final int color) {
        final int amount = -(Math.round((float) Color.alpha(color) * 0.25f));
        return shiftAlpha(color, amount);
    }

    /**
     * Increases the color's opacity by 25%.
     * Other color components will be kept as they were (R, G, B).
     *
     * @param color Which color to opacify
     * @return An opacified version of the given color
     */
    @ColorInt
    public static int opacifyColor(@ColorInt final int color) {
        final int amount = Math.round((float) Color.alpha(color) * 0.25f);
        return shiftAlpha(color, amount);
    }

    /**
     * Changes the given color's alpha component by the given amount.
     *
     * @param color  Which color to alpha-shift
     * @param amount Negative to dim, positive to opacify. Must be in range [-255, 255]
     * @return The alpha-shifted color
     */
    @ColorInt
    public static int shiftAlpha(@ColorInt final int color, @IntRange(from = -255, to = 255) final int amount) {
        final int a = clampRGB(Color.alpha(color) + amount);
        final int r = Color.red(color);
        final int g = Color.green(color);
        final int b = Color.blue(color);
        return Color.argb(a, r, g, b);
    }

    /**
     * Colors the given bitmap to the specified color. Uses {@link PorterDuff.Mode#SRC_ATOP}.
     *
     * @param bitmap The original bitmap, must not be {@code null}
     * @param color  Which color to use for coloring
     * @return A new, colored Bitmap, never {@code null}
     */
    @NonNull
    public static Bitmap colorBitmap(@NonNull final Bitmap bitmap, @ColorInt final int color) {
        // use the original bitmap config
        final Bitmap result = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());

        // paint over the new canvas
        final Paint paint = new Paint();
        final Canvas c = new Canvas(result);
        paint.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP));
        c.drawBitmap(bitmap, 0, 0, paint);

        return result;
    }

    /**
     * Creates a new drawable (implementation of the Drawable object may vary depending on the OS version).
     * The result Drawable will be colored with the given color, and clipped to match the given bounds.
     * Note that the drawable's alpha is set to 0 when argument color is {@link Color#TRANSPARENT}.
     *
     * @param color  Integer color used to color the output drawable
     * @param bounds Four-dimensional vector representing drawable bounds
     * @return Colored and clipped drawable object
     */
    @NonNull
    public static Drawable createColoredDrawable(@ColorInt final int color, @Nullable final Rect bounds) {
        // create the drawable depending on the OS (pre-Honeycomb couldn't use color drawables inside state lists)
        Drawable drawable;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            drawable = new ColorDrawable(color).mutate();
        } else {
            drawable = new GradientDrawable(Orientation.BOTTOM_TOP, new int[] { color, color }).mutate();
        }

        // set the alpha value
        if (color == Color.TRANSPARENT) {
            drawable.setAlpha(0);
        }

        // update bounds
        if (bounds != null) {
            drawable.setBounds(bounds);
        }
        return drawable;
    }

    /**
     * Colors the given drawable to the specified color. Uses {@link PorterDuff.Mode#SRC_ATOP}.
     *
     * @param resources Which resources to use
     * @param drawable  Which drawable to color
     * @param color     Which color to use
     * @return A colored drawable, new instance in most cases for bitmaps, cached instance for most other cases
     */
    @Nullable
    public static Drawable colorDrawable(@NonNull final Resources resources, @Nullable final Drawable drawable, @ColorInt final int color) {
        if (drawable instanceof VectorDrawable) {
            return colorVectorDrawable((VectorDrawable) drawable, color);
        }

        if (drawable instanceof VectorDrawableCompat) {
            return colorVectorDrawable((VectorDrawableCompat) drawable, color);
        }

        if (drawable instanceof ColorDrawable && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            ((ColorDrawable) drawable).setColor(color);
            return drawable;
        }

        if (drawable instanceof GradientDrawable) {
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
            return drawable;
        }

        if (drawable instanceof BitmapDrawable) {
            final Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            return new BitmapDrawable(resources, colorBitmap(bitmap, color));
        }

        // have no idea what this is..
        return colorUnknownDrawable(drawable, color);
    }

    /**
     * Colors the given drawable to the specified color set using the drawable wrapping technique ({@link DrawableCompat#wrap(Drawable)}).
     * This method also uses {@link PorterDuff.Mode#SRC_ATOP} to color the pixels.
     *
     * @param drawable    Which drawable to color
     * @param colorStates Which color set to use
     * @return A colored drawable, cached instance in most cases
     */
    @Nullable
    public static Drawable colorDrawableWrapped(@Nullable Drawable drawable, @NonNull final ColorStateList colorStates) {
        if (drawable != null) {
            drawable = DrawableCompat.wrap(drawable);
            DrawableCompat.setTintList(drawable, colorStates);
            DrawableCompat.setTintMode(drawable, PorterDuff.Mode.SRC_ATOP);
            drawable = DrawableCompat.unwrap(drawable);
            return drawable;
        }
        return null;
    }

    /**
     * Colors the given drawable to a specified color using the drawable wrapping technique ({@link DrawableCompat#wrap(Drawable)}).
     * This method also uses {@link PorterDuff.Mode#SRC_ATOP} to color the pixels.
     *
     * @param drawable Which drawable to color
     * @param color    Which color to use
     * @return A colored drawable, cached instance in most cases
     */
    @Nullable
    public static Drawable colorDrawableWrapped(@Nullable Drawable drawable, @ColorInt final int color) {
        if (drawable != null) {
            Drawable wrapped = DrawableCompat.wrap(drawable);
            DrawableCompat.setTint(wrapped, color);
            DrawableCompat.setTintMode(wrapped, PorterDuff.Mode.SRC_ATOP);
            return DrawableCompat.unwrap(wrapped);
        }
        return null;
    }

    /**
     * Tries to clone and simply color-filter the drawable. Uses {@link PorterDuff.Mode#SRC_ATOP}.
     * <b>Note</b>: Use this when you don't know which drawable you have.
     *
     * @param drawable Which drawable to color
     * @param color    Which color to use
     * @return A colored drawable ready for use
     */
    @Nullable
    public static Drawable colorUnknownDrawable(@Nullable final Drawable drawable, @ColorInt final int color) {
        if (drawable == null) {
            // well done developer.
            return null;
        }

        if (drawable instanceof DrawableWrapper || drawable instanceof android.support.v7.graphics.drawable.DrawableWrapper) {
            // it's a drawable wrapper, do coloring by drawable wrapping
            final Drawable wrapResult = colorDrawableWrapped(drawable, color);
            if (wrapResult != null) {
                if (Build.VERSION.SDK_INT == Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    // there is a bug for JellyBean MR2 when this won't work, so.. set the tint filter manually
                    wrapResult.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP));
                }
                return wrapResult;
            }
        }

        // wrapping failed, do a plain constant state clone
        try {
            final Drawable.ConstantState state = drawable.getConstantState();
            if (state == null) {
                // well done android.
                throw new IllegalStateException("Constant state is unavailable");
            }
            final Drawable copy = drawable.getConstantState().newDrawable().mutate();
            copy.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
            return copy;
        } catch (Exception ignored) {
            return drawable;
        }
    }

    /**
     * Colors a <b>bitmap</b> drawable to the specified color. Uses {@link PorterDuff.Mode#SRC_ATOP}.
     * Automatically loads a high quality (on Nougat+) or an optimal (on Nougat-) bitmap from the given resource ID.
     *
     * @param resources  Which resources to use
     * @param drawableId Which drawable resource to load, must be a bitmap drawable
     * @param color      Which color to use
     * @return A colored {@link Drawable} ready for use
     */
    @NonNull
    public static Drawable colorDrawable(@NonNull final Resources resources, @DrawableRes final int drawableId, @ColorInt final int color) {
        final BitmapFactory.Options opts = new BitmapFactory.Options();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            // noinspection deprecation
            opts.inDither = false; // disable dithering for pre-Nougat devices
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            // noinspection deprecation
            opts.inPurgeable = true; // allocate pixels that could be freed by the system, only for pre-Lollipop devices
            // noinspection deprecation
            opts.inInputShareable = true; // share an input resource stream to preserve memory, only for pre-Lollipop devices
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD_MR1 && Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            // noinspection deprecation
            opts.inPreferQualityOverSpeed = false; // load quickly on Gingerbread MR1 and later, ignored as of Nougat
        }
        opts.inTempStorage = new byte[32 * 1024]; // temp storage - advice is to use 16K, but..

        // load the resource and recolor it
        final Bitmap resourceBitmap = BitmapFactory.decodeResource(resources, drawableId, opts);
        final Bitmap coloredBitmap = colorBitmap(resourceBitmap, color);
        return new BitmapDrawable(resources, coloredBitmap);
    }

    /**
     * Sets a {@link PorterDuff.Mode#SRC_ATOP} color filter to the given vector drawable using the specified color.
     *
     * @param vectorDrawable Which drawable to color
     * @param color          Which color to use
     * @return The same instance with the color filter applied
     */
    @NonNull
    public static Drawable colorVectorDrawable(@NonNull final VectorDrawable vectorDrawable, @ColorInt final int color) {
        vectorDrawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        return vectorDrawable;
    }

    /**
     * Sets a {@link PorterDuff.Mode#SRC_ATOP} color filter to the given <b>compat</b> vector drawable using the specified color.
     *
     * @param vectorDrawableCompat Which drawable to color
     * @param color                Which color to use
     * @return The same instance with the color filter applied
     */
    @NonNull
    public static Drawable colorVectorDrawable(@NonNull final VectorDrawableCompat vectorDrawableCompat, @ColorInt final int color) {
        vectorDrawableCompat.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        return vectorDrawableCompat;
    }

    /**
     * Creates a new {@link StateListDrawable}. Colors that need to be provided are backgrounds for drawable states: "normal" (or "idle"),
     * "clicked" (or "pressed") and "checked" (or "selected"). Optionally, a <i>fade</i> argument can be set to {@code false} to avoid the
     * fading effect when the drawable animates.
     *
     * <b>Note</b>: Use {@link Color#TRANSPARENT} to set a transparent state.
     *
     * @param resources    Which resources to use
     * @param normal       Color for the normal/idle state
     * @param clicked      Color for the clicked/pressed state
     * @param checked      Color for the checked/selected state (makes sense only for Honeycomb and later)
     * @param shouldFade   Set to {@code true} to enable the fading effect, {@code false} to disable it
     * @param cornerRadius Set to round the corners on rectangular drawables, 0 to disable
     * @return A {@link StateListDrawable} drawable object, new instance each time
     */
    @NonNull
    public static Drawable createStateList(@NonNull final Resources resources, @ColorInt final int normal, @ColorInt final int clicked,
                                           @ColorInt final int checked, final boolean shouldFade, @IntRange(from = 0) int cornerRadius) {
        // initialize state arrays (they're in arrays because you can use different drawables for reverse transitions..)
        final int[] normalState = new int[] {};
        final int[] clickedState = new int[] { android.R.attr.state_pressed };
        final int[] checkedState = new int[] { android.R.attr.state_checked };
        final int[] selectedState = new int[] { android.R.attr.state_selected };
        final int[] focusedState = new int[] { android.R.attr.state_focused };
        int[] activatedState = new int[] {};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            activatedState = new int[] { android.R.attr.state_activated };
        }

        // normal state drawable
        final Drawable normalDrawable = createColoredDrawable(normal, new Rect(0, 0, DEFAULT_BOUNDS, DEFAULT_BOUNDS));
        if (normalDrawable instanceof GradientDrawable) {
            ((GradientDrawable) normalDrawable).setCornerRadius(cornerRadius);
        }
        // clicked state drawable
        final Drawable clickedDrawable = createColoredDrawable(clicked, new Rect(0, 0, DEFAULT_BOUNDS, DEFAULT_BOUNDS));
        if (clickedDrawable instanceof GradientDrawable) {
            ((GradientDrawable) clickedDrawable).setCornerRadius(cornerRadius);
        }
        // checked state drawable
        final Drawable checkedDrawable = createColoredDrawable(checked, new Rect(0, 0, DEFAULT_BOUNDS, DEFAULT_BOUNDS));
        if (checkedDrawable instanceof GradientDrawable) {
            ((GradientDrawable) checkedDrawable).setCornerRadius(cornerRadius);
        }
        // focused state drawable (same as normal, only lighter)
        final Drawable focusedDrawable = createColoredDrawable(lightenColor(normal), new Rect(0, 0, DEFAULT_BOUNDS, DEFAULT_BOUNDS));
        if (focusedDrawable instanceof GradientDrawable) {
            ((GradientDrawable) focusedDrawable).setCornerRadius(cornerRadius);
        }

        // prepare the state list (order of the states is extremely important!)
        final StateListDrawable states = new StateListDrawable();

        if (!shouldFade) {
            // no fading, add all applicable states
            states.addState(clickedState, clickedDrawable); // !
            states.addState(selectedState, focusedDrawable); // reuse the focused drawable
            states.addState(focusedState, focusedDrawable);
            states.addState(checkedState, checkedDrawable);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                states.addState(activatedState, focusedDrawable);
            }
            states.addState(normalState, normalDrawable); // !
            return states;
        } else {
            // fade enabled, add only normal and pressed states (Honeycomb bug..)
            states.addState(clickedState, clickedDrawable); // !
            states.addState(normalState, normalDrawable); // !
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                // fading only works on Honeycomb and later..
                states.setEnterFadeDuration(0);
                states.setExitFadeDuration(DEFAULT_FADE_DURATION);
            }
            return states;
        }
    }

    /**
     * Creates a new {@code RippleDrawable} used in Lollipop and later.
     *
     * @param normalColor Color for the idle ripple state
     * @param rippleColor Color for the clicked, pressed and focused ripple states
     * @param bounds      Clip/mask drawable to these rectangle bounds
     * @return A fully colored RippleDrawable instance
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static Drawable createRippleDrawable(int normalColor, int rippleColor, Rect bounds) {
        ColorDrawable maskDrawable = null;
        if (bounds != null) {
            maskDrawable = new ColorDrawable(Color.WHITE);
            maskDrawable.setBounds(bounds);
        }

        if (normalColor == Color.TRANSPARENT) {
            return new RippleDrawable(ColorStateList.valueOf(rippleColor), null, maskDrawable);
        } else {
            return new RippleDrawable(ColorStateList.valueOf(rippleColor), new ColorDrawable(normalColor), maskDrawable);
        }
    }

    /**
     * Creates a new drawable using given parameters. States that should be provided are "normal",<br> "clicked" (pressed) and "checked" (selected). All states
     * are actually integer colors.<br> Optionally, {@code shouldFade} can be set to false to avoid the fading effect.<br> Depending on API level, Drawable
     * instance will be a Ripple drawable (Lollipop) or StateListDrawable.<br> <br> Note: <i>{@link Color#TRANSPARENT} can be used to supply a transparent
     * state.</i>
     *
     * @param normal     Color for the idle state
     * @param clicked    Color for the clicked/pressed state
     * @param checked    Color for the checked/selected state
     * @param shouldFade Set to true to enable the fading effect, false otherwise
     * @return A {@link StateListDrawable} drawable object ready for use
     */
    public static Drawable createBackgroundDrawable(Resources resources, int normal, int clicked, int checked, boolean shouldFade, int cornerRadius) {
        return createBackgroundDrawable(resources, normal, clicked, checked, shouldFade, cornerRadius, null);
    }

    /**
     * Very similar to {@link #createBackgroundDrawable(Resources, int, int, int, boolean, int)}, adding only one more parameter.
     *
     * @param bounds Clip/mask drawable to these rectangle bounds
     * @return Clipped/masked drawable instance
     */
    public static Drawable createBackgroundDrawable(Resources resources, int normal, int clicked, int checked, boolean shouldFade, int cornerRadius, Rect
            bounds) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return createRippleDrawable(normal, clicked, bounds);
        } else {
            return createStateList(resources, normal, clicked, checked, shouldFade, cornerRadius);
        }
    }

    /**
     * Similar to {@link #createContrastStateDrawable(Resources, int, int, boolean, android.graphics.drawable.Drawable)} but using colors only, no drawables.
     *
     * @param normal            Color normal state to this color
     * @param clickedBackground Background color of the View that will show when view is clicked
     * @return The color state list that is in contrast with the on-click background color
     */
    @SuppressLint({ "InlinedApi", "NewApi" })
    public static ColorStateList createContrastStateColors(int normal, int clickedBackground) {
        // init state arrays
        int[] normalState = new int[] {};
        int[] selectedState = new int[] { android.R.attr.state_selected };
        int[] pressedState = new int[] { android.R.attr.state_pressed };
        int[] checkedState = new int[] { android.R.attr.state_checked };
        int[] activatedState = new int[] {};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            activatedState = new int[] { android.R.attr.state_activated };
        }

        // initialize identifiers
        int[] stateColors;
        int[][] stateIdentifiers;
        int contrastColor = getContrastColor(clickedBackground);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            stateIdentifiers = new int[][] { selectedState, pressedState, checkedState, activatedState, normalState };
            stateColors = new int[] { contrastColor, contrastColor, contrastColor, contrastColor, normal };
        } else {
            stateIdentifiers = new int[][] { selectedState, pressedState, checkedState, normalState };
            stateColors = new int[] { contrastColor, contrastColor, contrastColor, normal };
        }

        return new ColorStateList(stateIdentifiers, stateColors);
    }

    /**
     * Creates a new multi-state drawable using the provided drawable states.
     *
     * @param idle       Drawable's IDLE state, must not be null
     * @param clicked    Drawable's CLICKED state, must not be null
     * @param selected   Drawable's PRESSED state, must not be null
     * @param shouldFade Set to {@code true} if the state transition should have a fading effect
     * @return A complex multi-state drawable
     */
    // @formatter:off
    public static Drawable createMultiStateDrawable(@NonNull Drawable idle, @NonNull Drawable clicked, @NonNull Drawable selected, boolean shouldFade) { //
        // @formatter:on
        // noinspection ConstantConditions
        if (idle == null || clicked == null || selected == null) {
            Log.i(TAG, "One of the drawables is null, returning null");
            return null;
        }

        if (idle instanceof StateListDrawable) {
            idle = idle.getCurrent();
        }
        if (clicked instanceof StateListDrawable) {
            clicked = clicked.getCurrent();
        }
        if (selected instanceof StateListDrawable) {
            selected = selected.getCurrent();
        }

        // init state arrays
        int[] selectedState = new int[] { android.R.attr.state_selected };
        int[] pressedState = new int[] { android.R.attr.state_pressed };
        int[] checkedState = new int[] { android.R.attr.state_checked };
        int[] activatedState = new int[] {};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            activatedState = new int[] { android.R.attr.state_activated };
        }

        // prepare state list (order of adding states is important!)
        StateListDrawable states = new StateListDrawable();
        states.addState(pressedState, clicked);
        if (!shouldFade) {
            states.addState(selectedState, selected);
            states.addState(checkedState, selected);
        }

        // add fade effect if applicable
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (shouldFade) {
                states.addState(new int[] {}, idle);
                states.setEnterFadeDuration(0);
                states.setExitFadeDuration(DEFAULT_FADE_DURATION);
            } else {
                states.addState(activatedState, clicked);
                states.addState(new int[] {}, idle);
            }
        } else {
            states.addState(new int[] {}, idle);
        }

        return states;
    }

    /**
     * Similar to {@link #createBackgroundDrawable(Resources, int, int, int, boolean, int)} but with additional {@code original} drawable parameter.
     *
     * @param normal            Color normal state of the drawable to this color
     * @param clickedBackground Background color of the View that will show when view is clicked
     * @param shouldFade        Set to true if the state list should have a fading effect
     * @param original          This drawable will be contrasted to the {@code clickedBackground} color on press
     * @return The state list drawable that is in contrast with the on-click background color
     */
    @SuppressLint({ "InlinedApi", "NewApi" })
    public static Drawable createContrastStateDrawable(Resources resources, int normal, int clickedBackground, boolean shouldFade, Drawable original) {
        if (original == null || original instanceof StateListDrawable) {
            if (original != null) {
                original = original.getCurrent();
            }

            // overridden in previous if clause, so check again
            if (original == null) {
                return null;
            }
        }

        // init state arrays
        int[] selectedState = new int[] { android.R.attr.state_selected };
        int[] pressedState = new int[] { android.R.attr.state_pressed };
        int[] checkedState = new int[] { android.R.attr.state_checked };
        int[] activatedState = new int[] {};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            activatedState = new int[] { android.R.attr.state_activated };
        }

        Drawable normalStateDrawable = colorDrawable(resources, original, normal);
        Drawable clickedStateDrawable = colorDrawable(resources, original, getContrastColor(clickedBackground));
        Drawable checkedStateDrawable = colorDrawable(resources, original, getContrastColor(clickedBackground));

        // prepare state list (order of adding states is important!)
        StateListDrawable states = new StateListDrawable();
        states.addState(pressedState, clickedStateDrawable);
        if (!shouldFade) {
            states.addState(selectedState, clickedStateDrawable);
            states.addState(checkedState, checkedStateDrawable);
        }

        // add fade effect if applicable
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (shouldFade) {
                states.addState(new int[] {}, normalStateDrawable);
                states.setEnterFadeDuration(0);
                states.setExitFadeDuration(DEFAULT_FADE_DURATION);
            } else {
                states.addState(activatedState, clickedStateDrawable);
                states.addState(new int[] {}, normalStateDrawable);
            }
        } else {
            states.addState(new int[] {}, normalStateDrawable);
        }

        return states;
    }

    /**
     * Very similar to {@link #createContrastStateDrawable(Resources, int, int, boolean, android.graphics.drawable.Drawable)} but creates a Ripple drawable
     * available in
     * Lollipop.
     *
     * @param normal            Color normal state of the drawable to this color
     * @param clickedBackground Background color of the View that will show when view is clicked
     * @param original          This drawable will be contrasted to the {@code clickedBackground} color on press
     * @return The Ripple drawable that is in contrast with the on-click background color
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static Drawable createContrastRippleDrawable(int normal, int clickedBackground, Drawable original) {
        if (original == null) {
            Log.i(TAG, "Creating a boundless drawable for contrast ripple request - original was null!");
            return createRippleDrawable(normal, clickedBackground, null);
        }

        return new RippleDrawable(ColorStateList.valueOf(clickedBackground), original, new ColorDrawable(clickedBackground));
    }

    /**
     * This basically chooses between {@link #createContrastStateDrawable(Resources, int, int, boolean, android.graphics.drawable.Drawable)} and {@link
     * #createContrastRippleDrawable(int, int, android.graphics.drawable.Drawable)} depending on the available API level.
     *
     * @param normal            Color normal state of the drawable to this color
     * @param clickedBackground Background color of the View that will show when view is clicked
     * @param shouldFade        Set to true if the state list (pre-API 21) should have a fading effect
     * @param original          This drawable will be contrasted to the {@code clickedBackground} color on press (pre-API 21) or used for masking in ripples
     *                          on post-API
     *                          21
     * @return The state list drawable (< API21) or a ripple drawable (>= API21) that is in contrast with the on-click background color
     */
    public static Drawable createContrastBackgroundDrawable(Resources resources, int normal, int clickedBackground, boolean shouldFade, Drawable original) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return createContrastRippleDrawable(normal, clickedBackground, original);
        } else {
            return createContrastStateDrawable(resources, normal, clickedBackground, shouldFade, original);
        }
    }

    /**
     * Calculates the contrasted color from the given one. If the color darkness is under<br> the {@link #BRIGHTNESS_THRESHOLD}, contrasted color is white. If
     * the color darkness is<br> over the {@link #BRIGHTNESS_THRESHOLD}, contrasted color is black.
     *
     * @param color Calculating contrasted color to this one
     * @return White or black, depending on the provided color's brightness
     */
    public static int getContrastColor(int color) {
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);

        // human eye is least sensitive to blue, then to red, then green; calculating:
        int brightness = (b + r + r + g + g + g) / 6;

        if (brightness < BRIGHTNESS_THRESHOLD) {
            return Color.WHITE;
        } else {
            return Color.BLACK;
        }
    }

}