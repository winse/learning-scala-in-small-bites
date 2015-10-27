/**
 * An Exp object can be either a variable reference, a lambda term or application
 */
abstract class Exp {
  /**
   * Creates an application term when one term is applied to another.
   * Serves as syntactic sugar for the DSL
   */
  def apply(arg: Exp): Exp = App(this, arg)

  /**
   * Produces a term with subst._1 replaced with subst._2
   */
  def apply(subst: (Symbol, Exp)): Exp;

  /**
   * returns this term as fully call-by-value reduced as possible
   */
  def reduced: Exp;
}

/**
 * A Lam term has a parameter variable and a body expression.
 */
case class Lam(v: Symbol, body: Exp) extends Exp {
  def apply(subst: (Symbol, Exp)) =
    if (v == subst._1)
      this
    else if (Exp.free(body) contains v)
      throw new Exception("Variable capture while substituting " + subst._2 + " for " + subst._1 + " in " + body)
    else
      Lam(v, body(subst))

  def reduced = this

  override def toString = "(lambda (" + v.name + ") " + body + ")"

}

case class Ref(v: Symbol) extends Exp {
  def apply(subst: (Symbol, Exp)) =
    if (subst._1 == v)
      subst._2
    else
      this

  def reduced = this

  override def toString = v.name
}

case class App(f: Exp, e: Exp) extends Exp {

  def apply(subst: (Symbol, Exp)) = App(f(subst), e(subst))

  def reduced = f.reduced match {
    case Lam(v, body) => body(v -> e.reduced)
    case _ => this
  }

  override def toString = "(" + f + " " + e + ")"
}

object Exp {
  def free(e: Exp): Set[Symbol] = e match {
    case Ref(v) => Set(v)
    case Lam(v, body) => free(body) - v
    case App(f, e) => free(f) ++ free(e)
  }

  def λ(v: Symbol)(body: Exp): Exp = Lam(v, body)

  implicit def λ(f: Ref => Exp): Exp = {
    genSymCounter = genSymCounter + 1
    val s = Symbol("$v" + genSymCounter)
    Lam(s, f(Ref(s)))
  }

  /**
   * A counter for generated symbols.
   */
  private var genSymCounter = 0

  abstract class Abstractable {
    def :->(body: Exp): Lam;
  }

  implicit def symbolToAbstractable(s: Symbol): Abstractable =
    new Abstractable {
      def :->(body: Exp): Lam = Lam(s, body)
    }

  implicit def symbolToRef(s: Symbol): Exp = Ref(s)
}

//http://matt.might.net/articles/learning-scala-in-small-bites/
object DemoLambda extends scala.App {

  import Exp._

  val id = λ('x)('x)

  val U = λ(f => f(f))

  val U2 = λ { h => h(h)}

  val U3: Exp = (f: Ref) => f(f)

  val U4 = 'f :-> 'f('f)

  // identity applied to identity
  val idid = U(id)

  // Identity applied to z
  val appz = id('z)

  // f applied to x
  val appfx = 'f('x)

  println(id)
  // Prints:
  // (lambda (x) x)

  println(idid)
  // Prints:
  // ((lambda ($v1) ($v1 $v1)) (lambda (x) x))

  println(appz)
  // Prints:
  // ((lambda (x) x) z)

  println(appfx)
  // Prints:
  // (f x)

  println(free(appz))
  // Prints
  // Set('z)

  println(idid.reduced)
  // Prints
  // (lambda (x) x)
  // real: ((lambda (x) x) (lambda (x) x))

  println(appz.reduced)
  // Prints
  // z

}
