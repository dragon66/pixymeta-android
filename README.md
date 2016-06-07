# pixymeta-android
Combined Android/j2se version of PixyMeta - a pure Java image metadata manipulation tool.

## Subprojects

* pixymeta-lib
  * platform independant code useable by android and by j2se
  * has no code to handle (preview-)Bitmaps that are platform dependant.
  * if you want to use IBitmap (an interface replacement for Bitmap) you either need pixymeta-android-lib or pixymeta-j2se-lib
* pixymeta-android-lib
  * android specific implementation to handle (preview-)Bitmaps
* pixymeta-j2se-lib
  * java/awt specific implementation to handle (preview-)Bitmaps
* pixymeta-j2se-demo
  * java commandline programm to demonstrate image meta functions

## Dependencies 

* implementation of imageio drivers 
  * com.twelvemonkeys.imageio:imageio-tiff (version 3.2.1 working with pixymeta-j2se-demo)
  * com.github.jai-imageio:jai-imageio-core (version 1.3.1 has problems with tiff files)

## How to build

* use `./gradlew --gui` 
  * to start a build gui
* use `./gradlew pixymeta-j2se-demo:fatJar` 
  * to create `pixymeta-j2se-demo-all.jar` that contains all dependencies and testimages
  * execute `java -jar pixymeta-j2se-demo-all.jar` to run the demo
   
  

branch gradle-build of k3b-s fork: [![Build Status](https://travis-ci.org/k3b/pixymeta-android.svg?branch=gradle-build)](https://travis-ci.org/k3b/pixymeta-android)

Image metadata manipulation:
----------------------------------------
- JPEG and TIFF EXIF data manipulation
   * Insert EXIF data into JPEG.
   * Extract EXIF data from JPEG.
   * Remove EXIF data and other insignificant APPn segments from JPEG.
   * Insert EXIF data into TIFF.
   * Read EXIF data embedded in TIFF.
- JPEG and TIFF ICC Profile support
   * Insert ICC profile to JPEG and TIFF.
   * Extract ICC profile from JPEG and TIFF.
- JPEG and TIFF IPTC metadata support
   * Insert IPTC directly to TIFF via RichTiffIPTC tag.
   * Insert IPTC to JPEG via APP13 Photoshop IRB
   * Extract IPTC from both TIFF and JPEG
- JPEG and TIFF Photoshop IRB metadata support
   * Insert IRB into JPEG via APP13 segment
   * Insert IRB into TIFF via tag PHOTOSHOP.
   * Extract IRB data from both JPEG and TIFF.
- JPEG, GIF, PNG, TIFF XMP metadata support
   * Insert XMP metada into JPEG, GIF, PNG, and TIFF image
   * Extract XMP metadata from JPEG, GIF, PNG, and TIFF image
   * In case of JPEG, handle normal XMP and extendedXMP which cannot fit into one APP1 segment

Where can I get the latest release?
-----------------------------------
There is currently no stable release of PIXYMETA-ANDROID. However you can pull the latest SNAPSHOT from Sonatype SNAPSHOT repository by adding the snapshot repository to your pom.xml:
 
```xml
<repository>
  <id>oss.sonatype.org</id>
  <name>Sonatype Snapshot Repository</name>
  <url>https://oss.sonatype.org/content/repositories/snapshots</url>
  <releases>
    <enabled>false</enabled>
  </releases>
  <snapshots>
    <enabled>true</enabled>
  </snapshots>
</repository> 
```

Then you can use the SNAPSHOT version of ICAFE in your pom.xml:

```xml
<dependency>
  <groupId>com.github.dragon66</groupId>
  <artifactId>pixymeta-android</artifactId>
  <version>1.0-SNAPSHOT</version>
</dependency>
``` 

Go to the [wiki] page to see this library in action or grab the "pixymeta-android.jar" from the lib folder and try it yourself!

[wiki]:https://github.com/dragon66/pixymeta-android/wiki
[Open]:https://github.com/dragon66/pixymeta-android/issues/new
Tested on Android Nexus4 virtual device only!!!

[Project using pixymeta-android](https://github.com/CreativeCommons-Seneca/cc-xmp-tag)

Suggestions? custom requirements? [Open] an issue or send email to me directly: yuwen_66@yahoo.com
