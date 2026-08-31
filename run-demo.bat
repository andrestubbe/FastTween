@echo off
chcp 65001 >nul
cd /d "%~dp0"

echo ⚡ Building FastTween...
call mvn clean install -DskipTests -q
if %ERRORLEVEL% NEQ 0 ( echo ❌ Build failed. & pause & exit /b %ERRORLEVEL% )

echo 🚀 Running FastTween GUI Demo...
cd examples\Demo
call mvn compile exec:java -Dexec.mainClass=fasttween.demo.Demo -q
if %ERRORLEVEL% NEQ 0 ( echo ❌ Demo execution failed. & pause & exit /b %ERRORLEVEL% )

cd ..\..
pause
