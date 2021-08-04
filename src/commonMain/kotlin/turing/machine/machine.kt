package turing.machine

typealias CellValue = Boolean
typealias CardNumber = Int
typealias CellNumber = Int

const val CardDefaultValue: CellValue = false

enum class Direction {
  Left,
  Right,
  Halt
}

class Move(
  val write: CellValue,
  val direction: Direction,
  val next: CardNumber
) {
  override fun toString() = "(Write ${write}, Direction ${direction.name}, Next Card: ${next})"
}

class Card(
  val moves: Map<CellValue, Move>
)

class ReadWriteHead(
  var card: CardNumber,
  var cell: CellNumber
)

fun MutableList<CellValue>.fit(cell: CellNumber) {
  while (cell > size - 1) {
    add(CardDefaultValue)
  }
}

fun MutableList<CellValue>.readAndFit(cell: CellNumber): CellValue {
  fit(cell)
  return this[cell]
}

fun MutableList<CellValue>.writeAndFit(cell: CellNumber, value: CellValue) {
  fit(cell)
  this[cell] = value
}

class Tape {
  private val negative = mutableListOf<CellValue>()
  private val positive = mutableListOf<CellValue>()

  fun read(cell: CellNumber): CellValue {
    return if (cell < 0) {
      negative.readAndFit(-cell)
    } else {
      positive.readAndFit(cell)
    }
  }

  fun write(cell: CellNumber, value: CellValue) {
    if (cell < 0) {
      negative.writeAndFit(-cell, value)
    } else {
      positive.writeAndFit(cell, value)
    }
  }

  override fun toString() = buildString {
    appendLine("[Tape]")
    for (i in negative.indices) {
      if (i == 0) continue
      appendLine("[-${i}] ${negative[i]}")
    }

    for (i in positive.indices) {
      appendLine("[${i}] ${positive[i]}")
    }
  }
}

class CardStack(
  private val cards: MutableMap<CardNumber, Card> = mutableMapOf()
) {
  fun set(index: CardNumber, card: Card) {
    cards[index] = card
  }

  fun get(index: CardNumber): Card? = cards[index]
}

class TuringMachine(
  val tape: Tape = Tape(),
  val cards: CardStack = CardStack()
) {
  fun runWithHead(head: ReadWriteHead) {
    var index = 0L
    while (true) {
      index++
      val card = cards.get(head.card) ?: throw Exception("Card ${head.card} not found.")
      val value = tape.read(head.cell)
      val move = card.moves[value] ?: throw Exception("Card ${head.card} does not have a move for value $value")

      println("[Loop ${index}] [Cell ${head.cell} = $value] [Card ${head.card} Move $value = $move]")

      tape.write(head.cell, move.write)

      when (move.direction) {
        Direction.Halt -> break
        Direction.Left -> head.cell--
        Direction.Right -> head.cell++
      }

      head.card = move.next
    }
  }

  fun configure(function: StateDsl.() -> Unit): TuringMachine {
    val dsl = StateDsl(this)
    function(dsl)
    dsl.final()
    return this
  }
}
