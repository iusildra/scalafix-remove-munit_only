package fix

object LeaveNonMUnitOnly {
  class Onlyable {
    def only: String = "I'm onlyable"

    def test(s: String) = s

    def test(s: String)(f: => Unit) = ???
  }

  def test(only: String) = only

  def only = new Onlyable

  only.only
  test(only.only) { ??? }
}