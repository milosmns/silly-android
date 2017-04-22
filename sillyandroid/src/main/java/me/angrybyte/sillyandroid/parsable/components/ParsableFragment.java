package me.angrybyte.sillyandroid.parsable.components;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.MenuRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import me.angrybyte.sillyandroid.SillyAndroid;
import me.angrybyte.sillyandroid.components.EasyFragment;
import me.angrybyte.sillyandroid.parsable.AnnotationParser;
import me.angrybyte.sillyandroid.parsable.LayoutWrapper;

/**
 * An extension from {@link EasyFragment} with included {@link AnnotationParser} capabilities.
 */
@SuppressWarnings("unused")
public class ParsableFragment extends EasyFragment implements View.OnClickListener, View.OnLongClickListener {

    @LayoutRes
    @SuppressWarnings("unused")
    private int mLayoutId;

    @MenuRes
    @SuppressWarnings("unused")
    private int mMenuId;

    private boolean mParsedType;

    /**
     * @inheritDoc
     */
    @Override
    @CallSuper
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Context context = getContext();
        if (context != null) {
            AnnotationParser.parseType(context, this);
            mParsedType = true;
        }
    }

    /**
     * @inheritDoc
     */
    @Override
    @CallSuper
    public void onAttach(@NonNull final Context context) {
        super.onAttach(context);
        if (!mParsedType) {
            AnnotationParser.parseType(context, this);
            mParsedType = true;
        }
    }

    /**
     * @inheritDoc
     */
    @Nullable
    @Override
    @CallSuper
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        // it's barely possible to go in here, but it's Android, so.. check if not parsed by now
        final Context context = getContext() == null ? inflater.getContext() : getContext();
        if (!mParsedType && context != null) {
            AnnotationParser.parseType(context, this);
            mParsedType = true;
        }

        // inflate and parse views now
        if (getLayoutId() > 0 && context != null) {
            final View contentView = inflater.inflate(getLayoutId(), container, false);
            // using 'this' instead of a new wrapper won't work because the wrapper uses #getView(), which will at this point return null
            AnnotationParser.parseFields(context, this, new LayoutWrapper() {
                @Override
                public <ViewType extends View> ViewType findView(@IdRes final int viewId) {
                    return SillyAndroid.findViewById(contentView, viewId);
                }
            });
        }

        return null; // nothing worked, die.
    }

    /**
     * @inheritDoc
     */
    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (getMenuId() > 0) {
            inflater.inflate(getMenuId(), menu);
        }
    }

    /**
     * @inheritDoc
     */
    @Override
    public void onClick(final View v) {}

    /**
     * @inheritDoc
     */
    @Override
    public boolean onLongClick(final View v) {
        return false;
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
