/*
 * Copyright (C) 2013 Thinh Pham
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
package net.openitvn.moto2;

import javax.microedition.m3g.Background;

/**
 *
 * @author Thinh Pham
 */
public class CustomCamera extends javax.microedition.m3g.Camera {
    public static final byte STATE_HIGH = 0;
    public static final byte STATE_LOW = 1;
    public static final byte STATE_DOWN = 2;
    public static final byte STATE_UP = 3;
    
//#if ScreenHeight == 320
    private static final float BACKGROUND_TOP = 100;
    private static final float BACKGROUND_STEP = 5;
//#elif ScreenHeight == 400
//#     private static final float BACKGROUND_TOP = 124;
//#     private static final float BACKGROUND_STEP = 6.2f;
//#endif
    
    private byte state;
    public byte getState() { return state; }
    
    private int timeline;
    private float backgroundY;
    private final Background mBackground;
    
    public CustomCamera(Background mBackground) {
        super();
        
        this.mBackground = mBackground;
    }
    
    public void moveDown() {
        if(state == STATE_HIGH) {
            state = STATE_DOWN;
            timeline = 0;
        }
    }
    
    public void moveUp() {
        if(state == STATE_LOW) {
            state = STATE_UP;
            timeline = 0;
        }
    }
    
    public void reset() {
        setTranslation(0, 70, -70);
        setOrientation(180, 0, 1, 1f/3f);
        state = STATE_HIGH;
        backgroundY = BACKGROUND_TOP;
        mBackground.setCrop(0, (int)backgroundY, Main.SCREENSIZE_WIDTH, Main.SCREENSIZE_HEIGHT);
    }
    
    public void update() {
        switch(state) {
            case STATE_DOWN:
                if(++timeline < 20) {
                    translate(0, -2, 0);
                    setOrientation(180, 0, 1, (1f/3f) - (timeline * 0.01f));
                    backgroundY -= BACKGROUND_STEP;
                } else {
                    setTranslation(0, 30, -70);
                    setOrientation(180, 0, 1, (1f/3f) - 0.2f);
                    backgroundY = 0;
                    state = STATE_LOW;
                }
                mBackground.setCrop(0, (int)backgroundY, Main.SCREENSIZE_WIDTH, Main.SCREENSIZE_HEIGHT);
                break;
                
            case STATE_UP:
                if(++timeline < 20) {
                    translate(0, 2, 0);
                    setOrientation(180, 0, 1, (1f/3f) - 0.2f + (timeline * 0.01f));
                    backgroundY += BACKGROUND_STEP;
                } else {
                    setTranslation(0, 70, -70);
                    setOrientation(180, 0, 1, (1f/3f));
                    backgroundY = BACKGROUND_TOP;
                    state = STATE_HIGH;
                }
                mBackground.setCrop(0, (int)backgroundY, Main.SCREENSIZE_WIDTH, Main.SCREENSIZE_HEIGHT);
                break;
        }
    }
}
