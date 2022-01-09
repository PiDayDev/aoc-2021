package y17

private fun String.knotHash(): String {
    val lengths = toByteArray().map { it.toInt() }.toMutableList()
    lengths.addAll(listOf(17, 31, 73, 47, 23))
    val chunks = IntArray(256) { it }
        .knot(lengths, 64)
        .asIterable()
        .chunked(16)
    val output = StringBuilder()
    chunks.forEach { chunk ->
        var xored = 0
        chunk.forEach { number ->
            xored = xored xor number
        }
        output.append(xored.toString(2).padStart(8, '0'))
    }
    return output.toString()
}

private fun IntArray.knot(lengths: List<Int>, rounds: Int): IntArray {
    val list = copyOf()
    var current = 0
    var skip = 0
    var tmp: Int
    repeat(rounds) {
        for (number in lengths) {
            if (number > list.size) continue
            for (curr in 0 until number / 2) {
                tmp = list[(current + curr) % list.size]
                list[(current + curr) % list.size] = list[(current + number - 1 - curr) % list.size]
                list[(current + number - 1 - curr) % list.size] = tmp
            }
            current = (current + number + skip++) % list.size
        }
    }
    return list
}

private const val key = "uugsqrei"

fun main() {

    fun part1(s: String) = (0..127)
        .map { "$s-$it".knotHash() }
        .sumOf { it.count { c -> c == '1' } }

    fun part2(s: String): Int {
        val grid = (0..127).map { "$s-$it".knotHash() }
        val sets = DisjointSet<Pair<Int, Int>>()
        grid.forEachIndexed { r, hash ->
            hash.forEachIndexed { c, char ->
                if (char == '1') {
                    val pos = r to c
                    sets.add(pos)
                    if (r + 1 in grid.indices && grid[r + 1][c] == '1') {
                        val south = r + 1 to c
                        sets.add(south)
                        sets.merge(pos, south)
                    }
                    if (c + 1 in hash.indices && grid[r][c + 1] == '1') {
                        val east = r to c + 1
                        sets.add(east)
                        sets.merge(pos, east)
                    }
                }
            }
        }
        return sets.elements.keys.map { sets.findPartition(it) }.distinct().size
    }


    val testInput = "flqrgnkx"
    val test1 = part1(testInput)
    check(test1 == 8108) { "Wrong: $test1" }

    println(part1(key))
    println(part2(key))
}
