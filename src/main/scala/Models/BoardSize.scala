package Models

object BoardSize extends Enumeration {
  val SMALL: Value = Value(8)
  val MEDIUM: Value = Value(10)
  val BIG: Value = Value(12)
  type BoardSize = Value
}
