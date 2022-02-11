@echo off
set result=1
if not "%NDK_PATH%"=="" goto make
if not exist ..\..\ndk_path.txt goto nondk
for /f "delims=" %%i in (..\..\ndk_path.txt) do set NDK_PATH=%%~i&goto make

:make
echo %%NDK_PATH%%=%NDK_PATH%
echo.
set oldpath=%path%
set path=%path%;%NDK_PATH%
call ndk-build NDK_PROJECT_PATH=. APP_BUILD_SCRIPT=Android.mk.static
set result=%errorlevel%
if not %result%==0 goto end
call ndk-build NDK_PROJECT_PATH=. APP_BUILD_SCRIPT=Android.mk.shared
set result=%errorlevel%
set path=%oldpath%
if not %result%==0 goto end
if not exist libs mkdir libs
dir /ad /b libs>nul 2>nul
set result=%errorlevel%
if not %result%==0 goto end
for /f "usebackq delims=" %%i in (`dir /ad /b obj\local`) do call :install-static "%%~i"
goto end

:install-static
for /f "usebackq delims=" %%i in (`dir /a-d /b obj\local\%1\*.a`) do call :copy-lib "%~1" "%%~i"
goto :eof

:copy-lib
echo [%~1] Install        : %~2 =^> libs\%~1\%~2
if not exist "libs\%~1" mkdir "libs\%~1"
copy /b /y "obj\local\%~1\%~2" "libs\%~1\%~2">nul
goto :eof

:nondk
echo Please set %%NDK_PATH%%
goto end

:end
echo.
pause
exit /b %result%