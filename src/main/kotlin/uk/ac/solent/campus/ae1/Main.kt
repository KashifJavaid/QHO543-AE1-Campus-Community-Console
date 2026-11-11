package uk.ac.solent.campus.ae1

import uk.ac.solent.campus.ae1.data.SeededDataStore

/**
 * Stage I – Group Template Entry Point.
 * Verifies that initial domain classes and compulsory seeded dataset load properly.
 */
fun main() {
    val dataStore = SeededDataStore()
    println("=== Campus Community Management System (Stage I Template) ===")
    println("Loaded ${dataStore.students.size} students, ${dataStore.administrators.size} administrators, " +
            "${dataStore.events.size} events, and ${dataStore.reservations.size} reservations.")
}
