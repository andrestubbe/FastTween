@echo off
chcp 65001 >nul
cd /d "%~dp0"

echo ⚡ Building FastTween...
call mvn clean install -DskipTests -Dgpg.skip=true -q
if %ERRORLEVEL% NEQ 0 ( echo ❌ Build failed. & pause & exit /b %ERRORLEVEL% )

echo 🚀 Running FastTween Math Comparison GUI Test...
cd examples\test
call mvn compile exec:java -Dexec.mainClass=fasttween.test.Demo -q
if %ERRORLEVEL% NEQ 0 ( echo ❌ Test execution failed. & pause & exit /b %ERRORLEVEL% )

cd ..\..
pause