from collections import namedtuple


# AdderF[Mono] = lambda A: Or{ 'or': [ Add[And {'and': [ ('first': Mono, 'second': Fn{} )] }], Clear[Mono], Total[Fn] ] }

Or = namedtuple('Or', ['left', 'right'])
Free = namedtuple('Free', ['data'])
Pure = namedtuple('Pure', ['data'])

SELF = ()

from typing import Callable, TypeVar

T = TypeVar('T')

freeF: Callable[[Callable, T], Callable] = lambda f,a: Or(Pure(a),
                       Free(
                           f(SELF)
                           )
                      )


class Monad:
    @staticmethod
    def ret(x):
        return Pure(x)

    @staticmethod
    def bind(x, f):
        def mapper(a):
            if a is Pure:
                return f(a[0])
            else:
                return Free((mapper, [a[0]]))
        print(x, mapper)
        return map(mapper, x)


Monad.bind(Or(Monad.ret(1), Monad.ret(1)), lambda x: 2*x)

freeF(lambda x:x, int)
import pdb
pdb.set_trace()

