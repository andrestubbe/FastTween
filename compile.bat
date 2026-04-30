@echo off
setlocal
cd /d "%~dp0"

echo ===========================================
echo FastTween Builder (v0.1.0)
echo ===========================================
echo.

echo [1/1] Building FastTween...
call mvn clean install -DskipTests
if %errorlevel% neq 0 (
    echo.
    echo [ERROR] Build failed.
    pause
    exit /b %errorlevel%
)

echo.
echo ===========================================
echo Build Successful! 
echo ===========================================
pause
