package uk.ac.solent.campus.ae1

import uk.ac.solent.campus.ae1.data.SeededDataStore
import uk.ac.solent.campus.ae1.service.StudentEventService
import uk.ac.solent.campus.ae1.ui.MemberAConsoleMenu
import java.util.Scanner

/**
 * Stage II – Group Member A Standalone Entry Point (Branch: memberA).
 * Demonstrates Tasks 1, 2, and 3 independently before group integration.
 */
fun main() {
    val dataStore = SeededDataStore()
    val studentEventService = StudentEventService(dataStore)
    val scanner = Scanner(System.`in`)
    val memberAMenu = MemberAConsoleMenu(dataStore, studentEventService, scanner)

    println("************************************************************")
    println("  CAMPUS COMMUNITY MANAGEMENT SYSTEM (AE1 - MEMBER A)       ")
    println("  Standalone Console Demonstration for Tasks 1, 2, and 3    ")
    println("************************************************************")
    memberAMenu.runMenu(loggedInStudent = null)
}
