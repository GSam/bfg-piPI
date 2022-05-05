# bfg-pypy
Experiments

Core contains the main RPython interpreter

- cppyy cling based interop
- bloom filters
- embedded Q-Learning
- semantic similarity tree analysis
- semantic word-vectors for selecting useful functions
- type unification pipelines (map-reduce), with type hints
- (syntactic) levenstein distance and automata
- compile internal automata as C program and use AFL
- compile internal automata as C program and use static analysis (e.g. dead code)
- type unification pipelines using markov probabilities
- embedding of fitness function evaluation into interpreter via BF translation (benefit of hotter code?)

Updated 05/05/22:
- Git state machine automaton - ML-based search and retrieve with high fidelity
- PNG/Bitmap based memory encoding with multi-layer redundancy - RGB values used, but also high level contours generated to reinforce memory (RAM-like memory)
- Tree-based concatenative programming: Modifying code with GP in a Scratch-like environment with heavy type hints
- Tree-based imputation algorithms: Learning type hints based on naming convention or 1-shot learning. Usable on JSON or GP trees.
- Auto-generated state automata based on type: Using zipper types to create navigational automata = type differentiation with umbral calculus on RING of types based on (,) and (|)
- Wake sleep algorithms based on: https://github.com/GSam/ec using monad and comonads pairs. Waking = Monad + Comonad and Sleeping = Comonad + Monad. Essentially waking unravels a comonad, while sleeping builds a monad. NOTE: They have to be invertible so it can generate self-opposition.
- Monoidal (or applicative) programming as well as comonoidal (non-zero). Using forgetful functors and their inverse to simplify algebraic objects into their core operations (or core combinators of actions).
- Using algebraic theory and category theory to condense objects into their most natural group / lattice / geometric / automata / field etc.

- Neural networks and manipulating images in more 'compact' forms: https://github.com/GSam/FourierFromSVG Got the idea from watching 3Blue1Brown to decompose images in a different way. This includes PixelArrays: https://github.com/GSam/bfg-piPI/blob/master/External%20Slides/PixelArrays--20161026--MIT.pdf
- Abusing colour theory to map objects into semantically meaningful ways and as a base unit for a list of mutating GP objects.
