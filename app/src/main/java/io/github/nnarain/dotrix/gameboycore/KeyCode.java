package io.github.nnarain.dotrix.gameboycore;

/**
 * Enum representing Gameboy KeyCodes
 */

public enum KeyCode
{

    RIGHT(0),
    LEFT(1),
    UP(2),
    DOWN(3),
    A(4),
    B(5),
    SELECT(6),
    START(7);


    private final int value;

    KeyCode(int value)
    {
        this.value = value;
    }

    public int value()
    {
        return this.value;
    }
}
