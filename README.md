# QHO543 AE1 – Campus Community Management System (Console Mode – Group Project)

A complete **Kotlin Object-Oriented Console Application** and **JUnit 5 Verification Suite** for **QHO543: Object Oriented Design and Development (Assessment Element 1 – Group Project)**.

This repository demonstrates the full **3-Stage Git Collaborative Workflow** (`main`, `memberA`, `memberB`, `memberC`), implementing **Tasks 1–9** against the compulsory **QHO543 Initial Seeded Data** dataset.

---

## Table of Contents

1. [Project Overview & Architecture](#1-project-overview--architecture)
2. [Prerequisites & Installation (Windows & macOS/Linux)](#2-prerequisites--installation-windows--macoslinux)
3. [How to Clone, Open & Run the Project](#3-how-to-clone-open--run-the-project)
4. [Step-by-Step Guide: How to Do the Work (Stages I, II, and III)](#4-step-by-step-guide-how-to-do-the-work-stages-i-ii-and-iii)
5. [Task-by-Task Reference & Expected Seeded Data Outputs](#5-task-by-task-reference--expected-seeded-data-outputs)
6. [How to Inspect & Test Each Group Member Branch](#6-how-to-inspect--test-each-group-member-branch)
7. [Project Directory Structure](#7-project-directory-structure)

---

## 1. Project Overview & Architecture

The **Campus Community Management System (Console Mode)** manages university students, student societies, scheduled events, and RSVPs in memory using clean Object-Oriented Design principles:
- **Encapsulation:** Private mutable collections inside `Society` (`members`, `events`) and `CommunityManager` (`students`, `societies`, `rsvps`), exposed only as read-only `List<T>` views.
- **Inheritance & Polymorphism:** `Student` inherits from `User` and overrides `displayProfile()`.
- **Composition & Aggregation:** `Society` owns its `Event` instances (`1..*`) and aggregates `Student` members (`0..*`).
- **3-Stage Git Branching Workflow:**
  - **Stage I (`main`):** Shared domain class templates (`User`, `Student`, `Society`, `Event`, `RSVP`, `CommunityManager`) and `SeededDataStore`.
  - **Stage II (`memberA`, `memberB`, `memberC`):** Independent feature branches where each member implements and tests their assigned 3 tasks via a standalone `main()` entry point.
  - **Stage III (`main`):** All 3 member branches merged into `main` with a unified interactive console menu (`Main.kt`) and automated JUnit 5 test suite (`AE1IntegratedSystemTest.kt`).

![AE1 UML Class Diagram](diagrams/AE1_Class_Diagram.png)

---

## 2. Prerequisites & Installation (Windows & macOS/Linux)

### Required Software
1. **Java Development Kit (JDK) 17 or 21+ (64-bit)**
2. **Git** (to clone the repository and switch between `main`, `memberA`, `memberB`, and `memberC`)
3. **IntelliJ IDEA (Community or Ultimate Edition)** *(Optional — you can also build and run everything from the terminal or Windows `.bat` scripts using the bundled Gradle Wrapper).*

### Installing Prerequisites on Windows 10 / Windows 11
Open **PowerShell** and run:
```powershell
# 1. Check if Java 17+ and Git are already installed:
java -version
git --version

# 2. If missing, install Eclipse Temurin JDK 21 and Git via Windows Package Manager (winget):
winget install EclipseAdoptium.Temurin.21.JDK
winget install Git.Git
```
*(Alternatively, inside IntelliJ IDEA go to **File -> Project Structure -> SDKs -> `+` -> Download JDK...** and select **Eclipse Temurin 21**).*

### Installing Prerequisites on macOS / Linux
Open **Terminal** and run:
```bash
# macOS (using Homebrew):
brew install --cask temurin@21
brew install git

# Ubuntu / Debian Linux:
sudo apt update && sudo apt install -y openjdk-21-jdk git
```

---

## 3. How to Clone, Open & Run the Project

### Step 1: Clone the Repository (or Extract the ZIP)

#### Using Git (Recommended — preserves all branches)
```bash
git clone https://github.com/KashifJavaid/QHO543-AE1-Campus-Community-Console.git
cd QHO543-AE1-Campus-Community-Console
```

#### Using the ZIP Archive on Windows
> **Important for Windows Users:** Do **not** run files directly inside the Windows Explorer compressed `.zip` preview window.
1. Right-click `AE1_Group_Integrated_Project.zip` in File Explorer.
2. Click **Extract All...** -> Choose a destination folder (e.g., `C:\QHO543\AE1\`) -> Click **Extract**.

---

### Step 2: Run the Application & Automated Tests

#### Option A: One-Click Run on Windows (`.bat` Scripts)
- Double-click **`run-app-windows.bat`** in Windows File Explorer to compile and launch the interactive Console menu.
- Double-click **`run-tests-windows.bat`** to run all JUnit 5 automated tests for Tasks 1–9.

#### Option B: Run via Windows PowerShell or Command Prompt (`cmd.exe`)
```powershell
# In Windows PowerShell (note the .\ prefix):
.\gradlew.bat test --info
.\gradlew.bat run --console=plain -q
```
```cmd
:: In Windows Command Prompt (cmd.exe):
gradlew.bat test --info
gradlew.bat run --console=plain -q
```

#### Option C: Run via macOS / Linux Terminal
```bash
# Run the JUnit 5 test suite (Tasks 1-9):
./gradlew test --info

# Launch the interactive Console menu:
./gradlew run --console=plain -q
```

#### Option D: Open & Run in IntelliJ IDEA (Windows / macOS / Linux)
1. Open **IntelliJ IDEA** -> **File -> Open...** -> Select the `AE1-Console-Group-Project` directory (containing `build.gradle.kts`) -> Click **OK** -> **Trust Project**.
2. Wait for the **Gradle Sync** indicator in the bottom-right corner to complete.
3. **Run the Console App:** Open `src/main/kotlin/uk/ac/solent/campus/ae1/Main.kt` and click the green **Run (`▶`)** button next to `fun main()`.
4. **Run the JUnit 5 Tests:** Open `src/test/kotlin/uk/ac/solent/campus/ae1/AE1IntegratedSystemTest.kt` and click the green double-play icon (`▶▶`) next to `class AE1IntegratedSystemTest`.

---

## 4. Step-by-Step Guide: How to Do the Work (Stages I, II, and III)

If you are a **student** completing this coursework from scratch or an **instructor** guiding a lab session, follow these exact steps:

### Stage I: Group Analysis, UML Design & Initial Project Template (`main` branch)

1. **Analyse the Specification:**
   - Identify the core domain nouns (`User`, `Student`, `Society`, `Event`, `RSVP`, `CommunityManager`) and their relationships.
2. **Draw the UML Class Diagram:**
   - Model `User` (`id`, `name`, `email`, `open fun displayProfile()`) as the superclass of `Student` (`major`, `year`).
   - Model `Society` (`societyId`, `name`, `category`, `description`) with encapsulated `members: MutableList<Student>` and `events: MutableList<Event>`.
   - Model `Event` (`eventId`, `title`, `date`, `location`, `maxCapacity`, `societyId`).
   - Model `RSVP` (`rsvpId`, `studentId`, `eventId`, `status`, `timestamp`).
   - Model `CommunityManager` as the controller class holding all collections and method signatures for Tasks 1–9.
3. **Create the Stage I Skeleton on `main`:**
   - Create the Kotlin data/domain classes in `src/main/kotlin/uk/ac/solent/campus/ae1/model/Models.kt`.
   - Create `SeededDataStore.kt` containing the compulsory dataset (10 Students `S001–S010`, 4 Societies `SOC1–SOC4`, 16 Membership links, 6 Events `E101–E106`, and 15 RSVPs `R01–R15`).
   - Create `CommunityManager.kt` with method stubs (`TODO("Implemented in Stage II")`) for Tasks 1–9.
   - Commit and push to `main`:
     ```bash
     git add .
     git commit -m "Stage I: Initial UML class templates, CommunityManager skeleton, and SeededDataStore"
     git push -u origin main
     ```

---

### Stage II: Individual Development & Testing on Separate Branches (`memberA`, `memberB`, `memberC`)

Each group member creates their own branch from `main`, implements only their assigned 3 tasks in `CommunityManager.kt`, writes a standalone `Main.kt` harness demonstrating valid and invalid test cases, and commits to their branch.

#### Member A (Tasks 1–3) — Branch `memberA`
```bash
git checkout -b memberA main
```
1. **Implement Task 1 (`registerStudent`):** Validate non-blank fields, valid year (`1..4`), and unique `id` & `email` (case-insensitive) before adding to `students`.
2. **Implement Task 2 (`createSociety`):** Validate non-blank fields and ensure both `societyId` and `name` are unique before adding to `societies`.
3. **Implement Task 3 (`joinSociety`):** Look up the student and society; prevent duplicate membership via `society.isMember(studentId)` before calling `society.addMember(student)`.
4. **Test in `Main.kt`:** Write a standalone `main()` demonstrating valid and invalid test runs for Tasks 1–3.
5. **Commit & Push:**
   ```bash
   git commit -am "Stage II (Member A): Implement and test Tasks 1, 2, and 3"
   git push -u origin memberA
   ```

#### Member B (Tasks 4–6) — Branch `memberB`
```bash
git checkout -b memberB main
```
1. **Implement Task 4 (`leaveSociety`):** Verify the student and society exist and that the student is currently a member before removing them (`society.removeMember(studentId)`).
2. **Implement Task 5 (`createEvent`):** Verify the society exists, `maxCapacity > 0`, and `eventId` is globally unique across all societies before adding the event to the society.
3. **Implement Task 6 (`rsvpToEvent`):**
   - Verify the student and event exist.
   - **Rule 1 (Membership Gate):** When `status == "Attending"`, verify the student is a member of the hosting society (`society.isMember(studentId)`).
   - **Rule 2 (Capacity Gate):** Count existing `"Attending"` RSVPs (`getAttendingCount(eventId)`) and reject if `attendingCount >= event.maxCapacity`.
   - **Rule 3 (Upsert):** If the student already has an RSVP for this event, update its status; otherwise create a new `RSVP` record.
4. **Test in `Main.kt` & Commit:**
   ```bash
   git commit -am "Stage II (Member B): Implement and test Tasks 4, 5, and 6"
   git push -u origin memberB
   ```

#### Member C (Tasks 7–9) — Branch `memberC`
```bash
git checkout -b memberC main
```
1. **Implement Task 7 (`viewSocietiesAndEvents`):** Format and return a complete directory of all societies, their member counts, and their scheduled events with current `"Attending"` counts vs. `maxCapacity`.
2. **Implement Task 8 (`getStudentById` / `searchStudentsByName`):** Support exact ID lookup (case-insensitive) and partial substring name search (`contains(query, ignoreCase = true)`).
3. **Implement Task 9 (`generateStudentActivitySummary`):** Compile a formatted report for a given student showing their profile, joined societies, and all RSVPs enriched with event titles, dates, and society names.
4. **Test in `Main.kt` & Commit:**
   ```bash
   git commit -am "Stage II (Member C): Implement and test Tasks 7, 8, and 9"
   git push -u origin memberC
   ```

---

### Stage III: Branch Integration, Unified Console Menu & Verification (`main` branch)

1. **Merge All Member Branches into `main`:**
   ```bash
   git checkout main
   git merge memberA
   git merge memberB
   git merge memberC
   ```
2. **Resolve Merge Conflicts in `CommunityManager.kt` and `Main.kt`:**
   - Keep all 9 implemented task methods (`Tasks 1–9`) in `CommunityManager.kt`.
   - Replace the individual `Main.kt` test harnesses with the unified interactive console menu (`1–10`, `0` to exit) plus an automated self-test demonstration mode (`option 10` or `--demo` CLI flag).
3. **Run the Full JUnit 5 Verification Suite:**
   ```bash
   ./gradlew test --info
   ```

---

## 5. Task-by-Task Reference & Expected Seeded Data Outputs

When initialized with `SeededDataStore.populate(manager)`, the system contains **10 Students (`S001–S010`)**, **4 Societies (`SOC1–SOC4`)**, **16 Memberships**, **6 Events (`E101–E106`)**, and **15 RSVPs (`R01–R15`)**.

| Task | Owner | Method in `CommunityManager` | Key Validation Rules & Expected Seeded Data Result |
| :--- | :---: | :--- | :--- |
| **Task 1:** Register Student | Member A | `registerStudent(...)` | Rejects duplicate ID (`S001`) or email (`aisha.khan@student.ac.uk`); registers new student `S011`. |
| **Task 2:** Create Society | Member A | `createSociety(...)` | Rejects duplicate ID (`SOC1`) or name (`Computer Science Society`); creates `SOC5`. |
| **Task 3:** Join Society | Member A | `joinSociety(studentId, societyId)` | Rejects `S001 -> SOC1` (already member); allows `S001 -> SOC2` (`SOC2` grows from 4 to 5 members). |
| **Task 4:** Leave Society | Member B | `leaveSociety(studentId, societyId)` | Rejects `S001 -> SOC4` (not a member); allows `S010 -> SOC3` (`SOC3` drops from 4 to 3 members). |
| **Task 5:** Create Event | Member B | `createEvent(...)` | Rejects duplicate `E101` or `maxCapacity <= 0`; creates `E107` under `SOC1`. |
| **Task 6:** RSVP to Event | Member B | `rsvpToEvent(...)` | **Capacity check:** `E102` has `maxCapacity = 2` and 2 `"Attending"` (`S001`, `S002`) -> **Full (Rejected)**.<br>**Membership check:** `S001` is not in `SOC3`, so `S001 -> E104 ("Attending")` -> **Rejected**. |
| **Task 7:** View Societies & Events | Member C | `viewSocietiesAndEvents()` | Lists `SOC1` (5 members; `E101` 3/50, `E102` 2/2 FULL), `SOC2` (4 members; `E103` 2/30), `SOC3` (4 members; `E104` 3/25, `E105` 2/3), `SOC4` (3 members; `E106` 2/20). |
| **Task 8:** Search Student | Member C | `getStudentById` / `searchStudentsByName` | ID `"S004"` returns `Daniel Smith`; name `"an"` returns `Aisha Khan`, `Daniel Smith`, `Hannah Patel`. |
| **Task 9:** Activity Summary | Member C | `generateStudentActivitySummary` | `S001` summary lists 2 societies (`SOC1`, `SOC4`) and 3 RSVPs (`E101 Attending`, `E102 Attending`, `E106 Attending`). |

---

## 6. How to Inspect & Test Each Group Member Branch

To inspect or grade each group member's standalone Stage II work:

```bash
# View the complete Git branch and merge graph:
git log --all --graph --oneline --decorate

# Switch to Member A's branch (Tasks 1-3 standalone demo):
git checkout memberA
./gradlew run --console=plain -q

# Switch to Member B's branch (Tasks 4-6 standalone demo):
git checkout memberB
./gradlew run --console=plain -q

# Switch to Member C's branch (Tasks 7-9 standalone demo):
git checkout memberC
./gradlew run --console=plain -q

# Return to the integrated Stage III main branch:
git checkout main
./gradlew test --info
```

---

## 7. Project Directory Structure

```text
AE1-Console-Group-Project/
├── README.md                                 # Main installation & step-by-step coursework guide
├── README_WINDOWS.md                         # Dedicated Windows 10/11 quick-start instructions
├── AE1_Group_Report.md                       # Complete AE1 Group Report (Stages I, II, and III)
├── AE1_Individual_GitHub_Declarations.md     # Individual member GitHub URL & commit declaration templates
├── build.gradle.kts                          # Kotlin JVM 2.1.0 + JUnit 5 build configuration
├── settings.gradle.kts                       # Gradle project settings
├── gradlew / gradlew.bat                     # Cross-platform Gradle Wrapper scripts
├── run-app-windows.bat                       # Double-click Windows launcher for the Console app
├── run-tests-windows.bat                     # Double-click Windows launcher for JUnit 5 tests
├── diagrams/
│   └── AE1_Class_Diagram.png                 # High-resolution UML Class Diagram
└── src/
    ├── main/kotlin/uk/ac/solent/campus/ae1/
    │   ├── Main.kt                           # Interactive Console UI & automated demo runner
    │   ├── model/Models.kt                   # User, Student, Society, Event, RSVP, OperationResult
    │   ├── seed/SeededDataStore.kt           # Official QHO543 seeded dataset (S001-S010, SOC1-SOC4, E101-E106, R01-R15)
    │   └── service/CommunityManager.kt       # Business logic implementing Tasks 1-9
    └── test/kotlin/uk/ac/solent/campus/ae1/
        └── AE1IntegratedSystemTest.kt        # Automated JUnit 5 test suite covering Tasks 1-9
```
