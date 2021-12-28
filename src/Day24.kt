private class Alu {

    fun manualParsingAndSimplificationOfInput(inputDigits: List<Int>): Int {
        // After many simplifications, I got to this algorithm

        // Examining the steps on z, it turns out that the result is 0
        //  after the four divisions /= 26 only if every "if" branch
        //  with multiplications is skipped, which means their conditions
        //  must be false.
        //
        // This leads to the following conditions, that generate all valid numbers.
        // To maximize the number, the highest variable in each row must be 9.

        // a3 + 4 == a4
        // a5 + 2 == a6
        // a8 - 8 == a9
        // a7 + 5 == a10
        // a2     == a11
        // a1 - 6 == a12
        // a0 + 1 == a13

        val (a0, a1, a2, a3, a4) = inputDigits.take(5)
        val (a5, a6, a7, a8, a9) = inputDigits.drop(5).take(5)
        val (a10, a11, a12, a13) = inputDigits.takeLast(4)
        var z = (a0 + 15) * 26 * 26 +
                (a1 + 10) * 26 +
                (a2 + 2) +
                0
        if (a3 + 4 != a4) z = z * 26 + a4 + 12
        if (a5 + 2 != a6) z = z * 26 + a6 + 5
        z = z * 26 + a7 + 16
        if (a8 - 8 != a9) z = z * 26 + a9 + 15
        var x = z % 26 - 11 != a10
        z /= 26
        if (x) z = z * 26 + a10 + 3
        x = z % 26 - 2 != a11
        z /= 26
        if (x) z = z * 26 + a11 + 12
        x = z % 26 - 16 != a12
        z /= 26
        if (x) z = z * 26 + a12 + 10
        x = z % 26 - 14 != a13
        z /= 26
        if (x) z = z * 26 + a13 + 13

        return z
    }

}

fun main() {

    fun validateDeduction(myDeduction: Long): Long {
        val serialNumber = "$myDeduction".map { c -> c.digitToInt() }
        val z = Alu().manualParsingAndSimplificationOfInput(serialNumber)
        return if (z == 0) myDeduction else -1L
    }

    println(validateDeduction(89959794919939L))
    println(validateDeduction(17115131916112L))
}
