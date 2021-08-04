package turing.machine

fun main() {
  val tape = Tape()
  TuringMachine(tape).configure {
    card(0) {
      make case false write true move Direction.Right next 1
      make case true write true move Direction.Right next 1
    }

    card(1) {
      make case false write true move Direction.Halt next 0
      make case true write true move Direction.Right next 0
    }
  }.runWithHead(ReadWriteHead(card = 0, cell = 0))
  println(tape.toString().prependIndent("  ").trim())
}
