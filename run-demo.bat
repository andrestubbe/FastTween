@echo off
chcp 65001 >nul

echo ⚡ Building FastTheme...
cd ..\FastTheme
call mvn -q clean install -DskipTests
if %ERRORLEVEL% NEQ 0 ( pause & exit /b )
cd ..\FastTween

echo ⚡ Building FastTween...
call mvn -q clean install -DskipTests
if %ERRORLEVEL% NEQ 0 ( pause & exit /b )

echo 🚀 Running Demo...
cd examples\Demo
call mvn -q compile exec:java -Dexec.mainClass=fasttween.demo.Demo
cd ..\..
pause
