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
    test("hello") {
      assertEquals(42, 42)
    }

    test("hi") {
      assertEquals(42, 42)
    }

    test("failure".fail.name) {
      assertEquals(42, 43)
    }

    test("failure".fail.name) {
      assertEquals(42, 43)
    }

    test("failure".tag(new Tag("Slow")).name) {
      assertEquals(42, 43)
    }

    test("failure".tag(new Tag("Strange test")).name) {
      assertEquals(42, 43)
    }

    class Onlyable {
      def only: String = "I'm onlyable"
    }

    def only = new Onlyable

    only.only
  }
}
