package y17

private class Turing(
) {
    private var position: Int = 0
    private var state: Char = 'A'
    private val tape: MutableSet<Int> = mutableSetOf()

    val checksum: Int
        get() = tape.size

    fun step() {
        val value = position in tape
        val s = state

        if (nextValue(s, value)) {
            tape += position
        } else {
            tape -= position
        }
        position += nextMove(s, value)
        state = nextState(s, value)
    }

    fun nextValue(state: Char, value: Boolean) =
        state in "CEF" || state in "AD" && !value

    fun nextMove(state: Char, value: Boolean) = when {
        state in "ACF" -> +1
        state in "E" && !value -> +1
        state in "B" && value -> +1
        else -> -1
    }

    fun nextState(state: Char, value: Boolean) = when (state) {
        'A' -> if (value) 'C' else 'B'
        'B' -> if (value) 'D' else 'A'
        'C' -> if (value) 'A' else 'D'
        'D' -> if (value) 'D' else 'E'
        'E' -> if (value) 'B' else 'F'
        'F' -> if (value) 'E' else 'A'
        else -> state
    }

}

fun main() {
    // Implement Turing machine described in input file
    val turing = Turing()
    repeat(12_399_302) { turing.step() }
    println(turing.checksum)
}
