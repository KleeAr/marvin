package ar.com.klee.marvin.voiceControl.handlers;

import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;

public class SubirVolumenHandler extends CommandHandler{

    public SubirVolumenHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super("subir volumen", textToSpeech, context, commandHandlerManager);
    }

    public CommandHandlerContext drive(CommandHandlerContext context){

        AudioManager audio = (AudioManager) getCommandHandlerManager().getMainActivity().getSystemService(Context.AUDIO_SERVICE);
        audio.adjustStreamVolume(AudioManager.STREAM_MUSIC,AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);

        getTextToSpeech().speakText("Subiendo volumen. El valor actual es: " + audio.getStreamVolume(AudioManager.STREAM_MUSIC));

        context.put(STEP, 0);
        return context;

    }

    @Override
    protected void addSpecificCommandContext(CommandHandlerContext commandHandlerContext) {
        // TODO
    }
}
