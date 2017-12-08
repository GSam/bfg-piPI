import os

def rffi_run():
    from rpython.rtyper.lltypesystem import rffi, lltype
    from rpython.translator.tool.cbuild import ExternalCompilationInfo
    external_function = rffi.llexternal('myprint', [], lltype.Void, compilation_info=ExternalCompilationInfo(libraries=[os.path.abspath('./testlib.so')]))
    external_function()

def ctypes_run():
    import ctypes
    testlib = ctypes.CDLL(os.path.abspath('./testlib.so'))
    testlib.myprint()

def cffi_run():
    from cffi import FFI
    ffi = FFI()
    ffi.cdef("""void myprint(void);""")
    testlib = ffi.dlopen(os.path.abspath('./testlib.so'))
    testlib.myprint()

import pdb
pdb.set_trace()
