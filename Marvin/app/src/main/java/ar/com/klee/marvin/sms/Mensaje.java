package ar.com.klee.marvin.sms;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("serial")
public class Mensaje implements Serializable {

    private String phoneNumber, contactName, bodyMessage;
    private Long date;

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

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public Mensaje(String phoneNumber, String bodyMessage, String contactName, Long date) {
        this.phoneNumber = phoneNumber;
        this.bodyMessage = bodyMessage;
        this.contactName = contactName;
        this.date = date;
    }

}
