package uk.ac.solent.campus.ae1

import uk.ac.solent.campus.ae1.data.SeededDataStore
import uk.ac.solent.campus.ae1.service.AdminEventService
import uk.ac.solent.campus.ae1.service.StudentEventService
import uk.ac.solent.campus.ae1.ui.MemberAConsoleMenu
import uk.ac.solent.campus.ae1.ui.MemberBConsoleMenu
import java.util.Scanner

/**
 * Stage III Intermediate Integration (Members A + B).
 * Provides Student (Tasks 1-3) and Administrator (Tasks 4-6) options.
 */
fun main() {
    val dataStore = SeededDataStore()
    val studentEventService = StudentEventService(dataStore)
    val adminEventService = AdminEventService(dataStore)
    val scanner = Scanner(System.`in`)
    val memberAMenu = MemberAConsoleMenu(dataStore, studentEventService, scanner)
    val memberBMenu = MemberBConsoleMenu(dataStore, adminEventService, scanner)

    var running = true
    while (running) {
        println("\n============================================================")
        println("  CAMPUS COMMUNITY MANAGEMENT SYSTEM (MEMBERS A & B)        ")
        println("============================================================")
        println("  1. Student (Tasks 1–3)")
        println("  2. Administrator (Tasks 4–6)")
        println("  0. Exit")
        print("Select role (0-2): ")
        when (scanner.nextLine().trim()) {
            "1" -> memberAMenu.runMenu(loggedInStudent = null)
            "2" -> memberBMenu.runMenu()
            "0" -> running = false
            else -> println("[!] Invalid option.")
        }
    }
}
