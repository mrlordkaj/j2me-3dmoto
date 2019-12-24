/*
 * Copyright (C) 2012 Thinh Pham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.openitvn.game;

import java.io.IOException;
import java.io.InputStream;
import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import javax.microedition.media.control.VolumeControl;

/**
 *
 * @author Thinh Pham
 */
public class SoundManager {
    public static final String TYPE_MIDI = "audio/midi";
    public static final String TYPE_WAVE = "audio/x-wav";
    
    private static SoundManager instance;
    public static SoundManager getInstance() {
        if(instance == null) instance = new SoundManager();
        return instance;
    }
    
    private Player player;
    private String currentFile = "";
    private String currentType = "";
    
    private boolean enabled = true;
    public void setEnabled(boolean value) {
        enabled = value;
        if(enabled) startMusic();
        else stopMusic();
    }
    public boolean isEnabled() { return enabled; }
    
    public void sound(String fileName, String audioType) {
        if(enabled) {
            try {
                InputStream inputStream = SoundManager.class.getResourceAsStream(fileName);
                player = Manager.createPlayer(inputStream, audioType);
                player.realize();
                player.prefetch();
                ((VolumeControl)player.getControl("VolumeControl")).setLevel(30);
                player.start();
                ((VolumeControl)player.getControl("VolumeControl")).setLevel(30);
                inputStream.close();
            } catch (IOException ex) {
            } catch (MediaException ex) {
            }
        }
    }
    
    public void playMusic(String fileName, String audioType) {
        if(currentFile.equals(fileName)) return;
        
        currentFile = fileName;
        currentType = audioType;
        if(enabled) startMusic();
    }
    
    private void startMusic() {
        stopMusic();
        if(!currentFile.equals("")) {
            try {
                InputStream inputStream = SoundManager.class.getResourceAsStream(currentFile);
                player = Manager.createPlayer(inputStream, currentType);
                player.realize();
                player.prefetch();
                player.setLoopCount(-1);
                ((VolumeControl)player.getControl("VolumeControl")).setLevel(30);
                player.start();
                ((VolumeControl)player.getControl("VolumeControl")).setLevel(30);
                inputStream.close();
            } catch (IOException ex) {
                currentFile = "";
                currentType = "";
            } catch (MediaException ex) {
                currentFile = "";
                currentType = "";
            }
        }
    }
    
    public void stopMusic() {
        if (player != null && player.getState() == Player.STARTED) {
            try {
                player.deallocate();
                player.stop();
                player.close();
                player = null;
            } catch (MediaException ex) { }
        }
    }
}
