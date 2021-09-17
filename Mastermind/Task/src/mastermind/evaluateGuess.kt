package mastermind

data class Evaluation(var rightPosition: Int, val wrongPosition: Int)

fun evaluateGuess2(secret: String, guess: String): Evaluation {
    println("evaluateGuess init")
    println("secret:'$secret'")
    println(" guess:'$guess'")

    if (secret == guess) {
        return getEvaluationObj(secret.length, 0)
    }

    println("evaluateGuess finished.")
    return evaluate(secret, guess)
}

fun evaluateGuess(secret: String, guess: String): Evaluation {

    val rightPositions = secret.zip(guess).count { pair -> pair.first == pair.second }

    val commonLetters = "ABCDEF".sumBy { ch ->

        Math.min(secret.count {it == ch }, guess.count { it == ch })
    }
    return Evaluation(rightPositions, commonLetters - rightPositions)
}

private fun getEvaluationObj(rightPosition: Int, wrongPosition: Int): Evaluation {
    return Evaluation(rightPosition, wrongPosition)
}

private fun evaluate(secret: String, guess: String): Evaluation {
    var rightCount = 0
    var wrongCount = 0

    val secretStrBuilder = StringBuilder(secret)
    val valuesInGuessPresentInSecretButNotMatchedYet = mutableListOf<Char>()

    for (i in secret.indices) { // indices return valid range of string -> 0..str.length-1
        if (secret[i] == guess[i]) {
            if (valuesInGuessPresentInSecretButNotMatchedYet.contains(secret[i])) { // eliminar de la lista letras que posteriormente si hiciceron match
                if (secret.lastIndexOf(secret[i]) == i) { // lo borra siempre y cuando sea la ultima aparicion para asegurarnos de que no se pierde un mismatch previo
                    valuesInGuessPresentInSecretButNotMatchedYet.remove(secret[i])
                }
            }

            secretStrBuilder.setCharAt(i, '*') // limpiar secret: marcar letras que hicieron match correcto
            rightCount++
        } else if (secretStrBuilder.contains(guess[i])) {
            valuesInGuessPresentInSecretButNotMatchedYet.add(guess[i])
            secretStrBuilder.setCharAt(secretStrBuilder.indexOf(guess[i]), '*') // limpiar secret: marcar letras que cuentan como wrong psition
        }

        wrongCount = valuesInGuessPresentInSecretButNotMatchedYet.size
    }

    return getEvaluationObj(rightCount, wrongCount)
}
