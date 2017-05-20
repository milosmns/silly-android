package me.angrybyte.sillyandroid.extras;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
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
        testCases.add(new int[]{-1, 0});
        testCases.add(new int[]{0, 0});
        testCases.add(new int[]{130, 130});
        testCases.add(new int[]{255, 255});
        testCases.add(new int[]{256, 255});

        // test with those cases
        final String errorText = "Error in clampRGB(%d)";
        for (final int[] testCase : testCases) {
            assertEquals(String.format(errorText, testCase[0]), Coloring.clampRGB(testCase[0]), testCase[1]);
        }
    }

    /**
     * Tests the {@link Coloring#decodeColor(String)} method.
     */
    @Test
    public final void testDecodeColor() {
        final String[] realColors = new String[]{
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
            assertEquals(String.format(errorText, testCase.first), Coloring.decodeColor(testCase.first), (int) testCase.second);
        }
    }

    // FIXME Untested:
    //
    // public final void testAlphaBlendWithWhite() {
    //
    // }
    //
    // public final void testAlphaBlendWithBlack() {
    //
    // }
    //
    // public final void testAlphaBlendColors() {
    //
    // }
    //
    // public final void testDarkenColor() {
    //
    // }
    //
    // public final void testLightenColor() {
    //
    // }
    //
    // public final void testShiftBrightness() {
    //
    // }
    //
    // public final void testDimColor() {
    //
    // }
    //
    // public final void testOpacifyColor() {
    //
    // }
    //
    // public final void testShiftAlpha() {
    //
    // }
    //
    // public final void testContrastColor() {
    //
    // }
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
    // public final void testColorDrawable1() {
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

}