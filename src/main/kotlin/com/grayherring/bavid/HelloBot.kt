
]package com.grayherring.bavid

import com.grayherring.bavid.Commands.CatCommands
import com.grayherring.bavid.Commands.FunCommands
import com.ullink.slack.simpleslackapi.SlackSession
import com.ullink.slack.simpleslackapi.events.SlackMessagePosted
import com.ullink.slack.simpleslackapi.impl.SlackSessionFactory
import com.ullink.slack.simpleslackapi.listeners.SlackMessagePostedListener
import io.reactivex.Observable
import java.util.Properties
import java.util.concurrent.TimeUnit

private var botName = "bavid_botdeina"
private var authToken = "xoxb-405532021668-407102145430-HQi8ybEhABCn3E7Up3iZp9qZ"
private var channelName = "bavidsworld"
private var botId: String = ""

private var funModeOn = false
private var catModeOn = false

fun main(args: Array<String>) {
    loadConfig()
    val session = SlackSessionFactory.createWebSocketSlackSession(authToken)
    session.connect()
    botId = "<@${session.findUserByUserName(botName).id}>"
    session.addMessagePostedListener(messagePostedListener)

}

fun loadConfig() {
    val stream = Thread.currentThread().contextClassLoader.getResourceAsStream("bot.conf")
    val conf = Properties()
    conf.load(stream)
    botName = conf.getProperty("botName")
    authToken = conf.getProperty("authToken")
    channelName = conf.getProperty("channelName")
}

val messagePostedListener = SlackMessagePostedListener { event, session ->
        if (event.messageContent.contains(botId)) {
            if (event.messageContent.contains(CatCommands.value, true)) {
                meowModeToggle(event, session)
            }
            if (event.messageContent.contains(FunCommands.value, true)) {
                funModeToggle(event, session)
            }
            if (event.messageContent.endsWith(Commands.Joke.value, true)) {
                tellMeAJoke(event, session)
            }
        }
        sayHello(event, session)

        callMeAll(event, session)
    }


fun tellMeAJoke(event: SlackMessagePosted, session: SlackSession) {
    Observable.just(joke).flatMapIterable { it }
        .delay(3000, TimeUnit.MILLISECONDS)
        .subscribe({
            session.sendMessage(event.channel, it.applyFlags)
        }, {
            session.sendMessage(event.channel, it.localizedMessage.applyFlags)

        })

}

fun callMeAll(event: SlackMessagePosted, session: SlackSession) {
    if (!event.sender.isBot) {
        var lastIndex = -1
        var numberOfLinesToShow = 7
        youCanCallMeAl.forEachIndexed { index, s ->
            if (event.messageContent.contains(s, true)) {
                lastIndex = index
            }
        }
        if (lastIndex + numberOfLinesToShow > youCanCallMeAl.size) {
            numberOfLinesToShow = youCanCallMeAl.size - lastIndex

        }
        if (lastIndex > 0) {
            for (i in 1..numberOfLinesToShow) {
                session.sendMessage(event.channel, youCanCallMeAl[i + lastIndex].applyFlags)

            }
            session.sendMessage(event.channel, alGif.shuffled()[1])

        }
    }
}

fun funModeToggle(event: SlackMessagePosted, session: SlackSession) {
    val hello = "fub fat there are Tyoes in the basse texts so it looks even crezyer when on \uD83E\uDD2A"
    funModeOn = !funModeOn
    session.sendMessage(event.channel, hello.applyFlags)
}

fun meowModeToggle(event: SlackMessagePosted, session: SlackSession) {
    catModeOn = !catModeOn
    val catemoji = if (catModeOn) "😺" else "😿"
    session.sendMessage(event.channel, "cat mode $catModeOn ".applyFlags + catemoji)
}

fun sayHello(event: SlackMessagePosted, session: SlackSession) {
    if (event.messageContent.toLowerCase() == Commands.Hello.value)
        session.sendMessage(event.channel, "Hello, ${event.sender.realName}.".applyFlags)
}

val String.applyFlags: String
    get() {
        var finalString = this
        if (catModeOn) {
            finalString = finalString.cat()
        }
        if (funModeOn) {
            finalString = finalString.shuffle()
        }
        return finalString
    }


//Cammmanmds

sealed class Commands(val value: String) {
    object CatCommands : Commands("cat mode")
    object FunCommands : Commands("fun mode")
    object Joke : Commands("joke")
    object Hello : Commands("bavid")


}