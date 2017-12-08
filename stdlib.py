def rffi_run:
    from rpython.rtyper.lltypesystem import rffi, lltype
    from rpython.translator.tool.cbuild import ExternalCompilationInfo
    external_function = rffi.llexternal('myprint', [], lltype.Void, compilation_info=ExternalCompilationInfo(libraries=['testlib.so']))
    external_function()
    
def ctypes_run:
    import ctypes
    testlib = ctypes.CDLL('testlib.so')
    testlib.myprint()

def cffi_run:
    ffi = FFI()
    ffi.cdef("""void myprint(void);""")
    testlib = ffi.dlopen('/home/garming/pypy-env/testlib.so')
    testlib.myprint()
