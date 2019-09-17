package xieyuheng.tartlet

object check {

  def apply(exp: Exp, ctx: Ctx, t: Val): Either[Err, Exp] =
    apply(exp: Exp, ctx: Ctx, t: Val)

  def check(exp: Exp, ctx: Ctx, t: Val): Either[Err, Exp] = {
    exp match {
      case Quote(sym: String) =>
        // ---------------------
        // ctx :- Quote(sym) <= Atom()
        t match {
          case ValAtom() =>
            Right(
              The(Atom(), exp))
          case _ =>
            Left(Err(
              s"expected ValAtom(), found: ${t}"))
        }
      case Same() =>
        // ctx :- conversion_check(T, from, to)
        // ---------------------
        // ctx :- Same() <= Eqv(T, from, to)
        t match {
          case ValEqv(t_val, from, to) =>
            for {
              _ok <- conversion_check(ctx, t_val, from, to)
            } yield exp
          case _ =>
            Left(Err(
              s"expected ValEqv(t_val, from, to), found: ${t}"))
        }
      case Succ(prev: Exp) =>
        // ctx :- prev <= Nat()
        // ---------------------
        // ctx :- Succ(prev) <= Nat()
        t match {
          case ValNat() =>
            for {
              prev <- check(prev, ctx, t)
            } yield Succ(prev)
          case _ =>
            Left(Err(
              s"expected ValSucc, found: ${t}"))
        }
      case Zero() =>
        // ---------------------
        // ctx :- Zero() <= Nat()
        t match {
          case ValNat() =>
            Right(exp)
          case _ =>
            Left(Err(
              s"expected ValNat(), found: ${t}"))
        }
      case Fn(name: String, body: Exp) =>
        // ctx.ext(x, A) :- body <= R
        // ------------------------------
        // ctx :- Fn(x, body) <= Pi
        t match {
          case ValPi(arg_t, ret_t) => {
            val varVal = TheNeu(arg_t, NeuVar(name))
            for {
              real_ret_t <- ret_t.ap(varVal)
              body <- check(body, ctx.ext(name, Bind(arg_t)), real_ret_t)
            } yield Fn(name, body)
          }
          case _ =>
            Left(Err(
              s"expected ValPi(arg_t, ret_t), found: ${t}"))
        }
      case Sole() =>
        // ---------------------
        // ctx :- Sole() <= Trivial()
        t match {
          case ValTrivial() =>
            Right(exp)
          case _ =>
            Left(Err(
              s"expected ValTrivial(), found: ${t}"))
        }
      case Cons(car: Exp, cdr: Exp) =>
        // ctx :- car <= A
        // ctx.ext(x, A) :- cdr <= D
        // -----------------
        // ctx :- Cons(car, cdr) <= Sigma(x: A, D)
        t match {
          case ValSigma(arg_t, cdr_t) =>
            for {
              car <- check(car, ctx, arg_t)
              car_val <- eval(car, ctx.to_env)
              real_cdr_t <- cdr_t.ap(car_val)
              cdr <- check(cdr, ctx, real_cdr_t)
            } yield Cons(car, cdr)
          case _ =>
            Left(Err(
              s"expected ValSigma(arg_t, cdr_t), found: ${t}"))
        }
      case exp: The => {
        // TODO be sure about this
        for {
          the <- infer(exp, ctx)
          t2 <- eval(the.t, ctx.to_env)
          _ok <- conversion_check(ctx, ValUniverse(), t, t2)
        } yield the.value
      }
      case _ =>
        // ctx :- exp => E
        // ctx :- conversion_check (UNIVERSE, T, E)
        // -----------------
        // ctx :- exp <= T
        for {
          the <- infer(exp, ctx)
          t2 <- eval(the.t, ctx.to_env)
          _ok <- conversion_check(ctx, ValUniverse(), t, t2)
        } yield the.value
    }
  }
}
