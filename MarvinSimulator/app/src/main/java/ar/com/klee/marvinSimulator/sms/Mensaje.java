package ar.com.klee.marvinSimulator.sms;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("serial")
public class Mensaje implements Serializable {

    private String phoneNumber, contactName, bodyMessage;
    private String date;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getBodyMessage() {
        return bodyMessage;
    }

    public void setBodyMessage(String bodyMessage) {
        this.bodyMessage = bodyMessage;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Mensaje(String phoneNumber, String bodyMessage, String contactName, String date) {
        this.phoneNumber = phoneNumber;
        this.bodyMessage = bodyMessage;
        this.contactName = contactName;
        this.date = date;
    }

}
