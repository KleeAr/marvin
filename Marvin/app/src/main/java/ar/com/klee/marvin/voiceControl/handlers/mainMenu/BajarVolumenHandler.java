package ar.com.klee.marvin.voiceControl.handlers.mainMenu;

import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandler;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandlerContext;

public class BajarVolumenHandler extends CommandHandler {

    public BajarVolumenHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super("bajar volumen", textToSpeech, context, commandHandlerManager);
    }

    public CommandHandlerContext drive(CommandHandlerContext context){

        AudioManager audio = (AudioManager) getCommandHandlerManager().getMainActivity().getSystemService(Context.AUDIO_SERVICE);
        audio.adjustStreamVolume(AudioManager.STREAM_MUSIC,AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);

        getTextToSpeech().speakText("Bajando volumen. El valor actual es: " + audio.getStreamVolume(AudioManager.STREAM_MUSIC));

        context.put(STEP, 0);
        return context;

    }

    @Override
    protected void addSpecificCommandContext(CommandHandlerContext commandHandlerContext) {
        // TODO
    }
}
