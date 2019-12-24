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
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;
import javax.microedition.m3g.Group;
import javax.microedition.m3g.Mesh;
import javax.microedition.m3g.Texture2D;
import javax.microedition.m3g.World;

/**
 *
 * @author Thinh Pham
 */
public abstract class Racer {
    public static final int TOTAL_TYPE = 4;
    public static final int TOTAL_COLOR = 4;
    public static final byte TYPE_THUNDER = 0;
    public static final byte TYPE_LIZARD = 1;
    public static final byte TYPE_SPIRIT = 2;
    public static final byte TYPE_TOMAHAWK = 3;
    public static final byte COLOR_BLUE = 0;
    public static final byte COLOR_PINK = 1;
    public static final byte COLOR_GREEN = 2;
    public static final byte COLOR_RED = 3;
    public static final byte MESH_HUMAN = 0;
    public static final byte MESH_BIKE = 1;
    
    public static final byte STATE_READY = 0;
    public static final byte STATE_DIE = 1;
    public static final byte STATE_MOVE_STRAIGHT = 2;
    public static final byte STATE_MOVE_LEFT = 3;
    public static final byte STATE_MOVE_RIGHT = 4;
    public static final byte STATE_MOVE_LEFT_STRAIGHT = 5;
    public static final byte STATE_MOVE_RIGHT_STRAIGHT = 6;
    
    private static final int SPEEDOMETER_LENGTH = 22;
    private static final int SPEEDOMETER_X = Main.SCREENSIZE_WIDTH/2;
    private static final int SPEEDOMETER_Y = Main.SCREENSIZE_HEIGHT-22;
    protected static final byte ACCELERATION = 2;
    
    public static int calcRepairCost(int currentDurability) {
        return (100-currentDurability)*3;
    }
    
    protected final PlayScene scene;
    protected final World mWorld;
    protected final Group mRacer;
    protected Mesh mHuman;
    protected Group mBike;
    protected final Mesh mDefaultBike, mDefaultHuman;
    protected final Texture2D texBike, texHuman, texFlash;
    
    private int speedometerX1, speedometerY1, speedometerX2, speedometerY2, speedDigit;
    private float dieStep, dieSpeed;
    
    protected boolean activeFlash = false, activeSkill = false;
    private boolean deactivingFlash;
    
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
            float speedometerRad = (float)((200 - speedDigit*1.1f)*Math.PI/180);
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
            float speedometerRad = (float)((200 - speedDigit*1.1f)*Math.PI/180);
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
    
    protected final Rectangle btnLeft, btnRight, btnSkill, btnPowerUp;
    protected final Image imgBackground, imgLeft, imgRight;
    protected final Sprite sprSkill, sprPowerUp;
    protected int skillBarWidth, powerUpBarWidth;
    private final byte powerUpType;
    
    //setting parameters
    //handle
    protected float handleAngle, handleFactor;
    protected byte skewNumFrame;
    //skill charger
    private short skillChargerTime;
    protected short getSkillChargerTime() { return skillChargerTime; }
    protected short skillChargerTick;
    private final short powerUpChargerTime;
    protected short getPowerUpChargerTime() { return powerUpChargerTime; }
    protected short powerUpChargerTick;
    
    public Racer() {
        scene = PlayScene.getInstance();
        PlayResource resource = PlayResource.getInstance();
        
        mWorld = scene.getWorld();
        mRacer = resource.getModelRacer();
        mRacer.setTranslation(positionX, 0, -60);
        mRacer.setOrientation(0, 1, 1, 1);
        mWorld.addChild(mRacer);
        
        mDefaultHuman = (Mesh)mRacer.getChild(MESH_HUMAN);
        texHuman = resource.getTextureHumanDefault();
        mDefaultBike = (Mesh)mRacer.getChild(MESH_BIKE);
        texBike = resource.getTextureBikeDefault();
        texFlash = resource.getTextureFlash();
        mDefaultBike.getAppearance(0).setTexture(0, texBike);
        mDefaultHuman.getAppearance(0).setTexture(0, texHuman);
        
        //get background
        imgBackground = resource.getImageControlBackground();
        //get button left
        imgLeft = resource.getImageControlLeft();
        btnLeft = new Rectangle(0, Main.SCREENSIZE_HEIGHT - 50, 80, 50);
        //get button right
        imgRight = resource.getImageControlRight();
        btnRight = new Rectangle(Main.SCREENSIZE_WIDTH - 80, Main.SCREENSIZE_HEIGHT - 50, 80, 50);
        //get button skill
        btnSkill = new Rectangle(0, Main.SCREENSIZE_HEIGHT - 100, 50, 50);
        sprSkill = new Sprite(resource.getImageControlSkill(), 30, 30);
        sprSkill.setPosition(5, Main.SCREENSIZE_HEIGHT-73);
        //get button power-up
        powerUpType = resource.getPowerUpType();
        powerUpChargerTime = 100;
        powerUpChargerTick = 0;
        btnPowerUp = new Rectangle(Main.SCREENSIZE_WIDTH - 50, Main.SCREENSIZE_HEIGHT - 100, 50, 50);
        sprPowerUp = new Sprite(PlayResource.getInstance().getImageControlPowerUp(), 30, 30);
        sprPowerUp.setPosition(205, Main.SCREENSIZE_HEIGHT-73);
    }
    
    protected abstract void initSkill(int skillLevel);
    
    protected void initHandle(int handleLevel) {
        switch(handleLevel) {
            case 5:
                handleAngle = 7;
                handleFactor = 0.18f;
                skewNumFrame = 4;
                break;
                
            case 4:
                handleAngle = 7;
                handleFactor = 0.2f;
                skewNumFrame = 3;
                break;
                
            case 3:
                handleAngle = 6;
                handleFactor = 0.18f;
                skewNumFrame = 3;
                break;
                
            case 2:
                handleAngle = 6;
                handleFactor = 0.16f;
                skewNumFrame = 3;
                break;
                
            default:
                handleAngle = 5;
                handleFactor = 0.12f;
                skewNumFrame = 4;
                break;
        }
    }
    
    protected void initSkillCharger(int chargerLevel) {
        switch(chargerLevel) {
            case 5:
                skillChargerTime = (short)(35*scene.getFPS());
                break;
                
            case 4:
                skillChargerTime = (short)(45*scene.getFPS());
                break;
                
            case 3:
                skillChargerTime = (short)(50*scene.getFPS());
                break;
                
            case 2:
                skillChargerTime = (short)(55*scene.getFPS());
                break;
                
            default:
                skillChargerTime = (short)(60*scene.getFPS());
                break;
        }
    }
    
    public void beginReady() {
        setMaxSpeed(90);
        setFakeSpeed(0);
    }
    
    public abstract void initialize();
    public abstract void beginGo();
    
    protected abstract void pressLeft();
    protected void releaseLeft() {}
    protected abstract void pressRight();
    protected void releaseRight() {}
    protected abstract boolean activeSkill();
    protected abstract boolean destroySkill();
    protected abstract void receiveDamage();
    public abstract int getDurability();
    public abstract void fix();
    
    private void requestActiveSkill() {
        if(!denyControl() && !activeFlash && skillChargerTick == skillChargerTime) {
            if(activeSkill()) {
                activeSkill = true;
                skillChargerTick = 0;
                sprSkill.setFrame(0);
            }
        }
    }
    
    protected void chargerSkill() {
        if(skillChargerTick < skillChargerTime - 1) {
            skillChargerTick++;
            skillBarWidth = (int)(30.f * ((float)skillChargerTick / (float)skillChargerTime));
        } else if(skillChargerTick != skillChargerTime) {
            skillChargerTick = skillChargerTime;
            skillBarWidth = 30;
            sprSkill.setFrame(1);
        }
    }
    
    private void requestActivePowerUp() {
        if(!denyControl() && powerUpChargerTick == powerUpChargerTime) {
            if(scene.activePowerUp(powerUpType)) {
                powerUpChargerTick = 0;
                powerUpBarWidth = 0;
                sprPowerUp.setFrame(0);
            }
        }
    }
    
    public void chargerPowerUp() {
        if(powerUpChargerTick < powerUpChargerTime) {
            powerUpChargerTick++;
            powerUpBarWidth = (int)(30.f * ((float)powerUpChargerTick / (float)powerUpChargerTime));
            if(powerUpChargerTick == powerUpChargerTime) sprPowerUp.setFrame(1);
        }
    }
    
    public void activeFlash() {
        mDefaultHuman.getAppearance(0).setTexture(0, texFlash);
        mDefaultBike.getAppearance(0).setTexture(0, texFlash);
        activeFlash = true;
        deactivingFlash = false;
    }
    
    public void deactiveFlash() {
        deactivingFlash = true;
    }
    
//#if TKEY || QWERTY
    public boolean keyPressed(int keyCode) {
        if (!denyControl()) {
            switch (keyCode) {
                case KeyMap.KEY_4:
                case KeyMap.KEY_LEFT:
                    pressLeft();
                    return true;

                case KeyMap.KEY_6:
                case KeyMap.KEY_RIGHT:
                    pressRight();
                    return true;

                case KeyMap.KEY_1:
                case KeyMap.KEY_DOWN:
                    requestActiveSkill();
                    return true;

                case KeyMap.KEY_3:
                case KeyMap.KEY_UP:
                    requestActivePowerUp();
                    break;
            }
        }
        return false;
    }
    
    public boolean keyReleased(int keyCode) {
        if (!denyControl()) {
            switch (keyCode) {
                case KeyMap.KEY_4:
                case KeyMap.KEY_LEFT:
                    releaseLeft();
                    return true;

                case KeyMap.KEY_6:
                case KeyMap.KEY_RIGHT:
                    releaseRight();
                    return true;
            }
        }
        return false;
    }
//#endif
    
    public boolean pointerPressed(int x, int y) {
        if (!denyControl()) {
            Point p = new Point(x, y);
            if (btnLeft.contains(p)) {
                pressLeft();
                return true;
            }
            else if (btnRight.contains(p)) {
                pressRight();
                return true;
            }
            else if (btnSkill.contains(p)) {
                requestActiveSkill();
                return true;
            }
            else if (btnPowerUp.contains(x, y)) {
                requestActivePowerUp();
                return true;
            }
        }
        return false;
    }
    
    public boolean pointerReleased(int x, int y) {
        if (!denyControl()) {
            Point p = new Point(x, y);
            if (btnLeft.contains(p)) {
                releaseLeft();
                return true;
            }
            else if (btnRight.contains(p)) {
                releaseRight();
                return true;
            }
        }
        return false;
    }
    
    public void update() {
        if (activeFlash) {
            if (!deactivingFlash) {
                if (speed < 200 - 5)
                    setSpeed(speed + 5);
                else if (speed != 200)
                    setSpeed(200);
            } else if (speed > maxSpeed + 3) {
                setSpeed(speed - 3);
            } else if (speed != maxSpeed) {
                mDefaultBike.getAppearance(0).setTexture(0, texBike);
                mDefaultHuman.getAppearance(0).setTexture(0, texHuman);
                setSpeed(maxSpeed);
                activeFlash = false;
            }
        }
    }
    
    public void paint(Graphics g) {
        // draw background
        g.drawImage(imgBackground, 0, Main.SCREENSIZE_HEIGHT, Graphics.LEFT | Graphics.BOTTOM);
        // draw speedometer
        g.setColor(0xffffff);
        g.drawLine(speedometerX1, speedometerY1, speedometerX2, speedometerY2);
        g.drawString(Integer.toString(speedDigit), Main.SCREENSIZE_WIDTH/2, Main.SCREENSIZE_HEIGHT-6, Graphics.BASELINE | Graphics.HCENTER);
        // draw skill fill progress
        g.setColor(0x006db2);
        g.fillRect(52, Main.SCREENSIZE_HEIGHT-17, skillBarWidth, 4);
        sprSkill.paint(g);
        // draw power-up fill progress
        g.fillRect(158, Main.SCREENSIZE_HEIGHT-17, powerUpBarWidth, 4);
        sprPowerUp.paint(g);
    }
    
    protected boolean haveAccident() {
        if (positionX > 42 || positionX < -42) return true;
        if (activeFlash) return false;
        
        Vector vehicleList = scene.getTraffic().getVehicleList();
        Point p = new Point((int)positionX, 0);
        for (byte i = 0; i < vehicleList.size(); i++) {
            if (((Vehicle)vehicleList.elementAt(i)).contains(p))
                return true;
        }
        
        return false;
    }
    
    protected void pickPowerUp(int widthRanger) {
        Vector powerUpList = scene.getRoad().getPowerUpList();
        Rectangle bounding = new Rectangle((int)positionX-widthRanger/2, -(unitSpeed/2)-1, widthRanger, unitSpeed+1);
        for (int i = powerUpList.size()-1; i >= 0; i--) {
            ((Power)powerUpList.elementAt(i)).pickMe(bounding);
        }
    }
    
    protected void updateReady() {
        if (fakeSpeed < maxSpeed-2) {
            setFakeSpeed(fakeSpeed+2);
            setSpeed(fakeSpeed);
        }
        else if (fakeSpeed != maxSpeed) {
            setFakeSpeed(maxSpeed);
            setSpeed(maxSpeed);
        }
        
        float[] xyz = new float[3];
        mRacer.getTranslation(xyz);
        if (xyz[2] < -1)
            mRacer.translate(0, 0, 1);
        else if (xyz[2] != 0)
            mRacer.setTranslation(0, 0, 0);
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
    
    private int resurrectionDelay;
    protected void updateDie() {
        if (dieSpeed > 0.5f) {
            dieSpeed -= 0.5f;
            setSpeed((int)dieSpeed);
        }
        else if(speed != 0) {
            dieSpeed = 0;
            setSpeed(0);
        }

        if (dieStep > 0.15f) {
            mHuman.translate(0, 0, dieStep);
            mHuman.postRotate(dieStep*-0.5f, 0, 1, 0);
            mBike.translate(0, 0, dieStep*2);
            mBike.postRotate(dieStep*-4, 0, 1, 0);
            dieStep -= 0.15f;
        }
        
        if (fakeSpeed > 10) {
            setFakeSpeed(fakeSpeed-10);
        }
        else if (fakeSpeed != 0) {
            setFakeSpeed(0);
            resurrectionDelay = 3*scene.getFPS();
        }
        else if (--resurrectionDelay == 0) {
            PlayScene.getInstance().beginResurrection();
        }
    }
    
    public void resurrection() {
        if (state == STATE_DIE) {
            mWorld.removeChild(mBike);
            mWorld.removeChild(mHuman);
            mWorld.addChild(mRacer);
            beginGo();
        }
    }
}
