package y17

private const val steps = 363

fun main() {
    fun shortCircuit(steps: Int, loopCount: Int, findAfter: Int): Int {
        val successors = HashMap<Int, Int>(loopCount + 1)
        successors[0] = 0
        (1..loopCount).forEach {
            val prev = it - 1
            val src = (1..steps % successors.size).fold(prev) { n, _ -> successors[n]!! }
            val dst = successors[src]!!
            successors[src] = it
            successors[it] = dst
        }
        return successors[findAfter]!!
    }

    println(shortCircuit(steps = steps, loopCount = 2017, findAfter = 2017))

    // warning: EXTREMELY SLOW
    println(shortCircuit(steps = steps, loopCount = 50_000_000, findAfter = 0))
}
