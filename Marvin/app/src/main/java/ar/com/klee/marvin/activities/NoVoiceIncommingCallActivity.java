package ar.com.klee.marvin.activities;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import ar.com.klee.marvin.R;
import ar.com.klee.marvin.bluetooth.BluetoothConstants;
import ar.com.klee.marvin.bluetooth.BluetoothHandler;
import ar.com.klee.marvin.bluetooth.BluetoothService;

public class NoVoiceIncommingCallActivity extends FragmentActivity {

    private BluetoothService bluetoothService;
    private BluetoothHandler handler = new BluetoothHandler(this) {
        @Override
        protected void onMessageRead(String readMessage) {
            if(BluetoothConstants.ACCEPT_CALL.equals(readMessage)) {
                Context mContext = getApplicationContext();
                Intent buttonUp = new Intent(Intent.ACTION_MEDIA_BUTTON);
                buttonUp.putExtra(Intent.EXTRA_KEY_EVENT,
                        new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK));
                mContext.sendOrderedBroadcast(buttonUp, "android.permission.CALL_PRIVILEGED");

                //activar el altavoz
                AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                am.setMode(AudioManager.MODE_IN_CALL);
                am.setSpeakerphoneOn(true);
            } else {
                Context mContext = getApplicationContext();
                Intent buttonDown = new Intent(Intent.ACTION_MEDIA_BUTTON); buttonDown.putExtra(Intent.EXTRA_KEY_EVENT,
                        new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_HEADSETHOOK));
                mContext.sendOrderedBroadcast(buttonDown, "android.permission.CALL_PRIVILEGED");
                finish(); //Regresa a la activity anterior
            }
        }

        @Override
        protected void onMessageWrote(String writeMessage) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming_call);
        bluetoothService = BluetoothService.getInstance();
        bluetoothService.setmHandler(handler);
    }

}
