@echo off
setlocal enabledelayedexpansion
:: =============================================================================
::  PureEdgeSim Runner  —  Windows
::  Usage:  run.bat [--no-compile]
::
::  Behaviour:
::    1. Detects a local JDK under %USERPROFILE%\tools or %ProgramFiles%\Java.
::       Also honours JAVA_HOME if already set.
::    2. Detects a local Maven under %USERPROFILE%\tools or the system PATH.
::    3. Recompiles with  mvn clean compile  (skip with --no-compile).
::    4. Launches the interactive CLI menu.
:: =============================================================================

:: ── banner ───────────────────────────────────────────────────────────────────
echo.
echo  +======================================================+
echo  ^|          PureEdgeSim  --  Runner Script              ^|
echo  +======================================================+
echo.

:: ── locate script / project root ─────────────────────────────────────────────
set "PROJECT_ROOT=%~dp0"
:: Strip trailing backslash
if "%PROJECT_ROOT:~-1%"=="\" set "PROJECT_ROOT=%PROJECT_ROOT:~0,-1%"

if not exist "%PROJECT_ROOT%\pom.xml" (
    echo  [ERROR] pom.xml not found in %PROJECT_ROOT%
    echo          Run this script from the project root directory.
    pause & exit /b 1
)

:: ── parse flags & args ─────────────────────────────────────────────────────────
set "SKIP_COMPILE=false"
set "APP_ARGS="
for %%A in (%*) do (
    if /i "%%A"=="--no-compile" (
        set "SKIP_COMPILE=true"
    ) else (
        if not defined APP_ARGS (
            set "APP_ARGS=%%A"
        ) else (
            set "APP_ARGS=!APP_ARGS! %%A"
        )
    )
)

:: ── locate Java ───────────────────────────────────────────────────────────────
echo  ^> Locating Java...

:: Priority 1: already set JAVA_HOME
if defined JAVA_HOME (
    if exist "%JAVA_HOME%\bin\java.exe" (
        echo    [OK] Using existing JAVA_HOME: %JAVA_HOME%
        goto :java_found
    )
)

:: Priority 2: bundled JDK in %USERPROFILE%\tools
for /d %%D in ("%USERPROFILE%\tools\jdk*") do (
    if exist "%%D\bin\java.exe" (
        set "JAVA_HOME=%%D"
        echo    [OK] Using bundled JDK: %%D
        goto :java_found
    )
)

:: Priority 3: system java on PATH
where java >nul 2>&1
if %errorlevel%==0 (
    for /f "usebackq tokens=*" %%J in (`java -XshowSettings:property -version 2^>^&1 ^| findstr "java.home"`) do (
        set "JAVA_LINE=%%J"
    )
    :: Extract path after "= "
    for /f "tokens=3 delims== " %%P in ("!JAVA_LINE!") do set "JAVA_HOME=%%P"
    echo    [OK] Using system Java on PATH
    goto :java_found
)

:: Priority 4: common install locations
for %%P in ("%ProgramFiles%\Java" "%ProgramFiles(x86)%\Java") do (
    for /d %%D in ("%%~P\jdk*") do (
        if exist "%%D\bin\java.exe" (
            set "JAVA_HOME=%%D"
            echo    [OK] Found Java at: %%D
            goto :java_found
        )
    )
)

echo  [ERROR] No JDK found.
echo          Install JDK 17+ or place a jdk-* folder in %USERPROFILE%\tools\
pause & exit /b 1

:java_found
set "PATH=%JAVA_HOME%\bin;%PATH%"
for /f "tokens=*" %%V in ('java -version 2^>^&1') do (
    echo    Java: %%V
    goto :java_ver_done
)
:java_ver_done

:: ── locate Maven ──────────────────────────────────────────────────────────────
echo.
echo  ^> Locating Maven...

:: Priority 1: bundled Maven in %USERPROFILE%\tools
for /d %%D in ("%USERPROFILE%\tools\apache-maven*") do (
    if exist "%%D\bin\mvn.cmd" (
        set "MVN=%%D\bin\mvn.cmd"
        echo    [OK] Using bundled Maven: %%D
        goto :maven_found
    )
)

:: Priority 2: system mvn on PATH
where mvn >nul 2>&1
if %errorlevel%==0 (
    set "MVN=mvn"
    echo    [OK] Using system Maven on PATH
    goto :maven_found
)

where mvn.cmd >nul 2>&1
if %errorlevel%==0 (
    set "MVN=mvn.cmd"
    echo    [OK] Using system Maven (mvn.cmd) on PATH
    goto :maven_found
)

echo  [ERROR] Maven not found.
echo          Install Maven or place apache-maven-* in %USERPROFILE%\tools\
pause & exit /b 1

:maven_found
for /f "tokens=*" %%V in ('"%MVN%" -version 2^>^&1') do (
    echo    Maven: %%V
    goto :maven_ver_done
)
:maven_ver_done

:: ── compile ───────────────────────────────────────────────────────────────────
echo.
if /i "%SKIP_COMPILE%"=="true" (
    echo  [!] Skipping compilation  (--no-compile flag set^)
) else (
    echo  ^> Compiling PureEdgeSim...
    "%MVN%" clean compile -q --no-transfer-progress -f "%PROJECT_ROOT%\pom.xml"
    if !errorlevel! neq 0 (
        echo  [ERROR] Compilation failed -- check the output above.
        pause & exit /b 1
    )
    echo    [OK] Compilation successful.
)

:: ── launch ────────────────────────────────────────────────────────────────────
echo.
echo  ^> Launching PureEdgeSim...
echo.
echo     Tip: use options 2-6 in the menu to configure settings,
echo          then choose 1 to start the simulation.
echo.

if defined APP_ARGS (
    "%MVN%" exec:java ^
        -f "%PROJECT_ROOT%\pom.xml" ^
        -Dexec.mainClass="com.mechalikh.pureedgesim.MainApplication" ^
        -Dexec.args="!APP_ARGS!"
) else (
    "%MVN%" exec:java ^
        -f "%PROJECT_ROOT%\pom.xml" ^
        -Dexec.mainClass="com.mechalikh.pureedgesim.MainApplication"
)

echo.
echo  PureEdgeSim session ended.
echo.
pause
endlocal
