package uk.ac.solent.campus.ae1

import uk.ac.solent.campus.ae1.data.SeededDataStore
import uk.ac.solent.campus.ae1.service.AuthAndAnalyticsService
import uk.ac.solent.campus.ae1.ui.MemberCConsoleMenu
import java.util.Scanner

/**
 * Stage II – Group Member C Standalone Entry Point (Branch: memberC).
 * Demonstrates Tasks 7, 8, and 9 independently before group integration.
 */
fun main() {
    val dataStore = SeededDataStore()
    val authAndAnalyticsService = AuthAndAnalyticsService(dataStore)
    val scanner = Scanner(System.`in`)
    val memberCMenu = MemberCConsoleMenu(authAndAnalyticsService, scanner)

    println("************************************************************")
    println("  CAMPUS COMMUNITY MANAGEMENT SYSTEM (AE1 - MEMBER C)       ")
    println("  Standalone Console Demonstration for Tasks 7, 8, and 9    ")
    println("************************************************************")
    memberCMenu.runStandaloneMenu()
}
