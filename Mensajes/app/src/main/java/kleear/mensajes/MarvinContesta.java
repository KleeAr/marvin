package kleear.mensajes;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/*Pantalla que se activa cuando llega un mensaje
 */

public class MarvinContesta extends Activity {

    private TextView textFrom;
    private TextView textSMS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marvin_contesta);

        Intent i = getIntent();
        Mensaje objMensaje = (Mensaje)i.getSerializableExtra("objMensaje");

        textFrom.setText(objMensaje.getAddress());
        textSMS.setText(objMensaje.getBody());


    }

    public void leer(View view){
        //Metodo a desarrollar
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

}
