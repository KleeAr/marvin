package ar.com.klee.marvinSimulator.call;

import java.util.Date;

public class Call {

    private String contactName;
    private String numberPhone;
    private String type;
    private String date;
    private String duration;
    private Date unformattedDate;

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getNumberPhone() {
        return numberPhone;
    }

    public void setNumberPhone(String numberPhone) {
        this.numberPhone = numberPhone;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getUnformattedDate() {
        return unformattedDate;
    }

    public void setUnformattedDate(Date unformattedDate) {
        this.unformattedDate = unformattedDate;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}
