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
public class RacerThunder extends Racer {
    private int minSpeed;
    public void setMaxSpeed(int value) {
        super.setMaxSpeed(value);
        minSpeed = (int)(value*brakeFactor);
    }
    
    //setting parameters
    //brake
    private byte brakeStep;
    private float brakeFactor;
    private short brakeTime;
    
    //calculated parameters
    private float skewLength, maxVelocity, maxSkewAngle;
    
    //control paramaters
    //drive
    private short lane;
    private byte stackState;
    private float skewAngle, targetX, pseudoTargetX;
    //brake
    private short brakeTick;
    
    public RacerThunder() {
        super();
    }
    
    public void initialize() {
        String upgradeData = Profile.getInstance().getSetting(Profile.RECORD_BIKE_THUNDER_UPGRADE);
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
        //System.out.println("skew length = " + skewLength);
        //if(skewLength >= 14) throw new RuntimeException("Handle parameters is invalid.");
    }
    
    protected void initSkill(int skillLevel) {
        switch(skillLevel) {
            case 5:
                brakeStep = 5;
                brakeFactor = 0.4f;
                brakeTime = (short)(4*scene.getFPS());
                break;
                
            case 4:
                brakeStep = 3;
                brakeFactor = 0.4f;
                brakeTime = (short)(4*scene.getFPS());
                break;
                
            case 3:
                brakeStep = 3;
                brakeFactor = 0.4f;
                brakeTime = (short)(2*scene.getFPS());
                break;
                
            case 2:
                brakeStep = 3;
                brakeFactor = 0.6f;
                brakeTime = (short)(2*scene.getFPS());
                break;
            
            default:
                brakeStep = 2;
                brakeFactor = 0.7f;
                brakeTime = (short)(2*scene.getFPS());
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
            int speed = getSpeed();
            int maxSpeed = getMaxSpeed();
            //braking down or not
            if(activeSkill) {
                if(speed > minSpeed + brakeStep) setSpeed(speed - brakeStep);
                else if(speed != minSpeed) setSpeed(minSpeed);
                
                brakeTick--;
                skillBarWidth = (int)(30.f * ((float)brakeTick / (float)brakeTime));
                if(brakeTick == 0) activeSkill = false;
            } else {
                if(!activeFlash) {
                    //speedup to max
                    if(speed < maxSpeed - ACCELERATION) setSpeed(speed + ACCELERATION);
                    else if(speed != maxSpeed) setSpeed(maxSpeed);
                }
                
                chargerSkill();
            }
            
            //pick pu
            pickPowerUp(10);
            
            //check collision
            if(haveAccident()) {
                setupDie();
                return;
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
                break;
        }
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
        if(stackState == STATE_MOVE_LEFT) pressLeft();
        else if(stackState == STATE_MOVE_RIGHT) pressRight();
    }
    
    protected void pressLeft() {
        if(state == STATE_MOVE_STRAIGHT) {
            if(lane < 2) {
                lane++;
                targetX = lane*Lane.LANE_WIDTH;
                pseudoTargetX = targetX - skewLength;
                state = STATE_MOVE_LEFT;
                stackState = STATE_MOVE_STRAIGHT;
            }
        } else {
            stackState = (stackState == STATE_MOVE_RIGHT) ? STATE_MOVE_STRAIGHT : STATE_MOVE_LEFT;
        }
    }
    
    protected void pressRight() {
        if(state == STATE_MOVE_STRAIGHT) {
            if(lane > -2) {
                lane--;
                targetX = lane*Lane.LANE_WIDTH;
                pseudoTargetX = targetX + skewLength;
                state = STATE_MOVE_RIGHT;
                stackState = STATE_MOVE_STRAIGHT;
            }
        } else {
            stackState = (stackState == STATE_MOVE_LEFT) ? STATE_MOVE_STRAIGHT : STATE_MOVE_RIGHT;
        }
    }
    
    protected boolean activeSkill() {
        Achievement.getInstance().triggerRunningInFear();
        brakeTick = brakeTime;
        return true;
    }
    
    protected boolean destroySkill() {
        activeSkill = false;
        return true;
    }
    
    public int getDurability() {
        return Integer.parseInt(Profile.getInstance().getSetting(Profile.RECORD_BIKE_THUNDER_DURABILITY));
    }

    protected void receiveDamage() {
        int durability = getDurability() - ((getSpeed() - 90)/6 + 2);
        if(durability < 1) durability = 1;
        Profile.getInstance().storeSetting(Profile.RECORD_BIKE_THUNDER_DURABILITY, Integer.toString(durability));
    }
    
    public void fix() {
        Profile.getInstance().storeSetting(Profile.RECORD_BIKE_THUNDER_DURABILITY, "100");
    }
}
