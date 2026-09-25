PGSCUTILS_LIBARCHDIR := linux/x86_64
TAKTHIRDPARTYDIR := ../../../takthirdparty/builds/linux-amd64-release
RANLIB := ranlib
# For mac
#PGSCUTILS_LIBARCHDIR := macosx/x86_64
PGSCUTILS_SHLIBDIR := shlib/$(PGSCUTILS_LIBARCHDIR)
PUBINCDIR := ../include
PGSCUTILSDIR := $(TAKTHIRDPARTYDIR)/PGSC_Utils
CURL_CONF := $(TAKTHIRDPARTYDIR)/bin/curl-config

COMMOPATH=../..

ifndef HOST
  ifeq "$(shell uname | cut -d_ -f1)" "CYGWIN"
    HOST = win32
  else ifeq "$(shell uname)" "Linux"
    HOST = linux
  endif
endif

ifndef TARGET
  TARGET=$(HOST)
endif

ifeq ($(TARGET),win32)
$(error this makefile does not properly support windows targets)
endif

ifeq ($(findstring linux,$(TARGET)),linux)
  JAVA_TARGET=linux
else ifeq ($(findstring macos,$(TARGET)),macos)
  JAVA_TARGET=darwin
else
  JAVA_TARGET=$(TARGET)
endif

ifeq ($(findstring macos,$(TARGET)),macos)
JAVA_INC=-I"$(JAVA_HOME)/include" -I"$(JAVA_HOME)/include/$(JAVA_TARGET)"
PTHREAD_LIB=-lpthread
LINK_FLAGS += -Wl
else ifneq ($(findstring android,$(TARGET)),android)
JAVA_INC=-I"$(JAVA_HOME)/include" -I"$(JAVA_HOME)/include/$(JAVA_TARGET)"
PTHREAD_LIB=-lpthread
LINK_FLAGS += -Wl,--hash-style=both
endif


LIBNAME=commoncommojni


LIBS=-L$(COMMOPATH)/core/impl -L$(TAKTHIRDPARTYDIR)/lib -lcommoncommo -lxml2 -lssl -lcrypto $(PTHREAD_LIB) $(shell $(CURL_CONF) --libs) -lmicrohttpd -liconv -lprotobuf-lite
INCLUDES=-I$(COMMOPATH)/core/include                                         \
         -I$(TAKTHIRDPARTYDIR)/include                                       \
         -I$(TAKTHIRDPARTYDIR)/PGSC_Utils/include                            \
         $(JAVA_INC)                                                         \
         -I$(TAKTHIRDPARTYDIR)/include/libxml2


CXX = g++
CXXFLAGS += $(COMMO_CXXFLAGS) -Wall -Werror -Wno-unused                      \
            -D__STDC_LIMIT_MACROS                                            \
            $(INCLUDES) -fPIC -DCURL_STATICLIB -fstack-protector-all         \
            -std=c++11
DEBUG_CXXFLAGS = -g
RELEASE_CXXFLAGS = -O2

OBJS=$(subst .cpp,.o,$(wildcard *.cpp))

ifeq ($(findstring macos,$(TARGET)),macos)
LIBRARY = lib$(LIBNAME).dylib
else
LIBRARY = lib$(LIBNAME).so
endif

LINK_FLAGS += -shared
SEP=:
EXESUF=


all : release

debug : CXXFLAGS += $(DEBUG_CXXFLAGS)
debug : LINK_FLAGS += $(DEBUG_LINK_FLAGS)
release : CXXFLAGS += $(RELEASE_CXXFLAGS)
debug release : $(LIBRARY)

$(LIBRARY): $(OBJS) $(wildcard $(COMMOPATH)/core/impl/libcommoncommo.* $(TAKTHIRDPARTYDIR)/lib/libcommoncommo.*)
	$(CXX) $(LINK_FLAGS) -o $@ $(OBJS) $(LIBS)


clean:
	rm -f $(OBJS) $(LIBRARY) $(FILES_TO_CLEAN)


ifndef JAVA_HOME
  $(error $$JAVA_HOME is not set! I do not know what to do!)
endif

JAVAC = "$(JAVA_HOME)/bin/javac"
JROOT=$(COMMOPATH)/jni
CLASSP=$(JROOT)

# Rules and vars specific to this library
FILES_TO_CLEAN += $(wildcard j*.h)
JSRCS=$(JROOT)/com/atakmap/commoncommo
PACKAGE=com.atakmap.commoncommo

# javah was removed in JDK 10; `javac -h` emits one header per class into a
# directory, so generate into a scratch dir and merge into the target header.
JAVAH_TMP=$@.javah_tmp
define JAVAHCMD
rm -rf $(JAVAH_TMP) && mkdir -p $(JAVAH_TMP) && \
$(JAVAC) -proc:none -classpath $(CLASSP) -d $(JAVAH_TMP) -h $(JAVAH_TMP) $(JSRCS)/$(1).java && \
find $(JAVAH_TMP) -name '*.h' -exec cat {} + > $@ && rm -rf $(JAVAH_TMP)
endef

autoheaders: $(subst .cpp,.h,$(wildcard j*.cpp))

.PHONY: autoheaders

commojni_common.o: commojni_common.cpp commojni_common.h commojni_impl.h

jcommo.h: $(JSRCS)/Commo.class
	$(call JAVAHCMD,Commo)
jcommo.o: jcommo.cpp jcommo.h commojni_common.h commojni_impl.h

jcotpointdata.h: $(JSRCS)/CoTPointData.class
	$(call JAVAHCMD,CoTPointData)
jcotpointdata.o: jcotpointdata.cpp jcotpointdata.h commojni_common.h

jcloudclient.h: $(JSRCS)/CloudClient.class
	$(call JAVAHCMD,CloudClient)
jcloudclient.o: jcloudclient.cpp jcloudclient.h commojni_common.h

jnifileioprovider.h: $(JSRCS)/FileIOProvider.class
	$(call JAVAHCMD,FileIOProvider)
jnifileioprovider.o: jnifileioprovider.cpp jnifileioprovider.h commojni_common.h commojni_impl.h


%.class: %.java
	ant -f $(JROOT)/build.xml compile
