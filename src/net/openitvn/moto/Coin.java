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

import net.openitvn.game.bounding.Point;
import net.openitvn.game.bounding.Rectangle;
import javax.microedition.m3g.Mesh;

/**
 *
 * @author Thinh Pham
 */
public class Coin {
    private final Mesh mPowerUp;
    private final Point position;
    
    private final PlayScene scene;
    
    public Coin(int startX, int startZ) {
        scene = PlayScene.getInstance();
        position = new Point(startX, startZ);
        
        mPowerUp = PlayResource.getInstance().getModelCoin();
        scene.getWorld().addChild(mPowerUp);
        mPowerUp.setTranslation(position.x, 0, position.y);
    }
    
    public void update(int translateZ) {
        if(position.y > -150) {
            position.y += translateZ;
            mPowerUp.setTranslation(position.x, 0, position.y);
        } else removeMe();
    }
    
    public void pickMe(Rectangle racer) {
        if(racer.contains(position)) {
            scene.addCoin();
            removeMe();
        }
    }
    
    private void removeMe() {
        scene.getRoad().getCoinList().removeElement(this);
        scene.getWorld().removeChild(mPowerUp);
    }
}
