package flashcards

import java.io.File
import kotlin.random.Random
import kotlin.system.exitProcess

data class Card(val term: String, var definition: String, var mistakes: Int = 0)

class FlashCards(args: Array<String>) {
    private val cardsList = mutableListOf<Card>()

    init {
        log.clear()
        cardsList.clear()

        if ("-import" in args) import(fileName = args[args.indexOf("-import") + 1])

        while (true) {
            Console.Menu.show()
            when(logReadln()) {
                "add" -> add()
                "remove" -> remove()
                "import" -> import()
                "export" -> export()
                "ask" -> ask()
                "hardest card" -> hardestCard()
                "reset stats" -> reset()
                "log" -> log()
                "exit" -> {
                    if ("-export" in args) {
                        println(Errors.Exit.msg)
                        export(args[args.indexOf("-export") + 1])
                        exitProcess(0)
                    } else {
                        Errors.Exit.show()
                    }
                }
            }
        }
    }

    private fun hardestCard() {
        var hardestCardMistakes = 0
        cardsList.maxByOrNull { it.mistakes }.let { if (it != null) hardestCardMistakes = it.mistakes }
        if (hardestCardMistakes == 0) {
            Console.CardsNoMistakes.show()
            return
        }
        val hardestCards = mutableListOf<Card>()
        for (card in cardsList) if (card.mistakes == hardestCardMistakes) hardestCards.add(card)
        val hardestCardsTerms = mutableListOf<String>()
        for (card in hardestCards) hardestCardsTerms.add("\"${card.term}\"")

        val plural = if (hardestCardsTerms.size > 1) "s" else ""
        val verb = if (hardestCardsTerms.size > 1) "are" else "is"
        val pronoun = if (hardestCardsTerms.size > 1) "them" else "it"
        Console.CardHardest.show(plural, verb, hardestCardsTerms.joinToString(", "), hardestCardMistakes.toString(), pronoun)
    }

    private fun logReadln(): String {
        val input = readLine()!!
        log.add(input)
        return input
    }

    private fun log() {
        Console.FileName.show()
        val file = File(logReadln()).also { it.writeText("") }
        log.forEach { str -> file.appendText("$str\n") }
        Console.LogSaved.show()
    }

    private fun reset() { cardsList.forEach { it.mistakes = 0 }.also { Console.CardsReset.show() } }

    private fun ask() {
        Console.InputNoOfCards.show()
        val attempts = logReadln().toInt()

        repeat(attempts) {
            val random = Random.nextInt(0, cardsList.size)
            val card = cardsList[random]
            Console.PrintDefinition.show(card.term)
            val answer = logReadln()

            // Correct
            if (answer == card.definition) {
                Console.GoodAnswer.show()
                return@repeat
            }
            // Wrong Other
            for (i in 0..cardsList.lastIndex) {
                if (cardsList[i] != card && cardsList[i].definition == answer) {
                    Console.WrongAnswerOther.show(card.definition, cardsList[i].term)
                    card.mistakes++
                    return@repeat
                }
            }
            // Wrong
            Console.WrongAnswer.show(card.definition)
            card.mistakes++
        }
    }

    private fun export(fileName: String = "") {
        val file: File
        if (fileName.isNotEmpty()) {
            file = File(fileName)
        } else {
            Console.FileName.show()
            file = File(logReadln())
        }
        if (!file.exists()) file.createNewFile()
        file.writeText("")

        for (card in cardsList) file.appendText("${card.term}\n${card.definition}\n${card.mistakes}\n")
        Console.CardsSaved.show(cardsList.size.toString())
    }

    private fun import(fileName: String = "") {
        val file = if (File(fileName).exists()) {
            File(fileName)
        } else {
            Console.FileName.show()
            File(logReadln())
        }
        if (!file.exists()) {
            Console.FileNotFound.show()
            return
        }

        val lines = file.readLines()
        var imported = 0
        cards@ for (i in 0..lines.lastIndex step 3) {
            val term = lines.getOrNull(i)
            val definition = lines.getOrNull(i + 1)
            val mistakes = lines.getOrNull(i + 2)

            for (card in cardsList) {
                if (card.term == term) {
                    card.definition = definition ?: ""
                    card.mistakes = 0
                    imported++
                    continue@cards
                }
            }
            if (!term.isNullOrEmpty() && !definition.isNullOrEmpty() && !mistakes.isNullOrEmpty()) {
                cardsList.add(Card(term, definition, mistakes.toInt()))
                imported++
            }
        }

        Console.CardsFromFile.show((imported).toString())
    }

    private fun remove() {
        Console.CardRemove.show()
        val term = logReadln()

        cardsList.forEach { card ->
            if (card.term == term) {
                cardsList.remove(card)
                Console.CardRemoved.show()
                return
            }
        }
        Console.CardRemoveNotFound.show(term)
    }

    private fun add() {
        Console.Card.show()
        val term = logReadln()
        term@ while (true) {
            for (card in cardsList) {
                if (card.term == term) {
                    Console.DuplicateTerm.show(term)
                    return
                }
            }
            break@term
        }

        Console.CardDefinition.show()
        val definition = logReadln()
        definition@ while (true) {
            for (card in cardsList) {
                if (card.definition == definition) {
                    Console.DuplicateDefinition.show(definition)
                    return
                }
            }
            break@definition
        }

        cardsList.add(Card(term, definition))
        Console.CardAdded.show(term, definition)
    }

    companion object {
        val log = mutableListOf<String>()
    }
}

fun main(args: Array<String>) {
    FlashCards(args)
}