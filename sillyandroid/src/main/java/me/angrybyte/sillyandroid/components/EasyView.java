package me.angrybyte.sillyandroid.components;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.Px;
import android.support.annotation.RequiresApi;
import android.support.annotation.StringRes;
import android.support.annotation.UiThread;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;

import me.angrybyte.sillyandroid.SillyAndroid;
import me.angrybyte.sillyandroid.parsable.LayoutWrapper;

/**
 * An extension of the {@link View} with applied extensions from the {@link SillyAndroid} extension set.
 */
@UiThread
@SuppressWarnings("unused")
public class EasyView extends View implements LayoutWrapper {

    /**
     * Simple constructor to use when creating a view from code.
     *
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc.
     */
    public EasyView(@NonNull final Context context) {
        super(context);
    }

    /**
     * Constructor that is called when inflating a view from XML. This is called when a view is being constructed from an XML file,
     * supplying attributes that were specified in the XML file. This version uses a default style of 0, so the only attribute values
     * applied are those in the Context's Theme and the given AttributeSet.
     * <p>
     * <p>
     * The method onFinishInflate() will be called after all children have been added.
     *
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc.
     * @param attrs   The attributes of the XML tag that is inflating the view.
     * @see #EasyView(Context, AttributeSet, int)
     */
    public EasyView(@NonNull final Context context, @Nullable final AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Perform inflation from XML and apply a class-specific base style from a theme attribute. This constructor of View allows subclasses
     * to use their own base style when they are inflating. For example, a Button class's constructor would call this version of the super
     * class constructor and supply <code>R.attr.buttonStyle</code> for <var>defStyleAttr</var>; this allows the theme's button style to
     * modify all of the base view attributes (in particular its background) as well as the Button class's attributes.
     *
     * @param context      The Context the view is running in, through which it can access the current theme, resources, etc.
     * @param attrs        The attributes of the XML tag that is inflating the view.
     * @param defStyleAttr An attribute in the current theme that contains a reference to a style resource that supplies default values for
     *                     the view. Can be 0 to not look for defaults.
     * @see #EasyView(Context, AttributeSet)
     */
    public EasyView(@NonNull final Context context, @Nullable final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Perform inflation from XML and apply a class-specific base style from a theme attribute or style resource. This constructor of View
     * allows subclasses to use their own base style when they are inflating.
     * <p>
     * When determining the final value of a particular attribute, there are four inputs that come into play:
     * <ol>
     * <li>Any attribute values in the given AttributeSet.
     * <li>The style resource specified in the AttributeSet (named "style").
     * <li>The default style specified by <var>defStyleAttr</var>.
     * <li>The default style specified by <var>defStyleRes</var>.
     * <li>The base values in this theme.
     * </ol>
     * <p>
     * Each of these inputs is considered in-order, with the first listed taking precedence over the following ones. In other words, if in
     * the AttributeSet you have supplied <code>&lt;Button * textColor="#ff000000"&gt;</code> , then the button's text will <em>always</em>
     * be black, regardless of what is specified in any of the styles.
     *
     * @param context      The Context the view is running in, through which it can access the current theme, resources, etc.
     * @param attrs        The attributes of the XML tag that is inflating the view.
     * @param defStyleAttr An attribute in the current theme that contains a reference to a style resource that supplies default values for
     *                     the view. Can be 0 to not look for defaults.
     * @param defStyleRes  A resource identifier of a style resource that supplies default values for the view, used only if defStyleAttr is
     *                     0 or can not be found in the theme. Can be 0 to not look for defaults.
     * @see #EasyView(Context, AttributeSet, int)
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public EasyView(@NonNull final Context context, @Nullable final AttributeSet attrs, final int defStyleAttr, final int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /**
     * Returns the result from {@link SillyAndroid#findViewById(View, int)}.
     */
    public final <ViewType extends View> ViewType findView(@IdRes final int viewId) {
        return SillyAndroid.findViewById(this, viewId);
    }

    /**
     * Returns the result from {@link ContextCompat#getDrawable(Context, int)}.
     */
    @Nullable
    public Drawable getDrawableCompat(@DrawableRes final int drawableId) {
        return ContextCompat.getDrawable(getContext(), drawableId);
    }

    /**
     * Invokes {@link ViewCompat#setBackground(View, Drawable)} with the same arguments.
     */
    public void setBackgroundCompat(@Nullable final Drawable drawable) {
        ViewCompat.setBackground(this, drawable);
    }

    /**
     * Invokes {@link SillyAndroid#setPadding(View, int, int, int, int)} with the same arguments.
     */
    public void setPadding(@Px final int start, @Px final int top, @Px final int end, @Px final int bottom) {
        SillyAndroid.setPadding(this, start, top, end, bottom);
    }

    /**
     * Invokes {@link SillyAndroid#setPadding(View, int)} with the same arguments.
     */
    public void setPadding(@Px final int padding) {
        SillyAndroid.setPadding(this, padding);
    }

    /**
     * Invokes {@link SillyAndroid#toastShort(Context, int)}.
     */
    public void toastShort(@StringRes final int stringId) {
        SillyAndroid.toastShort(getContext(), stringId);
    }

    /**
     * Invokes {@link SillyAndroid#toastShort(Context, String)}.
     */
    public void toastShort(@NonNull final String string) {
        SillyAndroid.toastShort(getContext(), string);
    }

    /**
     * Invokes {@link SillyAndroid#toastLong(Context, int)}.
     */
    public void toastLong(@StringRes final int stringId) {
        SillyAndroid.toastLong(getContext(), stringId);
    }

    /**
     * Invokes {@link SillyAndroid#toastLong(Context, String)}.
     */
    public void toastLong(@NonNull final String string) {
        SillyAndroid.toastLong(getContext(), string);
    }

}
