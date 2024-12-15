package model;

import java.util.ArrayList;
import java.util.HashMap;

public class TeachingRoom extends Room {
        protected int numberOfVisitors;
        protected String projectorType;
        protected String boardType;
        protected String instructorName ;
        protected HashMap<Slot,ArrayList<Visitor>> visitorsInEachSlot;
        protected int maxNumber =20;

        public int getMaxNumber() {
            return maxNumber;
        }

        public TeachingRoom(String name, int ID, String projectorType, String boardType, String instructorName){
            super(name,ID);
            this.projectorType = projectorType;
            this.boardType = boardType;
            this.instructorName = instructorName;
            this.visitorsInEachSlot = new HashMap<>();

        }

        @Override
        public void setID(int iD) {
            this.ID = iD;
        }
    
        @Override
        public void setName(String name) {
            this.name = name;
        }

        public void setBoardType(String boardType) {
            this.boardType = boardType;
        }
        public void setInstructorName(String instructorName) {
            this.instructorName = instructorName;
        }

        public void setNumberOfVisitors(int numberOfVisitors) {
            this.numberOfVisitors = numberOfVisitors;
        }
        public void setProjectorType(String projectorType) {
            this.projectorType = projectorType;
        }
        



        @Override
        public void addSlotVisitors(Slot slot){
            this.visitorsInEachSlot.put(slot, null);
        }

        @Override
        public HashMap<Slot, ArrayList<Visitor>> getVisitorsInEachSlot() {
        return visitorsInEachSlot;
        }
        
        @Override
        public boolean addSlotVisitors(ArrayList<Visitor>visitors,Slot slot){
            if((this.visitorsInEachSlot.get(slot) == null) && visitors.size() <= maxNumber){
                this.visitorsInEachSlot.put(slot, visitors);
                return true;
            }else if((this.visitorsInEachSlot.get(slot).size() <= maxNumber) && (this.visitorsInEachSlot.get(slot).size() + visitors.size() <= maxNumber)){
                    this.visitorsInEachSlot.put(slot, visitors); 
                    return true;
            }else{
                    System.out.println("Sorry, Room is full.");
                    return false;
            }
        }
    
        @Override
        public boolean addSlotVisitors(Visitor visitor,Slot slot){
            ArrayList<Visitor> a =new ArrayList<>();
            if((this.visitorsInEachSlot.get(slot) == null)){
                a.add(visitor);
                this.visitorsInEachSlot.put(slot, a);
                return true;
            }else if(this.visitorsInEachSlot.get(slot).size() < maxNumber){
                a= this.visitorsInEachSlot.get(slot);
                a.add(visitor);    
                this.visitorsInEachSlot.put(slot, a);
                return true;
            }else{
                System.out.println("Sorry, Room is full.");
                return false;
    
            }
        }

        @Override
        public void displayDetails(){
            System.out.println("Name: "+ name + " Id: "+ ID + " Projector Type: "+ projectorType+ " Board Type: "+ boardType+ " Instructor Name: "+ instructorName );
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
        public int getNumberOfVisitors(Slot slot) {
            return this.visitorsInEachSlot.get(slot).size();
        }

        @Override
        public ArrayList<Visitor> getSlotVisitors(Slot slot) {
            return this.visitorsInEachSlot.get(slot);
        }

        public String getBoardType() {
            return boardType;
        }

        public String getInstructorName() {
            return instructorName;
        }

        public String getProjectorType() {
            return projectorType;
        }


}
