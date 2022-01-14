package y18

private const val day = "13"

private enum class Turn {
    LEFT, STRAIGHT, RIGHT;

    fun next() = values().let { it[(ordinal + 1) % it.size] }
}

private enum class Direction {
    UP {
        override fun move(x: Int, y: Int) = x to y - 1
        override fun slashForward() = RIGHT
        override fun slashBackward() = LEFT
        override fun turn(side: Turn) = when (side) {
            Turn.LEFT -> LEFT
            Turn.STRAIGHT -> this
            Turn.RIGHT -> RIGHT
        }
    },
    DOWN {
        override fun move(x: Int, y: Int) = x to y + 1
        override fun slashForward() = LEFT
        override fun slashBackward() = RIGHT
        override fun turn(side: Turn) = when (side) {
            Turn.LEFT -> RIGHT
            Turn.STRAIGHT -> this
            Turn.RIGHT -> LEFT
        }

    },
    LEFT {
        override fun move(x: Int, y: Int) = x - 1 to y
        override fun slashForward() = DOWN
        override fun slashBackward() = UP
        override fun turn(side: Turn) = when (side) {
            Turn.LEFT -> DOWN
            Turn.STRAIGHT -> this
            Turn.RIGHT -> UP
        }
    },
    RIGHT {
        override fun move(x: Int, y: Int) = x + 1 to y
        override fun slashForward() = UP
        override fun slashBackward() = DOWN
        override fun turn(side: Turn) = when (side) {
            Turn.LEFT -> UP
            Turn.STRAIGHT -> this
            Turn.RIGHT -> DOWN
        }
    };

    abstract fun move(x: Int, y: Int): Pair<Int, Int>
    abstract fun slashForward(): Direction
    abstract fun slashBackward(): Direction
    abstract fun turn(side: Turn): Direction
}

private data class Cart(
    val id: String,
    val x: Int,
    val y: Int,
    val dir: Direction,
    val turn: Turn = Turn.LEFT
) : Comparable<Cart> {
    infix fun moveOn(tracks: List<String>): Cart {
        val (xF, yF) = dir.move(x, y)
        val part: Char = tracks[yF][xF]
        val dirF = when (part) {
            '/' -> dir.slashForward()
            '\\' -> dir.slashBackward()
            '+' -> dir.turn(turn)
            else -> dir
        }
        val turnF = if (part == '+') turn.next() else turn
        return Cart(id, xF, yF, dirF, turnF)
    }

    infix fun collidesWith(c: Cart) = x == c.x && y == c.y

    override fun compareTo(other: Cart) =
        when (val dy = y - other.y) {
            0 -> x - other.x
            else -> dy
        }
}

fun main() {
    fun String.sanitizeTrack() = replace('<', '-').replace('>', '-').replace('^', '|').replace('v', '|')

    fun Char.toDirection() = when (this) {
        '<' -> Direction.LEFT
        '>' -> Direction.RIGHT
        '^' -> Direction.UP
        else -> Direction.DOWN
    }

    fun List<String>.toTracksAndCarts(): Pair<List<String>, List<Cart>> {
        val ids = ('A'..'Z').iterator()
        val tracks = map { it.sanitizeTrack() }
        val carts = flatMapIndexed { y, row ->
            row.mapIndexedNotNull { x, c ->
                if (c in "^<>v") Cart("${ids.next()}", x, y, c.toDirection()) else null
            }
        }.sorted()
        return tracks to carts
    }

    fun part1(input: List<String>): Cart {
        val (tracks, cartList) = input.toTracksAndCarts()

        val state: Pair<List<Cart>, Cart?> = cartList to null
        val collision = generateSequence(state) { (carts, _) ->
            var crash: Cart? = null
            val movedList = carts.mapIndexed { j, cart ->
                val movedCart = cart moveOn tracks
                if (carts.drop(j).any { movedCart collidesWith it }) {
                    crash = movedCart
                }
                movedCart
            }.sorted()
            movedList to crash
        }
            .mapNotNull { it.second }
            .first()

        return collision
    }

    fun part2(input: List<String>): Any {
        val (tracks, cartList) = input.toTracksAndCarts()
        val survivor = generateSequence(cartList) { carts ->
            val collisions = mutableListOf<Cart>()
            val movedList = carts.mapIndexed { j, cart ->
                val movedCart = cart moveOn tracks
                carts.drop(j + 1)
                    .filter { movedCart collidesWith it }
                    .also { if (it.isNotEmpty()) collisions.addAll(it + movedCart) }
                movedCart
            }.sorted()
            val survivors = movedList
                .filter { c -> movedList.none { k -> k.id != c.id && k collidesWith c } }
                .filter { c -> collisions.none { k -> k.id == c.id } }
            survivors
        }
            .first { it.size == 1 }
            .last()

        return survivor
    }

    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))
}
