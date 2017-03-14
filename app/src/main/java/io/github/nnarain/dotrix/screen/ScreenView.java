package io.github.nnarain.dotrix.screen;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import io.github.nnarain.dotrix.gameboycore.ScanlineListener;

/**
 * Created by nnarain on 1/14/2017.
 */

public class ScreenView extends SurfaceView implements ScanlineListener, SurfaceHolder.Callback, Runnable
{
    private static final String TAG = ScreenView.class.getSimpleName();

    private SurfaceHolder holder;
    private Bitmap lcd;
    private Rect lcdSrc;
    private Rect lcdDst;

    Thread drawThread;
    volatile boolean running;

    public ScreenView(Context context)
    {
        super(context);
        init();
    }

    public ScreenView(Context context, AttributeSet set)
    {
        this(context, set, 0);
    }

    public ScreenView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    public void run()
    {
        while (running)
        {
            Canvas canvas = holder.lockCanvas();

            if (canvas != null)
            {
                synchronized (holder)
                {
                    canvas.drawColor(Color.WHITE);
                    canvas.drawBitmap(lcd, lcdSrc, lcdDst, null);
                }

                holder.unlockCanvasAndPost(canvas);
            }

            try
            {
                Thread.sleep(30);
            }
            catch (InterruptedException e)
            {
                Log.d(TAG, e.getMessage());
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        // init the bitmap with source and destination rectangles
        lcd = Bitmap.createBitmap(160, 144, Bitmap.Config.ARGB_8888);
        lcdSrc = new Rect(0, 0, 160, 144);

        int w = getWidth();
        int h = getHeight();

        float aspect = (float)h/(float)w;

        int destWidth = (int)(((float)w * 3.0f/4.0f));

        this.lcdDst = new Rect(0, 0, w, h);

        drawThread = new Thread(this);
        running = true;
        drawThread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        running = false;

        try
        {
            drawThread.join();
        }
        catch (InterruptedException e)
        {
            Log.d(TAG, e.getMessage());
        }
    }

    private void init()
    {
        holder = getHolder();
        holder.addCallback(this);

        running = false;
    }

    @Override
    public void onScanline(int[] colors, int line)
    {
        final int len = colors.length;

        for (int i = 0; i < len; ++i)
        {
            lcd.setPixel(i, line, colors[i]);
        }
    }
}
