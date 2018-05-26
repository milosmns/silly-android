package me.angrybyte.sillyandroid.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class DialogManagerImpl implements DialogManager {

    private static final String TAG = DialogManagerImpl.class.getSimpleName();

    private static final class DialogInfo implements Parcelable {

        public final int id;
        @Nullable
        public final Bundle config;
        public final boolean isFragment;

        public DialogInfo(final int id, @Nullable final Bundle config, final boolean isFragment) {
            this.id = id;
            this.config = config;
            this.isFragment = isFragment;
        }

        // <editor-fold desc="Parcelable implementation">
        public DialogInfo(Parcel in) {
            id = in.readInt();
            config = in.readBundle(getClass().getClassLoader());
            isFragment = in.readByte() != 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(id);
            dest.writeBundle(config);
            dest.writeByte((byte) (isFragment ? 1 : 0));
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<DialogInfo> CREATOR = new Creator<DialogInfo>() {
            @Override
            public DialogInfo createFromParcel(Parcel in) {
                return new DialogInfo(in);
            }

            @Override
            public DialogInfo[] newArray(int size) {
                return new DialogInfo[size];
            }
        };
        // </editor-fold>
    }

    private static final class State implements Parcelable {

        public int size;
        public Set<DialogInfo> dialogs;

        public State(Map<Integer, DialogInfo> dialogConfigs) {
            dialogs = new LinkedHashSet<>(dialogConfigs.values());
            size = dialogs.size();
        }

        // <editor-fold desc="Parcelable implementation">
        public State(Parcel in) {
            size = in.readInt();
            for (int i = 0; i < size; i++) {
                final Parcelable parcelable = in.readParcelable(getClass().getClassLoader());
                if (parcelable instanceof DialogInfo) {
                    dialogs.add((DialogInfo) parcelable);
                }
            }
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(size);
            for (DialogInfo iDialogInfo : dialogs) {
                dest.writeParcelable(iDialogInfo, 0);
            }
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<State> CREATOR = new Creator<State>() {
            @Override
            public State createFromParcel(Parcel in) {
                return new State(in);
            }

            @Override
            public State[] newArray(int size) {
                return new State[size];
            }
        };
        // </editor-fold>
    }

    @Nullable
    private DialogManagerCallback mCallback;
    @Nullable
    private DialogManagerListener mListener;
    @NonNull
    private final WeakReference<FragmentManager> mFragmentManagerRef;
    private Map<Integer, DialogInfo> mDialogConfigs = new LinkedHashMap<>();
    private Map<Integer, Dialog> mDialogInstances = new LinkedHashMap<>();
    private Map<Integer, DialogFragment> mDialogFragmentInstances = new LinkedHashMap<>();

    public DialogManagerImpl(@NonNull final FragmentManager fragmentManager) {
        mFragmentManagerRef = new WeakReference<>(fragmentManager);
    }

    @Override
    public void setCallback(@Nullable final DialogManagerCallback callback) {
        mCallback = callback;
    }

    @Override
    public void setListener(@Nullable final DialogManagerListener listener) {
        mListener = listener;
    }

    @Override
    public void showDialog(final int dialogId) {
        showDialog(dialogId, null);
    }

    @Override
    public void showDialog(final int dialogId, @Nullable final Bundle config) {
        final Dialog created = createDialog(dialogId, config);
        if (created == null) {
            Log.w(TAG, "Can't show a dialog, callback returned `null` for ID " + dialogId);
            return;
        }
        mDialogConfigs.put(dialogId, new DialogInfo(dialogId, config, false));
        created.setOnShowListener(dialog -> {
            if (mListener != null) {
                mListener.onDialogShown(dialogId);
            }
        });
        created.setOnDismissListener(dialog -> {
            if (mListener != null) {
                mListener.onDialogDismissed(dialogId);
            }
            // TODO take care of instance and config removals
        });
        created.show();
    }

    @Override
    public void showDialogFragment(final int dialogId) {
        showDialog(dialogId, null);
    }

    @Override
    public void showDialogFragment(final int dialogId, @Nullable final Bundle config) {
        final DialogFragment created = createDialogFragment(dialogId, config);
        if (created == null) {
            Log.w(TAG, "Can't create a dialog fragment, callback returned `null` for ID " + dialogId);
            return;
        }
        mDialogConfigs.put(dialogId, new DialogInfo(dialogId, config, true));
        final FragmentManager manager = mFragmentManagerRef.get();
        if (manager == null) {
            Log.w(TAG, "Can't show a dialog fragment, FragmentManager instance expired");
            return;
        }
        created.show(manager, getFragmentTag(dialogId));
        // DialogFragment#getDialog() is null prior to this call
        manager.executePendingTransactions();
        created.getDialog().setOnShowListener(dialog -> {
            if (mListener != null) {
                mListener.onDialogShown(dialogId);
            }
        });
        created.getDialog().setOnDismissListener(dialog -> {
            if (mListener != null) {
                mListener.onDialogDismissed(dialogId);
                // TODO take care of instance and config removals
            }
        });
    }

    @Override
    public boolean isDialogShowing(final int dialogId) {
        final Dialog dialog = mDialogInstances.get(dialogId);
        if (dialog != null) {
            return dialog.isShowing();
        }
        final DialogFragment dialogFragment = mDialogFragmentInstances.get(dialogId);
        // noinspection SimplifiableIfStatement
        if (dialogFragment != null && dialogFragment.getDialog() != null) {
            return dialogFragment.getDialog().isShowing();
        }
        return false;
    }

    @Override
    public void dismissDialog(final int dialogId) {
        final Dialog dialog = mDialogInstances.get(dialogId);
        if (dialog != null) {
            dialog.dismiss();
            mDialogInstances.remove(dialogId);
            mDialogConfigs.remove(dialogId);
            return;
        }
        final DialogFragment dialogFragment = mDialogFragmentInstances.get(dialogId);
        if (dialogFragment != null) {
            dialogFragment.dismiss();
            mDialogFragmentInstances.remove(dialogId);
            mDialogConfigs.remove(dialogId);
        }
    }

    @NonNull
    @Override
    public Parcelable saveState() {
        return new State(mDialogConfigs);
    }

    @Override
    public void restoreState(@Nullable final Parcelable state, final boolean showNow) {
        if (state instanceof State) {
            final State toRestoreFrom = (State) state;
            mDialogConfigs.clear();
            mDialogInstances.clear();
            mDialogFragmentInstances.clear();
            for (DialogInfo iDialogInfo : toRestoreFrom.dialogs) {
                mDialogConfigs.put(iDialogInfo.id, iDialogInfo);
                if (showNow) {
                    if (iDialogInfo.isFragment && mCallback != null) {
                        createDialogFragment(iDialogInfo.id, iDialogInfo.config);
                    } else {
                        createDialog(iDialogInfo.id, iDialogInfo.config);
                    }
                }
            }
        }
    }

    @Override
    public void recreateAll() {
        // TODO implement
    }

    @Override
    public void showAll() {
        for (Dialog iDialog : mDialogInstances.values()) {
            if (!iDialog.isShowing()) {
                iDialog.show();
            }
        }
        for (DialogFragment iDialogFragment : mDialogFragmentInstances.values()) {
            if (iDialogFragment.getDialog() != null && !iDialogFragment.getDialog().isShowing()) {
                iDialogFragment.getDialog().show();
            }
        }
    }

    @Override
    public void hideAll() {
        for (Dialog iDialog : mDialogInstances.values()) {
            if (iDialog.isShowing()) {
                iDialog.hide();
            }
        }
        for (DialogFragment iDialogFragment : mDialogFragmentInstances.values()) {
            if (iDialogFragment.getDialog() != null && iDialogFragment.getDialog().isShowing()) {
                iDialogFragment.getDialog().hide();
            }
        }
    }

    @Override
    public void dismissAll() {
        // TODO implement
    }

    /* Private helpers */

    @NonNull
    private String getFragmentTag(final int dialogId) {
        return TAG + "_ID:" + dialogId;
    }

    @Nullable
    private Dialog createDialog(final int dialogId, @Nullable final Bundle config) {
        // TODO take care of listeners
        if (mCallback == null) {
            Log.w(TAG, "Can't show a dialog without the callback being set prior to this call");
            return null;
        }
        final Dialog created = mCallback.onCreateDialog(dialogId, config);
        mDialogInstances.put(dialogId, created);
        return created;
    }

    @Nullable
    private DialogFragment createDialogFragment(final int dialogId, @Nullable final Bundle config) {
        // TODO take care of listeners
        if (mCallback == null) {
            Log.w(TAG, "Can't create a dialog fragment without the callback being set prior to this call");
            return null;
        }
        final DialogFragment created = mCallback.onCreateDialogFragment(dialogId, config);
        mDialogFragmentInstances.put(dialogId, created);
        return created;
    }

}
