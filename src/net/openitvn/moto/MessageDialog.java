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
package net.openitvn.moto;

import net.openitvn.game.ImageHelper;
import net.openitvn.game.StringHelper;
import net.openitvn.game.bounding.Rectangle;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/**
 *
 * @author Thinh Pham
 */
public class MessageDialog extends Dialog {
    protected static final byte CONTENT_MARGIN = 6;
    protected static final byte STATE_WRITING = 3;
    protected static final byte STATE_ERASING = 4;
    
    private final String title;
    private final String[] content;
    private final byte command;
    public byte getCommand() { return command; }
    
    protected final Image imgYes;
    protected final Rectangle btnYes;
    private byte writeLine = 0;
    
    public MessageDialog(String title, String plainContent, byte command, IDialogHolder holder) {
        super(holder);
        this.title = title;
        this.content = StringHelper.split("|", plainContent);
        this.command = command;
        
        int tempWidth = Main.FontBold.stringWidth(title);
        int lineWidth;
        for(byte i = 0; i < content.length; i++) {
            lineWidth = Main.FontBold.stringWidth(content[i]);
            if(lineWidth > tempWidth) tempWidth = lineWidth;
        }
        width = tempWidth + CONTENT_MARGIN*2;
//#if ScreenHeight == 400
        height = (int)((content.length+3.5f)*(Main.FontBold.getHeight()+5)) + CONTENT_MARGIN*2;
//#else
//#         height = (int)((content.length+3.5f)*Main.FontBold.getHeight()) + CONTENT_MARGIN*2;
//#endif
        x = (Main.SCREENSIZE_WIDTH - width)/2;
        y = (Main.SCREENSIZE_HEIGHT - height)/2;
        
        //prepare buttons
        Image temp = ImageHelper.loadImage("/images/dialogButton.png");
        imgYes = Image.createImage(18, 14);
        imgYes.getGraphics().drawImage(temp, 0, 0, Graphics.LEFT | Graphics.TOP);
        btnYes = new Rectangle(x, y + height - 40, 60, 40);
    }
    
    public void update() {
        super.update();
        
        switch(state) {
            case STATE_WRITING:
                if(writeLine < content.length+3) writeLine++;
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
        
        if(writeLine > 0) g.drawString(title, Main.SCREENSIZE_WIDTH/2, y+16, Graphics.BASELINE | Graphics.HCENTER);
//#if ScreenHeight == 400
        int lineHeight = g.getFont().getHeight()+5;
//#else
//#         int lineHeight = g.getFont().getHeight();
//#endif
        int posX = x + CONTENT_MARGIN;
        int posY = y + (int)(lineHeight*1.4f) + CONTENT_MARGIN;
        for(byte i = 1; i < writeLine-2; i++) {
            posY += lineHeight;
            g.drawString(content[i-1], posX, posY, Graphics.BASELINE | Graphics.LEFT);
        }
        if(writeLine > content.length+2) drawButton(g);
    }
    
    protected void drawButton(Graphics g) {
        g.drawImage(imgYes, v1.x + CONTENT_MARGIN, v2.y - CONTENT_MARGIN, Graphics.LEFT | Graphics.BOTTOM);
    }
    
    public void pointerReleased(int x, int y) {
        if(state != STATE_OPEN) return;
        if(btnYes.contains(x, y)) state = STATE_ERASING;
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
