package com.grayherring.bavid

fun String.shuffle(): String {

    val words = this.split(" ")
    var newSentince = mutableListOf<String>()
    for (word in words) {
        newSentince.add(word.scramble())
    }
    return newSentince.joinToString(" ")
}

fun String.cat(): String {

    val words = this.split(" ")
    var newSentince = mutableListOf<String>()
    for (word in words) {
        newSentince.add("meow")
    }
    return newSentince.joinToString(" ")
}

private fun String.scramble(): String {
    try {
        if (this.length < 3) {
            return this
        }
        var shuffledString = "" + this.toCharArray().first() // <-- add the first char
        var last = this.toCharArray().last()
        var word = this.replaceFirst(shuffledString, "")
        word = word.substring(0, word.length - 1)
        while (word.isNotEmpty()) {
            val index = Math.floor(Math.random() * word.length).toInt()
            val c = word[index]
            word = word.substring(0, index) + word.substring(index + 1)
            shuffledString += c
        }
        return shuffledString + last // <-- add the last char
    } catch (error: Error) {
        return this
    }
}