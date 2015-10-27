/*
If an object extends Application, then the body of the object is effectively a script.
 */
object DemoApplication extends scala.App {

  /*
  Warning (courtesy of Dean Wampler):

  Extending Application runs the entire program in the constructor for the object,
  which prevents the JVM from performing JIT optimizations.

  For large applications, use a main() method instead of extending Application.
   */

  println("Hello World!")

}

object DemoApplication2 {
  def main(args: Array[String]) {
    println("Hello World!")
  }
}

/*
Demo: A ligthweight unit-testing framework.
Site: http://matt.might.net
      http://www.ucombinator.org

A home-grown unit-testing library, demostrating the utility of by-name parameters.
 */

/**
Thrown when checkThat fails.
  */
case class CheckException extends RuntimeException

/**
Thrown when a check fails and there is information as to why.
  */
case class ReasonedCheckException(reason: String) extends RuntimeException

/**
The Check object contains methods for testing.
  */
object Check {

  /**
   * Determines whether checks happen at runtime.
   */
  var checksEnabled = true

  /**
   * The === equality throws an exception on false.
   */
  abstract class CheckEquality[A] {
    def ===(a: A): Boolean
  }

  /**
   * Implicitly adds === to every object in Scala.
   */
  implicit def checkEqualable[A](a: A): CheckEquality[A] =
    new CheckEquality[A] {
      def ===(b: A): Boolean = {
        if (a != b) {
          throw new ReasonedCheckException(a + " != " + b)
        }
        return true
      }
    }

  /**
   * Throws an exception if its argument doesn't evaluate to true.
   */
  def checkThat(action: => Boolean) {
    if (checksEnabled) {
      if (!action) throw new CheckException
    }
  }
}

/**
Unit test suites should inherit from TestSuite
  */
trait TestSuite {

  /**
   * Unit test suites should inherit from TestSuite.
   */
  def test[A](description: String)(action: => A) {
    try {
      action
      println("test passed: " + description)
      return ()
    } catch {
      case (e: Exception) => {
        println("test failed: " + description)
        println("exception thrown: " + e)
      }
    }
  }
}

class ArithmeticTests extends TestSuite {

  import Check._

  test("2 equals 2") {
    checkThat(2 === 2)
  }

  test("2 plus 2 equals 4") {
    checkThat(2 + 2 === 4)
  }

  test("2 equals 3") {
    checkThat(2 === 3)
  }

}

object DemoTesting extends scala.App {
  new ArithmeticTests
}
