package uk.ac.solent.campus.ae1.ui

import uk.ac.solent.campus.ae1.data.SeededDataStore
import uk.ac.solent.campus.ae1.service.AdminEventFilter
import uk.ac.solent.campus.ae1.service.AdminEventService
import java.util.Scanner

/**
 * Console UI handler for Group Member B – Community Event Management (Admin) (Tasks 4–6).
 */
class MemberBConsoleMenu(
    private val dataStore: SeededDataStore,
    private val adminEventService: AdminEventService,
    private val scanner: Scanner
) {
    fun runMenu() {
        var running = true
        while (running) {
            println()
            println("============================================================")
            println("  GROUP MEMBER B – COMMUNITY EVENT MANAGEMENT (TASKS 4–6)   ")
            println("============================================================")
            println("  1. Task 4: View Community Events (All / Upcoming / Completed / Cancelled)")
            println("  2. Task 5: Add and Modify Community Events")
            println("  3. Task 6: Cancel Community Event")
            println("  0. Return / Exit")
            print("Select an option (0-3): ")

            when (scanner.nextLine().trim()) {
                "1" -> handleTask4ViewEvents()
                "2" -> handleTask5AddOrModifyEvent()
                "3" -> handleTask6CancelEvent()
                "0" -> running = false
                else -> println("[!] Invalid choice. Please enter a number between 0 and 3.")
            }
        }
    }

    fun handleTask4ViewEvents() {
        println("\n--- Task 4: View Community Events (Administrator) ---")
        println("  1. View All Events")
        println("  2. View Upcoming Events")
        println("  3. View Completed Events")
        println("  4. View Cancelled Events")
        print("Select view filter (1-4): ")

        val filter = when (scanner.nextLine().trim()) {
            "1" -> AdminEventFilter.ALL
            "2" -> AdminEventFilter.UPCOMING
            "3" -> AdminEventFilter.COMPLETED
            "4" -> AdminEventFilter.CANCELLED
            else -> {
                println("[!] Invalid filter. Defaulting to All Events.")
                AdminEventFilter.ALL
            }
        }

        val events = adminEventService.getEventsByFilter(filter)
        println("\nShowing ${filter.label} (${events.size} event(s)):")
        if (events.isEmpty()) {
            println("No events match the selected filter.")
            return
        }

        println("-----------------------------------------------------------------------------------------------------------------------------")
        events.forEach { event ->
            val resCount = adminEventService.getReservationCountForEvent(event)
            println(event.formatRowSummary(resCount))
        }
        println("-----------------------------------------------------------------------------------------------------------------------------")
    }

    fun handleTask5AddOrModifyEvent() {
        println("\n--- Task 5: Add and Modify Community Events ---")
        println("  1. Add a New Community Event")
        println("  2. Modify an Existing Community Event")
        print("Select action (1-2): ")

        when (scanner.nextLine().trim()) {
            "1" -> handleAddEvent()
            "2" -> handleModifyEvent()
            else -> println("[!] Invalid option.")
        }
    }

    private fun handleAddEvent() {
        println("\n[Add New Community Event]")
        print("Enter Event Title: ")
        val title = scanner.nextLine().trim()
        print("Enter Category (e.g. Academic, Sports, Careers, Wellbeing, Volunteering, Social): ")
        val category = scanner.nextLine().trim()
        print("Enter Organiser Name: ")
        val organiser = scanner.nextLine().trim()
        print("Enter Event Date (e.g. 25 Oct 2026): ")
        val date = scanner.nextLine().trim()
        print("Enter Event Time (e.g. 15:00): ")
        val time = scanner.nextLine().trim()
        print("Enter Location: ")
        val location = scanner.nextLine().trim()
        print("Enter Maximum Capacity (positive integer): ")
        val maxCapInput = scanner.nextLine().trim()
        val maxCap = maxCapInput.toIntOrNull()

        if (maxCap == null) {
            println("[Error] Maximum capacity must be a valid integer.")
            return
        }

        val result = adminEventService.addCommunityEvent(title, category, organiser, date, time, location, maxCap)
        result.fold(
            onSuccess = { created ->
                println("\n[Success] New Community Event created and integrated into the system:")
                println(created.formatDetailedView(0))
            },
            onFailure = { err ->
                println("\n[Validation Error] ${err.message}")
            }
        )
    }

    private fun handleModifyEvent() {
        println("\n[Modify Existing Community Event]")
        println("(Note: Event cancellation is not permitted here; use Task 6 to cancel an event.)")
        dataStore.events.forEach { ev ->
            println("  " + ev.formatRowSummary(adminEventService.getReservationCountForEvent(ev)))
        }
        print("\nEnter Event ID to modify (e.g. E001): ")
        val eventId = scanner.nextLine().trim()
        val existing = dataStore.findEventById(eventId)
        if (existing == null) {
            println("[Error] Event with ID '$eventId' not found.")
            return
        }

        println("Press ENTER on any field to keep its current value.")
        print("New Title [${existing.title}]: ")
        val newTitle = scanner.nextLine().trim().ifEmpty { null }
        print("New Category [${existing.category}]: ")
        val newCategory = scanner.nextLine().trim().ifEmpty { null }
        print("New Organiser [${existing.organiser}]: ")
        val newOrganiser = scanner.nextLine().trim().ifEmpty { null }
        print("New Date [${existing.date}]: ")
        val newDate = scanner.nextLine().trim().ifEmpty { null }
        print("New Time [${existing.time}]: ")
        val newTime = scanner.nextLine().trim().ifEmpty { null }
        print("New Location [${existing.location}]: ")
        val newLocation = scanner.nextLine().trim().ifEmpty { null }
        print("New Maximum Capacity [${existing.maxCapacity}]: ")
        val capStr = scanner.nextLine().trim()
        val newCap = if (capStr.isEmpty()) null else capStr.toIntOrNull()
        if (capStr.isNotEmpty() && newCap == null) {
            println("[Error] Maximum capacity must be a valid integer.")
            return
        }

        val result = adminEventService.modifyCommunityEvent(
            eventId = existing.eventId,
            newTitle = newTitle,
            newCategory = newCategory,
            newOrganiser = newOrganiser,
            newDate = newDate,
            newTime = newTime,
            newLocation = newLocation,
            newMaxCapacity = newCap
        )
        result.fold(
            onSuccess = { updated ->
                val count = adminEventService.getReservationCountForEvent(updated)
                println("\n[Success] Event ${updated.eventId} updated successfully:")
                println(updated.formatDetailedView(count))
            },
            onFailure = { err ->
                println("\n[Update Failed] ${err.message}")
            }
        )
    }

    fun handleTask6CancelEvent() {
        println("\n--- Task 6: Cancel Community Event ---")
        val upcomingEvents = adminEventService.getEventsByFilter(AdminEventFilter.UPCOMING)
        if (upcomingEvents.isEmpty()) {
            println("There are currently no upcoming events available to cancel.")
            return
        }

        println("Upcoming Events:")
        upcomingEvents.forEach { ev ->
            println("  " + ev.formatRowSummary(adminEventService.getReservationCountForEvent(ev)))
        }

        print("\nEnter Event ID of the upcoming event to cancel (e.g. E001): ")
        val eventId = scanner.nextLine().trim()

        val result = adminEventService.cancelCommunityEvent(eventId)
        result.fold(
            onSuccess = { (cancelledEvent, affectedCount) ->
                println("\n[Success] Event '${cancelledEvent.title}' (${cancelledEvent.eventId}) status updated to 'Cancelled'.")
                println("Active student reservations automatically updated to 'Event Cancelled': $affectedCount reservation(s).")
            },
            onFailure = { err ->
                println("\n[Cancellation Failed] ${err.message}")
            }
        )
    }
}
