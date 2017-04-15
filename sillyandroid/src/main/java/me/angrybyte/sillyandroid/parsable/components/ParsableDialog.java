package me.angrybyte.sillyandroid.parsable.components;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.MenuRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import me.angrybyte.sillyandroid.components.EasyDialog;
import me.angrybyte.sillyandroid.parsable.AnnotationParser;

/**
 * An extension from {@link EasyDialog} with included {@link AnnotationParser} capabilities.
 */
@SuppressWarnings({ "WeakerAccess", "unused" })
public class ParsableDialog extends EasyDialog implements View.OnClickListener, View.OnLongClickListener {

    @LayoutRes
    @SuppressWarnings("unused")
    private int mLayoutId;

    @MenuRes
    @SuppressWarnings("unused")
    private int mMenuId;

    /**
     * @inheritDoc
     */
    public ParsableDialog(@NonNull final Context context) {
        super(context);
        initializeParsedProperties(context);
    }

    /**
     * @inheritDoc
     */
    public ParsableDialog(@NonNull final Context context, @StyleRes final int themeResId) {
        super(context, themeResId);
        initializeParsedProperties(context);
    }

    /**
     * @inheritDoc
     */
    protected ParsableDialog(@NonNull final Context context, final boolean cancelable, @Nullable final OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        initializeParsedProperties(context);
    }

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

    /**
     * Set the screen content from a layout resource.  The resource will be
     * inflated, adding all top-level views to the screen.
     *
     * @param layoutResID Resource ID to be inflated.
     */
    @Override
    public void setContentView(@LayoutRes final int layoutResID) {
        super.setContentView(layoutResID);
        AnnotationParser.parseFields(getContext(), this, this);
    }

    /**
     * Set the screen content to an explicit view.  This view is placed
     * directly into the screen's view hierarchy.  It can itself be a complex
     * view hierarchy.
     *
     * @param view The desired content to display.
     */
    @Override
    public void setContentView(@NonNull final View view) {
        super.setContentView(view);
        AnnotationParser.parseFields(getContext(), this, this);
    }

    /**
     * Set the screen content to an explicit view.  This view is placed
     * directly into the screen's view hierarchy.  It can itself be a complex
     * view hierarchy.
     *
     * @param view   The desired content to display.
     * @param params Layout parameters for the view.
     */
    @Override
    public void setContentView(@NonNull final View view, @Nullable final ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        AnnotationParser.parseFields(getContext(), this, this);
    }

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
    public void onClick(final View v) {}

    @Override
    public boolean onLongClick(final View v) {
        return false;
    }

    @LayoutRes
    protected final int getLayoutId() {
        return mLayoutId;
    }

    @MenuRes
    protected final int getMenuId() {
        return mMenuId;
    }

}
