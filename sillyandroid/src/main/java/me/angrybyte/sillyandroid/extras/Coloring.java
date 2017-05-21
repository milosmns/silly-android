package me.angrybyte.sillyandroid.extras;

import android.content.Context;
import android.content.res.ColorStateList;
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
import android.support.annotation.RequiresApi;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.graphics.drawable.DrawableWrapper;

import me.angrybyte.sillyandroid.SillyAndroid;

/**
 * Enhanced color, tinting and drawable manipulation helpers.
 */
@SuppressWarnings({"WeakerAccess", "unused"})
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
     * Invokes {@link SillyAndroid#clamp(int, int, int)} with the given component (R,G,B) and [0, 255] range.
     *
     * @param component R, G or B component of a color; potentially out of the [0, 255] range due to modifications
     * @return The component clamped to range [0, 255]
     */
    @IntRange(from = 0, to = 255)
    public static int clampRGB(final int component) {
        return SillyAndroid.clamp(component, 0, 255);
    }

    /**
     * Converts an ARGB hex color String value to an Android Integer color value.
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
     * @param hexValue Hex value
     * @return Integer color, or {@link Color#DKGRAY} if something goes wrong
     */
    @ColorInt
    public static int decodeColor(@NonNull final String hexValue) {
        if (SillyAndroid.isEmpty(hexValue)) {
            return Color.DKGRAY;
        }

        String colorHex = hexValue.trim();
        colorHex = colorHex.replace("#", "").replace("0x", "").replace("0X", "");
        final int length = colorHex.length();
        if (length != 6 && length != 8) {
            return Color.DKGRAY;
        }

        int alpha = 255;
        final int red;
        final int green;
        final int blue;
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
            return Color.DKGRAY;
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
        final double topAlpha = (double) Color.alpha(topColor);
        final double topA = topAlpha == 255d ? 1.0d : topAlpha / 256d; // special: divide by 256 to get 0.5 instead of 0.51 for 0x80 alpha

        // extract bottom color's components
        final int botR = Color.red(bottomColor);
        final int botG = Color.green(bottomColor);
        final int botB = Color.blue(bottomColor);

        // rule: outputComponent = (foregroundComponent * foregroundAlpha) + (backgroundComponent * (1.0 - foregroundAlpha))
        int r = clampRGB((int) Math.round((topR * topA) + (botR * (1.0d - topA))));
        int g = clampRGB((int) Math.round((topG * topA) + (botG * (1.0d - topA))));
        int b = clampRGB((int) Math.round((topB * topA) + (botB * (1.0d - topA))));

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
        final int amount = (int) -(Math.round((double) Color.alpha(color) * 0.25d));
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
        final int amount = (int) Math.round((double) Color.alpha(color) * 0.25d);
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
     * Calculates the contrasted color for the given color. If color brightness is under the {@link #BRIGHTNESS_THRESHOLD}, the resulting contrast color is
     * white; similarly, if color brightness is over the {@link #BRIGHTNESS_THRESHOLD}, the resulting contrast color is black.
     *
     * @param color Which color to use for brightness check
     * @return Either white or black, depending on the given color's brightness - as described
     */
    public static int contrastColor(int color) {
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);

        // there are other more complex formulas, but this one seems to work just fine
        // human eye is least sensitive to blue, then to red, then green; calculating:
        int brightness = (b + r + r + g + g + g) / 6;
        if (brightness < BRIGHTNESS_THRESHOLD) {
            return Color.WHITE;
        } else {
            return Color.BLACK;
        }
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
            drawable = new GradientDrawable(Orientation.BOTTOM_TOP, new int[]{color, color}).mutate();
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
     * @param context  Which context to use
     * @param drawable Which drawable to color
     * @param color    Which color to use
     * @return A colored drawable, new instance in most cases for bitmaps, cached instance for most other cases
     */
    @NonNull
    public static Drawable colorDrawable(@NonNull final Context context, @NonNull final Drawable drawable, @ColorInt final int color) {
        if (drawable instanceof VectorDrawable && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
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
            return new BitmapDrawable(context.getResources(), colorBitmap(bitmap, color));
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
    @NonNull
    public static Drawable colorDrawableWrapped(@NonNull final Drawable drawable, @NonNull final ColorStateList colorStates) {
        Drawable wrapped = DrawableCompat.wrap(drawable);
        DrawableCompat.setTintList(wrapped, colorStates);
        DrawableCompat.setTintMode(wrapped, PorterDuff.Mode.SRC_ATOP);
        wrapped = DrawableCompat.unwrap(wrapped);
        return wrapped;
    }

    /**
     * Colors the given drawable to a specified color using the drawable wrapping technique ({@link DrawableCompat#wrap(Drawable)}).
     * This method also uses {@link PorterDuff.Mode#SRC_ATOP} to color the pixels.
     *
     * @param drawable Which drawable to color
     * @param color    Which color to use
     * @return A colored drawable, cached instance in most cases
     */
    @NonNull
    public static Drawable colorDrawableWrapped(@NonNull Drawable drawable, @ColorInt final int color) {
        Drawable wrapped = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(wrapped, color);
        DrawableCompat.setTintMode(wrapped, PorterDuff.Mode.SRC_ATOP);
        return DrawableCompat.unwrap(wrapped);
    }

    /**
     * Tries to clone and simply color-filter the drawable. Uses {@link PorterDuff.Mode#SRC_ATOP}.
     * <b>Note</b>: Use this when you don't know which drawable you have.
     *
     * @param drawable Which drawable to color
     * @param color    Which color to use
     * @return A colored drawable ready for use
     */
    @NonNull
    public static Drawable colorUnknownDrawable(@NonNull final Drawable drawable, @ColorInt final int color) {
        // check if this is a drawable wrapper, then do coloring by drawable wrapping
        if (drawable instanceof DrawableWrapper || drawable instanceof android.support.v7.graphics.drawable.DrawableWrapper) {
            final Drawable wrapResult = colorDrawableWrapped(drawable, color);
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.JELLY_BEAN_MR2) {
                // there is a bug for JellyBean MR2 when this won't work, so.. set the tint filter manually
                wrapResult.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP));
            }
            return wrapResult;
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
     * @param context    Which context to use
     * @param drawableId Which drawable resource to load, must be a bitmap drawable
     * @param color      Which color to use
     * @return A colored {@link Drawable} ready for use
     */
    @NonNull
    public static Drawable colorDrawable(@NonNull final Context context, @DrawableRes final int drawableId, @ColorInt final int color) {
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
        final Bitmap resourceBitmap = BitmapFactory.decodeResource(context.getResources(), drawableId, opts);
        final Bitmap coloredBitmap = colorBitmap(resourceBitmap, color);
        return new BitmapDrawable(context.getResources(), coloredBitmap);
    }

    /**
     * Sets a {@link PorterDuff.Mode#SRC_ATOP} color filter to the given vector drawable using the specified color.
     *
     * @param vectorDrawable Which drawable to color
     * @param color          Which color to use
     * @return The same instance with the color filter applied
     */
    @NonNull
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
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
     * @param context      Which context to use
     * @param normal       Color for the normal/idle state
     * @param clicked      Color for the clicked/pressed state
     * @param checked      Color for the checked/selected state (makes sense only for Honeycomb and later)
     * @param shouldFade   Set to {@code true} to enable the fading effect, {@code false} to disable it
     * @param cornerRadius Set to round the corners on rectangular drawables, 0 to disable
     * @return A {@link StateListDrawable} drawable object, new instance each time
     */
    @NonNull
    public static Drawable createStateList(@NonNull final Context context, @ColorInt final int normal, @ColorInt final int clicked,
                                           @ColorInt final int checked, final boolean shouldFade, @IntRange(from = 0) int cornerRadius) {
        // initialize state arrays (they're in arrays because you can use different drawables for reverse transitions..)
        final int[] normalState = new int[]{};
        final int[] clickedState = new int[]{android.R.attr.state_pressed};
        final int[] checkedState = new int[]{android.R.attr.state_checked};
        final int[] selectedState = new int[]{android.R.attr.state_selected};
        final int[] focusedState = new int[]{android.R.attr.state_focused};
        int[] activatedState = new int[]{};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            activatedState = new int[]{android.R.attr.state_activated};
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
     * Creates a new, simple borderless {@link RippleDrawable}.
     *
     * @param color The ripple color
     * @return A colored, borderless RippleDrawable, new instance each time
     */
    @NonNull
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public static RippleDrawable createRippleDrawable(@ColorInt final int color) {
        return createRippleDrawable(Color.TRANSPARENT, color, null, 0);
    }

    /**
     * Creates a new {@link RippleDrawable} introduced in Lollipop.
     *
     * @param normalColor  Color for the idle/normal state
     * @param rippleColor  Color for the ripple effect
     * @param bounds       Clipping bounds for the ripple state. Set to {@code null} to get a borderless ripple
     * @param cornerRadius Set to round the corners on rectangular drawables, 0 to disable
     * @return A fully colored RippleDrawable, new instance each time
     */
    @NonNull
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public static RippleDrawable createRippleDrawable(@ColorInt final int normalColor, @ColorInt final int rippleColor, @Nullable final Rect bounds,
                                                      @IntRange(from = 0) final int cornerRadius) {
        // TODO MM switch to #createColoredDrawable() to be able set the cornerRadius on the mask?
        Drawable maskDrawable = null;
        if (bounds != null) {
            // clip color is white
            maskDrawable = new ColorDrawable(Color.WHITE);
            maskDrawable.setBounds(bounds);
        }

        Drawable normalStateDrawable = null;
        // transparent has no idle state
        if (normalColor != Color.TRANSPARENT) {
            normalStateDrawable = createColoredDrawable(normalColor, bounds);
            if (normalStateDrawable instanceof GradientDrawable) {
                ((GradientDrawable) normalStateDrawable).setCornerRadius(cornerRadius);
            }
        }

        return new RippleDrawable(ColorStateList.valueOf(rippleColor), normalStateDrawable, maskDrawable);
    }

    /**
     * Overload of {@link #createResponsiveDrawable(Context, int, int, int, boolean, int, Rect)}. This one does not have bounds, so on Lollipop it creates a
     * borderless ripple drawable each time.
     *
     * @param context      Which context to use
     * @param normal       Color for the normal/idle state
     * @param clicked      Color for the clicked/pressed state
     * @param checked      Color for the checked/selected state
     * @param shouldFade   Set to true to enable the fading effect, false otherwise
     * @param cornerRadius Set to round the corners on rectangular drawables, 0 to disable
     * @return A click-responsive drawable, new instance each time
     */
    @NonNull
    public static Drawable createResponsiveDrawable(@NonNull final Context context, @ColorInt final int normal, @ColorInt final int clicked,
                                                    @ColorInt final int checked, final boolean shouldFade, @IntRange(from = 0) int cornerRadius) {
        // setting bounds to null makes a borderless ripple
        return createResponsiveDrawable(context, normal, clicked, checked, shouldFade, cornerRadius, null);
    }

    /**
     * Creates a new drawable that responds to touches using visual feedback. For Lollipop and later, this returns a {@link RippleDrawable}, and for older OS
     * versions it returns a {@link StateListDrawable}.
     *
     * @param context      Which context to use
     * @param normal       Color for the normal/idle state
     * @param clicked      Color for the clicked/pressed state
     * @param checked      Color for the checked/selected state
     * @param shouldFade   Set to true to enable the fading effect, false otherwise
     * @param cornerRadius Set to round the corners on rectangular drawables, 0 to disable
     * @param bounds       Clipping bounds for the resulting drawable (used only for Ripples). Set to {@code null} to get borderless ripples
     * @return A click-responsive drawable, new instance each time
     */
    @NonNull
    public static Drawable createResponsiveDrawable(@NonNull final Context context, @ColorInt final int normal, @ColorInt final int clicked,
                                                    @ColorInt final int checked, final boolean shouldFade, @IntRange(from = 0) int cornerRadius,
                                                    @Nullable final Rect bounds) {
        // each branch will create a new instance
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return createRippleDrawable(normal, clicked, bounds, cornerRadius);
        } else {
            return createStateList(context, normal, clicked, checked, shouldFade, cornerRadius);
        }
    }

    /**
     * Creates a new {@link ColorStateList} used for {@link android.widget.TextView} text coloring. The resulting state list's "pressed" text color will be
     * <b>contrasted</b> to the "pressed" color of the {@link android.widget.TextView}'s background drawable.
     * For example, since {@link android.widget.Button} extends {@link android.widget.TextView}, you can configure the Button's background to be black when
     * "idle" and white when "clicked", and set the text color to white. This means that, when you click the {@link android.widget.TextView}, its background
     * becomes white, making the white text impossible to see. This method returns a state list that will switch the text color to black when pressed, making
     * the text visible even on a white background; the only thing you really need to provide here is the background color you used for the pressed state.
     *
     * @param normalColor      Color normal/idle text state to this color
     * @param pressedBackColor Background color of the View that shows up when the View is pressed
     * @return The color state list that takes care of contrasted colors
     */
    @NonNull
    public static ColorStateList createContrastTextColors(@ColorInt final int normalColor, @ColorInt final int pressedBackColor) {
        // initialize state arrays (they're in arrays because you can use different colors for reverse transitions..)
        final int[] normalState = new int[]{};
        final int[] clickedState = new int[]{android.R.attr.state_pressed};
        final int[] checkedState = new int[]{android.R.attr.state_checked};
        final int[] selectedState = new int[]{android.R.attr.state_selected};
        final int[] focusedState = new int[]{android.R.attr.state_focused};
        int[] activatedState = new int[]{};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            activatedState = new int[]{android.R.attr.state_activated};
        }

        // initialize identifiers
        int[] stateColors;
        int[][] stateIdentifiers;
        int contrastColor = contrastColor(pressedBackColor);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            stateIdentifiers = new int[][]{selectedState, focusedState, clickedState, checkedState, activatedState, normalState};
            stateColors = new int[]{contrastColor, contrastColor, contrastColor, contrastColor, contrastColor, normalColor};
        } else {
            stateIdentifiers = new int[][]{selectedState, focusedState, clickedState, checkedState, normalState};
            stateColors = new int[]{contrastColor, contrastColor, contrastColor, contrastColor, normalColor};
        }

        return new ColorStateList(stateIdentifiers, stateColors);
    }

    /**
     * Creates a new {@link StateListDrawable} used for {@link android.widget.ImageView} icon coloring. The resulting state list's "pressed" icon color will be
     * <b>contrasted</b> to the "pressed" color of the {@link android.widget.ImageView}'s background drawable.
     * For example, since {@link android.widget.ImageButton} extends {@link android.widget.ImageView}, you can configure the ImageView's background to be black
     * when "idle" and white when "clicked", and set a fully white "idle" icon. This means that, when you click the {@link android.widget.ImageView}, its
     * background becomes white, making the white icon impossible to see. This method returns a drawable state list that will switch the icon color to black
     * when pressed, making the icon visible even on a white background; the only thing you really need to provide here is the background color you used for
     * the pressed state.
     *
     * @param context          Which context to use
     * @param normalColor      Color normal/idle icon state to this color
     * @param pressedBackColor Background color of the View that shows up when the View is pressed
     * @param shouldFade       Set to {@code true} if the state transition should have a fading effect
     * @param original         The "idle" state icon. This is the coloring base for all states
     * @return The color state list that takes care of contrasted colors
     */
    @NonNull
    public static Drawable createContrastStateDrawable(@NonNull final Context context, @ColorInt final int normalColor, @ColorInt final int pressedBackColor,
                                                       final boolean shouldFade, @NonNull final Drawable original) {
        // migrate to a static drawable
        Drawable originalState = original;
        if (originalState instanceof StateListDrawable) {
            originalState = originalState.getCurrent();
        }

        // initialize state arrays (they're in arrays because you can use different colors for reverse transitions..)
        final int[] normalState = new int[]{};
        final int[] clickedState = new int[]{android.R.attr.state_pressed};
        final int[] checkedState = new int[]{android.R.attr.state_checked};
        final int[] selectedState = new int[]{android.R.attr.state_selected};
        final int[] focusedState = new int[]{android.R.attr.state_focused};
        int[] activatedState = new int[]{};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            activatedState = new int[]{android.R.attr.state_activated};
        }

        final Drawable normalDrawable = colorDrawable(context, originalState, normalColor);
        final Drawable clickedDrawable = colorDrawable(context, originalState, contrastColor(pressedBackColor));
        final Drawable checkedDrawable = colorDrawable(context, originalState, contrastColor(pressedBackColor));
        final Drawable focusedDrawable = colorDrawable(context, originalState, contrastColor(darkenColor(pressedBackColor)));

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
     * Creates a new, multi-state {@link StateListDrawable} using the provided static drawables.
     *
     * @param normalDrawable  Used for the normal/idle and focused states
     * @param clickedDrawable Used for the clicked/pressed state
     * @param checkedDrawable Used for the checked/selected and active states
     * @param shouldFade      Set to {@code true} if the state transition should have a fading effect
     * @return A multi-state drawable consisting out of provided drawables, always a new instance
     */
    @NonNull
    public static Drawable createMultiStateDrawable(@NonNull final Drawable normalDrawable, @NonNull final Drawable clickedDrawable,
                                                    @NonNull final Drawable checkedDrawable, final boolean shouldFade) {
        // migrate to static drawables
        Drawable normalState = normalDrawable;
        if (normalState instanceof StateListDrawable) {
            normalState = normalState.getCurrent();
        }
        Drawable clickedState = clickedDrawable;
        if (clickedState instanceof StateListDrawable) {
            clickedState = clickedState.getCurrent();
        }
        Drawable checkedState = checkedDrawable;
        if (checkedState instanceof StateListDrawable) {
            checkedState = checkedState.getCurrent();
        }

        // initialize state arrays (they're in arrays because you can use different colors for reverse transitions..)
        final int[] normalStates = new int[]{};
        final int[] clickedStates = new int[]{android.R.attr.state_pressed};
        final int[] checkedStates = new int[]{android.R.attr.state_checked};
        final int[] selectedStates = new int[]{android.R.attr.state_selected};
        final int[] focusedStates = new int[]{android.R.attr.state_focused};
        int[] activatedState = new int[]{};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            activatedState = new int[]{android.R.attr.state_activated};
        }

        // prepare the state list (order of the states is extremely important!)
        final StateListDrawable states = new StateListDrawable();

        if (!shouldFade) {
            // no fading, add all applicable states
            states.addState(clickedStates, clickedState); // !
            states.addState(selectedStates, checkedState);
            states.addState(focusedStates, normalState);
            states.addState(checkedStates, checkedState);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                states.addState(activatedState, checkedState);
            }
            states.addState(normalStates, normalState); // !
            return states;
        } else {
            // fade enabled, add only normal and pressed states (Honeycomb bug..)
            states.addState(clickedStates, clickedState); // !
            states.addState(normalStates, normalState); // !
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                // fading only works on Honeycomb and later..
                states.setEnterFadeDuration(0);
                states.setExitFadeDuration(DEFAULT_FADE_DURATION);
            }
            return states;
        }
    }

}
