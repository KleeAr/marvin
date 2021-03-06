package ar.com.klee.marvinSimulator.voiceControl.handlers.mainMenu;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import ar.com.klee.marvinSimulator.activities.CallHistoryActivity;
import ar.com.klee.marvinSimulator.activities.CameraActivity;
import ar.com.klee.marvinSimulator.activities.MainMenuActivity;
import ar.com.klee.marvinSimulator.activities.MapActivity;
import ar.com.klee.marvinSimulator.activities.SMSInboxActivity;
import ar.com.klee.marvinSimulator.voiceControl.CommandHandlerManager;
import ar.com.klee.marvinSimulator.voiceControl.TTS;
import ar.com.klee.marvinSimulator.voiceControl.handlers.CommandHandler;
import ar.com.klee.marvinSimulator.voiceControl.handlers.CommandHandlerContext;

public class AbrirAplicacionHandler extends CommandHandler {

    public AbrirAplicacionHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager){

        super(Arrays.asList("abrir {aplicacion}","abre {aplicacion}"), textToSpeech, context, commandHandlerManager);

    }

    public CommandHandlerContext drive(CommandHandlerContext currentContext){

        String command = currentContext.getString(COMMAND);
        Map<String, String> values = getExpressionMatcher(command).getValuesFromExpression(command);

        String app = values.get("aplicacion");

        getTextToSpeech().speakTextWithoutStart("Esperá un momento");

        if(app.equals("cámara")) {
            Intent intent = new Intent(getContext(), CameraActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getTextToSpeech().speakText("Abriendo "+ app);
            getContext().startActivity(intent);
            currentContext.put(STEP, 0);
            return currentContext;
        }else if(app.equals("historial de sms")) {
            Intent intent = new Intent(getContext(), SMSInboxActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getTextToSpeech().speakText("Abriendo "+ app);
            getContext().startActivity(intent);
            currentContext.put(STEP, 0);
            return currentContext;
        }else if(app.equals("historial de llamadas")) {
            Intent intent = new Intent(getContext(), CallHistoryActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getTextToSpeech().speakText("Abriendo " + app);
            getContext().startActivity(intent);
            currentContext.put(STEP, 0);
            return currentContext;
        }else if(app.equals("mapa")) {
            Intent intent = new Intent(getContext(), MapActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getTextToSpeech().speakText("Abriendo " + app);
            getContext().startActivity(intent);
            currentContext.put(STEP, 0);
            return currentContext;
        }else if(app.equals("mis sitios")) {
            getTextToSpeech().speakText("Abriendo mis sitios");
            ((MainMenuActivity)getCommandHandlerManager().getMainActivity()).previousMenus.push(1);
            ((MainMenuActivity)getCommandHandlerManager().getMainActivity()).setFragment(5);
            currentContext.put(STEP, 0);
            return currentContext;
        }else if(app.equals("historial de viajes")) {
            getTextToSpeech().speakText("Abriendo historial de viajes");
            ((MainMenuActivity)getCommandHandlerManager().getMainActivity()).previousMenus.push(1);
            ((MainMenuActivity)getCommandHandlerManager().getMainActivity()).setFragment(4);
            currentContext.put(STEP, 0);
            return currentContext;
        }else if(app.equals("dónde estacioné")) {
            getTextToSpeech().speakText("Abriendo dónde estacioné");
            ((MainMenuActivity)getCommandHandlerManager().getMainActivity()).previousMenus.push(1);
            ((MainMenuActivity)getCommandHandlerManager().getMainActivity()).setFragment(6);
            currentContext.put(STEP, 0);
            return currentContext;
        }else if(app.equals("marvin")) {
            getTextToSpeech().speakText("La aplicación " + app + " ya se está ejecutando");
            currentContext.put(STEP, 0);
            return currentContext;
        }else{
            final PackageManager pm = getContext().getPackageManager();
            //get a list of installed apps.
            List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

            int i = 0;

            while(i < packages.size()) {
                String appLabel = pm.getApplicationLabel(packages.get(i)).toString();

                if(appLabel.toLowerCase().equals(app)){
                    getTextToSpeech().speakText("Abriendo "+ app);
                    getCommandHandlerManager().getMainActivity().startActivity(pm.getLaunchIntentForPackage(packages.get(i).packageName));
                    currentContext.put(STEP, 0);
                    return currentContext;
                }

                i++;
            }
        }

        getTextToSpeech().speakText("La aplicación " + app + " no se encuentra instalada.");
        currentContext.put(STEP, 0);
        return currentContext;

    }

    @Override
    protected void addSpecificCommandContext(CommandHandlerContext commandHandlerContext) {
        // Do nothing
    }
}
