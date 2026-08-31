@echo off
chcp 65001 >nul
cd /d "%~dp0"

echo ⚡ Building FastTween...
call mvn clean install -DskipTests -Dgpg.skip=true -q
if %ERRORLEVEL% NEQ 0 ( echo ❌ Build failed. & pause & exit /b %ERRORLEVEL% )

echo 🚀 Running FastTween + FastGPU 1,000,000 Particle Kinetic Swarm Demo (120 FPS)...
cd examples\test
call mvn compile exec:java -Dexec.mainClass=fasttween.test.GPUMillionDemo -q
if %ERRORLEVEL% NEQ 0 ( echo ❌ Execution failed. & pause & exit /b %ERRORLEVEL% )

cd ..\..
pause