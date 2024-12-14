package model;

import java.util.ArrayList;
import java.util.HashMap;

public class MeetingRoom extends Room {
        protected int numberOfVisitors;
        protected HashMap<Slot,ArrayList<Visitor>> visitorsInEachSlot;
        protected int maxNumber =10;


        public MeetingRoom(String name, int ID){
            super(name,ID);
            this.visitorsInEachSlot = new HashMap<>();

        }

        public int getMaxNumber() {
            return maxNumber;
        }

        @Override
        public void setID(int iD) {
            this.ID = iD;
        }
    
        @Override
        public void setName(String name) {
            this.name = name;
        }


        @Override
        public void addSlotVisitors(ArrayList<Visitor>visitors,Slot slot){
            if((this.visitorsInEachSlot.get(slot) == null) && visitors.size() <= 20){
                this.visitorsInEachSlot.put(slot, visitors);
            }else if((this.visitorsInEachSlot.get(slot).size() <= 20) && (this.visitorsInEachSlot.get(slot).size() + visitors.size() <= 20)){
                    this.visitorsInEachSlot.put(slot, visitors); 
            }else{
                    System.out.println("Sorry, Room is full.");
            }
        }

        @Override
        public void addSlotVisitors(Visitor visitor,Slot slot){
            ArrayList<Visitor> a =new ArrayList<>();
            a.add(visitor);
            if((this.visitorsInEachSlot.get(slot) == null)){
                this.visitorsInEachSlot.put(slot, a);
            }else if(this.visitorsInEachSlot.get(slot).size() < 10){
                    this.visitorsInEachSlot.put(slot, a); 
            }else{
                    System.out.println("Sorry, Room is full.");
            }
        }
    

        @Override
        public void displayDetails(){
            System.out.println("Name: "+ name + " Id: "+ ID );
        } 

        @Override
        public  int getID(){
            return this.ID;
        }
        
        @Override
        public  String getName(){
            return this.name;
        }
        
        @Override
        public void displayAvailableSlots() {
            System.out.println("Available slots for " + name + ":");
            for (Slot slot : this.visitorsInEachSlot.keySet()) {
                if (slot.isAvailable()) {
                    System.out.println(slot.getDetails());
                }
            }
        }

        @Override
        public  void removeVisitor(Visitor visitor,Slot slot){
            this.visitorsInEachSlot.remove(slot,visitor);
        };  
        
        @Override
        public void addSlotVisitors(Slot slot){
            this.visitorsInEachSlot.put(slot, null);
        }

        @Override
        public int getNumberOfVisitors(Slot slot) {
            return this.visitorsInEachSlot.get(slot).size();
        }

        @Override
        public HashMap<Slot, ArrayList<Visitor>> getVisitorsInEachSlot() {
        return visitorsInEachSlot;
        }

        @Override
        public ArrayList<Visitor> getSlotVisitors(Slot slot) {
                return this.visitorsInEachSlot.get(slot);
        }

}
