package io.github.nnarain.dotrix.ui;

import android.app.Activity;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;

import io.github.nnarain.dotrix.R;
import io.github.nnarain.dotrix.gameboycore.GameboyCore;
import io.github.nnarain.dotrix.gameboycore.KeyAction;
import io.github.nnarain.dotrix.gameboycore.KeyCode;

/**
 * Collect user input and send to GameboyCore
 */

public class Input implements View.OnTouchListener
{
    private static final String TAG = Input.class.getSimpleName();

    private final GameboyCore core;
    private final SparseArray<KeyCode> keymap;
    private final SparseArray<KeyAction> actionmap;

    public Input(Activity activity, GameboyCore core)
    {
        this.core = core;

        // set key and action maps
        this.keymap = new SparseArray<KeyCode>();
        this.keymap.append(R.id.bnUp, KeyCode.UP);
        this.keymap.append(R.id.bnDown, KeyCode.DOWN);
        this.keymap.append(R.id.bnLeft, KeyCode.LEFT);
        this.keymap.append(R.id.bnRight, KeyCode.RIGHT);
        this.keymap.append(R.id.bnA, KeyCode.A);
        this.keymap.append(R.id.bnB, KeyCode.B);
        this.keymap.append(R.id.bnSelect, KeyCode.SELECT);
        this.keymap.append(R.id.bnStart, KeyCode.START);

        this.actionmap = new SparseArray<KeyAction>();
        this.actionmap.append(MotionEvent.ACTION_DOWN, KeyAction.PRESS);
        this.actionmap.append(MotionEvent.ACTION_UP, KeyAction.RELEASE);

        // set button listeners
        activity.findViewById(R.id.bnUp).setOnTouchListener(this);
        activity.findViewById(R.id.bnDown).setOnTouchListener(this);
        activity.findViewById(R.id.bnLeft).setOnTouchListener(this);
        activity.findViewById(R.id.bnRight).setOnTouchListener(this);
        activity.findViewById(R.id.bnA).setOnTouchListener(this);
        activity.findViewById(R.id.bnB).setOnTouchListener(this);
        activity.findViewById(R.id.bnSelect).setOnTouchListener(this);
        activity.findViewById(R.id.bnStart).setOnTouchListener(this);

    }

    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        final int action = event.getAction();

        if(action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_DOWN)
        {
            KeyAction a = this.actionmap.get(action);
            KeyCode code = this.keymap.get(v.getId());

            Log.d(TAG, "Sending input to core: " + a.value() + ", " + code.value());
            core.input(a, code);
        }

        return true;
    }
}
