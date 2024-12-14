package services;

import model.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;

/*
    - SystemManager()

    - visitorMenu(Scanner scanner)
    - findVisitorByName(String name)
    - loginVisitor(Scanner scanner)
    - registerVisitor(Scanner scanner)
    - visitorOperationsMenu(Visitor visitor, Scanner scanner)
    - makeReservation(Visitor visitor, Scanner scanner)
    - cancelReservation(Visitor visitor, Scanner scanner)
    - updateReservation(Visitor visitor, Scanner scanner)
    - displayAvailableRoomsAndSlots(Visitor visitor)
    - applyRewardSystem(Visitor visitor)

    - adminMenu(Scanner scanner)
    - adminOptions(Scanner scanner)
    - addSlot(Scanner scanner)
    - deleteEntity(Scanner scanner)
        - deleteVisitor(Scanner scanner)
        - deleteRoom(Scanner scanner)
        - deleteSlot(Scanner scanner)
    - displayAllAvailableSlots()
    - displayAllVisitors()
    - displayAllRooms()
    - displayAllInstructors()
    - calculateTotalFees()
    - updateEntity(Scanner scanner)
        - updateVisitor(Scanner scanner)
        - updateRoom(Scanner scanner)
        - updateSlot(Scanner scanner)
*/

public class SystemManager {
    protected ArrayList<Visitor> visitors;
    private Admin admin;
    protected ArrayList<Room> rooms;

    public SystemManager() {
        visitors = FileHandler.loadVisitors("CoWorkingSpace/src/services/visitors.txt");
        rooms = FileHandler.loadRooms("CoWorkingSpace/src/services/rooms.txt", visitors);
        admin = new Admin("admin", "admin");
        if (rooms.isEmpty()) {
            initializeRooms();
        }
    }

    private void initializeRooms() {
        Room room = new GeneralRoom("General Room 1", 1);
        room.addSlotVisitors(new Slot("10:00", "2024-12-1", 15, true));
        rooms.add(room);

        room = new GeneralRoom("General Room 2", 2);
        room.addSlotVisitors(new Slot("10:00", "2024-12-1", 15, true));
        rooms.add(room);

        room = new MeetingRoom("Meeting Room 1", 3);
        room.addSlotVisitors(new Slot("10:00", "2024-12-1", 15, true));
        rooms.add(room);

        room = new MeetingRoom("Meeting Room 2", 4);
        room.addSlotVisitors(new Slot("10:00", "2024-12-1", 15, true));
        rooms.add(room);

        room = new TeachingRoom("Teaching Room 1", 5, "4K", "Whiteboard", "Ali");
        room.addSlotVisitors(new Slot("10:00", "2024-12-1", 15, true));
        rooms.add(room);
    }

    public void visitorMenu(Scanner scanner) {
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.println("3. Back to Main Menu");
        System.out.print("Choose an option: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1 -> loginVisitor(scanner);
            case 2 -> registerVisitor(scanner);
            case 3 -> System.out.println("Returning to main menu...");
            default -> System.out.println("Invalid option! Please try again.");
        }
        savedata();
    }

    public Visitor findVisitorByName(String name) {
        for (Visitor visitor : visitors) {
            if (visitor.getName().equalsIgnoreCase(name)) return visitor;
        }
        return null;
    }

    private void loginVisitor(Scanner scanner) {
        System.out.print("Enter your name: ");
        String name = scanner.nextLine();
        System.out.print("Enter your password: ");
        String password = scanner.nextLine();

        Visitor visitor = findVisitorByName(name);
        if (visitor != null && visitor.getPassword().equals(password)) {
            System.out.println("Welcome back, " + visitor.getName() + "!");
            visitorOperationsMenu(visitor, scanner);
        } else {
            System.out.println("Invalid credentials. Please try again or register first.");
        }
    }

    private void registerVisitor(Scanner scanner) {
        System.out.print("Enter your name: ");
        String name = scanner.nextLine();
        System.out.print("Create a password: ");
        String password = scanner.nextLine();

        System.out.println("Select your type:");
        System.out.println("1. General");
        System.out.println("2. Formal");
        System.out.println("3. Instructor");
        System.out.print("Choose an option: ");
        int typeChoice = scanner.nextInt();
        scanner.nextLine();

        String type = switch (typeChoice) {
            case 1 -> "general";
            case 2 -> "formal";
            case 3 -> "instructor";
            default -> {
                System.out.println("Invalid choice! Defaulting to 'general'.");
                yield "general";
            }
        };

        Visitor newVisitor = new Visitor(name, password, visitors.size() + 1, type);
        visitors.add(newVisitor);

        System.out.println("Registration successful! You can now log in.");
    }

    private void visitorOperationsMenu(Visitor visitor, Scanner scanner) {
        while (true) {
            System.out.println("1. Make Reservation");
            System.out.println("2. Cancel Reservation");
            System.out.println("3. Update Reservation");
            System.out.println("4. Display Available Slots");
            System.out.println("5. Display Leader Board");
            System.out.println("6. Logout");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> makeReservation(visitor, scanner);
                case 2 -> cancelReservation(visitor, scanner);
                case 3 -> updateReservation(visitor, scanner);
                case 4 -> displayAvailableRoomsAndSlots(visitor);
                case 5 -> LeaderBoardForVisitor();
                case 6 -> {
                    System.out.println("Logging out...");
                    return;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
            savedata();
        }
    }

    public void LeaderBoardForVisitor(){
            visitors.sort(Comparator.comparingInt(Visitor::getHoursReserved).reversed());

            for (Visitor visitor : visitors) {
                visitor.displayDetails();
            }
    }

    public void makeReservation(Visitor visitor, Scanner scanner) {
        ArrayList<Room> availableRooms = displayAvailableRoomsAndSlots(visitor);
        if (availableRooms.isEmpty()) {
            System.out.println("No available rooms or slots for your type of visit.");
            return;
        }
        System.out.print("Enter room ID to reserve: ");
        int roomId = scanner.nextInt();
        scanner.nextLine();
        Room selectedRoom = null;
        for (Room room : availableRooms) {
            if (room.getID() == roomId) {
                selectedRoom = room;
                break;
            }
        }
        if (selectedRoom == null) {
            System.out.println("Invalid room ID. Please try again.");
            return;
        }
        ArrayList<Slot> availableSlots = new ArrayList<>();
        for (Slot slot : selectedRoom.getVisitorsInEachSlot().keySet()) {
            if (slot.isAvailable()) {
                availableSlots.add(slot);
                System.out.println("  Slot: " + slot.getDate() + " at " + slot.getTime());
            }
        }
        if (availableSlots.isEmpty()) {
            System.out.println("No available slots for this room.");
            return;
        }
        System.out.print("Enter the date (yyyy-mm-dd) of the slot you want to reserve: ");
        String selectedDate = scanner.nextLine();
        System.out.print("Enter the time (hh:mm) of the slot you want to reserve: ");
        String selectedTime = scanner.nextLine();
        Slot selectedSlot = null;
        for (Slot slot : availableSlots) {
            if (slot.getDate().equals(selectedDate) && slot.getTime().equals(selectedTime)) {
                selectedSlot = slot;
                break;
            }
        }
        if (selectedSlot == null) {
            System.out.println("Invalid slot. Please try again.");
            return;
        }    
        boolean useFreeHour = false;
        if (visitor.getHoursRewarded() > 0) { // Correct method name
            while (true) { // Loop until valid input
                System.out.print("You have " + visitor.getHoursRewarded() + " free hour(s). Do you want to use a free hour for this reservation? (yes/no): ");
                String response = scanner.nextLine().trim().toLowerCase();
    
                if (response.equals("yes") || response.equals("y")) {
                    useFreeHour = true;
                    visitor.decrementHoursRewarded(1);
                    selectedRoom.addSlotVisitors(visitor, selectedSlot);
                    System.out.println("A free hour has been applied to your reservation.");
                    break;
                } else if (response.equals("no") || response.equals("n")) {
                    useFreeHour = false;
                    break;
                } else {
                    System.out.println("Invalid input. Please enter 'yes' or 'no'.");
                }
            }
        }
    
        selectedSlot.setAvailable(false);
        selectedRoom.addSlotVisitors(visitor, selectedSlot);
        visitor.incrementHoursReserved(1);
        if (!useFreeHour) {
            visitor.setTotalFees(visitor.getTotalFees() + selectedSlot.getFees());
            System.out.println("Fees of $" + selectedSlot.getFees() + " have been added to your account.");
        } else {
            System.out.println("You have used a free hour. No fees charged for this reservation.");
        }
        System.out.println("Reservation successful for " + selectedRoom.getName() + " on " + selectedDate + " at " + selectedTime);
        applyRewardSystem(visitor);
    }

    public void savedata() {
        FileHandler.saveVisitors("CoWorkingSpace/src/services/visitors.txt", visitors);
        FileHandler.saveRooms("CoWorkingSpace/src/services/rooms.txt", rooms);
    }

    public void cancelReservation(Visitor visitor, Scanner scanner) {
        ArrayList<Room> roomsWithReservations = new ArrayList<>();

        for (Room room : rooms) {
            for (Slot slot : room.getVisitorsInEachSlot().keySet()) {
                if (!slot.isAvailable() && room.getSlotVisitors(slot).contains(visitor)) {
                    roomsWithReservations.add(room);
                    break;
                }
            }
        }
        if (roomsWithReservations.isEmpty()) {
            System.out.println("You don't have any reservations to cancel.");
            return;
        }

        System.out.println("Rooms where you have reservations:");
        for (Room room : roomsWithReservations) {
            System.out.println("Room: " + room.getName() + " (ID: " + room.getID() + ")");
        }

        System.out.print("Enter room ID to cancel reservation: ");
        int roomId = scanner.nextInt();
        scanner.nextLine();

        Room selectedRoom = null;
        for (Room room : roomsWithReservations) {
            if (room.getID() == roomId) {
                selectedRoom = room;
                break;
            }
        }

        if (selectedRoom == null) {
            System.out.println("Invalid room ID. Please try again.");
            return;
        }

        ArrayList<Slot> reservedSlots = new ArrayList<>();
        for (Slot slot : selectedRoom.getVisitorsInEachSlot().keySet()) {
            if (!slot.isAvailable() && selectedRoom.getSlotVisitors(slot).contains(visitor)) {
                reservedSlots.add(slot);
                System.out.println("Reserved Slot: " + slot.getDate() + " at " + slot.getTime());
            }
        }

        if (reservedSlots.isEmpty()) {
            System.out.println("You don't have any reserved slots in this room.");
            return;
        }

        System.out.print("Enter the date (yyyy-mm-dd) of the slot you want to cancel: ");
        String selectedDate = scanner.nextLine();
        System.out.print("Enter the time (hh:mm) of the slot you want to cancel: ");
        String selectedTime = scanner.nextLine();

        Slot selectedSlot = null;
        for (Slot slot : reservedSlots) {
            if (slot.getDate().equals(selectedDate) && slot.getTime().equals(selectedTime)) {
                selectedSlot = slot;
                break;
            }
        }

        if (selectedSlot == null) {
            System.out.println("Invalid slot. Please try again.");
            return;
        }

        selectedSlot.setAvailable(true);
        selectedRoom.removeVisitor(visitor, selectedSlot);
        visitor.decrementHoursReserved(1);
        visitor.setPenaltyFees(selectedSlot.getFees() * 0.10);
        visitor.setTotalFees(visitor.getTotalFees() - selectedSlot.getFees());

        System.out.println("Reservation cancelled for " + selectedRoom.getName() + " on " + selectedDate + " at " + selectedTime);
    }

    public void updateReservation(Visitor visitor, Scanner scanner) {
        ArrayList<Room> roomsWithReservations = new ArrayList<>();

        for (Room room : rooms) {
            for (Slot slot : room.getVisitorsInEachSlot().keySet()) {
                if (!slot.isAvailable() && room.getSlotVisitors(slot).contains(visitor)) {
                    roomsWithReservations.add(room);
                    break;
                }
            }
        }

        if (roomsWithReservations.isEmpty()) {
            System.out.println("You don't have any reservations to update.");
            return;
        }

        System.out.println("Rooms where you have reservations:");
        for (Room room : roomsWithReservations) {
            System.out.println("Room: " + room.getName() + " (ID: " + room.getID() + ")");
        }

        System.out.print("Enter room ID to update reservation: ");
        int roomId = scanner.nextInt();
        scanner.nextLine();

        Room selectedRoom = null;
        for (Room room : roomsWithReservations) {
            if (room.getID() == roomId) {
                selectedRoom = room;
                break;
            }
        }

        if (selectedRoom == null) {
            System.out.println("Invalid room ID. Please try again.");
            return;
        }

        ArrayList<Slot> reservedSlots = new ArrayList<>();
        for (Slot slot : selectedRoom.getVisitorsInEachSlot().keySet()) {
            if (!slot.isAvailable() && selectedRoom.getSlotVisitors(slot).contains(visitor)) {
                reservedSlots.add(slot);
                System.out.println("Reserved Slot: " + slot.getDate() + " at " + slot.getTime());
            }
        }

        if (reservedSlots.isEmpty()) {
            System.out.println("You don't have any reserved slots in this room.");
            return;
        }

        System.out.print("Enter the date (yyyy-mm-dd) of the slot you want to update: ");
        String selectedDate = scanner.nextLine();
        System.out.print("Enter the time (hh:mm) of the slot you want to update: ");
        String selectedTime = scanner.nextLine();

        Slot selectedSlot = null;
        for (Slot slot : reservedSlots) {
            if (slot.getDate().equals(selectedDate) && slot.getTime().equals(selectedTime)) {
                selectedSlot = slot;
                break;
            }
        }

        if (selectedSlot == null) {
            System.out.println("Invalid slot. Please try again.");
            return;
        }

        System.out.println("Available slots for " + selectedRoom.getName() + ":");
        for (Slot slot : selectedRoom.getVisitorsInEachSlot().keySet()) {
            if (slot.isAvailable()) {
                System.out.println("  Slot: " + slot.getDate() + " at " + slot.getTime());
            }
        }

        System.out.print("Enter the new date (yyyy-mm-dd) for the slot: ");
        String newDate = scanner.nextLine();
        System.out.print("Enter the new time (hh:mm) for the slot: ");
        String newTime = scanner.nextLine();

        Slot newSlot = null;
        for (Slot slot : selectedRoom.getVisitorsInEachSlot().keySet()) {
            if (slot.getDate().equals(newDate) && slot.getTime().equals(newTime) && slot.isAvailable()) {
                newSlot = slot;
                break;
            }
        }

        if (newSlot == null) {
            System.out.println("The new slot is not available. Please try again.");
            return;
        }

        selectedSlot.setAvailable(true);
        selectedRoom.removeVisitor(visitor, selectedSlot);

        newSlot.setAvailable(false);
        selectedRoom.addSlotVisitors(visitor, newSlot);
        visitor.setPenaltyFees(selectedSlot.getFees() * 0.10);
        visitor.setTotalFees(newSlot.getFees());

        System.out.println("Reservation updated for " + selectedRoom.getName() + " from " + selectedDate + " at " + selectedTime +
                " to " + newDate + " at " + newTime);
    }

    public ArrayList<Room> displayAvailableRoomsAndSlots(Visitor visitor) {
        System.out.println("\nDisplaying available rooms and slots...");
        ArrayList<Room> availableRooms = new ArrayList<>();
        for (Room room : rooms) {
            if ((room instanceof GeneralRoom && visitor.getType().equalsIgnoreCase("general")) ||
                    (room instanceof MeetingRoom && visitor.getType().equalsIgnoreCase("formal")) ||
                    (room instanceof TeachingRoom && visitor.getType().equalsIgnoreCase("instructor"))) {
                boolean roomHasAvailableSlots = false;
                for (Slot slot : room.getVisitorsInEachSlot().keySet()) {
                    if (slot.isAvailable()) {
                        roomHasAvailableSlots = true;
                        break;
                    }
                }
                if (roomHasAvailableSlots) {
                    availableRooms.add(room);
                    room.displayDetails();
                    for (Slot slot : room.getVisitorsInEachSlot().keySet()) {
                        if (slot.isAvailable()) {
                            System.out.println("  Slot: " + slot.getDate() + " at " + slot.getTime());
                        }
                    }
                }
            }
        }
        return availableRooms;
    }

    public void applyRewardSystem(Visitor visitor) {
        int generalVisitorRewardThreshold = 6;
        int instructorVisitorRewardThreshold = 12;
        int rewardHours = 0;
        if (visitor.getType().equalsIgnoreCase("instructor")) {
            if(visitor.getHoursReserved() % instructorVisitorRewardThreshold==0&&visitor.getHoursReserved()>0){rewardHours++;};
        } else {
            if(visitor.getHoursReserved() % generalVisitorRewardThreshold==0&&visitor.getHoursReserved()>0){rewardHours++;};

        }

        if (rewardHours > 0) {
            visitor.addRewardHours(rewardHours);
            System.out.println("You have earned " + rewardHours + " free hours for your reservations!");
        } else {
            System.out.println("No rewards earned yet.");
        }

        FileHandler.saveVisitors("CoWorkingSpace/src/services/visitors.txt", visitors);
    }

    public void adminMenu(Scanner scanner) {
        System.out.print("Enter admin name: ");
        String name = scanner.nextLine();
        System.out.print("Enter admin password: ");
        String password = scanner.nextLine();

        if (admin.getName().equals(name) && admin.getPassword().equals(password)) {
            System.out.println("Admin logged in successfully!");
            adminOptions(scanner);
        } else {
            System.out.println("Invalid admin credentials.");
        }
    }

    public void adminOptions(Scanner scanner) {
        while (true) {
            System.out.println("\n--- Admin Menu ---");
            System.out.println("1. Add Slot");
            System.out.println("2. Delete Entity");
            System.out.println("3. Display Available Slots");
            System.out.println("4. Display All Visitors");
            System.out.println("5. Display All Rooms");
            System.out.println("6. Display All Instructors");
            System.out.println("7. Calculate Total Fees");
            System.out.println("8. Update Entity");
            System.out.println("9. Logout");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> addSlot(scanner);
                case 2 -> deleteEntity(scanner);
                case 3 -> displayAllAvailableSlots();
                case 4 -> displayAllVisitors();
                case 5 -> displayAllRooms();
                case 6 -> displayAllInstructors();
                case 7 -> calculateTotalFees();
                case 8 -> updateEntity(scanner);
                case 9 -> {
                    System.out.println("Logging out...");
                    return;
                }
                default -> System.out.println("Invalid choice. Try again.");
            }
            savedata();
        }
    }

    public void addSlot(Scanner scanner) {
        System.out.println("Enter the fees of the slot you want to create: ");
        double selectedFees = scanner.nextDouble();
        scanner.nextLine();

        System.out.println("Enter the time (hh:mm) of the slot you want to create: ");
        String time = scanner.nextLine();

        System.out.println("Enter the date (yyyy-mm-dd) of the slot you want to create: ");
        String selectedDate = scanner.nextLine();

        Slot newSlot = new Slot(time, selectedDate, selectedFees, true);
        System.out.println("Enter the ID of the Room you want to create a slot in: ");
        int roomID = scanner.nextInt();
        scanner.nextLine();

        Room selectedRoom = null;
        for (Room room : rooms) {
            if (room.getID() == roomID) {
                selectedRoom = room;
                break;
            }
        }

        if (selectedRoom == null) {
            System.out.println("Invalid Room ID. Slot creation failed.");
            return;
        }

        selectedRoom.addSlotVisitors(newSlot);
        System.out.println("Slot added successfully to " + selectedRoom.getName());

        FileHandler.saveRooms("CoWorkingSpace/src/services/rooms.txt", rooms);
    }

    public void deleteEntity(Scanner scanner) {
        System.out.println("\n--- Delete Entity ---");
        System.out.println("1. Delete Visitor");
        System.out.println("2. Delete Room");
        System.out.println("3. Delete Slot");
        System.out.print("Choose an option: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1 -> deleteVisitor(scanner);
            case 2 -> deleteRoom(scanner);
            case 3 -> deleteSlot(scanner);
            default -> System.out.println("Invalid choice. Returning to Admin Menu.");
        }
    }

    private void deleteVisitor(Scanner scanner) {
        System.out.print("Enter Visitor ID to delete: ");
        int visitorId = scanner.nextInt();
        scanner.nextLine();

        Visitor visitorToDelete = null;
        for (Visitor visitor : visitors) {
            if (visitor.getId() == visitorId) {
                visitorToDelete = visitor;
                break;
            }
        }

        if (visitorToDelete == null) {
            System.out.println("Visitor not found.");
            return;
        }

        for (Room room : rooms) {
            for (Slot slot : room.getVisitorsInEachSlot().keySet()) {
                if (!slot.isAvailable()) {
                    room.removeVisitor(visitorToDelete, slot);
                }
            }
        }

        visitors.remove(visitorToDelete);
        System.out.println("Visitor deleted successfully.");
    }

    private void deleteRoom(Scanner scanner) {
        System.out.print("Enter Room ID to delete: ");
        int roomId = scanner.nextInt();
        scanner.nextLine();

        Room roomToDelete = null;
        for (Room room : rooms) {
            if (room.getID() == roomId) {
                roomToDelete = room;
                break;
            }
        }

        if (roomToDelete == null) {
            System.out.println("Room not found.");
            return;
        }

        rooms.remove(roomToDelete);
        System.out.println("Room deleted successfully.");
    }

    private void deleteSlot(Scanner scanner) {
        System.out.print("Enter Room ID where the slot is located: ");
        int roomId = scanner.nextInt();
        scanner.nextLine();

        Room selectedRoom = null;
        for (Room room : rooms) {
            if (room.getID() == roomId) {
                selectedRoom = room;
                break;
            }
        }

        if (selectedRoom == null) {
            System.out.println("Room not found.");
            return;
        }

        System.out.println("Available Slots in " + selectedRoom.getName() + ":");
        for (Slot slot : selectedRoom.getVisitorsInEachSlot().keySet()) {
            System.out.println("Date: " + slot.getDate() + ", Time: " + slot.getTime());
        }

        System.out.print("Enter the date (yyyy-mm-dd) of the slot to delete: ");
        String date = scanner.nextLine();
        System.out.print("Enter the time (hh:mm) of the slot to delete: ");
        String time = scanner.nextLine();

        Slot slotToDelete = null;
        for (Slot slot : selectedRoom.getVisitorsInEachSlot().keySet()) {
            if (slot.getDate().equals(date) && slot.getTime().equals(time)) {
                slotToDelete = slot;
                break;
            }
        }

        if (slotToDelete == null) {
            System.out.println("Slot not found.");
            return;
        }

        if (!slotToDelete.isAvailable()) {
            System.out.println("Cannot delete a slot that is currently reserved.");
            return;
        }

        selectedRoom.getVisitorsInEachSlot().remove(slotToDelete);
        System.out.println("Slot deleted successfully.");

        FileHandler.saveRooms("CoWorkingSpace/src/services/rooms.txt", rooms);
    }

    public void displayAllAvailableSlots() {
        System.out.println("\n--- All Available Slots ---");
        for (Room room : rooms) {
            System.out.println("Room Type: " + room.getClass().getSimpleName() + ", Room ID: " + room.getID() + ", Room Name: " + room.getName());
            for (Slot slot : room.getVisitorsInEachSlot().keySet()) {
                if (slot.isAvailable()) {
                    System.out.println("  Date: " + slot.getDate() + ", Time: " + slot.getTime() + ", Fees: $" + slot.getFees());
                }
            }
        }
    }

    public void displayAllVisitors() {
        System.out.println("\n--- All Visitors ---");
        for (Visitor visitor : visitors) {
            System.out.println("ID: " + visitor.getId() + ", Name: " + visitor.getName() + ", Type: " + visitor.getType() + ", Total Fees: $" + visitor.getTotalFees());
        }
    }

    public void displayAllRooms() {
        System.out.println("\n--- All Rooms ---");
        for (Room room : rooms) {
            room.displayDetails();
            System.out.println();
        }
    }

    public void displayAllInstructors() {
        System.out.println("\n--- All Instructors ---");
        for (Visitor visitor : visitors) {
            if (visitor.getType().equalsIgnoreCase("instructor")) {
                System.out.println("ID: " + visitor.getId() + ", Name: " + visitor.getName());
            }
        }
    }

    public void calculateTotalFees() {
        double totalGeneral = 0;
        double totalMeeting = 0;
        double totalTeaching = 0;

        for (Room room : rooms) {
            for (Slot slot : room.getVisitorsInEachSlot().keySet()) {
                if (!slot.isAvailable()) {
                    if (room instanceof GeneralRoom) {
                        totalGeneral += slot.getFees();
                    } else if (room instanceof MeetingRoom) {
                        totalMeeting += slot.getFees();
                    } else if (room instanceof TeachingRoom) {
                        totalTeaching += slot.getFees();
                    }
                }
            }
        }

        System.out.println("\n--- Total Fees Collected ---");
        System.out.println("General Rooms: $" + totalGeneral);
        System.out.println("Meeting Rooms: $" + totalMeeting);
        System.out.println("Teaching Rooms: $" + totalTeaching);
    }

    public void updateEntity(Scanner scanner) {
        System.out.println("\n--- Update Entity ---");
        System.out.println("1. Update Visitor");
        System.out.println("2. Update Room");
        System.out.println("3. Update Slot");
        System.out.print("Choose an option: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1 -> updateVisitor(scanner);
            case 2 -> updateRoom(scanner);
            case 3 -> updateSlot(scanner);
            default -> System.out.println("Invalid choice. Returning to Admin Menu.");
        }
    }

    private void updateVisitor(Scanner scanner) {
        System.out.print("Enter Visitor ID to update: ");
        int visitorId = scanner.nextInt();
        scanner.nextLine();

        Visitor visitorToUpdate = null;
        for (Visitor visitor : visitors) {
            if (visitor.getId() == visitorId) {
                visitorToUpdate = visitor;
                break;
            }
        }

        if (visitorToUpdate == null) {
            System.out.println("Visitor not found.");
            return;
        }

        System.out.println("Current Name: " + visitorToUpdate.getName());
        System.out.print("Enter new name (or press Enter to keep current): ");
        String newName = scanner.nextLine();
        if (!newName.isEmpty()) {
            visitorToUpdate.setName(newName);
        }

        System.out.println("Current Password: " + visitorToUpdate.getPassword());
        System.out.print("Enter new password (or press Enter to keep current): ");
        String newPassword = scanner.nextLine();
        if (!newPassword.isEmpty()) {
            visitorToUpdate.setPassword(newPassword);
        }

        System.out.println("Current Type: " + visitorToUpdate.getType());
        System.out.println("Select new type (or press Enter to keep current):");
        System.out.println("1. General");
        System.out.println("2. Formal");
        System.out.println("3. Instructor");
        System.out.print("Choose an option: ");
        String typeInput = scanner.nextLine();
        if (!typeInput.isEmpty()) {
            int typeChoice;
            try {
                typeChoice = Integer.parseInt(typeInput);
                String newType = switch (typeChoice) {
                    case 1 -> "general";
                    case 2 -> "formal";
                    case 3 -> "instructor";
                    default -> {
                        System.out.println("Invalid choice! Keeping current type.");
                        yield visitorToUpdate.getType();
                    }
                };
                visitorToUpdate.setType(newType);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Keeping current type.");
            }
        }

        System.out.println("Visitor updated successfully.");
    }

    private void updateRoom(Scanner scanner) {
        System.out.print("Enter Room ID to update: ");
        int roomId = scanner.nextInt();
        scanner.nextLine();

        Room roomToUpdate = null;
        for (Room room : rooms) {
            if (room.getID() == roomId) {
                roomToUpdate = room;
                break;
            }
        }

        if (roomToUpdate == null) {
            System.out.println("Room not found.");
            return;
        }

        System.out.println("Current Room Name: " + roomToUpdate.getName());
        System.out.print("Enter new room name (or press Enter to keep current): ");
        String newName = scanner.nextLine();
        if (!newName.isEmpty()) {
            roomToUpdate.setName(newName);
        }

        if (roomToUpdate instanceof TeachingRoom teachingRoom) {
            System.out.println("Current Projector Type: " + teachingRoom.getProjectorType());
            System.out.print("Enter new projector type (or press Enter to keep current): ");
            String newProjector = scanner.nextLine();
            if (!newProjector.isEmpty()) {
                teachingRoom.setProjectorType(newProjector);
            }

            System.out.println("Current Board Type: " + teachingRoom.getBoardType());
            System.out.print("Enter new board type (or press Enter to keep current): ");
            String newBoard = scanner.nextLine();
            if (!newBoard.isEmpty()) {
                teachingRoom.setBoardType(newBoard);
            }

            System.out.println("Current Instructor Name: " + teachingRoom.getInstructorName());
            System.out.print("Enter new instructor name (or press Enter to keep current): ");
            String newInstructor = scanner.nextLine();
            if (!newInstructor.isEmpty()) {
                teachingRoom.setInstructorName(newInstructor);
            }
        }

        System.out.println("Room updated successfully.");
    }

    private void updateSlot(Scanner scanner) {
        System.out.print("Enter Room ID where the slot is located: ");
        int roomId = scanner.nextInt();
        scanner.nextLine();

        Room selectedRoom = null;
        for (Room room : rooms) {
            if (room.getID() == roomId) {
                selectedRoom = room;
                break;
            }
        }

        if (selectedRoom == null) {
            System.out.println("Room not found.");
            return;
        }

        System.out.println("Available Slots in " + selectedRoom.getName() + ":");
        for (Slot slot : selectedRoom.getVisitorsInEachSlot().keySet()) {
            System.out.println("Date: " + slot.getDate() + ", Time: " + slot.getTime() + ", Fees: $" + slot.getFees());
        }

        System.out.print("Enter the date (yyyy-mm-dd) of the slot to update: ");
        String date = scanner.nextLine();
        System.out.print("Enter the time (hh:mm) of the slot to update: ");
        String time = scanner.nextLine();

        Slot slotToUpdate = null;
        for (Slot slot : selectedRoom.getVisitorsInEachSlot().keySet()) {
            if (slot.getDate().equals(date) && slot.getTime().equals(time)) {
                slotToUpdate = slot;
                break;
            }
        }

        if (slotToUpdate == null) {
            System.out.println("Slot not found.");
            return;
        }

        System.out.println("Current Fees: $" + slotToUpdate.getFees());
        System.out.print("Enter new fees (or press Enter to keep current): ");
        String feesInput = scanner.nextLine();
        if (!feesInput.isEmpty()) {
            try {
                double newFees = Double.parseDouble(feesInput);
                slotToUpdate.setFees(newFees);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Keeping current fees.");
            }
        }

        System.out.println("Current Availability: " + (slotToUpdate.isAvailable() ? "Available" : "Reserved"));
        System.out.print("Enter new availability (true/false) or press Enter to keep current: ");
        String availabilityInput = scanner.nextLine();
        if (!availabilityInput.isEmpty()) {
            if (availabilityInput.equalsIgnoreCase("true") || availabilityInput.equalsIgnoreCase("false")) {
                boolean newAvailability = Boolean.parseBoolean(availabilityInput);
                slotToUpdate.setAvailable(newAvailability);
            } else {
                System.out.println("Invalid input! Keeping current availability.");
            }
        }

        System.out.println("Slot updated successfully.");
    }
}
