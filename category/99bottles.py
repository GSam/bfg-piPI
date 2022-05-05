#
# Generalizing repeating lyrics in a systematic fashion using meta-heuristics
# for understanding the content.
#
# A formal exegesis on the apperception and interpretation of the content.
#

# 'No more bottles of beer on the wall, no more bottles of beer. Go to the store and buy some more, 99 bottles of beer on the wall.'

# Form 1: Perfectly periodic with a trivial terminating base case. No special processing required in recursion.

data = ''
for x in range(99, 0, -1):
    print("{} bottles of beer on the wall, {} bottles of beer. Take one down and pass it around, {} bottles of beer on the wall.".format(int(x), int(x), int(x-1)))
    data += "{} bottles of beer on the wall, {} bottles of beer. Take one down and pass it around, {} bottles of beer on the wall.".format(int(x), int(x), int(x-1))
    data += "\n"

data = data.strip()
print(repr(data))

from typing import NamedTuple, Any
class Fragment(NamedTuple):
    o: Any
    t: Any

dreamf = Fragment(data, type(data))

class Oneiromancer:
    def __init__(self, fragments=[]):
        self.fragments = fragments[:]

    def add_fragment(self, fragment):
        self.fragments.append(fragment)

    def set_strategy(self):
        pass

    def iterate(self, maximum=100):
        return []

oneiros = Oneiromancer()
oneiros.add_fragment(dreamf)

# Human based tree search, auto
# Known repository of techniques and data
oneiros.set_strategy()

# interpreter = oneiros.interpreter()

for dream in oneiros.iterate(maximum=100):
    # Yields dreams into a dream-space for search
    # Fragment
    # - Dream A
    # - Dream B
    # - Dream C
    pass

import importlib
importlib.import_module('typetest1')

class ValueF(ExpressionF[T]):
    def __init__(self, value: Any):
        self.value = value

    def __str__(self):
        return "ValueF({})".format(str(self.value))


class ConcatF(ExpressionF[T]):
    def __init__(self, e1: T, e2: T):
        self.expression1 = e1
        self.expression2 = e2

    def __str__(self):
        return "ConcatF({}, {})".format(str(self.expression1),
                                      str(self.expression2))

class JoinF(ExpressionF[T]):
    def __init__(self, e1: T, e2: T):
        self.expression1 = e1
        self.expression2 = e2

    def __str__(self):
        return "JoinF({}, {})".format(str(self.expression1),
                                      str(self.expression2))

class ForkF(ExpressionF[T]):
    def __init__(self, e1: T, e2: T):
        self.expression1 = e1
        self.expression2 = e2

    def __str__(self):
        return "ForkF({}, {})".format(str(self.expression1),
                                      str(self.expression2))


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


#print('hello')
print(str(futu(cvexpandF2)(express)(Pure("hello"))))

class ExecutionFunctor(Generic[A, B]):
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

execution = ExecutionFunctor()

import pdb
pdb.set_trace()
# Oneiromancer enters a dream it generates...

#oneiros.learn(dreamspace)

#interpreter = oneiros.interpreter()

#import pdb
#pdb.set_trace()

# Dream fragment [ fragment = (Any), type = ( String | Integer | ...) ]

# Anamorphic expansion into multiple execution paths
#
# Fragment -> Dream A
#          -> Dream B
#
# SimpleOneiromance(Fragment) -> [Dream]
# SimpleOneiromance(Fragment) -> () -> Dream
#
# Oneiromancer.set_strategy(bruteforce)
# Oneiromancer.interpret_fragment(fragment)
#
# Alternatively: StrategyMap(Oneiromancer x Bruteforce)
# Isomorphic relations, treat as lenses.
#
# Bruteforce :: Sigma (i <: I) -> [Option]
#                              -> () -> Option
#
# Strategies can encode state as co-monadic behaviour, alternatively they can
# avoid it altogether. Bruteforce can invoke duplicates in theory.
#
# Oneiromancer.set_strategies(...)
# Oneiromancer.set_metastrategy(...)
#
# Can one dream of oneiromancers? Yes.
# Can one dream of dreams? Yes.
#
# Multiply :: Int x Int -> Int
#          :: Int -> Int -> Int
# Natural  :: 0, 1, 2, 3, 4, ...
#
# Fragment :: [(Int, Int)] = (2, 4), (4, 8), (1, 2), (3, 6)
#
# Dream A = [(In: Int, Out: Int)]
#         = (Relation | Function)
#         = Data-set describles subsampling, partial function
# Dream B = [ n, n + 1, n + 2, ... ] Standard sequence + indexing
# Dream C = 2D drawing, scatterplot, density analysis
#
# TrainGP :: Metaparam -> 
# [(In: [Int ^ R], Out: Int)] -> ( (In: [Int ^ R]) -> Out: Int )
#
# Classification :: Metaparam ->
# [(In: [Any ^ R], Out: (label <: LABEL)] -> ( (In: [Any ^ R]) -> Out: LABEL )
#
# Classification :: <Init> -> [ add_example ] -> <Out>
#
# (In: Any, Out: Any) -*> ( (In: Any) -> Out: Any )
#
# TRUTH is constant based on In vector. If there is inconsistency perhaps it
# should be supplied in, RANDOM classificiation.
#
# ClassifierCOM.add_example(In1, Out1)
# ClassifierCOM.add_example(In2, Out2)
#
# ClassifierCOM.build_classifier() -> ( (In: Any) -> Out: Any )
# Word vectors, addition
#
# * -> * -> *
# * (In) -> * (Out) -> * (In -> Out) -- PAIR
#
# Classifier :: add_example . X ^ R
#            :: build . $
#
# Classifier transformers vs classifiers
#
# DecisionTree . Composite . DecisionTree [ int[], { A, B, C, D } ]
#
# Data is currently in form '99 bottles of beer... \n98 bottles of beer \n...'

# spaCy analysis of grammatical structure

# Lemple-Ziv compression
# Analysis in the form of a matrix
#
# Mutual subtraction of the character matrix:
#
# A B C D 1 E
# A B C D 2 E
# A B C D 3 E
# A B C D 4 E
# A B C D 5 E
#
# Analysis as column vectors
# Insertion of a column to generalize two slightly misshapen matrices
#
# View as a vim edit sequence, macro (automata with finite memory)
# View as a constant form
# Thesaurus replacement
# Annotated list of tuples
# (n, n, n-1)
# (n-1, n-1, n-2)
