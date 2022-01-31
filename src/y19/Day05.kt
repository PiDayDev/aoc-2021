package y19

private const val day = "05"

fun main() {

    fun List<String>.execute(systemId: Long): Long {
        val codes = codes()
        val outputs = mutableListOf<Long>()
        IntCodeProcessor(codes).process(sequenceOf(systemId).iterator(), outputs::add)
        println(outputs)
        return outputs.last()
    }

    fun part1(input: List<String>) = input.execute(1)
    fun part2(input: List<String>) = input.execute(5)

    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))
}
