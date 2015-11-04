package ar.com.klee.marvin.voiceControl.handlers.fun;

import android.content.Context;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandler;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandlerContext;

public class ChisteHandler extends CommandHandler {

    public ChisteHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super(Arrays.asList("contá un chiste","contar chiste","contate un chiste","contar un chiste","contame un chiste"), textToSpeech, context, commandHandlerManager);
    }

    public CommandHandlerContext drive(CommandHandlerContext context){

        List<String> jokes = Arrays.asList("Cariño, ¿tengo la nariz grande? No, tenés una nariz común. ¡Común tucán! jajaja",
                "Mi amor estoy embarazada. ¿Qué te gustaría que fuera? ¡Una broma! jajaja",
                "¿Tenés wifi? Sí. ¿Y cuál es la clave? ¡Tener dinero y pagarlo! jajaja",
                "Cariño, tengo dos noticias, una buena y otra mala. He dejado las drogas. Pero no sé donde. jajaja",
                "¿Qué hace una persona con un sobre de ketchup en la oreja?. Está escuchando salsa. jajaja",
                "Hija, lo que hiciste no tiene nombre! Todavía no Papi, ¡pero espérate 9 meses y le pongo uno bien bonito! jajaja",
                "¿Cómo se dice dame un beso en árabe?. Mohamed Lajeta. jajaja",
                "Yo para adelgazar sigo la dieta de la manzana: me compré un Iphone 5S y ya no tengo para comer. jajaja",
                "Soy tan buena persona que no madrugo para que Dios ayude a otro. jajaja",
                "Tía Teresa, ¿para qué te pintás? Para estar más bella. Ah, ¿y tarda mucho en hacer efecto? jajaja");

        Random r = new Random();
        int i1 = r.nextInt(10);

        getTextToSpeech().speakText(jokes.get(i1));

        context.put(STEP, 0);
        return context;

    }

    @Override
    protected void addSpecificCommandContext(CommandHandlerContext commandHandlerContext) {
        // TODO
    }
}
