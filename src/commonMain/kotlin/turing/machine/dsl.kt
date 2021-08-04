package turing.machine

class StateDsl(private val machine: TuringMachine) {
  private val cards = mutableMapOf<CardNumber, CardDsl>()

  fun card(number: CardNumber, function: CardDsl.() -> Unit) =
    cards.getOrPut(number) { CardDsl() }.apply(function)

  fun card(number: CardNumber) =
    cards.getOrPut(number) { CardDsl() }

  class CardDsl {
    private val moves = mutableMapOf<CellValue, Move>()

    val make = this

    fun case(value: CellValue, move: Move) {
      moves[value] = move
    }

    infix fun case(value: CellValue) = CardCaseDsl(this, value)

    fun final() = Card(moves)
  }

  class CardCaseDsl(val card: CardDsl, val value: CellValue) {
    infix fun write(value: CellValue) = CardMoveDsl(this, value)
  }

  class CardMoveDsl(val case: CardCaseDsl, val write: CellValue) {
    lateinit var direction: Direction
    var next: CardNumber = 0

    infix fun move(direction: Direction): CardMoveDsl {
      this.direction = direction
      return this
    }

    infix fun next(card: CardNumber) {
      next = card
      final()
    }

    fun final() {
      case.card.case(case.value, Move(write = write, direction, next))
    }
  }

  fun final() {
    for (number in cards.keys) {
      machine.cards.set(number, cards[number]!!.final())
    }
  }
}
