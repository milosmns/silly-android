package me.angrybyte.sillyandroid.extras;

import android.app.Activity;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import me.angrybyte.sillyandroid.BuildConfig;
import me.angrybyte.sillyandroid.components.EasyActivity;

/**
 * A set of tests related to the {@link Coloring}.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class ColoringTest {

    // <editor-fold desc="Tests setup">
    private Activity mActivityContext;

    /**
     * Sets up the testing environment.
     */
    @Before
    public void setUp() {
        mActivityContext = Robolectric.setupActivity(EasyActivity.class);
    }

    /**
     * Destroys the testing environment.
     */
    @After
    public void tearDown() {
        mActivityContext = null;
    }
    // </editor-fold>

    public void testClampRGB() {

    }

    public void testDecodeColor() {

    }

    public void testAlphaBlendWithWhite() {

    }

    public void testAlphaBlendWithBlack() {

    }

    public void testAlphaBlendColors() {

    }

    public void testDarkenColor() {

    }

    public void testLightenColor() {

    }

    public void testShiftBrightness() {

    }

    public void testDimColor() {

    }

    public void testOpacifyColor() {

    }

    public void testShiftAlpha() {

    }

    public void testContrastColor() {

    }

    public void testColorBitmap() {

    }

    public void testCreateColoredDrawable() {

    }

    public void testColorDrawable() {

    }

    public void testColorDrawableWrapped() {

    }

    public void testColorDrawableWrapped1() {

    }

    public void testColorUnknownDrawable() {

    }

    public void testColorDrawable1() {

    }

    public void testColorVectorDrawable() {

    }

    public void testColorVectorDrawable1() {

    }

    public void testCreateStateList() {

    }

    public void testCreateRippleDrawable() {

    }

    public void testCreateRippleDrawable1() {

    }

    public void testCreateResponsiveDrawable() {

    }

    public void testCreateResponsiveDrawable1() {

    }

    public void testCreateContrastTextColors() {

    }

    public void testCreateContrastStateDrawable() {

    }

    public void testCreateMultiStateDrawable() {

    }

}