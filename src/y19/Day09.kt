package y19

import java.util.concurrent.atomic.AtomicLong

private const val day = "09"

fun main() {
    fun solve(codes: List<Long>, initValue: Long): Long {
        val ins = iterator { yield(initValue) }
        val out = AtomicLong(-initValue)
        IntCodeProcessor(codes).process(ins) { out.set(it) }
        return out.get()
    }

    val codes = readInput("Day${day}").codes()
    println(solve(codes, 1L))
    println(solve(codes, 2L))
}
