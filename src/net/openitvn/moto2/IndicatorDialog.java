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

/**
 *
 * @author Thinh Pham
 */
public class IndicatorDialog extends Dialog {
    private final String message;
    private StringBuffer show;
    private int timeline = 0;
    private final int stringX;

    public IndicatorDialog(String message, int width, int height, IDialogHolder holder) {
        super(holder);
        this.message = message;
        show = new StringBuffer(message);
        stringX = (Main.SCREENSIZE_WIDTH - Main.FontBold.stringWidth(message + "...")) / 2;
        this.width = width;
        this.height = height;
        x = (Main.SCREENSIZE_WIDTH - width)/2;
        y = (Main.SCREENSIZE_HEIGHT - height)/2;
    }
    
    public void update() {
        super.update();
        
        if(state == STATE_OPEN) {
            timeline++;
            if(timeline < 4) show.append(".");
            else {
                timeline = 0;
                show = new StringBuffer(message);
            }
        }
    }
    
    public void paint(Graphics g) {
        super.paint(g);
        
        if(state == STATE_OPEN) {
            g.drawString(show.toString(), stringX, Main.SCREENSIZE_HEIGHT/2, Graphics.LEFT | Graphics.BASELINE);
        }
    }

    protected void dialogOpened() {
        state = STATE_OPEN;
    }

    protected void dialogClosed() {
        holder.runDialogCommand(COMMAND_NONE);
    }

    public void pointerReleased(int x, int y) { }
//#if TKEY || QWERTY
    public void keyReleased(int keyCode) { }
//#endif
}
