package xieyuheng.syst

import pretty._
import readback._
import check._

case class Module() {

  var top_list: List[Top] = List()

  def add_top(top: Top): Module = {
    top_list = top_list :+ top
    this
  }

  def declare(decl: Decl): Unit = {
    add_top(TopDecl(decl))
  }

  def env: Env = {
    var env: Env = Env()
    top_list.foreach {
      case TopDecl(DeclLet(name, t, e)) =>
        env = env.ext(name, eval(e, env))
      case _ => {}
    }
    env
  }

  def ctx: Ctx = {
    var ctx: Ctx = Ctx()
    top_list.foreach {
      case TopDecl(DeclLet(name, t, e)) =>
        ctx = ctx.ext(name, t)
      case _ => {}
    }
    ctx
  }

  def type_check(): Unit = {
    var env: Env = Env()
    var ctx: Ctx = Ctx()
    top_list.foreach {
      case TopDecl(DeclLet(name, t, e)) =>
        check(e, ctx, t) match {
          case Right(()) =>
            ctx = ctx.ext(name, t)
            env = env.ext(name, eval(e, env))
          case Left(err) =>
            println(s"${err.msg}")
            throw new Exception()
        }
      case TopEval(exp) =>
        eval_print(exp)
      case TopEq(e1, e2) =>
        assert_eq(e1)(e2)
      case TopNotEq(e1, e2) =>
        assert_not_eq(e1)(e2)
      case _ => {}
    }
  }

  def run(): Unit = {
    var env: Env = Env()
    top_list.foreach {
      case TopDecl(DeclLet(name, t, e)) =>
        env = env.ext(name, eval(e, env))
      case TopEval(exp) =>
        eval_print(exp)
      case TopEq(e1, e2) =>
        assert_eq(e1)(e2)
      case TopNotEq(e1, e2) =>
        assert_not_eq(e1)(e2)
      case _ => {}
    }
  }

  def assert_not_eq(e1: Exp)(e2: Exp): Unit = {
    val v1 = eval(e1, env)
    val v2 = eval(e2, env)
    // val n1 = readback_val(v1, Set())
    // val n2 = readback_val(v2, Set())
    if (v1 == v2) {
      println(s"[assertion fail]")
      println(s"the following two expressions are asserted to be not equal")
      println(s">>> ${pretty_exp(e1)}")
      println(s"=== ${pretty_val(v1)}")
      // println(s"=== ${pretty_exp(n1)}")
      println(s">>> ${pretty_exp(e2)}")
      println(s"=== ${pretty_val(v2)}")
      // println(s"=== ${pretty_exp(n2)}")
      throw new Exception()
    }
  }

  def assert_eq(e1: Exp)(e2: Exp): Unit = {
    val v1 = eval(e1, env)
    val v2 = eval(e2, env)
    // val n1 = readback_val(v1, Set())
    // val n2 = readback_val(v2, Set())
    if (v1 != v2) {
      println(s"[assertion fail]")
      println(s"the following two expressions are asserted to be equal")
      println(s">>> ${pretty_exp(e1)}")
      println(s"=== ${pretty_val(v1)}")
      // println(s"=== ${pretty_exp(n1)}")
      println(s">>> ${pretty_exp(e2)}")
      println(s"=== ${pretty_val(v2)}")
      // println(s"=== ${pretty_exp(n2)}")
      throw new Exception()
    }
  }

  def eval_print(exp: Exp): Unit = {
    val value = eval(exp, env)
    // val norm = readback_val(value, Set())
    println(s">>> ${pretty_exp(exp)}")
    println(s"=== ${pretty_val(value)}")
    // println(s"=== ${pretty_exp(norm)}")
    println()
  }

}


// package xieyuheng.syst

// case class Module(
//   var env: Env = Env(),
//   var ctx: Ctx = Ctx(),
// ) {
//   def claim(name: String, t: Type): Module = {
//     ctx = ctx.ext(name, t)
//     this
//   }

//   def define(name: String, exp: Exp): Module = {
//     ctx.lookup_type(name) match {
//       case Some(t) =>
//         exp.check(ctx, t) match {
//           case Right(()) =>
//             for {
//               value <- exp.eval(env)
//             } yield {
//               env = env.ext(name, value)
//             }
//           case Left(Err(errorMsg)) =>
//             println(s"type check fail for name: ${name}, error: ${errorMsg}")
//         }
//       case None =>
//         println(s"name: ${name} is not claimed before define")
//     }
//     this
//   }

//   def run(exp: Exp): Either[Err, Exp] = {
//     val result = for {
//       t <- exp.infer(ctx)
//       value <- exp.eval(env)
//       norm <- value.readback_val(ctx.names, t)
//     } yield norm

//     result match {
//       case Right(exp) =>
//         println(s"exp: ${exp}")
//       case Left(Err(msg)) =>
//         println(s"error: ${msg}")
//     }

//     result
//   }
// }