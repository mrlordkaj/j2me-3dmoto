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

import javax.microedition.lcdui.Image;

/**
 *
 * @author Thinh Pham
 */
public class Message {
    public static final byte TYPE_GEM = 0;
    public static final byte TYPE_CHEST = 1;
    public static final byte TYPE_RECORD = 2;
    public static final byte TYPE_LEVEL = 3;
    public static final byte TYPE_ACHIEVEMENT = 4;
    
    private final byte type;
    public byte getType() { return type; }
    private final Image content;
    public Image getContent() { return content; }
    private String[] achievementContent;
    public String[] getAchievementContent() { return achievementContent; }
    
    public Message(byte type) {
        this.type = type;
        switch(type) {
            case TYPE_RECORD:
                content = PlayResource.getInstance().getImageMessageRecord();
                break;
                
            case TYPE_LEVEL:
                content = PlayResource.getInstance().getImageMessageLevel();
                break;
                
            default:
                content = null;
                break;
        }
    }
}
