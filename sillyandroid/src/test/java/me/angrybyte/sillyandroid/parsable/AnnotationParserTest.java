package me.angrybyte.sillyandroid.parsable;

import android.support.annotation.NonNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import me.angrybyte.sillyandroid.BuildConfig;

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
    // </editor-fold>

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

}
