package ar.com.klee.marvin.voiceControl.handlers.mainMenu;

import android.content.Context;
import android.media.AudioManager;

import java.util.Map;

import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandler;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandlerContext;

public class EstablecerVolumenHandler extends CommandHandler {

    public EstablecerVolumenHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super("establecer volumen {volumen}", textToSpeech, context, commandHandlerManager);
    }

    public CommandHandlerContext drive(CommandHandlerContext context){

        String command = context.getString(COMMAND);
        Map<String, String> values = getExpressionMatcher(command).getValuesFromExpression(command);

        String volume = values.get("volumen");
        int vol;

        try{

            vol = Integer.parseInt(volume);

        }catch(NumberFormatException e){

            getTextToSpeech().speakText("Debe indicarse un número para el volumen");

            context.put(STEP, 0);
            return context;

        }

        if(vol>15 || vol<1){

            getTextToSpeech().speakText("El volumen indicado debe estar entre 1 y 15");

        }else {

            AudioManager audio = (AudioManager) getCommandHandlerManager().getMainActivity().getSystemService(Context.AUDIO_SERVICE);

            int difference = vol - audio.getStreamVolume(AudioManager.STREAM_MUSIC);

            if(difference > 0){
                while(difference!=0) {
                    audio.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
                    difference--;
                }
            }else if(difference < 0){
                while(difference!=0) {
                    audio.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
                    difference++;
                }
            }

            getTextToSpeech().speakText("El volumen cambió a: " + audio.getStreamVolume(AudioManager.STREAM_MUSIC));
        }
        context.put(STEP, 0);
        return context;

    }

    @Override
    protected void addSpecificCommandContext(CommandHandlerContext commandHandlerContext) {
        // TODO
    }
}
