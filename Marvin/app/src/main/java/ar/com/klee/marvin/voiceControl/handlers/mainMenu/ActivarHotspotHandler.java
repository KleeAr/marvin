package ar.com.klee.marvin.voiceControl.handlers.mainMenu;

import android.content.Context;

import ar.com.klee.marvin.expressions.ExpressionMatcher;
import ar.com.klee.marvin.hotspot.WiFiHotspot;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandler;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandlerContext;

public class ActivarHotspotHandler extends CommandHandler {


    public ActivarHotspotHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager){
        super("activar hotspot", textToSpeech, context, commandHandlerManager);
    }

    public CommandHandlerContext drive(CommandHandlerContext currentContext){

        WiFiHotspot wiFiHotspot = new WiFiHotspot(getContext());

        if(!wiFiHotspot.isApOn(getContext())) {
            if (wiFiHotspot.createWifiAccessPoint())
                getTextToSpeech().speakText("Activando hotspot");
            else
                getTextToSpeech().speakText("El hotspot no pudo ser activado");
        }else{
            getTextToSpeech().speakText("El hotspot ya está activado");
        }

        currentContext.put(STEP, 0);
        return currentContext;
    }

    @Override
    protected void addSpecificCommandContext(CommandHandlerContext commandHandlerContext) {
        // TODO: verify if there is something to do here
    }
}
