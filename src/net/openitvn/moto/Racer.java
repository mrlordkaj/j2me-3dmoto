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

import net.openitvn.game.bound.Point;
import net.openitvn.game.bound.Rectangle;
import java.util.Vector;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.m3g.Group;
import javax.microedition.m3g.Mesh;
import javax.microedition.m3g.World;

/**
 *
 * @author Thinh Pham
 */
public abstract class Racer {
    public static final int TOTAL_TYPE = 4;
    public static final byte TYPE_BLUE = 0;
    public static final byte TYPE_PINK = 1;
    public static final byte TYPE_GREEN = 2;
    public static final byte TYPE_RED = 3;
    public static final byte MESH_HUMAN = 0;
    public static final byte MESH_BIKE = 1;
    
    public static final byte STATE_READY = 0;
    public static final byte STATE_DIE = 1;
    public static final byte STATE_RUN = 2;
    
    private static final int SPEEDOMETER_LENGTH = 28;
    private static final int SPEEDOMETER_X = 50;
    private static final int SPEEDOMETER_Y = Main.SCREENSIZE_HEIGHT-35;
    protected static final byte ACCELERATION = 2;
    
    public static int calcRepairCost(int currentDurability) {
        return (100-currentDurability)*3;
    }
    
    protected final PlayScene scene;
    protected final World mWorld;
    protected final Group mRacer;
    protected Mesh mHuman;
    protected Group mBike;
    
    private int speedometerX1, speedometerY1, speedometerX2, speedometerY2, speedDigit;
    private float dieStep, dieSpeed;
    
    private int maxSpeed;
    public void setMaxSpeed(int value) { maxSpeed = value; }
    public int getMaxSpeed() { return maxSpeed; }
    
    private int speed, unitSpeed;
    public void setSpeed(int value) {
        speed = value;
        //update relative speed for road and traffic
        unitSpeed = (int)(speed * scene.getKph2UpfFactor());
        scene.getRoad().setUnitSpeed(-unitSpeed);
        scene.getTraffic().setUnitSpeed((int)((Traffic.TRAFFIC_SPEED - speed) * scene.getKph2UpfFactor()));
        //update speedometer
        if(!denyControl()) {
            speedDigit = getSpeed();
            float speedometerRad = (float)((220 - speedDigit*2)*Math.PI/180);
            speedometerX1 = SPEEDOMETER_X + (int)(6 * Math.cos(speedometerRad));
            speedometerY1 = SPEEDOMETER_Y - (int)(6 * Math.sin(speedometerRad));
            speedometerX2 = SPEEDOMETER_X + (int)(SPEEDOMETER_LENGTH * Math.cos(speedometerRad));
            speedometerY2 = SPEEDOMETER_Y - (int)(SPEEDOMETER_LENGTH * Math.sin(speedometerRad));
        }
    }
    public int getSpeed() { return speed; }
    public int getUnitSpeed() { return unitSpeed; }
    
    private int fakeSpeed;
    private void setFakeSpeed(int value) {
        fakeSpeed = value;
        //update speedometer
        if(denyControl()) {
            speedDigit = getFakeSpeed();
            float speedometerRad = (float)((220 - speedDigit*2)*Math.PI/180);
            speedometerX1 = SPEEDOMETER_X + (int)(6 * Math.cos(speedometerRad));
            speedometerY1 = SPEEDOMETER_Y - (int)(6 * Math.sin(speedometerRad));
            speedometerX2 = SPEEDOMETER_X + (int)(SPEEDOMETER_LENGTH * Math.cos(speedometerRad));
            speedometerY2 = SPEEDOMETER_Y - (int)(SPEEDOMETER_LENGTH * Math.sin(speedometerRad));
        }
    }
    protected int getFakeSpeed() { return fakeSpeed; }
    
    protected boolean denyControl() { return state == STATE_DIE || state == STATE_READY; }
    
    protected byte state = STATE_READY;
    public byte getState() { return state; }
    
    protected float positionX = 0;
    public int getPositionX() { return (int)positionX; }
    
    protected final Image imgBackground;
    
    //setting parameters
    //handle
    protected float handleAngle, handleFactor;
    
    //calculated parameters
    protected short maxSkewAngle;
    //control paramaters
    private short targetSkewAngle = 0, skewAngle = 0;
    
    public Racer() {
        scene = PlayScene.getInstance();
        PlayResource resource = PlayResource.getInstance();
        
        mWorld = scene.getWorld();
        mRacer = resource.getModelRacer();
        mRacer.setTranslation(positionX, 0, -60);
        mRacer.setOrientation(0, 1, 1, 1);
        mWorld.addChild(mRacer);
        
        //get background
        imgBackground = resource.getImageControlBackground();
    }
    
    public void beginReady() {
        setMaxSpeed(90);
        setFakeSpeed(0);
    }
    
    public abstract void initialize();
    
    public void beginGo() {
        positionX = 0;
        skewAngle = 0;
        mRacer.setTranslation(positionX, 0, 0);
        mRacer.setOrientation(0, 1, 1, 1);
        state = STATE_RUN;
    }
    protected abstract void receiveDamage();
    public abstract int getDurability();
    public abstract void fix();
    
    public void update() {
        if(!denyControl()) {
            //pick coin
            pickCoin();
            
            //check collision
            if(haveAccident()) {
                setupDie();
                return;
            }
            
            //speed up to max
            if(speed < maxSpeed - ACCELERATION) setSpeed(speed + ACCELERATION);
            else if(speed != maxSpeed) setSpeed(maxSpeed);
        }
        
        switch(state) {
            case STATE_READY:
                updateReady();
                break;
                
            case STATE_RUN:
                //dieu chinh do nghieng
                if(skewAngle <= targetSkewAngle - handleAngle) {
                    skewAngle += handleAngle;
                } else if(skewAngle >= targetSkewAngle + handleAngle) {
                    skewAngle -= handleAngle;
                } else {
                    skewAngle = targetSkewAngle;
                }
                mRacer.setOrientation(skewAngle, 0, 1, -1);
                
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
        //draw background
        g.drawImage(imgBackground, 0, Main.SCREENSIZE_HEIGHT, Graphics.LEFT | Graphics.BOTTOM);
        //draw speedometer
        g.setColor(0xff0000);
        g.drawLine(speedometerX1, speedometerY1, speedometerX2, speedometerY2);
        g.setColor(0xffffff);
        g.drawString(Integer.toString(speedDigit), SPEEDOMETER_X, Main.SCREENSIZE_HEIGHT-10, Graphics.BASELINE | Graphics.HCENTER);
    }
    
    protected boolean haveAccident() {
        if(positionX > 42 || positionX < -42) return true;
        
        Vector vehicleList = scene.getTraffic().getVehicleList();
        Point p = new Point((int)positionX, 0);
        for(byte i = 0; i < vehicleList.size(); i++) {
            if(((Vehicle)vehicleList.elementAt(i)).contains(p)) {
                return true;
            }
        }
        
        return false;
    }
    
    protected void pickCoin() {
        Vector coinList = scene.getRoad().getCoinList();
        Rectangle bounce = new Rectangle((int)positionX-12, -(unitSpeed/2)-1, 24, unitSpeed+1);
        for(int i = coinList.size()-1; i >= 0; i--) {
            ((Coin)coinList.elementAt(i)).pickMe(bounce);
        }
    }
    
    protected void updateReady() {
        if(fakeSpeed < maxSpeed-2) {
            setFakeSpeed(fakeSpeed+2);
            setSpeed(fakeSpeed);
        }
        else if(fakeSpeed != maxSpeed) {
            setFakeSpeed(maxSpeed);
            setSpeed(maxSpeed);
        }
        
        float[] xyz = new float[3];
        mRacer.getTranslation(xyz);
        if(xyz[2] < -1) mRacer.translate(0, 0, 1);
        else if (xyz[2] != 0) mRacer.setTranslation(0, 0, 0);
    }
    
    protected void setupDie() {
        state = STATE_DIE;
        
        receiveDamage();
        
        mWorld.removeChild(mRacer);
        
        if(positionX > 36) positionX = 36;
        if(positionX < -36) positionX = -36;
        
        mBike = PlayResource.getInstance().getModelBikeDie();
        mBike.setTranslation(positionX, 0, 0);
        mWorld.addChild(mBike);
        
        mHuman = PlayResource.getInstance().getModelHumanDie();
        mHuman.setTranslation(positionX, 0, 0);
        mWorld.addChild(mHuman);
        
        setFakeSpeed(speed);
        dieSpeed = 20;
        setSpeed((int)dieSpeed);
        dieStep = 4;
    }
    
    private int summaryDelay;
    protected void updateDie() {
        if(dieSpeed > 0.5f) {
            dieSpeed -= 0.5f;
            setSpeed((int)dieSpeed);
        } else if(speed != 0) {
            dieSpeed = 0;
            setSpeed(0);
        }

        if(dieStep > 0.15f) {
            mHuman.translate(0, 0, dieStep);
            mHuman.postRotate(dieStep*-0.5f, 0, 1, 0);
            mBike.translate(0, 0, dieStep*2);
            mBike.postRotate(dieStep*-4, 0, 1, 0);
            dieStep -= 0.15f;
        }
        
        if(fakeSpeed > 10) setFakeSpeed(fakeSpeed-10);
        else if(fakeSpeed != 0) {
            setFakeSpeed(0);
            summaryDelay = 3*scene.getFPS();
        }
        else if(--summaryDelay == 0) scene.beginSummary();
    }
    
    public void updateSkewAngle(short xData) {
        if(xData < -maxSkewAngle) targetSkewAngle = (short)(-maxSkewAngle);
        else if(xData > maxSkewAngle) targetSkewAngle = maxSkewAngle;
        else targetSkewAngle = xData;
    }
}
