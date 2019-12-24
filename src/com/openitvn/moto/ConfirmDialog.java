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

import com.openitvn.game.ImageHelper;
import com.openitvn.game.bounce.Point;
import com.openitvn.game.bounce.Rectangle;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/**
 *
 * @author Thinh Pham
 */
public class ConfirmDialog extends MessageDialog {
    private boolean runCommand;
    private final Image imgNo;
    private final Rectangle btnNo;
    
    public ConfirmDialog(String title, String plainContent, byte command, IDialogHolder holder) {
        super(title, plainContent, command, holder);
        
        //prepare buttons
        Image temp = ImageHelper.loadImage("/images/dialogButton.png");
        imgNo = Image.createImage(14, 14);
        imgNo.getGraphics().drawImage(temp, -18, 0, Graphics.LEFT | Graphics.TOP);
        btnNo = new Rectangle(x + width - 60, y + height - 40, 60, 40);
    }
    
    protected void drawButton(Graphics g) {
        g.drawImage(imgYes, v1.x + CONTENT_MARGIN, v2.y - CONTENT_MARGIN, Graphics.LEFT | Graphics.BOTTOM);
        g.drawImage(imgNo, v2.x - CONTENT_MARGIN, v2.y - CONTENT_MARGIN, Graphics.RIGHT | Graphics.BOTTOM);
    }
    
    protected void dialogClosed() {
        holder.runDialogCommand(runCommand ? getCommand() : COMMAND_NONE);
    }
    
    public void pointerReleased(int x, int y) {
        if(state != STATE_OPEN) return;
        
        Point p = new Point(x, y);
        if(btnYes.contains(p)) {
            runCommand = true;
            state = STATE_ERASING;
        } else if(btnNo.contains(p)) {
            runCommand = false;
            state = STATE_ERASING;
        }
    }
}
