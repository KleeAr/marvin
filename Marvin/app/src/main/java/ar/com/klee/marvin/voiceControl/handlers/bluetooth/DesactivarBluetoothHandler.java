package ar.com.klee.marvin.voiceControl.handlers.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;

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
public class DesactivarBluetoothHandler extends CommandHandler {


    public DesactivarBluetoothHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super(Arrays.asList("desactivar bluetooth", "deshabilitar bluetooth"), textToSpeech, context, commandHandlerManager);
    }

    @Override
    public CommandHandlerContext drive(CommandHandlerContext currentContext) {

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter.isEnabled() && BluetoothService.isStarted()) {
            ((MainMenuActivity)getCommandHandlerManager().getActivity()).stopBluetoothService();
            getTextToSpeech().speakText("bluetooth desactivado");
        } else {
            getTextToSpeech().speakText("El bluetooth ya estaba desactivado");
        }
        currentContext.put(STEP, 0);
        return currentContext;
    }

    @Override
    protected void addSpecificCommandContext(CommandHandlerContext commandHandlerContext) {

    }
}
