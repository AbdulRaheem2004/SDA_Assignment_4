package hospital.tests;

import hospital.model.Appointment;
import hospital.model.Doctor;
import hospital.model.Patient;
import hospital.model.TimeSlot;
import hospital.model.User;
import hospital.service.ApptDB;
import hospital.service.AppointmentMgr;
import hospital.service.AuditLog;
import hospital.service.EmailNotification;
import hospital.service.NotificationChannel;
import hospital.service.PaymentService;
import hospital.service.SMSNotification;
import hospital.service.SlotService;

public class AcceptanceTest {
    private static int checks = 0;

    private static void check(boolean condition, String name) {
        if (!condition) {
            System.out.println("FAILED: " + name);
            System.exit(1);
        }
        checks++;
    }

    public static void main(String[] args) {
        Patient patient = new Patient("P01", "Ali", "ali@test.local", "03001234567", "PN1");
        Doctor doctor = new Doctor("D01", "Dr One", "one@test.local", "0300", "PR1", "Cardiology", "Peshawar", 2000);
        Doctor anotherDoctor = new Doctor("D02", "Dr Two", "two@test.local", "0301", "PR2", "Skin", "Peshawar", 3000);
        Doctor[] doctors = {doctor, anotherDoctor};
        TimeSlot[] slots = {
            new TimeSlot("S1", "D01", "2026-11-25 09:00", "2026-11-25 09:30"),
            new TimeSlot("S2", "D01", "2026-11-25 10:00", "2026-11-25 10:30"),
            new TimeSlot("S3", "D02", "2026-11-25 11:00", "2026-11-25 11:30"),
            new TimeSlot("S4", "D01", "2026-11-25 12:00", "2026-11-25 12:30")
        };
        SlotService slotService = new SlotService(slots, doctors);
        PaymentService payments = new PaymentService("TEST", 20);
        ApptDB database = new ApptDB("TEST", 20);
        AuditLog audit = new AuditLog("TEST-AUDIT");
        NotificationChannel[] channels = {
            new SMSNotification("SDA", "+92"), new EmailNotification("test@local", "help@local")
        };
        AppointmentMgr manager = new AppointmentMgr(slotService, payments, database, audit, channels, patient);

        check(manager.searchSlots("Cardiology", "Peshawar", "2026-11-25")[0] == slots[0], "search");
        check(slotService.searchSlots("Skin")[0] == slots[2], "overloaded search");
        check(patient.selectAppointmentSlot(slots[0]), "select open slot");
        Appointment first = manager.bookAppointment("P01", "S1", "Pain", "approve", 72);
        check(first != null && first.getStatus().equals("CONFIRMED"), "confirmed booking");
        check(slots[0].getStatus().equals("BOOKED"), "slot booked");
        check(payments.verifyTransaction(first.getPaymentTransactionId()), "payment authorized");
        check(manager.bookAppointment("P01", "S1", "Repeat", "approve", 72) == null, "no double booking");
        check(manager.bookAppointment("P01", "S2", "Pain", "decline", 72) == null, "declined payment");
        check(slots[1].isAvailable(), "release on decline");
        check(!manager.cancelOrReschedule(first.getAppointmentId(), "reschedule", "S3", 0, 72), "other doctor reschedule denied");
        check(manager.cancelOrReschedule(first.getAppointmentId(), "reschedule", "S2", 0, 70), "reschedule same doctor");
        check(slots[0].isAvailable() && slots[1].getStatus().equals("BOOKED"), "swap slot state");
        check(first.getSlotId().equals("S2"), "appointment transferred");
        check(!manager.cancelOrReschedule(first.getAppointmentId(), "cancel", "", -1, 0), "negative elapsed invalid");
        check(manager.cancelOrReschedule(first.getAppointmentId(), "cancel", "", 0, 0), "early cancel");
        check(manager.getLastRefundAmount() == 2000.0, "full refund");
        check(slots[1].isAvailable(), "cancel frees slot");
        check(!manager.cancelOrReschedule(first.getAppointmentId(), "cancel", "", 0, 0), "duplicate cancel denied");

        Appointment second = manager.bookAppointment("P01", "S4", "Follow up", "approve", 36);
        check(second != null, "second booking");
        check(manager.cancelOrReschedule(second.getAppointmentId(), "cancel", "", 20, 0), "late cancel");
        check(manager.getLastRefundAmount() == 1000.0, "half refund");

        Appointment third = manager.bookAppointment("P01", "S3", "Skin issue", "approve", 48);
        check(third != null, "third booking");
        check(manager.checkIn(third.getQrToken(), "ST1"), "check in");
        check(!manager.checkIn(third.getQrToken(), "ST1"), "duplicate check in denied");
        User[] people = {patient, doctor};
        check(!people[0].getRolePermissions().equals(people[1].getRolePermissions()), "runtime polymorphism");
        check(audit.getEntryCount() >= 7, "audit entries");
        System.out.println("PASS: " + checks + " acceptance checks");
    }
}
