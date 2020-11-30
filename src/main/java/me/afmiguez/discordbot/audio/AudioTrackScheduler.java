package me.afmiguez.discordbot.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public final class AudioTrackScheduler implements AudioLoadResultHandler {

    private final List<AudioTrack> queue;
    private final AudioPlayer player;

    public AudioTrackScheduler(final AudioPlayer player) {
        queue = Collections.synchronizedList(new LinkedList<>());
        this.player = player;
    }


    @Override
    public void trackLoaded(AudioTrack audioTrack) {
        this.player.playTrack(audioTrack);
    }

    @Override
    public void playlistLoaded(AudioPlaylist audioPlaylist) {
        queue.addAll(audioPlaylist.getTracks());
    }

    @Override
    public void noMatches() {

    }

    @Override
    public void loadFailed(FriendlyException e) {
        e.printStackTrace();
    }
}
