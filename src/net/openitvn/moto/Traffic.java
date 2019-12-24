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

import net.openitvn.game.RandomHelper;
import java.util.Vector;

/**
 *
 * @author Thinh Pham
 */
public class Traffic {
    public static final int TRAFFIC_SPEED = 50;
    public static final int[] LEVEL_UP = new int[] { 1000, 2500, 5500, 10000, 18000};
    
    private byte level;
    private int nextLevel;
    public byte getLevel() { return level; }
    
    //level settings
    private byte vehiclePerSegment;
    public byte getVehiclePerSegment() { return vehiclePerSegment; }
    private short segmentLength;
    public short getSegmentLength() { return segmentLength; }
    private short segmentSpace;
    public short getSegmentSpace() { return segmentSpace; }
    private short vehicleSpace;
    public short getVehicleSpace() { return vehicleSpace; }
    private byte emptyLaneRequired;
    public byte getEmptyLaneRequired() { return emptyLaneRequired; }
    private float turnAcceleratorMin;
    private float turnAcceleratorMax;
    public float getTurnAccelerator() { return RandomHelper.randFloat(turnAcceleratorMin, turnAcceleratorMax); }
    private byte wantTurnChance;
    public byte getWantTurnChance() { return wantTurnChance; }
    private int turnPositionMin;
    private int turnPositionMax;
    public int getTurnPosition() { return RandomHelper.randInt(turnPositionMin, turnPositionMax); }
    
    private final Vector vehicleList = new Vector();
    public Vector getVehicleList() { return vehicleList; }
    
    private int[] segmentZ;
    
    //speed is kilometers per hour, unitSpeed is units per frame
    private int unitSpeed;
    public void setUnitSpeed(int value) { unitSpeed = value; }
    public int getUnitSpeed() { return unitSpeed; }
    
    public Traffic() {
        level = 0;
    }
    
    public void tryLevelUp(int meters) {
        if (level < 6 && meters >= nextLevel) {
            MessageQueue.addMessage(Message.TYPE_LEVEL);
            levelUp();
        }
    }
    
    public void levelUp() {
        level++;
        updateLevelParam();
        if(level == 1) segmentZ = new int[] { -segmentLength, segmentSpace};
        if(level < 6) nextLevel = LEVEL_UP[level-1];
    }
    
    private void updateLevelParam() {
        switch(level) {
            case 1:
                vehiclePerSegment = 3;
                segmentLength = 850;
                segmentSpace = 200;
                vehicleSpace = 100;
                emptyLaneRequired = 3;
                wantTurnChance = 32;
                turnAcceleratorMin = 0.12f;
                turnAcceleratorMax = 0.13f;
                turnPositionMin = 280;
                turnPositionMax = 410;
                PlayScene.getInstance().getRacer().setMaxSpeed(90);
                break;
                
            case 2:
                vehiclePerSegment = 3;
                segmentLength = 850;
                segmentSpace = 180;
                vehicleSpace = 90;
                emptyLaneRequired = 3;
                wantTurnChance = 32;
                turnAcceleratorMin = 0.12f;
                turnAcceleratorMax = 0.15f;
                turnPositionMin = 280;
                turnPositionMax = 410;
                PlayScene.getInstance().getRacer().setMaxSpeed(96);
                break;
                
            case 3:
                vehiclePerSegment = 4;
                segmentLength = 850;
                segmentSpace = 150;
                vehicleSpace = 80;
                emptyLaneRequired = 3;
                wantTurnChance = 38;
                turnAcceleratorMin = 0.14f;
                turnAcceleratorMax = 0.18f;
                turnPositionMin = 240;
                turnPositionMax = 400;
                PlayScene.getInstance().getRacer().setMaxSpeed(102);
                break;
                
            case 4:
                vehiclePerSegment = 4;
                segmentLength = 1000;
                segmentSpace = 140;
                vehicleSpace = 80;
                emptyLaneRequired = 3;
                wantTurnChance = 46;
                turnAcceleratorMin = 0.18f;
                turnAcceleratorMax = 0.24f;
                turnPositionMin = 200;
                turnPositionMax = 400;
                PlayScene.getInstance().getRacer().setMaxSpeed(108);
                break;
                
            case 5:
                vehiclePerSegment = 5;
                segmentLength = 1100;
                segmentSpace = 80;
                vehicleSpace = 50;
                emptyLaneRequired = 3;
                wantTurnChance = 58;
                turnAcceleratorMin = 0.22f;
                turnAcceleratorMax = 0.27f;
                turnPositionMin = 200;
                turnPositionMax = 400;
                PlayScene.getInstance().getRacer().setMaxSpeed(114);
                break;
                
            case 6:
                vehiclePerSegment = 6;
                segmentLength = 1000;
                segmentSpace = 70;
                vehicleSpace = 40;
                emptyLaneRequired = 3;
                wantTurnChance = 66;
                turnAcceleratorMin = 0.26f;
                turnAcceleratorMax = 0.32f;
                turnPositionMin = 180;
                turnPositionMax = 400;
                PlayScene.getInstance().getRacer().setMaxSpeed(120);
                break;
        }
    }
    
    public void update() {
        for(byte i = 0; i < segmentZ.length; i++) {
            segmentZ[i] += unitSpeed;
            if (segmentZ[i] < -segmentLength) {
                segmentZ[i] += segmentZ.length*(segmentLength+segmentSpace);
                Preloader.regenerateTrafficSegment(segmentZ[i]);
            }
        }
        
        for(int i = vehicleList.size() - 1; i >= 0; i--) {
            ((Vehicle)vehicleList.elementAt(i)).update();
        }
    }
}
