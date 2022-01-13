package y18

private const val day = "08"

private class Node8(
    val childrenSize: Int,
    val metadataSize: Int
) {
    val children: MutableList<Node8> = mutableListOf()
    val metadata: MutableList<Int> = mutableListOf()

    fun consume(list: List<Int>): List<Int> {
        var rest = list
        while (children.size < childrenSize) {
            val (c, m) = rest
            val child = Node8(c, m)
            children += child
            rest = child.consume(rest.drop(2))
        }
        metadata.addAll(rest.take(metadataSize))
        return rest.drop(metadataSize)
    }

    fun sum(): Int = metadata.sum() + children.sumOf { it.sum() }

    fun value() : Int = when(childrenSize) {
        0 -> sum()
        else -> metadata.sumOf { children.getOrNull(it-1)?.value() ?: 0 }
    }
}

fun main() {
    fun List<String>.toTree(): Node8 {
        val list = first().split(" ").map { it.toInt() }
        val root = Node8(list[0], list[1])
        root.consume(list.drop(2))
        return root
    }

    fun part1(input: List<String>) = input.toTree().sum()

    fun part2(input: List<String>) = input.toTree().value()

    try {
        val testInput = readInput("Day${day}_test")
        val test1 = part1(testInput)
        check(test1 == 138) { "$test1 should be 138" }
    } catch (e: java.io.FileNotFoundException) {
        // no tests
    }

    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))
}
