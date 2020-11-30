package me.afmiguez.discordbot;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import discord4j.voice.AudioProvider;
import me.afmiguez.discordbot.audio.AudioTrackScheduler;
import me.afmiguez.discordbot.audio.LavaPlayerAudioProvider;
import me.afmiguez.discordbot.events.EventListener;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.playback.NonAllocatingAudioFrameBuffer;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class BotConfiguration {

    private static final Logger log = LoggerFactory.getLogger( BotConfiguration.class );

    @Value("${token}")//TODO modificar o valor no ficheiro application.yml
    private String token;

    @Bean
    public <T extends Event> GatewayDiscordClient gatewayDiscordClient(List<EventListener<T>> eventListeners) {
        GatewayDiscordClient client = null;

        try {
            client = DiscordClientBuilder.create(token)
              .build()
              .login()
              .block();
            assert(client!=null);

            for(EventListener<T> listener : eventListeners) {
                client.on(listener.getEventType())
                  .flatMap(listener::execute)
                  .onErrorResume(listener::handleError)
                  .subscribe();
            }
            client.onDisconnect().block();
        }
        catch ( Exception exception ) {
            log.error( "Be sure to use a valid bot token!", exception );
        }

        return client;
    }


    //declaração de Beans Spring
    @Bean
    public AudioPlayerManager audioPlayerManager() {
        // Cria instâncias de AudioPlayer e converte URLs em instâncias de AudioTrack
        final AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
        playerManager.getConfiguration().setFrameBufferFactory(NonAllocatingAudioFrameBuffer::new);
        // Permite que o playerManager analise fontes remotas como links do YouTube
        AudioSourceManagers.registerRemoteSources(playerManager);
        return playerManager;
    }

    @Bean
    public AudioPlayer audioPlayer(AudioPlayerManager playerManager){
        // Cria um AudioPlayer para que Discord4J possa receber dados de áudio
        return playerManager.createPlayer();
    }


    @Bean
    public AudioLoadResultHandler audioLoadResultHandler(AudioPlayer audioPlayer){
        return new AudioTrackScheduler(audioPlayer);
    }


    @Bean
    public AudioProvider audioProvider(){
        return new LavaPlayerAudioProvider(audioPlayer(audioPlayerManager()));
    }

}
