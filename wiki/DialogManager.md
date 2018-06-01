## DialogManager Wiki

### The "WHY"

To open a system (or appcompat) dialog on Android, developers need to create a dialog builder, then configure it for their specific need, then create a dialog instance from it, and finally call the `show()` method on the dialog instance to present it to the user. If the activity the dialog lives in is configured to allow configuration changes (such as rotation), you might be getting into the risky zone of having to deal with state saving and restoring; you might also need to worry about avoiding memory leaks (i.e. keeping the reference to the activity inside of a dialog that "survives" the rotation), also dismissing the dialog and showing it again after rotation has finished; and you might want to persist the initial configuration that the dialog had at creation time in order to re-create it identically to the first time at some point in the future. 

All of this can become really annoying - especially when you have multiple dialogs shown - some would even say that the whole Dialog API is a bit... silly. `DialogManager` aims to solve these issues and relieve you of having to write the boilerplate code yourself.

### `DialogManager`

`DialogManager` is a simple, robust dialog management API designed around the unfortunate code flow of showing and hiding dialogs (and `DialogFragment`s) on Android. It allows the user to show, hide, dismiss, and persist dialogs by using a combination of static dialog IDs and configuration Bundles, in a way that doesn't affect the surrounding code and also helps keep the code very low maintenance.

Usage rules are:
1. You are responsible for creating dialog instances by implementing the `DialogManagerCallback` interface, but `DialogManager` will take care of showing them. The main entry point for you is the `DialogManager#show()` method. You have to be prepared that `DialogManager` can use your callback at any time after calling `show()` for the first time (due to configuration changes, etc.)
2. All dialog instances (and their visibility) are managed by the `DialogManager`. Never keep the dialog instance in your activity/fragment, always interact with it through the `DialogManager`
3. You are responsible for choosing static, unique IDs for each dialog you manage through the `DialogManager`
4. All configuration options for the dialog (i.e. data, style, etc.) should be sent in a Bundle through the `DialogManager#show()` call. This Bundle will be persisted by the `DialogManager` for subsequent creation steps if needed, and it also gets passed as a method argument to the `DialogManagerCallback` you implement
5. On configuration changes such as rotation, you should use the save/restore `Parcelable` methods on the `DialogManager` to persist the manager's state within the activity or fragment

Many of these steps have been taken care of already inside of `EasyActivty` and `EasyFragment` from the *SillyAndroid* `components` package - obviously apart from creating actual dialog instances - you'll have to override the callback method implementations to provide your own dialog instances. Check out the Demo app from the repository for more details.

A code example of using the manager in an app would be:  
*(1) Assuming you have instantiated the dialog manager in your activity or fragment*  
*(2) Assuming you have the `DialogManagerCallback` interface implemented on the same class and set it to the `DialogManager`*

Defining a static, unique dialog ID:
```java
private static final int DIALOG_EXIT_PROMPT = 0x000001; // any number here
```
Showing a dialog with a configuration Bundle:
```java
Bundle config = new Bundle(); 
config.putString("USER_DISPLAY_NAME", getUser().getDisplayName());
config.putInt("DIALOG_THEME", R.style.ThemeDialog_CustomizedExit); 
getDialogManager().showDialog(DIALOG_EXIT_PROMPT, config); // <-- show() call
```
Showing a simple dialog without any configuration:
```java
getDialogManager().showDialog(DIALOG_EXIT_PROMPT); // <-- show() call
```
A sample implementation of the `DialogManagerCallback` interface:
```java
@Nullable
@Override
public Dialog onCreateDialog(int dialogId, @Nullable Bundle config) { 
    switch (dialogId) { 
        case DIALOG_EXIT_PROMPT: 
            return createExitDialog(config); // <-- create instance
        default: 
            return null; 
    }
}
```

Generally, all of these features are available also for `DialogFragment`s. Apart from the basic show/hide/dismiss/persist API, there are also ways to listen for dialogs being shown or dismissed, using the `DialogManagerListener`.  
To recap: the main idea is to let the manager handle creation time, re-creation and state saving for dialogs, you just show and dismiss dialogs when it's needed manually.

## API JavaDoc

**`interface DialogManagerCallback`**

The main creation callback. The manager invokes this callback when a new dialog instance is needed, typically after `#showDialog(int)` or `#showDialogFragment(int)` were called on the manager.

**`@Nullable Dialog onCreateDialog(final int dialogId, @Nullable final Bundle config)`**

Invoked when a new `Dialog` instance is needed, either after a `show()` call, or after state restored.

 * **Parameters:**
   * `dialogId` — A static, unique identifier of the dialog
   * `config` — A configuration bundle, contains all information needed for the dialog to be created
 * **Returns:** A dialog instance if the ID is recognized, `null` if creator knows no such dialog

**`@Nullable DialogFragment onCreateDialogFragment(final int dialogId, @Nullable final Bundle config)`**

Invoked when a new `DialogFragment` instance is needed, either after a `show()` call, or after state restored.

 * **Parameters:**
   * `dialogId` — A static, unique identifier of the dialog fragment
   * `config` — A configuration bundle, contains all information needed for the dialog fragment to be created
 * **Returns:** A dialog fragment instance if the ID is recognized, `null` if creator knows no such dialog fragment

**`interface DialogManagerListener`**

A basic listener to get information about a dialog that has just been shown or dismissed.

**`void onDialogShown(final int dialogId)`**

Invoked by the manager to notify that a dialog was just shown to the user.

 * **Parameters:** `dialogId` — Which dialog was shown

**`void onDialogDismissed(final int dialogId)`**

Invoked by the manager to notify that a dialog was just dismissed.

 * **Parameters:** `dialogId` — Which dialog was dismissed

**`void setCallback(@Nullable final DialogManagerCallback callback)`**

Sets a permanent creator callback for this manager instance.

 * **Parameters:** `callback` — A new instance that is to be used to create dialogs; set to `null` to remove

**`@Nullable DialogManagerCallback getCallback()`**

Gets the instance that was previously set by `#setCallback(DialogManagerCallback)`.

 * **Returns:** Either the callback instance, or `null` if none was set

**`void setListener(@Nullable final DialogManagerListener listener)`**

Sets a permanent dialog listener for this manager instance.

 * **Parameters:** `listener` — A new instance that is to be used to listen for dialog events; set to `null` to remove

**`@Nullable DialogManagerListener getListener()`**

Gets the instance that was previously set by `#setListener(DialogManagerListener)`.

 * **Returns:** Either the listener instance, or `null` if none was set

**`void showDialog(final int dialogId)`**

Overload of `#showDialog(int, Bundle)`.

**`void showDialog(final int dialogId, @Nullable final Bundle config)`**

Creates and then shows the new dialog associated with the given dialog ID. Creation step is delegated to the set `DialogManagerCallback` instance.

 * **Parameters:**
   * `dialogId` — A static, unique identifier of the dialog
   * `config` — A configuration bundle, contains all information needed for the dialog to be created

**`void showDialogFragment(final int dialogId)`**

Overload of `#showDialogFragment(int, Bundle)`.

**`void showDialogFragment(final int dialogId, @Nullable final Bundle config)`**

Creates and then shows the new dialog fragment associated with the given dialog fragment ID. Creation step is delegated to the set `DialogManagerCallback` instance.

 * **Parameters:**
   * `dialogId` — A static, unique identifier of the dialog fragment
   * `config` — A configuration bundle, contains all information needed for the dialog fragment to be created

**`boolean isDialogShowing(final int dialogId)`**

Checks if the dialog instance associated with the given dialog ID is `Dialog#isShowing()`.

 * **Parameters:** `dialogId` — A static, unique identifier of the dialog
 * **Returns:** `True` if there is such dialog and it's showing, `false` otherwise

**`void dismissDialog(final int dialogId)`**

Dismisses a dialog, if there is such a dialog instance currently shown to the user.

 * **Parameters:** `dialogId` — Which dialog to dismiss

**`@NonNull Parcelable saveState()`**

Saves a complete configuration state of the dialog manager. Instances are not saved, only their initial configuration.

 * **Returns:** A `Parcelable` you can store in the saved instance state bundle, for example

**`void restoreState(@Nullable final Parcelable state, final boolean showNow)`**

Restores the dialogs from the given `Parcelable` (usually obtained through calling `#saveState()`). Note that calling this method invokes the `DialogManagerCallback`, so you have to set the callback instance prior to this call.

 * **Parameters:**
   * `state` — The state to restore from
   * `showNow` — Whether to show the dialogs immediately, or wait for a manual call to `#unhideAll()`

**`void recreateAll(final boolean showNow)`**

Dismisses, and then recreates all dialog instances from the currently stored configuration.

 * **Parameters:** `showNow` — Whether to show the dialogs immediately, or wait for a manual call to `#unhideAll()`

**`void unhideAll()`**

Un-hides all currently invisible dialogs, i.e. all that are not `Dialog#isShowing()`.

**`void hideAll()`**

Hides all currently visible dialogs, i.e. all that are `Dialog#isShowing()`.

**`void dismissAll()`**

Dismisses all currently visible dialogs.

**`void dispose()`**

Disposes of all dialogs, listeners and callbacks assigned to this manager. Basically, a "free memory" method, should you ever need it.