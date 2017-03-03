#!/bin/bash

# Script based on code written by 
# Collin (http://unix.stackexchange.com/users/154766/collin) at 
# http://unix.stackexchange.com/a/259639 and licensed under the MIT license.

sudo apt-get install -y cmake yasm libjpeg-dev libsdl-image1.2-dev libsdl1.2-dev git g++

pushd /tmp
    wget -O libpng-1.6.21.tar.xz "https://downloads.sourceforge.net/project/libpng/libpng16/older-releases/1.6.21/libpng-1.6.21.tar.gz?ts=$(date +%s)"
    tar -xf libpng-1.6.21.tar.xz
    pushd libpng-1.6.21
        ./configure
        make clean
        make -j
        sudo make install
    popd
    rm -rf libbpg
    git clone "https://github.com/mirrorer/libbpg"
    pushd libbpg
        patch <<EOF
--- Makefile    2016-02-03 11:43:37.883142427 -0500
+++ Makefile    2016-02-03 11:44:20.867143492 -0500
@@ -41,6 +41,7 @@
 CFLAGS:=-Os -Wall -MMD -fno-asynchronous-unwind-tables -fdata-sections -ffunction-sections -fno-math-errno -fno-signed-zeros -fno-tree-vectorize -fomit-frame-pointer
 CFLAGS+=-D_FILE_OFFSET_BITS=64 -D_LARGEFILE_SOURCE -D_REENTRANT
 CFLAGS+=-I.
+CFLAGS+=-I/usr/local/include
 CFLAGS+=-DCONFIG_BPG_VERSION=\"\$(shell cat VERSION)\"
 ifdef USE_JCTVC_HIGH_BIT_DEPTH
 CFLAGS+=-DRExt__HIGH_BIT_DEPTH_SUPPORT
@@ -59,6 +60,7 @@
 else
 LDFLAGS+=-Wl,--gc-sections
 endif
+LDFLAGS+=-L /usr/local/lib
 CFLAGS+=-g
 CXXFLAGS=\$(CFLAGS)
EOF
        make clean
        make -j
        sudo make install
    popd
popd
sudo ldconfig  # Required for bpgenc to find libpng16.

