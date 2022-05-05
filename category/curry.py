

def curry(*args, **kwargs):
    print([*args])

curry(1, 2, 3)

def mult(x, y):
    return x*y

import inspect

def curry(func):
    import pdb
    sig = inspect.signature(func)
    pdb.set_trace()

    l = lambda x: lambda y: func(x, y)


def curry(func):
    f_args = []
    f_kwargs = {}
    def f(*args, **kwargs):
        nonlocal f_args, f_kwargs
        if args or kwargs:
            f_args += args
            f_kwargs.update(kwargs)
            return f
        else:
            return func(*f_args, **f_kwargs)
    return f

class FunctionWrapper(object):
    def __init__(self, func, length):
        self._func = func
        self._length = length

    def __getattr__(self, attr):
        return getattr(self._func, attr)

    def __call__(self, *args, **kwargs):
        return self._func(*args, **kwargs)

def curry(func):
    f_args = []
    f_kwargs = {}
    sig = inspect.signature(func)
    if 'args' in sig.parameters or 'kwargs' in sig.parameters:
        length = -1
    else:
        length = len(sig.parameters)

    def f(*args, **kwargs):
        nonlocal f_args, f_kwargs, length
        if args or kwargs:
            f_args += args
            f_kwargs.update(kwargs)
            length -= len(args) + len(kwargs)
            return FunctionWrapper(f, length)
        else:
            return func(*f_args, **f_kwargs)

    return FunctionWrapper(f, length)

def g(a, *args, **kwargs):
    pass

#print(curry(mult))
print(curry(mult)(2)(3)())
print(curry(g)(1)._length)
