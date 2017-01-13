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

    public void loadRom(byte[] buffer)
    {
        GameboyCore.loadRomFromBuffer(this.handle, buffer);
    }

    public void dispose()
    {
        GameboyCore.release(this.handle);
    }

    /* Native Functions */

    private static native long createInstance();
    private static native void release(long handle);
    private static native void loadRomFromBuffer(long handle, byte[] buffer);
}
