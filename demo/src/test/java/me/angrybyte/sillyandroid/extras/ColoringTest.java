package me.angrybyte.sillyandroid.extras;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.util.Pair;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.LinkedList;
import java.util.List;

import me.angrybyte.sillyandroid.BuildConfig;

import static junit.framework.Assert.assertEquals;

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

    // FIXME Untested:
    //
    //
    // public final void testColorBitmap() {
    //
    // }
    //
    // public final void testCreateColoredDrawable() {
    //
    // }
    //
    // public final void testColorDrawable() {
    //
    // }
    //
    // public final void testColorDrawable1() {
    //
    // }
    //
    // public final void testColorDrawableWrapped() {
    //
    // }
    //
    // public final void testColorDrawableWrapped1() {
    //
    // }
    //
    // public final void testColorUnknownDrawable() {
    //
    // }
    //
    // public final void testColorVectorDrawable() {
    //
    // }
    //
    // public final void testColorVectorDrawable1() {
    //
    // }
    //
    // public final void testCreateStateList() {
    //
    // }
    //
    // public final void testCreateRippleDrawable() {
    //
    // }
    //
    // public final void testCreateRippleDrawable1() {
    //
    // }
    //
    // public final void testCreateResponsiveDrawable() {
    //
    // }
    //
    // public final void testCreateResponsiveDrawable1() {
    //
    // }
    //
    // public final void testCreateContrastTextColors() {
    //
    // }
    //
    // public final void testCreateContrastStateDrawable() {
    //
    // }
    //
    // public final void testCreateMultiStateDrawable() {
    //
    // }

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