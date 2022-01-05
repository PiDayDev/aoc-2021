package y16

fun main() {
    fun part1(): Int {
        // translating the input into JS results in Day25.js
        // - the first part (original lines 1..8)
        //   multiplies 14*182 and puts A + result into D
        // - the rest is an infinite loop that
        //   - copies D ( = original A + 14 * 182) into A
        //   - while A!=0
        //      > divides A by 2 rounding down
        //      > puts the result into A
        //      > outputs the remainder

        // This means that (A+14*182)
        //  must be written as 1010...1010 in base 2
        //  so that the division yields alternating remainders 0,1,0,1,...
        val increment = 14 * 182
        return generateSequence(1) { it + 1 }
            .map { "10".repeat(it) }
            .map { it.toInt(2) - increment }
            .first { it > 0 }
    }

    println(part1())
}

