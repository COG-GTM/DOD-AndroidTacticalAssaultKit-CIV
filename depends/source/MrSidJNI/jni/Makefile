LIBRARY = libMrsidLibrary.so

LIBS = ../libs/x86/$(LIBRARY) ../libs/armeabi-v7a/$(LIBRARY) ../libs/arm64-v8a/$(LIBRARY)

SEP = :

TOPDIR = ../../../atak

CLASSPATH = "$(TOPDIR)/ATAK/src/build/intermediates/javac/milDebug/classes$(SEP)$(TOPDIR)/ATAKMapEngine/lib/build/intermediates/javac/milDebug/classes$(SEP)$(TOPDIR)/MapCoreInterfaces/bin/classes$(SEP)$(ANDROID_SDK)/platforms/android-21/android.jar"

all : release

clean :
	rm -f MrsidLibrary.h
	rm -rf ../libs 
	rm -rf ../obj

install : release
	cp -R ../libs ../../ATAKMapEngine

MrsidLibrary.cpp : MrsidLibrary.h

$(LIBS) : MrsidLibrary.cpp
                 
MrsidLibrary.h : $(TOPDIR)/ATAKMapEngine/lib/src/main/java/com/lizardtech/android/mrsid/MrsidImageReaderImp.java
	rm -rf $@.javah_tmp && mkdir -p $@.javah_tmp && \
	javac -proc:none -classpath $(CLASSPATH) -d $@.javah_tmp -h $@.javah_tmp $< && \
	cat $@.javah_tmp/*.h > $@ && rm -rf $@.javah_tmp
                 
release : $(LIBS)
	tar xvf *.tar.gz 
	$(ANDROID_NDK)/ndk-build


