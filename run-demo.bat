@echo off
chcp 65001 >nul
cd /d "%~dp0"

echo ==========================================
echo   FastTween v0.1.0 - Demo
echo ==========================================
echo.
echo Dependencies resolved from JitPack
echo.

cd examples\Demo
call mvn -q compile exec:java -Dexec.mainClass=fasttween.demo.Demo
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERROR] Demo failed to launch.
    pause
)
cd ..\..
pause
