import scala.collection.{immutable, mutable}

object MutableVsImmutable extends scala.App {

  /*
  Note the difference between how the update behaves for immutable and mutable data structures
   */

  val mutHashMap = new mutable.HashMap[String, Int]
  val immHashMap = new immutable.HashMap[String, Int]

  mutHashMap("Foo") = 1
  // immutable.HashMap has not update method!!
  // immHashMap("Foo") = 1

  println(mutHashMap)
  // Prints:
  // Map(Foo -> 1)

  println(immHashMap)
  // Prints:
  // Map()

  println(immHashMap + ("Foo" -> 1))
  // Prints
  // Map(Foo -> 1)
}
