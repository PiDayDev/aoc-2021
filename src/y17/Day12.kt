package y17

private const val day = 12

class DisjointSet<I>(initialSet: List<I> = emptyList()) {
    val elements: MutableMap<I, I> = initialSet.associateWith { it }.toMutableMap()

    fun add(elem: I) {
        if (elem !in elements.keys)
            elements[elem] = elem
    }

    fun findPartition(elem: I): I =
        when (val info = elements[elem]!!) {
            elem -> elem
            else -> findPartition(info)
        }

    fun merge(elem1: I, elem2: I): Boolean {
        val r1 = findPartition(elem1)
        val r2 = findPartition(elem2)
        if (r1 == r2) {
            return false
        }
        elements[r1] = r2
        elements[elem1] = r2
        return true
    }

    fun areDisjoint(elem1: I, elem2: I) = findPartition(elem1) != findPartition(elem2)
}


fun main() {
    fun List<String>.toDisjointSets(): DisjointSet<Int> {
        val ds = DisjointSet<Int>()
        forEach { row ->
            val (k, v) = row.split(" <-> ")
            val key = k.toInt()
            ds.add(key)
            val values = v.split(", ").map { it.toInt() }
            values.forEach {
                ds.add(it)
                ds.merge(key, it)
            }
        }
        return ds
    }

    fun part1(input: List<String>): Int {
        val ds = input.toDisjointSets()
        return ds.elements.keys.count { !ds.areDisjoint(it, 0) }
    }

    fun part2(input: List<String>): Int {
        val ds = input.toDisjointSets()
        return ds.elements.keys.groupBy { ds.findPartition(it) }.size
    }

    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))
}
