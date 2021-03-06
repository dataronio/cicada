# cicada

study of programming language design and implementation

> Remember,<br>
> an engineer solve problem,<br>
> an engineer does not want just a religion about how to solve a problem.<br>
> -- Gerry Sussman

### study of parsing techniques

- [partech](src/main/scala/xieyuheng/partech)<br>
  A parser generator, with various parsing techniques
  - [Earley parser](src/main/scala/xieyuheng/partech/parsing_techniques/Earley.scala)<br>
    O(n^3) for context free grammar

### study of scope

- [eopl/lang_let](src/main/scala/xieyuheng/eopl/lang_let)<br>
  scope!
- [eopl/lang_proc](src/main/scala/xieyuheng/eopl/lang_proc)<br>
  lexical scope! (closure)
- [eopl/lang_letrec](src/main/scala/xieyuheng/eopl/lang_letrec)<br>
  recursive scope! (by a trick in the env lookup function)
- [eopl/lang_nameless](src/main/scala/xieyuheng/eopl/lang_nameless)<br>
  nameless scope! (a.k.a. de bruijn index)

### study of assignment

- [eopl/lang_explicit_refs](src/main/scala/xieyuheng/eopl/lang_explicit_refs)<br>
  explicit reference (address)
- [eopl/lang_implicit_refs](src/main/scala/xieyuheng/eopl/lang_implicit_refs)<br>
  distinguish `denoted value` from `expressed value` for mutable variable. <br>
  this is also called call-by-value parameter-passing.
- [eopl/lang_mutable_pairs](src/main/scala/xieyuheng/eopl/lang_mutable_pairs)<br>
  remember Dan's paper "Cons should not evaluate its arguments" ? <br>
  this is preparation for it.
- TODO [eopl/lang_call_by_need](src/main/scala/xieyuheng/eopl/lang_call_by_need)<br>
  remember Dan's paper "Cons should not evaluate its arguments" ? <br>
  this is about it. <br>
  call-by-need is also called lazy-eval.
- TODO [eopl/lang_call_by_reference](src/main/scala/xieyuheng/eopl/lang_call_by_reference)<br>
  caller can mutate reference passed as arg into it. <br>
  this is often seen in imperative language.

### study of lambda calculus with types

- [de_bruijn](src/main/scala/xieyuheng/de_bruijn)<br>
  De Bruijn stype simple type lambda calculus
  - implemented by term rewriting

### study of type inference

- TODO [miniml](src/main/scala/xieyuheng/miniml)<br>
  "A simple applicative language: Mini-ML"
  - by Dominique Clement, Joelle Despeyroux, Thierry Despeyroux, Gilles Kahn

### study of normalization by evaluation (a.k.a. norm-by-eval (a.k.a. NbE))

- [lambda](src/main/scala/xieyuheng/lambda)<br>
  The untyped lambda calculus
- [syst](src/main/scala/xieyuheng/syst)<br>
  Kurt Gödel's system T
  - simply typed lambda calculus with natural number,
- [tartlet](src/main/scala/xieyuheng/tartlet)<br>
  "Checking Dependent Types with Normalization by Evaluation: A Tutorial"
  - this is the little pie, recursion is also not an option here.
  - by David Thrane Christiansen.
    ([the original tutorial](http://davidchristiansen.dk/tutorials/nbe))
- [minitt](src/main/scala/xieyuheng/minitt)<br>
  "A simple type-theoretic language: Mini-TT"
  - by Thierry Coquand, Yoshiki Kinoshita, Bengt Nordström, Makoto Takeyama
  - no termination check, thus when viewed as logic it is unsound.

### study of function composition and stack machine

- [adventure/jojo_untyped](src/main/scala/xieyuheng/adventure/jojo_untyped)<br>
  de bruijn notation implemented by threaded code
- TODO [adventure/jojo_simple](src/main/scala/xieyuheng/adventure/jojo_simple)<br>
  two levels of computations -- `exe` and `cut`
  - `exe` level computation is just computation
  - `cut` level computation is type checking <br>
    thinking about how we implement type checking, <br>
    type checking is compile time computation with good error report, is it not ?
  - `let` is not allowed in `cut` level computation
- TODO [adventure/jojo_dependent](src/main/scala/xieyuheng/adventure/jojo_dependent)<br>
  the same as `simple`, but allow `let` in `cut`

### references

- Parsing Techniques -- A Practical Guide, Second Edition
  - by Dick Grune and Ceriel J.H. Jacobs
- Essentials of Programming Languages (a.k.a. EOPL)
  - by Daniel P. Friedman and Mitchell Wand
- Lambda Calculus with Types
  - by Henk Barendregt, Wil Dekkers and Richard Statman

## Usage

- Build: `sbt stage`
  - test: `./dev t`
  - find executables at `./target/universal/stage/bin/`
- Example code at: [example](example)

## Community

- We enforce C4 as collaboration protocol -- [The C4 RFC](https://rfc.zeromq.org/spec:42/C4)
- [Style Guide](STYLE-GUIDE.md) -- observe the style of existing code and respect it
- [Code of Conduct](CODE-OF-CONDUCT.md)
- [Travis CI](https://travis-ci.org/xieyuheng/cicada)

## License

- [GPLv3](LICENSE)
