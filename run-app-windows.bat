@echo off
title QHO543 AE1 - Campus Community Management System (Console Mode)
echo ====================================================================
echo   Launching QHO543 AE1 Console Application on Windows...
echo ====================================================================
call "%~dp0gradlew.bat" run --console=plain -q
pause
