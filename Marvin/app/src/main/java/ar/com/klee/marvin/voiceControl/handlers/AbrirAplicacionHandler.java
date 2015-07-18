package ar.com.klee.marvin.voiceControl.handlers;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import java.util.List;
import java.util.Map;

import ar.com.klee.marvin.activities.CallHistoryActivity;
import ar.com.klee.marvin.activities.CameraActivity;
import ar.com.klee.marvin.activities.SMSInboxActivity;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;

public class AbrirAplicacionHandler extends CommandHandler{

    public AbrirAplicacionHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager){

        super("abrir {aplicacion}", textToSpeech, context, commandHandlerManager);
    }

    public CommandHandlerContext drive(CommandHandlerContext currentContext){

        Map<String, String> values = getExpressionMatcher().getValuesFromExpression(currentContext.getString(COMMAND));

        String app = values.get("aplicacion");

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
