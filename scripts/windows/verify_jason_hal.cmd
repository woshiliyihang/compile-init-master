@echo off
setlocal enabledelayedexpansion

if "%~1"=="" (
  echo Usage: %~nx0 ^<artifact_dir^> [serial]
  echo Example: %~nx0 C:\temp\jason-artifacts emulator-5554
  exit /b 1
)

set ARTIFACT_DIR=%~1
set SERIAL=%~2
set ADB=adb
if not "%SERIAL%"=="" set ADB=adb -s %SERIAL%

if not exist "%ARTIFACT_DIR%\android.hardware.jason-service" (
  echo Missing artifact: android.hardware.jason-service
  exit /b 2
)
if not exist "%ARTIFACT_DIR%\android.hardware.jason-client" (
  echo Missing artifact: android.hardware.jason-client
  exit /b 3
)
if not exist "%ARTIFACT_DIR%\android.hardware.jason-service.rc" (
  echo Missing artifact: android.hardware.jason-service.rc
  exit /b 4
)
if not exist "%ARTIFACT_DIR%\android.hardware.jason-service.xml" (
  echo Missing artifact: android.hardware.jason-service.xml
  exit /b 5
)

echo [1/7] adb root
%ADB% root || exit /b 10

echo [2/7] adb remount
%ADB% remount || exit /b 11

echo [3/7] push service/client and config
%ADB% push "%ARTIFACT_DIR%\android.hardware.jason-service" /vendor/bin/hw/ || exit /b 12
%ADB% push "%ARTIFACT_DIR%\android.hardware.jason-client" /vendor/bin/ || exit /b 13
%ADB% push "%ARTIFACT_DIR%\android.hardware.jason-service.rc" /vendor/etc/init/ || exit /b 14
%ADB% push "%ARTIFACT_DIR%\android.hardware.jason-service.xml" /vendor/etc/vintf/manifest/ || exit /b 15

echo [4/7] chmod binaries
%ADB% shell chmod 755 /vendor/bin/hw/android.hardware.jason-service || exit /b 16
%ADB% shell chmod 755 /vendor/bin/android.hardware.jason-client || exit /b 17

echo [5/7] reboot device
%ADB% reboot || exit /b 18

%timeout /t 15 /nobreak >nul
echo [6/7] wait-for-device
%ADB% wait-for-device || exit /b 19
%ADB% root >nul 2>nul

echo [7/7] run HAL client
%ADB% shell /vendor/bin/android.hardware.jason-client || exit /b 20

echo.
echo Success. If needed, check logs:
echo   %ADB% logcat -d -s JasonHal

endlocal
