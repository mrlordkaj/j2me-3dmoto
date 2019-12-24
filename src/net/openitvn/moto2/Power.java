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
import net.openitvn.game.bounding.Rectangle;
import javax.microedition.m3g.Mesh;

/**
 *
 * @author Thinh Pham
 */
public class Power {
    public static final byte TYPE_COIN = 0;
    public static final byte TYPE_GEM = 1;
    public static final byte TYPE_CHEST = 2;
    public static final byte TYPE_MAGNET = 3;
    public static final byte TYPE_DOUBLE = 4;
    public static final byte TYPE_FLASH = 5;
    
    private final byte type;
    public byte getType() { return type; }
    private final Mesh mPowerUp;
    private final Point position;
    
    private final PlayScene scene;
    
    public Power(byte powerUpType, int startX, int startZ, int startAngle) {
        scene = PlayScene.getInstance();
        type = powerUpType;
        position = new Point(startX, startZ);
        
        mPowerUp = PlayResource.getInstance().getModelPowerUp(type);
        scene.getWorld().addChild(mPowerUp);
        mPowerUp.setTranslation(position.x, 0, position.y);
        mPowerUp.setOrientation(startAngle, 0, 1, 0);
    }
    
    public void update(int translateZ) {
        if(position.y > -150) {
            if(scene.isMagnetActived() && type == TYPE_COIN && position.y < 100) {
                int racerX = PlayScene.getInstance().getRacer().getPositionX();
                if(position.x < racerX - 8) position.x += 8;
                else if(position.x > racerX + 8) position.x -= 8;
                else if(position.x != racerX) position.x = racerX;
                
                if(position.y > 20) position.y -= 20;
                else if(position.y < -20) position.y += 20;
                else position.y = 0;
            } else {
                position.y += translateZ;
            }
            mPowerUp.setTranslation(position.x, 0, position.y);
            if(Main.isHighVisualEffect()) mPowerUp.postRotate(-15, 0, 1, 0);
        } else removeMe();
    }
    
    public void pickMe(Rectangle racer) {
        if(racer.contains(position)) {
            switch(type) {
                case TYPE_COIN:
                    scene.addCoin();
                    break;
                    
                case TYPE_DOUBLE:
                case TYPE_MAGNET:
                case TYPE_FLASH:
                    scene.activePowerUp(type);
                    break;
                    
                case TYPE_CHEST:
                    scene.getRoad().getPowerUpChance()[Power.TYPE_CHEST] = 0;
                    MessageQueue.addMessage(Message.TYPE_CHEST);
                    scene.chestCollected = true;
                    break;
                    
                case TYPE_GEM:
                    scene.addGem();
                    break;
            }
            removeMe();
        }
    }
    
    private void removeMe() {
        scene.getRoad().getPowerUpList().removeElement(this);
        scene.getWorld().removeChild(mPowerUp);
    }
}
