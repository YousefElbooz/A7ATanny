package services;

import java.io.*;
import java.util.ArrayList;

import model.*;

public class FileHandler {
    //------->Visitor Load and Save
    public static ArrayList<Visitor> loadVisitors(String fileName){
        ArrayList<Visitor> visitorsData = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line =reader.readLine()) !=null) {
                String[] data = line.split(",");
                String name = data[0];
                String password = data[1];
                int id = Integer.parseInt(data[2]);
                String type = data[3];
                int hoursReserved = Integer.parseInt(data[4]);
                double totalFees = Double.parseDouble(data[5]);
                double penaltyFees = Double.parseDouble(data[6]);
                Visitor visitor = new Visitor(name, password, id, type,hoursReserved) ;
                visitor.setPenaltyFees(penaltyFees);
                visitor.setTotalFees(totalFees);
                visitorsData.add(visitor);
            }
        } catch (IOException e) {
            System.out.println("Error loading visitors: " + e.getMessage() );
        }
        return visitorsData;
    }
    public static void saveVisitors(String fileName, ArrayList<Visitor> visitors) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for(Visitor visitor : visitors) {
                writer.write(visitor.getName() + "," + visitor.getPassword() + "," + visitor.getId() + "," + visitor.getType()+","+visitor.getHoursReserved()+","+visitor.getTotalFees()+","+visitor.getPenaltyFees());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error Saving Visitors: " + e.getMessage());
        }
    }
    //------->Visitor Load and Save

    //------->Room and Slots Load and Save
    public static ArrayList<Room> loadRooms(String filePath,ArrayList<Visitor> visitors) {
        ArrayList<Room> rooms = new ArrayList<>();
        Room currentRoom = null; 
        Slot currentSlot = null;
        Visitor currentVisitors = null;
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
    
            while ((line = br.readLine()) != null) {
                line = line.trim(); 
                if (line.isEmpty()) {
                    continue;
                }
                if (line.split(",").length >= 3 && isRoomLine(line)) {
                    String[] data = line.split(",");
                    String type = data[0].trim();
                    String name = data[1].trim();
                    int id = Integer.parseInt(data[2].trim());
                    if (currentRoom != null) {
                        rooms.add(currentRoom);
                    }
                    if (type.equals("General")) {
                        currentRoom = new GeneralRoom(name, id);
                    } else if (type.equals("Meeting")) {
                        currentRoom = new MeetingRoom(name, id);
                    } else if (type.equals("Teaching")) {
                        String projectorType = data[3].trim();
                        String boardType = data[4].trim();
                        String instructorName = data[5].trim();
                        currentRoom = new TeachingRoom(name, id, projectorType, boardType, instructorName);
                    } else {
                        System.out.println("Unknown room type: " + type);
                        continue; 
                    }
                }    
                else if (line.split(",").length == 4 && isSlotLine(line)) {
                    String[] slotData = line.split(",");
                    String slotDate = slotData[0].trim();
                    String slotTime = slotData[1].trim();
                    double slotFees = Double.parseDouble(slotData[2].trim());
                    boolean isAvailable = Boolean.parseBoolean(slotData[3].trim());
                    if (currentRoom != null) {
                        currentSlot = new Slot(slotTime, slotDate, slotFees, isAvailable);
                        currentRoom.addSlotVisitors(currentSlot);
                    }
                }else{
                    String[] data = line.split(",");
                    String name = data[0];
                    String password = data[1];
                    int id = Integer.parseInt(data[2]);
                    String type = data[3];
                    int hoursReserved = Integer.parseInt(data[4]);
                    if(currentSlot!=null){
                        for (Visitor visitor : visitors) {
                            if(visitor.getName().equals(name)&&visitor.getId()==id&&visitor.getHoursReserved()==hoursReserved
                            &&visitor.getPassword().equals(password)&&visitor.getType().equals(type)){
                                currentVisitors= visitor;
                            }
                        }
                        currentRoom.addSlotVisitors(currentVisitors, currentSlot);
                    }
                }
            }
            if (currentRoom != null) {
                rooms.add(currentRoom);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }    
        return rooms;
    }
    public static void saveRooms(String fileName, ArrayList<Room> rooms) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (Room room : rooms) {
                if (room instanceof GeneralRoom) {
                    writer.write("General," + room.getName() + "," + room.getID());
                } else if (room instanceof MeetingRoom) {
                    writer.write("Meeting," + room.getName() + "," + room.getID());
                } else if (room instanceof TeachingRoom) {
                    TeachingRoom teachingRoom = (TeachingRoom) room;
                    writer.write("Teaching," + room.getName() + "," + room.getID() + "," +
                            teachingRoom.getProjectorType() + "," + teachingRoom.getBoardType() + "," +
                            teachingRoom.getInstructorName());
                }
                writer.newLine();
                for (Slot slot : room.getVisitorsInEachSlot().keySet()) {
                    writer.write(slot.getDate() + "," + slot.getTime() + "," + slot.getFees() + "," + slot.isAvailable());
                    if(!slot.isAvailable()){
                        writer.newLine();
                        if(room.getSlotVisitors(slot)!=null){
                            for (Visitor  visitor: room.getSlotVisitors(slot)) {
                                writer.write(visitor.getName() + "," + visitor.getPassword() + "," + visitor.getId() + "," + visitor.getType()+","+visitor.getHoursReserved());
                            }
                        }
                    }
                    writer.newLine();
                }

            }
        } catch (IOException e) {
            System.out.println("Error saving rooms: " + e.getMessage());
        }
    }
    //------->Room and Slots Load and Save



    private static boolean isRoomLine(String line) {
        return line.contains("Room");
    }
    private static boolean isSlotLine(String line) {
        return line.contains(":");
    }
    

}