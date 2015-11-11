package ar.com.klee.marvin.voiceControl.handlers.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import java.util.Arrays;

import ar.com.klee.marvin.activities.MainMenuActivity;
import ar.com.klee.marvin.bluetooth.BluetoothService;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandler;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandlerContext;

/**
 * @author msalerno
 */
public class ActivarBluetoothHandler extends CommandHandler {


    public ActivarBluetoothHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super(Arrays.asList("activar bluetooth", "habilitar bluetooth"), textToSpeech, context, commandHandlerManager);
    }

    @Override
    public CommandHandlerContext drive(CommandHandlerContext currentContext) {


        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!bluetoothAdapter.isEnabled()) {
            getTextToSpeech().speakText("activando bluetooth");
            bluetoothAdapter.enable();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ((MainMenuActivity)getCommandHandlerManager().getActivity()).initializeBluetoothService();
                    getTextToSpeech().speakText("bluetooth activado");
                }
            }, 3500);
        } else if(!BluetoothService.isStarted()) {
            ((MainMenuActivity)getCommandHandlerManager().getActivity()).initializeBluetoothService();
            getTextToSpeech().speakText("bluetooth activado");
        } else {
            getTextToSpeech().speakText("El bluetooth ya estaba activado");
        }
        currentContext.put(STEP, 0);
        return currentContext;
    }

    @Override
    protected void addSpecificCommandContext(CommandHandlerContext commandHandlerContext) {

    }
}
