package ar.com.klee.marvin.call;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Date;

import ar.com.klee.marvin.bluetooth.BluetoothClientCallMode;

/**
 * @author msalerno
 */
public abstract class CallMode {

    protected static final int BUSY = 10;
    protected static final int LONG_CALL = 0;
    protected String savedNumber;
    protected long timeCall; //calculo los segundos hablados en la llamada
    final private Class<? extends Activity> activityClass;

    private static CallMode instance;

    public static CallMode getInstance() {
        if (instance == null) {
            instance = new DefaultCallMode();
        }
        return instance;
    }

    public static void setBluetoothClientCallMode() {
        instance = new BluetoothClientCallMode();
    }

    public static void setDefaultCallMode() {
        instance = new DefaultCallMode();
    }

    protected CallMode(Class<? extends Activity> activityClass) {
        this.activityClass = activityClass;
    }

    public void incomingCallStarted(final Context ctx, final String number, Date start) {
        onIncomingCallStarted(ctx, number, start);

        Log.d("CALL", "Incoming started");
        //Hay que crear un nuevo hilo y esperar unos instantes para superponer el activity
        Thread thread = new Thread() {
            private int sleepTime = 500;

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

                    e.printStackTrace();

                } finally {
                }

                //IncomingCallReciever.this.myContext.startActivity(IncomingCallReciever.this.myIntent);

                Intent incomingIntent = new Intent(ctx, activityClass);
                incomingIntent.putExtra("number",number);
                incomingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(incomingIntent);

            }
        };
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.run();
    }

    protected abstract void onIncomingCallStarted(final Context ctx, final String number, Date start);

    public abstract void onIncomingCallEnded(Context ctx, String number, Date start, Date end);

    public abstract void onOutgoingCallEnded(Context ctx, String number, Date start, Date end);

    public abstract void onMissedCall(Context ctx, String number, Date start);

    public void setTimeCall(long timeCall) {
        this.timeCall = timeCall;
    }

    public long getTimeCall() {
        return timeCall;
    }

    public abstract boolean isDefault();
}
