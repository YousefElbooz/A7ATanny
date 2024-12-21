package services;

import model.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class SystemManager {

    // -------> Constants
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    // -------> Fields
    protected ArrayList<Visitor> visitors;
    protected ArrayList<Room> rooms;
    private Admin admin;

    /*
        - SystemManager()

        - Visitor Operations
            - visitorMenu(Scanner scanner)
            - findVisitorByName(String name)
            - loginVisitor(Scanner scanner)
            - registerVisitor(Scanner scanner)
            - visitorOperationsMenu(Visitor visitor, Scanner scanner)
            - makeReservation(Visitor visitor, Scanner scanner)
            - cancelReservation(Visitor visitor, Scanner scanner)
            - joinReservation(Visitor visitor, Scanner scanner)
            - updateReservation(Visitor visitor, Scanner scanner)
            - displayAvailableRoomsAndSlots(Visitor visitor)
            - applyRewardSystem(Visitor visitor)
            - LeaderBoardForVisitor()

        - Admin Operations
            - adminMenu(Scanner scanner)
            - adminOptions(Scanner scanner)
            - addSlot(Scanner scanner)
            - deleteEntity(Scanner scanner)
                - deleteVisitor(Scanner scanner)
                - deleteRoom(Scanner scanner)
                - deleteSlot(Scanner scanner)
            - updateEntity(Scanner scanner)
                - updateVisitor(Scanner scanner)
                - updateRoom(Scanner scanner)
                - updateSlot(Scanner scanner)
            - displayAllAvailableSlots()
            - displayAllVisitors()
            - displayAllRooms()
            - displayAllInstructors()
            - calculateTotalFees()
            - LeaderBoardForAdmin(Scanner scanner)
            - emptySafe(Scanner scanner)

        - Utility Methods
            - findRoomById(int id)
            - savedata()
    */

    // -------> Constructor
    public SystemManager() {
        visitors = FileHandler.loadVisitors("CoWorkingSpace/src/services/visitors.txt");
        rooms = FileHandler.loadRooms("CoWorkingSpace/src/services/rooms.txt", visitors);
        admin = new Admin("admin", "admin");
        if (rooms.isEmpty()) {
            initializeRooms();
        }
    }

    // -------> Initialization Methods
    private void initializeRooms() {
        LocalDate initialDate = LocalDate.parse("2024-12-01", DATE_FORMATTER);
        LocalTime initialTime = LocalTime.parse("10:00", TIME_FORMATTER);

        Room room = new GeneralRoom("General Room 1", 1);
        room.addSlotVisitors(new Slot(initialTime, initialDate, 15, true));
        rooms.add(room);

        room = new GeneralRoom("General Room 2", 2);
        room.addSlotVisitors(new Slot(initialTime, initialDate, 15, true));
        rooms.add(room);

        room = new MeetingRoom("Meeting Room 1", 3);
        room.addSlotVisitors(new Slot(initialTime, initialDate, 15, true));
        rooms.add(room);

        room = new MeetingRoom("Meeting Room 2", 4);
        room.addSlotVisitors(new Slot(initialTime, initialDate, 15, true));
        rooms.add(room);

        room = new TeachingRoom("Teaching Room 1", 5, "4K", "Whiteboard", "Ali");
        room.addSlotVisitors(new Slot(initialTime, initialDate, 15, true));
        rooms.add(room);
    }

    // -------> Utility Methods
    public Room findRoomById(int id) {
        for (Room room : rooms) {
            if (room.getID() == id) {
                return room;
            }
        }
        return null;
    }

    public void savedata() {
        FileHandler.saveVisitors("CoWorkingSpace/src/services/visitors.txt", visitors);
        FileHandler.saveRooms("CoWorkingSpace/src/services/rooms.txt", rooms);
    }

    // -------> Visitor Operations

    public void visitorMenu(Scanner scanner) {
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.println("3. Back to Main Menu");
        System.out.print("Choose an option: ");
        String input = scanner.nextLine().trim();
        int choice;

        try {
            choice = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Invalid input! Please enter a number corresponding to the options.");
            return;
        }

        switch (choice) {
            case 1 -> loginVisitor(scanner);
            case 2 -> registerVisitor(scanner);
            case 3 -> System.out.println("Returning to main menu...");
            default -> System.out.println("Invalid option! Please try again.");
        }
    }

    public Visitor findVisitorByName(String name) {
        for (Visitor visitor : visitors) {
            if (visitor.getName().equalsIgnoreCase(name)) {
                return visitor;
            }
        }
        return null;
    }

    private void loginVisitor(Scanner scanner) {
        System.out.print("Enter your name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Enter your password: ");
        String password = scanner.nextLine().trim();

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
        String name = scanner.nextLine().trim();
        System.out.print("Create a password: ");
        String password = scanner.nextLine().trim();

        System.out.println("Select your type:");
        System.out.println("1. General");
        System.out.println("2. Formal");
        System.out.println("3. Instructor");
        System.out.print("Choose an option: ");
        String input = scanner.nextLine().trim();
        int typeChoice;

        try {
            typeChoice = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Invalid choice! Defaulting to 'General'.");
            typeChoice = 1;
        }

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
            System.out.println("\n--- Visitor Operations Menu ---");
            System.out.println("1. Join Reservation");
            System.out.println("2. Make Reservation");
            System.out.println("3. Cancel Reservation");
            System.out.println("4. Update Reservation");
            System.out.println("5. Display Available Slots");
            System.out.println("6. Display Leader Board");
            System.out.println("7. Logout");
            System.out.print("Choose an option: ");
            String input = scanner.nextLine().trim();
            int choice;

            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a number corresponding to the options.");
                continue;
            }

            switch (choice) {
                case 1 -> joinReservation(visitor, scanner);
                case 2 -> makeReservation(visitor, scanner);
                case 3 -> cancelReservation(visitor, scanner);
                case 4 -> updateReservation(visitor, scanner);
                case 5 -> displayAvailableRoomsAndSlots(visitor);
                case 6 -> LeaderBoardForVisitor();
                case 7 -> {
                    System.out.println("Logging out...");
                    return;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
            savedata();
        }
    }

    public void LeaderBoardForVisitor() {
        visitors.sort(Comparator.comparingInt(Visitor::getHoursReserved).reversed());

        System.out.println("\n--- Leader Board ---");
        for (Visitor visitor : visitors) {
            visitor.displayDetails();
        }
    }

    public void joinReservation(Visitor visitor, Scanner scanner) {
        System.out.print("Enter Your Join Code (format: roomId&date&time): ");
        String code = scanner.nextLine().trim();

        String[] data = code.split("&");
        if (data.length != 3) {
            System.out.println("Invalid join code format. Please use 'roomId&date&time'.");
            return;
        }

        int roomId;
        LocalDate slotDate;
        LocalTime slotTime;

        // Parse the join code components with error handling
        try {
            roomId = Integer.parseInt(data[0].trim());
            slotDate = LocalDate.parse(data[1].trim(), DATE_FORMATTER);
            slotTime = LocalTime.parse(data[2].trim(), TIME_FORMATTER);
        } catch (NumberFormatException | DateTimeParseException e) {
            System.out.println("Invalid join code components: " + e.getMessage());
            return;
        }

        Room selectedRoom = findRoomById(roomId);
        if (selectedRoom == null) {
            System.out.println("No room found with ID " + roomId + ".");
            return;
        }

        Slot selectedSlot = null;
        for (Slot slot : selectedRoom.getVisitorsInEachSlot().keySet()) {
            if (slot.getDate().equals(slotDate) && slot.getTime().equals(slotTime)) {
                selectedSlot = slot;
                break;
            }
        }

        if (selectedSlot == null) {
            System.out.println("No slot found on " + slotDate + " at " + slotTime + ".");
            return;
        }

        List<Visitor> slotVisitors = selectedRoom.getSlotVisitors(selectedSlot);
        if (slotVisitors.contains(visitor)) {
            System.out.println("You have already joined this reservation.");
            return;
        }

        boolean added = selectedRoom.addSlotVisitors(visitor, selectedSlot);
        if (added) {
            boolean useFreeHour = false;
            if (visitor.getHoursRewarded() > 0) {
                while (true) {
                    System.out.print("You have " + visitor.getHoursRewarded() + " free hour(s). Do you want to use a free hour for this reservation? (yes/no): ");
                    String response = scanner.nextLine().trim().toLowerCase();

                    if (response.equals("yes") || response.equals("y")) {
                        useFreeHour = true;
                        visitor.decrementHoursRewarded(1);
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

            if (!useFreeHour) {
                double reservationFee = selectedSlot.getFees();
                boolean validChoice = false;

                while (!validChoice) {
                    System.out.println("\nPayment Options:");
                    System.out.println("1. Pay this reservation's fee now.");
                    System.out.println("2. Pay your total accumulated fees now with this reservation fees.");
                    System.out.println("3. Skip payment for now (fees will be added to your account).");
                    System.out.print("Choose an option (1/2/3): ");
                    String paymentChoice = scanner.nextLine().trim();

                    switch (paymentChoice) {
                        case "1":
                            // Pay this reservation's fee
                            visitor.payPenaltyFees();
                            System.out.println("Fees of $" + reservationFee + " have been paid and added to your account.");
                            validChoice = true;
                            break;
                        case "2":
                            // Pay total accumulated fees
                            visitor.setTotalFees(visitor.getTotalFees()+selectedSlot.getFees());
                            double totalFees = visitor.getTotalFees();
                            if (totalFees > 0) {
                                visitor.payTotalFees();
                                System.out.println("Total fees of $" + totalFees + " have been paid and your account balance is now $0.");
                            } else {
                                System.out.println("You have no accumulated fees to pay.");
                            }
                            validChoice = true;
                            break;
                        case "3":
                            // Add reservation fee to total fees
                            visitor.setTotalFees(reservationFee);
                            System.out.println("Fees of $" + reservationFee + " have been added to your account.");
                            validChoice = true;
                            break;
                        default:
                            System.out.println("Invalid choice. Please select option 1, 2, or 3.");
                    }
                }
            } else {
                System.out.println("You have used a free hour. No fees charged for this reservation.");
            }

            visitor.incrementHoursReserved(1);
            System.out.println("Successfully joined the reservation for " + selectedRoom.getName() + " on " +
                    slotDate + " at " + slotTime + ".");
            System.out.println("Your Join Code: " + selectedRoom.getID() + "&" + slotDate + "&" + slotTime);
            applyRewardSystem(visitor);
        } else {
            System.out.println("Failed to join the reservation. The slot might be full.");
        }
    }

    public void makeReservation(Visitor visitor, Scanner scanner) {
        ArrayList<Room> availableRooms = displayAvailableRoomsAndSlots(visitor);
        if (availableRooms.isEmpty()) {
            System.out.println("No available rooms or slots for your type of visit.");
            return;
        }

        System.out.print("Enter room ID to reserve: ");
        String input = scanner.nextLine().trim();
        int roomId;

        try {
            roomId = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Invalid room ID format. Please enter a numeric value.");
            return;
        }

        Room selectedRoom = findRoomById(roomId);
        if (selectedRoom == null) {
            System.out.println("Invalid room ID. Please try again.");
            return;
        }

        ArrayList<Slot> availableSlots = new ArrayList<>();
        System.out.println("Available Slots in " + selectedRoom.getName() + ":");
        for (Slot slot : selectedRoom.getVisitorsInEachSlot().keySet()) {
            if (slot.isAvailable()) {
                availableSlots.add(slot);
                System.out.println("  Slot: " + slot.getDate().format(DATE_FORMATTER) + " at " + slot.getTime().format(TIME_FORMATTER));
            }
        }

        if (availableSlots.isEmpty()) {
            System.out.println("No available slots for this room.");
            return;
        }

        System.out.print("Enter the date (yyyy-mm-dd) of the slot you want to reserve: ");
        String dateInput = scanner.nextLine().trim();
        System.out.print("Enter the time (HH:mm) of the slot you want to reserve: ");
        String timeInput = scanner.nextLine().trim();

        LocalDate selectedDate;
        LocalTime selectedTime;
        try {
            selectedDate = LocalDate.parse(dateInput, DATE_FORMATTER);
            selectedTime = LocalTime.parse(timeInput, TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date/time format: " + e.getMessage());
            return;
        }

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
        if (visitor.getHoursRewarded() > 0) {
            while (true) {
                System.out.print("You have " + visitor.getHoursRewarded() + " free hour(s). Do you want to use a free hour for this reservation? (yes/no): ");
                String response = scanner.nextLine().trim().toLowerCase();

                if (response.equals("yes") || response.equals("y")) {
                    useFreeHour = true;
                    visitor.decrementHoursRewarded(1);
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

        // Reserve the slot
        selectedSlot.setAvailable(false);
        selectedRoom.addSlotVisitors(visitor, selectedSlot);
        visitor.incrementHoursReserved(1);

        if (!useFreeHour) {
            double reservationFee = selectedSlot.getFees();
            boolean validChoice = false;

            while (!validChoice) {
                System.out.println("\nPayment Options:");
                System.out.println("1. Pay this reservation's fee now.");
                System.out.println("2. Pay your total accumulated fees now.");
                System.out.println("3. Skip payment for now (fees will be added to your account).");
                System.out.print("Choose an option (1/2/3): ");
                String paymentChoice = scanner.nextLine().trim();

                switch (paymentChoice) {
                    case "1":
                        // Pay this reservation's fee
                        visitor.payTotalFees();
                        System.out.println("Fees of $" + reservationFee + " have been paid and added to your account.");
                        validChoice = true;
                        break;
                    case "2":
                        // Pay total accumulated fees
                        double totalFees = visitor.getTotalFees();
                        if (totalFees > 0) {
                            visitor.payTotalFees();
                            System.out.println("Total fees of $" + totalFees + " have been paid and your account balance is now $0.");
                        } else {
                            System.out.println("You have no accumulated fees to pay.");
                        }
                        validChoice = true;
                        break;
                    case "3":
                        // Add reservation fee to total fees
                        visitor.setTotalFees(reservationFee);
                        System.out.println("Fees of $" + reservationFee + " have been added to your account.");
                        validChoice = true;
                        break;
                    default:
                        System.out.println("Invalid choice. Please select option 1, 2, or 3.");
                }
            }
        } else {
            System.out.println("You have used a free hour. No fees charged for this reservation.");
        }

        System.out.println("Reservation successful for " + selectedRoom.getName() + " on " + selectedDate + " at " + selectedTime);
        System.out.println("Your Join Code: " + selectedRoom.getID() + "&" + selectedDate + "&" + selectedTime);

        applyRewardSystem(visitor);
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
        String input = scanner.nextLine().trim();
        int roomId;

        try {
            roomId = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Invalid room ID format. Please enter a numeric value.");
            return;
        }

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
        System.out.println("Reserved Slots in " + selectedRoom.getName() + ":");
        for (Slot slot : selectedRoom.getVisitorsInEachSlot().keySet()) {
            if (!slot.isAvailable() && selectedRoom.getSlotVisitors(slot).contains(visitor)) {
                reservedSlots.add(slot);
                System.out.println("  Slot: " + slot.getDate().format(DATE_FORMATTER) + " at " + slot.getTime().format(TIME_FORMATTER));
            }
        }

        if (reservedSlots.isEmpty()) {
            System.out.println("You don't have any reserved slots in this room.");
            return;
        }

        System.out.print("Enter the date (yyyy-mm-dd) of the slot you want to cancel: ");
        String dateInput = scanner.nextLine().trim();
        System.out.print("Enter the time (HH:mm) of the slot you want to cancel: ");
        String timeInput = scanner.nextLine().trim();

        LocalDate selectedDate;
        LocalTime selectedTime;
        try {
            selectedDate = LocalDate.parse(dateInput, DATE_FORMATTER);
            selectedTime = LocalTime.parse(timeInput, TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date/time format: " + e.getMessage());
            return;
        }

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
        String input = scanner.nextLine().trim();
        int roomId;

        try {
            roomId = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Invalid room ID format. Please enter a numeric value.");
            return;
        }

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
        System.out.println("Reserved Slots in " + selectedRoom.getName() + ":");
        for (Slot slot : selectedRoom.getVisitorsInEachSlot().keySet()) {
            if (!slot.isAvailable() && selectedRoom.getSlotVisitors(slot).contains(visitor)) {
                reservedSlots.add(slot);
                System.out.println("  Slot: " + slot.getDate().format(DATE_FORMATTER) + " at " + slot.getTime().format(TIME_FORMATTER));
            }
        }

        if (reservedSlots.isEmpty()) {
            System.out.println("You don't have any reserved slots in this room.");
            return;
        }

        System.out.print("Enter the date (yyyy-mm-dd) of the slot you want to update: ");
        String dateInput = scanner.nextLine().trim();
        System.out.print("Enter the time (HH:mm) of the slot you want to update: ");
        String timeInput = scanner.nextLine().trim();

        LocalDate selectedDate;
        LocalTime selectedTime;
        try {
            selectedDate = LocalDate.parse(dateInput, DATE_FORMATTER);
            selectedTime = LocalTime.parse(timeInput, TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date/time format: " + e.getMessage());
            return;
        }

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
                System.out.println("  Slot: " + slot.getDate().format(DATE_FORMATTER) + " at " + slot.getTime().format(TIME_FORMATTER));
            }
        }

        System.out.print("Enter the new date (yyyy-mm-dd) for the slot: ");
        String newDateStr = scanner.nextLine().trim();
        System.out.print("Enter the new time (HH:mm) for the slot: ");
        String newTimeStr = scanner.nextLine().trim();

        LocalDate newDate;
        LocalTime newTime;
        try {
            newDate = LocalDate.parse(newDateStr, DATE_FORMATTER);
            newTime = LocalTime.parse(newTimeStr, TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date/time format: " + e.getMessage());
            return;
        }

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

        // Update the reservation
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
            boolean isEligible = false;
            if (room instanceof GeneralRoom && visitor.getType().equalsIgnoreCase("general")) {
                isEligible = true;
            } else if (room instanceof MeetingRoom && visitor.getType().equalsIgnoreCase("formal")) {
                isEligible = true;
            } else if (room instanceof TeachingRoom && visitor.getType().equalsIgnoreCase("instructor")) {
                isEligible = true;
            }

            if (!isEligible) {
                continue;
            }

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
                        System.out.println("  Slot: " + slot.getDate().format(DATE_FORMATTER) + " at " + slot.getTime().format(TIME_FORMATTER));
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
            if (visitor.getHoursReserved() % instructorVisitorRewardThreshold == 0 && visitor.getHoursReserved() > 0) {
                rewardHours++;
            }
        } else {
            if (visitor.getHoursReserved() % generalVisitorRewardThreshold == 0 && visitor.getHoursReserved() > 0) {
                rewardHours++;
            }
        }

        if (rewardHours > 0) {
            visitor.addRewardHours(rewardHours);
            System.out.println("You have earned " + rewardHours + " free hour(s) for your reservations!");
        } else {
            System.out.println("No rewards earned yet.");
        }

        FileHandler.saveVisitors("CoWorkingSpace/src/services/visitors.txt", visitors);
    }

    // -------> Admin Operations

    public void adminMenu(Scanner scanner) {
        System.out.print("Enter admin name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Enter admin password: ");
        String password = scanner.nextLine().trim();

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
            System.out.println("9. Leader Board");
            System.out.println("10. Empty Safe");
            System.out.println("11. Logout");
            System.out.print("Choose an option: ");
            String input = scanner.nextLine().trim();
            int choice;

            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a number corresponding to the options.");
                continue;
            }

            switch (choice) {
                case 1 -> addSlot(scanner);
                case 2 -> deleteEntity(scanner);
                case 3 -> displayAllAvailableSlots();
                case 4 -> displayAllVisitors();
                case 5 -> displayAllRooms();
                case 6 -> displayAllInstructors();
                case 7 -> calculateTotalFees();
                case 8 -> updateEntity(scanner);
                case 9 -> LeaderBoardForAdmin(scanner);
                case 10 -> emptySafe(scanner);
                case 11 -> {
                    System.out.println("Logging out...");
                    return;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
            savedata();
        }
    }

    public void addSlot(Scanner scanner) {
        System.out.print("Enter the fees of the slot you want to create: ");
        String feeInput = scanner.nextLine().trim();
        double selectedFees;

        try {
            selectedFees = Double.parseDouble(feeInput);
            if (selectedFees < 0) {
                System.out.println("Fees cannot be negative.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid fee format. Please enter a numeric value.");
            return;
        }

        System.out.print("Enter the time (HH:mm) of the slot you want to create: ");
        String timeInput = scanner.nextLine().trim();
        System.out.print("Enter the date (yyyy-mm-dd) of the slot you want to create: ");
        String dateInput = scanner.nextLine().trim();

        LocalDate newDate;
        LocalTime newTime;
        try {
            newDate = LocalDate.parse(dateInput, DATE_FORMATTER);
            newTime = LocalTime.parse(timeInput, TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date/time format: " + e.getMessage());
            return;
        }

        Slot newSlot = new Slot(newTime, newDate, selectedFees, true);
        System.out.print("Enter the ID of the Room you want to create a slot in: ");
        String roomIdInput = scanner.nextLine().trim();
        int roomID;

        try {
            roomID = Integer.parseInt(roomIdInput);
        } catch (NumberFormatException e) {
            System.out.println("Invalid Room ID format. Please enter a numeric value.");
            return;
        }

        Room selectedRoom = findRoomById(roomID);
        if (selectedRoom == null) {
            System.out.println("Invalid Room ID. Slot creation failed.");
            return;
        }

        // Check for duplicate slot
        for (Slot slot : selectedRoom.getVisitorsInEachSlot().keySet()) {
            if (slot.getDate().equals(newDate) && slot.getTime().equals(newTime)) {
                System.out.println("A slot at this date and time already exists in the selected room.");
                return;
            }
        }

        selectedRoom.addSlotVisitors(newSlot);
        System.out.println("Slot added successfully to " + selectedRoom.getName() + ".");

        FileHandler.saveRooms("CoWorkingSpace/src/services/rooms.txt", rooms);
    }

    public void deleteEntity(Scanner scanner) {
        System.out.println("\n--- Delete Entity ---");
        System.out.println("1. Delete Visitor");
        System.out.println("2. Delete Room");
        System.out.println("3. Delete Slot");
        System.out.print("Choose an option: ");
        String input = scanner.nextLine().trim();
        int choice;

        try {
            choice = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Invalid input! Please enter a number corresponding to the options.");
            return;
        }

        switch (choice) {
            case 1 -> deleteVisitor(scanner);
            case 2 -> deleteRoom(scanner);
            case 3 -> deleteSlot(scanner);
            default -> System.out.println("Invalid choice. Returning to Admin Menu.");
        }
    }

    private void deleteVisitor(Scanner scanner) {
        System.out.print("Enter Visitor ID to delete: ");
        String input = scanner.nextLine().trim();
        int visitorId;

        try {
            visitorId = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Invalid Visitor ID format. Please enter a numeric value.");
            return;
        }

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

        // Remove visitor from all slots
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
        String input = scanner.nextLine().trim();
        int roomId;

        try {
            roomId = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Invalid Room ID format. Please enter a numeric value.");
            return;
        }

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
        String roomIdInput = scanner.nextLine().trim();
        int roomId;

        try {
            roomId = Integer.parseInt(roomIdInput);
        } catch (NumberFormatException e) {
            System.out.println("Invalid Room ID format. Please enter a numeric value.");
            return;
        }

        Room selectedRoom = findRoomById(roomId);
        if (selectedRoom == null) {
            System.out.println("Room not found.");
            return;
        }

        System.out.println("Available Slots in " + selectedRoom.getName() + ":");
        for (Slot slot : selectedRoom.getVisitorsInEachSlot().keySet()) {
            System.out.println("  Date: " + slot.getDate().format(DATE_FORMATTER) + ", Time: " + slot.getTime().format(TIME_FORMATTER));
        }

        System.out.print("Enter the date (yyyy-mm-dd) of the slot you want to delete: ");
        String dateInput = scanner.nextLine().trim();
        System.out.print("Enter the time (HH:mm) of the slot you want to delete: ");
        String timeInput = scanner.nextLine().trim();

        LocalDate selectedDate;
        LocalTime selectedTime;
        try {
            selectedDate = LocalDate.parse(dateInput, DATE_FORMATTER);
            selectedTime = LocalTime.parse(timeInput, TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date/time format: " + e.getMessage());
            return;
        }

        Slot slotToDelete = null;
        for (Slot slot : selectedRoom.getVisitorsInEachSlot().keySet()) {
            if (slot.getDate().equals(selectedDate) && slot.getTime().equals(selectedTime)) {
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
                    System.out.println("  Date: " + slot.getDate().format(DATE_FORMATTER) + ", Time: " + slot.getTime().format(TIME_FORMATTER) + ", Fees: $" + slot.getFees());
                }
            }
        }
    }

    public void displayAllVisitors() {
        System.out.println("\n--- All Visitors ---");
        for (Visitor visitor : visitors) {
            visitor.displayDetails();
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
                visitor.displayDetails();
            }
        }
    }

    public void calculateTotalFees() {
        double totalGeneralUnpayed = 0;
        double totalMeetingUnpayed = 0;
        double totalTeachingUnpayed = 0;
        double totalGeneralPayed = 0;
        double totalMeetingPayed = 0;
        double totalTeachingPayed = 0;

        for (Visitor visitor : visitors) {
            switch (visitor.getType().toLowerCase()) {
                case "general":
                    totalGeneralUnpayed += visitor.getTotalFees();
                    totalGeneralPayed += visitor.getPayedFees();
                    break;
                case "formal":
                    totalMeetingUnpayed += visitor.getTotalFees();
                    totalMeetingPayed += visitor.getPayedFees();
                    break;
                case "instructor":
                    totalTeachingUnpayed += visitor.getTotalFees();
                    totalTeachingPayed += visitor.getPayedFees();
                    break;
                default:
                    // Handle other types if any
                    break;
            }
        }

        System.out.println("\n--- Total Fees Collected ---");
        System.out.println("General Rooms - Unpaid: $" + totalGeneralUnpayed + ", Paid: $" + totalGeneralPayed);
        System.out.println("Meeting Rooms - Unpaid: $" + totalMeetingUnpayed + ", Paid: $" + totalMeetingPayed);
        System.out.println("Teaching Rooms - Unpaid: $" + totalTeachingUnpayed + ", Paid: $" + totalTeachingPayed);
        System.out.println("General Rooms - Total Expected: $" + (totalGeneralUnpayed + totalGeneralPayed));
        System.out.println("Meeting Rooms - Total Expected: $" + (totalMeetingUnpayed + totalMeetingPayed));
        System.out.println("Teaching Rooms - Total Expected: $" + (totalTeachingUnpayed + totalTeachingPayed));
    }

    public void updateEntity(Scanner scanner) {
        System.out.println("\n--- Update Entity ---");
        System.out.println("1. Update Visitor");
        System.out.println("2. Update Room");
        System.out.println("3. Update Slot");
        System.out.print("Choose an option: ");
        String input = scanner.nextLine().trim();
        int choice;

        try {
            choice = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Invalid input! Please enter a number corresponding to the options.");
            return;
        }

        switch (choice) {
            case 1 -> updateVisitor(scanner);
            case 2 -> updateRoom(scanner);
            case 3 -> updateSlot(scanner);
            default -> System.out.println("Invalid choice. Returning to Admin Menu.");
        }
    }

    private void updateVisitor(Scanner scanner) {
        System.out.print("Enter Visitor ID to update: ");
        String input = scanner.nextLine().trim();
        int visitorId;

        try {
            visitorId = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Invalid Visitor ID format. Please enter a numeric value.");
            return;
        }

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
        String newName = scanner.nextLine().trim();
        if (!newName.isEmpty()) {
            visitorToUpdate.setName(newName);
        }

        System.out.println("Current Password: " + visitorToUpdate.getPassword());
        System.out.print("Enter new password (or press Enter to keep current): ");
        String newPassword = scanner.nextLine().trim();
        if (!newPassword.isEmpty()) {
            visitorToUpdate.setPassword(newPassword);
        }

        System.out.println("Current Type: " + visitorToUpdate.getType());
        System.out.println("Select new type (or press Enter to keep current):");
        System.out.println("1. General");
        System.out.println("2. Formal");
        System.out.println("3. Instructor");
        System.out.print("Choose an option: ");
        String typeInput = scanner.nextLine().trim();

        if (!typeInput.isEmpty()) {
            int typeChoice;
            try {
                typeChoice = Integer.parseInt(typeInput);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Keeping current type.");
                typeChoice = -1;
            }

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
        }

        System.out.println("Visitor updated successfully.");
    }

    private void updateRoom(Scanner scanner) {
        System.out.print("Enter Room ID to update: ");
        String input = scanner.nextLine().trim();
        int roomId;

        try {
            roomId = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Invalid Room ID format. Please enter a numeric value.");
            return;
        }

        Room roomToUpdate = findRoomById(roomId);
        if (roomToUpdate == null) {
            System.out.println("Room not found.");
            return;
        }

        System.out.println("Current Room Name: " + roomToUpdate.getName());
        System.out.print("Enter new room name (or press Enter to keep current): ");
        String newName = scanner.nextLine().trim();
        if (!newName.isEmpty()) {
            roomToUpdate.setName(newName);
        }

        if (roomToUpdate instanceof TeachingRoom teachingRoom) {
            System.out.println("Current Projector Type: " + teachingRoom.getProjectorType());
            System.out.print("Enter new projector type (or press Enter to keep current): ");
            String newProjector = scanner.nextLine().trim();
            if (!newProjector.isEmpty()) {
                teachingRoom.setProjectorType(newProjector);
            }

            System.out.println("Current Board Type: " + teachingRoom.getBoardType());
            System.out.print("Enter new board type (or press Enter to keep current): ");
            String newBoard = scanner.nextLine().trim();
            if (!newBoard.isEmpty()) {
                teachingRoom.setBoardType(newBoard);
            }

            System.out.println("Current Instructor Name: " + teachingRoom.getInstructorName());
            System.out.print("Enter new instructor name (or press Enter to keep current): ");
            String newInstructor = scanner.nextLine().trim();
            if (!newInstructor.isEmpty()) {
                teachingRoom.setInstructorName(newInstructor);
            }
        }

        System.out.println("Room updated successfully.");
    }

    private void updateSlot(Scanner scanner) {
        System.out.print("Enter Room ID where the slot is located: ");
        String roomIdInput = scanner.nextLine().trim();
        int roomId;

        try {
            roomId = Integer.parseInt(roomIdInput);
        } catch (NumberFormatException e) {
            System.out.println("Invalid Room ID format. Please enter a numeric value.");
            return;
        }

        Room selectedRoom = findRoomById(roomId);
        if (selectedRoom == null) {
            System.out.println("Room not found.");
            return;
        }

        System.out.println("Available Slots in " + selectedRoom.getName() + ":");
        for (Slot slot : selectedRoom.getVisitorsInEachSlot().keySet()) {
            System.out.println("  Date: " + slot.getDate().format(DATE_FORMATTER) + ", Time: " + slot.getTime().format(TIME_FORMATTER) + ", Fees: $" + slot.getFees());
        }

        System.out.print("Enter the date (yyyy-mm-dd) of the slot you want to update: ");
        String dateInput = scanner.nextLine().trim();
        System.out.print("Enter the time (HH:mm) of the slot you want to update: ");
        String timeInput = scanner.nextLine().trim();

        LocalDate selectedDate;
        LocalTime selectedTime;
        try {
            selectedDate = LocalDate.parse(dateInput, DATE_FORMATTER);
            selectedTime = LocalTime.parse(timeInput, TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date/time format: " + e.getMessage());
            return;
        }

        Slot slotToUpdate = null;
        for (Slot slot : selectedRoom.getVisitorsInEachSlot().keySet()) {
            if (slot.getDate().equals(selectedDate) && slot.getTime().equals(selectedTime)) {
                slotToUpdate = slot;
                break;
            }
        }

        if (slotToUpdate == null) {
            System.out.println("Slot not found.");
            return;
        }

        // Update Fees
        System.out.println("Current Fees: $" + slotToUpdate.getFees());
        System.out.print("Enter new fees (or press Enter to keep current): ");
        String feesInput = scanner.nextLine().trim();
        if (!feesInput.isEmpty()) {
            try {
                double newFees = Double.parseDouble(feesInput);
                if (newFees < 0) {
                    System.out.println("Fees cannot be negative. Keeping current fees.");
                } else {
                    slotToUpdate.setFees(newFees);
                    System.out.println("Fees updated successfully.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Keeping current fees.");
            }
        }

        // Update Availability
        System.out.println("Current Availability: " + (slotToUpdate.isAvailable() ? "Available" : "Reserved"));
        System.out.print("Enter new availability (true/false) or press Enter to keep current: ");
        String availabilityInput = scanner.nextLine().trim();
        if (!availabilityInput.isEmpty()) {
            if (availabilityInput.equalsIgnoreCase("true") || availabilityInput.equalsIgnoreCase("false")) {
                boolean newAvailability = Boolean.parseBoolean(availabilityInput);
                slotToUpdate.setAvailable(newAvailability);
                System.out.println("Availability updated successfully.");
            } else {
                System.out.println("Invalid input! Keeping current availability.");
            }
        }

        // Update Date and Time
        System.out.println("Current Date: " + slotToUpdate.getDate().format(DATE_FORMATTER));
        System.out.print("Enter new date (yyyy-mm-dd) or press Enter to keep current: ");
        String newDateInput = scanner.nextLine().trim();
        if (!newDateInput.isEmpty()) {
            try {
                LocalDate newDate = LocalDate.parse(newDateInput, DATE_FORMATTER);
                // Check if new date and time combination already exists
                boolean duplicate = false;
                for (Slot slot : selectedRoom.getVisitorsInEachSlot().keySet()) {
                    if (slot.getDate().equals(newDate) && slot.getTime().equals(slotToUpdate.getTime())) {
                        duplicate = true;
                        break;
                    }
                }
                if (!duplicate) {
                    slotToUpdate.setDate(newDate);
                    System.out.println("Date updated successfully.");
                } else {
                    System.out.println("A slot with the new date and current time already exists. Date not updated.");
                }
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format! Keeping current date.");
            }
        }

        System.out.println("Current Time: " + slotToUpdate.getTime().format(TIME_FORMATTER));
        System.out.print("Enter new time (HH:mm) or press Enter to keep current: ");
        String newTimeInput = scanner.nextLine().trim();
        if (!newTimeInput.isEmpty()) {
            try {
                LocalTime newTime = LocalTime.parse(newTimeInput, TIME_FORMATTER);
                // Check if new date and time combination already exists
                boolean duplicate = false;
                for (Slot slot : selectedRoom.getVisitorsInEachSlot().keySet()) {
                    if (slot.getDate().equals(slotToUpdate.getDate()) && slot.getTime().equals(newTime)) {
                        duplicate = true;
                        break;
                    }
                }
                if (!duplicate) {
                    slotToUpdate.setTime(newTime);
                    System.out.println("Time updated successfully.");
                } else {
                    System.out.println("A slot with the current date and new time already exists. Time not updated.");
                }
            } catch (DateTimeParseException e) {
                System.out.println("Invalid time format! Keeping current time.");
            }
        }

        System.out.println("Slot updated successfully.");
    }

    public void LeaderBoardForAdmin(Scanner scanner) {
        LeaderBoardForVisitor();
        System.out.print("Do you want to apply the reward? (yes/no): ");
        String response = scanner.nextLine().trim().toLowerCase();

        if (response.equals("yes") || response.equals("y")) {
            if (!visitors.isEmpty()) {
                visitors.get(0).addRewardHours(7);
                System.out.println("Reward applied to the top visitor.");
            } else {
                System.out.println("No visitors available to apply the reward.");
            }
        } else if (response.equals("no") || response.equals("n")) {
            System.out.println("Leader Board Has Been Checked.");
        } else {
            System.out.println("Invalid input. Please enter 'yes' or 'no'.");
        }
    }

    public void emptySafe(Scanner scanner) {
        System.out.print("Do you want to empty the safe? (yes/no): ");
        String response = scanner.nextLine().trim().toLowerCase();

        if (response.equals("yes") || response.equals("y")) {
            for (Visitor visitor : visitors) {
                visitor.setPayedFees(0);
            }
            System.out.println("Safe has been emptied successfully.");
            System.out.println("بقينا علي الحميد المجيد");
        } else if (response.equals("no") || response.equals("n")) {
            System.out.println("Safe remains unchanged.");
            System.out.println("البضاهة معايا"); 
        } else {
            System.out.println("Invalid input. Please enter 'yes' or 'no'.");
        }
    }
}
