# Medical Image Compression

Written for CMPUT 414 Winter 2017 at the University of Alberta.

## Requirements

* Java 1.8+

* Maven 

## Supported Systems

Only 64-bit operating systems are supported because x265 needs a bit depth 
greater than eight.

* Windows x64

* Ubuntu x64

## Installation Notes

### Windows

The compiled BPG binaries have already been included in this project under the 
`bin\bpg\win64` folder. No further setup is required.

### Linux

For Linux distributions, the BPG encoder must be installed. For Debian varients 
such as Ubuntu, the included setup script, `debian-setup.sh`, should download 
and install all the necessary dependencies (PNG Library v1.6) to use and 
compile the BPG binaries. For all other Linux distributions, please follow the 
BPG README: https://github.com/mirrorer/libbpg.

## JavaFX Application

![Medical Image Compressor JavaFX Application](screenshot.png?raw=true)

The program includes a GUI JavaFX program that can be easily built using Maven:

```
mvn package
```

The executable JAR will be in `/target/jfx/app/`.

On a Windows machine, a self-contained native application with the Java Runtime 
Environment will be in `/target/jfx/native/Medical Image Compression/`.

On Windows, verify that the BPG program and dependencies are also present in 
the root directories of the output. For example:

`/target/jfx/app/bin/bpg/win64/` should contain the BPG program for the 
executable JAR program and 
`/target/jfx/native/Medical Image Compression/app/bin/bpg/win64/` should 
contain the BPG program for the native executable program.

### Usage

* By default, the program expects to read input images within the `input/` 
  folder. Raw input images should be contained within a second, sub-directory 
  beneath this input directory. For example, `input/512x512/` should contain 
  all images with the given resolution of 512x512. The program will not read 
  any images that are contained solely in the root of the `input/` directory.

* After processing, the output images will be located in the `output/` 
  directory. This folder is located relative to the application itself.  

## Sources

* The BPG code and binaries are by Fabrice Bellard with portions of the code 
  licensed under the GPLv2, LGPL, and BSD licenses. For more information 
  regarding the license terms of BPG and the dependent HEVC compression 
  technology, please visit https://github.com/mirrorer/libbpg.

* The `debian-setup.sh` contains code written by 
  Collin (http://unix.stackexchange.com/users/154766/collin) at 
  http://unix.stackexchange.com/a/259639 and licensed under the MIT license.

* OpenCV segmentation and flood-fill technique inspired by  
  Luigi De Russis's (https://github.com/luigidr) code from  
  https://github.com/opencv-java/image-segmentation
  
* Lossless JPEG code based on code written by 
  bobince (http://stackoverflow.com/users/18936/bobince)
  From http://stackoverflow.com/a/7619091/2557554 and licensed under 
  CC-BY-SA 3.0 (https://creativecommons.org/licenses/by-sa/3.0/deed.en)

* Inverted flood-fill code based on the code and ideas of 
  Michale Koval (http://stackoverflow.com/users/111426/michael-koval)
  From http://stackoverflow.com/a/5740264/2557554 and licensed under 
  CC-BY-SA 3.0 (https://creativecommons.org/licenses/by-sa/3.0/deed.en)
  
* Progress double field idea by 
  James_D (http://stackoverflow.com/users/2189127/james-d)
  From http://stackoverflow.com/a/34358090/2557554 and licensed under
  CC-BY-SA 3.0 (https://creativecommons.org/licenses/by-sa/3.0/deed.en)

