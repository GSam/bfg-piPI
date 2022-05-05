from typing import TypeVar, Generic, Callable, Type, Any

T = TypeVar('T')

class ExpressionF(Generic[T]):
    pass

class ValueF(ExpressionF[T]):
    def __init__(self, value: int):
        self.value = value

class AddF(ExpressionF[T]):
    def __init__(self, e1: T, e2: T):
        self.expression1 = e1
        self.expression2 = e2

class MultF(ExpressionF[T]):
    def __init__(self, e1: T, e2: T):
        self.expression1 = e1
        self.expression2 = e2

A = TypeVar('A')
B = TypeVar('B')

#S = TypeVar('S', Generic)


#class Functor(Generic[S]):
#    def map(self, invar: Callable[[A], B]) -> Callable[[S[A]], S[B]]:
#        pass


class ExpressionFunctor(Generic[A, B]):
    def map(self, invar: Callable[[A], B]) -> Callable[[ExpressionF[A]], ExpressionF[B]]:
        def internal(a: ExpressionF[A]) -> ExpressionF[B]:
            if isinstance(a, ValueF):
                return ValueF(a.value)
            elif isinstance(a, AddF):
                return AddF(invar(a.expression1), invar(a.expression2))
            elif isinstance(a, MultF):
                return MultF(invar(a.expression1), invar(a.expression2))
            else:
                raise Exception("AAAAAA", e)

        return internal

def evalExprF(e: ExpressionF[int]) -> int:
    if isinstance(e, ValueF):
        return e.value
    elif isinstance(e, AddF):
        return e.expression1 + e.expression2
    elif isinstance(e, MultF):
        return e.expression1 * e.expression2
    else:
        raise Exception("AAAAAA", e)

def evalF(e: ExpressionF[str]) -> str:
    if isinstance(e, ValueF):
        return str(e.value)
    elif isinstance(e, AddF):
        return "(" + e.expression1 + " " + e.expression2 + ")"
    elif isinstance(e, MultF):
        return e.expression1 + e.expression2
    else:
        raise Exception("AAAAAA", e)

n: ValueF = ValueF(1)
m = AddF(n, n)
o = MultF(m, n)
o = MultF(o, o)
p = AddF(o, n)

class Fix(Generic[T]):
    def __init__(self, value: T):
        self.value = value

    def unfix(self) -> T:
        return self.value

q = Fix(p)

def fmap(func: Callable[[Any], Any], functor: Any) -> Any:
    return func(functor)

# Functor[T] not Any
def cata(alg: Callable[[Any], T]) -> Callable:
    def cata1(fun: ExpressionFunctor) -> Callable:
        def cata2(e: Fix[Any]) -> T:
            return alg(fun.map(cata(alg)(fun))(e.unfix()))

        # returns (e) -> T
        return cata2

    # returns (fun, e) -> T
    return cata1

express: ExpressionFunctor = ExpressionFunctor()

e: Fix = Fix(MultF(Fix(ValueF(4)), Fix(AddF(Fix(ValueF(1)), Fix(ValueF(9))))))
print(cata(evalExprF)(express)(e))

#reveal_type(q)

