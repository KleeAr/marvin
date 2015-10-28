package ar.com.klee.marvin.voiceControl.handlers.camera;

import android.content.Context;
import android.support.v4.media.session.MediaSessionCompatApi14;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import ar.com.klee.marvin.activities.CameraActivity;
import ar.com.klee.marvin.activities.SiteActivity;
import ar.com.klee.marvin.activities.TripActivity;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandler;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandlerContext;

public class CompartirEnFacebookHandler extends CommandHandler {

    protected static final String FACEBOOK_HASHTAGS = "FACEBOOK_HASHTAGS";

    public CompartirEnFacebookHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super(Arrays.asList("compartir en facebook"), textToSpeech, context, commandHandlerManager);
    }


    @Override
    public CommandHandlerContext drive(CommandHandlerContext context){
        Integer step = context.getInteger(STEP);

        try {
            if (context.getBoolean(SET_MESSAGE)) {
                context.put(MESSAGE, context.getString(COMMAND));
            }
        }catch (Exception e){
        }

        switch(step){

            case 1:
                return stepOne(context);
            case 3:
                return stepThree(context);
            case 5:
                return stepFive(context);
            case 7:
                return stepSeven(context);
            case 9:
                return stepNine(context);
            case 11:
                return stepEleven(context);
        }
        context.put(STEP, 0);
        return context;

    }

    @Override
    protected void addSpecificCommandContext(CommandHandlerContext commandHandlerContext) {
        commandHandlerContext.put(FACEBOOK_HASHTAGS, new ArrayList<String>());
        commandHandlerContext.put(SET_MESSAGE, false);
        commandHandlerContext.put(MESSAGE,"");
    }

    //PRONUNCIA MENSAJE
    public CommandHandlerContext stepOne(CommandHandlerContext context){
        getTextToSpeech().speakText("¿Querés publicar el mensaje " + context.getString(MESSAGE) + "?");
        context.put(SET_MESSAGE, false);
        return context.put(STEP, 3);
    }

    //CONFIRMA MENSAJE
    public CommandHandlerContext stepThree(CommandHandlerContext context){

        String input = context.getString(COMMAND);
        if(input.equals("si") || input.equals("sí")) {
            getTextToSpeech().speakText("¿Querés agregar un hashtag?");
            return context.put(STEP, 5);
        }

        if(input.equals("cancelar")) {
            getTextToSpeech().speakText("Cancelando publicación");
            return context.put(STEP, 0);
        }

        if(input.equals("no")){
            getTextToSpeech().speakText("¿Qué mensaje deseás publicar?");
            return context.put(STEP, 1);
        }

        getTextToSpeech().speakText("Debés indicar sí, no o cancelar");

        return context.put(STEP, 3);

    }

    //INDICA SI SE QUIERE AGREGAR UN HASHTAG
    public CommandHandlerContext stepFive(CommandHandlerContext context){
        String input = context.getString(COMMAND);
        if(input.equals("si") || input.equals("sí")) {
            getTextToSpeech().speakText("¿Qué hashtag querés agregar?");
            return context.put(STEP, 7);
        }

        if(input.equals("cancelar")) {
            getTextToSpeech().speakText("Cancelando publicación");
            return context.put(STEP, 0);
        }

        if(input.equals("no")){

            String textToPublish = context.getString(MESSAGE);

            CameraActivity cameraActivity = null;
            SiteActivity siteActivity = null;
            TripActivity tripActivity = null;

            if(getCommandHandlerManager().getCurrentActivity() == CommandHandlerManager.ACTIVITY_CAMERA) {
                cameraActivity = context.getObject(ACTIVITY, CameraActivity.class);
                cameraActivity.share();
                try {
                    cameraActivity.shareInFacebook(textToPublish);
                    getTextToSpeech().speakText("Publicando en el muro de Facebook");
                }catch (Exception e){
                    getTextToSpeech().speakText("No se pudo publicar en Facebook. Recordá asociar la cuenta en tu perfil");
                    e.printStackTrace();
                }
            }else if(getCommandHandlerManager().getCurrentActivity() == CommandHandlerManager.ACTIVITY_SITE) {
                siteActivity = context.getObject(ACTIVITY, SiteActivity.class);
                try {
                    siteActivity.shareInFacebook(textToPublish);
                    getTextToSpeech().speakText("Publicando en el muro de Facebook");
                }catch (Exception e){
                    getTextToSpeech().speakText("No se pudo publicar en Facebook. Recordá asociar la cuenta en tu perfil");
                    e.printStackTrace();
                }
            }else{
                tripActivity = context.getObject(ACTIVITY, TripActivity.class);
                try {
                    tripActivity.shareInFacebook(textToPublish);
                    getTextToSpeech().speakText("Publicando en el muro de Facebook");
                }catch (Exception e){
                    getTextToSpeech().speakText("No se pudo publicar en Facebook. Recordá asociar la cuenta en tu perfil");
                    e.printStackTrace();
                }
            }

            return context.put(STEP, 0);
        }

        getTextToSpeech().speakText("Debés indicar sí, no o cancelar");

        return context.put(STEP, 5);
    }

    //INGRESA HASHTAG
    public CommandHandlerContext stepSeven(CommandHandlerContext context){

        String hashtag = context.getString(COMMAND);
        getTextToSpeech().speakText("¿Querés agregar el hashtag " + hashtag + "?");

        List<String> hashtags = context.getList(FACEBOOK_HASHTAGS, String.class);
        hashtags.add(hashtag);

        return context.put(STEP, 9);

    }

    //CONFIRMA HASHTAG
    public CommandHandlerContext stepNine(CommandHandlerContext context){
        String input = context.getString(COMMAND);
        if(input.equals("si") || input.equals("sí")) {
            getTextToSpeech().speakText("¿Querés agregar otro hashtag?");
            return context.put(STEP, 11);
        }

        if(input.equals("cancelar")) {
            getTextToSpeech().speakText("Cancelando publicación");
            return context.put(STEP, 0);
        }

        if(input.equals("no")){
            getTextToSpeech().speakText("¿Qué hashtag querés agregar?");
            List<String> hashtags = context.getList(FACEBOOK_HASHTAGS, String.class);
            hashtags.remove(hashtags.size()-1);
            context.put(FACEBOOK_HASHTAGS, hashtags);
            return context.put(STEP, 7);
        }

        getTextToSpeech().speakText("Debés indicar sí, no o cancelar");

        return context.put(STEP, 9);

    }

    //INDICA SI SE QUIERE AGREGAR OTRO HASHTAG
    public CommandHandlerContext stepEleven(CommandHandlerContext context){
        String input = context.getString(COMMAND);
        if(input.equals("si") || input.equals("sí")) {
            getTextToSpeech().speakText("¿Qué hashtag querés agregar?");
            return context.put(STEP, 7);
        }
        if(input.equals("cancelar")) {
            getTextToSpeech().speakText("Cancelando publicación");
            return context.put(STEP, 0);
        }
        if(input.equals("no")){
            getTextToSpeech().speakText("Publicando en el muro de Facebook");

            String textToPublish = context.getString(MESSAGE);
            List<String> hashtags = context.getList(FACEBOOK_HASHTAGS, String.class);

            Character firstCharacter, newFirstCharacter;

            int i=0;

            while(i != hashtags.size()){

                textToPublish = textToPublish + " #";

                String hashtag = hashtags.get(i).toLowerCase();

                String word;

                StringTokenizer stringTokenizer = new StringTokenizer(hashtag);

                while(stringTokenizer.hasMoreTokens()){

                    word = stringTokenizer.nextToken();

                    firstCharacter = word.charAt(0);
                    newFirstCharacter = Character.toUpperCase(firstCharacter);
                    word = word.replaceFirst(firstCharacter.toString(),newFirstCharacter.toString());

                    textToPublish = textToPublish + word;

                }

                i++;

            }

            CameraActivity cameraActivity = null;
            SiteActivity siteActivity = null;
            TripActivity tripActivity = null;

            if(getCommandHandlerManager().getCurrentActivity() == CommandHandlerManager.ACTIVITY_CAMERA) {
                cameraActivity = context.getObject(ACTIVITY, CameraActivity.class);
                cameraActivity.share();
                try {
                    cameraActivity.shareInFacebook(textToPublish);
                    getTextToSpeech().speakText("Publicando en el muro de Facebook");
                }catch (Exception e){
                    getTextToSpeech().speakText("No se pudo publicar en Facebook. Recordá asociar la cuenta en tu perfil");
                    e.printStackTrace();
                }
            }else if(getCommandHandlerManager().getCurrentActivity() == CommandHandlerManager.ACTIVITY_SITE) {
                siteActivity = context.getObject(ACTIVITY, SiteActivity.class);
                try {
                    siteActivity.shareInFacebook(textToPublish);
                    getTextToSpeech().speakText("Publicando en el muro de Facebook");
                }catch (Exception e){
                    getTextToSpeech().speakText("No se pudo publicar en Facebook. Recordá asociar la cuenta en tu perfil");
                    e.printStackTrace();
                }
            }else{
                tripActivity = context.getObject(ACTIVITY, TripActivity.class);
                try {
                    tripActivity.shareInFacebook(textToPublish);
                    getTextToSpeech().speakText("Publicando en el muro de Facebook");
                }catch (Exception e){
                    getTextToSpeech().speakText("No se pudo publicar en Facebook. Recordá asociar la cuenta en tu perfil");
                    e.printStackTrace();
                }
            }

            return context.put(STEP, 0);
        }
        getTextToSpeech().speakText("Debés indicar sí, no o cancelar");
        return context.put(STEP, 11);
    }

}

