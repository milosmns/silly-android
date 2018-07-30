Silly Android
=============
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Build Status](https://travis-ci.org/milosmns/silly-android.svg?branch=master)](https://travis-ci.org/milosmns/silly-android)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/1efe7dca11024c14811e6012f0d3ae0a)](https://www.codacy.com/app/milosmns/silly-android)
[![Code Climate](https://codeclimate.com/github/milosmns/silly-android/badges/gpa.svg)](https://codeclimate.com/github/milosmns/silly-android)
[![Download](https://api.bintray.com/packages/milosmns/maven/sillyandroid/images/download.svg)](https://bintray.com/milosmns/maven/sillyandroid/_latestVersion)

What is this?
-------------
_Silly Android_ is an Android (Java) library with various plugins for the core Android framework. In general, AppCompat offers a lot of help; but often enough, we need more. A lot of quirky Android APIs introduce the need for a bunch of utility classes, other times we just add a considerable amount of boilerplate code. Having said that, _Silly Android_ **does not** aim to remove all boilerplate code from your apps, or **fix all** of the silly API design decisions imposed by the default framework; people are tired of copying the same utilities and workarounds over and over, from one app they work on to the next one - and _Silly Android_'s core goal is to help with that.

In shortest terms, _Silly Android_ is a set of **most commonly** used workarounds, fixes and utilities, all of which should have been included in the core framework by default. When you find yourself asking questions like _"Why is this simple task so complicated to do?"_ or _"How was this not done by default?"_ - _Silly Android_ should be the help you need.

Examples
--------
_**Note**: Not all features are demonstrated in this section. Please check the 'Code organization' section for more info.
All samples internally reference the `SillyAndroid` class which does all of the heavy lifting - and, all APIs are available
through that class directly, but are integrated into `Easy` and `Parsable` components for ease of use._

Extra dimensions, colors and other values:
```xml
    ...
    <!-- SillyAndroid provides '@dimen/spacing_large', '@color/yellow', '@dimen/text_size_small', etc. -->
    <Button
        android:id="@+id/button_random_padding"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:paddingLeft="@dimen/spacing_large"
        android:paddingRight="@dimen/spacing_large"
        android:text="@string/label_random_padding"
        android:textColor="@color/yellow"
        android:textSize="@dimen/text_size_small" />
    ...
```
Typed `findView()` method (available in all _Easy_ components)
```java
    ...
    TextView displayView = findView(R.id.my_text_view); // no cast necessary!
    ...
```
Dialog management (see more in the [Wiki page](https://github.com/milosmns/silly-android/wiki/DialogManager-Wiki))
```java
// Create/dismiss/save/restore all managed by the DialogManager!
getDialogManager().showDialog(DIALOG_ID, configBundle);
```
Screen size calculations:
```java
    ...
    Point screenSize = SillyAndroid.UI.getScreenSize(this);
    if (screenSize.x < 1000 && screenSize.y < 1000) {
        // do something dynamically for a small screen
    }
    ...
```
Software keyboard helpers (available in all _Easy_ components):
```java
    ...
    @Override
    public void onKeyboardShown(int size) {
        Log.d(TAG, "Current keyboard size is: " + size);
    }
    
    @Override
    public void onKeyboardHidden() {
        Log.d(TAG, "Current keyboard is hidden!");
    }
    
    @Override
    public void onClick(@NonNull View view) {
        // this returns false if it fails to hide the keyboard:
        hideKeyboard(); 
    }
    ...
```
UI configuration checks:
```java
    ...
    @DeviceType int type = SillyAndroid.UI.getDeviceType(this);
    if (type == SillyAndroid.UI.TABLET_PORT || type == SillyAndroid.UI.PHONE_LAND) {
        // do something weird
    }
    ...
```
`EasyActivity` permission enhancement (also available in `EasyFragment`s):
```java
    ...
    // obviously you don't need to request this, but it's just a demo
    if (!hasPermission(Manifest.permission.ACCESS_WIFI_STATE)) {
        // no typed array, just declare them one after the other
        requestPermissions(REQ_CODE, Manifest.permission.ACCESS_WIFI_STATE); 
        return;
    }
    // do your permitted work here
    ...
```
`EasyFragment` permission enhancement (also available in `EasyActivit`ies):
```java
    @Override
    protected void onPermissionsResult(int reqestCode, @NonNull Set<String> granted, @NonNull Set<String> denied) {
        if (granted.contains(Manifest.permission.ACCESS_WIFI_STATE)) {
            // do your permitted work here
        }
    }
```
Easy Toast displays (available in all _Easy_ components):
```java
    ...
    toastLong(R.string.my_error);
    toastShort("Dynamic text here");
    ...
```
Easy conversions, checks and setters (available in all _Easy_ components):
```java
    ...
    // dp<->px conversions, network checks, and finally an easy padding setter
    int padding = SillyAndroid.convertDipsToPixels(getContext(), 20);
    boolean hasNetwork = SillyAndroid.isNetworkConnected(getContext());
    setPadding(mYourView, 0); // reset padding for some reason
    if (hasNetwork) {
        setPaddingVertical(mYourView, padding);
    } else {
        setPaddingHorizontal(mYourView, padding);
    }
    ...
```
Thread checking:
```java
    ...
    if (SillyAndroid.isThisMainThread()) {
        throw new IllegalStateException("Don't run this on the UI thread");
    }
    ...
```
Dynamic, responsive, and contrasted View coloring:
```java
    /*
     * Prepare the special button coloring to demonstrate the Coloring class (you would have something like this happen generically in real apps):
     *
     * - IDLE state colors -
     *     Background: GRAY
     *     Text: Contrast to [IDLE.Background]
     *     Icon: Contrast to [IDLE.Background]
     *
     * - PRESSED state colors -
     *     Background: #FFCC00 (bright yellow)
     *     Text: Contrast to [PRESSED.Background]
     *     Icon: Contrast to [PRESSED.Background]
     *
     * Android does not recolor to contrast colors when pressed, so we're doing that manually below.
     */
    int idleBackgroundColor = Color.GRAY; // button background when not pressed
    int idleContentColor = Coloring.contrastColor(idleBackgroundColor); // text and icon color when not pressed (need them to be always legible)
    int pressedBackgroundColor = 0xFFFFCC00; // button background highlight color when pressed
    Drawable originalDrawable = ContextCompat.getDrawable(this, android.R.drawable.star_big_on); // load a random icon from android
    StateListDrawable statefulDrawable = Coloring.createContrastStateDrawable(this, idleContentColor, pressedBackgroundColor, true, originalDrawable);
    ColorStateList statefulTextColors = Coloring.createContrastTextColors(idleContentColor, pressedBackgroundColor);
    Rect originalBounds = mPaddingButton.getBackground().copyBounds(); // copy original drawable's bounds so that the ripple is bordered
    int cornerRoundness = SillyAndroid.convertDipsToPixels(this, 4);
    Drawable backgroundDrawable = Coloring.createResponsiveDrawable(this, idleBackgroundColor, pressedBackgroundColor, idleBackgroundColor, true, cornerRoundness, originalBounds);
    // set the background color, pre-colored compound icon and text colors
    setBackgroundCompat(mPaddingButton, backgroundDrawable);
    mPaddingButton.setCompoundDrawablesWithIntrinsicBounds(statefulDrawable, null, null, null);
    mPaddingButton.setTextColor(statefulTextColors);
```
Automatic layout and menu setup for _Parsable_ components:
```java
    @Menu(R.menu.my_menu)
    @Layout(R.layout.my_layout)
    class MainActivity extends ParsableActivity {
        // your activity code here
    }
```
Automatic View finding and injections for _Parsable_ components:
```java
    @Layout(R.layout.my_layout)
    class MainActivity extends ParsableActivity {
        @Clickable // makes the View respond to clicks via this#onClick()
        @FindView(R.id.my_clickable_button)
        private Button mClickableButton;
    
        @LongClickable // makes the View respond to clicks via this#onLongClick()
        @FindView(R.id.my_long_clickable_button)
        private Button mLongClickableButton;
    }
```
[Google Guava-like verification](https://github.com/google/guava/wiki/PreconditionsExplained) with `Preconditions` class:
```java
    void saveResource(@NonNull final String resource) {
        mResource = Preconditions.checkNotNull(resource); // crashes if resource is null
    }
```

Setup
-----
- Gradle build - `jCenter` and `mavenCentral` are both supported
- **AppCompat**, minimum version `25.0.2` - _Silly Android_ heavily relies on the features provided by this library. Note that including other versions might work too, but don't report issues if it does not. You've been warned! 

To include _Silly Android_ in your app, add the following line to your app's `build.gradle`'s `dependecies` block.
```gradle
    // look for the latest version on top of this file
    compile "me.angrybyte.sillyandroid:sillyandroid:VERSION_NAME" 
```

Code organization
-----------------

- The backbone of the library is here: `me.angrybyte.sillyandroid.SillyAndroid`. It contains various utilities to help around with accessing APIs more easily.
- Check out the `me.angrybyte.sillyandroid.extras.Coloring` class for various color and drawable utilities.
- Android components such as Activity, Fragment, Dialog, View and ViewGroup have been enhanced to include features from the _Silly Android_ internally, you can check that out in the `me.angrybyte.sillyandroid.components` package. They are now called `EasyActivity`, `EasyFragment` and so on.
- Even more enhancements are added to the _Easy_ component set using _Silly Android_'s annotations, such as View injections, typed `T findView(int id)` methods, automated click and long-click handling, and more. For information about that, see package `me.angrybyte.sillyandroid.parsable.components`.
- Dialog management components are in the `me.angrybyte.sillyandroid.dialogs` package.
- For a coded demo of the fully enhanced Activity class, go to `me.angrybyte.sillyandroid.demo.MainActivity`.
- Check out the colors, UI sizes and text sizes added in `sillyandroid/src/main/res`.

Contributions and how we determine what to include
--------------------------------------------------
All interested parties need to create a new [Feature request](https://github.com/milosmns/silly-android/issues/new) so that everyone involved in active development can discuss the feature or the workaround described. Any pull request not referencing a _Feature request_ will be automatically denied. You need to have actual reasons backed up by real-world facts in order to include stuff in the library - otherwise, we would have a huge library with lots of useless components that would need to be removed with ProGuard (and we don't want to be another AppCompat).

Furthermore, we are trying to test everything that's not trivial and keep the code as clean as humanly possible; thus, any pull requests that fail the CI code quality check or fail to properly pass the tests will also be denied. If pull requests pass every check (and don't worry, it's not impossible to pass), one of the admins could then merge the changes to the `release` branch - this triggers a CI build with device/emulator tests. If all goes ok, the library is automatically deployed to `jCenter` and `MavenCentral`.

Further support
---------------
In case of emergency errors, please [create an issue](https://github.com/milosmns/silly-android/issues/new).
We missed something really useful? Have an idea for a cleaner API? [Fork this project](https://github.com/milosmns/silly-android/fork) and submit a pull request through GitHub. Keep in mind that you need a _Feature request_ first with a finalized discussion (see the Contributions section).
Some more help may be found here:
- StackOverflow [here](http://stackoverflow.com/questions/tagged/silly-android)
- [On my blog](http://angrybyte.me)
