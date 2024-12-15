package model;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class Room {
    protected String name;
    protected int ID;

    public Room(String name, int ID){
        this.name = name;
        this.ID = ID;
    }

    public abstract void setName(String name);
    public abstract void setID(int iD);

    public abstract int getMaxNumber();

    
    public abstract boolean addSlotVisitors(ArrayList<Visitor> visitors, Slot slot);

    public abstract boolean addSlotVisitors(Visitor visitor, Slot slot);

    public abstract void addSlotVisitors( Slot slot);
    
    public abstract ArrayList<Visitor> getSlotVisitors(Slot slot);

    public abstract int getNumberOfVisitors(Slot slot);

    public abstract void removeVisitor(Visitor visitor,Slot slot) ;

    public abstract void displayDetails();

    public abstract int getID();

    public abstract String getName();

    public abstract HashMap<Slot,ArrayList<Visitor>> getVisitorsInEachSlot();

    public abstract void displayAvailableSlots();

}
