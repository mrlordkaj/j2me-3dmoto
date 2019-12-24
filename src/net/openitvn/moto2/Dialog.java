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

import net.openitvn.game.bounding.Point;
import javax.microedition.lcdui.Graphics;

/**
 *
 * @author Thinh Pham
 */
public abstract class Dialog {
    public static final byte COMMAND_NONE = 0;
    
    public static final byte STATE_OPENING = 0;
    public static final byte STATE_OPEN = 1;
    public static final byte STATE_CLOSING = 2;
    
    private static final byte STATE_EXPAND_STEP = 20;
    
    protected Point v1, v2;
    protected int x, y, width, height;
    protected int centerX, centerY;
    
    protected byte state = STATE_OPENING;
    private boolean expandingWidth = true;
    public byte getState() { return state; }
    
    protected final IDialogHolder holder;
    
    public Dialog(IDialogHolder holder) {
        this.holder = holder;
        
        centerX = Main.SCREENSIZE_WIDTH/2;
        centerY = Main.SCREENSIZE_HEIGHT/2;
        
        v1 = new Point(centerX-3, centerY-3);
        v2 = new Point(centerX+3, centerY+3);
    }
    
    public void update() {
        switch(state) {
            case STATE_OPENING:
                if(expandingWidth) {
                    if(v1.x > x + STATE_EXPAND_STEP) {
                        v1.x -= STATE_EXPAND_STEP;
                        v2.x += STATE_EXPAND_STEP;
                    } else {
                        v1.x = x;
                        v2.x = x + width;
                        expandingWidth = false;
                    }
                } else {
                    if(v1.y > y + STATE_EXPAND_STEP) {
                        v1.y -= STATE_EXPAND_STEP;
                        v2.y += STATE_EXPAND_STEP;
                    } else {
                        v1.y = y;
                        v2.y = y + height;
                        dialogOpened();
                    }
                }
                break;
                
            case STATE_CLOSING:
                if(!expandingWidth) {
                    if(v1.y < centerY - 3 - STATE_EXPAND_STEP) {
                        v1.y += STATE_EXPAND_STEP;
                        v2.y -= STATE_EXPAND_STEP;
                    } else {
                        v1.y = centerY - 3;
                        v2.y = centerY + 3;
                        expandingWidth = true;
                    }
                } else {
                    if(v1.x < centerX - 3 - STATE_EXPAND_STEP) {
                        v1.x += STATE_EXPAND_STEP;
                        v2.x -= STATE_EXPAND_STEP;
                    } else {
                        v1.x = centerX - 3;
                        v2.x = centerX + 3;
                        dialogClosed();
                    }
                }
                break;
        }
    }
    
    public void paint(Graphics g) {
        g.setFont(Main.FontBold);
        g.setColor(0x2c2c2c);
        g.fillRect(v1.x, v1.y, v2.x - v1.x, v2.y - v1.y);
        g.setColor(0x000000);
        g.drawRect(v1.x-1, v1.y-1, v2.x - v1.x+1, v2.y - v1.y+1);
        g.setColor(0xffffff);
        g.fillRect(v1.x, v1.y, 6, 2);
        g.fillRect(v1.x, v1.y, 2, 6);
        g.fillRect(v2.x-6, v1.y, 6, 2);
        g.fillRect(v2.x-2, v1.y, 2, 6);
        g.fillRect(v1.x, v2.y-2, 6, 2);
        g.fillRect(v1.x, v2.y-6, 2, 6);
        g.fillRect(v2.x-6, v2.y-2, 6, 2);
        g.fillRect(v2.x-2, v2.y-6, 2, 6);
    }
    
    public void forceClose() {
        state = STATE_CLOSING;
    }
    
    protected abstract void dialogOpened();
    protected abstract void dialogClosed();
    public abstract void pointerReleased(int x, int y);
//#if TKEY || QWERTY
    public abstract void keyReleased(int keyCode);
//#endif
}
