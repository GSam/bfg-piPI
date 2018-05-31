default: testlib

testlib: stdlib.c
	gcc -shared -Wl,-soname,testlib -o testlib.so -fPIC stdlib.c stdlib.h
	cp testlib.so core/
	cp stdlib.h core/
