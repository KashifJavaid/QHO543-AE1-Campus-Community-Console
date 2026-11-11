# Windows 10 / Windows 11 Quick-Start Guide (QHO543 AE1 – Console Mode Group Project)

This guide provides step-by-step instructions for Instructors and Students running, testing, and grading **QHO543 AE1 (Campus Community Management System – Console Mode)** on a **Windows 10 or Windows 11** machine.

---

## 1. Prerequisites on Windows

1. **JDK 17 or JDK 21+ (64-bit Windows)**
   - Verify in **Command Prompt (`cmd.exe`)** or **PowerShell**:
     ```powershell
     java -version
     ```
   - If Java is not installed, install Eclipse Temurin JDK 21 via Windows Package Manager (`winget`) in PowerShell:
     ```powershell
     winget install EclipseAdoptium.Temurin.21.JDK
     ```
     *(Alternatively, open IntelliJ IDEA -> `File -> Project Structure -> SDKs -> + -> Download JDK...` and select **Eclipse Temurin 21**).*
2. **Git for Windows** (to inspect branches `main`, `memberA`, `memberB`, `memberC`):
   ```powershell
   winget install Git.Git
   ```
3. **IntelliJ IDEA (Community or Ultimate Edition for Windows)** (optional if running via `.bat` or `gradlew.bat`).

---

## 2. Extracting the ZIP File on Windows (Important!)

> **Warning:** Do **not** double-click to open the `.zip` archive in Windows Explorer and run files directly from inside the compressed preview window. Always extract the archive first:
1. Right-click `AE1_Group_Integrated_Project.zip` (or the individual member `.zip` file) in Windows File Explorer.
2. Select **Extract All...** -> Choose a destination folder (e.g., `C:\QHO543\AE1\`) -> Click **Extract**.

---

## 3. Option A: Opening & Running in IntelliJ IDEA on Windows

1. Launch **IntelliJ IDEA** on Windows.
2. Click **File -> Open...** and select the extracted `AE1-Console-Group-Project` folder (the folder containing `build.gradle.kts` and `gradlew.bat`), then click **OK** -> **Trust Project**.
3. Wait for the bottom-right **Gradle Sync** progress bar to finish.
   - *If IntelliJ prompts for a Gradle JVM:* Go to **File -> Settings (`Ctrl + Alt + S`) -> Build, Execution, Deployment -> Build Tools -> Gradle** and set **Gradle JVM** to **JDK 17** or **JDK 21+**.
4. **To Run the Console Application:**
   - Open `src\main\kotlin\uk\ac\solent\campus\ae1\Main.kt` in the Project view.
   - Click the green **Run (`▶`)** triangle in the left gutter next to `fun main()` and select **Run 'MainKt'**.
   - Interact with the console menu inside IntelliJ's bottom **Run** tool window.
5. **To Switch Between Group Member Branches (`main`, `memberA`, `memberB`, `memberC`) in IntelliJ on Windows:**
   - Click the **Git Branch widget** in the top-right toolbar (or bottom-right status bar) showing `main`.
   - Select **`memberA` -> Checkout** (demonstrates Member A standalone `main()` for Tasks 1–3).
   - Select **`memberB` -> Checkout** (demonstrates Member B standalone `main()` for Tasks 4–6).
   - Select **`memberC` -> Checkout** (demonstrates Member C standalone `main()` for Tasks 7–9).
   - Select **`main` -> Checkout** (demonstrates the final Stage III integrated application for Tasks 1–9).
6. **To Run the Automated JUnit 5 Verification Suite in IntelliJ on Windows:**
   - On branch `main`, open `src\test\kotlin\uk\ac\solent\campus\ae1\AE1IntegratedSystemTest.kt`.
   - Click the green double-play icon (`▶▶`) next to `class AE1IntegratedSystemTest` -> **Run 'AE1IntegratedSystemTest'**.

---

## 4. Option B: Running via Windows Double-Click Batch Scripts or PowerShell / CMD

### Double-Click in Windows File Explorer
- Double-click **`run-app-windows.bat`** to compile and run the interactive Console application in a Windows Command Prompt window.
- Double-click **`run-tests-windows.bat`** to run all JUnit 5 tests for Tasks 1–9.

### Windows PowerShell Commands
*(Note: In PowerShell, always prefix `gradlew.bat` with `.\`)*
```powershell
cd C:\path\to\AE1-Console-Group-Project

# 1. Inspect the Git branch & merge history graph:
git log --all --graph --oneline --decorate

# 2. Run the automated JUnit 5 test suite (Tasks 1-9):
.\gradlew.bat test --info

# 3. Run the interactive Console application:
.\gradlew.bat run --console=plain -q

# 4. Checkout and test individual member branches:
git checkout memberA
.\gradlew.bat run --console=plain -q

git checkout memberB
.\gradlew.bat run --console=plain -q

git checkout memberC
.\gradlew.bat run --console=plain -q

# Return to integrated main branch:
git checkout main
```

### Windows Command Prompt (`cmd.exe`) Commands
```cmd
cd /d C:\path\to\AE1-Console-Group-Project
gradlew.bat test --info
gradlew.bat run --console=plain -q
```

