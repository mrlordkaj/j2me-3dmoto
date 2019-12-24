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

import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.game.GameCanvas;
import javax.microedition.midlet.MIDlet;

/**
 *
 * @author Thinh Pham
 */
public abstract class GameScene extends GameCanvas implements Runnable {
    public static final byte STATE_LOAD = 0;
    
    protected byte state = STATE_LOAD;
//    private short clearMemoryTicker;
    private boolean isRunning = true;
    private short updateInterval;
    private byte fps;
    
    protected static GameScene instance;
    public static GameScene getAbstractInstance() { return instance; }
    
    public GameScene(MIDlet midlet, int targetFPS) {
        super(false);
        setFullScreenMode(true);
        fps = (byte)targetFPS;
        updateInterval = (short)(1000/fps);
        begin(midlet);
    }
    
    private void begin(MIDlet midlet) {
        instance = this;
        prepareResource();
        Display.getDisplay(midlet).setCurrent(this);
        new Thread(this).start();
    }
    
    public void run() {
        while (isRunning) {
            try {
                update();
                repaint();
            } catch (Throwable ex) { }
            
            try {
                Thread.sleep(updateInterval);
            } catch (InterruptedException ex) { }
//            if (--clearMemoryTicker < 0) {
//                System.gc();
//                clearMemoryTicker = (short)(fps*10);
//            }
        }
    }
    
    protected abstract void prepareResource();
    protected abstract void update();
    
    public void dispose() {
        isRunning = false;
        instance = null;
        System.gc();
    }
    
    public byte getState() {
        return state;
    }
    
    public void setFPS(int targetFPS) {
        fps = (byte)targetFPS;
        updateInterval = (short)(1000/fps);
    }
    
    public final byte getFPS() {
        return fps;
    }
}
