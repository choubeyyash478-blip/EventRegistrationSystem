# Simple Event Registration System

**PS No. 47 – Data Structures and Algorithms (DSA)**
Submitted by: Yash Choubey (ERP ID: RU-25-11623)
Guide: Ms. Naina Singh
Rungta International Skills University, Bhilai – Session 2026-27

A console application in Java for event registration. Events are stored in a `HashMap`
(event name -> `ArrayList` of confirmed registrations) and every event has a FIFO waiting
list (`Queue`). Each event has 50 seats. When an event is full, new participants join the
waiting list and are confirmed automatically, in order, when a seat is cancelled.

**Concepts used:** ArrayList, HashMap, Queue, Methods, File Handling

**Events:** Tech Fest, Music Night, Sports Day (50 seats each)

## Features

| Menu option | Action |
|---|---|
| 1 | Register – asks for name and event, assigns ID (REG101, REG102, ...); confirmed if a seat is free, otherwise placed on the waiting list |
| 2 | Cancel Registration – cancels by ID; the first person on the waiting list is confirmed automatically |
| 3 | Display Registrations – confirmed registrations of one event (or all events, press Enter), with seats left |
| 4 | Show Waiting List – FIFO waiting list of one event (or all events, press Enter) |
| 5 | Exit – saves everything to `events.txt` and closes |

Registrations are loaded from `events.txt` when the program starts and saved to it on exit.

## How to run

Requires JDK 17 or above (tested on JDK 21).

```bash
javac EventRegistrationSystem.java
java EventRegistrationSystem
```

Run the commands from the folder that contains `events.txt` so the saved data is found.

## Sample run

```
Enter choice: 1
Name: Aman
Event: Tech Fest
Registered. ID: REG101 | Aman | Tech Fest | Status: Confirmed

Enter choice: 1
Name: Bina
Event: Cooking Class
Event Cooking Class not found. Available: Tech Fest, Music Night, Sports Day
```

## Test data for the test cases

Test Cases 2 and 4 need an event that is almost full or full. Two ready-made data files are
in the `testdata` folder. Copy one of them over `events.txt` (in the folder where you run the
program) before running the test case:

| File | Contents | Test case |
|---|---|---|
| `testdata/events_testcase2_49_confirmed.txt` | Tech Fest has 49 confirmed registrations (REG101-REG149) | Test Case 2 – register Asha -> REG150, Seats left: 0 |
| `testdata/events_testcase4_full_50_confirmed.txt` | Tech Fest is full (REG101-REG150) | Test Case 4 – register Chirag -> Waiting; show waiting list; cancel REG101 |

Test Cases 1 and 3 use the starting `events.txt` (no registrations).

## File format (`events.txt`)

```
#NEXT_ID=154
REG101|Aman|Tech Fest|Confirmed
REG153|Eshan|Tech Fest|Waiting
```

The first line stores the next ID so that IDs are never reused. Each other line is one
registration: `ID|Name|Event|Status`. Waiting registrations are saved in queue order.

## Files

- `EventRegistrationSystem.java` – complete source code (one file)
- `events.txt` – data file (starts empty; first registration will be REG101)
- `testdata/` – data files for Test Cases 2 and 4
- `Simple_Event_Registration_System_Final_Report.docx` – project report
