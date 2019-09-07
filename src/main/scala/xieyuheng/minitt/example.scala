package xieyuheng.minitt

object example extends App {
  import expDSL._

  // id : (A : U) -> A -> A
  // id = (A, x) => x

  Let("id",
    pi("A" :: U) { "A" ->: "A" },
    fn("A", "x") { "x" })

  // Bool : U
  // Bool = sum {
  //   true
  //   false
  // }

  Let("Bool", U,
    sum(
      "true" -> Trivial,
      "false" -> Trivial))

  // elimBool : (C : Bool -> U) -> C true -> C false -> (b : Bool) -> C b
  // elimBool = (C, h0, h1) => choice {
  //   true => h0
  //   false => h1
  // }

  Let("elimBool",
    pi("C" :: "Bool" ->: U) {
      ("C" $ %("true")) ->:
      ("C" $ %("false")) ->:
      pi("b" :: "Bool") { "C" $ "b" } },
    fn("C", "h0", "h1") {
      choice(
        "true" -> fn("_") { "h0" },
        "false" -> fn("_") { "h1" }) })

  // Nat : U
  // Nat = sum {
  //   zero
  //   succ Nat
  // }

  LetRec("Nat", U,
    sum(
      "zero" -> Trivial,
      "succ" -> "Nat"))

  // List : U -> U
  // List = A => sum {
  //   nil
  //   cons A List A
  // }

  LetRec("List", U ->: U,
    fn("A") {
      sum(
        "nil" -> Trivial,
        "cons" -> "A" * ("List" $ "A")) })

  // natrec : (C : Nat -> U) -> C zero -> ((n : Nat) -> C n -> C (succ n)) ->
  //          (n : Nat) -> C n
  // natrec = (C, a, g) => choice {
  //   zero => a
  //   succ prev => g prev (natrec C a g prev)
  // }

  LetRec("natrec",
    pi("C" :: "Nat" ->: U) {
      ("C" $ %("zero")) ->:
      pi("n" :: "Nat") { ("C" $ "n") ->: ("C" $ %("succ", "n")) } ->:
      pi("n" :: "Nat") { ("C" $ "n") } },
    fn("C", "a", "g") {
      choice(
        "zero" -> fn("_") { "a" },
        "succ" -> fn("prev") { "g" $ "prev" $ ("natrec" $ "C" $ "a" $ "g" $ "prev") }) })

  // add : Nat -> Nat -> Nat
  // add = x => choice {
  //   zero => x
  //   succ prev => succ (add x prev)
  // }

  LetRec("add",
    "Nat" ->: "Nat" ->: "Nat",
    fn("x") {
      choice(
        "zero" -> fn("_") { "x" },
        "succ" -> fn("prev") { %("succ", "add" $ "x" $ "prev") }) })

  // eqNat : Nat -> Nat -> Bool
  // eqNat = choice {
  //   zero => choice {
  //     zero => true
  //     succ _ => false
  //   }
  //   succ x => choice {
  //     zero => false
  //     succ y => eqNat x y
  //   }
  // }

  LetRec("eqNat",
    "Nat" ->: "Nat" ->: "Bool",
    choice(
      "zero" -> fn("_") {
        choice(
          "zero" -> fn("_") { %("true") },
          "succ" -> fn("_") { %("false") }) },
      "succ" -> fn("x") {
        choice(
          "zero" -> fn("_") { %("false") },
          "succ" -> fn("y") { "eqNat" $ "x" $ "y" }) }))

}