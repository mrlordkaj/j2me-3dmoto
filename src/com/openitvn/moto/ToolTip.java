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
package com.openitvn.moto;

import com.openitvn.game.StringHelper;
import com.openitvn.game.bounce.Point;
import javax.microedition.lcdui.Graphics;

/**
 *
 * @author Thinh Pham
 */
public class ToolTip extends Dialog {
    protected static final byte CONTENT_MARGIN = 6;
    protected static final byte STATE_WRITING = 3;
    protected static final byte STATE_ERASING = 4;
    
    private final String[] content;
    private final byte command;
    public byte getCommand() { return command; }
    
    private byte writeLine = 0;
    
    public ToolTip(String plainContent, int centerX, int centerY, byte command, IDialogHolder holder) {
        super(holder);
        this.content = StringHelper.split("|", plainContent);
        this.command = command;
        this.centerX = centerX;
        this.centerY = centerY;
        
        int tempWidth = 0;
        int lineWidth;
        for(byte i = 0; i < content.length; i++) {
            lineWidth = Main.FontBold.stringWidth(content[i]);
            if(lineWidth > tempWidth) tempWidth = lineWidth;
        }
        width = tempWidth + CONTENT_MARGIN*2;
        height = content.length*Main.FontBold.getHeight() + CONTENT_MARGIN*2;
        x = centerX - width/2;
        y = centerY - height/2;
        
        v1 = new Point(centerX-3, centerY-3);
        v2 = new Point(centerX+3, centerY+3);
        
    }
    
    public void update() {
        super.update();
        
        switch(state) {
            case STATE_WRITING:
                if(writeLine < content.length) writeLine++;
                else state = STATE_OPEN;
                break;
                
            case STATE_ERASING:
                if(writeLine > 0) writeLine--;
                else state = STATE_CLOSING;
                break;
        }
    }
    
    public void paint(Graphics g) {
        super.paint(g);
        
        int lineHeight = g.getFont().getHeight();
        int posY = y;
        for(byte i = 0; i < writeLine; i++) {
            posY += lineHeight;
            g.drawString(content[i], centerX, posY, Graphics.BASELINE | Graphics.HCENTER);
        }
    }
    
    public void pointerReleased(int x, int y) {
        if(state != STATE_OPEN) return;
        
        state = STATE_ERASING;
    }
    
    public void keyReleased(int keyCode) {
        if(state != STATE_OPEN) return;
        
        state = STATE_ERASING;
    }
    
    public void forceClose() {
        state = STATE_ERASING;
    }

    protected void dialogOpened() {
        state = STATE_WRITING;
    }
    
    protected void dialogClosed() {
        holder.runDialogCommand(command);
    }
}
