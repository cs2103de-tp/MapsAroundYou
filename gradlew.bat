@rem
@rem Copyright 2015 the original author or authors.
@rem
@rem Licensed under the Apache License, Version 2.0 (the "License");
@rem you may not use this file except in compliance with the License.
@rem You may obtain a copy of the License at
@rem
@rem      https://www.apache.org/licenses/LICENSE-2.0
@rem
@rem Unless required by applicable law or agreed to in writing, software
@rem distributed under the License is distributed on an "AS IS" BASIS,
@rem WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@rem See the License for the specific language governing permissions and
@rem limitations under the License.
@rem

@if "%DEBUG%"=="" @echo off
@rem ##########################################################################
@rem
@rem  Gradle startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%"=="" set DIRNAME=.
@rem This is normally unused
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%

@rem Resolve any "." and ".." in APP_HOME to make it shorter.
for %%i in ("%APP_HOME%") do set APP_HOME=%%~fi

@rem Add default JVM options here. You can also use JAVA_OPTS and GRADLE_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS="-Xmx64m" "-Xms64m"

@rem On Windows ARM64, JavaFX requires an x64 JDK in this project setup.
@rem Check both variables: PROCESSOR_ARCHITECTURE is ARM64 from a native
@rem ARM64 process, but from x64-emulated shells (e.g. Git Bash) it reports
@rem AMD64 while PROCESSOR_ARCHITEW6432 reveals the true architecture.
if /I "%PROCESSOR_ARCHITECTURE%"=="ARM64" call :useX64JavaOnArm64
if /I "%PROCESSOR_ARCHITEW6432%"=="ARM64" call :useX64JavaOnArm64

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if %ERRORLEVEL% equ 0 goto execute

echo. 1>&2
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH. 1>&2
echo. 1>&2
echo Please set the JAVA_HOME variable in your environment to match the 1>&2
echo location of your Java installation. 1>&2

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto execute

echo. 1>&2
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME% 1>&2
echo. 1>&2
echo Please set the JAVA_HOME variable in your environment to match the 1>&2
echo location of your Java installation. 1>&2

goto fail

:useX64JavaOnArm64
if defined JAVA_HOME (
	if /I not "%JAVA_HOME:x64=%"=="%JAVA_HOME%" goto :eof
)

set "X64_JAVA_HOME="
@rem Search common JDK install locations for an x64 build.
for /d %%D in ("%ProgramFiles%\Microsoft\jdk-*-x64") do set "X64_JAVA_HOME=%%~fD"
if defined X64_JAVA_HOME if exist "%X64_JAVA_HOME%\bin\java.exe" goto setX64
for /d %%D in ("%ProgramFiles%\Eclipse Adoptium\jdk-*-x64") do set "X64_JAVA_HOME=%%~fD"
if defined X64_JAVA_HOME if exist "%X64_JAVA_HOME%\bin\java.exe" goto setX64
for /d %%D in ("%ProgramFiles%\Java\jdk-*-x64") do set "X64_JAVA_HOME=%%~fD"
if defined X64_JAVA_HOME if exist "%X64_JAVA_HOME%\bin\java.exe" goto setX64
@rem Also search Program Files (x86) in case an x64 JDK was installed there.
for /d %%D in ("%ProgramFiles(x86)%\Microsoft\jdk-*-x64") do set "X64_JAVA_HOME=%%~fD"
if defined X64_JAVA_HOME if exist "%X64_JAVA_HOME%\bin\java.exe" goto setX64
goto :eof

:setX64
set "JAVA_HOME=%X64_JAVA_HOME%"
set "PATH=%JAVA_HOME%\bin;%PATH%"
goto :eof

:execute
@rem Setup the command line

set CLASSPATH=%APP_HOME%\gradle\wrapper\gradle-wrapper.jar


@rem Execute Gradle
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %GRADLE_OPTS% "-Dorg.gradle.appname=%APP_BASE_NAME%" -classpath "%CLASSPATH%" org.gradle.wrapper.GradleWrapperMain %*

:end
@rem End local scope for the variables with windows NT shell
if %ERRORLEVEL% equ 0 goto mainEnd

:fail
rem Set variable GRADLE_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
set EXIT_CODE=%ERRORLEVEL%
if %EXIT_CODE% equ 0 set EXIT_CODE=1
if not ""=="%GRADLE_EXIT_CONSOLE%" exit %EXIT_CODE%
exit /b %EXIT_CODE%

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega
