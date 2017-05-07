package me.angrybyte.sillyandroid.parsable;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.view.View;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import me.angrybyte.sillyandroid.BuildConfig;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 * A set of tests related to the {@link me.angrybyte.sillyandroid.parsable.AnnotationParser}.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class AnnotationParserTest {

    // <editor-fold desc="Inner classes">

    /**
     * The base class used for testing field parsing. Includes an integer field.
     */
    private static class TestParsableClass {

        @SuppressWarnings("unused")
        private int mIntField;

        /**
         * Gets the int field value.
         *
         * @return An integer
         */
        @SuppressWarnings("unused")
        public int getIntField() {
            return mIntField;
        }
    }

    /**
     * An extension class used for testing field parsing with an Object field.
     */
    private static final class TestFinalParsableClass extends TestParsableClass {

        @SuppressWarnings("unused")
        private Object mObjectField;

        /**
         * Gets the Object field value.
         *
         * @return A Object
         */
        @SuppressWarnings("unused")
        private Object getObjectField() {
            return mObjectField;
        }
    }

    /**
     * Mock implementation of the {@link LayoutWrapper}.
     */
    private static final class MockLayoutWrapper implements LayoutWrapper {

        private WeakReference<Context> mContextRef;

        /**
         * The default constructor.
         *
         * @param context A context to use for View inflation
         */
        MockLayoutWrapper(@NonNull final Context context) {
            mContextRef = new WeakReference<>(context);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        @SuppressWarnings({ "ResourceType", "unchecked" })
        public <ViewType extends View> ViewType findView(@IdRes final int viewId) {
            if (viewId == 1) {
                return (ViewType) new View(mContextRef.get());
            }
            return null;
        }
    }

    /**
     * A test activity with annotation-injectable menu and layout fields.
     */
    @Annotations.Menu(1)
    @Annotations.Layout(2)
    @SuppressWarnings("ResourceType")
    private static final class TestMenuLayoutActivity extends Activity {

        @SuppressWarnings("unused")
        private int mMenuId;

        @SuppressWarnings("unused")
        private int mLayoutId;

        /**
         * Gets the menu ID value passed through the annotation.
         *
         * @return An integer ID
         */
        public int getMenuId() {
            return mMenuId;
        }

        /**
         * Gets the layout ID value passed through the annotation.
         *
         * @return An integer ID
         */
        public int getLayoutId() {
            return mLayoutId;
        }
    }
    // </editor-fold>

    private Activity mActivity;

    /**
     * {@inheritDoc}
     */
    @Before
    public void setUp() {
        mActivity = Robolectric.setupActivity(TestMenuLayoutActivity.class);
    }

    /**
     * {@inheritDoc}
     */
    @After
    public void tearDown() {
        mActivity = null;
    }

    /**
     * Tests the {@link AnnotationParser#getAllFields(Class)} method using {@link TestParsableClass}.
     */
    @Test
    public void testGetAllFieldsBase() {
        final List<Field> baseFields = AnnotationParser.getAllFields(TestParsableClass.class);
        final String baseString = Arrays.toString(baseFields.toArray());
        assertNotNull("[1] Fields list is null", baseFields);
        assertTrue("[1] Fields list is not parsed correctly: " + baseString, baseFields.size() >= 1);
        assertTrue("[1] Field 'mIntField' not found: " + baseString, containsField(baseFields, "mIntField"));
    }

    /**
     * Tests the {@link AnnotationParser#getAllFields(Class)} method using {@link TestFinalParsableClass}.
     */
    @Test
    public void testGetAllFieldsExtended() {
        final List<Field> extFields = AnnotationParser.getAllFields(TestFinalParsableClass.class);
        final String extString = Arrays.toString(extFields.toArray());
        assertNotNull("[2] Fields list is null", extFields);
        assertTrue("[2] Fields list is not parsed correctly: " + extString, extFields.size() >= 2);
        assertTrue("[2] Field 'mIntField' not found: " + extString, containsField(extFields, "mIntField"));
        assertTrue("[2] Field 'mObjectField' not found: " + extString, containsField(extFields, "mObjectField"));
    }

    /**
     * Checks if given fields list contain the given field name.
     *
     * @return {@code True} if the given list contains the field, {@code false} if not
     */
    private boolean containsField(@NonNull final List<Field> fieldList, @NonNull final String fieldName) {
        for (int i = 0; i < fieldList.size(); i++) {
            if (fieldList.get(i).getName().equals(fieldName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Tests the {@link AnnotationParser#parseType(Context, Object)} method.
     */
    @Test
    public void testParseType() {
        final TestMenuLayoutActivity activity = (TestMenuLayoutActivity) mActivity;
        AnnotationParser.parseType(mActivity, activity);
        assertEquals("Menu ID not injected properly", activity.getMenuId(), 1);
        assertEquals("Layout ID not injected properly", activity.getLayoutId(), 2);
    }

}
