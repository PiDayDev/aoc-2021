package y16

private const val day = "02"

fun main() {
    fun easyMove(button: Char, instruction: Char) = when (instruction) {
        'U' -> if (button in "123") button else button - 3
        'D' -> if (button in "789") button else button + 3
        'L' -> if (button in "147") button else button - 1
        'R' -> if (button in "369") button else button + 1
        else -> button
    }

    fun hardMove(button: Char, instruction: Char): Char {
        when (button) {
            /*
        1
      2 3 4
    5 6 7 8 9
      A B C
        D
             */
            '1' -> if (instruction == 'D') return '3'
            '5' -> if (instruction == 'R') return '6'
            '9' -> if (instruction == 'L') return '8'
            'D' -> if (instruction == 'U') return 'B'
            '2' -> if (instruction == 'D') return '6' else if (instruction == 'R') return '3'
            '4' -> if (instruction == 'D') return '8' else if (instruction == 'L') return '3'
            'A' -> if (instruction == 'U') return '6' else if (instruction == 'R') return 'B'
            'C' -> if (instruction == 'U') return '8' else if (instruction == 'L') return 'B'
            '3' -> when (instruction) {
                'L' -> return '2'
                'R' -> return '4'
                'U' -> return '1'
                'D' -> return '7'
            }
            '6', '7', '8' -> when (instruction) {
                'L' -> return button - 1
                'R' -> return button + 1
                'U' -> return button - 4
                'D' -> return button + ('A'.code - '6'.code)
            }
            'B' -> when (instruction) {
                'L' -> return 'A'
                'R' -> return 'C'
                'U' -> return '7'
                'D' -> return 'D'
            }
        }
        return button
    }

    fun List<String>.code(move: (Char, Char) -> Char) = this
        .runningFold('5') { button, instr -> instr.fold(button, move) }
        .drop(1)
        .joinToString("") { "$it" }


    fun part1(input: List<String>) = input.code(::easyMove)

    fun part2(input: List<String>) = input.code(::hardMove)

    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))
}
