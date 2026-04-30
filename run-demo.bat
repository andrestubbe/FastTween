@echo off
setlocal
cd /d "%~dp0"

echo ===========================================
echo FastTween Demo (v0.1.0)
echo ===========================================
echo.
echo Launching: Easing Showcase
echo.

cd examples
call mvn compile exec:java -Dexec.mainClass="fasttween.Demo"
if %errorlevel% neq 0 (
    echo.
    echo [ERROR] Demo failed to launch. 
    echo Ensure you ran 'compile.bat' first to install the library.
    pause
)

cd ..
