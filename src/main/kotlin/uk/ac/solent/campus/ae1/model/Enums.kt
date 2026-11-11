package uk.ac.solent.campus.ae1.model

/**
 * Defines user roles within the Campus Community Management System.
 */
enum class UserRole(val displayName: String) {
    STUDENT("Student"),
    ADMINISTRATOR("Administrator")
}

/**
 * Defines the lifecycle status of a CommunityEvent.
 */
enum class EventStatus(val displayName: String) {
    UPCOMING("Upcoming"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled");

    companion object {
        fun fromDisplayName(value: String): EventStatus {
            return entries.firstOrNull { it.displayName.equals(value.trim(), ignoreCase = true) }
                ?: throw IllegalArgumentException("Unknown event status: $value")
        }
    }
}

/**
 * Defines the status of a student's event Reservation.
 */
enum class ReservationStatus(val displayName: String) {
    ACTIVE("Active"),
    COMPLETED("Completed"),
    USER_CANCELLED("User Cancelled"),
    EVENT_CANCELLED("Event Cancelled");

    companion object {
        fun fromDisplayName(value: String): ReservationStatus {
            return entries.firstOrNull { it.displayName.equals(value.trim(), ignoreCase = true) }
                ?: throw IllegalArgumentException("Unknown reservation status: $value")
        }
    }
}
