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
import java.util.Vector;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.m3g.Texture2D;

/**
 *
 * @author Thinh Pham
 */
public class RacerTomahawk extends Racer {
    private final Texture2D texBull;
    private final Image imgLeftActive, imgRightActive;
    
    //setting parameters
    //kick
    private short kickTime;
    
    //calculated parameters
    private float maxSkewAngle;
    
    //control paramaters
    //drive
    private float skewAngle = 0;
    //kick
    private short kickTick;
    
    public RacerTomahawk() {
        super();
        
        PlayResource resource = PlayResource.getInstance();
        texBull = resource.getTextureSkill();
        imgLeftActive = resource.getImageControlLeftActive();
        imgRightActive = resource.getImageControlRightActive();
    }
    
    public void initialize() {
        String upgradeData = Profile.getInstance().getSetting(Profile.RECORD_BIKE_TOMAHAWK_UPGRADE);
        //handle
        initHandle(Integer.parseInt(upgradeData.substring(0, 1)));
        //charger
        initSkillCharger(Integer.parseInt(upgradeData.substring(1, 2)));
        //skill
        initSkill(Integer.parseInt(upgradeData.substring(2, 3)));
        
        //calculate skew handle
        maxSkewAngle = handleAngle*skewNumFrame;
    }
    
    protected void initSkill(int skillLevel) {
        switch(skillLevel) {
            case 5:
                kickTime = (short)(20*scene.getFPS());
                break;
                
            case 4:
                kickTime = (short)(16*scene.getFPS());
                break;
                
            case 3:
                kickTime = (short)(12*scene.getFPS());
                break;
                
            case 2:
                kickTime = (short)(8*scene.getFPS());
                break;
                
            default:
                kickTime = (short)(5*scene.getFPS());
                break;
        }
    }
    
    public void beginGo() {
        positionX = 0;
        skewAngle = 0;
        activeSkill = false;
        mRacer.setTranslation(positionX, 0, 0);
        mRacer.setOrientation(0, 1, 1, 1);
        state = STATE_MOVE_STRAIGHT;
    }
    
    public void update() {
        super.update();
        
        if(!denyControl()) {
            //pick pu
            pickPowerUp(24);
            
            //invisible down or not
            if(activeSkill) {
                kickTick--;
                skillBarWidth = (int)(30.f * ((float)kickTick / (float)kickTime));
                switch(kickTick) {
                    case 15:
                    case 9:
                    case 3:
                        mDefaultBike.getAppearance(0).setTexture(0, texBike);
                        mDefaultHuman.getAppearance(0).setTexture(0, texHuman);
                        break;
                        
                    case 12:
                    case 6:
                        mDefaultBike.getAppearance(0).setTexture(0, texBull);
                        mDefaultHuman.getAppearance(0).setTexture(0, texBull);
                        break;
                        
                    case 0:
                        activeSkill = false;
                        break;
                }
                //out road
                if(positionX > 42 || positionX < -42) setupDie();
                else checkCollision();
            } else {
                chargerSkill();
                
                //check collision
                if(haveAccident()) {
                    setupDie();
                    return;
                }
            }
            
            if(!activeFlash) {
                //speed up to max
                int speed = getSpeed();
                int maxSpeed = getMaxSpeed();
                if(speed < maxSpeed - ACCELERATION) setSpeed(speed + ACCELERATION);
                else if(speed != maxSpeed) setSpeed(maxSpeed);
            }
        }
        
        switch(state) {
            case STATE_READY:
                updateReady();
                break;
                
            case STATE_MOVE_LEFT:
                //dieu chinh do nghieng
                if(skewAngle < maxSkewAngle - handleAngle) {
                    skewAngle += handleAngle;
                    mRacer.setOrientation(skewAngle, 0, 1, -1);
                } else if(skewAngle < maxSkewAngle) {
                    skewAngle = maxSkewAngle;
                    mRacer.setOrientation(skewAngle, 0, 1, -1);
                }
                
                //dieu chinh vi tri
                positionX += skewAngle*handleFactor;
                mRacer.setTranslation(positionX, 0, 0);
                break;
                
            case STATE_MOVE_RIGHT:
                //dieu chinh do nghieng
                if(skewAngle > -maxSkewAngle + handleAngle) {
                    skewAngle -= handleAngle;
                    mRacer.setOrientation(skewAngle, 0, 1, -1);
                } else if(skewAngle > -maxSkewAngle) {
                    skewAngle = -maxSkewAngle;
                    mRacer.setOrientation(skewAngle, 0, 1, -1);
                }
                
                //dieu chinh vi tri
                positionX += skewAngle*handleFactor;
                mRacer.setTranslation(positionX, 0, 0);
                break;
                
            case STATE_MOVE_LEFT_STRAIGHT:
                //dieu chinh do nghieng
                if(skewAngle > handleAngle) {
                    skewAngle -= handleAngle;
                    mRacer.setOrientation(skewAngle, 0, 1, -1);
                } else moveStraight();
                
                //dieu chinh vi tri
                positionX += skewAngle*handleFactor;
                mRacer.setTranslation(positionX, 0, 0);
                break;
                
            case STATE_MOVE_RIGHT_STRAIGHT:
                //dieu chinh do nghieng
                if(skewAngle < -handleAngle) {
                    skewAngle += handleAngle;
                    mRacer.setOrientation(skewAngle, 0, 1, -1);
                } else moveStraight();
                
                //dieu chinh vi tri
                positionX += skewAngle*handleFactor;
                mRacer.setTranslation(positionX, 0, 0);
                break;
                
            case STATE_DIE:
                updateDie();
                break;
        }
    }
    
    public void paint(Graphics g) {
        super.paint(g);
        
        if(!denyControl()) {
            g.drawImage(state == STATE_MOVE_LEFT ? imgLeftActive : imgLeft, 0, Main.SCREENSIZE_HEIGHT, Graphics.LEFT | Graphics.BOTTOM);
            g.drawImage(state == STATE_MOVE_RIGHT ? imgRightActive : imgRight, Main.SCREENSIZE_WIDTH, Main.SCREENSIZE_HEIGHT, Graphics.RIGHT | Graphics.BOTTOM);
        }
    }
    
    private void checkCollision() {
        Vector vehicleList = PlayScene.getInstance().getTraffic().getVehicleList();
        Point p = new Point((int)positionX, 0);
        for(byte i = 0; i < vehicleList.size(); i++) {
            Vehicle curVehicle = (Vehicle)vehicleList.elementAt(i);
            if(curVehicle.contains(p)) {
                if(curVehicle.getY() > -curVehicle.getPivot().y) {
                    curVehicle.beKicked((curVehicle.getX() + curVehicle.getPivot().x > 0) ? Vehicle.STATE_DIE_BOTTOM_LEFT : Vehicle.STATE_DIE_BOTTOM_RIGHT);
                } else {
                    switch(state) {
                        case STATE_MOVE_LEFT:
                        case STATE_MOVE_LEFT_STRAIGHT:
                            curVehicle.beKicked(Vehicle.STATE_DIE_LEFT);
                            break;

                        case STATE_MOVE_RIGHT:
                        case STATE_MOVE_RIGHT_STRAIGHT:
                            curVehicle.beKicked(Vehicle.STATE_DIE_RIGHT);
                            break;
                    }
                }
                Achievement.getInstance().triggerTheDestroyer();
            }
        }
    }
    
    private void moveStraight() {
        skewAngle = 0;
        mRacer.setOrientation(0, 1, 1, 1);
        state = STATE_MOVE_STRAIGHT;
    }
    
    protected void pressLeft() {
        state = STATE_MOVE_LEFT;
    }
    
    protected void releaseLeft() {
        if(state == STATE_MOVE_LEFT) state = STATE_MOVE_LEFT_STRAIGHT;
    }
    
    protected void pressRight() {
        state = STATE_MOVE_RIGHT;
    }
    
    protected void releaseRight() {
        if(state == STATE_MOVE_RIGHT) state = STATE_MOVE_RIGHT_STRAIGHT;
    }
    
    protected boolean activeSkill() {
        kickTick = kickTime;
        mDefaultBike.getAppearance(0).setTexture(0, texBull);
        mDefaultHuman.getAppearance(0).setTexture(0, texBull);
        return true;
    }
    
    protected boolean destroySkill() {
        activeSkill = false;
        return true;
    }
    
    public int getDurability() {
        return Integer.parseInt(Profile.getInstance().getSetting(Profile.RECORD_BIKE_TOMAHAWK_DURABILITY));
    }

    protected void receiveDamage() {
        int durability = getDurability() - ((getSpeed() - 90)/6 + 2);
        if(durability < 1) durability = 1;
        Profile.getInstance().storeSetting(Profile.RECORD_BIKE_TOMAHAWK_DURABILITY, Integer.toString(durability));
    }
    
    public void fix() {
        Profile.getInstance().storeSetting(Profile.RECORD_BIKE_TOMAHAWK_DURABILITY, "100");
    }
}
