@if "%DEBUG%" == "" @echo off
@rem Minimal Windows gradle wrapper launcher. Relies on gradle\wrapper\gradle-wrapper.jar
@rem being present. If it's missing, open this folder in Android Studio (recommended)
@rem or run `gradle wrapper` once using a system Gradle install.

setlocal
set DIRNAME=%~dp0
set WRAPPER_JAR=%DIRNAME%gradle\wrapper\gradle-wrapper.jar

if not exist "%WRAPPER_JAR%" (
  echo ERROR: %WRAPPER_JAR% is missing.
  echo Bootstrap it once with either:
  echo   1^) Open this folder in Android Studio ^(recommended^), or
  echo   2^) Install Gradle and run 'gradle wrapper' here.
  exit /b 1
)

if defined JAVA_HOME (
  set JAVA_EXE=%JAVA_HOME%\bin\java.exe
) else (
  set JAVA_EXE=java.exe
)

"%JAVA_EXE%" -classpath "%WRAPPER_JAR%" org.gradle.wrapper.GradleWrapperMain %*
endlocal
