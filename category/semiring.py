from abc import abstractmethod
from oslash import Functor
import pyrsistent
from pyrsistent import plist, field

class Semiring(pyrsistent.PRecord):
    monoid = plist()

    @abstractmethod
    def map(self, f):
        return None

class Or(Semiring):
    left = field(type=Semiring)
    right = field(type=Semiring)

    def fold(self):
        if isinstance(left, Or):
            pass
        if isinstance(right, Or):
            pass

class And(Semiring):
    first = field(type=Semiring)
    second = field(type=Semiring)

class TypeVar(Semiring):
    pass

# ()
class SemiOne(Semiring):
    pass

# VOID
class SemiZero(Semiring):
    pass


Or(left=1)
import pdb
pdb.set_trace()
