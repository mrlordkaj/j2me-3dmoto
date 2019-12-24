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
import java.util.Vector;
import javax.microedition.lcdui.Graphics;
import javax.microedition.m3g.Mesh;
import javax.microedition.m3g.Node;

/**
 *
 * @author Thinh Pham
 */
public class RacerLizard extends Racer {
    private static final byte STATE_FLYING_UP = 8;
    private static final byte STATE_FLYING_DOWN = 9;
    private static final byte STATE_FLYING_STRAIGHT = 10;
    
    //setting parameters
    //fly
    private byte delayFlyFrame;
    private short flyLength;
    
    //calculated parameters
    private float skewLength, maxVelocity, maxSkewAngle;
    private short flyTime;
    private float rotateDownStep;
    
    //control paramaters
    //drive
    private short lane = 0, slopeLane;
    private byte stackState;
    private float skewAngle = 0;
    private float targetX, pseudoTargetX;
    //fly
    private boolean cancelFly;
    private short flyTimeline, flyTick;
    private float upStep, flyingDownAngle;
    private Mesh mSlope;
    private Vehicle targetVehicle;
    
    private final float[] xyz = new float[3];
    
    public RacerLizard() {
        super();
    }
    
    public void initialize() {
        String upgradeData = Profile.getInstance().getSetting(Profile.RECORD_BIKE_LIZARD_UPGRADE);
        //handle
        initHandle(Integer.parseInt(upgradeData.substring(0, 1)));
        //charger
        initSkillCharger(Integer.parseInt(upgradeData.substring(1, 2)));
        //skill
        initSkill(Integer.parseInt(upgradeData.substring(2, 3)));
        
        //calculate skew handle
        float length = 0, velocity = 0;
        for(byte i = 0; i < skewNumFrame; i++) {
            velocity += handleAngle*handleFactor;
            length += velocity;
        }
        skewLength = length;
        maxVelocity = velocity;
        maxSkewAngle = handleAngle*skewNumFrame;
    }
    
    protected void initSkill(int skillLevel) {
        switch(skillLevel) {
            case 5:
                delayFlyFrame = (byte)(4);
                flyLength = 160;
                break;
                
            case 4:
                delayFlyFrame = (byte)(4);
                flyLength = 100;
                break;
                
            case 3:
                delayFlyFrame = (byte)(0.25f*scene.getFPS() + 4);
                flyLength = 100;
                break;
                
            case 2:
                delayFlyFrame = (byte)(0.25f*scene.getFPS() + 4);
                flyLength = 80;
                break;
                
            default:
                delayFlyFrame = (byte)(0.5f*scene.getFPS() + 4);
                flyLength = 80;
                break;
        }
    }
    
    public void beginGo() {
        positionX = 0;
        skewAngle = 0;
        lane = 0;
        targetX = 0;
        pseudoTargetX = 0;
        activeSkill = false;
        mRacer.setTranslation(positionX, 0, 0);
        mRacer.setOrientation(0, 1, 1, 1);
        state = STATE_MOVE_STRAIGHT;
        stackState = STATE_MOVE_STRAIGHT;
    }
    
    public void update() {
        super.update();
        
        if(!denyControl()) {
            if(activeSkill) {
                //rise the slope
                if(mSlope != null) {
                    mSlope.getTranslation(xyz);
                    if(xyz[2] > -150) {
                        if(cancelFly) mSlope.translate(0, -4, -getUnitSpeed());
                        else mSlope.translate(0, xyz[1] < 0 ? 4 : 0, -getUnitSpeed());
                    } else {
                        mWorld.removeChild(mSlope);
                        mSlope = null;
                        if(cancelFly) {
                            //cancel fly
                            sprSkill.setFrame(1);
                            skillChargerTick = getSkillChargerTime();
                            activeSkill = false;
                        }
                    }
                }
                
                //when move on slope
                flyTimeline++;
                if(state == STATE_MOVE_STRAIGHT
                        && lane == slopeLane
                        && flyTimeline == delayFlyFrame-2
                        && !activeFlash) {
                    Achievement.getInstance().triggerLikeABird();
                    state = STATE_FLYING_UP;
                }
                
                if(state != STATE_DIE) {
                    //pick-up coin
                    if(state == STATE_FLYING_UP || state == STATE_FLYING_DOWN && scene.isMagnetActived()) pickCoin();
                    
                    //check collision
                    mRacer.getTranslation(xyz);
                    if(haveAccident(xyz[1])) {
                        setupDie();
                        mWorld.removeChild(mSlope);
                        mSlope = null;
                    }
                }
            } else {
                if(!activeFlash) {
                    //speed up to max
                    int speed = getSpeed();
                    int maxSpeed = getMaxSpeed();
                    if(speed < maxSpeed - ACCELERATION) setSpeed(speed + ACCELERATION);
                    else if(speed != maxSpeed) setSpeed(maxSpeed);
                }
            
                chargerSkill();
                
                //pickup pu
                pickPowerUp(10);
                
                //check collision
                if(haveAccident()) {
                    setupDie();
                    return;
                }
            }
        }
        
        switch(state) {
            case STATE_READY:
                updateReady();
                break;
                
            case STATE_FLYING_UP:
                //moto fly
                if(flyTimeline < delayFlyFrame) {
                    mRacer.postRotate(-15, 1, 0, 0);
                    mRacer.translate(0, upStep, 0);
                } else if (upStep > 0) {
                    mRacer.postRotate(rotateDownStep, 1, 0, 0);
                    mRacer.translate(0, upStep, 0);
                    upStep -= 1;
                } else {
                    mRacer.postRotate(rotateDownStep, 1, 0, 0);
                    //down skill bar
                    flyTick--;
                    skillBarWidth = (int)(30.f * ((float)flyTick / (float)flyTime));
                    if(flyTick == 0) state = STATE_FLYING_DOWN;
                }
                break;
                
            case STATE_FLYING_DOWN:
                mRacer.getTranslation(xyz);
                if(xyz[1] > 5) {
                    upStep += 0.2f;
                    mRacer.postRotate(rotateDownStep, 1, 0, 0);
                    mRacer.translate(0, -upStep, 0);
                } else {
                    mRacer.setOrientation(1, 1, 0, 0);
                    mRacer.setTranslation(positionX, 0, 0);
                    flyingDownAngle = -6;
                    state = STATE_FLYING_STRAIGHT;
                    activeSkill = false;
                }
                break;
                
            case STATE_FLYING_STRAIGHT:
                if(flyingDownAngle < 6) {
                    mRacer.postRotate(flyingDownAngle, 1, 0, 0);
                    flyingDownAngle += 3;
                } else moveStraight();
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
                if(positionX < pseudoTargetX - maxVelocity) {
                    positionX += skewAngle*handleFactor;
                } else {
                    positionX = pseudoTargetX;
                    state = STATE_MOVE_LEFT_STRAIGHT;
                }
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
                if(positionX > pseudoTargetX + maxVelocity) {
                    positionX += skewAngle*handleFactor;
                } else {
                    positionX = pseudoTargetX;
                    state = STATE_MOVE_RIGHT_STRAIGHT;
                }
                mRacer.setTranslation(positionX, 0, 0);
                break;
                
            case STATE_MOVE_LEFT_STRAIGHT:
                //dieu chinh vi tri
                if(positionX < targetX - skewAngle*handleFactor) positionX += skewAngle*handleFactor;
                else moveStraight();
                mRacer.setTranslation(positionX, 0, 0);
                
                //dieu chinh do nghieng
                if(skewAngle > handleAngle) {
                    skewAngle -= handleAngle;
                    mRacer.setOrientation(skewAngle, 0, 1, -1);
                }
                break;
                
            case STATE_MOVE_RIGHT_STRAIGHT:
                //dieu chinh vi tri
                if(positionX > targetX - skewAngle*handleFactor) positionX += skewAngle*handleFactor;
                else moveStraight();
                mRacer.setTranslation(positionX, 0, 0);
                
                //dieu chinh do nghieng
                if(skewAngle < -handleAngle) {
                    skewAngle += handleAngle;
                    mRacer.setOrientation(skewAngle, 0, 1, -1);
                }
                break;
                
            case STATE_DIE:
                updateDie();
                if(activeSkill) {
                    byte needDown = 2;
                    short height = checkCollision(mHuman, xyz);
                    if(height > 0) mHuman.setTranslation(xyz[0], height, xyz[2]);
                    else if(xyz[1] > 3) mHuman.translate(0, -3, 0);
                    else needDown--;
                    height = checkCollision(mBike, xyz);
                    if(height > 0) mBike.setTranslation(xyz[0], height, xyz[2]);
                    else if(xyz[1] > 3) mBike.translate(0, -3, 0);
                    else needDown--;
                    if(needDown == 0) activeSkill = false;
                }
                break;
        }
    }
    
    protected void pickCoin() {
        Vector powerUpList = scene.getRoad().getPowerUpList();
        Rectangle bounce = new Rectangle((int)positionX-5, -(getUnitSpeed()/2)-1, 10, getUnitSpeed()+1);
        for(int i = powerUpList.size()-1; i >= 0; i--) {
            Power curPowerUp = (Power)powerUpList.elementAt(i);
            if(curPowerUp.getType() == Power.TYPE_COIN) curPowerUp.pickMe(bounce);
        }
    }
    
    private boolean haveAccident(float height) {
        Vector vehicleList = PlayScene.getInstance().getTraffic().getVehicleList();
        Point p = new Point((int)positionX, 0);
        for(byte i = 0; i < vehicleList.size(); i++) {
            targetVehicle = (Vehicle)vehicleList.elementAt(i);
            if(targetVehicle.contains(p)) {
                float minHeight = 0;
                switch(targetVehicle.getTypeId()) {
                    case Vehicle.TYPE_TAXI:
                        minHeight = 14;
                        break;
                        
                    case Vehicle.TYPE_TRUCK:
                        minHeight = 16;
                        break;
                        
                    case Vehicle.TYPE_CONTAINER:
                        minHeight = 22;
                        break;
                        
                    case Vehicle.TYPE_CAR:
                        minHeight = 18;
                        break;
                }
                return height < minHeight;
            }
        }
        
        return false;
    }
    
    private short checkCollision(Node model, float[] xyz) {
        model.getTranslation(xyz);
        
        if(targetVehicle != null) {
            Point p = new Point((int)xyz[0], (int)xyz[2]);
            if(targetVehicle.contains(p)) {
                switch(targetVehicle.getTypeId()) {
                    case Vehicle.TYPE_TAXI:
                        return 14;
                        
                    case Vehicle.TYPE_TRUCK:
                        return 16;
                        
                    case Vehicle.TYPE_CONTAINER:
                        return 22;
                        
                    case Vehicle.TYPE_CAR:
                        return 18;
                }
            }
        }
        
        return -1;
    }
    
    public void paint(Graphics g) {
        super.paint(g);
        
        if(state == STATE_MOVE_STRAIGHT) {
            g.drawImage(imgLeft, 0, Main.SCREENSIZE_HEIGHT, Graphics.LEFT | Graphics.BOTTOM);
            g.drawImage(imgRight, Main.SCREENSIZE_WIDTH, Main.SCREENSIZE_HEIGHT, Graphics.RIGHT | Graphics.BOTTOM);
        }
    }
    
    private void moveStraight() {
        positionX = targetX;
        skewAngle = 0;
        mRacer.setOrientation(0, 1, 1, 1);
        state = STATE_MOVE_STRAIGHT;
        if(stackState == STATE_MOVE_LEFT)  pressLeft();
        else if(stackState == STATE_MOVE_RIGHT) pressRight();
    }
    
    protected void pressLeft() {
        if(denyControl()) return;
        
        if(state == STATE_MOVE_STRAIGHT || state == STATE_FLYING_STRAIGHT) {
            if(lane < 2) {
                lane++;
                targetX = lane*Lane.LANE_WIDTH;
                pseudoTargetX = targetX - skewLength;
                state = STATE_MOVE_LEFT;
                stackState = STATE_MOVE_STRAIGHT;
                if(activeSkill) cancelFly = true;
            }
        } else if (state != STATE_FLYING_UP) {
            stackState = (stackState == STATE_MOVE_RIGHT) ? STATE_MOVE_STRAIGHT : STATE_MOVE_LEFT;
        }
    }
    
    protected void pressRight() {
        if(denyControl()) return;
        
        if(state == STATE_MOVE_STRAIGHT || state == STATE_FLYING_STRAIGHT) {
            if(lane > -2) {
                lane--;
                targetX = lane*Lane.LANE_WIDTH;
                pseudoTargetX = targetX + skewLength;
                state = STATE_MOVE_RIGHT;
                stackState = STATE_MOVE_STRAIGHT;
                if(activeSkill) cancelFly = true;
            }
        } else if (state != STATE_FLYING_UP) {
            stackState = (stackState == STATE_MOVE_LEFT) ? STATE_MOVE_STRAIGHT : STATE_MOVE_RIGHT;
        }
    }
    
    protected boolean activeSkill() {
        if(state == STATE_MOVE_STRAIGHT) {
            cancelFly = false;
            slopeLane = lane;
            mSlope = PlayResource.getInstance().getModelSlope();
            mSlope.setTranslation(slopeLane*28, -16, getUnitSpeed()*delayFlyFrame);
            mWorld.addChild(mSlope);
            upStep = 6;
            flyTimeline = 0;
            flyTick = flyTime = (short)(flyLength / getUnitSpeed());
            rotateDownStep = 62.f / (flyTime+23);
            return true;
        }
        
        return false;
    }
    
    protected boolean destroySkill() {
        if(state == STATE_FLYING_UP || state == STATE_FLYING_DOWN) {
            return false;
        } else {
            cancelFly = true;
            return true;
        }
    }
    
    public int getDurability() {
        return Integer.parseInt(Profile.getInstance().getSetting(Profile.RECORD_BIKE_LIZARD_DURABILITY));
    }

    protected void receiveDamage() {
        int durability = getDurability() - ((getSpeed() - 90)/6 + 2);
        if(durability < 1) durability = 1;
        Profile.getInstance().storeSetting(Profile.RECORD_BIKE_LIZARD_DURABILITY, Integer.toString(durability));
    }
    
    public void fix() {
        Profile.getInstance().storeSetting(Profile.RECORD_BIKE_LIZARD_DURABILITY, "100");
    }
}
