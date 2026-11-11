package uk.ac.solent.campus.ae1.model

/**
 * Represents a community event organised within the university.
 */
class CommunityEvent(
    val eventId: String,
    var title: String,
    var category: String,
    var organiser: String,
    var date: String,
    var time: String,
    var location: String,
    var maxCapacity: Int,
    var status: EventStatus
) {
    /**
     * Calculates remaining capacity based on current active or completed reservations.
     */
    fun calculateRemainingCapacity(currentReservationCount: Int): Int {
        return (maxCapacity - currentReservationCount).coerceAtLeast(0)
    }

    /**
     * Calculates percentage of event capacity used.
     */
    fun calculateCapacityPercentage(currentReservationCount: Int): Double {
        if (maxCapacity <= 0) return 0.0
        return (currentReservationCount.toDouble() / maxCapacity.toDouble()) * 100.0
    }

    /**
     * Returns the display status of the event.
     * Per Task 1 specification: clearly indicates whether an event is
     * Upcoming, Full, Completed, or Cancelled.
     */
    fun getDisplayStatus(activeReservationCount: Int): String {
        return when (status) {
            EventStatus.UPCOMING -> {
                if (calculateRemainingCapacity(activeReservationCount) == 0) "Full" else "Upcoming"
            }
            EventStatus.COMPLETED -> "Completed"
            EventStatus.CANCELLED -> "Cancelled"
        }
    }

    /**
     * Formats complete event details for console output.
     */
    fun formatDetailedView(reservationCount: Int): String {
        val remaining = calculateRemainingCapacity(reservationCount)
        val displayStatus = getDisplayStatus(reservationCount)
        return buildString {
            appendLine("------------------------------------------------------------")
            appendLine("Event ID           : $eventId")
            appendLine("Title              : $title")
            appendLine("Category           : $category")
            appendLine("Organiser          : $organiser")
            appendLine("Date               : $date")
            appendLine("Time               : $time")
            appendLine("Location           : $location")
            appendLine("Maximum Capacity   : $maxCapacity")
            appendLine("Reservations Count : $reservationCount")
            appendLine("Remaining Capacity : $remaining")
            appendLine("Current Status     : $displayStatus (Lifecycle: ${status.displayName})")
            append("------------------------------------------------------------")
        }
    }

    /**
     * Formats a single-row tabular summary of the event.
     */
    fun formatRowSummary(reservationCount: Int): String {
        val remaining = calculateRemainingCapacity(reservationCount)
        val displayStatus = getDisplayStatus(reservationCount)
        return String.format(
            "%-5s | %-28s | %-12s | %-13s | %-11s | %-5s | %-18s | Cap: %d/%d (Rem: %d) | %-10s",
            eventId,
            title.take(28),
            category.take(12),
            organiser.take(13),
            date,
            time,
            location.take(18),
            reservationCount,
            maxCapacity,
            remaining,
            displayStatus
        )
    }
}
