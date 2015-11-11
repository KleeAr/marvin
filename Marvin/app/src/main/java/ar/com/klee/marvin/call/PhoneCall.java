package ar.com.klee.marvin.call;

import java.util.Date;

/**
 * @author msalerno
 */
public class PhoneCall {

    private String number;
    private Date start;

    public PhoneCall(){}

    public PhoneCall(String number, Date start) {
        this.number = number;
        this.start = start;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }
}
