@echo off
echo ========================================
echo    SINCRONIZACION DE PROYECTO ANDROID
echo ========================================
echo.

echo 1. Deteniendo procesos de Gradle...
gradlew --stop 2>nul

echo 2. Limpiando proyecto...
gradlew clean 2>nul

echo 3. Sincronizando proyecto...
gradlew build 2>nul

echo.
echo ========================================
echo    PROCESO COMPLETADO
echo ========================================
echo.
echo Ahora en Android Studio:
echo 1. Ve a File > Sync Project with Gradle Files
echo 2. O presiona Ctrl+Shift+O
echo 3. Si no funciona, ve a File > Invalidate Caches and Restart
echo.
pause 