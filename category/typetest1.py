from typing import TypeVar, Generic, Callable, Type, Any

import readline

T = TypeVar('T')

class ExpressionF(Generic[T]):
    pass

class ValueF(ExpressionF[T]):
    def __init__(self, value: int):
        self.value = value

    def __str__(self):
        return "ValueF({})".format(str(self.value))


class AddF(ExpressionF[T]):
    def __init__(self, e1: T, e2: T):
        self.expression1 = e1
        self.expression2 = e2

    def __str__(self):
        return "AddF({}, {})".format(str(self.expression1),
                                     str(self.expression2))

class MultF(ExpressionF[T]):
    def __init__(self, e1: T, e2: T):
        self.expression1 = e1
        self.expression2 = e2

    def __str__(self):
        return "MultF({}, {})".format(str(self.expression1),
                                      str(self.expression2))

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

import pdb
i = False
def evalADMINF(e: ExpressionF[int]) -> int:
    if isinstance(e, ValueF):
        global i
        print(e.value)
        namespace = {'answer': e.value, 'question': e}
        if not i:
            pdb.set_trace()
            i = True
        print(readline.get_history_item(readline.get_current_history_length()-1))
        exec(readline.get_history_item(readline.get_current_history_length()-1), namespace)
        print(namespace['answer'])
        return int(namespace['answer'])
    elif isinstance(e, AddF):
        answer = e.expression1 + e.expression2
        return answer
    elif isinstance(e, MultF):
        answer = e.expression1 * e.expression2
        return answer
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

    def __str__(self):
        return "Fix({})".format(str(self.value))

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

def expandExprF(e: int) -> ExpressionF[int]:
    if e == 1:
        return ValueF(1)
    elif e > 1:
        return AddF(e - 1, 1)
    else:
        raise Exception("AAAAAA", e)


def expandF(string: str) -> ExpressionF[str]:
    e = len(string)
    if e == 1:
        return ValueF(string)
    elif e > 1:
        return AddF(string[:1], string[1:])
    else:
        raise Exception("AAAAAA", e)

#reveal_type(q)

# unfold :: (s -> Maybe (a, s)) -> s -> [a]

def ana(coalg: Callable[[T], Any]) -> Callable:
    def ana1(fun: ExpressionFunctor) -> Callable:
        def ana2(e: Any) -> Fix[Any]:
            return Fix(fun.map(ana(coalg)(fun))(coalg(e)))

        # returns (e) -> T
        return ana2

    # returns (fun, e) -> T
    return ana1

#print(str(ana(expandF)(express)("hellomyname")))
#print(str(ana(expandExprF)(express)(40)))


def hylo(alg: Callable[[Any], T], coalg: Callable[[T], Any], fun: ExpressionFunctor, seed: Any) -> Any:
    buildup = coalg(seed)
    mapped = fun.map(lambda x: hylo(alg, coalg, fun, x))(buildup)
    flattened = alg(mapped)
    return flattened

def hylo1(alg: Callable[[Any], T], coalg: Callable[[T], Any], fun: ExpressionFunctor, seed: Any) -> Any:
    return cata(alg)(fun)(ana(coalg)(fun)(seed))

print(hylo1(evalExprF, expandExprF, express, 40))

from collections.abc import Iterable

class Free(Generic[T]):
    def __init__(self, value: T):
        self.value = value

    if isinstance(value, Iterable):
        pass

    def __str__(self):
        return "Free({})".format(str(self.value))

    def map(self, invar: Callable[[A], B]) -> Callable[[ExpressionF[A]], ExpressionF[B]]:
        def internal(a: ExpressionF[A]) -> ExpressionF[B]:
            if isinstance(a, Pure):
                return Pure(a.value)
            elif isinstance(a, Impure):
                return Impure(invar(a.value))
            else:
                raise Exception("AAAAAA", e)

        return internal

class Pure(Free):
    pass

class Impure(Free):
    pass

# A -> F(Free) (wrappable / sum type)
def cvexpandF(string: str) -> ExpressionF[Free[str]]:
    e = len(string)
    if e == 1:
        return ValueF(Pure(string))
    elif e > 1:
        return AddF(Impure(MultF(Pure(string[:1]),
                                 Pure("1"))),
                    Pure(string[1:]))
    else:
        raise Exception("AAAAAA", e)

def cvexpandF2(string: str) -> ExpressionF[Free[str]]:
    e = len(string)
    if e == 1:
        return ValueF(Pure(string))
    elif e > 1:
        return AddF(Impure(MultF(Impure(ValueF(Pure(string[:1]))),
                                 Pure("1"))),
                    Pure(string[1:]))
    else:
        raise Exception("AAAAAA", e)


def futu(cvcoalg: Callable[[T], Any]) -> Callable:
    def futu1(fun: ExpressionFunctor) -> Callable:
        def futu2(e: Free) -> Fix[Any]:
            #print(e)
            if isinstance(e, Pure):
                #print(e, 'Pure', type(e))
                worker = futu(cvcoalg)(fun)
                unravel = cvcoalg(e.value)
                return Fix(fun.map(worker)(unravel))
            else:
                #print(e, 'Impure', type(e))
                # Impure(MultF(Impure(...
                # Impure.map(...)
                # e.value = MultF
                # fun.map(futu(cvcoalg)(fun))(e.value)
                return Fix(fun.map(futu(cvcoalg)(fun))(e.value))
                #return Fix(e.map(fun.map(futu(cvcoalg)(fun)))(e).value)

        # returns (e) -> T
        return futu2

    # returns (fun, e) -> T
    return futu1

#print('hello')
print(str(futu(cvexpandF2)(express)(Pure("hello"))))

