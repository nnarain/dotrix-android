package io.github.nnarain.dotrix.gameboycore;

/**
 * @class GameboyCore
 * @brief Wrapper for Native GameboyCore resource
 */
public class GameboyCore
{
    private long handle;

    public GameboyCore()
    {
        this.handle = GameboyCore.createInstance();
    }

    public void dispose()
    {
        GameboyCore.release(this.handle);
    }

    public void update(int steps)
    {
        if(steps < 0)
        {
            steps = 0;
        }

        GameboyCore.update(this.handle, steps);
    }

    public void loadRom(byte[] buffer)
    {
        GameboyCore.loadRomFromBuffer(this.handle, buffer);
    }

    public void registerScanlineCallback(ScanlineListener listener)
    {
        GameboyCore.registerScanlineCallback(this.handle, listener);
    }

    /* Native Functions */

    private static native long createInstance();
    private static native void release(long handle);
    private static native void update(long handle, int steps);
    private static native void loadRomFromBuffer(long handle, byte[] buffer);
    private static native void registerScanlineCallback(long handle, ScanlineListener listener);
}
