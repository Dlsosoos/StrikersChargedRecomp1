cmake_minimum_required(VERSION 3.18)
project(strikersrecomp CXX)

set(CMAKE_CXX_STANDARD 17)

# Quando integrar o decomp, adicione aqui os fontes dele e da camada de plataforma:
#   file(GLOB_RECURSE STRIKERS_SRC ${CMAKE_SOURCE_DIR}/../../../../third_party/strikers/src/*.c*)
#   file(GLOB_RECURSE PLATFORM_SRC ${CMAKE_SOURCE_DIR}/platform/*.cpp)
add_library(strikersrecomp SHARED native-lib.cpp)

find_library(log-lib log)
target_link_libraries(strikersrecomp ${log-lib})
