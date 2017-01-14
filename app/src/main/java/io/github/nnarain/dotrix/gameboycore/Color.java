package io.github.nnarain.dotrix.gameboycore;

/**
 * Created by nnarain on 1/13/2017.
 */

public class Color
{
    public int r;
    public int g;
    public int b;

    public Color(int r, int g, int b)
    {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public int toInt()
    {
        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }

    public Color()
    {
        this(0,0,0);
    }
}
