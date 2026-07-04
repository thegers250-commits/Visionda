cmake_minimum_required(VERSION 3.22.1)
project("native-lib")

add_library(native-lib SHARED 
    src/main/cpp/native-lib.cpp
)

target_include_directories(native-lib PRIVATE
    src/main/cpp/libpd/include
)

find_library(log-lib log)
find_library(aaudio-lib aaudio)

target_link_libraries(native-lib
    ${log-lib}
    ${aaudio-lib}
    ${CMAKE_CURRENT_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI}/libpd.so
)
