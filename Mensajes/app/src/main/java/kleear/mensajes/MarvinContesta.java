package kleear.mensajes;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

/*Pantalla que se activa cuando llega un mensaje
 */

public class MarvinContesta extends Activity {

    private TextView textFrom;
    private TextView textSMS;
    private Mensaje dato;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marvin_contesta);

        Intent i = getIntent();
        dato = (Mensaje)i.getSerializableExtra("objMensaje");

        textFrom = (TextView) findViewById(R.id.textFrom);
        textFrom.setText(dato.getAddress());

        textSMS = (TextView) findViewById(R.id.textSMS);
        textSMS.setText(dato.getBody());



    }

    public void leer(View view){
        //Metodo a desarrollar
        markMessageRead(getApplicationContext(), dato.getAddress(), dato.getBody());//marca el mensaje como leido
    }

    public void cancelar(View view){
        finish();
    }


    public void responder(View view){
        //Llamada al activity de mensaje
        Intent i = new Intent(this, AnswerSMS.class );
        i.putExtra("parametro", textFrom.getText().toString());
        startActivity(i);
    }

    private void markMessageRead(Context context, String number, String body) {

        Uri uri = Uri.parse("content://sms/inbox");
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        try{

            while (cursor.moveToNext()) {
                if ((cursor.getString(cursor.getColumnIndex("address")).equals(number)) && (cursor.getInt(cursor.getColumnIndex("read")) == 0)) {
                    if (cursor.getString(cursor.getColumnIndex("body")).startsWith(body)) {
                        String SmsMessageId = cursor.getString(cursor.getColumnIndex("_id"));
                        ContentValues values = new ContentValues();
                        values.put("read", true);
                        context.getContentResolver().update(Uri.parse("content://sms/inbox"), values, "_id=" + SmsMessageId, null);
                        return;
                    }
                }
            }
        }catch(Exception e)
        {
            Log.e("Mark Read", "Error in Read: " + e.toString());
        }
    }

}
