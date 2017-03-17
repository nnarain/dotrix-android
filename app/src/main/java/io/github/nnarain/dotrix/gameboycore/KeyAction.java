package io.github.nnarain.dotrix.gameboycore;

/**
 * Created by nnarain on 3/14/2017.
 */

public enum KeyAction
{
    PRESS(0),
    RELEASE(1);

    private final int value;

    KeyAction(int value)
    {
        this.value = value;
    }

    public int value()
    {
        return this.value;
    }
}
