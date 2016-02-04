package com.example.h156252.connected_cars;

/**
 * Created by H156252 on 1/27/2016.
 */
public interface IVoiceControl {
    public abstract void processVoiceCommands(String... voiceCommands); // This will be executed when a voice command was found

    public void restartListeningService(); // This will be executed after a voice command was processed to keep the recognition service activated
}