package me.afmiguez.discordbot.audio;

import me.afmiguez.discordbot.events.EventListener;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.VoiceState;
import discord4j.core.object.entity.Member;
import discord4j.voice.AudioProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


@Service
public class JoinListener implements EventListener<MessageCreateEvent> {

    private final AudioProvider provider;

    @Autowired
    public JoinListener(AudioProvider audioProvider) {
        this.provider = audioProvider;
    }

    @Override
    public Class<MessageCreateEvent> getEventType() {
        return MessageCreateEvent.class;
    }

    @Override
    public Mono<Void> execute(MessageCreateEvent eventMessage) {

        if(eventMessage.getMessage().getContent().contains("!join")) {

            return Mono.justOrEmpty(eventMessage.getMember())
                    .flatMap(Member::getVoiceState)//obtem o voiceState do Utilizador
                    .flatMap(VoiceState::getChannel)//obtem o canal deste voiceState
                    .flatMap(channel -> channel.join(spec -> spec.setProvider(provider))) //faz com que o bot ingresse no canal
                    .then();

        }
         return Mono.empty();

    }
}
