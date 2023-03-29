package flashcards

import java.util.MissingFormatArgumentException
import kotlin.system.exitProcess

interface Messaging {
    val msg: String
    fun show(vararg strings: String) {
        try {
            println(this.msg.format(*strings)).also {
                if (this in Errors.values()) exitProcess(0)
                FlashCards.log.add(this.msg.format(*strings))
            }
        } catch(E: MissingFormatArgumentException) {
            println("$this: Invalid number of arguments.")
            exitProcess(0)
        }
    }
}

enum class Console(override val msg: String): Messaging {
    Menu("Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):"),
    InputNoOfCards("How many times to ask?"),
    Card("The card:"),
    CardDefinition("The definition of the card:"),
    CardAdded("The pair (\"%s\":\"%s\") has been added."),
    CardRemove("Which card?"),
    CardRemoved("The card has been removed."),
    CardRemoveNotFound("Can't remove \"%s\": there is no such card."),
    FileName("File name:"),
    LogSaved("The log has been saved."),
    CardsReset("Card statistics have been reset."),
    CardHardest("The hardest card%s %s %s. You have %s errors answering %s."),
    CardsNoMistakes("There are no cards with errors."),
    FileNotFound("File not found."),
    CardsFromFile("%s cards have been loaded."),
    CardsSaved("%s cards have been saved."),
    PrintDefinition("Print the definition of \"%s\":"),
    DuplicateTerm("The card \"%s\" already exists."),
    DuplicateDefinition("The definition \"%s\" already exists."),
    GoodAnswer("Correct!"),
    WrongAnswer("Wrong. The right answer is \"%s\"."),
    WrongAnswerOther("Wrong. The right answer is \"%s\", but your definition is correct for \"%s\".")
}

enum class Errors(override val msg: String): Messaging { Exit("Bye bye!") }