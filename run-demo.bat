@echo off

echo ==========================================
echo   FastTween v0.1.0 - Demo
echo ==========================================
echo.
echo Dependencies resolved from JitPack
echo.

cd examples\Demo
call mvn compile exec:java -Dexec.mainClass=fasttween.demo.Demo
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERROR] Demo failed to launch.
    pause
)
cd ..\..
pause
