
package me.angrybyte.sillyandroid.parsable;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("WeakerAccess")
public final class AnnotationParser {

    private static final String TAG = AnnotationParser.class.getSimpleName();
    private static final String MENU_ID_FIELD_NAME = "mMenuId";
    private static final String LAYOUT_ID_FIELD_NAME = "mLayoutId";
    private static final Map<String, List<Field>> FIELD_CACHE = new HashMap<>();

    /**
     * Checks for usable annotations on the given Object, and sets the corresponding properties on the instance.
     *
     * @param context  Which context to use
     * @param instance A non-{@code null} Object that was instantiated from the type being parsed
     */
    public static void parseType(@NonNull final Context context, @NonNull final Object instance) {
        Class<?> currentClass = instance.getClass();

        // look for the @Menu annotation
        if (currentClass.isAnnotationPresent(Annotations.Menu.class)) {
            Annotations.Menu annotation = currentClass.getAnnotation(Annotations.Menu.class);
            int menuId = annotation.value();
            if (menuId == -1) {
                // ID not provided, check the name
                menuId = context.getResources().getIdentifier(annotation.name(), "menu", context.getPackageName());
            }
            if (menuId < 1) {
                throw new IllegalArgumentException("Menu ID must be provided in the @Menu annotation");
            }
            if (!setFieldValue(instance, menuId, MENU_ID_FIELD_NAME)) {
                throw new IllegalArgumentException("Failed to set the menu ID");
            }
        }

        // look for the @Layout annotation
        if (currentClass.isAnnotationPresent(Annotations.Layout.class)) {
            Annotations.Layout annotation = currentClass.getAnnotation(Annotations.Layout.class);
            int layoutId = annotation.value();
            if (layoutId == -1) {
                // ID not provided, check the name
                layoutId = context.getResources().getIdentifier(annotation.name(), "layout", context.getPackageName());
            }
            if (layoutId < 1) {
                throw new IllegalArgumentException("Layout ID must be provided in the @Layout annotation");
            }
            if (!setFieldValue(instance, layoutId, LAYOUT_ID_FIELD_NAME)) {
                throw new IllegalArgumentException("Failed to set the layout ID");
            }
        }
    }

    /**
     * Tries to parse the given Object, looking for Views with usable annotations such as {@link Annotations.FindView}, {@link Annotations.Clickable},
     * {@link Annotations.LongClickable} and similar.
     *
     * @param context  Which context to use
     * @param instance A non-{@code null} instance that holds the annotated fields being parsed
     * @param wrapper  A non-{@code null} {@link LayoutWrapper}, used to find the Views
     * @return A map (sparse array) of Views found while parsing the {@link Annotations.FindView} annotation
     */
    public static SparseArray<View> parseFields(@NonNull final Context context, @NonNull final Object instance, @NonNull final LayoutWrapper wrapper) {
        // find all fields
        SparseArray<View> parsedFields = new SparseArray<>();
        List<Field> allFields = getAllFields(instance.getClass());

        // run through all fields
        for (Field iField : allFields) {
            // check for annotations - click/long-click makes no sense when field is not parsed through this
            if (iField.isAnnotationPresent(Annotations.FindView.class)) {
                View v = findAndSetView(context, instance, wrapper, iField);
                if (v == null) {
                    continue; // happens when 'safe' is set for @FindView and View is not found
                }
                parsedFields.put(v.getId(), v);
                
                // add listeners
                if (iField.isAnnotationPresent(Annotations.Clickable.class) && (instance instanceof View.OnClickListener)) {
                    setClickListener(v, (View.OnClickListener) instance);
                }
                if (iField.isAnnotationPresent(Annotations.LongClickable.class) && (instance instanceof View.OnLongClickListener)) {
                    setLongClickListener(v, (View.OnLongClickListener) instance);
                }
            }
        }
        return parsedFields;
    }

    /**
     * Returns all fields from the given class, including its superclass fields. If cached fields are available, they will be used; instead, a new list will
     * be saved to the cache.
     *
     * @param parsedClass Which class to look into
     * @return A list of declared class' fields. Do not modify this instance
     */
    @NonNull
    public static List<Field> getAllFields(@NonNull Class<?> parsedClass) {
        final String name = parsedClass.getName();
        List<Field> allFields = FIELD_CACHE.get(name);
        if (allFields == null || allFields.isEmpty()) {
            allFields = new LinkedList<>();
            while (parsedClass != null && parsedClass != Object.class) {
                allFields.addAll(Arrays.asList(parsedClass.getDeclaredFields()));
                parsedClass = parsedClass.getSuperclass();
            }
            FIELD_CACHE.put(name, allFields);
        }
        return allFields;
    }

    /**
     * Assigns an integer to the given field.
     *
     * @param instance  A non-{@code null} instance that holds the field
     * @param value     The value being set
     * @param fieldName The name of the field being modified
     * @return {@code True} if the value was successfully assigned, {@code false} otherwise
     */
    private static boolean setFieldValue(@NonNull final Object instance, final int value, @NonNull final String fieldName) {
        // check the cache first (iterating is much faster than reflection)
        Field fieldReference = null;
        for (Field iField : getAllFields(instance.getClass())) {
            if (iField.getName().equals(instance.getClass().getName())) {
                // found it!
                fieldReference = iField;
                break;
            }
        }

        // if not found, die.
        if (fieldReference == null) {
            throw new IllegalArgumentException("Class '" + instance.getClass().getName() + "' needs to have a '" + fieldName + "' field");
        }

        // finally, set the value
        try {
            fieldReference.setAccessible(true);
            fieldReference.setInt(instance, value);
            return true;
        } catch (IllegalAccessException e) {
            Log.w(TAG, "Failed to set " + value + " to " + fieldName, e);
        }
        return false;
    }

    /**
     * Sets the click listener to the given {@link View}.
     *
     * @param view     The View to set the listener to
     * @param listener The listener
     */
    private static void setClickListener(@Nullable final View view, @Nullable final View.OnClickListener listener) {
        if (view != null && listener != null) {
            view.setOnClickListener(listener);
        } else {
            throw new RuntimeException("Cannot set a click listener " + listener + " to " + view);
        }
    }

    /**
     * Sets the long-click listener to the given {@link View}.
     *
     * @param view     The View to set the listener to
     * @param listener The listener
     */
    private static void setLongClickListener(@Nullable final View view, @Nullable final View.OnLongClickListener listener) {
        if (view != null && listener != null) {
            view.setOnLongClickListener(listener);
        } else {
            throw new RuntimeException("Cannot set a long-click listener " + listener + " to " + view);
        }
    }

    @Nullable
    private static View findAndSetView(@NonNull final Context context, @NonNull final Object instance, @NonNull final LayoutWrapper wrapper,
                                       @NonNull final Field field) {
        // check @FindView annotation, don't crash when 'safe' is set
        Annotations.FindView annotation = field.getAnnotation(Annotations.FindView.class);
        boolean safeFail = annotation.safeFail();
        int viewId = annotation.value();
        if (viewId == -1) {
            // ID not provided, check the name
            viewId = context.getResources().getIdentifier(annotation.name(), "id", context.getPackageName());
        }
        if (viewId < 1 && !safeFail) {
            throw new IllegalStateException("View not found for " + field.getName());
        } else if (viewId < 1 && safeFail) {
            Log.e(TAG, "Failed to find View for " + field.getName());
            return null;
        }

        try {
            // view ID is valid, try to find it
            View v = wrapper.findView(viewId);
            if (v == null && !safeFail) {
                throw new RuntimeException("View not found for " + field + " in " + instance.getClass().getName());
            } else if (v == null) {
                Log.e(TAG, "View not found for " + field + " in " + instance.getClass().getName());
                return null;
            }

            // set the View instance to the field
            field.setAccessible(true);
            field.set(instance, v);
            return v;
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
