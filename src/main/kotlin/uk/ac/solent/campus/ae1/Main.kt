package uk.ac.solent.campus.ae1

import uk.ac.solent.campus.ae1.ui.IntegratedConsoleApp

/**
 * Stage III – Final Integrated Application Entry Point (Main Branch).
 * Starts with Student Registration (Task 7) and Login / Authentication (Task 8),
 * routing authenticated Students to Tasks 1–3 and authenticated Administrators to Tasks 4–6 & Task 9.
 */
fun main() {
    val app = IntegratedConsoleApp()
    app.run()
}
