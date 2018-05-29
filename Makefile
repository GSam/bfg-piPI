default: testlib

testlib: stdlib.c
	gcc -shared -Wl,-soname,testlib -o testlib.so -fPIC stdlib.c
