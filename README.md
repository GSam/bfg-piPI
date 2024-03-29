# bfg-piPI
Experiments with rarely-tested or exercised areas of research

Name has been changed to reflect the fact that all the previous ideas required intensely sophisticated type systems (both at bootstrapping stages as well as operationally). The name BFG comes from 'Brain %^&! Garming' as well as (π, Π) - pi, PI - which holds three key pieces of information:

- The Letter P in Greek - impliicit generalized / low-resolution interpreted result (greedy result)
- A standard capitalization implied by pairing (comma implies toUpper functor in this situation by a Yoneda-like principle, type-safe result based on an endofunctor on CHAR)
- Possible internal intervals e.g. all letters between p to P in ASCII or functors belonging to this object (tentative result via objectification)

The interesting property of this is that I can apply said toUpper endofunctor to pi (of type CHAR[]) and yield a roughly equivalent pairing (pi, PI) where these properties are still held - hence a mirrored relation in the subspace of CHAR[] ~ STRING rather than UNICODE[1] ~ SYMBOL.

In this case () implicitly defined a closed quantity like an object interface. π, Π without the braces might mean the infinite sequence π, Π, π, Π, π, Π... which might be better represented canonically as π|Π. The () defines a fixed point e.g. AND (π, Π) SLOT and also a monoid which consists of repeating pairs of <π, Π> which can be unfolded / flatMap into a final concrete sequence. Any array can be mapped into a subsequence of this infinite sequence using various trivial maps.

[?,?,?] -> [ (π, Π), (π, Π), (π, Π) ] CARDINALITY-0

[?,?,?] -> [ π, Π, π, Π, π, Π ] CARDINALITY-1

[?,?,?] -> [ π, Π, π, Π, π, Π, π, Π, π, Π, π, Π] CARDINALITY-N

[?,?,?] -> [ (π, Π, π, Π), (π, Π, π, Π), (π, Π, π, Π)] an example of NEG-CARDINALITY-2 (where the terminals are of order 2 ^ N)


[1,2,3] -> [ (π, Π), (π, Π, π, Π), (π, Π, π, Π, π, Π)... ] - FROM-INT



Core contains the obsolete and never really worked on RPython interpreter:

New ideas (Updated 05/05/22):
- Git state machine automaton - ML-based search and retrieve with high fidelity
- Tree-based concatenative programming: Modifying code with GP in a Scratch-like environment with heavy type hints
- Tree-based imputation algorithms: Learning type hints based on naming convention or 1-shot learning. Usable on JSON or GP trees.
- Auto-generated state automata based on type: Using zipper types to create navigational automata = type differentiation with umbral calculus on (polynomial) RING of types based on (,) and (|)
- Wake sleep algorithms based on: https://github.com/GSam/ec using monad and comonads pairs. Waking = Monad + Comonad and Sleeping = Comonad + Monad. Essentially waking unravels a comonad, while sleeping builds a monad. NOTE: They have to be invertible so it can generate self-opposition.
- Monoidal (or applicative) programming as well as comonoidal (non-zero). Using forgetful functors and their inverse to simplify algebraic objects into their core operations (or core combinators of actions).

- Neural networks to work on the space of Fourier series and algebraic formulae in order to categorize infinite computation as 1/(1 - z) as a lazy expansion (see: Riordan arrays, Motzkin path counting and group theoretic counting for different automata based on Chomsky enumeration theorem may help)
- Using algebraic theory and category theory to condense objects into their most natural group / lattice / geometric / automata / field etc.
- PNG/Bitmap based memory encoding with multi-layer redundancy - RGB values used, but also high level contours / heatmaps generated to reinforce memory (RAM-like memory)
- Neural networks and manipulating images in more 'compact' forms: https://github.com/GSam/FourierFromSVG Got the idea from watching 3Blue1Brown to decompose images in a different way. This includes PixelArrays: https://github.com/GSam/bfg-piPI/blob/master/External%20Slides/PixelArrays--20161026--MIT.pdf
- Abusing colour theory to map objects into semantically meaningful ways and as a base unit for a list of mutating GP objects.
- 'Continuous' or non-discrete optics used for manipulating symmetric objects i.e. groups

Old ideas:
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
