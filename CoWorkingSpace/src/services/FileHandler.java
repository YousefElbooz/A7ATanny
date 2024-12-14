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
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length < 8) {
                    System.out.println("Invalid visitor data: " + line);
                    continue;
                }
                String name = data[0];
                String password = data[1];
                int id = Integer.parseInt(data[2]);
                String type = data[3];
                int hoursReserved = Integer.parseInt(data[4]);
                int hoursRewarded = Integer.parseInt(data[5]);
                double totalFees = Double.parseDouble(data[6]);
                double penaltyFees = Double.parseDouble(data[7]);
                Visitor visitor = new Visitor(name, password, id, type, hoursReserved);
                visitor.setPenaltyFees(penaltyFees);
                visitor.setHoursRewarded(hoursRewarded);
                visitor.setTotalFees(totalFees);
                visitorsData.add(visitor);
            }
        } catch (IOException e) {
            System.out.println("Error loading visitors: " + e.getMessage());
        }
        return visitorsData;
    }

    public static void saveVisitors(String fileName, ArrayList<Visitor> visitors) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for(Visitor visitor : visitors) {
                writer.write(visitor.getName() + "," + 
                             visitor.getPassword() + "," + 
                             visitor.getId() + "," + 
                             visitor.getType() + "," + 
                             visitor.getHoursReserved() + "," + 
                             visitor.getHoursRewarded() + "," + 
                             visitor.getTotalFees() + "," + 
                             visitor.getPenaltyFees());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error Saving Visitors: " + e.getMessage());
        }
    }
    //------->Visitor Load and Save

    //------->Room and Slots Load and Save
    public static ArrayList<Room> loadRooms(String filePath, ArrayList<Visitor> visitors) {
        ArrayList<Room> rooms = new ArrayList<>();
        Room currentRoom = null; 
        Slot currentSlot = null;
        Visitor currentVisitor = null;
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;

            while ((line = br.readLine()) != null) {
                line = line.trim(); 
                if (line.isEmpty()) {
                    continue;
                }
                String[] parts = line.split(",");
                if (parts.length >= 3 && isRoomLine(line)) {
                    String type = parts[0].trim();
                    String name = parts[1].trim();
                    int id = Integer.parseInt(parts[2].trim());
                    if (currentRoom != null) {
                        rooms.add(currentRoom);
                    }
                    switch(type) {
                        case "General":
                            currentRoom = new GeneralRoom(name, id);
                            break;
                        case "Meeting":
                            currentRoom = new MeetingRoom(name, id);
                            break;
                        case "Teaching":
                            if (parts.length < 6) {
                                System.out.println("Invalid teaching room data: " + line);
                                currentRoom = null;
                                break;
                            }
                            String projectorType = parts[3].trim();
                            String boardType = parts[4].trim();
                            String instructorName = parts[5].trim();
                            currentRoom = new TeachingRoom(name, id, projectorType, boardType, instructorName);
                            break;
                        default:
                            System.out.println("Unknown room type: " + type);
                            currentRoom = null;
                    }
                }    
                else if (parts.length == 4 && isSlotLine(line)) {
                    String slotDate = parts[0].trim();
                    String slotTime = parts[1].trim();
                    double slotFees = Double.parseDouble(parts[2].trim());
                    boolean isAvailable = Boolean.parseBoolean(parts[3].trim());
                    if (currentRoom != null) {
                        currentSlot = new Slot(slotTime, slotDate, slotFees, isAvailable);
                        currentRoom.addSlotVisitors(currentSlot);
                    }
                } else if (parts.length == 5) {
                    String name = parts[0].trim();
                    String password = parts[1].trim();
                    int id = Integer.parseInt(parts[2].trim());
                    String type = parts[3].trim();
                    int hoursReserved = Integer.parseInt(parts[4].trim());
                    if(currentSlot != null){
                        for (Visitor visitor : visitors) {
                            if(visitor.getName().equals(name) && 
                               visitor.getId() == id && 
                               visitor.getHoursReserved() == hoursReserved &&
                               visitor.getPassword().equals(password) && 
                               visitor.getType().equals(type)){
                                currentVisitor = visitor;
                                break;
                            }
                        }
                        if(currentVisitor != null) {
                            currentRoom.addSlotVisitors(currentVisitor, currentSlot);
                        }
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
                                teachingRoom.getProjectorType() + "," + 
                                teachingRoom.getBoardType() + "," +
                                teachingRoom.getInstructorName());
                }
                writer.newLine();
                for (Slot slot : room.getVisitorsInEachSlot().keySet()) {
                    writer.write(slot.getDate() + "," + slot.getTime() + "," + slot.getFees() + "," + slot.isAvailable());
                    writer.newLine();
                    if(!slot.isAvailable()){
                        ArrayList<Visitor> slotVisitors = room.getSlotVisitors(slot);
                        if(slotVisitors != null){
                            for (Visitor visitor: slotVisitors) {
                                writer.write(visitor.getName() + "," + 
                                             visitor.getPassword() + "," + 
                                             visitor.getId() + "," + 
                                             visitor.getType() + "," + 
                                             visitor.getHoursReserved());
                                writer.newLine();
                            }
                        }
                    }
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
