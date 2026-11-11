package uk.ac.solent.campus.ae1.model

/**
 * Abstract base class representing any authenticated user of the system.
 * Demonstrates inheritance, encapsulation, and polymorphism.
 */
abstract class User(
    val username: String,
    private val password: String,
    val role: UserRole
) {
    /**
     * Verifies whether the supplied password matches the user's stored password.
     */
    fun verifyPassword(inputPassword: String): Boolean = password == inputPassword

    /**
     * Exposes password for persistence or seeding inspection where required.
     */
    fun getPasswordValue(): String = password

    /**
     * Polymorphic summary representation of the user.
     */
    abstract fun displaySummary(): String
}

/**
 * Represents a university student who can browse events and manage reservations.
 */
class Student(
    val studentId: String,
    var fullName: String,
    var course: String,
    var email: String,
    username: String,
    password: String
) : User(username, password, UserRole.STUDENT) {

    override fun displaySummary(): String {
        return "[$studentId] $fullName | Course: $course | Email: $email | Username: $username"
    }

    override fun toString(): String = displaySummary()
}

/**
 * Represents an administrator who manages community events and views analytics.
 */
class Administrator(
    username: String,
    password: String,
    val adminTitle: String = "Administrator"
) : User(username, password, UserRole.ADMINISTRATOR) {

    override fun displaySummary(): String {
        return "[ADMIN] Username: $username | Role: ${role.displayName}"
    }

    override fun toString(): String = displaySummary()
}

