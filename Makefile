default: testlib

testlib: stdlib.c
	gcc -shared -Wl,-soname,testlib -o testlib.so -fPIC stdlib.c stdlib.h -Wall -g
	cp testlib.so core/
	cp stdlib.h core/

optimized: stdlib.c
	gcc -shared -Wl,-soname,testlib -o testlib.so -fPIC stdlib.c stdlib.h -Wall -O2
	cp testlib.so core/
	cp stdlib.h core/
