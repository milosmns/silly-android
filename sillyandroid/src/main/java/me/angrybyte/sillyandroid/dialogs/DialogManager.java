package me.angrybyte.sillyandroid.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;

public interface DialogManager {

    interface DialogManagerCallback {

        @Nullable
        Dialog onCreateDialog(final int dialogId, @Nullable final Bundle config);

        @Nullable
        DialogFragment onCreateDialogFragment(final int dialogId, @Nullable final Bundle config);
    }

    interface DialogManagerListener {

        void onDialogShown(final int dialogId);

        void onDialogDismissed(final int dialogId);
    }

    void setCallback(@Nullable final DialogManagerCallback callback);

    void setListener(@Nullable final DialogManagerListener listener);

    void showDialog(final int dialogId);

    void showDialog(final int dialogId, @Nullable final Bundle config);

    void showDialogFragment(final int dialogId);

    void showDialogFragment(final int dialogId, @Nullable final Bundle config);

    boolean isDialogShowing(final int dialogId);

    void dismissDialog(final int dialogId);

    @NonNull
    Parcelable saveState();

    void restoreState(@Nullable final Parcelable state, final boolean showNow);

    void recreateAll(final boolean showNow);

    void unhideAll();

    void hideAll();

    void dismissAll();

}