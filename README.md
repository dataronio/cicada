# cicada

A dependently typed language and proof assistant.
- with **fulfilling type system**, in which partially fulfilled data can be used as type.
- compile to human readable javascript, and maintain all type information at runtime (do not erase type information).
- literal programming support by markdown, to help proof writers write proofs for human.

## Also Contains

- [partech](https://github.com/xieyuheng/cicada/tree/master/src/main/scala/xieyuheng/partech):
  A parser generator with different parsing techniques.
- [lambda](https://github.com/xieyuheng/cicada/tree/master/src/main/scala/xieyuheng/lambda):
  The untyped lambda calculus with norm-by-eval (normalization by evaluation).
- [syst](https://github.com/xieyuheng/cicada/tree/master/src/main/scala/xieyuheng/syst):
  Kurt Gödel's System T (curry style simply typed lambda calculus with natural number)
  with typed norm-by-eval, (a step toward tartlet and pie).
- [tartlet](https://github.com/xieyuheng/cicada/tree/master/src/main/scala/xieyuheng/tartlet)
  The tutorial "Checking Dependent Types with Normalization by Evaluation: A Tutorial",
  by David Thrane Christiansen.
  ([the original tutorial](http://davidchristiansen.dk/tutorials/nbe))
  - subrecursive, recursion is not an option.
- [minitt](https://github.com/xieyuheng/cicada/tree/master/src/main/scala/xieyuheng/minitt):
  The dependently typed Mini-TT language
  described in paper: "A simple type-theoretic language: Mini-TT",
  by Thierry Coquand, Yoshiki Kinoshita, Bengt Nordström, Makoto Takeyama.
  - no termination check, thus when viewed as logic it is unsound.

## Usage

- Pre-compiled binaries at: [cicada-release](https://github.com/xieyuheng/cicada-release)
- Example code at: [example](https://github.com/xieyuheng/cicada/tree/master/example)

## Community

- We enforce C4 as collaboration protocol -- [The C4 RFC](https://rfc.zeromq.org/spec:42/C4)
- [Style Guide](STYLE-GUIDE.md) -- observe the style of existing code and respect it
- [Code of Conduct](CODE-OF-CONDUCT.md)
- [Travis CI](https://travis-ci.org/xieyuheng/cicada)

## License

- [GPLv3](LICENSE)
