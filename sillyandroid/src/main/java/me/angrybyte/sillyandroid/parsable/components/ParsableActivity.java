package me.angrybyte.sillyandroid.parsable.components;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.MenuRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.SparseArray;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import me.angrybyte.sillyandroid.components.EasyActivity;
import me.angrybyte.sillyandroid.parsable.AnnotationParser;
import me.angrybyte.sillyandroid.parsable.Annotations;

/**
 * An extension from {@link EasyActivity} with included {@link AnnotationParser} capabilities.
 */
public class ParsableActivity extends EasyActivity implements View.OnClickListener, View.OnLongClickListener {

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
     * {@inheritDoc}
     */
    @Override
    @CallSuper
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AnnotationParser.parseType(this, this);
        if (getLayoutId() > 0) {
            setContentView(getLayoutId());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setContentView(@LayoutRes final int layoutResID) {
        super.setContentView(layoutResID);
        if (mFoundViews != null) {
            mFoundViews.clear();
        }
        mFoundViews = AnnotationParser.parseFields(this, this, this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setContentView(@NonNull final View view) {
        super.setContentView(view);
        if (mFoundViews != null) {
            mFoundViews.clear();
        }
        mFoundViews = AnnotationParser.parseFields(this, this, this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setContentView(@NonNull final View view, @Nullable final ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        if (mFoundViews != null) {
            mFoundViews.clear();
        }
        mFoundViews = AnnotationParser.parseFields(this, this, this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        super.onCreateOptionsMenu(menu);
        if (getMenuId() > 0) {
            getMenuInflater().inflate(getMenuId(), menu);
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onBlockingDestroy() {
        super.onBlockingDestroy();
        if (mFoundViews != null) {
            mFoundViews.clear();
        }
    }

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
     * Tries to find a {@link Annotations.FindView}-annotated View from the {@link #mFoundViews} cache. Note that cache is emptied when this activity dies.
     *
     * @param viewId The ID of the View being looked for
     * @return Either a View object; or {@code null} if not found, not parsed at all or activity died already
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
