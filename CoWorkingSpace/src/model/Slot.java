package model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Slot {
    private LocalTime time;
    private LocalDate date;
    private double fees;
    private boolean isAvailable;

    public Slot(LocalTime time, LocalDate date, double fees,boolean isAvailable) {
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

    public LocalDate getDate() {
        return date;
    }

    public double getFees() {
        return fees;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }
    
    public void setDate(LocalDate date) {
        this.date = date;
    }
    public LocalTime getTime() {
        return time;
    }
}
