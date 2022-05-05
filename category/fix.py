from arrow import Arrow, X, Y, DreamIdentity

class LambdaArrow(Arrow[X,Y]):
    def __init__(self, lamb):
        self.lamb = lamb

    @property
    def down(self):
        return self._down

    @down.setter
    def down(self, down):
        self._down = down

    @property
    def up(self):
        temp = self._down
        return lambda: self.lamb(temp)

l = LambdaArrow(lambda x: str(x))
l.down = 1
m1 = l.up
l.down = 2
m2 = l.up
print(m1(), m2())

dream = DreamIdentity.arrow(l)

dream.down = 3
dream.down = 2
dream.down = 1

print(len(dream))
print(dream.up(), dream.up(), dream.up(), dream.up())

UP = 0
RIGHT = 1
DOWN = 2
LEFT = 3

DATA = -1

a = { UP : 1, DOWN: 2, LEFT: 1, RIGHT: 1, DATA: int}


# LIST

a1 = { DATA: 1 }
a2 = { DATA: 2 }
a3 = { DATA: 3 }

a1[RIGHT] = a2
a2[LEFT] = a1

a2[RIGHT] = a3
a3[LEFT] = a2

b1 = { DATA: 1 }
b1[RIGHT] = b1
b1[LEFT] = b1

# interpret as comonad
# 1 -> 1 -> 1 -> 1

# COPY, STORE
# IN, OUT

# F[A] -> A
class LeftExecAlgebra:
    def __init__(self, dream):
        self.dream = dream

    @property
    def up(self):
        ret = self.dream[DATA]
        self.dream = self.dream[RIGHT]
        return ret


class RightExecAlgebra:
    def __init__(self, dream):
        self.dream = dream

    @property
    def up(self):
        ret = self.dream[DATA]
        self.dream = self.dream[LEFT]
        return ret

n = LeftExecAlgebra(b1)

import pdb
pdb.set_trace()
