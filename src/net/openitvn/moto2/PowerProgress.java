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

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/**
 *
 * @author Thinh Pham
 */
public class PowerProgress {
    private byte type;
    public byte getType() { return type; }
    private final Image imgBackground;
    
    private int totalTime, timeLeft;
    private final int top;
    private int left;
    private final byte slot;
    
    public PowerProgress(byte powerUpType, int maxTime, byte slot) {
        type = powerUpType;
        totalTime = timeLeft = maxTime;
        left = -70;
        this.slot = slot;
        top = slot*30+50;
        
        switch(type) {
            case Power.TYPE_FLASH:
                imgBackground = PlayResource.getInstance().getImageFlashBar();
                break;
                
            case Power.TYPE_DOUBLE:
                imgBackground = PlayResource.getInstance().getImageDoubleBar();
                break;
                
            case Power.TYPE_MAGNET:
                imgBackground = PlayResource.getInstance().getImageMagnetBar();
                break;
                
            default:
                imgBackground = null;
                break;
        }
    }
    
    public void recharge(int newTime) {
        timeLeft = totalTime = newTime;
    }
    
    public void update() {
        if(timeLeft > 0) {
            if(left < 10) left += 20;
        } else {
            if(left > -70) left -= 20;
            else PlayScene.getInstance().removeSlot(slot);
        }
        timeLeft--;
    }
    
    public void paint(Graphics g) {
        g.setColor(0x00cfdc);
        g.fillRect(left + 20, top + 6, (int)(timeLeft*40.f/totalTime), 10);
        g.drawImage(imgBackground, left, top, Graphics.TOP | Graphics.LEFT);
    }
}
