package me.angrybyte.sillyandroid.parsable;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

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
import static junit.framework.Assert.fail;
import static me.angrybyte.sillyandroid.parsable.Annotations.Clickable;
import static me.angrybyte.sillyandroid.parsable.Annotations.FindView;
import static me.angrybyte.sillyandroid.parsable.Annotations.Layout;
import static me.angrybyte.sillyandroid.parsable.Annotations.LongClickable;
import static me.angrybyte.sillyandroid.parsable.Annotations.Menu;

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
        public int getInt() {
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
        private Object getObject() {
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
                final View view = new View(mContextRef.get());
                view.setId(1);
                return (ViewType) view;
            }
            return null;
        }
    }

    /**
     * A test activity with annotation-injectable menu and layout fields.
     */
    @Menu(1)
    @Layout(2)
    @SuppressWarnings("ResourceType")
    private static final class TestMenuLayoutActivity extends Activity implements LayoutWrapper, View.OnClickListener, View.OnLongClickListener {

        @FindView(1)
        @Clickable
        @LongClickable
        @SuppressWarnings("unused")
        private View mInjectedView;

        @SuppressWarnings("unused")
        private int mMenuId;

        @SuppressWarnings("unused")
        private int mLayoutId;

        private boolean mIsViewClicked;
        private boolean mIsViewLongClicked;

        /**
         * {@inheritDoc}
         */
        @Override
        public <ViewType extends View> ViewType findView(@IdRes final int viewId) {
            return new MockLayoutWrapper(this).findView(viewId);
        }

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

        /**
         * Gets the injected View value passed through the annotation.
         *
         * @return A View
         */
        public View getInjectedView() {
            return mInjectedView;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void onClick(final View v) {
            mIsViewClicked = true;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean onLongClick(final View v) {
            mIsViewLongClicked = true;
            return true;
        }

        /**
         * Checks if injected view was clicked.
         *
         * @return {@code True} if clicked, {@code false} if not yet clicked
         */
        public boolean isViewClicked() {
            return mIsViewClicked;
        }

        /**
         * Checks if injected view was long clicked.
         *
         * @return {@code True} if long clicked, {@code false} if not yet long clicked
         */
        public boolean isViewLongClicked() {
            return mIsViewLongClicked;
        }
    }
    // </editor-fold>

    // <editor-fold desc="Tests setup">
    private Activity mActivityContext;

    /**
     * Sets up the testing environment.
     */
    @Before
    public void setUp() {
        mActivityContext = Robolectric.setupActivity(TestMenuLayoutActivity.class);
    }

    /**
     * Destroys the testing environment.
     */
    @After
    public void tearDown() {
        mActivityContext = null;
    }
    // </editor-fold>

    /**
     * Tests the {@link AnnotationParser#getAllFields(Class)} method using {@link TestParsableClass}.
     */
    @Test
    public void testGetAllFieldsBase() {
        final List<Field> baseFields = AnnotationParser.getAllFields(TestParsableClass.class);
        final String baseString = Arrays.toString(baseFields.toArray());
        assertNotNull("Fields list is null", baseFields);
        assertTrue("Fields list is not parsed correctly: " + baseString, baseFields.size() >= 1);
        assertTrue("Field 'mIntField' not found: " + baseString, containsField(baseFields, "mIntField"));
    }

    /**
     * Tests the {@link AnnotationParser#getAllFields(Class)} method using {@link TestFinalParsableClass}.
     */
    @Test
    public void testGetAllFieldsExtended() {
        final List<Field> extFields = AnnotationParser.getAllFields(TestFinalParsableClass.class);
        final String extString = Arrays.toString(extFields.toArray());
        assertNotNull("Fields list is null", extFields);
        assertTrue("Fields list is not parsed correctly: " + extString, extFields.size() >= 2);
        assertTrue("Field 'mIntField' not found: " + extString, containsField(extFields, "mIntField"));
        assertTrue("Field 'mObjectField' not found: " + extString, containsField(extFields, "mObjectField"));
    }

    /**
     * Tests the {@link AnnotationParser#parseType(Context, Object)} method.
     */
    @Test
    public void testParseType() {
        final TestMenuLayoutActivity activity = (TestMenuLayoutActivity) mActivityContext;
        AnnotationParser.parseType(mActivityContext, activity);
        assertEquals("Menu ID not injected properly", activity.getMenuId(), 1);
        assertEquals("Layout ID not injected properly", activity.getLayoutId(), 2);
    }

    /**
     * Tests the {@link AnnotationParser#verifyTypeOfView(Field, Object)} method using a non-View and a View.
     */
    @Test
    public void testVerifyTypeOfView() {
        // check a non-View first (it should fail the verification)
        Field objectField = null;
        final TestFinalParsableClass objectHolder = new TestFinalParsableClass();
        objectHolder.mObjectField = new Object();
        try {
            objectField = TestFinalParsableClass.class.getDeclaredField("mObjectField");
        } catch (NoSuchFieldException e) {
            fail("Object field not found by name: " + e.getMessage());
        }
        try {
            AnnotationParser.verifyTypeOfView(objectField, objectHolder);
            fail("Non-View " + getFieldName(objectField) + " recognized as View");
        } catch (IllegalArgumentException ignored) {}

        // check a View now (it should not fail the test)
        Field viewField = null;
        final TestMenuLayoutActivity viewHolder = new TestMenuLayoutActivity();
        viewHolder.mInjectedView = new TextView(mActivityContext);
        try {
            viewField = TestMenuLayoutActivity.class.getDeclaredField("mInjectedView");
        } catch (NoSuchFieldException e) {
            fail("View field not found by name: " + e.getMessage());
        }
        try {
            AnnotationParser.verifyTypeOfView(viewField, viewHolder);
        } catch (IllegalArgumentException e) {
            fail("View " + getFieldName(viewField) + " not recognized as View: " + e.getMessage());
        }
    }

    /**
     * Tests the {@link AnnotationParser#parseFields(Context, Object, LayoutWrapper)} method.
     */
    @Test
    public void testParseFields() {
        final TestMenuLayoutActivity activity = (TestMenuLayoutActivity) mActivityContext;
        final LayoutWrapper layoutWrapper = (LayoutWrapper) mActivityContext;
        AnnotationParser.parseFields(mActivityContext, activity, layoutWrapper);
        final View injected = activity.getInjectedView();
        assertNotNull("View not injected, is null", injected);
        assertEquals("View not injected properly", injected.getId(), 1);
    }

    /**
     * Tests the usage of {@link Clickable} annotation with {@link AnnotationParser#parseFields(Context, Object, LayoutWrapper)}.
     */
    @Test
    public void testParseClickableView() {
        final TestMenuLayoutActivity activity = (TestMenuLayoutActivity) mActivityContext;
        final LayoutWrapper layoutWrapper = (LayoutWrapper) mActivityContext;
        AnnotationParser.parseFields(mActivityContext, activity, layoutWrapper);
        activity.getInjectedView().performClick();
        assertTrue("View click not performed", activity.isViewClicked());
    }

    /**
     * Tests the usage of {@link LongClickable} annotation with {@link AnnotationParser#parseFields(Context, Object, LayoutWrapper)}.
     */
    @Test
    public void testParseLongClickableView() {
        final TestMenuLayoutActivity activity = (TestMenuLayoutActivity) mActivityContext;
        final LayoutWrapper layoutWrapper = (LayoutWrapper) mActivityContext;
        AnnotationParser.parseFields(mActivityContext, activity, layoutWrapper);
        activity.getInjectedView().performLongClick();
        assertTrue("View long click not performed", activity.isViewLongClicked());
    }

    // <editor-fold desc="Private helpers">

    /**
     * Checks if given fields list contain the given field name.
     *
     * @param fieldList All of the class' fields
     * @param fieldName The field name to search for in the list
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
     * Gets the given field's name using {@link Field#getName()}. Returns {@code "null"}, a 4-character String if the given Field is {@code null}.
     *
     * @param field The field to get the name from
     * @return A non-{@code null} String representation of the field's name
     */
    private String getFieldName(@Nullable final Field field) {
        return field == null ? "null" : field.getName();
    }
    // </editor-fold>

}
