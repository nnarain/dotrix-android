# Sets the minimum version of CMake required to build the native
# library. You should either keep the default value or only pass a
# value of 3.4.0 or lower.

cmake_minimum_required(VERSION 3.4.1)

# test system endianness
include(TestBigEndian)
test_big_endian(IS_BIG_ENDIAN)

if(IS_BIG_ENDIAN)
    set(ENDIAN "__BIGENDIAN__")
else(IS_BIG_ENDIAN)
    set(ENDIAN "__LITTLEENDIAN__")
endif(IS_BIG_ENDIAN)

file(GLOB_RECURSE GAMEBOYCORE_SOURCES "src/main/cpp/gameboycore/src/core/*.cpp")

include_directories(
    src/main/cpp/gameboycore/include/
)

add_library(gameboycore-jni SHARED
    src/main/cpp/gameboycore_jni.cpp
    ${GAMEBOYCORE_SOURCES}
)

set_target_properties(gameboycore-jni PROPERTIES
	COMPILE_FLAGS "-D${ENDIAN}"
)

find_library(
    log-lib
    log
)

target_link_libraries(gameboycore-jni
    ${log-lib}
)
