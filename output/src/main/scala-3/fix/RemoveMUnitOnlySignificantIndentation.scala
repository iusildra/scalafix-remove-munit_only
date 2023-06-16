package fix

import munit.FunSuite

object RemoveMUnitOnlySignificantIndentation:
  class Test extends FunSuite:
    test("hello"):
      assertEquals(42, 42)

    test("hi"):
      assertEquals(42, 42)

    class Onlyable:
      def only: String = "I'm onlyable"

    def only = new Onlyable

    only.only
