package me.angrybyte.sillyandroid.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;

/**
 * A simple, robust API designed around the unfortunate code flow of showing and hiding Dialogs on Android.
 * Allows the user to show, hide, dismiss, and persist dialogs by using a combination of static dialog IDs
 * and configuration Bundles, in a way that doesn't affect the surrounding code and is very low maintenance.
 * <p>
 * The main usage rules are:
 * <ol>
 * <li>Your dialogs' instances and their visibilities are managed by the {@code DialogManager}</li>
 * <li>You are responsible for setting static IDs for each dialog you manage through the manager</li>
 * <li>You are responsible for creating dialog instances by implementing the {@link DialogManagerCallback} interface</li>
 * <li>You have to be prepared at any time that {@code DialogManager} will use your callback (due to configuration changes, etc.)</li>
 * <li>All configuration for the dialog (i.e. data, style, etc.) is sent inside of a {@link Bundle}</li>
 * <li>On configuration change, you should use the save/restore {@link Parcelable} methods on the manager</li>
 * </ol>
 * <p>
 * An example of using the manager in an app would be:<br>
 * <i>(1) Assuming you have instantiated the dialog manager in your activity or fragment</i><br>
 * <i>(2) Assuming you have the {@link DialogManagerCallback} interface implemented on the same class</i>
 * <p>
 * <pre>
 *     private static final int DIALOG_EXIT_PROMPT = 0x000001; // any number here
 *
 *     ...
 *
 *     // for example after a button is clicked:
 *     Bundle config = new Bundle();
 *     config.putString("USER_DISPLAY_NAME", getUser().getDisplayName());
 *     config.putInt("DIALOG_THEME", R.style.ThemeDialog_CustomizedExit);
 *     getDialogManager().showDialog(DIALOG_EXIT_PROMPT, config);
 *
 *     ...
 *
 *     // or a simple, no-config show:
 *     getDialogManager().showDialog(DIALOG_EXIT_PROMPT);
 *
 *     ...
 *
 *     // this might get called at any time, always be prepared to create a new instance
 *    &nbsp;@Override
 *     public Dialog onCreateDialog(int dialogId, Bundle config) {
 *         switch (dialogId) {
 *             case DIALOG_EXIT_PROMPT:
 *                 return createExitDialog(config);
 *             default:
 *                 return null;
 *         }
 *     }
 * </pre>
 * Apart from this, there are options to listen for dialogs being shown or dismissed ({@link DialogManagerListener}),
 * and also the same options available for {@link DialogFragment}s through the same callback interfaces.
 * <p>
 * The whole idea is to let the manager handle recreation and state saving for dialogs, you just show and
 * dismiss dialogs when it's needed manually, and wait for creation callbacks.
 */
public interface DialogManager {

    /**
     * The main creation callback. The manager invokes this callback when a new dialog instance is needed,
     * typically after {@link #showDialog(int)} or {@link #showDialogFragment(int)} were called on the manager.
     */
    interface DialogManagerCallback {

        /**
         * Invoked when a new {@link Dialog} instance is needed, either after a 'show' call, or after state restored.
         *
         * @param dialogId A static, unique identifier of the dialog
         * @param config   A configuration bundle, contains all information needed for the dialog to be created
         * @return A dialog instance if the ID is recognized, {@code null} if creator knows no such dialog
         */
        @Nullable
        Dialog onCreateDialog(final int dialogId, @Nullable final Bundle config);

        /**
         * Invoked when a new {@link DialogFragment} instance is needed, either after a 'show' call, or after state restored.
         *
         * @param dialogId A static, unique identifier of the dialog fragment
         * @param config   A configuration bundle, contains all information needed for the dialog fragment to be created
         * @return A dialog fragment instance if the ID is recognized, {@code null} if creator knows no such dialog fragment
         */
        @Nullable
        DialogFragment onCreateDialogFragment(final int dialogId, @Nullable final Bundle config);
    }

    /**
     * A basic listener to get information about a dialog that has just been shown or dismissed.
     */
    interface DialogManagerListener {

        /**
         * Invoked by the manager to notify that a dialog was just shown to the user.
         *
         * @param dialogId Which dialog was shown
         */
        void onDialogShown(final int dialogId);

        /**
         * Invoked by the manager to notify that a dialog was just dismissed.
         *
         * @param dialogId Which dialog was dismissed
         */
        void onDialogDismissed(final int dialogId);
    }

    /**
     * Sets a permanent creator callback for this manager instance.
     *
     * @param callback A new instance that is to be used to create dialogs; set to {@code null} to remove
     */
    void setCallback(@Nullable final DialogManagerCallback callback);

    /**
     * Gets the instance that was previously set by {@link #setCallback(DialogManagerCallback)}.
     *
     * @return Either the callback instance, or {@code null} if none was set
     */
    @Nullable
    DialogManagerCallback getCallback();

    /**
     * Sets a permanent dialog listener for this manager instance.
     *
     * @param listener A new instance that is to be used to listen for dialog events; set to {@code null} to remove
     */
    void setListener(@Nullable final DialogManagerListener listener);

    /**
     * Gets the instance that was previously set by {@link #setListener(DialogManagerListener)}.
     *
     * @return Either the listener instance, or {@code null} if none was set
     */
    @Nullable
    DialogManagerListener getListener();

    /**
     * Overload of {@link #showDialog(int, Bundle)}.
     */
    void showDialog(final int dialogId);

    /**
     * Creates and then shows the new dialog associated with the given dialog ID. Creation step is delegated
     * to the set {@link DialogManagerCallback} instance.
     *
     * @param dialogId A static, unique identifier of the dialog
     * @param config   A configuration bundle, contains all information needed for the dialog to be created
     */
    void showDialog(final int dialogId, @Nullable final Bundle config);

    /**
     * Overload of {@link #showDialogFragment(int, Bundle)}.
     */
    void showDialogFragment(final int dialogId);

    /**
     * Creates and then shows the new dialog fragment associated with the given dialog fragment ID.
     * Creation step is delegated to the set {@link DialogManagerCallback} instance.
     *
     * @param dialogId A static, unique identifier of the dialog fragment
     * @param config   A configuration bundle, contains all information needed for the dialog fragment to be created
     */
    void showDialogFragment(final int dialogId, @Nullable final Bundle config);

    /**
     * Checks if the dialog instance associated with the given dialog ID is {@link Dialog#isShowing()}.
     *
     * @param dialogId A static, unique identifier of the dialog
     * @return {@code True} if there is such dialog and it's showing, {@code false} otherwise
     */
    boolean isDialogShowing(final int dialogId);

    /**
     * Dismisses a dialog, if there is such a dialog instance currently shown to the user.
     *
     * @param dialogId Which dialog to dismiss
     */
    void dismissDialog(final int dialogId);

    /**
     * Saves a complete configuration state of the dialog manager.
     * Instances are not saved, only their initial configuration.
     *
     * @return A {@link Parcelable} you can store in the saved instance state bundle, for example
     */
    @NonNull
    Parcelable saveState();

    /**
     * Restores the dialogs from the given {@link Parcelable} (usually obtained through calling {@link #saveState()}).
     * Note that calling this method invokes the {@link DialogManagerCallback}, so you have to set the callback
     * instance prior to this call.
     *
     * @param state   The state to restore from
     * @param showNow Whether to show the dialogs immediately, or wait for a manual call to {@link #unhideAll()}
     */
    void restoreState(@Nullable final Parcelable state, final boolean showNow);

    /**
     * Dismisses, and then recreates all dialog instances from the currently stored configuration.
     *
     * @param showNow Whether to show the dialogs immediately, or wait for a manual call to {@link #unhideAll()}
     */
    void recreateAll(final boolean showNow);

    /**
     * Un-hides all currently invisible dialogs, i.e. all that are not {@link Dialog#isShowing()}.
     */
    void unhideAll();

    /**
     * Hides all currently visible dialogs, i.e. all that are {@link Dialog#isShowing()}.
     */
    void hideAll();

    /**
     * Dismisses all currently visible dialogs.
     */
    void dismissAll();

    /**
     * Disposes of all dialogs, listeners and callbacks assigned to this manager. Basically, a "free memory" method,
     * should you ever need it.
     */
    void dispose();

}