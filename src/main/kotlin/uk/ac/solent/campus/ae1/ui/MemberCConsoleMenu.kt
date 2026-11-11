package uk.ac.solent.campus.ae1.ui

import uk.ac.solent.campus.ae1.model.Administrator
import uk.ac.solent.campus.ae1.model.Student
import uk.ac.solent.campus.ae1.model.User
import uk.ac.solent.campus.ae1.service.AuthAndAnalyticsService
import java.util.Scanner

/**
 * Console UI handler for Group Member C – Registration, Authentication and Analytics (Tasks 7–9).
 */
class MemberCConsoleMenu(
    private val authAndAnalyticsService: AuthAndAnalyticsService,
    private val scanner: Scanner
) {
    /**
     * Standalone menu for Member C's individual branch demonstration.
     */
    fun runStandaloneMenu() {
        var running = true
        while (running) {
            println()
            println("============================================================")
            println("  GROUP MEMBER C – REGISTRATION, AUTH & ANALYTICS (TASKS 7–9)")
            println("============================================================")
            println("  1. Task 7: Student Registration")
            println("  2. Task 8: Authentication and Role Management")
            println("  3. Task 9: Community Event Analytics")
            println("  0. Exit")
            print("Select an option (0-3): ")

            when (scanner.nextLine().trim()) {
                "1" -> promptStudentRegistration()
                "2" -> {
                    val user = promptLogin()
                    if (user != null) {
                        when (user) {
                            is Student -> println("[Role Access Verified] Authenticated as STUDENT (${user.fullName}, ID: ${user.studentId}). Access granted to Student Tasks 1–3.")
                            is Administrator -> println("[Role Access Verified] Authenticated as ADMINISTRATOR (${user.username}). Access granted to Admin Tasks 4–6 & Analytics Task 9.")
                        }
                    }
                }
                "3" -> displayAnalyticsReport()
                "0" -> running = false
                else -> println("[!] Invalid choice. Please enter a number between 0 and 3.")
            }
        }
    }

    fun promptStudentRegistration(): Student? {
        println("\n--- Task 7: Student Registration ---")
        print("Enter Student ID (e.g. S007): ")
        val studentId = scanner.nextLine().trim()
        print("Enter Full Name: ")
        val fullName = scanner.nextLine().trim()
        print("Enter Email Address (e.g. name@student.ac.uk): ")
        val email = scanner.nextLine().trim()
        print("Enter Course: ")
        val course = scanner.nextLine().trim()
        print("Enter Username: ")
        val username = scanner.nextLine().trim()
        print("Enter Password (min 4 characters): ")
        val password = scanner.nextLine()

        val result = authAndAnalyticsService.registerStudent(studentId, fullName, email, course, username, password)
        return result.fold(
            onSuccess = { student ->
                println("\n[Registration Successful] Account created for ${student.fullName} (${student.studentId}).")
                println("You may now log in using username '${student.username}'.")
                student
            },
            onFailure = { err ->
                println("\n[Registration Failed] ${err.message}")
                null
            }
        )
    }

    fun promptLogin(): User? {
        println("\n--- Task 8: Login / Authentication ---")
        print("Enter Username: ")
        val username = scanner.nextLine().trim()
        print("Enter Password: ")
        val password = scanner.nextLine()

        val result = authAndAnalyticsService.authenticateUser(username, password)
        return result.fold(
            onSuccess = { user ->
                println("\n[Login Successful] Welcome, ${user.username}! Identified Role: ${user.role.displayName}")
                user
            },
            onFailure = { err ->
                println("\n[Login Rejected] ${err.message}")
                null
            }
        )
    }

    fun displayAnalyticsReport() {
        println()
        println(authAndAnalyticsService.formatAnalyticsReport())
    }
}
