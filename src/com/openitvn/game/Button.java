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

/**
 *
 * @author Thinh Pham
 */
public class Button {
    public static final int DOWN_OFFSET = 5;
    
    public int x, y;
    private final Image image;
    private final int width, height;
    
    public boolean active;
    
    public Button(Image image, int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.image = image;
        active = false;
    }
    
    public void paint(Graphics g) {
        g.drawImage(image, x, active ? y + DOWN_OFFSET : y, Graphics.LEFT | Graphics.TOP);
    }
    
    public boolean testHit(int x, int y) {
        active = (x > this.x && x < this.x + this.width && y > this.y && y < this.y + this.height);
        return active;
    }
    
    public boolean gotClick(int x, int y) {
        boolean clicked = (active && x > this.x && x < this.x + this.width && y > this.y && y < this.y + this.height);
        if(clicked) active = false;
        return clicked;
    }
    
    public boolean gotClick() {
        if(active) {
            active = false;
            return true;
        } else {
            return false;
        }
    }
}
