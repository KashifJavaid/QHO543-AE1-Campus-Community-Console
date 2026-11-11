package uk.ac.solent.campus.ae1.service

import uk.ac.solent.campus.ae1.data.SeededDataStore
import uk.ac.solent.campus.ae1.model.*

/**
 * Search criteria options for Task 1.
 */
enum class EventSearchCriteria {
    ALL, TITLE, CATEGORY, ORGANISER, DATE
}

/**
 * Result wrapper for service operations to provide clear feedback and testability.
 */
sealed class ServiceResult<out T> {
    data class Success<out T>(val data: T, val message: String = "") : ServiceResult<T>()
    data class Failure(val errorMessage: String) : ServiceResult<Nothing>()
}

/**
 * Group Member A – Student Event Functionality (Tasks 1, 2, and 3).
 */
class StudentEventService(private val dataStore: SeededDataStore) {

    /**
     * Task 1: View all community events stored in the system.
     */
    fun getAllEvents(): List<CommunityEvent> = dataStore.events.toList()

    /**
     * Task 1: View only available upcoming events (Upcoming and with remaining capacity > 0).
     */
    fun getAvailableEvents(): List<CommunityEvent> {
        return dataStore.events.filter { event ->
            val currentReservations = dataStore.countEffectiveReservations(event)
            event.status == EventStatus.UPCOMING && event.calculateRemainingCapacity(currentReservations) > 0
        }
    }

    /**
     * Task 1: Search community events by title, category, organiser, or date.
     */
    fun searchEvents(criteria: EventSearchCriteria, query: String): List<CommunityEvent> {
        val trimmed = query.trim()
        if (criteria == EventSearchCriteria.ALL || trimmed.isEmpty()) {
            return dataStore.events.toList()
        }
        return dataStore.events.filter { event ->
            when (criteria) {
                EventSearchCriteria.ALL -> true
                EventSearchCriteria.TITLE -> event.title.contains(trimmed, ignoreCase = true)
                EventSearchCriteria.CATEGORY -> event.category.contains(trimmed, ignoreCase = true)
                EventSearchCriteria.ORGANISER -> event.organiser.contains(trimmed, ignoreCase = true)
                EventSearchCriteria.DATE -> event.date.contains(trimmed, ignoreCase = true)
            }
        }
    }

    /**
     * Task 1: Select a specific event by ID and format its detailed view including
     * maximum capacity, number of reservations, remaining capacity, and current status.
     */
    fun getEventDetailedInfo(eventId: String): ServiceResult<String> {
        val event = dataStore.findEventById(eventId)
            ?: return ServiceResult.Failure("Event with ID '${eventId.trim()}' was not found.")
        val count = dataStore.countEffectiveReservations(event)
        return ServiceResult.Success(event.formatDetailedView(count))
    }

    /**
     * Helper to retrieve the current reservation count for an event.
     */
    fun getReservationCountForEvent(event: CommunityEvent): Int {
        return dataStore.countEffectiveReservations(event)
    }

    /**
     * Task 2: Make Event Reservation and Print Confirmation.
     * Verifies that:
     * 1. The student exists.
     * 2. The selected event exists and is Upcoming (not Completed or Cancelled).
     * 3. The event has remaining capacity (> 0, not Full).
     * 4. The student does not already have an Active reservation for the same event.
     */
    fun makeReservation(studentId: String, eventId: String): ServiceResult<Reservation> {
        val student = dataStore.findStudentById(studentId)
            ?: return ServiceResult.Failure("Student with ID '${studentId.trim()}' does not exist.")

        val event = dataStore.findEventById(eventId)
            ?: return ServiceResult.Failure("Event with ID '${eventId.trim()}' does not exist.")

        if (event.status == EventStatus.CANCELLED) {
            return ServiceResult.Failure("Cannot reserve '${event.title}' (${event.eventId}): This event has been cancelled.")
        }
        if (event.status == EventStatus.COMPLETED) {
            return ServiceResult.Failure("Cannot reserve '${event.title}' (${event.eventId}): This event is already completed.")
        }

        val currentActiveReservations = dataStore.countEffectiveReservations(event)
        val remainingCapacity = event.calculateRemainingCapacity(currentActiveReservations)
        if (remainingCapacity <= 0) {
            return ServiceResult.Failure("Cannot reserve '${event.title}' (${event.eventId}): Event is full (Max capacity: ${event.maxCapacity}).")
        }

        val alreadyReserved = dataStore.reservations.any {
            it.student.studentId.equals(student.studentId, ignoreCase = true) &&
                it.event.eventId.equals(event.eventId, ignoreCase = true) &&
                it.status == ReservationStatus.ACTIVE
        }
        if (alreadyReserved) {
            return ServiceResult.Failure("Student ${student.fullName} (${student.studentId}) already holds an Active reservation for '${event.title}'.")
        }

        val newReservationId = dataStore.generateNextReservationId()
        val reservation = Reservation(
            reservationId = newReservationId,
            student = student,
            event = event,
            status = ReservationStatus.ACTIVE
        )
        dataStore.reservations.add(reservation)

        return ServiceResult.Success(
            data = reservation,
            message = reservation.formatConfirmationTicket()
        )
    }

    /**
     * Task 3: View all reservations for a given student (past, current, and upcoming).
     */
    fun getReservationsForStudent(studentId: String): ServiceResult<List<Reservation>> {
        val student = dataStore.findStudentById(studentId)
            ?: return ServiceResult.Failure("Student with ID '${studentId.trim()}' does not exist.")

        val studentReservations = dataStore.reservations.filter {
            it.student.studentId.equals(student.studentId, ignoreCase = true)
        }
        return ServiceResult.Success(studentReservations)
    }

    /**
     * Task 3: Cancel an active reservation for an upcoming event.
     * Updates status to User Cancelled while keeping the record visible in history,
     * and automatically frees up the event's available capacity.
     */
    fun cancelStudentReservation(studentId: String, reservationId: String): ServiceResult<Reservation> {
        val student = dataStore.findStudentById(studentId)
            ?: return ServiceResult.Failure("Student with ID '${studentId.trim()}' does not exist.")

        val reservation = dataStore.findReservationById(reservationId)
            ?: return ServiceResult.Failure("Reservation with ID '${reservationId.trim()}' does not exist.")

        if (!reservation.student.studentId.equals(student.studentId, ignoreCase = true)) {
            return ServiceResult.Failure("Reservation '${reservation.reservationId}' does not belong to student ${student.fullName} (${student.studentId}).")
        }

        return when (reservation.status) {
            ReservationStatus.COMPLETED ->
                ServiceResult.Failure("Cannot cancel reservation '${reservation.reservationId}': Completed reservations cannot be cancelled.")
            ReservationStatus.USER_CANCELLED ->
                ServiceResult.Failure("Cannot cancel reservation '${reservation.reservationId}': Reservation was already cancelled by the student.")
            ReservationStatus.EVENT_CANCELLED ->
                ServiceResult.Failure("Cannot cancel reservation '${reservation.reservationId}': Event was already cancelled by an administrator.")
            ReservationStatus.ACTIVE -> {
                if (reservation.event.status != EventStatus.UPCOMING) {
                    ServiceResult.Failure("Cannot cancel reservation '${reservation.reservationId}': Associated event is not upcoming.")
                } else {
                    reservation.status = ReservationStatus.USER_CANCELLED
                    val updatedRemaining = reservation.event.calculateRemainingCapacity(
                        dataStore.countEffectiveReservations(reservation.event)
                    )
                    ServiceResult.Success(
                        data = reservation,
                        message = "Reservation ${reservation.reservationId} for '${reservation.event.title}' has been updated to '${ReservationStatus.USER_CANCELLED.displayName}'. " +
                                "Updated event remaining capacity: $updatedRemaining/${reservation.event.maxCapacity}."
                    )
                }
            }
        }
    }
}

