# LibPD Host Skeleton — Prototipo de integración

Este directorio contiene un ejemplo mínimo en C++ con las funciones de mapeo definidas en el manifiesto y un esqueleto para integrar LibPD.

Requisitos:
- `libpd` (libpd embedded) o Pure Data instalados para pruebas locales.
- CMake y un compilador C++ moderno.

Pasos rápidos (Linux/Windows con MSYS/CMake):

1. Instalar libpd y dejar headers/libs accesibles.
2. Crear un build dir y compilar:

```bash
mkdir build && cd build
cmake ..
cmake --build .
```

3. Ejecutar el binario `libpd_host` y observar logs de parámetros.

Nota: este ejemplo no incluye la compilación cruzada para Android; sirve como referencia para portar las funciones de mapeo y la lógica de control al motor nativo (NDK) y a LibPD.
