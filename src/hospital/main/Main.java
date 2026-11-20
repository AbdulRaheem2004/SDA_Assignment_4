package hospital.main;

import java.util.Scanner;
import hospital.model.Appointment;
import hospital.model.ClinicReceptionist;
import hospital.model.Doctor;
import hospital.model.HospitalAdministrator;
import hospital.model.Patient;
import hospital.model.TimeSlot;
import hospital.model.User;
import hospital.service.ApptDB;
import hospital.service.AppointmentMgr;
import hospital.service.AuditLog;
import hospital.service.EmailNotification;
import hospital.service.NotificationChannel;
import hospital.service.PaymentService;
import hospital.service.ReportService;
import hospital.service.SMSNotification;
import hospital.service.SlotService;

public class Main {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        Patient patient = new Patient("P01", "Ali Raza", "ali@example.com", "03001234567", "PN-01");
        Doctor[] doctors = {
            new Doctor("D01", "Dr Ayesha Khan", "ayesha@example.com", "03000000002", "PR-01", "Cardiology", "Peshawar", 2500),
            new Doctor("D02", "Dr Bilal Ahmad", "bilal@example.com", "03000000003", "PR-02", "Dermatology", "Peshawar", 1800),
            new Doctor("D03", "Dr Sara Iqbal", "sara@example.com", "03000000004", "PR-03", "Cardiology", "Islamabad", 3000)
        };
        TimeSlot[] slots = {
            new TimeSlot("S01", "D01", "2026-11-25 10:00", "2026-11-25 10:30"),
            new TimeSlot("S02", "D01", "2026-11-25 11:00", "2026-11-25 11:30"),
            new TimeSlot("S03", "D02", "2026-11-25 12:00", "2026-11-25 12:30"),
            new TimeSlot("S04", "D03", "2026-11-26 09:00", "2026-11-26 09:30"),
            new TimeSlot("S05", "D01", "2026-11-26 13:00", "2026-11-26 13:30")
        };

        ClinicReceptionist receptionist = new ClinicReceptionist("R01", "Hina Shah", "ST-01", "CLINIC-01", "SC-01");
        HospitalAdministrator administrator = new HospitalAdministrator("H01", "Hamza Malik", "AD-01", "CLINIC-01");
        ApptDB database = new ApptDB("HospitalAppointmentDemo", 30);
        SlotService slotService = new SlotService(slots, doctors);
        PaymentService paymentService = new PaymentService("DEMO-MERCHANT", 60);
        AuditLog audit = new AuditLog("LOG-01");
        NotificationChannel[] channels = {
            new SMSNotification("HOSPITAL", "+92"),
            new EmailNotification("noreply@demo.local", "help@demo.local")
        };
        AppointmentMgr manager = new AppointmentMgr(slotService, paymentService, database, audit, channels, patient);
        BookingUI ui = new BookingUI("SESSION-1", manager, slotService);
        ReportService reports = new ReportService(database, doctors);

        User[] people = {patient, doctors[0], receptionist, administrator};
        System.out.println("HOSPITAL APPOINTMENT SYSTEM - JAVA CONSOLE DEMO");
        System.out.println("Patient: " + patient.describeIdentity());
        System.out.println("Role permissions (runtime polymorphism):");
        for (int i = 0; i < people.length; i++) {
            System.out.println(people[i].getFullName() + " -> " + people[i].getRolePermissions());
        }
        System.out.println("All payments, notifications, QR scans and records are simulated locally.");

        int option = -1;
        while (option != 0) {
            System.out.println("\n1 Search doctor availability | 2 Book appointment | 3 View appointments");
            System.out.println("4 Cancel appointment | 5 Reschedule | 6 Check in using QR");
            System.out.println("7 Manage doctor schedule | 8 Reports and audit | 0 Exit");
            System.out.print("Choose: ");
            option = readInt(input);
            switch (option) {
                case 1:
                    System.out.print("Specialty (blank = all): ");
                    String specialty = input.nextLine();
                    System.out.print("Location (blank = all): ");
                    String location = input.nextLine();
                    System.out.print("Date YYYY-MM-DD (blank = all): ");
                    String date = input.nextLine();
                    ui.displaySlots(ui.requestSlots(specialty, location, date));
                    break;
                case 2:
                    ui.displaySlots(ui.requestSlots("", "", ""));
                    System.out.print("Slot ID: ");
                    ui.selectSlot(input.nextLine());
                    System.out.print("Chief complaint: ");
                    String complaint = input.nextLine();
                    System.out.print("Hours until appointment (demo estimate): ");
                    int hours = readInt(input);
                    System.out.print("Payment simulation (type approve or decline): ");
                    String details = input.nextLine();
                    Appointment booked = ui.submitBooking(patient.getUserId(), complaint, details, hours);
                    if (booked != null) ui.showBookingConfirmation(booked.getAppointmentId(), booked.getQrToken());
                    else ui.showError(manager.getLastMessage());
                    break;
                case 3:
                    showAppointments(database.getAppointments(), patient.getUserId());
                    break;
                case 4:
                    System.out.print("Appointment ID: ");
                    String cancelId = input.nextLine();
                    System.out.print("Hours elapsed since booking (demo clock): ");
                    int elapsed = readInt(input);
                    boolean cancelled = ui.requestCancelOrReschedule(cancelId, "cancel", "", elapsed, 0);
                    System.out.println((cancelled ? "SUCCESS: " : "ERROR: ") + manager.getLastMessage());
                    break;
                case 5:
                    System.out.print("Appointment ID: ");
                    String appointmentId = input.nextLine();
                    ui.displaySlots(ui.requestSlots("", "", ""));
                    System.out.print("New slot ID (same doctor): ");
                    String newSlotId = input.nextLine();
                    System.out.print("Hours until new appointment (demo estimate): ");
                    int newHours = readInt(input);
                    boolean changed = ui.requestCancelOrReschedule(appointmentId, "reschedule", newSlotId, 0, newHours);
                    System.out.println((changed ? "SUCCESS: " : "ERROR: ") + manager.getLastMessage());
                    break;
                case 6:
                    System.out.print("QR token (e.g. QR-A-1): ");
                    String qrToken = input.nextLine();
                    boolean checkedIn = manager.checkIn(qrToken, receptionist.getStaffId());
                    System.out.println((checkedIn ? "SUCCESS: " : "ERROR: ") + manager.getLastMessage());
                    break;
                case 7:
                    System.out.print("Doctor ID (D01, D02, D03): ");
                    String doctorId = input.nextLine();
                    System.out.print("New weekly hours: ");
                    String weeklyHours = input.nextLine();
                    System.out.print("Blackout date (blank for none): ");
                    String blackout = input.nextLine();
                    if (slotService.updateDoctorSchedule(doctorId, weeklyHours, blackout)) {
                        System.out.println("Schedule updated for " + slotService.findDoctor(doctorId).getFullName());
                    } else {
                        System.out.println("ERROR: Invalid doctor or hours.");
                    }
                    break;
                case 8:
                    System.out.println(administrator.requestUtilizationReport(reports));
                    System.out.println("Audit: " + administrator.reviewAuditLog(audit, ""));
                    break;
                case 0:
                    System.out.println("Goodbye.");
                    break;
                default:
                    if (option < 0) System.out.println("Please enter a menu number.");
                    else if (option > 8) System.out.println("Menu choices are 0 to 8.");
                    else System.out.println("Unknown choice.");
            }
        }
        input.close();
    }

    private static int readInt(Scanner input) {
        if (input.hasNextInt()) {
            int value = input.nextInt();
            input.nextLine();
            return value;
        }
        input.nextLine();
        return -1;
    }

    private static void showAppointments(Appointment[] appointments, String patientId) {
        int shown = 0;
        for (int i = 0; i < appointments.length; i++) {
            if (appointments[i] != null && appointments[i].getPatientId().equals(patientId)) {
                Appointment item = appointments[i];
                System.out.println(item.getAppointmentId() + " | " + item.getScheduledAt()
                        + " | " + item.getStatus() + " | " + item.getQrToken()
                        + " | PKR " + item.getDepositAmount());
                shown++;
            }
        }
        if (shown == 0) System.out.println("No appointments found.");
    }
}
