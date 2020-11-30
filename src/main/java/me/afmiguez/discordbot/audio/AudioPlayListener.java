package me.afmiguez.discordbot.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import me.afmiguez.discordbot.events.EventListener;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Arrays;

@Service
public class AudioPlayListener implements EventListener<MessageCreateEvent> {

    private final AudioPlayerManager audioPlayerManager;
    private final AudioLoadResultHandler audioLoadResultHandler;


    @Autowired
    public AudioPlayListener(AudioPlayerManager audioPlayerManager, AudioLoadResultHandler audioLoadResultHandler) {
        this.audioPlayerManager = audioPlayerManager;
        this.audioLoadResultHandler = audioLoadResultHandler;
    }


    public Mono<Void> processCommand(Message eventMessage) {
        return Mono.just(eventMessage)
                .filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))   //verifica se o user não é bot
                .filter(message -> message.getContent().contains("!play"))            //verifica se a mensagem é !play
                .map(content -> Arrays.asList(content.getContent().split(" ")))//separa os argumentos do comando
                .doOnNext(command->audioPlayerManager.loadItem(command.get(1),audioLoadResultHandler))//passa o segundo argumento para ser carregado pelo audioPlayer
                .then();
    }


    @Override
    public Class<MessageCreateEvent> getEventType() {
        return MessageCreateEvent.class;
    }

    @Override
    public Mono<Void> execute(MessageCreateEvent event) {
        return processCommand(event.getMessage());
    }
}
