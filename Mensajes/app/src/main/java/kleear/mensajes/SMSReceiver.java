package kleear.mensajes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

/*Clase de escucha para recibir mensajes
 */
public class SMSReceiver extends BroadcastReceiver {

    public static final String SMS_BUNDLE = "pdus";

    public void onReceive(Context context, Intent intent) {

        //this stops notifications to others
        //this.abortBroadcast();

        Bundle intentExtras = intent.getExtras();
        if (intentExtras != null) {
            Object[] sms = (Object[]) intentExtras.get(SMS_BUNDLE);
            String smsMessageStr = "";
            String smsBody="";
            String address="";
            for (int i = 0; i < sms.length; ++i) {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sms[i]);

                smsBody = smsMessage.getMessageBody().toString();
                address = smsMessage.getOriginatingAddress();




                smsMessageStr += "De: " + address + "\n";
                smsMessageStr += smsBody + "\n";
            }

            Mensaje mensajeEntrante = new Mensaje(address,smsBody);


            Toast.makeText(context, smsMessageStr, Toast.LENGTH_SHORT).show();

            context.startActivity(new Intent(context, MarvinContesta.class).putExtra("objMensaje", mensajeEntrante)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));




           // SMSActivity inst = SMSActivity.instance();
            //inst.updateList(smsMessageStr);
        }
        //continue the normal process of sms and will get alert and reaches inbox
        //this.clearAbortBroadcast();

    }



}
