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
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * The basic implementation of the {@link DialogManager} API. Of course, you can modify this implementation
 * by extending the class, or even write your own version - but really there is no need as this covers 99% of
 * the use cases you might come across, and it's also covered by JUnit so you don't have to worry about tests.
 */
public class DialogManagerImpl implements DialogManager {

    private static final String TAG = DialogManagerImpl.class.getSimpleName();

    static final class DialogInfo implements Parcelable {

        public final int id;
        @Nullable
        public final Bundle config;
        public final boolean isFragment;

        public DialogInfo(final int id, @Nullable final Bundle config, final boolean isFragment) {
            this.id = id;
            this.config = config;
            this.isFragment = isFragment;
        }

        // <editor-fold desc="Equals & HashCode">
        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            DialogInfo info = (DialogInfo) o;
            return id == info.id && isFragment == info.isFragment &&
                    (config != null ? config.equals(info.config) : info.config == null);
        }

        @Override
        public int hashCode() {
            int result = id;
            result = 31 * result + (config != null ? config.hashCode() : 0);
            result = 31 * result + (isFragment ? 1 : 0);
            return result;
        }
        // </editor-fold>

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

    static final class State implements Parcelable {

        public int size;
        public Set<DialogInfo> configs;

        public State(@NonNull final Map<Integer, DialogInfo> dialogConfigs) {
            configs = new LinkedHashSet<>(dialogConfigs.values());
            size = configs.size();
        }

        // <editor-fold desc="Equals & HashCode">
        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            State state = (State) o;
            return size == state.size &&
                    (configs != null ? configs.equals(state.configs) : state.configs == null);
        }

        @Override
        public int hashCode() {
            int result = size;
            result = 31 * result + (configs != null ? configs.hashCode() : 0);
            return result;
        }
        // </editor-fold>

        // <editor-fold desc="Parcelable implementation">
        public State(Parcel in) {
            size = in.readInt();
            configs = new LinkedHashSet<>();
            for (int i = 0; i < size; i++) {
                final Parcelable parcelable = in.readParcelable(getClass().getClassLoader());
                if (parcelable instanceof DialogInfo) {
                    configs.add((DialogInfo) parcelable);
                }
            }
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(size);
            for (DialogInfo iDialogInfo : configs) {
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

    @Nullable
    @Override
    public DialogManagerCallback getCallback() {
        return mCallback;
    }

    @Override
    public void setListener(@Nullable final DialogManagerListener listener) {
        mListener = listener;
    }

    @Nullable
    @Override
    public DialogManagerListener getListener() {
        return mListener;
    }

    @Override
    public void showDialog(final int dialogId) {
        showDialog(dialogId, null);
    }

    @Override
    public void showDialog(final int dialogId, @Nullable final Bundle config) {
        final Dialog created = createDialog(dialogId, config);
        if (created == null) { return; }
        showDialogInternal(dialogId, created);
    }

    @Override
    public void showDialogFragment(final int dialogId) {
        showDialog(dialogId, null);
    }

    @Override
    public void showDialogFragment(final int dialogId, @Nullable final Bundle config) {
        final DialogFragment created = createDialogFragment(dialogId, config);
        if (created == null) { return; }
        showDialogFragmentInternal(dialogId, created);
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
            recreateFromConfigs(toRestoreFrom.configs, showNow);
        }
    }

    @Override
    public void recreateAll(final boolean showNow) {
        final Collection<DialogInfo> configs = new LinkedHashSet<>(mDialogConfigs.values());
        dismissAll();
        recreateFromConfigs(configs, showNow);
    }

    @Override
    public void unhideAll() {
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
        for (Dialog iDialog : mDialogInstances.values()) {
            if (iDialog.isShowing()) {
                iDialog.dismiss();
            }
        }
        for (DialogFragment iDialogFragment : mDialogFragmentInstances.values()) {
            if (iDialogFragment.getDialog() != null && iDialogFragment.getDialog().isShowing()) {
                iDialogFragment.dismissAllowingStateLoss();
            }
        }
        clearAllMappings();
    }

    @Override
    public void dispose() {
        dismissAll();
        mCallback = null;
        mListener = null;
        mFragmentManagerRef.clear();
    }

    /* Private helpers */

    @Nullable
    private Dialog createDialog(final int dialogId, @Nullable final Bundle config) {
        if (mCallback == null) {
            Log.w(TAG, "Can't show a dialog without the callback being set prior to this call");
            return null;
        }
        final Dialog created = mCallback.onCreateDialog(dialogId, config);
        mDialogConfigs.put(dialogId, new DialogInfo(dialogId, config, false));
        mDialogInstances.put(dialogId, created);
        return created;
    }

    @Nullable
    private DialogFragment createDialogFragment(final int dialogId, @Nullable final Bundle config) {
        if (mCallback == null) {
            Log.w(TAG, "Can't create a dialog fragment without the callback being set prior to this call");
            return null;
        }
        final DialogFragment created = mCallback.onCreateDialogFragment(dialogId, config);
        mDialogConfigs.put(dialogId, new DialogInfo(dialogId, config, true));
        mDialogFragmentInstances.put(dialogId, created);
        return created;
    }

    private void showDialogInternal(final int dialogId, @NonNull final Dialog instance) {
        instance.setOnShowListener(dialog -> {
            if (mListener != null) {
                mListener.onDialogShown(dialogId);
            }
        });
        instance.setOnDismissListener(dialog -> {
            if (mListener != null) {
                mListener.onDialogDismissed(dialogId);
                mDialogConfigs.remove(dialogId);
                mDialogInstances.remove(dialogId);
            }
        });
        instance.show();
    }

    private void showDialogFragmentInternal(final int dialogId, @NonNull final DialogFragment instance) {
        final FragmentManager manager = mFragmentManagerRef.get();
        if (manager == null) {
            Log.w(TAG, "Can't show a dialog fragment, FragmentManager instance expired");
            return;
        }
        instance.show(manager, getFragmentTag(dialogId));
        // DialogFragment#getDialog() is `null` prior to this call
        manager.executePendingTransactions();
        instance.getDialog().setOnShowListener(dialog -> {
            if (mListener != null) {
                mListener.onDialogShown(dialogId);
            }
        });
        instance.getDialog().setOnDismissListener(dialog -> {
            if (mListener != null) {
                mListener.onDialogDismissed(dialogId);
                mDialogConfigs.remove(dialogId);
                mDialogFragmentInstances.remove(dialogId);
            }
        });
    }

    private void recreateFromConfigs(@NonNull final Collection<DialogInfo> configs, boolean showNow) {
        clearAllMappings();
        for (DialogInfo iDialogInfo : configs) {
            if (!iDialogInfo.isFragment) {
                final Dialog created = createDialog(iDialogInfo.id, iDialogInfo.config);
                if (created == null) { return; }
                if (showNow) {
                    showDialogInternal(iDialogInfo.id, created);
                }
            } else {
                final DialogFragment created = createDialogFragment(iDialogInfo.id, iDialogInfo.config);
                if (created == null) { return; }
                if (showNow) {
                    showDialogFragmentInternal(iDialogInfo.id, created);
                }
            }
        }
    }

    private void clearAllMappings() {
        mDialogConfigs.clear();
        mDialogInstances.clear();
        mDialogFragmentInstances.clear();
    }

    @NonNull
    private String getFragmentTag(final int dialogId) {
        return TAG + "_ID:" + dialogId;
    }

}
