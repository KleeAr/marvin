package ar.com.klee.marvin.voiceControl.handlers.mainMenu;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v4.view.ViewPager;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import ar.com.klee.marvin.activities.CallHistoryActivity;
import ar.com.klee.marvin.activities.CameraActivity;
import ar.com.klee.marvin.activities.MainMenuActivity;
import ar.com.klee.marvin.activities.MapActivity;
import ar.com.klee.marvin.activities.SMSInboxActivity;
import ar.com.klee.marvin.fragments.MainMenuFragment;
import ar.com.klee.marvin.fragments.MisViajesFragment;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandler;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandlerContext;

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
            ViewPager pager = ((MainMenuActivity) getCommandHandlerManager().getMainActivity()).getPager();
            int item;
            if(pager == null) {
                item = MainMenuFragment.getInstance().getPager().getCurrentItem();
                MainMenuFragment.getInstance().getPager().setCurrentItem(0);
            }else {
                item = pager.getCurrentItem();
                pager.setCurrentItem(0);
            }
            ((MainMenuActivity) getCommandHandlerManager().getMainActivity()).previousMenus.push(1);
            if(item == 2) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        ((MainMenuActivity) getCommandHandlerManager().getMainActivity()).setFragment(5);
                    }
                }, 1000);
            }else{
                ((MainMenuActivity) getCommandHandlerManager().getMainActivity()).setFragment(5);
            }
            currentContext.put(STEP, 0);
            return currentContext;
        }else if(app.equals("historial de viajes")) {
            getTextToSpeech().speakText("Abriendo historial de viajes");
            ViewPager pager = ((MainMenuActivity) getCommandHandlerManager().getMainActivity()).getPager();
            int item;
            if(pager == null) {
                item = MainMenuFragment.getInstance().getPager().getCurrentItem();
                MainMenuFragment.getInstance().getPager().setCurrentItem(0);
            }else {
                item = pager.getCurrentItem();
                pager.setCurrentItem(0);
            }
            ((MainMenuActivity) getCommandHandlerManager().getMainActivity()).previousMenus.push(1);
            if(item == 2) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        ((MainMenuActivity) getCommandHandlerManager().getMainActivity()).setFragment(4);
                    }
                }, 1000);
            }else{
                ((MainMenuActivity) getCommandHandlerManager().getMainActivity()).setFragment(4);
            }
            currentContext.put(STEP, 0);
            return currentContext;
        }else if(app.equals("dónde estacioné") || app.equals("donde estacioné")) {
            getTextToSpeech().speakText("Abriendo dónde estacioné");
            ViewPager pager = ((MainMenuActivity) getCommandHandlerManager().getMainActivity()).getPager();
            int item;
            if(pager == null) {
                item = MainMenuFragment.getInstance().getPager().getCurrentItem();
                MainMenuFragment.getInstance().getPager().setCurrentItem(0);
            }else {
                item = pager.getCurrentItem();
                pager.setCurrentItem(0);
            }
            ((MainMenuActivity) getCommandHandlerManager().getMainActivity()).previousMenus.push(1);
            if(item == 2) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        ((MainMenuActivity) getCommandHandlerManager().getMainActivity()).setFragment(6);
                    }
                }, 1000);
            }else{
                ((MainMenuActivity) getCommandHandlerManager().getMainActivity()).setFragment(6);
            }
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
