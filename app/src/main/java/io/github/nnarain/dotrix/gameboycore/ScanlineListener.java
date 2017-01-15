package io.github.nnarain.dotrix.gameboycore;

/**
 * Created by nnarain on 1/14/2017.
 */

public interface ScanlineListener
{
    void onScanline(Color[] color, int line);
}
