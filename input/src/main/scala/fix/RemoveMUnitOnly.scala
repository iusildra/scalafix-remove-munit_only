/*
rule = RemoveMUnitOnly
 */
package fix

import munit.FunSuite
import munit.Tag

object Extensions {
  implicit class Onlyable(val s: String) {
    def only: String = s
  }
}

object RemoveMUnitOnly {
  class Test extends FunSuite {
    test("hello".only) {
      assertEquals(42, 42)
    }

    test("hi".only) {
      assertEquals(42, 42)
    }

    test("failure".only.fail.name) {
      assertEquals(42, 43)
    }

    test("failure".fail.only.name) {
      assertEquals(42, 43)
    }

    test("failure".tag(new Tag("Slow")).only.name) {
      assertEquals(42, 43)
    }

    test("failure".only.tag(new Tag("Strange test")).name) {
      assertEquals(42, 43)
    }

    class Onlyable {
      def only: String = "I'm onlyable"
    }

    def only = new Onlyable

    only.only
  }
}
