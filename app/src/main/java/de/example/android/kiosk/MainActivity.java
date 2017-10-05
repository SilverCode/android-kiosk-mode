package de.example.android.kiosk;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    private final List blockedKeys = new ArrayList(Arrays.asList(KeyEvent.KEYCODE_VOLUME_DOWN, KeyEvent.KEYCODE_VOLUME_UP));
    private Button hiddenExitButton;
    private CustomViewGroup customViewGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        setContentView(R.layout.activity_main);
        preventStatusBarExpansion(getApplicationContext());

        // every time someone enters the kiosk mode, set the flag true
        PrefUtils.setKioskModeActive(true, getApplicationContext());

        hiddenExitButton = (Button) findViewById(R.id.hiddenExitButton);
        hiddenExitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Break out!
                PrefUtils.setKioskModeActive(false, getApplicationContext());
                Toast.makeText(getApplicationContext(),"You can leave the app now!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(!hasFocus) {
            // Close every kind of system dialog
            Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            sendBroadcast(closeDialog);
        }
    }

    @Override
    public void onBackPressed() {
        // nothing to do here
        // … really
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (blockedKeys.contains(event.getKeyCode())) {
            return true;
        } else {
            return super.dispatchKeyEvent(event);
        }
    }

    public void preventStatusBarExpansion(Context context) {
        WindowManager manager = ((WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE));

        WindowManager.LayoutParams localLayoutParams = new WindowManager.LayoutParams();
        localLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        localLayoutParams.gravity = Gravity.TOP;
        localLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL|WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;

        localLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;

        int resId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        int result = 0;
        if (resId > 0) {
            result = context.getResources().getDimensionPixelSize(resId);
        } else {
            // Use Fallback size:
            result = 60; // 60px Fallback
        }

        localLayoutParams.height = result;
        localLayoutParams.format = PixelFormat.TRANSPARENT;

        customViewGroup = new CustomViewGroup(context);
        manager.addView(customViewGroup, localLayoutParams);
    }

}
