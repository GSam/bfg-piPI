from typing import TypeVar, Generic, Callable, Type, Any, List

import readline

T = TypeVar('T')

X, Y = TypeVar('X'), TypeVar('Y')

VOID = {}

# Left = Any, Right = F[Any]
# Effectful computation, output type depends on input
class DreamFragment(Generic[X, Y]):
    def __init__(self, left, right):
        self._left = left
        self._right = right

# Down = 'Input', Up = 'Output'
# Static computation, output type is fixed
class Arrow(Generic[X, Y]):
    def __init__(self, up, down):
        self._up = up
        self._down = down

# Dream has Left = Arrow*, Right = Arrow*
# Dream has Down = Arrow*?[Any], Up = Arrow*[Any]
class Dream(Generic[T]):

    def __init__(self, left, right, up=None, down=None):
        self._left = left
        self._right = right
        self._up = up
        self._down = down

        self.magnitude = 0

    def __len__(self):
        return self.magnitude

    @classmethod
    def bare(cls):
        return cls(None, None)

    @property
    def down(self):
        return self._down

    @down.setter
    def down(self, down):
        self._down = down

    @property
    def up(self):
        return self._up

    @up.setter
    def up(self, up):
        self._up = up

    @property
    def left(self):
        return self._left

    @left.setter
    def left(self, left):
        self._left = left

    @property
    def right(self):
        return self._right

    @right.setter
    def right(self, right):
        self._right = right

# Operators of arrows
class TripleStar:
    pass

class DreamList(Dream[T]):
    # Container initialization
    #def __init__(self, down: T):
    #    self._down = down

    class DreamListMonad:
        pass

    class DreamListComonad:
        pass

    # down: T

    @property
    def up(self):
        return [self._down]


class DreamIdentity(Dream[T]):

    @classmethod
    def arrow(cls, arrow):
        ret = cls(None, None)
        ret.arrow = arrow
        ret.storage = []
        return ret

    @property
    def up(self) -> Dream[T]:
        if self.magnitude:
            self.magnitude -= 1
            return lambda: self.storage.pop()()
        else:
            return lambda: VOID

    @up.setter
    def up(self, up):
        self._up = up

    @property
    def down(self) -> Dream[T]:
        return self._down

    @down.setter
    def down(self, down: Dream[T]):
        self.arrow.down = down
        self.storage.insert(0, self.arrow.up)
        self.magnitude += 1

    class DreamIdentityMonad:
        pass

    class DreamIdentityComonad(Dream[T]):

        @property
        def up(self) -> Dream[T]:
            return self.down

        @up.setter
        def up(self, up):
            self._up = up

        @property
        def down(self) -> Dream[T]:
            return self._down

        @down.setter
        def down(self, down: Dream[T]):
            self._down = down

class DreamConstant(Dream[T]):
    class DreamConstantMonad:
        pass

    class DreamConstantComonad(Dream[T]):

        @property
        def up(self) -> Dream[T]:
            return self._up

        @up.setter
        def up(self, up):
            self._up = up

        @property
        def down(self) -> Dream[T]:
            return None

        @down.setter
        def down(self, down: Dream[T]):
            self._down = None


class LeftExecution:

    def run(arg):
        arrow.down = arg
        return arrow.up

