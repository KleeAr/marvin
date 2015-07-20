package ar.com.klee.marvin.voiceControl.handlers.mainMenu;

import android.content.Context;

import ar.com.klee.marvin.expressions.ExpressionMatcher;
import ar.com.klee.marvin.hotspot.WiFiHotspot;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandler;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandlerContext;

public class DesactivarHotspotHandler extends CommandHandler {

    public DesactivarHotspotHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super("desactivar hotspot", textToSpeech, context, commandHandlerManager);
    }

    public CommandHandlerContext drive(CommandHandlerContext context){

        WiFiHotspot wiFiHotspot = new WiFiHotspot(getContext());

        if(wiFiHotspot.isApOn(getContext())) {
            if (wiFiHotspot.removeWifiAccessPoint())
                getTextToSpeech().speakText("Desactivando hotspot");
            else
                getTextToSpeech().speakText("El hotspot no pudo ser desactivado");
        }else{
            getTextToSpeech().speakText("El hotspot ya está desactivado");
        }

        context.put(STEP, 0);
        return context;
    }

    @Override
    protected void addSpecificCommandContext(CommandHandlerContext commandHandlerContext) {
        // TODO
    }
}
