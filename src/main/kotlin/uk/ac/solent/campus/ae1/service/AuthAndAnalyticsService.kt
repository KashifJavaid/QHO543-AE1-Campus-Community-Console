package uk.ac.solent.campus.ae1.service

import uk.ac.solent.campus.ae1.data.SeededDataStore
import uk.ac.solent.campus.ae1.model.*

/**
 * Analytical metric for a single completed event (Task 9).
 */
data class CompletedEventAnalytics(
    val event: CommunityEvent,
    val completedReservationsCount: Int,
    val percentageCapacityUsed: Double
)

/**
 * Analytical metric for a single upcoming event (Task 9).
 */
data class UpcomingEventAnalytics(
    val event: CommunityEvent,
    val currentActiveReservations: Int,
    val remainingCapacity: Int,
    val percentageCapacityReserved: Double
)

/**
 * Comprehensive Community Event Analytics summary (Task 9).
 */
data class AnalyticsSummary(
    val completedMetrics: List<CompletedEventAnalytics>,
    val mostPopularCompletedEvents: List<CompletedEventAnalytics>,
    val leastPopularCompletedEvents: List<CompletedEventAnalytics>,
    val upcomingMetrics: List<UpcomingEventAnalytics>,
    val mostPopularUpcomingEvents: List<UpcomingEventAnalytics>,
    val cancelledEventsCount: Int,
    val reservationsAffectedByEventCancellations: Int
)

/**
 * Group Member C – Registration, Authentication and Analytics (Tasks 7, 8, and 9).
 */
class AuthAndAnalyticsService(private val dataStore: SeededDataStore) {

    /**
     * Task 7: Student Registration.
     * Validates entered information and prevents duplicate Student IDs, usernames, and email addresses.
     */
    fun registerStudent(
        studentId: String,
        fullName: String,
        email: String,
        course: String,
        username: String,
        password: String
    ): Result<Student> {
        val cleanId = studentId.trim()
        val cleanName = fullName.trim()
        val cleanEmail = email.trim()
        val cleanCourse = course.trim()
        val cleanUsername = username.trim()

        if (cleanId.isEmpty()) return Result.failure(IllegalArgumentException("Student ID must not be blank."))
        if (cleanName.isEmpty()) return Result.failure(IllegalArgumentException("Full name must not be blank."))
        if (cleanEmail.isEmpty() || !cleanEmail.contains("@") || !cleanEmail.contains(".")) {
            return Result.failure(IllegalArgumentException("A valid email address is required (e.g. name@student.ac.uk)."))
        }
        if (cleanCourse.isEmpty()) return Result.failure(IllegalArgumentException("Course must not be blank."))
        if (cleanUsername.isEmpty()) return Result.failure(IllegalArgumentException("Username must not be blank."))
        if (password.length < 4) return Result.failure(IllegalArgumentException("Password must be at least 4 characters long."))

        // Check duplicate Student ID
        if (dataStore.students.any { it.studentId.equals(cleanId, ignoreCase = true) }) {
            return Result.failure(IllegalArgumentException("A student with Student ID '$cleanId' already exists."))
        }

        // Check duplicate Email address
        if (dataStore.students.any { it.email.equals(cleanEmail, ignoreCase = true) }) {
            return Result.failure(IllegalArgumentException("A student with email address '$cleanEmail' is already registered."))
        }

        // Check duplicate Username across both students and administrators
        val usernameTakenByStudent = dataStore.students.any { it.username.equals(cleanUsername, ignoreCase = true) }
        val usernameTakenByAdmin = dataStore.administrators.any { it.username.equals(cleanUsername, ignoreCase = true) }
        if (usernameTakenByStudent || usernameTakenByAdmin) {
            return Result.failure(IllegalArgumentException("Username '$cleanUsername' is already taken."))
        }

        val newStudent = Student(
            studentId = cleanId,
            fullName = cleanName,
            course = cleanCourse,
            email = cleanEmail,
            username = cleanUsername,
            password = password
        )
        dataStore.students.add(newStudent)
        return Result.success(newStudent)
    }

    /**
     * Task 8: Authentication and Role Management.
     * Authenticates credentials against both student and administrator accounts,
     * returning the polymorphic User (Student or Administrator) upon valid login.
     */
    fun authenticateUser(username: String, password: String): Result<User> {
        val cleanUsername = username.trim()
        if (cleanUsername.isEmpty() || password.isEmpty()) {
            return Result.failure(IllegalArgumentException("Username and password must not be empty."))
        }

        val matchingAdmin = dataStore.administrators.firstOrNull {
            it.username.equals(cleanUsername, ignoreCase = true) && it.verifyPassword(password)
        }
        if (matchingAdmin != null) {
            return Result.success(matchingAdmin)
        }

        val matchingStudent = dataStore.students.firstOrNull {
            it.username.equals(cleanUsername, ignoreCase = true) && it.verifyPassword(password)
        }
        if (matchingStudent != null) {
            return Result.success(matchingStudent)
        }

        return Result.failure(IllegalArgumentException("Invalid username or password. Access denied."))
    }

    /**
     * Task 9: Community Event Analytics.
     * Dynamically calculates all analytical statistics from the current in-memory data.
     */
    fun generateAnalytics(): AnalyticsSummary {
        // 1. Completed Events Analytics
        val completedEvents = dataStore.events.filter { it.status == EventStatus.COMPLETED }
        val completedMetrics = completedEvents.map { event ->
            val resCount = dataStore.reservations.count {
                it.event.eventId == event.eventId && it.status == ReservationStatus.COMPLETED
            }
            CompletedEventAnalytics(
                event = event,
                completedReservationsCount = resCount,
                percentageCapacityUsed = event.calculateCapacityPercentage(resCount)
            )
        }

        val maxCompletedCount = completedMetrics.maxOfOrNull { it.completedReservationsCount }
        val minCompletedCount = completedMetrics.minOfOrNull { it.completedReservationsCount }

        val mostPopularCompleted = if (maxCompletedCount != null) {
            completedMetrics.filter { it.completedReservationsCount == maxCompletedCount }
        } else emptyList()

        val leastPopularCompleted = if (minCompletedCount != null) {
            completedMetrics.filter { it.completedReservationsCount == minCompletedCount }
        } else emptyList()

        // 2. Upcoming Events Analytics
        val upcomingEvents = dataStore.events.filter { it.status == EventStatus.UPCOMING }
        val upcomingMetrics = upcomingEvents.map { event ->
            val activeCount = dataStore.reservations.count {
                it.event.eventId == event.eventId && it.status == ReservationStatus.ACTIVE
            }
            UpcomingEventAnalytics(
                event = event,
                currentActiveReservations = activeCount,
                remainingCapacity = event.calculateRemainingCapacity(activeCount),
                percentageCapacityReserved = event.calculateCapacityPercentage(activeCount)
            )
        }

        val maxUpcomingCount = upcomingMetrics.maxOfOrNull { it.currentActiveReservations }
        val mostPopularUpcoming = if (maxUpcomingCount != null) {
            upcomingMetrics.filter { it.currentActiveReservations == maxUpcomingCount }
        } else emptyList()

        // 3. Cancelled Events Summary
        val cancelledEventsCount = dataStore.events.count { it.status == EventStatus.CANCELLED }
        val affectedReservationsCount = dataStore.reservations.count {
            it.status == ReservationStatus.EVENT_CANCELLED
        }

        return AnalyticsSummary(
            completedMetrics = completedMetrics,
            mostPopularCompletedEvents = mostPopularCompleted,
            leastPopularCompletedEvents = leastPopularCompleted,
            upcomingMetrics = upcomingMetrics,
            mostPopularUpcomingEvents = mostPopularUpcoming,
            cancelledEventsCount = cancelledEventsCount,
            reservationsAffectedByEventCancellations = affectedReservationsCount
        )
    }

    /**
     * Formats the complete Task 9 Analytics Report for console display.
     */
    fun formatAnalyticsReport(): String {
        val summary = generateAnalytics()
        return buildString {
            appendLine("====================================================================================")
            appendLine("                     COMMUNITY EVENT ANALYTICS REPORT (TASK 9)                      ")
            appendLine("====================================================================================")

            appendLine("\n[1] COMPLETED EVENTS ANALYTICS")
            appendLine("------------------------------------------------------------------------------------")
            if (summary.completedMetrics.isEmpty()) {
                appendLine("  No completed events in the system.")
            } else {
                summary.completedMetrics.forEach { m ->
                    appendLine(
                        String.format(
                            "  %-5s | %-30s | Reservations: %d/%d | Capacity Used: %.1f%%",
                            m.event.eventId,
                            m.event.title,
                            m.completedReservationsCount,
                            m.event.maxCapacity,
                            m.percentageCapacityUsed
                        )
                    )
                }
                val mostPopStr = summary.mostPopularCompletedEvents.joinToString(", ") {
                    "${it.event.title} (${it.event.eventId}: ${it.completedReservationsCount} reservations, ${String.format("%.1f%%", it.percentageCapacityUsed)})"
                }
                val leastPopStr = summary.leastPopularCompletedEvents.joinToString(", ") {
                    "${it.event.title} (${it.event.eventId}: ${it.completedReservationsCount} reservations, ${String.format("%.1f%%", it.percentageCapacityUsed)})"
                }
                appendLine("  -> Most Popular Completed Event(s)  : $mostPopStr")
                appendLine("  -> Least Popular Completed Event(s) : $leastPopStr")
            }

            appendLine("\n[2] UPCOMING EVENTS ANALYTICS")
            appendLine("------------------------------------------------------------------------------------")
            if (summary.upcomingMetrics.isEmpty()) {
                appendLine("  No upcoming events in the system.")
            } else {
                summary.upcomingMetrics.forEach { m ->
                    appendLine(
                        String.format(
                            "  %-5s | %-30s | Reserved: %d/%d | Remaining: %d | Reserved: %.1f%%",
                            m.event.eventId,
                            m.event.title,
                            m.currentActiveReservations,
                            m.event.maxCapacity,
                            m.remainingCapacity,
                            m.percentageCapacityReserved
                        )
                    )
                }
                val mostPopUpcomingStr = summary.mostPopularUpcomingEvents.joinToString(", ") {
                    "${it.event.title} (${it.event.eventId}: ${it.currentActiveReservations} reservations, ${String.format("%.1f%%", it.percentageCapacityReserved)})"
                }
                appendLine("  -> Most Popular Upcoming Event(s)   : $mostPopUpcomingStr")
            }

            appendLine("\n[3] CANCELLED EVENTS SUMMARY")
            appendLine("------------------------------------------------------------------------------------")
            appendLine("  Total Cancelled Events                               : ${summary.cancelledEventsCount}")
            appendLine("  Student Reservations Affected by Event Cancellations : ${summary.reservationsAffectedByEventCancellations}")
            append("====================================================================================")
        }
    }
}
