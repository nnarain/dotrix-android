package io.github.nnarain.dotrix.gameboycore;

import android.util.Log;

/**
 * Created by nnarain on 1/15/2017.
 */

public class GameboyCoreWorker extends Thread
{
    private static final String TAG = GameboyCoreWorker.class.getSimpleName();

    private GameboyCore core;
    private volatile boolean running;

    public GameboyCoreWorker(GameboyCore core)
    {
        this.core = core;
        this.running = false;
    }

    @Override
    public void run()
    {
        core.setJniEnv();

        while(running)
        {
            core.update(1024);
        }
    }

    @Override
    public synchronized void start() {
        running = true;
        super.start();
    }

    public void stopRunning()
    {
        running = true;

        try
        {
            join();
        }
        catch (InterruptedException e)
        {
            Log.d(TAG, "interrupted");
        }
    }
}
