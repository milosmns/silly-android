package me.angrybyte.sillyandroid.parsable.components;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.MenuRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.util.SparseArray;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import me.angrybyte.sillyandroid.components.EasyDialog;
import me.angrybyte.sillyandroid.parsable.AnnotationParser;
import me.angrybyte.sillyandroid.parsable.Annotations;

/**
 * An extension from {@link EasyDialog} with included {@link AnnotationParser} capabilities.
 */
@SuppressWarnings({ "WeakerAccess", "unused" })
public class ParsableDialog extends EasyDialog implements View.OnClickListener, View.OnLongClickListener {

    /**
     * The layout ID pulled from the {@link me.angrybyte.sillyandroid.parsable.Annotations.Layout} annotation will be stored here.
     */
    @LayoutRes
    @SuppressWarnings("unused")
    private int mLayoutId;

    /**
     * The menu ID pulled from the {@link me.angrybyte.sillyandroid.parsable.Annotations.Menu} annotation will be stored here.
     */
    @MenuRes
    @SuppressWarnings("unused")
    private int mMenuId;

    /**
     * All Views annotated with {@link me.angrybyte.sillyandroid.parsable.Annotations.FindView} annotation will be mapped here.
     */
    private SparseArray<View> mFoundViews;

    /**
     * A real (external) dismiss listener, invoked from the dismiss proxy {@link #mOnDismissListenerProxy}.
     */
    private OnDismissListener mRealDismissListener;

    // <editor-fold desc="Constructors">

    /**
     * {@inheritDoc}
     */
    public ParsableDialog(@NonNull final Context context) {
        super(context);
        initializeParsedProperties(context);
    }

    /**
     * {@inheritDoc}
     */
    public ParsableDialog(@NonNull final Context context, @StyleRes final int themeResId) {
        super(context, themeResId);
        initializeParsedProperties(context);
    }

    /**
     * {@inheritDoc}
     */
    protected ParsableDialog(@NonNull final Context context, final boolean cancelable, @Nullable final OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        initializeParsedProperties(context);
    }
    // </editor-fold>

    // <editor-fold desc="Content View setters">

    /**
     * {@inheritDoc}
     */
    @Override
    public void setContentView(@LayoutRes final int layoutResID) {
        super.setContentView(layoutResID);
        mFoundViews.clear();
        mFoundViews = AnnotationParser.parseFields(getContext(), this, this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setContentView(@NonNull final View view) {
        super.setContentView(view);
        mFoundViews.clear();
        mFoundViews = AnnotationParser.parseFields(getContext(), this, this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setContentView(@NonNull final View view, @Nullable final ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        mFoundViews.clear();
        mFoundViews = AnnotationParser.parseFields(getContext(), this, this);
    }
    // </editor-fold>

    /**
     * Parses the type and all its fields using the {@link AnnotationParser}.
     *
     * @param context Which context to use
     */
    private void initializeParsedProperties(@NonNull final Context context) {
        AnnotationParser.parseType(context, this);
        if (getLayoutId() > 0) {
            setContentView(getLayoutId());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        setOnDismissListener(mOnDismissListenerProxy);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onCreateOptionsMenu(@NonNull final Menu menu) {
        super.onCreateOptionsMenu(menu);
        if (getMenuId() > 0 && getOwnerActivity() != null) {
            getOwnerActivity().getMenuInflater().inflate(getMenuId(), menu);
            return true;
        }
        return false;
    }

    @Override
    public void setOnDismissListener(@Nullable final OnDismissListener listener) {
        mRealDismissListener = listener;
    }

    /**
     * A local dismiss listener proxy that also clears up the parsed Views collection.
     */
    private final OnDismissListener mOnDismissListenerProxy = new OnDismissListener() {
        @Override
        public void onDismiss(final DialogInterface dialog) {
            mFoundViews.clear();
            final OnDismissListener realListener = mRealDismissListener;
            if (realListener != null) {
                realListener.onDismiss(dialog);
            }
            setOnDismissListener(null);
        }
    };

    /**
     * {@inheritDoc}
     */
    @Override
    public void onClick(final View v) {}

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onLongClick(final View v) {
        return false;
    }

    /**
     * Tries to find a {@link Annotations.FindView}-annotated View from the {@link #mFoundViews} cache. Note that cache is emptied when this dialog dies.
     *
     * @param viewId The ID of the View being looked for
     * @return Either a View object; or {@code null} if not found, not parsed at all or dialog died already
     */
    @Nullable
    @SuppressWarnings("unused")
    protected final View getFoundView(@IdRes final int viewId) {
        return mFoundViews == null ? null : mFoundViews.get(viewId);
    }

    /**
     * Returns the currently assigned layout's ID. This usually comes from {@link me.angrybyte.sillyandroid.parsable.Annotations.Layout}.
     */
    @LayoutRes
    protected final int getLayoutId() {
        return mLayoutId;
    }

    /**
     * Returns the currently assigned menu's ID. This usually comes from {@link me.angrybyte.sillyandroid.parsable.Annotations.Menu}.
     */
    @MenuRes
    protected final int getMenuId() {
        return mMenuId;
    }

}
