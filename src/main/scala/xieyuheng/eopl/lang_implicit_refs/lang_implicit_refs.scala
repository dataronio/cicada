package xieyuheng.eopl.lang_implicit_refs

import pretty._

import xieyuheng.util.mini_interpreter
import xieyuheng.util.err._

import xieyuheng.partech.Parser

object lang_implicit_refs extends mini_interpreter(
  "lang_implicit_refs", "0.0.1", { case code =>
    Parser(grammar.lexer, grammar.exp).parse(code) match {
      case Right(tree) =>
        val env = EnvEmpty()
        val store = Store()
        val exp = grammar.exp_matcher(tree)
        eval.eval(exp, env, store) match {
          case Right((new_store, value)) =>
            println(s">>> ${pretty_exp(exp)}")
            println(s"=== ${pretty_val(value)}")
          case Left(err) =>
            println(s"${err.msg}")
            System.exit(1)
        }
      case Left(error) =>
        println(s"[parse_error] ${error.msg}")
        System.exit(1)
    }
  }
)
