package uk.ac.solent.campus.ae1.data

import uk.ac.solent.campus.ae1.model.*

/**
 * In-memory data store pre-populated with the compulsory initial dataset defined in
 * "QHO543 – Initial Seeded Data".
 * No database is used in AE1 as required by the specification.
 */
class SeededDataStore {
    val students: MutableList<Student> = mutableListOf()
    val administrators: MutableList<Administrator> = mutableListOf()
    val events: MutableList<CommunityEvent> = mutableListOf()
    val reservations: MutableList<Reservation> = mutableListOf()

    init {
        resetToSeededData()
    }

    /**
     * Populates (or resets) the in-memory collections to the compulsory initial dataset.
     */
    fun resetToSeededData() {
        students.clear()
        administrators.clear()
        events.clear()
        reservations.clear()

        // 2. Predefined Student Data (S001 - S006)
        students.addAll(
            listOf(
                Student("S001", "Aisha Khan", "Computing", "aisha.khan@student.ac.uk", "aisha", "Student123"),
                Student("S002", "Daniel Green", "Business Management", "daniel.green@student.ac.uk", "daniel", "Student123"),
                Student("S003", "Maya Patel", "Cyber Security", "maya.patel@student.ac.uk", "maya", "Student123"),
                Student("S004", "Omar Hassan", "Software Engineering", "omar.hassan@student.ac.uk", "omar", "Student123"),
                Student("S005", "Emily Carter", "Psychology", "emily.carter@student.ac.uk", "emily", "Student123"),
                Student("S006", "Lucas Martin", "Digital Marketing", "lucas.martin@student.ac.uk", "lucas", "Student123")
            )
        )

        // 3. Predefined Administrator Account
        administrators.add(
            Administrator("admin", "Admin123", "Administrator")
        )

        // 4. Predefined Community Events (E001 - E006)
        events.addAll(
            listOf(
                CommunityEvent("E001", "Python Coding Workshop", "Academic", "Helen Moore", "14 Oct 2026", "14:00", "Computing Lab B12", 6, EventStatus.UPCOMING),
                CommunityEvent("E002", "Five-a-Side Football", "Sports", "Alex Turner", "16 Oct 2026", "17:00", "Sports Hall", 4, EventStatus.UPCOMING),
                CommunityEvent("E003", "Careers in Technology", "Careers", "Rachel Adams", "20 Oct 2026", "13:00", "Lecture Hall 2", 6, EventStatus.UPCOMING),
                CommunityEvent("E004", "Student Wellbeing Workshop", "Wellbeing", "Sarah Lewis", "05 Oct 2026", "11:00", "Room C05", 5, EventStatus.COMPLETED),
                CommunityEvent("E005", "Community Volunteering Day", "Volunteering", "James Wilson", "02 Oct 2026", "09:00", "Community Hub", 6, EventStatus.COMPLETED),
                CommunityEvent("E006", "International Students Social", "Social", "Maria Costa", "18 Oct 2026", "18:00", "Student Union", 5, EventStatus.CANCELLED)
            )
        )

        val studentById = students.associateBy { it.studentId }
        val eventById = events.associateBy { it.eventId }

        // 5. Predefined Reservation Data (R001 - R014)
        reservations.addAll(
            listOf(
                Reservation("R001", studentById.getValue("S001"), eventById.getValue("E001"), ReservationStatus.ACTIVE),
                Reservation("R002", studentById.getValue("S002"), eventById.getValue("E001"), ReservationStatus.ACTIVE),
                Reservation("R003", studentById.getValue("S003"), eventById.getValue("E001"), ReservationStatus.USER_CANCELLED),
                Reservation("R004", studentById.getValue("S001"), eventById.getValue("E002"), ReservationStatus.ACTIVE),
                Reservation("R005", studentById.getValue("S002"), eventById.getValue("E002"), ReservationStatus.ACTIVE),
                Reservation("R006", studentById.getValue("S004"), eventById.getValue("E002"), ReservationStatus.ACTIVE),
                Reservation("R007", studentById.getValue("S005"), eventById.getValue("E002"), ReservationStatus.ACTIVE),
                Reservation("R008", studentById.getValue("S006"), eventById.getValue("E003"), ReservationStatus.ACTIVE),
                Reservation("R009", studentById.getValue("S001"), eventById.getValue("E004"), ReservationStatus.COMPLETED),
                Reservation("R010", studentById.getValue("S003"), eventById.getValue("E004"), ReservationStatus.COMPLETED),
                Reservation("R011", studentById.getValue("S004"), eventById.getValue("E005"), ReservationStatus.COMPLETED),
                Reservation("R012", studentById.getValue("S005"), eventById.getValue("E005"), ReservationStatus.USER_CANCELLED),
                Reservation("R013", studentById.getValue("S002"), eventById.getValue("E006"), ReservationStatus.EVENT_CANCELLED),
                Reservation("R014", studentById.getValue("S006"), eventById.getValue("E006"), ReservationStatus.EVENT_CANCELLED)
            )
        )
    }

    fun findStudentById(studentId: String): Student? =
        students.firstOrNull { it.studentId.equals(studentId.trim(), ignoreCase = true) }

    fun findEventById(eventId: String): CommunityEvent? =
        events.firstOrNull { it.eventId.equals(eventId.trim(), ignoreCase = true) }

    fun findReservationById(reservationId: String): Reservation? =
        reservations.firstOrNull { it.reservationId.equals(reservationId.trim(), ignoreCase = true) }

    /**
     * Counts the effective reservations occupying capacity for a given event:
     * - For Upcoming events: Active reservations
     * - For Completed events: Completed reservations
     * - For Cancelled events: 0 active reservations
     */
    fun countEffectiveReservations(event: CommunityEvent): Int {
        return when (event.status) {
            EventStatus.UPCOMING -> reservations.count {
                it.event.eventId == event.eventId && it.status == ReservationStatus.ACTIVE
            }
            EventStatus.COMPLETED -> reservations.count {
                it.event.eventId == event.eventId && it.status == ReservationStatus.COMPLETED
            }
            EventStatus.CANCELLED -> 0
        }
    }

    /**
     * Generates the next unique Event ID (e.g. E007, E008, ...).
     */
    fun generateNextEventId(): String {
        val maxNum = events.mapNotNull { it.eventId.removePrefix("E").toIntOrNull() }.maxOrNull() ?: 0
        return String.format("E%03d", maxNum + 1)
    }

    /**
     * Generates the next unique Reservation ID (e.g. R015, R016, ...).
     */
    fun generateNextReservationId(): String {
        val maxNum = reservations.mapNotNull { it.reservationId.removePrefix("R").toIntOrNull() }.maxOrNull() ?: 0
        return String.format("R%03d", maxNum + 1)
    }
}
