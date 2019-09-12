package xieyuheng.minitt

import xieyuheng.partech._
import xieyuheng.partech.ruleDSL._
import xieyuheng.partech.predefined._

object grammar {

  val lexer = Lexer.default

  def preserved_identifiers: Set[String] = Set(
    "let", "letrec",
    "sum", "match",
    "trivial",
    "univ",
  )

  def identifier: WordPred = WordPred(
    "identifier", { case word =>
      if (preserved_identifiers.contains(word)) {
        false
      } else {
        word.headOption match {
          case Some(char) =>
            val head_set = lower_case_char_set ++ upper_case_char_set + '_'
            val tail_set = head_set ++ digit_char_set
            head_set.contains(char) && wordInCharSet(tail_set)(word.tail)
          case None => false
        }
      }
    })

  def module = Rule(
    "module", Map(
      "module" -> List(non_empty_list(top)),
    ))

  def module_matcher = Tree.matcher[Module](
    "module", Map(
      "module" -> { case List(top_list) =>
        var module = Module()
        module.top_list = non_empty_list_matcher(top_matcher)(top_list)
        module
      },
    ))

  def top = Rule(
    "top", Map(
      "decl" -> List(decl),
      "eval" -> List("eval", "!", exp),
      "eq" -> List("eq", "!", exp, exp),
      "not_eq" -> List("not_eq", "!", exp, exp),
    ))

  def top_matcher = Tree.matcher[Top](
    "top", Map(
      "decl" -> { case List(decl) => TopDecl(decl_matcher(decl)) },
      "eval" -> { case List(_, _, exp) => TopEval(exp_matcher(exp)) },
      "eq" -> { case List(_, _, x, y) => TopEq(exp_matcher(x), exp_matcher(y)) },
      "not_eq" -> { case List(_, _, x, y) => TopNotEq(exp_matcher(x), exp_matcher(y)) },
    ))

  def decl = Rule(
    "decl", Map(
      "let" -> List("let", pat, ":", exp, "=", exp),
      "letrec" -> List("letrec", pat, ":", exp, "=", exp),
    ))

  def decl_matcher = Tree.matcher[Decl](
    "decl", Map(
      "let" -> { case List(_, p, _, e, _, t) =>
        Let(pat_matcher(p), exp_matcher(e), exp_matcher(t)) },
      "letrec" -> { case List(_, p, _, e, _, t) =>
        Letrec(pat_matcher(p), exp_matcher(e), exp_matcher(t)) },
    ))

  def exp: Rule = Rule(
    "exp", Map(
      "rator" -> List(rator),
      "non_rator" -> List(non_rator),
    ))

  def exp_matcher: Tree => Exp = Tree.matcher[Exp](
    "exp", Map(
      "rator" -> { case List(rator) => rator_matcher(rator) },
      "non_rator" -> { case List(non_rator) => non_rator_matcher(non_rator) },
    ))

  def rator: Rule = Rule(
    "rator", Map(
      "var" -> List(identifier),
      "ap" ->
        List(rator, "(", non_empty_list(exp_comma), ")"),
      "ap_one_without_comma" ->
        List(rator, "(", exp, ")"),
      "ap_without_last_comma" ->
        List(rator, "(", non_empty_list(exp_comma), exp, ")"),
      "car" -> List(exp, ".", "car"),
      "cdr" -> List(exp, ".", "cdr"),
      "match" -> List("match", "{", non_empty_list(mat_clause), "}"),
    ))

  def rator_matcher: Tree => Exp = Tree.matcher[Exp](
    "rator", Map(
      "var" -> { case List(Leaf(name)) => Var(name) },
      "ap" -> { case List(rator, _, exp_comma_list, _) =>
        non_empty_list_matcher(exp_comma_matcher)(exp_comma_list)
          .foldLeft(rator_matcher(rator)) { case (fn, arg) => Ap(fn, arg) } },
      "ap_one_without_comma" -> { case List(rator, _, exp, _) =>
        Ap(rator_matcher(rator), exp_matcher(exp)) },
      "ap_without_last_comma" -> { case List(rator, _, exp_comma_list, exp, _) =>
        val fn = non_empty_list_matcher(exp_comma_matcher)(exp_comma_list)
          .foldLeft(rator_matcher(rator)) { case (fn, arg) => Ap(fn, arg) }
        Ap(fn, exp_matcher(exp)) },
      "car" -> { case List(exp, _, _) => Car(exp_matcher(exp)) },
      "cdr" -> { case List(exp, _, _) => Cdr(exp_matcher(exp)) },
      "match" -> { case List(_, _, mat_clause_list, _) =>
        Mat(non_empty_list_matcher(mat_clause_matcher)(mat_clause_list).toMap) },
    ))

  def multi_pi_arg: Rule = Rule(
    "multi_pi_arg", Map(
      "arg" -> List(exp),
      "arg_comma" -> List(exp, ","),
      "arg_t" -> List(pat, ":", exp),
      "arg_t_comma" -> List(pat, ":", exp, ","),
    ))

  def multi_pi_arg_matcher = Tree.matcher[(Pat, Exp)](
    "multi_pi_arg", Map(
      "arg" -> { case List(exp) =>
        (PatSole(), exp_matcher(exp)) },
      "arg_comma" -> { case List(exp, _) =>
        (PatSole(), exp_matcher(exp)) },
      "arg_t" -> { case List(pat, _, exp) =>
        (pat_matcher(pat), exp_matcher(exp)) },
      "arg_t_comma" -> { case List(pat, _, exp, _) =>
        (pat_matcher(pat), exp_matcher(exp)) },
    ))


  def multi_fn_arg: Rule = Rule(
    "multi_fn_arg", Map(
      "arg" -> List(pat),
      "arg_comma" -> List(pat, ","),
    ))

  def multi_fn_arg_matcher = Tree.matcher[Pat](
    "multi_fn_arg", Map(
      "arg" -> { case List(pat) =>
        pat_matcher(pat) },
      "arg_comma" -> { case List(pat, _) =>
        pat_matcher(pat) },
    ))

  def non_rator: Rule = Rule(
    "non_rator", Map(
      "pi" -> List("(", pat, ":", exp, ")", "-", ">", exp),
      "multi_pi" -> List("(", non_empty_list(multi_pi_arg), ")", "-", ">", exp),
      "fn" -> List(pat, "=", ">", exp),
      "multi_fn" -> List("(", non_empty_list(multi_fn_arg), ")", "=", ">", exp),
      "cons" ->
        List("[", non_empty_list(exp_comma), "]"),
      "cons_one_without_comma" ->
        List("[", exp, "]"),
      "cons_without_last_comma" ->
        List("[", non_empty_list(exp_comma), exp, "]"),
      "sigma" -> List("(", pat, ":", exp, ")", "*", "*", exp),
      "data" -> List(identifier, exp),
      "sum" -> List("sum", "{", non_empty_list(sum_clause), "}"),
      "sole" -> List("[", "]"),
      "trivial" -> List("trivial"),
      "univ" -> List("univ"),
    ))

  def non_rator_matcher: Tree => Exp = Tree.matcher[Exp](
    "non_rator", Map(
      "pi" -> { case List(_, pat, _, arg_t, _, _, _, t) =>
        Pi(pat_matcher(pat), exp_matcher(arg_t), exp_matcher(t)) },
      "multi_pi" -> { case List(_, multi_pi_arg_list, _, _, _, t) =>
        var exp = exp_matcher(t)
        non_empty_list_matcher(multi_pi_arg_matcher)(multi_pi_arg_list)
          .reverse.foreach { case (pat, arg_t) =>
            exp = Pi(pat, arg_t, exp)
          }
        exp },
      "fn" -> { case List(pat, _, _, t) =>
        Fn(pat_matcher(pat), exp_matcher(t)) },
      "multi_fn" -> { case List(_, multi_fn_arg_list, _, _, _, t) =>
        var exp = exp_matcher(t)
        non_empty_list_matcher(multi_fn_arg_matcher)(multi_fn_arg_list)
          .reverse.foreach { case pat =>
            exp = Fn(pat, exp)
          }
        exp },
      "cons" -> { case List(_, exp_comma_list, _) =>
        val list = non_empty_list_matcher(exp_comma_matcher)(exp_comma_list)
        list.init.foldRight(list.last) { case (head, tail) =>
          Cons(head, tail) } },
      "cons_one_without_comma" -> { case List(_, exp, _) =>
        exp_matcher(exp) },
      "cons_without_last_comma" -> { case List(_, exp_comma_list, exp, _) =>
        val list = non_empty_list_matcher(exp_comma_matcher)(exp_comma_list)
        list.foldRight(exp_matcher(exp)) { case (head, tail) =>
          Cons(head, tail) } },
      "sigma" -> { case List(_, pat, _, arg_t, _, _, _, t) =>
        Sigma(pat_matcher(pat), exp_matcher(arg_t), exp_matcher(t)) },
      "data" -> { case List(Leaf(tag), exp) =>
        Data(tag, exp_matcher(exp)) },
      "sum" -> { case List(_, _, sum_clause_list, _) =>
        Sum(non_empty_list_matcher(sum_clause_matcher)(sum_clause_list).toMap) },
      "sole" -> { case _ => Sole() },
      "trivial" -> { case _ => Trivial() },
      "univ" -> { case _ => Univ() },
    ))

  def exp_comma = Rule(
    "exp_comma", Map(
      "exp_comma" -> List(exp, ","),
    ))

  def exp_comma_matcher = Tree.matcher[Exp](
    "exp_comma", Map(
      "exp_comma" -> { case List(exp, _) => exp_matcher(exp) },
    ))

  def sum_clause = Rule(
    "sum_clause", Map(
      "sum_clause" -> List(identifier, exp, ";"),
    ))

  def sum_clause_matcher: Tree => (String, Exp) = Tree.matcher[(String, Exp)](
    "sum_clause", Map(
      "sum_clause" -> { case List(Leaf(name), t, _) =>
        val exp = exp_matcher(t)
        if (exp == Sole()) {
          (name, Trivial())
        } else {
          (name, exp)
        }
      },
    ))

  def mat_clause = Rule(
    "mat_clause", Map(
      "mat_clause" -> List(identifier, exp, ";"),
    ))

  def mat_clause_matcher: Tree => (String, Exp) = Tree.matcher[(String, Exp)](
    "mat_clause", Map(
      "mat_clause" -> { case List(Leaf(name), exp, _) =>
        (name, exp_matcher(exp)) },
    ))

  def pat: Rule = Rule(
    "pat", Map(
      "var" -> List(identifier),
      "cons" ->
        List("[", non_empty_list(pat_comma), "]"),
      "cons_one_without_comma" ->
        List("[", pat, "]"),
      "cons_without_last_comma" ->
        List("[", non_empty_list(pat_comma), pat, "]"),
      "sole" -> List("[", "]"),
    ))

  def pat_matcher: Tree => Pat = Tree.matcher[Pat](
    "pat", Map(
      "var" -> { case List(Leaf(name)) => PatVar(name) },
      "cons" -> { case List(_, pat_comma_list, _) =>
        val list = non_empty_list_matcher(pat_comma_matcher)(pat_comma_list)
        list.init.foldRight(list.last) { case (head, tail) =>
          PatCons(head, tail) } },
      "cons_one_without_comma" -> { case List(_, pat, _) =>
        pat_matcher(pat) },
      "cons_without_last_comma" -> { case List(_, pat_comma_list, pat, _) =>
        val list = non_empty_list_matcher(pat_comma_matcher)(pat_comma_list)
        list.foldRight(pat_matcher(pat)) { case (head, tail) =>
          PatCons(head, tail) } },
      "sole" -> { case _ => PatSole() },
    ))

  def pat_comma = Rule(
    "pat_comma", Map(
      "pat_comma" -> List(pat, ","),
    ))

  def pat_comma_matcher = Tree.matcher[Pat](
    "pat_comma", Map(
      "pat_comma" -> { case List(pat, _) => pat_matcher(pat) },
    ))
}