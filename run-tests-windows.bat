@echo off
title QHO543 AE1 - JUnit 5 Verification Test Suite
echo ====================================================================
echo   Running QHO543 AE1 JUnit 5 Tests (Tasks 1 through 9) on Windows...
echo ====================================================================
call "%~dp0gradlew.bat" test --info
echo.
echo HTML Test Report generated at: build\reports\tests\test\index.html
pause

