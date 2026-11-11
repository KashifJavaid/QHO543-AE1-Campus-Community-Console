package uk.ac.solent.campus.ae1.ui

import uk.ac.solent.campus.ae1.data.SeededDataStore
import uk.ac.solent.campus.ae1.model.Administrator
import uk.ac.solent.campus.ae1.model.Student
import uk.ac.solent.campus.ae1.service.AdminEventService
import uk.ac.solent.campus.ae1.service.AuthAndAnalyticsService
import uk.ac.solent.campus.ae1.service.StudentEventService
import java.util.Scanner

/**
 * Stage III – Fully Integrated Console Application Coordinator.
 * Integrates Member A (Tasks 1–3), Member B (Tasks 4–6), and Member C (Tasks 7–9)
 * using shared SeededDataStore state so all updates are reflected across the system.
 */
class IntegratedConsoleApp(
    val dataStore: SeededDataStore = SeededDataStore(),
    private val scanner: Scanner = Scanner(System.`in`)
) {
    val studentEventService = StudentEventService(dataStore)
    val adminEventService = AdminEventService(dataStore)
    val authAndAnalyticsService = AuthAndAnalyticsService(dataStore)

    private val memberAMenu = MemberAConsoleMenu(dataStore, studentEventService, scanner)
    private val memberBMenu = MemberBConsoleMenu(dataStore, adminEventService, scanner)
    private val memberCMenu = MemberCConsoleMenu(authAndAnalyticsService, scanner)

    /**
     * Primary entry point for the 3-Member Group Integrated Application (Tasks 1–9).
     * Also provides access to 2-Member Group Mode (Tasks 1–6 without authentication) for completeness.
     */
    fun run() {
        var running = true
        while (running) {
            println()
            println("====================================================================")
            println("        CAMPUS COMMUNITY MANAGEMENT SYSTEM (AE1 INTEGRATED)         ")
            println("====================================================================")
            println("  1. Student Registration (Task 7)")
            println("  2. Login / Authentication (Task 8 -> Student Tasks 1–3 / Admin Tasks 4–6 & 9)")
            println("  3. [Two-Member Group Mode] Direct Student / Administrator Menu (Tasks 1–6)")
            println("  0. Exit Application")
            print("Select an option (0-3): ")

            when (scanner.nextLine().trim()) {
                "1" -> memberCMenu.promptStudentRegistration()
                "2" -> handleAuthenticatedSession()
                "3" -> runTwoMemberGroupMode()
                "0" -> {
                    println("Exiting Campus Community Management System. Goodbye!")
                    running = false
                }
                else -> println("[!] Invalid choice. Please enter a number between 0 and 3.")
            }
        }
    }

    /**
     * Handles Task 8 Authentication and routes the authenticated user to their
     * role-specific menu (Student -> Tasks 1–3; Administrator -> Tasks 4–6 and Task 9).
     */
    private fun handleAuthenticatedSession() {
        val authenticatedUser = memberCMenu.promptLogin() ?: return

        when (authenticatedUser) {
            is Student -> {
                // Authenticated Student receives access strictly to Tasks 1–3
                memberAMenu.runMenu(loggedInStudent = authenticatedUser)
                println("\n[Logged Out] Student ${authenticatedUser.fullName} (${authenticatedUser.studentId}) has logged out.")
            }
            is Administrator -> {
                // Authenticated Administrator receives access to Tasks 4–6 and Task 9
                runAuthenticatedAdminMenu(authenticatedUser)
                println("\n[Logged Out] Administrator ${authenticatedUser.username} has logged out.")
            }
        }
    }

    /**
     * Role-restricted Administrator menu combining Member B (Tasks 4–6) and Member C (Task 9).
     */
    private fun runAuthenticatedAdminMenu(admin: Administrator) {
        var inAdminSession = true
        while (inAdminSession) {
            println()
            println("====================================================================")
            println("  ADMINISTRATOR DASHBOARD | Logged in as: ${admin.username} (${admin.role.displayName})")
            println("====================================================================")
            println("  1. Task 4: View Community Events (All / Upcoming / Completed / Cancelled)")
            println("  2. Task 5: Add and Modify Community Events")
            println("  3. Task 6: Cancel Community Event")
            println("  4. Task 9: View Community Event Analytics")
            println("  0. Logout")
            print("Select an administrator option (0-4): ")

            when (scanner.nextLine().trim()) {
                "1" -> memberBMenu.handleTask4ViewEvents()
                "2" -> memberBMenu.handleTask5AddOrModifyEvent()
                "3" -> memberBMenu.handleTask6CancelEvent()
                "4" -> memberCMenu.displayAnalyticsReport()
                "0" -> inAdminSession = false
                else -> println("[!] Invalid choice. Please enter a number between 0 and 4.")
            }
        }
    }

    /**
     * Supports the Two-Member Group (Members A & B) expected navigation:
     * Main menu with two options: Student (Tasks 1–3) and Administrator (Tasks 4–6).
     */
    private fun runTwoMemberGroupMode() {
        var inTwoMemberMode = true
        while (inTwoMemberMode) {
            println()
            println("====================================================================")
            println("  CAMPUS COMMUNITY MANAGEMENT SYSTEM (TWO-MEMBER GROUP NAVIGATION)  ")
            println("====================================================================")
            println("  1. Student (Tasks 1–3)")
            println("  2. Administrator (Tasks 4–6)")
            println("  0. Return to Main Menu")
            print("Select role (0-2): ")

            when (scanner.nextLine().trim()) {
                "1" -> memberAMenu.runMenu(loggedInStudent = null)
                "2" -> memberBMenu.runMenu()
                "0" -> inTwoMemberMode = false
                else -> println("[!] Invalid choice. Please enter a number between 0 and 2.")
            }
        }
    }
}
