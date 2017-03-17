package io.github.nnarain.dotrix;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SurfaceView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import io.github.nnarain.dotrix.gameboycore.GameboyCore;
import io.github.nnarain.dotrix.gameboycore.GameboyCoreWorker;
import io.github.nnarain.dotrix.gameboycore.ScanlineListener;
import io.github.nnarain.dotrix.ui.Input;
import io.github.nnarain.dotrix.ui.ScreenView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int REQUEST_CODE_OPEN_FILE = 1;

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("gameboycore-jni");
    }

    private SurfaceView screen;
    private Input input;
    private GameboyCore core;
    private GameboyCoreWorker coreUpdater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        core = new GameboyCore();
        coreUpdater = new GameboyCoreWorker(core);

        screen = (ScreenView)findViewById(R.id.screenview);

        // set touch listener
        input = new Input(this, core);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        coreUpdater.stopRunning();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        core.release();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE_OPEN_FILE)
        {
            final Uri uri = data.getData();

            try
            {
                InputStream in = getContentResolver().openInputStream(uri);

                byte[] buffer = new byte[in.available()];
                in.read(buffer);

                core.loadRom(buffer);
                core.registerScanlineCallback((ScanlineListener)screen);

                coreUpdater.start();
            }
            catch(FileNotFoundException e)
            {
                Log.d(TAG, "File not found");
            }
            catch (IOException e)
            {
                Log.d(TAG, "IO Exception");
            }
        }
    }

    private void getFileUri()
    {
        Intent intent = new Intent();
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(intent, "Select a file"), REQUEST_CODE_OPEN_FILE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId())
        {
            case R.id.menu_main_open_file:
                getFileUri();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
