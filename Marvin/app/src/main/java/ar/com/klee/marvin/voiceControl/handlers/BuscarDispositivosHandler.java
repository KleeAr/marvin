package ar.com.klee.marvin.voiceControl.handlers;

import android.content.Context;
import android.content.Intent;

import java.util.Arrays;

import ar.com.klee.marvin.activities.BluetoothActivity;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;

/**
 * @author msalerno
 */
public class BuscarDispositivosHandler extends CommandHandler {

    public BuscarDispositivosHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super(Arrays.asList("buscar dispositivos bluetooth"), textToSpeech, context, commandHandlerManager);
    }

    @Override
    public CommandHandlerContext drive(CommandHandlerContext currentContext) {
        getTextToSpeech().speakText("Buscando dispositivos bluetooth");
        Intent intent = new Intent(getContext(), BluetoothActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getContext().startActivity(intent);
        return currentContext.put(STEP, 0);
    }

    @Override
    protected void addSpecificCommandContext(CommandHandlerContext commandHandlerContext) {

    }
}
