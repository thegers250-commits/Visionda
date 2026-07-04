cmake_minimum_required(VERSION 3.22.1)
project("native-lib")

# ─── Rutas de libpd ──────────────────────────────────────────────────────────
set(LIBPD_INCLUDE_DIR "${CMAKE_CURRENT_SOURCE_DIR}/src/main/cpp/libpd/include")
set(LIBPD_SO_PATH    "${CMAKE_CURRENT_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI}/libpd.so")

message(STATUS "ANDROID_ABI    = ${ANDROID_ABI}")
message(STATUS "LIBPD_SO_PATH  = ${LIBPD_SO_PATH}")

# ─── Biblioteca principal ─────────────────────────────────────────────────────
add_library(native-lib SHARED
    src/main/cpp/native-lib.cpp
    src/main/cpp/libpd_wrapper.cpp
    src/main/cpp/audio_engine.cpp
)

target_include_directories(native-lib PRIVATE
    src/main/cpp
    ${LIBPD_INCLUDE_DIR}
)

target_compile_options(native-lib PRIVATE -std=c++17 -Wall -Wno-unused-variable)

# ─── Sistema Android ──────────────────────────────────────────────────────────
find_library(log-lib    log)
find_library(aaudio-lib aaudio)

# ─── libpd pre-compilada ──────────────────────────────────────────────────────
add_library(libpd SHARED IMPORTED)
set_target_properties(libpd PROPERTIES IMPORTED_LOCATION ${LIBPD_SO_PATH})

# ─── Linker ───────────────────────────────────────────────────────────────────
target_link_libraries(native-lib
    ${log-lib}
    ${aaudio-lib}
    libpd
)
