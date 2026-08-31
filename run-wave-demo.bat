@echo off
chcp 65001 >nul
cd /d "%~dp0"

echo ⚡ Building FastTween...
call mvn clean install -DskipTests -Dgpg.skip=true -q
if %ERRORLEVEL% NEQ 0 ( echo ❌ Build failed. & pause & exit /b %ERRORLEVEL% )

echo 🚀 Running FastTween 3D Holographic Kinetic Wave Demo (102,400 Nodes @ 120 FPS)...
cd examples\test
call mvn compile exec:java -Dexec.mainClass=fasttween.test.WaveDemo -q
if %ERRORLEVEL% NEQ 0 ( echo ❌ Execution failed. & pause & exit /b %ERRORLEVEL% )

cd ..\..
pause