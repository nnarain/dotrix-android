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
        this.handle = createInstance();
    }

    public void release()
    {
        release(this.handle);
    }

    public void update(int steps)
    {
        if(steps < 0)
        {
            steps = 0;
        }

        update(this.handle, steps);
    }

    public void loadRom(byte[] buffer)
    {
        loadRomFromBuffer(this.handle, buffer);
    }

    public void registerScanlineCallback(ScanlineListener listener)
    {
        registerScanlineCallback(this.handle, listener);
    }

    public void setJniEnv()
    {
        setJniEnv(this.handle);
    }

    /* Native Functions */

    private native long createInstance();
    private native void release(long handle);
    private native void update(long handle, int steps);
    private native void loadRomFromBuffer(long handle, byte[] buffer);
    private native void registerScanlineCallback(long handle, ScanlineListener listener);
    private native void setJniEnv(long handle);
}
