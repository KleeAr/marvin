package ar.com.klee.marvin.call;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import ar.com.klee.marvin.activities.IncomingCallActivity;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.STTService;
import ar.com.klee.marvin.voiceControl.handlers.ResponderLlamadaHandler;
import ar.com.klee.marvin.voiceControl.handlers.ResponderSMSHandler;

/*
Servicio que se activa cuando se produce un cambio en el estado del telefono
Puede ser por una llamada entrante, o una realizada por la aplicacion
 */
public class CallReceiver extends BroadcastReceiver {

    private static boolean isIncoming;//variable para controlar si la llamada es recibida por el dispositivo
    private static int lastState = TelephonyManager.CALL_STATE_IDLE;
    private static Date callStartTime;
    private static String savedNumber;
    private static long timeCall; //calculo los segundos hablados en la llamada

    private static CallReceiver instance;
    private Context context;
    private CommandHandlerManager commandHandlerManager;

    public CallReceiver(Context context){
        this.context = context;
    }

    public void initializeCommandHandlerManager(){
        commandHandlerManager = CommandHandlerManager.getInstance();
    }

    public static CallReceiver getInstance(){
        if (instance == null) {
            throw new IllegalStateException("Instance not initialized. Call initializeInstance before calling getInstance");
        }
        return instance;
    }

    public static CallReceiver initializeInstance(Context context) {
        if(instance != null) {
            throw new IllegalStateException("Instance already initialized");
        }
        CallReceiver.instance = new CallReceiver(context);
        return instance;
    }

    public void onReceive(Context context, Intent intent) {

        String stateStr = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
        String number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
        int state = 0;
        if (stateStr.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
            state = TelephonyManager.CALL_STATE_IDLE;
        } else if (stateStr.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
            state = TelephonyManager.CALL_STATE_OFFHOOK;
        } else if (stateStr.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
            state = TelephonyManager.CALL_STATE_RINGING;
        }

        onCallStateChanged(context, state, number);
    }


    protected void onIncomingCallStarted(final Context ctx, final String number, Date start) {

        STTService.getInstance().setIsListening(true);
        commandHandlerManager.setCurrentCommandHandler(new ResponderLlamadaHandler(commandHandlerManager.getTextToSpeech(), commandHandlerManager.getContext(), commandHandlerManager));
        commandHandlerManager.setCurrentContext(commandHandlerManager.getCommandHandler().drive(commandHandlerManager.getCommandHandler().createContext(commandHandlerManager.getCurrentContext(), commandHandlerManager.getActivity(), "responder llamada")));

        //Hay que crear un nuevo hilo y esperar unos instantes para superponer el activity
        Thread thread = new Thread() {
            private int sleepTime = 200;

            @Override
            public void run() {
                super.run();
                try {
                    int wait_Time = 0;

                    while (wait_Time < sleepTime) {
                        sleep(200);
                        wait_Time += 100;
                    }
                } catch (Exception e) {
                } finally {
                }
                //IncomingCallReciever.this.myContext.startActivity(IncomingCallReciever.this.myIntent);
                ctx.startActivity(new Intent(ctx, IncomingCallActivity.class).putExtra("number", number)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));//set

            }
        };
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.run();
        //lanza el activity para aceptar o rechazar la llamada

        timeCall = start.getTime();


    }

    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end) {
        long diffInMs = end.getTime() - timeCall;
        long diffInSec = TimeUnit.MILLISECONDS.toSeconds(diffInMs);
        //hay que agregar a la pregunta de si no es contacto la duracion de los segundos. Valor a definir
        if (!Contact.isContact(ctx, savedNumber)) {
            Toast.makeText(ctx, "no es contacto", Toast.LENGTH_LONG).show();
            //ejecutar comandos de voz para preguntarle el nombre y el email
            //Llamar addNewContact para guardar al nuevo contacto

        }
    }

    protected void onMissedCall(Context ctx, String number, Date start) {
    }

    public void onCallStateChanged(Context context, int state, String number) {
        if (lastState == state) {
            //No se producieron cambios
            return;
        }
        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING:
                isIncoming = true;
                callStartTime = new Date();
                savedNumber = number;
                onIncomingCallStarted(context, number, callStartTime);
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                //Transicion de ringing->offhook
                AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                am.setMode(AudioManager.MODE_IN_CALL);
                am.setSpeakerphoneOn(true);//se activa el altavoz al iniciar la llamada o al recibirla

                //Si se quiere poner el telefono en silencio deberia ser implemntada por voz
                //am.setMicrophoneMute(true); //pone el microfono en mute

                if (lastState != TelephonyManager.CALL_STATE_RINGING) {
                    isIncoming = false;
                    //llamada realizada desde el dispositivo
                }
                break;
            case TelephonyManager.CALL_STATE_IDLE:
                //Telefono en sin uso- esto puede ser porque termino una llamada o no. Depende del estado previo.
                if (lastState == TelephonyManager.CALL_STATE_RINGING) {
                    //Sono pero no contesto - llamada perdida
                    onMissedCall(context, number, callStartTime);
                } else if (isIncoming) {
                    onIncomingCallEnded(context, savedNumber, callStartTime, new Date());
                }


                break;
        }
        lastState = state;
    }


    public static boolean isInstanceInitialized() {
        return instance != null;
    }

    public static void destroyInstance() {
        instance = null;
    }

}
