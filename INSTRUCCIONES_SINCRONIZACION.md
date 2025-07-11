# Instrucciones para Sincronizar el Proyecto en Android Studio

## Problema
Los cambios realizados no se ven en Android Studio porque el proyecto necesita ser sincronizado.

## Solución

### Opción 1: Sincronización Automática
1. Abre Android Studio
2. Ve a **File > Sync Project with Gradle Files** (o presiona Ctrl+Shift+O)
3. Espera a que termine la sincronización

### Opción 2: Sincronización Manual
1. En Android Studio, busca el botón **"Sync Now"** (icono de elefante con flecha)
2. Haz clic en él para sincronizar el proyecto

### Opción 3: Clean y Rebuild
1. Ve a **Build > Clean Project**
2. Espera a que termine
3. Ve a **Build > Rebuild Project**

### Opción 4: Invalidar Caché y Reiniciar
1. Ve a **File > Invalidate Caches and Restart**
2. Selecciona **"Invalidate and Restart"**
3. Espera a que Android Studio se reinicie

## Verificación
Después de la sincronización, deberías ver:
- Los nuevos campos en el layout de registro (marca, modelo, año)
- El icono del vehículo en los recursos
- Los layouts actualizados
- Las actividades con el código mejorado

## Si persiste el problema
1. Cierra Android Studio completamente
2. Abre el proyecto nuevamente
3. Ejecuta la sincronización

## Nota importante
Asegúrate de que Android Studio esté usando la versión correcta de Java (JDK 8 o superior). 