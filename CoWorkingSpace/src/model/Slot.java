package model;

import java.util.ArrayList;

public class Slot {
    private String time;
    private String date;
    private double fees;
    private boolean isAvailable;

    public Slot(String time, String date, double fees,boolean isAvailable) {
        this.time = time;
        this.date = date;
        this.fees = fees;
        this.isAvailable =isAvailable;
    }

    public void setFees(double fees) {
        this.fees = fees;
    }

    

    public String getDetails(){
        return "Slot [Date: " + date + ", Time: " + time + ", Fees: " + fees + " Available: "+isAvailable+ " ]";
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }


    public boolean isAvailable() {
        return isAvailable;
    }

    public String getDate() {
        return date;
    }

    public double getFees() {
        return fees;
    }

    
    public String getTime() {
        return time;
    }
}
