package me.angrybyte.sillyandroid.extras;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.CallSuper;
import android.support.v7.app.AppCompatActivity;

import java.lang.ref.WeakReference;

/**
 * A simple and particularly <i>hacky</i> solution to properly sequencing Activity Lifecycle which has no guaranteed invocation of {@link Activity#onStop()} and
 * {@link Activity#onDestroy()} methods. By extending this, your activity should guarantee invocation of these methods on your <b>old</b> activity instance
 * <b>before</b> invoking the {@link Activity#onCreate(Bundle)} or {@link Activity#onCreate(Bundle, PersistableBundle)} on your <b>new</b> Activity instance.
 * <p>
 * The main difference in usage to the "old" way is that you will need to implement two new methods (instead of your normal {@link Activity#onStop()} and
 * {@link Activity#onDestroy()} - called {@link BlockingLifecycleActivity#onBlockingStop()} and {@link BlockingLifecycleActivity#onBlockingDestroy()}.
 * <p>
 * <i>Note that you still need to call "{@code super}" on each of the original activity stop and destroy lifecycle methods if you are overriding them.</i><br>
 * <i>Note that {@link BlockingLifecycleActivity#onBlockingStop()} and {@link BlockingLifecycleActivity#onBlockingDestroy()} events will not get invoked twice,
 * but only the first time "{@code stop}" or "{@code destroy}" happens.</i><br>
 * <i>Note that using this class will have side effects when used with {@code launchMode="singleTop"}, so the <u>hack has been disabled</u> in that case.</i>
 */
public abstract class BlockingLifecycleActivity extends AppCompatActivity {

    private boolean mIsStopInvoked;
    private boolean mIsDestroyInvoked;
    private static WeakReference<BlockingLifecycleActivity> lastInstance;

    /**
     * Invoked immediately after {@link Activity#onStop()} event happens on the <b>current activity instance</b>.
     */
    protected void onBlockingStop() {
        // implement this instead of onStop()
    }

    /**
     * Invoked immediately after {@link Activity#onDestroy()} event happens on the <b>current activity instance</b>.
     */
    protected void onBlockingDestroy() {
        // implement this instead of onStop()
    }

    /**
     * Checks if {@link #onBlockingStop()} has been invoked on this instance.
     *
     * @return {@code True} if "stop" was invoked by now, {@code false} if not
     */
    protected final boolean isStopInvoked() {
        return mIsStopInvoked;
    }

    /**
     * Checks if {@link #onBlockingDestroy()} has been invoked on this instance.
     *
     * @return {@code True} if "destroy" was invoked by now, {@code false} if not
     */
    protected final boolean isDestroyInvoked() {
        return mIsDestroyInvoked;
    }

    /**
     * Sets the flag to indicate whether {@link #onBlockingStop()} was invoked on this instance or not.
     *
     * @param invoked {@code True} if invoked, {@code false} if not
     */
    public void setStopInvoked(final boolean invoked) {
        mIsStopInvoked = invoked;
    }

    /**
     * Sets the flag to indicate whether {@link #onBlockingDestroy()} was invoked on this instance or not.
     *
     * @param invoked {@code True} if invoked, {@code false} if not
     */
    public void setDestroyInvoked(final boolean invoked) {
        mIsDestroyInvoked = invoked;
    }

    /**
     * This method checks if the current instance is the same one as the one before it, and then invokes {@link #onBlockingStop()} and
     * {@link #onBlockingDestroy()} if they haven't been invoked yet. After these have been invoked once, they will not be invoked again from this class.
     */
    private void invokeLifecycleOperations() {
        // singleTop launch mode has nasty side effects, disable the workaround when that launch mode is detected
        try {
            int launchMode = getPackageManager().getActivityInfo(getComponentName(), PackageManager.GET_META_DATA).launchMode;
            if (launchMode == ActivityInfo.LAUNCH_SINGLE_TOP) {
                return;
            }
        } catch (PackageManager.NameNotFoundException ignored) {}

        // invoke lifecycle events that haven't been invoked yet
        BlockingLifecycleActivity activityInstance = lastInstance != null && lastInstance.get() != null ? lastInstance.get() : null;
        if (activityInstance != null && activityInstance != this && activityInstance.getClass().getName().equals(getClass().getName())) {
            if (!activityInstance.isStopInvoked()) {
                activityInstance.onBlockingStop();
                activityInstance.setStopInvoked(true);
            }
            if (!activityInstance.isDestroyInvoked()) {
                activityInstance.onBlockingDestroy();
                activityInstance.setDestroyInvoked(true);
            }
        }

        lastInstance = new WeakReference<>(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CallSuper
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        invokeLifecycleOperations();
        super.onCreate(savedInstanceState, persistentState);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CallSuper
    protected void onCreate(Bundle savedInstanceState) {
        invokeLifecycleOperations();
        super.onCreate(savedInstanceState);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CallSuper
    protected void onRestart() {
        setStopInvoked(false);
        setDestroyInvoked(false);
        super.onRestart();
    }

    /**
     * This method invokes {@link Activity#onStop()} on the parent class after {@link #onBlockingStop()} is finished. Does not invoke {@link #onBlockingStop()}
     * if it was already invoked.
     */
    @Override
    @CallSuper
    protected void onStop() {
        if (!isStopInvoked()) {
            onBlockingStop();
            setStopInvoked(true);
        }
        super.onStop();
    }

    /**
     * This method invokes {@link Activity#onDestroy()} on the parent class after {@link #onBlockingDestroy()} is finished. Does not invoke
     * {@link #onBlockingDestroy()} if it was already invoked.
     */
    @Override
    @CallSuper
    protected void onDestroy() {
        if (!isDestroyInvoked()) {
            onBlockingDestroy();
            setDestroyInvoked(true);
        }
        super.onDestroy();
    }

}
