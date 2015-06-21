package kleear.mensajes;
import java.io.Serializable;

@SuppressWarnings("serial")
public class Mensaje implements Serializable {

    private String address, body;

    public Mensaje(String address, String body){
        this.address = address;
        this.body = body;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}

