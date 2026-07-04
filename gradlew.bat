@if "%DEBUG%"=="" @echo off
@rem Gradle startup script for Windows
@rem
@rem Prepend the current directory to the path to allow java to be found
@rem if it's installed in the directory structure
@if exist "%~dp0.." (
    set DIRNAME=%~dp0..
) else (
    set DIRNAME=%CD%
)

call "%DIRNAME%\gradle\wrapper\gradle-wrapper.bat" %*
