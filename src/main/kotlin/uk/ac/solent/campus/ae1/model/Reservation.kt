package uk.ac.solent.campus.ae1.model

/**
 * Represents a student's reservation for a specific CommunityEvent.
 */
class Reservation(
    val reservationId: String,
    val student: Student,
    val event: CommunityEvent,
    var status: ReservationStatus
) {
    /**
     * Determines whether this reservation can be cancelled by the student (Task 3).
     * Only Active reservations for Upcoming events can be cancelled by a student.
     */
    fun isCancellableByStudent(): Boolean {
        return status == ReservationStatus.ACTIVE && event.status == EventStatus.UPCOMING
    }

    /**
     * Produces the exact reservation confirmation format required by Task 2.
     */
    fun formatConfirmationTicket(): String {
        return buildString {
            appendLine("******************************")
            appendLine("CAMPUS COMMUNITY")
            appendLine("EVENT RESERVATION")
            appendLine()
            appendLine("Student: ${student.fullName}")
            appendLine("Student ID: ${student.studentId}")
            appendLine("Event: ${event.title}")
            appendLine("Category: ${event.category}")
            appendLine("Date: ${event.date}")
            appendLine("Time: ${event.time}")
            appendLine("Location: ${event.location}")
            appendLine("Reservation ID: $reservationId")
            append("******************************")
        }
    }

    /**
     * Formats reservation record for student reservation history (Task 3).
     */
    fun formatReservationRow(): String {
        return String.format(
            "%-6s | Event: %-5s - %-28s | %-11s %-5s | %-18s | Status: %-15s",
            reservationId,
            event.eventId,
            event.title.take(28),
            event.date,
            event.time,
            event.location.take(18),
            status.displayName
        )
    }
}
