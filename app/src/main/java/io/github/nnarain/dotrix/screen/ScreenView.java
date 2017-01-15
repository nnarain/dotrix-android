package io.github.nnarain.dotrix.screen;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.SurfaceView;

import io.github.nnarain.dotrix.gameboycore.Color;
import io.github.nnarain.dotrix.gameboycore.ScanlineListener;

/**
 * Created by nnarain on 1/14/2017.
 */

public class ScreenView extends SurfaceView implements ScanlineListener
{
    private Bitmap lcd;
    private Rect lcd_src;
    private Rect lcd_dst;

    public ScreenView(Context context)
    {
        super(context);

        this.lcd = Bitmap.createBitmap(160, 144, Bitmap.Config.ARGB_8888);
        this.lcd_src = new Rect(0,0,160,144);
        this.lcd_dst = new Rect(0,0,this.getWidth(),this.getHeight());
    }

    public ScreenView(Context context, AttributeSet set)
    {
        super(context, set);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        canvas.drawBitmap(this.lcd, this.lcd_src, this.lcd_dst, null);
    }

    @Override
    public void onScanline(Color[] colors, int line)
    {
        final int len = colors.length;

        for(int i = 0; i < len; ++i)
        {
            this.lcd.setPixel(i, line, colors[i].toInt());
        }
    }
}
