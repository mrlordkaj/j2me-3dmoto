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

import net.openitvn.game.GameScene;
import java.util.Vector;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/**
 *
 * @author Thinh Pham
 */
public class MessageQueue {
    private final Vector stackMessage = new Vector();
    
    private int x;
    private int timeline;
    private byte type;
    private int achievementContentTop;
    private String[] achievementContent;
    private Image content;
    private boolean open = false;
    
    private static MessageQueue instance;
    public static MessageQueue createInstance() {
        instance = new MessageQueue();
        return instance;
    }
    public static MessageQueue getInstance() {
        if(instance == null) instance = new MessageQueue();
        return instance;
    }
    
    private MessageQueue() {
        x = Main.SCREENSIZE_WIDTH + 4;
    }
    
    public static void addMessage(byte messageType) {
        instance.stackMessage.addElement(new Message(messageType));
    }
    
    public static void addAchievement(String achievementContent) {
        instance.stackMessage.addElement(new Message(achievementContent));
    }
    
    public void update() {
        if(open) {
            if(x > Main.SCREENSIZE_WIDTH - 76) x -= 10;
            else if (--timeline == 0) open = false;
        } else {
            if(x < Main.SCREENSIZE_WIDTH + 4) {
                x += 10;
            } else if(stackMessage.size() > 0) {
                Message curMessage = (Message)stackMessage.elementAt(0);
                content = curMessage.getContent();
                type = curMessage.getType();
                if(type == Message.TYPE_ACHIEVEMENT) {
                    achievementContent = curMessage.getAchievementContent();
                    achievementContentTop = (achievementContent.length == 2) ? 106 : 112;
                }
                timeline = GameScene.getAbstractInstance().getFPS()*2;
                open = true;
                stackMessage.removeElementAt(0);
            } else {
                content = null;
            }
        }
    }
    
    public void paint(Graphics g) {
        if(x < Main.SCREENSIZE_WIDTH + 4) {
            g.drawImage(content, x, 80, Graphics.LEFT | Graphics.TOP);
            if(type == Message.TYPE_ACHIEVEMENT) {
                g.setColor(0xffffff);
                g.setFont(Main.FontPlain);
                for(byte i = 0; i < achievementContent.length; i++) {
                    g.drawString(achievementContent[i], x+33, achievementContentTop + i*16, Graphics.HCENTER | Graphics.BASELINE);
                }
            }
        }
    }
}
