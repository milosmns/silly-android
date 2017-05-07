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
        @SuppressWarnings({"ResourceType", "unchecked"})
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
    @Annotations.Menu(1)
    @Annotations.Layout(2)
    @SuppressWarnings("ResourceType")
    private static final class TestMenuLayoutActivity extends Activity implements LayoutWrapper, View.OnClickListener, View.OnLongClickListener {

        @Annotations.FindView(1)
        @Annotations.Clickable
        @Annotations.LongClickable
        @SuppressWarnings("unused")
        private View mInjected;

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
            return mInjected;
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

    /**
     * Tests the {@link AnnotationParser#parseFields(Context, Object, LayoutWrapper)} method.
     */
    @Test
    public void testParseViews() {
        final TestMenuLayoutActivity activity = (TestMenuLayoutActivity) mActivity;
        AnnotationParser.parseFields(mActivity, activity, activity);
        final View injected = activity.getInjectedView();
        assertEquals("View not injected properly", injected.getId(), 1);
        injected.performClick();
        assertTrue("View click not performed", activity.isViewClicked());
        injected.performLongClick();
        assertTrue("View long click not performed", activity.isViewLongClicked());
    }

}
