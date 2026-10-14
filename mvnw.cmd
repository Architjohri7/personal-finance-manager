@echo off
where mvn >nul 2>nul
if %ERRORLEVEL% EQU 0 (mvn %*) else (echo Maven is not installed. Please install Maven 3.9.9 and retry. & exit /b 1)
