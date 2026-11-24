
import java.io.*;
import java.util.*;

/**
 * Hotel Room Booking System
 * DSA Project - Priya Sharma (RU-25-11033)
 *
 * Concepts used: ArrayList, HashMap, Searching, Sorting, Methods, File Handling.
 */

// ---------------------------------------------------------------
// Room class: represents one hotel room
// ---------------------------------------------------------------
class Room {
    private final int roomNumber;
    private final String type;
    private final int pricePerNight;
    private boolean booked;
    private String guestName;
    private int nights;

    Room(int roomNumber, String type, int pricePerNight) {
        this.roomNumber = roomNumber;
        this.type = type;
        this.pricePerNight = pricePerNight;
        this.booked = false;
        this.guestName = "-";
        this.nights = 0;
    }

    int getRoomNumber()    { return roomNumber; }
    String getType()       { return type; }
    int getPricePerNight() { return pricePerNight; }
    boolean isBooked()     { return booked; }
    String getGuestName()  { return guestName; }
    int getNights()        { return nights; }

    void book(String guestName, int nights) {
        this.booked = true;
        this.guestName = guestName;
        this.nights = nights;
    }

    void release() {
        this.booked = false;
        this.guestName = "-";
        this.nights = 0;
    }

    int calculateBill() {
        return pricePerNight * nights;
    }

    String toFileLine() {
        return roomNumber + "," + type + "," + pricePerNight + "," + booked + "," + guestName + "," + nights;
    }

    @Override
    public String toString() {
        String status = booked ? "Booked (" + guestName + ", " + nights + " night(s))" : "Available";
        return String.format("Room %d | %-6s | ₹%,d | %s", roomNumber, type, pricePerNight, status);
    }
}

// ---------------------------------------------------------------
// Main class: menu-driven application
// ---------------------------------------------------------------
public class dsaProject {

    private static final String FILE_NAME = "rooms.txt";

    // ArrayList stores all room objects
    private static final ArrayList<Room> rooms = new ArrayList<>();
    // HashMap maps room number -> Room object for fast searching
    private static final HashMap<Integer, Room> roomMap = new HashMap<>();

    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        loadRooms();

        int choice;
        do {
            printMenu();
            choice = readInt("Enter your choice: ");
            System.out.println();
            switch (choice) {
                case 1: viewRooms();            break;
                case 2: bookRoom();             break;
                case 3: searchRoom();           break;
                case 4: searchAvailableByType(); break;
                case 5: checkOut();             break;
                case 6: cancelBooking();        break;
                case 7: sortRooms();            break;
                case 8: saveRooms();
                        System.out.println("Data saved to " + FILE_NAME + ". Goodbye!");
                        break;
                default: System.out.println("Invalid choice. Please enter 1-8.");
            }
            System.out.println();
        } while (choice != 8);
    }

    // ---------------- Menu ----------------
    private static void printMenu() {
        System.out.println("========== HOTEL ROOM BOOKING SYSTEM ==========");
        System.out.println("1. View all rooms");
        System.out.println("2. Book a room");
        System.out.println("3. Search room by number");
        System.out.println("4. Search available rooms by type");
        System.out.println("5. Check out a guest");
        System.out.println("6. Cancel a booking");
        System.out.println("7. Sort rooms by price (high to low)");
        System.out.println("8. Save and exit");
        System.out.println("===============================================");
    }

    // ---------------- Operations ----------------

    /** Displays all room records. */
    static void viewRooms() {
        System.out.println("--- All Rooms ---");
        int available = 0;
        for (Room r : rooms) {
            System.out.println(r);
            if (!r.isBooked()) available++;
        }
        System.out.println("Available rooms: " + available + " of " + rooms.size());
    }

    /** Books an available room. */
    static void bookRoom() {
        int number = readInt("Room Number: ");
        Room room = roomMap.get(number);              // HashMap lookup
        if (room == null) {
            System.out.println("Room " + number + " not found.");
            return;
        }
        if (room.isBooked()) {
            System.out.println("Room " + number + " is already booked.");
            return;
        }
        String guest = readNonEmpty("Guest Name: ");
        int nights = readInt("Nights: ");
        if (nights < 1) {
            System.out.println("Number of nights must be at least 1.");
            return;
        }
        room.book(guest, nights);
        System.out.println("Room booked successfully");
        System.out.println("Room " + number + " booked for " + guest + ".");
        System.out.printf("Total: ₹%,d%n", room.calculateBill());
    }

    /** Searches for a room by room number (HashMap). */
    static void searchRoom() {
        int number = readInt("Room Number: ");
        Room room = roomMap.get(number);
        if (room == null) {
            System.out.println("Room " + number + " not found.");
        } else {
            System.out.println(room);
        }
    }

    /** Linear search over the ArrayList: available rooms of a given type. */
    static void searchAvailableByType() {
        String type = readNonEmpty("Room type (Single/Double/Suite): ");
        boolean found = false;
        for (Room r : rooms) {
            if (r.getType().equalsIgnoreCase(type) && !r.isBooked()) {
                System.out.println(r);
                found = true;
            }
        }
        if (!found) {
            System.out.println("No available " + type + " rooms.");
        }
    }

    /** Checks out a guest and prints the bill. */
    static void checkOut() {
        int number = readInt("Room Number: ");
        Room room = roomMap.get(number);
        if (room == null) {
            System.out.println("Room " + number + " not found.");
            return;
        }
        if (!room.isBooked()) {
            System.out.println("Room " + number + " is not booked.");
            return;
        }
        int bill = room.calculateBill();
        String guest = room.getGuestName();
        int nights = room.getNights();
        room.release();
        System.out.println("Room " + number + " checked out.");
        System.out.println("Guest: " + guest + " | Nights: " + nights);
        System.out.printf("Bill: ₹%,d%n", bill);
        System.out.println("Status: Available");
    }

    /** Cancels an existing booking without billing. */
    static void cancelBooking() {
        int number = readInt("Room Number: ");
        Room room = roomMap.get(number);
        if (room == null) {
            System.out.println("Room " + number + " not found.");
        } else if (!room.isBooked()) {
            System.out.println("Room " + number + " is not booked.");
        } else {
            room.release();
            System.out.println("Booking cancelled successfully");
        }
    }

    /** Sorts rooms by price, high to low (selection sort on a copy of the list). */
    static void sortRooms() {
        ArrayList<Room> sorted = new ArrayList<>(rooms);
        for (int i = 0; i < sorted.size() - 1; i++) {
            int max = i;
            for (int j = i + 1; j < sorted.size(); j++) {
                if (sorted.get(j).getPricePerNight() > sorted.get(max).getPricePerNight()) {
                    max = j;
                }
            }
            Room temp = sorted.get(i);
            sorted.set(i, sorted.get(max));
            sorted.set(max, temp);
        }
        System.out.println("--- Sorted Rooms (price high to low) ---");
        for (Room r : sorted) {
            System.out.println(r);
        }
    }

    // ---------------- File handling ----------------

    /** Loads room data from rooms.txt; creates default rooms if the file is missing. */
    static void loadRooms() {
        rooms.clear();
        roomMap.clear();
        File file = new File(FILE_NAME);

        if (!file.exists()) {
            addRoom(new Room(101, "Single", 1500));
            addRoom(new Room(102, "Double", 2500));
            addRoom(new Room(103, "Suite", 5000));
            saveRooms();
            System.out.println("rooms.txt not found. Created with default rooms.");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] p = line.split(",");
                if (p.length < 6) continue;           // skip malformed lines
                try {
                    Room r = new Room(Integer.parseInt(p[0].trim()), p[1].trim(), Integer.parseInt(p[2].trim()));
                    if (Boolean.parseBoolean(p[3].trim())) {
                        r.book(p[4].trim(), Integer.parseInt(p[5].trim()));
                    }
                    addRoom(r);
                } catch (NumberFormatException e) {
                    System.out.println("Skipping invalid record: " + line);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading " + FILE_NAME + ": " + e.getMessage());
        }
    }

    /** Writes the current room records back to rooms.txt. */
    static void saveRooms() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (Room r : rooms) {
                pw.println(r.toFileLine());
            }
        } catch (IOException e) {
            System.out.println("Error saving " + FILE_NAME + ": " + e.getMessage());
        }
    }

    // ---------------- Helpers ----------------

    private static void addRoom(Room r) {
        rooms.add(r);
        roomMap.put(r.getRoomNumber(), r);
    }

    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            if (!sc.hasNextLine()) {          // end of input: exit cleanly
                saveRooms();
                System.exit(0);
            }
            String s = sc.nextLine().trim();
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private static String readNonEmpty(String prompt) {
        while (true) {
            System.out.print(prompt);
            if (!sc.hasNextLine()) {
                saveRooms();
                System.exit(0);
            }
            String s = sc.nextLine().trim().replace(",", " ");
            if (!s.isEmpty()) return s;
            System.out.println("Input cannot be empty.");
        }
    }
}