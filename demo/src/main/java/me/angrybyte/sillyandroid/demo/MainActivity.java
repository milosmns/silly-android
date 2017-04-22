package me.angrybyte.sillyandroid.demo;

import android.os.Bundle;

import me.angrybyte.sillyandroid.components.EasyActivity;

/**
 * The main activity of the demo app.
 */
public final class MainActivity extends EasyActivity {

    /**
     * @inheritDoc
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

}
