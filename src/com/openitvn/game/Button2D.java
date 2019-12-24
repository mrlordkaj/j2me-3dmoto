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

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;

/**
 *
 * @author Thinh Pham
 */
public class Button2D {
    private final Sprite sprite;
    private final int x, y, width, height;
    
    private final byte cmd;
    public byte getCommand() { return cmd; }
    
    public Button2D(Image img, byte cmd, int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.cmd = cmd;
        this.sprite = new Sprite(img, width, height);
    }
    
    public void paint(Graphics g) {
        sprite.setPosition(x, y);
        sprite.paint(g);
    }
    
    public boolean testHit(int x, int y) {
        boolean active = checkHit(x, y);
        sprite.setFrame(active ? 1 : 0);
        return active;
    }
    
    public boolean checkHit(int x, int y) {
        return (x > this.x && x < this.x + this.width && y > this.y && y < this.y + this.height);
    }
    
    public void forceInactive() {
        sprite.setFrame(0);
    }
}
