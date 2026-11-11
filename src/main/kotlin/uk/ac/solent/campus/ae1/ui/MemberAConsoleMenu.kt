package uk.ac.solent.campus.ae1.ui

import uk.ac.solent.campus.ae1.data.SeededDataStore
import uk.ac.solent.campus.ae1.model.CommunityEvent
import uk.ac.solent.campus.ae1.model.Student
import uk.ac.solent.campus.ae1.service.EventSearchCriteria
import uk.ac.solent.campus.ae1.service.ServiceResult
import uk.ac.solent.campus.ae1.service.StudentEventService
import java.util.Scanner

/**
 * Console UI handler for Group Member A – Student Event Functionality (Tasks 1–3).
 * Can run independently (pre-integration using Student ID) or for an authenticated Student.
 */
class MemberAConsoleMenu(
    private val dataStore: SeededDataStore,
    private val studentEventService: StudentEventService,
    private val scanner: Scanner
) {
    /**
     * Runs Member A's 3-task console menu.
     * If [loggedInStudent] is null (standalone Member A mode or 2-member group mode),
     * prompts for a predefined Student ID when performing Tasks 2 and 3.
     */
    fun runMenu(loggedInStudent: Student? = null) {
        var running = true
        while (running) {
            println()
            println("============================================================")
            if (loggedInStudent != null) {
                println("  STUDENT MENU | Logged in as: ${loggedInStudent.fullName} (${loggedInStudent.studentId})")
            } else {
                println("  GROUP MEMBER A – STUDENT EVENT FUNCTIONALITY (TASKS 1–3)")
            }
            println("============================================================")
            println("  1. Task 1: View and Search Community Events")
            println("  2. Task 2: Make Event Reservation and Print Confirmation")
            println("  3. Task 3: View Reservations and Cancel Active Reservations")
            println("  0. Return / Exit")
            print("Select an option (0-3): ")

            when (scanner.nextLine().trim()) {
                "1" -> handleTask1ViewAndSearch()
                "2" -> handleTask2MakeReservation(loggedInStudent)
                "3" -> handleTask3ViewAndCancelReservations(loggedInStudent)
                "0" -> running = false
                else -> println("[!] Invalid choice. Please enter a number between 0 and 3.")
            }
        }
    }

    fun handleTask1ViewAndSearch() {
        println("\n--- Task 1: View and Search Community Events ---")
        println("  1. View All Community Events")
        println("  2. Search by Event Title")
        println("  3. Search by Category")
        println("  4. Search by Organiser")
        println("  5. Search by Date (e.g. 14 Oct 2026)")
        print("Choose search/view mode (1-5): ")

        val mode = scanner.nextLine().trim()
        val matchingEvents: List<CommunityEvent> = when (mode) {
            "1" -> studentEventService.getAllEvents()
            "2" -> {
                print("Enter Event Title keyword: ")
                studentEventService.searchEvents(EventSearchCriteria.TITLE, scanner.nextLine())
            }
            "3" -> {
                print("Enter Category keyword (e.g. Academic, Sports, Careers, Wellbeing, Volunteering, Social): ")
                studentEventService.searchEvents(EventSearchCriteria.CATEGORY, scanner.nextLine())
            }
            "4" -> {
                print("Enter Organiser name keyword: ")
                studentEventService.searchEvents(EventSearchCriteria.ORGANISER, scanner.nextLine())
            }
            "5" -> {
                print("Enter Date keyword (e.g. 14 Oct 2026): ")
                studentEventService.searchEvents(EventSearchCriteria.DATE, scanner.nextLine())
            }
            else -> {
                println("[!] Invalid option. Showing all events by default.")
                studentEventService.getAllEvents()
            }
        }

        if (matchingEvents.isEmpty()) {
            println("No matching community events found.")
            return
        }

        println("\nMatching Community Events (${matchingEvents.size} found):")
        println("-----------------------------------------------------------------------------------------------------------------------------")
        matchingEvents.forEach { event ->
            val count = studentEventService.getReservationCountForEvent(event)
            println(event.formatRowSummary(count))
        }
        println("-----------------------------------------------------------------------------------------------------------------------------")

        print("Enter an Event ID (e.g. E001) to inspect full details, or press ENTER to skip: ")
        val selectedId = scanner.nextLine().trim()
        if (selectedId.isNotEmpty()) {
            when (val result = studentEventService.getEventDetailedInfo(selectedId)) {
                is ServiceResult.Success -> println("\n${result.data}")
                is ServiceResult.Failure -> println("[Error] ${result.errorMessage}")
            }
        }
    }

    fun handleTask2MakeReservation(loggedInStudent: Student?) {
        println("\n--- Task 2: Make Event Reservation and Print Confirmation ---")
        val studentId = if (loggedInStudent != null) {
            println("Booking as authenticated student: ${loggedInStudent.fullName} (${loggedInStudent.studentId})")
            loggedInStudent.studentId
        } else {
            println("Predefined Students: " + dataStore.students.joinToString(", ") { "${it.studentId} (${it.fullName})" })
            print("Enter Student ID (e.g. S001): ")
            scanner.nextLine().trim()
        }

        println("\nCurrent Community Events:")
        studentEventService.getAllEvents().forEach { event ->
            val count = studentEventService.getReservationCountForEvent(event)
            println("  " + event.formatRowSummary(count))
        }

        print("\nEnter Event ID to reserve (e.g. E001 or E003): ")
        val eventId = scanner.nextLine().trim()

        when (val result = studentEventService.makeReservation(studentId, eventId)) {
            is ServiceResult.Success -> {
                println("\nReservation Confirmed! Printing Confirmation Ticket:\n")
                println(result.message)
            }
            is ServiceResult.Failure -> {
                println("\n[Reservation Failed] ${result.errorMessage}")
            }
        }
    }

    fun handleTask3ViewAndCancelReservations(loggedInStudent: Student?) {
        println("\n--- Task 3: View Reservations and Cancel Active Reservations ---")
        val studentId = if (loggedInStudent != null) {
            println("Viewing reservations for: ${loggedInStudent.fullName} (${loggedInStudent.studentId})")
            loggedInStudent.studentId
        } else {
            println("Predefined Students: " + dataStore.students.joinToString(", ") { "${it.studentId} (${it.fullName})" })
            print("Enter Student ID (e.g. S001): ")
            scanner.nextLine().trim()
        }

        when (val resResult = studentEventService.getReservationsForStudent(studentId)) {
            is ServiceResult.Failure -> {
                println("[Error] ${resResult.errorMessage}")
            }
            is ServiceResult.Success -> {
                val reservations = resResult.data
                if (reservations.isEmpty()) {
                    println("No reservations found for student ID '$studentId'.")
                    return
                }

                println("\nReservation History for Student $studentId (${reservations.size} total):")
                println("-------------------------------------------------------------------------------------------------------------")
                reservations.forEach { res ->
                    val cancellableTag = if (res.isCancellableByStudent()) "[Cancellable]" else "[Non-Cancellable]"
                    println("${res.formatReservationRow()} $cancellableTag")
                }
                println("-------------------------------------------------------------------------------------------------------------")

                print("\nEnter a Reservation ID to cancel (e.g. R001), or press ENTER to return: ")
                val resIdToCancel = scanner.nextLine().trim()
                if (resIdToCancel.isNotEmpty()) {
                    when (val cancelResult = studentEventService.cancelStudentReservation(studentId, resIdToCancel)) {
                        is ServiceResult.Success -> println("\n[Success] ${cancelResult.message}")
                        is ServiceResult.Failure -> println("\n[Cancellation Rejected] ${cancelResult.errorMessage}")
                    }
                }
            }
        }
    }
}
