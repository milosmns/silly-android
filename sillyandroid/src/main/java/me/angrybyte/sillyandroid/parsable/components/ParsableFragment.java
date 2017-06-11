package me.angrybyte.sillyandroid.parsable.components;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.MenuRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import me.angrybyte.sillyandroid.SillyAndroid;
import me.angrybyte.sillyandroid.components.EasyFragment;
import me.angrybyte.sillyandroid.parsable.AnnotationParser;
import me.angrybyte.sillyandroid.parsable.Annotations;
import me.angrybyte.sillyandroid.parsable.LayoutWrapper;

/**
 * An extension from {@link EasyFragment} with included {@link AnnotationParser} capabilities.
 */
@SuppressWarnings("unused")
public class ParsableFragment extends EasyFragment implements View.OnClickListener, View.OnLongClickListener {

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
     * A flag that doesn't allow us to parse the same View hierarchy twice using {@link AnnotationParser#parseFields(Context, Object, LayoutWrapper)}.
     */
    private boolean mIsParsed;

    /**
     * The public, default, empty constructor which should have been included by default.
     */
    public ParsableFragment() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CallSuper
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Context context = getContext();
        if (context != null) {
            AnnotationParser.parseType(context, this);
            mIsParsed = true;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CallSuper
    public void onAttach(@NonNull final Context context) {
        super.onAttach(context);
        if (!mIsParsed) {
            AnnotationParser.parseType(context, this);
            mIsParsed = true;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    @CallSuper
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        // it's barely possible to go in here, but it's Android, so.. check if not parsed by now
        final Context context = getContext() == null ? inflater.getContext() : getContext();
        if (!mIsParsed && context != null) {
            AnnotationParser.parseType(context, this);
            mIsParsed = true;
        }

        // inflate and parse views now
        if (getLayoutId() > 0 && context != null) {
            final View contentView = inflater.inflate(getLayoutId(), container, false);
            // using 'this' instead of a new wrapper won't work because the wrapper uses #getView(), which will at this point return null
            if (mFoundViews != null) {
                mFoundViews.clear();
            }
            mFoundViews = AnnotationParser.parseFields(context, this, new LayoutWrapper() {
                @Override
                public <ViewType extends View> ViewType findView(@IdRes final int viewId) {
                    return SillyAndroid.findViewById(contentView, viewId);
                }
            });
        }

        return null; // nothing worked, die.
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (getMenuId() > 0) {
            inflater.inflate(getMenuId(), menu);
        }
    }

    @Override
    @CallSuper
    public void onDestroy() {
        super.onDestroy();
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
     * Tries to find a {@link Annotations.FindView}-annotated View from the {@link #mFoundViews} cache. Note that cache is emptied when this fragment dies.
     *
     * @param viewId The ID of the View being looked for
     * @return Either a View object; or {@code null} if not found, not parsed at all or fragment died already
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
