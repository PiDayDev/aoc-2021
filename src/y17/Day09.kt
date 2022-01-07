package y17

import java.util.*

private const val day = "09"

fun main() {
    fun part1(input: List<String>): Int {
        val stream = input.first()
        val stack = Stack<Char>()
        var score = 0
        var garbage = false
        var skip = false
        stream.forEach { c ->
            if (skip) {
                skip = false
            } else {
                skip = c == '!'
                if (garbage) garbage = c != '>'
                else when (c) {
                    '{' -> {
                        stack.push(c)
                        score += stack.size
                    }
                    '}' -> stack.pop()
                    '<' -> garbage = true
                }
            }
        }
        return score
    }

    fun part2(input: List<String>): Int {
        val stream = input.first()
        val stack = Stack<Char>()
        var count = 0
        var garbage = false
        var skip = false
        stream.forEach { c ->
            if (skip) {
                skip = false
            } else {
                skip = c == '!'
                if (garbage) {
                    garbage = c != '>'
                    if (garbage && !skip) {
                        count++
                    }
                } else when (c) {
                    '{' -> stack.push(c)
                    '}' -> stack.pop()
                    '<' -> garbage = true
                }
            }
        }
        return count
    }

    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))
}
