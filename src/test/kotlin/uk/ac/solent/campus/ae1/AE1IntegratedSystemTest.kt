package uk.ac.solent.campus.ae1

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import uk.ac.solent.campus.ae1.data.SeededDataStore
import uk.ac.solent.campus.ae1.model.*
import uk.ac.solent.campus.ae1.service.*

/**
 * Automated verification suite for AE1 (Tasks 1 through 9) and compulsory seeded data.
 */
class AE1IntegratedSystemTest {

    private lateinit var dataStore: SeededDataStore
    private lateinit var studentService: StudentEventService
    private lateinit var adminService: AdminEventService
    private lateinit var authAnalyticsService: AuthAndAnalyticsService

    @BeforeEach
    fun setUp() {
        dataStore = SeededDataStore()
        studentService = StudentEventService(dataStore)
        adminService = AdminEventService(dataStore)
        authAnalyticsService = AuthAndAnalyticsService(dataStore)
    }

    // --- Task 1 Tests: View and Search Community Events ---
    @Test
    fun `Task 1 - View all events and search by title, category, organiser, and date`() {
        val allEvents = studentService.getAllEvents()
        assertEquals(6, allEvents.size, "Compulsory dataset must contain 6 initial events")

        val byTitle = studentService.searchEvents(EventSearchCriteria.TITLE, "Python")
        assertEquals(1, byTitle.size)
        assertEquals("E001", byTitle.first().eventId)

        val byCategory = studentService.searchEvents(EventSearchCriteria.CATEGORY, "Sports")
        assertEquals(1, byCategory.size)
        assertEquals("E002", byCategory.first().eventId)
        assertEquals("Full", byCategory.first().getDisplayStatus(studentService.getReservationCountForEvent(byCategory.first())))

        val byOrganiser = studentService.searchEvents(EventSearchCriteria.ORGANISER, "Rachel Adams")
        assertEquals(1, byOrganiser.size)
        assertEquals("E003", byOrganiser.first().eventId)

        val byDate = studentService.searchEvents(EventSearchCriteria.DATE, "05 Oct 2026")
        assertEquals(1, byDate.size)
        assertEquals("E004", byDate.first().eventId)
    }

    // --- Task 2 Tests: Make Event Reservation and Print Confirmation ---
    @Test
    fun `Task 2 - Make reservation succeeds for available event and formats exact confirmation`() {
        // S003 (Maya Patel) had User Cancelled R003 on E001, so S003 has no Active reservation on E001
        val result = studentService.makeReservation("S003", "E001")
        assertTrue(result is ServiceResult.Success)
        val reservation = (result as ServiceResult.Success).data
        assertEquals("R015", reservation.reservationId)
        assertEquals(ReservationStatus.ACTIVE, reservation.status)
        assertEquals(3, studentService.getReservationCountForEvent(dataStore.findEventById("E001")!!))

        val ticket = reservation.formatConfirmationTicket()
        assertTrue(ticket.contains("CAMPUS COMMUNITY"))
        assertTrue(ticket.contains("EVENT RESERVATION"))
        assertTrue(ticket.contains("Student: Maya Patel"))
        assertTrue(ticket.contains("Student ID: S003"))
        assertTrue(ticket.contains("Event: Python Coding Workshop"))
        assertTrue(ticket.contains("Reservation ID: R015"))
    }

    @Test
    fun `Task 2 - Make reservation rejects full event, duplicate active booking, completed, and cancelled events`() {
        // E002 is full (4/4)
        val fullResult = studentService.makeReservation("S003", "E002")
        assertTrue(fullResult is ServiceResult.Failure)

        // S001 already has an Active reservation (R001) on E001
        val duplicateResult = studentService.makeReservation("S001", "E001")
        assertTrue(duplicateResult is ServiceResult.Failure)

        // E004 is Completed
        val completedResult = studentService.makeReservation("S002", "E004")
        assertTrue(completedResult is ServiceResult.Failure)

        // E006 is Cancelled
        val cancelledResult = studentService.makeReservation("S001", "E006")
        assertTrue(cancelledResult is ServiceResult.Failure)
    }

    // --- Task 3 Tests: View Reservations and Cancel Active Reservations ---
    @Test
    fun `Task 3 - Student can cancel Active reservation and status becomes User Cancelled`() {
        val cancelResult = studentService.cancelStudentReservation("S001", "R001")
        assertTrue(cancelResult is ServiceResult.Success)
        val updatedReservation = dataStore.findReservationById("R001")!!
        assertEquals(ReservationStatus.USER_CANCELLED, updatedReservation.status)

        // Remaining capacity of E001 increases from 4 to 5 (only 1 active reservation left)
        val e001 = dataStore.findEventById("E001")!!
        assertEquals(1, studentService.getReservationCountForEvent(e001))
        assertEquals(5, e001.calculateRemainingCapacity(studentService.getReservationCountForEvent(e001)))
    }

    @Test
    fun `Task 3 - Completed, User Cancelled, and Event Cancelled reservations cannot be cancelled by student`() {
        // R009 is Completed (S001)
        assertTrue(studentService.cancelStudentReservation("S001", "R009") is ServiceResult.Failure)
        // R003 is User Cancelled (S003)
        assertTrue(studentService.cancelStudentReservation("S003", "R003") is ServiceResult.Failure)
        // R013 is Event Cancelled (S002)
        assertTrue(studentService.cancelStudentReservation("S002", "R013") is ServiceResult.Failure)
    }

    // --- Task 4 Tests: Admin View Community Events ---
    @Test
    fun `Task 4 - Administrator can view All, Upcoming, Completed, and Cancelled events`() {
        assertEquals(6, adminService.getEventsByFilter(AdminEventFilter.ALL).size)
        assertEquals(3, adminService.getEventsByFilter(AdminEventFilter.UPCOMING).size)
        assertEquals(2, adminService.getEventsByFilter(AdminEventFilter.COMPLETED).size)
        assertEquals(1, adminService.getEventsByFilter(AdminEventFilter.CANCELLED).size)
    }

    // --- Task 5 Tests: Admin Add and Modify Community Events ---
    @Test
    fun `Task 5 - Administrator can add a valid new event and modify an existing event`() {
        val addRes = adminService.addCommunityEvent(
            title = "AI & Robotics Showcase",
            category = "Academic",
            organiser = "Dr. Alan Grant",
            date = "28 Oct 2026",
            time = "15:30",
            location = "Main Auditorium",
            maxCapacity = 10
        )
        assertTrue(addRes.isSuccess)
        val added = addRes.getOrThrow()
        assertEquals("E007", added.eventId)
        assertEquals(EventStatus.UPCOMING, added.status)

        val modRes = adminService.modifyCommunityEvent(
            eventId = "E001",
            newTitle = "Advanced Python Coding Workshop",
            newMaxCapacity = 8
        )
        assertTrue(modRes.isSuccess)
        assertEquals("Advanced Python Coding Workshop", dataStore.findEventById("E001")!!.title)
        assertEquals(8, dataStore.findEventById("E001")!!.maxCapacity)

        // Modifying capacity below current active reservations (E002 has 4 active) must fail
        val invalidCap = adminService.modifyCommunityEvent(eventId = "E002", newMaxCapacity = 2)
        assertTrue(invalidCap.isFailure)
    }

    // --- Task 6 Tests: Admin Cancel Community Event ---
    @Test
    fun `Task 6 - Administrator cancelling upcoming event sets event to Cancelled and active reservations to Event Cancelled`() {
        // E001 has R001 (Active), R002 (Active), and R003 (User Cancelled)
        val cancelRes = adminService.cancelCommunityEvent("E001")
        assertTrue(cancelRes.isSuccess)
        val (cancelledEvent, affectedCount) = cancelRes.getOrThrow()

        assertEquals(EventStatus.CANCELLED, cancelledEvent.status)
        assertEquals(2, affectedCount)
        assertEquals(ReservationStatus.EVENT_CANCELLED, dataStore.findReservationById("R001")!!.status)
        assertEquals(ReservationStatus.EVENT_CANCELLED, dataStore.findReservationById("R002")!!.status)
        // R003 was User Cancelled and must remain User Cancelled!
        assertEquals(ReservationStatus.USER_CANCELLED, dataStore.findReservationById("R003")!!.status)
    }

    // --- Task 7 Tests: Student Registration ---
    @Test
    fun `Task 7 - Student registration validates duplicates and allows new student to register`() {
        val regSuccess = authAnalyticsService.registerStudent(
            studentId = "S007",
            fullName = "Zara Ahmed",
            email = "zara.ahmed@student.ac.uk",
            course = "Data Science",
            username = "zara",
            password = "Password123"
        )
        assertTrue(regSuccess.isSuccess)

        // Duplicate Student ID S001 must fail
        assertTrue(
            authAnalyticsService.registerStudent("S001", "Dup ID", "new@student.ac.uk", "IT", "newuser", "Pass123").isFailure
        )
        // Duplicate Username 'aisha' or 'admin' must fail
        assertTrue(
            authAnalyticsService.registerStudent("S008", "Dup User", "new2@student.ac.uk", "IT", "aisha", "Pass123").isFailure
        )
        assertTrue(
            authAnalyticsService.registerStudent("S008", "Dup Admin", "new3@student.ac.uk", "IT", "admin", "Pass123").isFailure
        )
        // Duplicate Email must fail
        assertTrue(
            authAnalyticsService.registerStudent("S008", "Dup Email", "aisha.khan@student.ac.uk", "IT", "uniqueu", "Pass123").isFailure
        )
    }

    // --- Task 8 Tests: Authentication and Role Management ---
    @Test
    fun `Task 8 - Authentication identifies Student and Administrator roles and rejects invalid credentials`() {
        val studentAuth = authAnalyticsService.authenticateUser("aisha", "Student123")
        assertTrue(studentAuth.isSuccess)
        assertEquals(UserRole.STUDENT, studentAuth.getOrThrow().role)

        val adminAuth = authAnalyticsService.authenticateUser("admin", "Admin123")
        assertTrue(adminAuth.isSuccess)
        assertEquals(UserRole.ADMINISTRATOR, adminAuth.getOrThrow().role)

        val invalidAuth = authAnalyticsService.authenticateUser("aisha", "WrongPass")
        assertTrue(invalidAuth.isFailure)
    }

    // --- Task 9 Tests: Community Event Analytics ---
    @Test
    fun `Task 9 - Analytics accurately computes completed, upcoming, and cancelled metrics dynamically`() {
        val summary = authAnalyticsService.generateAnalytics()

        // Completed events: E004 (2/5 = 40%) and E005 (1/6 = 16.67%)
        assertEquals(2, summary.completedMetrics.size)
        assertEquals("E004", summary.mostPopularCompletedEvents.single().event.eventId)
        assertEquals(2, summary.mostPopularCompletedEvents.single().completedReservationsCount)
        assertEquals(40.0, summary.mostPopularCompletedEvents.single().percentageCapacityUsed, 0.01)

        assertEquals("E005", summary.leastPopularCompletedEvents.single().event.eventId)
        assertEquals(1, summary.leastPopularCompletedEvents.single().completedReservationsCount)

        // Upcoming events: E001 (2/6), E002 (4/4 = 100%), E003 (1/6)
        assertEquals(3, summary.upcomingMetrics.size)
        assertEquals("E002", summary.mostPopularUpcomingEvents.single().event.eventId)
        assertEquals(4, summary.mostPopularUpcomingEvents.single().currentActiveReservations)
        assertEquals(100.0, summary.mostPopularUpcomingEvents.single().percentageCapacityReserved, 0.01)

        // Cancelled events: E006 (1 event, 2 affected reservations R013 & R014)
        assertEquals(1, summary.cancelledEventsCount)
        assertEquals(2, summary.reservationsAffectedByEventCancellations)
    }
}
