package uk.ac.solent.campus.ae1.service

import uk.ac.solent.campus.ae1.data.SeededDataStore
import uk.ac.solent.campus.ae1.model.CommunityEvent
import uk.ac.solent.campus.ae1.model.EventStatus
import uk.ac.solent.campus.ae1.model.ReservationStatus

/**
 * Filter modes for Task 4 (Administrator Event Viewing).
 */
enum class AdminEventFilter(val label: String) {
    ALL("All Events"),
    UPCOMING("Upcoming Events"),
    COMPLETED("Completed Events"),
    CANCELLED("Cancelled Events")
}

/**
 * Group Member B – Community Event Management (Admin) (Tasks 4, 5, and 6).
 */
class AdminEventService(private val dataStore: SeededDataStore) {

    /**
     * Task 4: View Community Events in four different ways:
     * All events, Upcoming events, Completed events, Cancelled events.
     */
    fun getEventsByFilter(filter: AdminEventFilter): List<CommunityEvent> {
        return when (filter) {
            AdminEventFilter.ALL -> dataStore.events.toList()
            AdminEventFilter.UPCOMING -> dataStore.events.filter { it.status == EventStatus.UPCOMING }
            AdminEventFilter.COMPLETED -> dataStore.events.filter { it.status == EventStatus.COMPLETED }
            AdminEventFilter.CANCELLED -> dataStore.events.filter { it.status == EventStatus.CANCELLED }
        }
    }

    /**
     * Helper to retrieve the current reservation count for an event.
     */
    fun getReservationCountForEvent(event: CommunityEvent): Int {
        return dataStore.countEffectiveReservations(event)
    }

    /**
     * Task 5 (Part 1): Add a new community event to the system.
     * Validates all required fields and integrates the new event with status Upcoming.
     */
    fun addCommunityEvent(
        title: String,
        category: String,
        organiser: String,
        date: String,
        time: String,
        location: String,
        maxCapacity: Int
    ): Result<CommunityEvent> {
        if (title.isBlank()) return Result.failure(IllegalArgumentException("Event title must not be blank."))
        if (category.isBlank()) return Result.failure(IllegalArgumentException("Event category must not be blank."))
        if (organiser.isBlank()) return Result.failure(IllegalArgumentException("Event organiser must not be blank."))
        if (date.isBlank()) return Result.failure(IllegalArgumentException("Event date must not be blank."))
        if (time.isBlank()) return Result.failure(IllegalArgumentException("Event time must not be blank."))
        if (location.isBlank()) return Result.failure(IllegalArgumentException("Event location must not be blank."))
        if (maxCapacity <= 0) return Result.failure(IllegalArgumentException("Maximum capacity must be a positive integer (> 0)."))

        val newEventId = dataStore.generateNextEventId()
        val newEvent = CommunityEvent(
            eventId = newEventId,
            title = title.trim(),
            category = category.trim(),
            organiser = organiser.trim(),
            date = date.trim(),
            time = time.trim(),
            location = location.trim(),
            maxCapacity = maxCapacity,
            status = EventStatus.UPCOMING
        )
        dataStore.events.add(newEvent)
        return Result.success(newEvent)
    }

    /**
     * Task 5 (Part 2): Modify an existing community event.
     * Note: Per specification, event cancellation must NOT be performed through this task.
     * Any parameter left null or blank retains its current value.
     */
    fun modifyCommunityEvent(
        eventId: String,
        newTitle: String? = null,
        newCategory: String? = null,
        newOrganiser: String? = null,
        newDate: String? = null,
        newTime: String? = null,
        newLocation: String? = null,
        newMaxCapacity: Int? = null
    ): Result<CommunityEvent> {
        val event = dataStore.findEventById(eventId)
            ?: return Result.failure(IllegalArgumentException("Event with ID '${eventId.trim()}' does not exist."))

        val currentReservations = dataStore.countEffectiveReservations(event)
        if (newMaxCapacity != null) {
            if (newMaxCapacity <= 0) {
                return Result.failure(IllegalArgumentException("Maximum capacity must be greater than 0."))
            }
            if (newMaxCapacity < currentReservations) {
                return Result.failure(
                    IllegalArgumentException(
                        "Cannot reduce maximum capacity to $newMaxCapacity because there are already $currentReservations active/completed reservations."
                    )
                )
            }
        }

        if (!newTitle.isNullOrBlank()) event.title = newTitle.trim()
        if (!newCategory.isNullOrBlank()) event.category = newCategory.trim()
        if (!newOrganiser.isNullOrBlank()) event.organiser = newOrganiser.trim()
        if (!newDate.isNullOrBlank()) event.date = newDate.trim()
        if (!newTime.isNullOrBlank()) event.time = newTime.trim()
        if (!newLocation.isNullOrBlank()) event.location = newLocation.trim()
        if (newMaxCapacity != null) event.maxCapacity = newMaxCapacity

        return Result.success(event)
    }

    /**
     * Task 6: Cancel an existing upcoming community event.
     * Updates event status to Cancelled, prevents future reservations, and updates all
     * Active student reservations for this event to Event Cancelled.
     */
    fun cancelCommunityEvent(eventId: String): Result<Pair<CommunityEvent, Int>> {
        val event = dataStore.findEventById(eventId)
            ?: return Result.failure(IllegalArgumentException("Event with ID '${eventId.trim()}' does not exist."))

        if (event.status == EventStatus.CANCELLED) {
            return Result.failure(IllegalArgumentException("Event '${event.title}' (${event.eventId}) is already cancelled."))
        }
        if (event.status == EventStatus.COMPLETED) {
            return Result.failure(IllegalArgumentException("Event '${event.title}' (${event.eventId}) is already completed and cannot be cancelled."))
        }

        event.status = EventStatus.CANCELLED

        var affectedReservationsCount = 0
        dataStore.reservations.forEach { reservation ->
            if (reservation.event.eventId.equals(event.eventId, ignoreCase = true) &&
                reservation.status == ReservationStatus.ACTIVE
            ) {
                reservation.status = ReservationStatus.EVENT_CANCELLED
                affectedReservationsCount++
            }
        }

        return Result.success(Pair(event, affectedReservationsCount))
    }
}
