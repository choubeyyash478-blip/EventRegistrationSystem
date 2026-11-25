import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class EventRegistrationSystem {
 static class Registration {
        private final String id;
        private final String name;
        private final String event;
        private String status;          // "Confirmed" or "Waiting"

        Registration(String id, String name, String event, String status) {
            this.id = id;
            this.name = name;
            this.event = event;
            this.status = status;
        }

        String getId()     { return id; }
        String getName()   { return name; }
        String getEvent()  { return event; }
        String getStatus() { return status; }

        void setStatus(String status) { this.status = status; }

        @Override
        public String toString() {
            return id + " | " + name + " | " + event;
        }
    }
    private static final String FILE_NAME = "events.txt";
    private static final String COUNTER_PREFIX = "#NEXT_ID=";
    private static final String CONFIRMED = "Confirmed";
    private static final String WAITING = "Waiting";
    private static final int CAPACITY = 50;
    private static final int FIRST_REG_NUMBER = 101;
    private static final String[] EVENT_NAMES = {"Tech Fest", "Music Night", "Sports Day"};
    private static HashMap<String, ArrayList<Registration>> confirmed = new LinkedHashMap<>();
    private static HashMap<String, Queue<Registration>> waiting = new LinkedHashMap<>();
    private static int nextRegNumber = FIRST_REG_NUMBER;
    
    /** Option 1 - register a participant for an event. */
    static void registerParticipant(Scanner sc) {
        System.out.print("Name: ");
        String name = readLine(sc);
        System.out.print("Event: ");
        String eventInput = readLine(sc);

        if (name.isEmpty() || eventInput.isEmpty()) {
            System.out.println("Invalid input. Name and event cannot be empty.");
            return;
        }
    
        name = name.replace('|', '/');

        String event = findEvent(eventInput);
        if (event == null) {
            System.out.println("Event " + eventInput + " not found. Available: "
                    + availableEvents());
            return;
        }

        String id = "REG" + nextRegNumber;
        nextRegNumber++;

        if (seatsLeft(event) > 0) {
            Registration r = new Registration(id, name, event, CONFIRMED);
            confirmed.get(event).add(r);
            System.out.println("Registered. ID: " + r + " | Status: Confirmed");
            if (seatsLeft(event) == 0) {
                System.out.println("Seats left: 0");
            }
        } else {
            Registration r = new Registration(id, name, event, WAITING);
            waiting.get(event).add(r);           // joins the rear of the queue
            System.out.println("Event full. ID: " + r + " | Status: Waiting");
        }
    }

    /** Option 2 - cancel a registration by ID. */
    static void cancelRegistration(Scanner sc) {
        System.out.print("Registration ID: ");
        String id = readLine(sc);
        if (id.isEmpty()) {
            System.out.println("Invalid input. Registration ID cannot be empty.");
            return;
        }

        // 1) look for the ID among the confirmed registrations
        for (String event : confirmed.keySet()) {
            ArrayList<Registration> list = confirmed.get(event);
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getId().equalsIgnoreCase(id)) {
                    Registration removed = list.remove(i);
                    Queue<Registration> queue = waiting.get(event);
                    if (!queue.isEmpty()) {
                        // the seat goes to the first person on the waiting list
                        Registration next = queue.poll();
                        next.setStatus(CONFIRMED);
                        list.add(next);
                        System.out.println(removed.getId() + " cancelled. "
                                + next.getId() + " | " + next.getName()
                                + " moved to Confirmed");
                    } else {
                        System.out.println(removed.getId() + " cancelled. Seats left: "
                                + seatsLeft(event));
                    }
                    return;
                }
            }
        }

        // 2) look for the ID on the waiting lists
        for (String event : waiting.keySet()) {
            Queue<Registration> queue = waiting.get(event);
            for (Registration r : queue) {
                if (r.getId().equalsIgnoreCase(id)) {
                    queue.remove(r);
                    System.out.println(r.getId() + " cancelled (removed from the waiting list).");
                    return;
                }
            }
        }

        System.out.println("Registration ID " + id.toUpperCase() + " not found.");
    }

    /** Option 3 - display the confirmed registrations of one event or of all events. */
    static void displayRegistrations(Scanner sc) {
        System.out.print("Event name (press Enter for all events): ");
        String eventInput = readLine(sc);

        if (eventInput.isEmpty()) {
            for (String event : confirmed.keySet()) {
                printEventRegistrations(event);
            }
            return;
        }
        String event = findEvent(eventInput);
        if (event == null) {
            System.out.println("Event " + eventInput + " not found. Available: "
                    + availableEvents());
            return;
        }
        printEventRegistrations(event);
    }

    /** Option 4 - show the FIFO waiting list of one event or of all events. */
    static void showWaitingList(Scanner sc) {
        System.out.print("Event name (press Enter for all events): ");
        String eventInput = readLine(sc);

        ArrayList<String> events = new ArrayList<>();
        if (eventInput.isEmpty()) {
            for (String e : waiting.keySet()) {
                events.add(e);
            }
        } else {
            String event = findEvent(eventInput);
            if (event == null) {
                System.out.println("Event " + eventInput + " not found. Available: "
                        + availableEvents());
                return;
            }
            events.add(event);
        }

        int total = 0;
        for (String e : events) {
            total += waiting.get(e).size();
        }
        System.out.println("Waiting list: " + total);
        for (String e : events) {
            int position = 1;
            for (Registration r : waiting.get(e)) {
                System.out.println("  " + position + ". " + r);
                position++;
            }
        }
    }

    private static void printEventRegistrations(String event) {
        ArrayList<Registration> list = confirmed.get(event);
        System.out.println(event + " | Confirmed: " + list.size() + "/" + CAPACITY
                + " | Seats left: " + seatsLeft(event)
                + " | Waiting: " + waiting.get(event).size());
        if (list.isEmpty()) {
            System.out.println("  No registrations yet.");
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + list.get(i));
        }
    }

    private static int seatsLeft(String event) {
        return CAPACITY - confirmed.get(event).size();
    }

    
    private static String findEvent(String input) {
        for (String event : confirmed.keySet()) {
            if (event.equalsIgnoreCase(input.trim())) {
                return event;
            }
        }
        return null;
    }

    private static String availableEvents() {
        String result = "";
        for (String event : confirmed.keySet()) {
            if (!result.isEmpty()) {
                result += ", ";
            }
            result += event;
        }
        return result;
    }

  static void loadFromFile() {
        File f = new File(FILE_NAME);
        if (!f.exists()) {
            return;   
        }
        int highestSeen = FIRST_REG_NUMBER - 1;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }
                if (line.startsWith(COUNTER_PREFIX)) {
                    try {
                        nextRegNumber = Integer.parseInt(
                                line.substring(COUNTER_PREFIX.length()).trim());
                    } catch (NumberFormatException e) {
                    
                    }
                    continue;
                }
                String[] parts = line.split("\\|", 4);
                if (parts.length < 4) {
                    continue;   // skip a damaged line
                }
                String event = findEvent(parts[2]);
                if (event == null) {
                    continue;  
                }
                Registration r = new Registration(parts[0].trim(), parts[1].trim(),
                        event, parts[3].trim());
                boolean wantsWaiting = WAITING.equalsIgnoreCase(r.getStatus());
                if (!wantsWaiting && seatsLeft(event) > 0) {
                    r.setStatus(CONFIRMED);
                    confirmed.get(event).add(r);
                } else {
                    r.setStatus(WAITING);   // also covers a full event in a hand-edited file
                    waiting.get(event).add(r);
                }
                int num = numberOf(r.getId());
                if (num > highestSeen) {
                    highestSeen = num;
                }
            }
        } catch (IOException e) {
            System.out.println("Warning: could not read " + FILE_NAME + ": " + e.getMessage());
        }
        
        if (nextRegNumber <= highestSeen) {
            nextRegNumber = highestSeen + 1;
        }
    }


    static void saveToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME))) {
            bw.write(COUNTER_PREFIX + nextRegNumber);
            bw.newLine();
            for (String event : confirmed.keySet()) {
                for (Registration r : confirmed.get(event)) {
                    writeRecord(bw, r, CONFIRMED);
                }
                for (Registration r : waiting.get(event)) {   // queue order is kept
                    writeRecord(bw, r, WAITING);
                }
            }
        } catch (IOException e) {
            System.out.println("Warning: could not save " + FILE_NAME + ": " + e.getMessage());
        }
    }

    private static void writeRecord(BufferedWriter bw, Registration r, String status)
            throws IOException {
        bw.write(r.getId() + "|" + r.getName() + "|" + r.getEvent() + "|" + status);
        bw.newLine();
    }

    
    private static int numberOf(String id) {
        if (id.length() < 4) {
            return 0;
        }
        try {
            return Integer.parseInt(id.substring(3));
        } catch (NumberFormatException e) {
            return 0;
        }
    }


    private static String readLine(Scanner sc) {
        if (!sc.hasNextLine()) {
            return "";
        }
        return sc.nextLine().trim();
    }

    private static void setUpEvents() {
        for (String event : EVENT_NAMES) {
            confirmed.put(event, new ArrayList<Registration>());
            waiting.put(event, new LinkedList<Registration>());
        }
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("===== Simple Event Registration System =====");
        System.out.println("1 - Register");
        System.out.println("2 - Cancel Registration");
        System.out.println("3 - Display Registrations");
        System.out.println("4 - Show Waiting List");
        System.out.println("5 - Exit");
        System.out.print("Enter choice: ");
    }


    public static void main(String[] args) {
        setUpEvents();
        loadFromFile();
        Scanner sc = new Scanner(System.in);
        boolean running = true;

        while (running) {
            printMenu();
            if (!sc.hasNextLine()) {
                break;   // input finished: leave the loop and save
            }
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    registerParticipant(sc);
                    break;
                case "2":
                    cancelRegistration(sc);
                    break;
                case "3":
                    displayRegistrations(sc);
                    break;
                case "4":
                    showWaitingList(sc);
                    break;
                case "5":
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please enter a number from 1 to 5.");
            }
        }

        saveToFile();
        System.out.println("Data saved to " + FILE_NAME + ". Goodbye!");
        sc.close();
    }
}
