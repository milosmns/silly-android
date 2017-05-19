
package me.angrybyte.sillyandroid.parsable;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * A runtime annotation processor for annotations like {@link Annotations.FindView}, {@link Annotations.Layout}, {@link Annotations.Clickable} and similar.
 * Note that using this parser slows down your initialization due to JVM reflection cost, but it gets quicker as the parser uses its {@link #FIELD_CACHE}
 * more and more.
 * <br>
 * To parse annotations like {@link Annotations.Layout} you need to use {@link #parseType(Context, Object)}, and to parse annotations like
 * {@link Annotations.FindView} you need to use {@link #parseFields(Context, Object, LayoutWrapper)}.
 *
 * @see me.angrybyte.sillyandroid.parsable.Annotations
 */
@SuppressWarnings("WeakerAccess")
public final class AnnotationParser {

    private static final String TAG = AnnotationParser.class.getSimpleName();
    private static final String MENU_ID_FIELD_NAME = "mMenuId";
    private static final String LAYOUT_ID_FIELD_NAME = "mLayoutId";
    private static final Map<String, List<Field>> FIELD_CACHE = new HashMap<>();

    /**
     * Making sure that this class's default constructor is private.
     */
    private AnnotationParser() {}

    /**
     * Checks for usable annotations on the given Object, and sets the corresponding properties on the instance.
     * For example, the 'menu' annotation will set the {@code mMenuId} field, while the 'layout' annotation sets the {@code mLayoutId} field.
     *
     * @param context  Which context to use
     * @param instance A non-{@code null} Object that was instantiated from the type being parsed
     */
    public static void parseType(@NonNull final Context context, @NonNull final Object instance) {
        final Class<?> parsedClass = instance.getClass();

        // look for the @Menu annotation
        if (parsedClass.isAnnotationPresent(Annotations.Menu.class)) {
            Annotations.Menu annotation = parsedClass.getAnnotation(Annotations.Menu.class);
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
        if (parsedClass.isAnnotationPresent(Annotations.Layout.class)) {
            Annotations.Layout annotation = parsedClass.getAnnotation(Annotations.Layout.class);
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
     * For example, to find a 'title text view' and make it clickable, in your {@link LayoutWrapper} implementation (i.e. activity, fragment, etc) specify:
     * <br>
     * <pre>
     *     &#64;Clickable
     *     &#64;FindView(R.id.titleTextView)
     *     private TextView mTitleTextView;
     * </pre>
     * After this, you need to call this method to properly initialize all fields. It's best to do it in
     * your {@link android.app.Activity#onCreate(android.os.Bundle)} or
     * {@link android.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)} methods after setting up the content
     * view. To be able to use {@link Annotations.Clickable} and {@link Annotations.LongClickable}, your {@code instance} needs to implement
     * {@link android.view.View.OnClickListener} and {@link android.view.View.OnLongClickListener} respectively.
     * <p>
     * <b>Note</b>: Some library classes already implement these operation chains by default; for examples see: {@link me.angrybyte.sillyandroid.parsable}
     * </p>
     *
     * @param context  Which context to use
     * @param instance A non-{@code null} instance that holds the annotated fields being parsed
     * @param wrapper  A non-{@code null} {@link LayoutWrapper}, used to find the Views
     * @return A map (sparse array) of Views found while parsing the {@link Annotations.FindView} annotation
     */
    public static SparseArray<View> parseFields(@NonNull final Context context, @NonNull final Object instance, @NonNull final LayoutWrapper wrapper) {
        // find all fields
        final SparseArray<View> parsedFields = new SparseArray<>();
        final List<Field> allFields = getAllFields(instance.getClass());

        // run through all fields
        for (final Field iField : allFields) {
            // check for annotations - click/long-click makes no sense when field is not parsed through this
            if (iField.isAnnotationPresent(Annotations.FindView.class)) {
                verifyTypeOfView(iField, instance);
                final View v = findAndSetView(context, instance, wrapper, iField);
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
     * @param classInstance Which class to look into
     * @return A list of declared class' fields. Do not modify this instance
     */
    @NonNull
    @VisibleForTesting
    static List<Field> getAllFields(@NonNull final Class<?> classInstance) {
        Class<?> parsedClass = classInstance;
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
    @VisibleForTesting
    static boolean setFieldValue(@NonNull final Object instance, final int value, @NonNull final String fieldName) {
        // check the cache first (iterating is much faster than reflection)
        Field fieldReference = null;

        List<Field> classFields = getAllFields(instance.getClass());
        for (final Field iField : classFields) {
            if (iField.getName().equals(fieldName)) {
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
    @VisibleForTesting
    static void setClickListener(@Nullable final View view, @Nullable final View.OnClickListener listener) {
        if (view != null && listener != null) {
            view.setOnClickListener(listener);
        } else {
            throw new IllegalArgumentException("Cannot set a click listener " + listener + " to " + view);
        }
    }

    /**
     * Sets the long-click listener to the given {@link View}.
     *
     * @param view     The View to set the listener to
     * @param listener The listener
     */
    @VisibleForTesting
    static void setLongClickListener(@Nullable final View view, @Nullable final View.OnLongClickListener listener) {
        if (view != null && listener != null) {
            view.setOnLongClickListener(listener);
        } else {
            throw new IllegalArgumentException("Cannot set a long-click listener " + listener + " to " + view);
        }
    }

    /**
     * Verifies that the given field is a {@link View} or crashes.
     *
     * @param field  The field you are checking
     * @param object The object instance holding the field
     * @throws IllegalArgumentException When field is not a {@link View}
     */
    @VisibleForTesting
    static void verifyTypeOfView(@NonNull final Field field, @NonNull final Object object) {
        try {
            field.setAccessible(true);
            Object value = field.get(object);
            if (value instanceof View || View.class.isAssignableFrom(field.getType())) {
                return;
            }
        } catch (IllegalAccessException ignored) {}
        throw new IllegalArgumentException("Field \n\t'" + String.valueOf(field) + "\n is not a View, instead it is a " + field.getType().getSimpleName());
    }

    /**
     * Tries to find the View declared through the {@link Annotations.FindView} annotation and set it to the given instance field.
     *
     * @param context  Which context to use
     * @param instance A non-{@code null} instance that holds the field
     * @param wrapper  A non-{@code null} {@link LayoutWrapper}, used to find the Views
     * @param field    The field to assign the View instance to
     * @return The View that was set to the field, or {@code null} if nothing was set
     */
    @Nullable
    @VisibleForTesting
    static View findAndSetView(@NonNull final Context context, @NonNull final Object instance, @NonNull final LayoutWrapper wrapper,
                               @NonNull final Field field) {
        // check 'find view' annotation, don't crash when 'safe' is set
        final Annotations.FindView annotation = field.getAnnotation(Annotations.FindView.class);
        final boolean safeFail = annotation.safeFail();
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
            final View v = wrapper.findView(viewId);
            if (v == null && !safeFail) {
                throw new IllegalStateException("View not found for " + field + " in " + instance.getClass().getName());
            } else if (v == null) {
                Log.e(TAG, "View not found for " + field + " in " + instance.getClass().getName());
                return null;
            }

            // set the View instance to the field
            field.setAccessible(true);
            field.set(instance, v);
            return v;
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

}
