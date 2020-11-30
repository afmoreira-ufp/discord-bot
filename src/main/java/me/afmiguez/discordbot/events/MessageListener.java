package me.afmiguez.discordbot.events;

import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class MessageListener {

    private final Random random=new Random();
    public Mono<Void> processCommand(Message eventMessage) {
        return Mono.just(eventMessage)
           .filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))   //verifica se o user não é bot
           .filter(message -> message.getContent().equalsIgnoreCase("!hey"))            //verifica se a mensagem é !hey
           .flatMap(Message::getChannel)                                                           //busca o canal da mensagem
           .flatMap(channel -> channel.createMessage(geraStringComNumeroAleatorio()))               //devolve uma mensagem para o canal
           .then();
    }

    private String geraStringComNumeroAleatorio(){
        return random.nextInt(100)+"";
    }

}

