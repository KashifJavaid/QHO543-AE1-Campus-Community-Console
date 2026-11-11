package uk.ac.solent.campus.ae1

import uk.ac.solent.campus.ae1.data.SeededDataStore
import uk.ac.solent.campus.ae1.service.AdminEventService
import uk.ac.solent.campus.ae1.ui.MemberBConsoleMenu
import java.util.Scanner

/**
 * Stage II – Group Member B Standalone Entry Point (Branch: memberB).
 * Demonstrates Tasks 4, 5, and 6 independently before group integration.
 */
fun main() {
    val dataStore = SeededDataStore()
    val adminEventService = AdminEventService(dataStore)
    val scanner = Scanner(System.`in`)
    val memberBMenu = MemberBConsoleMenu(dataStore, adminEventService, scanner)

    println("************************************************************")
    println("  CAMPUS COMMUNITY MANAGEMENT SYSTEM (AE1 - MEMBER B)       ")
    println("  Standalone Console Demonstration for Tasks 4, 5, and 6    ")
    println("************************************************************")
    memberBMenu.runMenu()
}
