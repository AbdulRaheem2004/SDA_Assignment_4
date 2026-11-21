# Hospital Appointment System

**Software Design and Analysis - Assignment 4**  
**Student:** Abdul Raheem  
**Registration:** 23P-0591  
**Program:** BS (CS 9A), FAST NUCES Peshawar, Fall 2026

This project implements the class design from Assignment 3 as a plain Java console program. It demonstrates UC-02 Search Doctor Availability, UC-01 Book Appointment, UC-04 Cancel or Reschedule Appointment, UC-05 Check-In Patient, UC-03 Manage Doctor Schedule, and UC-08 Reports.

## Video Demonstration & Final Report

- **Primary Video Demonstration (Google Drive):** [Google Drive Stream & View](https://drive.google.com/file/d/1oWC6GNInkGhFYLvaxk5HT6du9IndgF3Y/view?usp=sharing)
- **Direct Video Demonstration (GitHub Release MP4):** [Download Hospital_Appointment_System_Eclipse_Demo.mp4](https://github.com/AbdulRaheem2004/SDA_Assignment_4/releases/download/v1.0.0/Hospital_Appointment_System_Eclipse_Demo.mp4)
- **GitHub Release & Video Notes:** [v1.0.0 Release Page](https://github.com/AbdulRaheem2004/SDA_Assignment_4/releases/tag/v1.0.0)
- **Demonstration Details:** Genuine 3-minute screen recording of the application running live in Eclipse IDE on Windows with spoken audio explanations covering project structure, UC-02 doctor availability search, UC-01 booking and simulated payment, UC-04 cancellation under the 24-hour refund policy, and source inspection of core OOP concepts.

## Classes

| Package | Classes |
| --- | --- |
| `hospital.model` | `User` (abstract), `Patient`, `Doctor`, `ClinicReceptionist`, `HospitalAdministrator`, `Appointment`, `TimeSlot`, `PaymentTransaction` |
| `hospital.service` | `AppointmentMgr`, `SlotService`, `ApptDB`, `PaymentService`, `AuditLog`, `NotificationChannel` (abstract), `SMSNotification`, `EmailNotification`, `ReportService`, `NotificationSender` (interface) |
| `hospital.main` | `BookingUI`, `Main` |

All 18 classes in Assignment 3's final list are present; `NotificationSender` and `Main` are added for Assignment 4. Each Java file has the same name as its class/interface.

## Run in Eclipse with JDK

1. Install a JDK and Eclipse IDE for Java Developers.
2. In Eclipse, choose **File > Import > General > Existing Projects into Workspace**.
3. Select this repository's root directory and import `SDA_Assignment_4_Hospital_Appointment_System`.
4. Select `src/hospital/main/Main.java`, then **Run As > Java Application**.
5. Type a menu number in the Eclipse Console. You can filter specialty `Cardiology`, location `Peshawar`, and date `2026-11-25`, then book slot `S01`.

No framework, third-party library, server, or API key is required. The standard JDK `Scanner` class reads console input. Classes are divided into `model`, `service`, and `main` packages.

## Demo menu

| Number | Operation | Assignment 1 use case |
| --- | --- | --- |
| 1 | Search doctor availability | UC-02 |
| 2 | Book appointment and simulate deposit | UC-01 |
| 3 | View appointments | Supports UC-01 and UC-04 |
| 4 | Cancel appointment and simulate refund | UC-04 |
| 5 | Reschedule an appointment | UC-04 |
| 6 | Check in using QR token | UC-05 |
| 7 | Manage doctor's weekly schedule | UC-03 |
| 8 | View utilization and audit entries | UC-08 |
| 0 | Exit | - |

Booking uses a fixed in-memory patient `P01`, doctor/slot arrays, a 10-minute **logical** hold, and the `approve`/`decline` payment test inputs. Enter an **estimated number of hours until the appointment** when booking and **elapsed hours since booking** when cancelling; these integers demonstrate the 24-hour cancellation notice rule. A full simulated refund applies for 24 hours or more remaining, otherwise a 50% refund. The real clock does not advance the hold. Email/SMS messages and QR passes print to the console; they are not delivered or machine-scanned. Records disappear after the process exits.

## Optional command-line verification

From the repository root using a JDK:

```bash
javac -d build src/hospital/model/*.java src/hospital/service/*.java src/hospital/main/*.java
java -cp build hospital.main.Main
javac -cp build -d build tests/hospital/tests/AcceptanceTest.java
java -cp build hospital.tests.AcceptanceTest
```

## Design changes from Assignment 3

- Added `NotificationSender` interface and `Main` entry point to satisfy Assignment 4; the original 18 class names remain.
- Replaced Date/DateTime, collection, and Decimal signatures with lecture-level `String`, `int`, arrays, and `double`.
- `ApptDB` is a fixed-capacity in-memory array, not a real database; `PaymentService`, SMS and email are deterministic console simulations.
- Appointment-time calculations use input elapsed hours, and temporary slot holds are logical, not wall-clock timed, because timers, persistence and external services are outside the permitted material.
- QR check-in uses a generated text token instead of a physical QR scanner.

This project is a teaching demo, not a production hospital or payment system.
