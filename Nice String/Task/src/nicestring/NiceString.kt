package nicestring

fun String.isNiceNotFunctional(): Boolean {
    val vowels = listOf('a', 'e', 'i', 'o', 'u')

    val containsOnlyValidSubstrings = { str: String ->
        when {
            str.contains("bu") -> false
            str.contains("ba") -> false
            str.contains("be") -> false
            else -> true
        }
    }

    val containsAtLeastThreeVowels = { str: String -> str.count { it in vowels } >= 3 }

    val containsDoubleLetters = { str: String ->
        var result = false

        for (i in str.indices) {
            if (str.hasNext(i)) {
                if (str[i] == str[i + 1]) {
                    result = true
                    break
                }
            }
        }

        result
    }

    val satisfiedConditions = listOf(
        containsOnlyValidSubstrings(this),
        containsAtLeastThreeVowels(this),
        containsDoubleLetters(this)
    ).count { it }

    return satisfiedConditions >= 2
}

fun String.hasNext(i: Int): Boolean {
    return try {
        this[i + 1]
        true
    } catch (ex: StringIndexOutOfBoundsException) {
        false
    }
}

fun String.isNice(): Boolean {
    //setOf("bu", "ba", "be").all { !this.contains(it) }
    val containsOnlyValidSubstrings = setOf("bu", "ba", "be").none { this.contains(it) }

    val containsAtLeastThreeVowels = count { it in setOf('a', 'e', 'i', 'o', 'u') } >= 3

    val containsDoubleLetters = zipWithNext().any() { it.first == it.second } // abcd -> (a, b) (b, c) (c, d)

    return listOf(
        containsAtLeastThreeVowels,
        containsDoubleLetters,
        containsOnlyValidSubstrings
    ).count { it } >= 2

}

fun main(args: Array<String>) {
    println("aaab".isNice())
}