cmake_minimum_required(VERSION 3.22.1)
project("native-lib")

# ─── Incluir headers de LibPD ────────────────────────────────────────────────
set(LIBPD_INCLUDE "${CMAKE_CURRENT_SOURCE_DIR}/src/main/cpp/libpd/include")
set(LIBPD_SO     "${CMAKE_CURRENT_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI}/libpd.so")

# ─── Biblioteca compartida principal ─────────────────────────────────────────
add_library(native-lib SHARED
    src/main/cpp/native-lib.cpp
    src/main/cpp/libpd_wrapper.cpp
    src/main/cpp/audio_engine.cpp
)

target_include_directories(native-lib PRIVATE
    ${LIBPD_INCLUDE}
    src/main/cpp
)

target_compile_options(native-lib PRIVATE
    -std=c++17
    -Wall
    -Wno-unused-variable
)

# ─── Librerías del sistema Android ───────────────────────────────────────────
find_library(log-lib    log)
find_library(aaudio-lib aaudio)

# ─── Importar libpd.so como biblioteca pre-compilada ─────────────────────────
add_library(libpd SHARED IMPORTED)
set_target_properties(libpd PROPERTIES
    IMPORTED_LOCATION ${LIBPD_SO}
)

# ─── Linkear todo ────────────────────────────────────────────────────────────
target_link_libraries(native-lib
    ${log-lib}
    ${aaudio-lib}
    libpd
)
