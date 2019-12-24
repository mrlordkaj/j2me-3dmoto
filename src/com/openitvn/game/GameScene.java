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
package com.openitvn.game;

import com.nokia.mid.ui.DeviceControl;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.game.GameCanvas;
import javax.microedition.midlet.MIDlet;

/**
 *
 * @author Thinh Pham
 */
public abstract class GameScene extends GameCanvas implements Runnable {
//#if QWERTY
//#     public static final int KEY_0 = 32;
//#     public static final int KEY_1 = 114;
//#     public static final int KEY_2 = 116;
//#     public static final int KEY_3 = 121;
//#     public static final int KEY_4 = 102;
//#     public static final int KEY_5 = 103;
//#     public static final int KEY_6 = 104;
//#     public static final int KEY_8 = 98;
//#     public static final int KEY_LF = 113;
//#     public static final int KEY_CF = 119;
//#     public static final int KEY_RF = 101;
//#     public static final int KEY_SHARP = 106;
//#     public static final int KEY_BACK = 8;
//#elif TKEY
//#     public static final int KEY_0 = 48;
//#     public static final int KEY_1 = 49;
//#     public static final int KEY_2 = 50;
//#     public static final int KEY_3 = 51;
//#     public static final int KEY_4 = 52;
//#     public static final int KEY_5 = 53;
//#     public static final int KEY_6 = 54;
//#     public static final int KEY_7 = 55;
//#     public static final int KEY_8 = 56;
//#     public static final int KEY_9 = 57;
//#     public static final int KEY_LF = -6;
//#     public static final int KEY_CF = -5;
//#     public static final int KEY_RF = -7;
//#     public static final int KEY_SHARP = 35;
//#     public static final int KEY_UP = -1;
//#     public static final int KEY_RIGHT = -4;
//#     public static final int KEY_DOWN = -2;
//#     public static final int KEY_LEFT = -3;
//#endif
    
    public static final byte STATE_LOAD = 0;
    
    protected byte state = STATE_LOAD;
    public byte getState() { return state; }
    
    private short clearMemoryTicker;
    private boolean isRunning = true;
    
    private short updateInterval;
    private byte fps;
    public void setFPS(int targetFPS) {
        fps = (byte)targetFPS;
        updateInterval = (short)(1000/fps);
    }
    public byte getFPS() { return fps; };
    
    protected static GameScene instance;
    public static GameScene getAbstractInstance() { return instance; }
    
    private static int backlightLevel = 0;
    public static void setBacklightLevel(int value) { backlightLevel = value; }
    
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
        while(isRunning) {
            try {
                update();
                repaint();
            } catch (Throwable ex) { }
            
            try {
                Thread.sleep(updateInterval);
            } catch (InterruptedException ex) { }
            if(--clearMemoryTicker < 0) {
                System.gc();
                if(backlightLevel > 0) DeviceControl.setLights(0, backlightLevel);
                clearMemoryTicker = (short)(fps*10);
            }
        }
    }
    
    protected abstract void prepareResource();
    protected abstract void update();
    
    public void dispose() {
        isRunning = false;
        instance = null;
        System.gc();
    }
}
