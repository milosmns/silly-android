package me.angrybyte.sillyandroid.extras;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.VectorDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.util.Pair;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.LinkedList;
import java.util.List;

import me.angrybyte.sillyandroid.BuildConfig;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

/**
 * A set of tests related to the {@link Coloring}.
 */
@SuppressLint("DefaultLocale")
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public final class ColoringTest {

    // <editor-fold desc="Tests setup">

    private Activity mActivityContext;

    /**
     * Sets up the testing environment.
     */
    @Before
    public final void setUp() {
        mActivityContext = Robolectric.setupActivity(Activity.class);
    }

    /**
     * Destroys the testing environment.
     */
    @After
    public final void tearDown() {
        mActivityContext = null;
    }
    // </editor-fold>

    /**
     * Tests the {@link Coloring#clampRGB(int)} method.
     */
    @Test
    public final void testClampRGB() {
        // input and output/expected list
        final List<int[]> testCases = new LinkedList<>();
        testCases.add(new int[] { -1, 0 });
        testCases.add(new int[] { 0, 0 });
        testCases.add(new int[] { 130, 130 });
        testCases.add(new int[] { 255, 255 });
        testCases.add(new int[] { 256, 255 });

        // test with those cases
        final String errorText = "Error in clampRGB(%s)";
        for (final int[] testCase : testCases) {
            final String error = String.format(errorText, hex(testCase[0]));
            assertEquals(error, hex(testCase[1]), hex(Coloring.clampRGB(testCase[0])));
        }
    }

    /**
     * Tests the {@link Coloring#decodeColor(String)} method.
     */
    @Test
    public final void testDecodeColor() {
        final String[] realColors = new String[] {
                "0x40AA6070",
                "0x506070",
                "0X5060AA80",
                "0X607080",
                "#20ff4050",
                "#3040cC",
                "dD00FF00",
                "00FF00"
        };

        // input and output/expected list
        final List<Pair<String, Integer>> testCases = new LinkedList<>();
        for (final String realColor : realColors) {
            testCases.add(new Pair<>(realColor, Color.parseColor("#" + realColor.replace("#", "").replace("0x", "").replace("0X", ""))));
        }
        // add failing cases
        testCases.add(new Pair<>("", Color.DKGRAY));
        testCases.add(new Pair<>("qwerty-fail", Color.DKGRAY));
        testCases.add(new Pair<>("eight!", Color.DKGRAY));
        testCases.add(new Pair<>(null, Color.DKGRAY));

        // test with those cases
        final String errorText = "Error in decodeColor(%s)";
        for (final Pair<String, Integer> testCase : testCases) {
            final String error = String.format(errorText, testCase.first);
            assertEquals(error, (int) testCase.second, Coloring.decodeColor(testCase.first));
        }
    }

    /**
     * Tests the {@link Coloring#alphaBlendWithWhite(int)} method.
     */
    @Test
    public final void testAlphaBlendWithWhite() {
        // input and output/expected list
        final List<int[]> testCases = new LinkedList<>();
        testCases.add(new int[] { Color.WHITE, Color.WHITE });
        testCases.add(new int[] { Color.BLACK, Color.BLACK });
        testCases.add(new int[] { Color.TRANSPARENT, Color.WHITE });
        testCases.add(new int[] { 0x80FFED00, 0xFFFFF680 });
        testCases.add(new int[] { 0x80FF0000, 0xFFFF8080 });
        testCases.add(new int[] { 0x800047AB, 0xFF80A3D5 });
        testCases.add(new int[] { 0x8000B500, 0xFF80DA80 });

        // test with those cases
        final String errorText = "Error in alphaBlendWithWhite(%s)";
        for (final int[] testCase : testCases) {
            final String error = String.format(errorText, hex(testCase[0]));
            assertEquals(error, hex(testCase[1]), hex(Coloring.alphaBlendWithWhite(testCase[0])));
        }
    }

    /**
     * Tests the {@link Coloring#alphaBlendWithBlack(int)} method.
     */
    @Test
    public final void testAlphaBlendWithBlack() {
        // input and output/expected list
        final List<int[]> testCases = new LinkedList<>();
        testCases.add(new int[] { Color.WHITE, Color.WHITE });
        testCases.add(new int[] { Color.BLACK, Color.BLACK });
        testCases.add(new int[] { Color.TRANSPARENT, Color.BLACK });
        testCases.add(new int[] { 0x80FFED00, 0xFF807700 });
        testCases.add(new int[] { 0x80FF0000, 0xFF800000 });
        testCases.add(new int[] { 0x800047AB, 0xFF002456 });
        testCases.add(new int[] { 0x8000B500, 0xFF005B00 });

        // test with those cases
        final String errorText = "Error in alphaBlendWithBlack(%s)";
        for (final int[] testCase : testCases) {
            final String error = String.format(errorText, hex(testCase[0]));
            assertEquals(error, hex(testCase[1]), hex(Coloring.alphaBlendWithBlack(testCase[0])));
        }
    }

    /**
     * Tests the {@link Coloring#alphaBlendColors(int, int)} method.
     */
    @Test
    public final void testAlphaBlendColors() {
        // input and output/expected list
        final List<int[]> testCases = new LinkedList<>();
        testCases.add(new int[] { Color.WHITE, Color.WHITE, Color.WHITE });
        testCases.add(new int[] { Color.BLACK, Color.BLACK, Color.BLACK });
        testCases.add(new int[] { Color.TRANSPARENT, Color.BLACK, Color.BLACK });
        testCases.add(new int[] { Color.BLACK, Color.TRANSPARENT, Color.BLACK });
        testCases.add(new int[] { Color.RED & 0x80FFFFFF, Color.YELLOW, 0xFFFF8000 });
        testCases.add(new int[] { Color.GREEN & 0x80FFFFFF, Color.BLUE, 0xFF008080 });
        testCases.add(new int[] { Color.BLUE & 0x80FFFFFF, Color.RED, 0xFF800080 });
        testCases.add(new int[] { Color.YELLOW & 0x80FFFFFF, Color.BLUE, 0xFF808080 });

        // test with those cases
        final String errorText = "Error in alphaBlendColors(%s, %s)";
        for (final int[] testCase : testCases) {
            final String error = String.format(errorText, hex(testCase[0]), hex(testCase[1]));
            assertEquals(error, hex(testCase[2]), hex(Coloring.alphaBlendColors(testCase[0], testCase[1])));
        }
    }

    /**
     * Tests the {@link Coloring#darkenColor(int)} method.
     */
    @Test
    public final void testDarkenColor() {
        // input and output/expected list
        final List<int[]> testCases = new LinkedList<>();
        testCases.add(new int[] { Color.WHITE, 0xFFD2D2D2 });
        testCases.add(new int[] { Color.BLACK, Color.BLACK });
        testCases.add(new int[] { Color.TRANSPARENT, Color.TRANSPARENT });
        testCases.add(new int[] { Color.RED, 0xFFA50000 });
        testCases.add(new int[] { Color.GREEN, 0xFF00A500 });
        testCases.add(new int[] { Color.BLUE, 0xFF0000A5 });
        testCases.add(new int[] { 0xAAD2D2D2, 0xAAA5A5A5 });
        testCases.add(new int[] { 0xBBA5A5A5, 0xBB787878 });
        testCases.add(new int[] { 0xCC787878, 0xCC4B4B4B });
        testCases.add(new int[] { 0xDD4B4B4B, 0xDD1E1E1E });
        testCases.add(new int[] { 0xEE00FF62, 0xEE00A53F });
        testCases.add(new int[] { 0xFF1E1E1E, Color.BLACK });

        // test with those cases
        final String errorText = "Error in darkenColor(%s)";
        for (final int[] testCase : testCases) {
            final String error = String.format(errorText, hex(testCase[0]));
            assertEquals(error, hex(testCase[1]), hex(Coloring.darkenColor(testCase[0])));
        }
    }

    /**
     * Tests the {@link Coloring#lightenColor(int)} method.
     */
    @Test
    public final void testLightenColor() {
        // input and output/expected list
        final List<int[]> testCases = new LinkedList<>();
        testCases.add(new int[] { Color.WHITE, Color.WHITE });
        testCases.add(new int[] { Color.BLACK, 0xFF2D2D2D });
        testCases.add(new int[] { Color.TRANSPARENT, 0x002D2D2D });
        testCases.add(new int[] { Color.RED, 0xFFFF5A5A });
        testCases.add(new int[] { Color.GREEN, 0xFF5AFF5A });
        testCases.add(new int[] { Color.BLUE, 0xFF5A5AFF });
        testCases.add(new int[] { 0xAA2D2D2D, 0xAA5A5A5A });
        testCases.add(new int[] { 0xBBA5A5A5, 0xBBD2D2D2 });
        testCases.add(new int[] { 0xCC787878, 0xCCA5A5A5 });
        testCases.add(new int[] { 0xDD4B4B4B, 0xDD787878 });
        testCases.add(new int[] { 0xEE1E1E1E, 0xEE4B4B4B });
        testCases.add(new int[] { 0xFF00A540, 0xFF00FF63 });

        // test with those cases
        final String errorText = "Error in lightenColor(%s)";
        for (final int[] testCase : testCases) {
            final String error = String.format(errorText, hex(testCase[0]));
            assertEquals(error, hex(testCase[1]), hex(Coloring.lightenColor(testCase[0])));
        }
    }

    /**
     * Tests the {@link Coloring#shiftBrightness(int, int)} method.
     */
    @Test
    public final void testShiftBrightness() {
        // input and output/expected list
        final List<int[]> testCases = new LinkedList<>();
        testCases.add(new int[] { Color.WHITE, 255, Color.WHITE });
        testCases.add(new int[] { Color.WHITE, -255, Color.BLACK });
        testCases.add(new int[] { Color.BLACK, 255, Color.WHITE });
        testCases.add(new int[] { Color.BLACK, -255, Color.BLACK });
        testCases.add(new int[] { Color.YELLOW, 0, Color.YELLOW });
        testCases.add(new int[] { Color.TRANSPARENT, 255, 0x00FFFFFF });
        testCases.add(new int[] { Color.TRANSPARENT, -255, Color.TRANSPARENT });
        testCases.add(new int[] { 0xAAFF3050, 30, 0xAAFF6C83 });
        testCases.add(new int[] { 0xBBFF3050, -30, 0xBBF30026 });
        testCases.add(new int[] { 0xCC30FF50, 50, 0xCC94FFA5 });
        testCases.add(new int[] { 0xDD30FF50, -50, 0xDD00CB1F });
        testCases.add(new int[] { 0xEEFFFF30, 120, 0xEEFFFFFF });
        testCases.add(new int[] { 0xFFFFFF30, -120, 0xFF3F3F00 });

        // test with those cases
        final String errorText = "Error in shiftBrightness(%s, %s)";
        for (final int[] testCase : testCases) {
            final String error = String.format(errorText, hex(testCase[0]), testCase[1]);
            assertEquals(error, hex(testCase[2]), hex(Coloring.shiftBrightness(testCase[0], testCase[1])));
        }
    }

    /**
     * Tests the {@link Coloring#dimColor(int)} method.
     */
    @Test
    public final void testDimColor() {
        // input and output/expected list
        final List<int[]> testCases = new LinkedList<>();
        testCases.add(new int[] { Color.WHITE, 0xC0FFFFFF });
        testCases.add(new int[] { Color.BLACK, 0xC0000000 });
        testCases.add(new int[] { Color.TRANSPARENT, Color.TRANSPARENT });
        testCases.add(new int[] { Color.RED, 0xC0FF0000 });
        testCases.add(new int[] { Color.GREEN, 0xC000FF00 });
        testCases.add(new int[] { Color.BLUE, 0xC00000FF });
        testCases.add(new int[] { 0xC0FFFFFF, 0x90FFFFFF });
        testCases.add(new int[] { 0x90FFFFFF, 0x6CFFFFFF });
        testCases.add(new int[] { 0x6CFFFFFF, 0x51FFFFFF });
        testCases.add(new int[] { 0x04FFFFFF, 0x03FFFFFF });
        testCases.add(new int[] { 0x01000000, 0x01000000 });

        // test with those cases
        final String errorText = "Error in dimColor(%s)";
        for (final int[] testCase : testCases) {
            final String error = String.format(errorText, hex(testCase[0]));
            assertEquals(error, hex(testCase[1]), hex(Coloring.dimColor(testCase[0])));
        }
    }

    /**
     * Tests the {@link Coloring#opacifyColor(int)} method.
     */
    @Test
    public final void testOpacifyColor() {
        // input and output/expected list
        final List<int[]> testCases = new LinkedList<>();
        testCases.add(new int[] { Color.WHITE, Color.WHITE });
        testCases.add(new int[] { Color.BLACK, Color.BLACK });
        testCases.add(new int[] { Color.TRANSPARENT, Color.TRANSPARENT });
        testCases.add(new int[] { Color.RED, Color.RED });
        testCases.add(new int[] { Color.GREEN, Color.GREEN });
        testCases.add(new int[] { Color.BLUE, Color.BLUE });
        testCases.add(new int[] { 0x01FFFFFF, 0x01FFFFFF });
        testCases.add(new int[] { 0x04FFFFFF, 0x05FFFFFF });
        testCases.add(new int[] { 0x10FFFFFF, 0x14FFFFFF });
        testCases.add(new int[] { 0xA0FFFFFF, 0xC8FFFFFF });
        testCases.add(new int[] { 0xF0FFFFFF, Color.WHITE });

        // test with those cases
        final String errorText = "Error in opacifyColor(%s)";
        for (final int[] testCase : testCases) {
            final String error = String.format(errorText, hex(testCase[0]));
            assertEquals(error, hex(testCase[1]), hex(Coloring.opacifyColor(testCase[0])));
        }
    }

    /**
     * Tests the {@link Coloring#shiftAlpha(int, int)} method.
     */
    @Test
    public final void testShiftAlpha() {
        // input and output/expected list
        final List<int[]> testCases = new LinkedList<>();
        testCases.add(new int[] { Color.WHITE, 255, Color.WHITE });
        testCases.add(new int[] { Color.WHITE, -255, 0x00FFFFFF });
        testCases.add(new int[] { Color.BLACK, 255, Color.BLACK });
        testCases.add(new int[] { Color.BLACK, -255, Color.TRANSPARENT });
        testCases.add(new int[] { Color.YELLOW, 0, Color.YELLOW });
        testCases.add(new int[] { Color.TRANSPARENT, 255, Color.BLACK });
        testCases.add(new int[] { Color.TRANSPARENT, -255, Color.TRANSPARENT });
        testCases.add(new int[] { 0x00000000, 0x20, 0x20000000 });
        testCases.add(new int[] { 0x00FFFFFF, -0x20, 0x00FFFFFF });
        testCases.add(new int[] { 0x20000000, 0x20, 0x40000000 });
        testCases.add(new int[] { 0x20AAAAAA, -0x20, 0x00AAAAAA });
        testCases.add(new int[] { 0xF0000000, 0x0F, 0xFF000000 });
        testCases.add(new int[] { 0xF0CCCCCC, -0x0F, 0xE1CCCCCC });

        // test with those cases
        final String errorText = "Error in shiftAlpha(%s, %s)";
        for (final int[] testCase : testCases) {
            final String error = String.format(errorText, hex(testCase[0]), testCase[1]);
            assertEquals(error, hex(testCase[2]), hex(Coloring.shiftAlpha(testCase[0], testCase[1])));
        }
    }

    /**
     * Tests the {@link Coloring#contrastColor(int)} method.
     */
    @Test
    public final void testContrastColor() {
        // input and output/expected list
        final List<int[]> testCases = new LinkedList<>();
        testCases.add(new int[] { Color.WHITE, Color.BLACK });
        testCases.add(new int[] { Color.BLACK, Color.WHITE });
        testCases.add(new int[] { Color.TRANSPARENT, Color.WHITE });
        testCases.add(new int[] { Color.RED, Color.WHITE });
        testCases.add(new int[] { Color.GREEN, Color.WHITE });
        testCases.add(new int[] { Color.BLUE, Color.WHITE });
        testCases.add(new int[] { Color.YELLOW, Color.BLACK });
        testCases.add(new int[] { Color.CYAN, Color.WHITE });
        testCases.add(new int[] { Color.MAGENTA, Color.WHITE });
        testCases.add(new int[] { Color.DKGRAY, Color.WHITE });
        testCases.add(new int[] { Color.LTGRAY, Color.BLACK });

        // test with those cases
        final String errorText = "Error in contrastColor(%s)";
        for (final int[] testCase : testCases) {
            final String error = String.format(errorText, hex(testCase[0]));
            assertEquals(error, hex(testCase[1]), hex(Coloring.contrastColor(testCase[0])));
        }
    }

    /**
     * Tests the {@link Coloring#colorBitmap(Bitmap, int)} method.
     * <p>
     * Due to {@link org.robolectric.shadows.ShadowBitmap}'s empty implementation, this won't really work, so we can only test the transparency.
     */
    @Test
    public final void testColorBitmap() {
        final Bitmap.Config config = Bitmap.Config.ARGB_8888;
        final int width = 10, height = 10;
        final int[] allReds = new int[width * height];
        for (int i = 0; i < width * height; i++) {
            allReds[i] = Color.RED;
        }

        final Bitmap redSquare = Bitmap.createBitmap(allReds, width, height, config);
        assertNotNull("Created Bitmap is null", redSquare);

        // initialize red Bitmap's internal structures, otherwise it won't draw properly
        redSquare.prepareToDraw();
        final byte[] redPixels = new byte[redSquare.getWidth() * redSquare.getHeight() * 8];
        final ByteBuffer redBuffer = ByteBuffer.wrap(redPixels);
        redBuffer.order(ByteOrder.nativeOrder());
        redSquare.copyPixelsToBuffer(redBuffer);
        redSquare.copyPixelsFromBuffer(redBuffer);
        redSquare.prepareToDraw();

        final String redPixel = hex(redSquare.getPixel(width / 2, height / 2));
        final String errorRed = String.format("Error while creating red bitmap, middle pixel is %s", redPixel);
        assertEquals(errorRed, hex(Color.TRANSPARENT), redPixel);

        final Bitmap greenSquare = Coloring.colorBitmap(redSquare, Color.GREEN);
        assertNotNull("Created colored Bitmap is null", greenSquare);
        final String greenPixel = hex(greenSquare.getPixel(width / 2, height / 2));
        final String errorGreen = String.format("Error while coloring bitmap, middle pixel is %s", greenPixel);
        assertEquals(errorGreen, hex(Color.TRANSPARENT), greenPixel);
    }

    /**
     * Tests the {@link Coloring#createColoredDrawable(int, Rect)} method.
     * <p>
     * Unfortunately some Drawable properties are not shadowed by Robolectric yet, so we can test only the basic stuff here.
     */
    @Test
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public final void testCreateColoredDrawable() {
        final Drawable transparentNoBounds = Coloring.createColoredDrawable(Color.TRANSPARENT, null);
        assertNotNull("Colored drawable is null", transparentNoBounds);
        assertTrue("Drawable not a ColorDrawable", transparentNoBounds instanceof ColorDrawable);
        assertEquals("Drawable color is not transparent", Color.TRANSPARENT, ((ColorDrawable) transparentNoBounds).getColor());
        assertEquals("Bounds are not 0", new Rect(0, 0, 0, 0), transparentNoBounds.getBounds());
        // noinspection RedundantCast - it's not. alpha getter was added for Drawable in KITKAT
        assertEquals("Alpha is not 0", 0, ((ColorDrawable) transparentNoBounds).getAlpha());

        final Drawable transparentWithBounds = Coloring.createColoredDrawable(Color.TRANSPARENT, new Rect(0, 0, 100, 100));
        assertNotNull("Colored drawable is null", transparentWithBounds);
        assertTrue("Drawable not a ColorDrawable", transparentWithBounds instanceof ColorDrawable);
        assertEquals("Drawable color is not transparent", Color.TRANSPARENT, ((ColorDrawable) transparentWithBounds).getColor());
        assertEquals("Bounds are not null", new Rect(0, 0, 100, 100), transparentWithBounds.getBounds());
        // noinspection RedundantCast - it's not. alpha getter was added for Drawable in KITKAT
        assertEquals("Alpha is not 0", 0, ((ColorDrawable) transparentWithBounds).getAlpha());

        final Drawable redNoBounds = Coloring.createColoredDrawable(Color.RED, null);
        assertNotNull("Colored drawable is null", redNoBounds);
        assertTrue("Drawable not a ColorDrawable", redNoBounds instanceof ColorDrawable);
        assertEquals("Drawable color is not red", hex(((ColorDrawable) redNoBounds).getColor()), hex(Color.RED));
        assertEquals("Bounds are not null", new Rect(0, 0, 0, 0), redNoBounds.getBounds());
        // noinspection RedundantCast - it's not. alpha getter was added for Drawable in KITKAT
        assertEquals("Alpha is not 255", 255, ((ColorDrawable) redNoBounds).getAlpha());

        final Drawable redWithBounds = Coloring.createColoredDrawable(Color.RED, new Rect(0, 0, 100, 100));
        assertNotNull("Colored drawable is null", redWithBounds);
        assertTrue("Drawable not a ColorDrawable", redWithBounds instanceof ColorDrawable);
        assertEquals("Drawable color is not red", hex(((ColorDrawable) redWithBounds).getColor()), hex(Color.RED));
        assertEquals("Bounds are not null", new Rect(0, 0, 100, 100), redWithBounds.getBounds());
        // noinspection RedundantCast - it's not. alpha getter was added for Drawable in KITKAT
        assertEquals("Alpha is not 255", 255, ((ColorDrawable) redWithBounds).getAlpha());
    }

    /**
     * Tests the {@link Coloring#colorBitmapDrawable(Context, int, int)} method.
     * <p>
     * Due to {@link org.robolectric.shadows.ShadowBitmap}'s empty implementation, this won't really work, so we can only test the transparency.
     */
    @Test
    public final void testColorDrawableResource() {
        // noinspection deprecation - can't enforce Lollipop here
        final BitmapDrawable colored = Coloring.colorBitmapDrawable(mActivityContext, android.R.drawable.btn_star_big_on, Color.RED);
        assertNotNull("Colored drawable is null", colored);
        final int coloredPixel = colored.getBitmap().getPixel(colored.getBitmap().getWidth() / 2, colored.getBitmap().getHeight() / 2);
        assertEquals("Colored middle pixel is not transparent", hex(Color.TRANSPARENT), hex(coloredPixel));
    }

    /**
     * Tests the {@link Coloring#colorDrawable(Context, Drawable, int)} method.
     * <p>
     * Due to {@link org.robolectric.shadows.ShadowBitmap}'s empty implementation, this won't really work, so we can only test the transparency.
     */
    @Test
    public final void testColorDrawableObject() {
        // noinspection deprecation - can't enforce Lollipop here
        final BitmapDrawable original = (BitmapDrawable) mActivityContext.getResources().getDrawable(android.R.drawable.btn_star_big_on);
        assertNotNull("Original drawable is null", original);
        final int originalPixel = original.getBitmap().getPixel(original.getBitmap().getWidth() / 2, original.getBitmap().getHeight() / 2);
        assertEquals("Original middle pixel is not transparent", hex(Color.TRANSPARENT), hex(originalPixel));

        final BitmapDrawable colored = (BitmapDrawable) Coloring.colorDrawable(mActivityContext, original, Color.RED);
        assertNotNull("Colored drawable is null", colored);
        final int coloredPixel = colored.getBitmap().getPixel(colored.getBitmap().getWidth() / 2, colored.getBitmap().getHeight() / 2);
        assertEquals("Colored middle pixel is not transparent", hex(Color.TRANSPARENT), hex(coloredPixel));
    }

    /**
     * Tests the {@link Coloring#colorDrawableWrapped(Drawable, int)} method.
     * <p>
     * Due to {@link org.robolectric.shadows.ShadowBitmap}'s empty implementation, this won't really work, so we can only test the transparency.
     */
    @Test
    public final void testColorDrawableWrappedColor() {
        // noinspection deprecation - can't enforce Lollipop here
        final BitmapDrawable original = (BitmapDrawable) mActivityContext.getResources().getDrawable(android.R.drawable.btn_star_big_on);
        assertNotNull("Original drawable is null", original);
        final int originalPixel = original.getBitmap().getPixel(original.getBitmap().getWidth() / 2, original.getBitmap().getHeight() / 2);
        assertEquals("Original middle pixel is not transparent", hex(Color.TRANSPARENT), hex(originalPixel));

        final BitmapDrawable colored = (BitmapDrawable) Coloring.colorDrawableWrapped(original, Color.RED);
        assertNotNull("Colored drawable is null", colored);
        final int coloredPixel = colored.getBitmap().getPixel(colored.getBitmap().getWidth() / 2, colored.getBitmap().getHeight() / 2);
        assertEquals("Colored middle pixel is not transparent", hex(Color.TRANSPARENT), hex(coloredPixel));
    }

    /**
     * Tests the {@link Coloring#colorDrawableWrapped(Drawable, ColorStateList)} method.
     * <p>
     * Due to {@link org.robolectric.shadows.ShadowBitmap}'s empty implementation, this won't really work, so we can only test the transparency.
     */
    @Test
    public final void testColorDrawableWrappedStateList() {
        // noinspection deprecation - can't enforce Lollipop here
        final BitmapDrawable original = (BitmapDrawable) mActivityContext.getResources().getDrawable(android.R.drawable.btn_star_big_on);
        assertNotNull("Original drawable is null", original);
        final int originalPixel = original.getBitmap().getPixel(original.getBitmap().getWidth() / 2, original.getBitmap().getHeight() / 2);
        assertEquals("Original middle pixel is not transparent", hex(Color.TRANSPARENT), hex(originalPixel));

        final BitmapDrawable colored = (BitmapDrawable) Coloring.colorDrawableWrapped(original, ColorStateList.valueOf(Color.RED));
        assertNotNull("Colored drawable is null", colored);
        final int coloredPixel = colored.getBitmap().getPixel(colored.getBitmap().getWidth() / 2, colored.getBitmap().getHeight() / 2);
        assertEquals("Colored middle pixel is not transparent", hex(Color.TRANSPARENT), hex(coloredPixel));
    }

    /**
     * Tests the {@link Coloring#colorUnknownDrawable(Drawable, int)} method.
     * <p>
     * Due to {@link org.robolectric.shadows.ShadowBitmap}'s empty implementation, this won't really work, so we can only test the transparency.
     */
    @Test
    public final void testColorUnknownDrawable() {
        // noinspection deprecation - can't enforce Lollipop here
        final BitmapDrawable original = (BitmapDrawable) mActivityContext.getResources().getDrawable(android.R.drawable.btn_star_big_on);
        assertNotNull("Original drawable is null", original);
        final int originalPixel = original.getBitmap().getPixel(original.getBitmap().getWidth() / 2, original.getBitmap().getHeight() / 2);
        assertEquals("Original middle pixel is not transparent", hex(Color.TRANSPARENT), hex(originalPixel));

        final BitmapDrawable colored = (BitmapDrawable) Coloring.colorUnknownDrawable(original, Color.RED);
        assertNotNull("Colored drawable is null", colored);
        final int coloredPixel = colored.getBitmap().getPixel(colored.getBitmap().getWidth() / 2, colored.getBitmap().getHeight() / 2);
        assertEquals("Colored middle pixel is not transparent", hex(Color.TRANSPARENT), hex(coloredPixel));
    }

    /**
     * Tests the {@link Coloring#colorVectorDrawable(VectorDrawable, int)} method.
     * <p>
     * Unfortunately {@link VectorDrawable#setColorFilter(int, PorterDuff.Mode)} is not mocked in Android JAR yet.
     */
    @Test
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public final void testColorVectorDrawable() {
        try {
            final VectorDrawable vectorDrawable = new VectorDrawable();
            assertNotNull("VectorDrawable is null", vectorDrawable);
            final VectorDrawable colored = Coloring.colorVectorDrawable(vectorDrawable, Color.RED);
            final PorterDuff.Mode mode = PorterDuff.Mode.SRC_ATOP;
            assertEquals("Vector color filter does not match", new PorterDuffColorFilter(Color.RED, mode), colored.getColorFilter());
        } catch (RuntimeException e) {
            boolean knownIssue = e.getMessage().contains("not mocked");
            if (!knownIssue) {
                e.printStackTrace();
            }
            assertTrue("Unknown error: " + e.getMessage(), knownIssue);
        }
    }

    /**
     * Tests the {@link Coloring#colorVectorDrawableCompat(VectorDrawableCompat, int)} method.
     * <p>
     * Unfortunately {@link VectorDrawableCompat#setColorFilter(int, PorterDuff.Mode)} is not shadowed by Robolectric yet.
     */
    @Test
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public final void testColorVectorDrawableCompat() {
        try {
            // constructor not accessible outside of its own package
            Class<?> cl = Class.forName(VectorDrawableCompat.class.getCanonicalName());
            Constructor<?> constructor = cl.getConstructor();
            constructor.setAccessible(true);
            final VectorDrawableCompat vectorDrawableCompat = (VectorDrawableCompat) constructor.newInstance();
            assertNotNull("VectorDrawableCompat is null", vectorDrawableCompat);
            final VectorDrawableCompat colored = Coloring.colorVectorDrawableCompat(vectorDrawableCompat, Color.RED);
            assertEquals("VectorCompat color filter does not match", null, colored.getColorFilter());
        } catch (RuntimeException e) {
            boolean knownIssue = e.getMessage().contains("no such method");
            if (!knownIssue) {
                e.printStackTrace();
            }
            assertTrue("Unknown error: " + e.getMessage(), knownIssue);
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Tests the {@link Coloring#createStateList(Context, int, int, int, boolean, int)} method.
     * <p>
     * Unfortunately {@link StateListDrawable#getCurrent()} is not shadowed by Robolectric yet.
     */
    @Test
    public final void testCreateStateList() {
        final int colorNormal = Color.WHITE;
        final int colorActive = Color.GRAY;
        final int colorFocused = Color.YELLOW;
        final StateListDrawable drawable = Coloring.createStateList(mActivityContext, colorNormal, colorActive, colorFocused, true, 0);
        assertNotNull("StateListDrawable is null", drawable);
        drawable.setState(new int[] {});
        assertTrue("StateListDrawable is not stateful", drawable.isStateful());
        final Drawable.ConstantState constantState = drawable.getConstantState();
        assertNotNull("Constant state is null", constantState);
        final Drawable currentState = drawable.getCurrent();
        assertNull("Robolectric started shadowing current state", currentState);
    }

    /**
     * Tests the {@link Coloring#createRippleDrawable(int)} method.
     * <p>
     * Unfortunately {@link android.graphics.drawable.LayerDrawable}'s constructor is not shadowed by Robolectric yet.
     */
    @Test
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public final void testCreateRippleDrawableSingle() {
        try {
            final RippleDrawable ripple = Coloring.createRippleDrawable(Color.GREEN);
            assertNotNull("RippleDrawable is null", ripple);
            final Drawable.ConstantState constantState = ripple.getConstantState();
            assertNotNull("Constant state is null", constantState);
        } catch (NullPointerException ignored) {
            // Robolectric shadowing error, expected in the current version
        }
    }

    /**
     * Tests the {@link Coloring#createRippleDrawable(int, int, Rect, int)} method.
     * <p>
     * Unfortunately {@link android.graphics.drawable.LayerDrawable}'s constructor is not shadowed by Robolectric yet.
     */
    @Test
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public final void testCreateRippleDrawableMulti() {
        try {
            final RippleDrawable ripple = Coloring.createRippleDrawable(Color.WHITE, Color.RED, null, 20);
            assertNotNull("RippleDrawable is null", ripple);
            final Drawable.ConstantState constantState = ripple.getConstantState();
            assertNotNull("Constant state is null", constantState);
        } catch (NullPointerException ignored) {
            // Robolectric shadowing error, expected in the current version
        }
    }

    /**
     * Tests the {@link Coloring#createResponsiveDrawable(Context, int, int, int, boolean, int)} method.
     * <p>
     * Unfortunately some Drawable properties are not shadowed by Robolectric yet, so we can test only the basic stuff here.
     */
    @Test
    public final void testCreateResponsiveDrawableBorderless() {
        final Drawable drawable = Coloring.createResponsiveDrawable(mActivityContext, Color.WHITE, Color.GRAY, Color.GREEN, true, 20);
        assertNotNull("Responsive drawable is null", drawable);
        assertTrue("Responsive drawable is of unknown type: " + drawable.getClass().getCanonicalName(), drawable instanceof StateListDrawable || drawable
                instanceof RippleDrawable);
        final Drawable.ConstantState constantState = drawable.getConstantState();
        assertNotNull("Constant state is null", constantState);
    }

    /**
     * Tests the {@link Coloring#createResponsiveDrawable(Context, int, int, int, boolean, int, Rect)} method.
     * <p>
     * Unfortunately some Drawable properties are not shadowed by Robolectric yet, so we can test only the basic stuff here.
     */
    @Test
    public final void testCreateResponsiveDrawableBorders() {
        final Drawable drawable = Coloring.createResponsiveDrawable(mActivityContext, Color.WHITE, Color.GRAY, Color.GREEN, true, 20, new Rect(0, 0, 20, 20));
        assertNotNull("Responsive drawable is null", drawable);
        assertTrue("Responsive drawable is of unknown type: " + drawable.getClass().getCanonicalName(), drawable instanceof StateListDrawable || drawable
                instanceof RippleDrawable);
        final Drawable.ConstantState constantState = drawable.getConstantState();
        assertNotNull("Constant state is null", constantState);
    }

    /**
     * Tests the {@link Coloring#createContrastTextColors(int, int)} method.
     */
    @Test
    public final void testCreateContrastTextColors() {
        final ColorStateList stateList = Coloring.createContrastTextColors(Color.WHITE, Color.BLACK);
        assertNotNull("ColorStateList is null", stateList);
        assertTrue("ColorStateList is not stateful", stateList.isStateful());
        assertEquals("Default color is not white", hex(Color.WHITE), hex(stateList.getDefaultColor()));
        final int activeTextColor = stateList.getColorForState(new int[] { android.R.attr.state_pressed }, Color.BLACK);
        assertEquals("Clicked color is not contrasted to black", hex(Coloring.contrastColor(Color.BLACK)), hex(activeTextColor));
    }

    /**
     * Tests the {@link Coloring#createContrastStateDrawable(Context, int, int, boolean, Drawable)} method.
     * <p>
     * Unfortunately some Drawable properties are not shadowed by Robolectric yet, so we can test only the basic stuff here.
     */
    @Test
    public final void testCreateContrastStateDrawable() {
        // noinspection deprecation - can't enforce Lollipop here
        final BitmapDrawable original = (BitmapDrawable) mActivityContext.getResources().getDrawable(android.R.drawable.btn_star_big_on);
        assertNotNull("Original drawable is null", original);

        final StateListDrawable stateList = Coloring.createContrastStateDrawable(mActivityContext, Color.WHITE, Color.BLACK, true, original);
        assertNotNull("Contrast state drawable is null", stateList);
        assertTrue("Contrast state drawable is not stateful", stateList.isStateful());
        final Drawable.ConstantState constantState = stateList.getConstantState();
        assertNotNull("Constant state is null", constantState);
    }

    /**
     * Tests the {@link Coloring#createMultiStateDrawable(Drawable, Drawable, Drawable, boolean)} method.
     * <p>
     * Unfortunately some Drawable properties are not shadowed by Robolectric yet, so we can test only the basic stuff here.
     */
    @Test
    public final void testCreateMultiStateDrawable() {
        // noinspection deprecation - can't enforce Lollipop here
        final BitmapDrawable normal = (BitmapDrawable) mActivityContext.getResources().getDrawable(android.R.drawable.btn_star_big_on);
        assertNotNull("Normal drawable is null", normal);

        // noinspection deprecation - can't enforce Lollipop here
        final BitmapDrawable clicked = (BitmapDrawable) mActivityContext.getResources().getDrawable(android.R.drawable.btn_star_big_off);
        assertNotNull("Clicked drawable is null", clicked);

        // noinspection deprecation - can't enforce Lollipop here
        final BitmapDrawable checked = (BitmapDrawable) mActivityContext.getResources().getDrawable(android.R.drawable.star_off);
        assertNotNull("Checked drawable is null", checked);

        final StateListDrawable stateList = Coloring.createMultiStateDrawable(normal, clicked, checked, true);
        assertNotNull("Contrast state drawable is null", stateList);
        assertTrue("Contrast state drawable is not stateful", stateList.isStateful());
        final Drawable.ConstantState constantState = stateList.getConstantState();
        assertNotNull("Constant state is null", constantState);
    }

    // <editor-fold desc="Private helpers">

    /**
     * Gets the hex value of an integer. This is just a shorthand for {@link Integer#toHexString(int)}.
     *
     * @param decimal The number to convert
     * @return The string representation of the unsigned integer value represented by the argument in hexadecimal (base 16)
     */
    @NonNull
    private String hex(final int decimal) {
        return Integer.toHexString(decimal).toUpperCase();
    }
    // </editor-fold>

}