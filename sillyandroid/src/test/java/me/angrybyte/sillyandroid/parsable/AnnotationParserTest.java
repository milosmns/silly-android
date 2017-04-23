package me.angrybyte.sillyandroid.parsable;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.lang.reflect.Field;
import java.util.List;

import me.angrybyte.sillyandroid.BuildConfig;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

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
     * Tests the {@link AnnotationParser#getAllFields(Class)} method.
     */
    @Test
    public void testGetAllFields() {
        List<Field> fields = AnnotationParser.getAllFields(TestParsableClass.class);
        assertNotNull("[1] Fields list is null", fields);
        assertEquals("[1] Fields list is not parsed correctly", fields.size(), 1);
        assertEquals("[1] Field 'mIntField' not found", fields.get(0).getName(), "mIntField");

        fields = AnnotationParser.getAllFields(TestFinalParsableClass.class);
        assertNotNull("[2] Fields list is null", fields);
        assertEquals("[2] Fields list is not parsed correctly", fields.size(), 2);
        assertEquals("[2] Field 'mObjectField' not found", fields.get(0).getName(), "mObjectField");
        assertEquals("[2] Field 'mIntField' not found", fields.get(1).getName(), "mIntField");
    }

}
