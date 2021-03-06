package xieyuheng.adventure.jojo_untyped

import pretty._

import xieyuheng.util.err._

import scala.annotation.tailrec

object exe {

  def frame_empty: Frame = {
    Frame(0, List(), EnvEmpty())
  }

  def run(ds: Ds, rs: Rs): Either[Err, Ds] = {
    run_with_limit(ds, rs, 0) match {
      case Right((ds, rs)) => Right(ds)
      case Left(err) => Left(err)
    }
  }

  def run_jo_list(ds: Ds, rs: Rs, list: List[Jo]): Either[Err, (Ds, Rs)] = {
    val limit = rs.length
    val frame = Frame(0, list, EnvEmpty())
    run_with_limit(ds, rs.push(frame), limit)
  }

  @tailrec
  def run_with_limit(ds: Ds, rs: Rs, limit: Int): Either[Err, (Ds, Rs)] = {
    if (rs.length > limit) {
      step(ds, rs) match {
        case Right((ds, rs)) =>
          run_with_limit(ds, rs, limit)
        case Left(err) =>
          Left(err)
      }
    } else {
      Right(ds, rs)
    }
  }

  def step(ds: Ds, rs: Rs): Either[Err, (Ds, Rs)] = {
    rs.toc() match {
      case Some(frame) =>
        if (frame.index == frame.list.length) {
          Right(ds, rs.drop())
        } else {
          val jo = frame.list(frame.index)
          exe(ds, rs.next(), frame.env, jo)
        }
      case None =>
        Left(Err(
          s"[step fail]\n" ++
            s"stack underflow\n"
        ))
    }
  }

  def exe(ds: Ds, rs: Rs, env: Env, jo: Jo): Either[Err, (Ds, Rs)] = {
    jo match {
      case Var(name: String) =>
        env.lookup_val(name) match {
          case Some(value) =>
            ap(ds, rs, env, value)
          case None =>
            Left(Err(
              s"[exe fail]\n" ++
                s"undefined name: ${name}\n"
            ))
        }
      case Let(name: String) =>
        ds.toc() match {
          case Some(value) =>
            Right(ds.drop(), rs.toc_ext(name, value))
          case None =>
            Left(Err(
              s"[exe fail]\n" ++
                s"stack underflow\n"
            ))
        }
      case JoJo(list: List[Jo]) =>
        Right(ds.push(ValJoJo(list, env)), rs)
      case Define(name: String, jojo: JoJo) =>
        Right(ds, rs.toc_ext(name, ValJoJo(jojo.list, env)))
      case Str(str: String) =>
        Right(ds.push(ValStr(str)), rs)
      case Cons() =>
        ds.toc() match {
          case Some(car) =>
            ds.drop().toc() match {
              case Some(cdr) =>
                Right(ds.drop().drop().push(ValCons(car, cdr)), rs)
              case None =>
                Left(Err(
                  s"[exe fail]\n" ++
                    s"stack underflow\n"
                ))
            }
          case None =>
            Left(Err(
              s"[exe fail]\n" ++
                s"stack underflow\n"
            ))
        }
      case Car() =>
        ds.toc() match {
          case Some(ValCons(car, cdr)) =>
            Right(ds.drop().push(car), rs)
          case Some(value) =>
            Left(Err(
              s"[exe fail]\n" ++
                s"type mismatch match, excepting cons\n" ++
                s"value: ${pretty_val(value)}\n"
            ))
          case None =>
            Left(Err(
              s"[exe fail]\n" ++
                s"stack underflow\n"
            ))
        }
      case Cdr() =>
        ds.toc() match {
          case Some(ValCons(car, cdr)) =>
            Right(ds.drop().push(cdr), rs)
          case Some(value) =>
            Left(Err(
              s"[exe fail]\n" ++
                s"type mismatch match, excepting cons\n" ++
                s"value: ${pretty_val(value)}\n"
            ))
          case None =>
            Left(Err(
              s"[exe fail]\n" ++
                s"stack underflow\n"
            ))
        }
    }
  }

  def ap(ds: Ds, rs: Rs, env: Env, value: Val): Either[Err, (Ds, Rs)] = {
    value match {
      case jojo: ValJoJo =>
        Right(ds, rs.push(Frame(0, jojo.list, jojo.env)))
      case ValStr(str) =>
        Right(ds.push(ValStr(str)), rs)
      case ValCons(car, cdr) =>
        Right(ds.push(ValCons(car, cdr)), rs)
    }
  }

}
