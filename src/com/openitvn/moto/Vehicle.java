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

import com.openitvn.game.RandomHelper;
import com.openitvn.game.bounce.Point;
import com.openitvn.game.bounce.Rectangle;
import java.util.Vector;
import javax.microedition.m3g.Mesh;
import javax.microedition.m3g.World;

/**
 *
 * @author Thinh Pham
 */
public class Vehicle extends Rectangle {
    public static final byte TOTAL_TYPE = 4;
    public static final byte TOTAL_COLOR = 3;
    
    public static final byte TYPE_TAXI = 0;
    public static final byte TYPE_TRUCK = 1;
    public static final byte TYPE_CONTAINER = 2;
    public static final byte TYPE_CAR = 3;
    
    public static final byte TAIL_RIGHT = 0;
    public static final byte TAIL_LEFT = 1;
    
    public static final byte STATE_STRAIGHT = 0;
    public static final byte STATE_PREPARE_RIGHT = 1;
    public static final byte STATE_TURN_RIGHT = 2;
    public static final byte STATE_PREPARE_LEFT = 3;
    public static final byte STATE_TURN_LEFT = 4;
    public static final byte STATE_DIE_LEFT = 5;
    public static final byte STATE_DIE_RIGHT = 6;
    public static final byte STATE_DIE_BOTTOM_LEFT = 7;
    public static final byte STATE_DIE_BOTTOM_RIGHT = 8;
    
    private Vector vehicleList;
    
    private final byte typeId;
    public byte getTypeId() { return typeId; }
    private final byte colorId;
    
    private short lane;
    public short getLane() { return lane; }
    
    private final PlayResource resource;
    private final Traffic traffic;
    private final Road road;
    private Mesh mVehicle;
    private final World mWorld;
    private final boolean[] tailOn = new boolean[] { false, false };
    
    private Point pivot;
    
    private byte state = STATE_STRAIGHT;
    private short timeline;
    private boolean wantTurn;
    private int turnPosition;
    private float turnAngle, turnAngleStep;
    private int targetX, oldX;
    private float x, stepX, stepZBonus;
    private byte tailTicker;
    
    public Vehicle() {
        resource = PlayResource.getInstance();
        mWorld = PlayScene.getInstance().getWorld();
        traffic = PlayScene.getInstance().getTraffic();
        road = PlayScene.getInstance().getRoad();
        vehicleList = traffic.getVehicleList();
        
        byte rand = RandomHelper.randByte(0, TOTAL_TYPE*TOTAL_COLOR - 1);
        typeId = (byte)(rand / TOTAL_COLOR);
        colorId = (byte)(rand % TOTAL_COLOR);
        setWidth(26);
        switch(typeId) {
            case TYPE_TAXI:
                pivot = new Point(13, 12);
                setHeight(12+40);
                turnAngleStep = 0.8f;
                break;

            case TYPE_TRUCK:
                pivot = new Point(13, 12);
                setHeight(12+44);
                turnAngleStep = 0.7f;
                break;

            case TYPE_CONTAINER:
                pivot = new Point(13, 22);
                setHeight(22+110);
                turnAngleStep = 0.4f;
                break;

            case TYPE_CAR:
                pivot = new Point(13, 10);
                setHeight(10+40);
                turnAngleStep = 0.8f;
                break;
        }
        setPivot(pivot);
    }
    
    public void selectPosition(int min, int max) {
        byte loop = 0;
        while(true) {
            setY(RandomHelper.randInt(min, max));
            selectLane();
            if(lane != Lane.LANE_EMPTY) {
                vehicleList.addElement(this);
                
                x = lane*Lane.LANE_WIDTH - pivot.x;
                setX((int)x);
                
                wantTurn = RandomHelper.randChance(traffic.getWantTurnChance());
                turnPosition = traffic.getTurnPosition();
                return;
            }
            if(++loop == 10) return;
        }
    }
    
    private void selectLane() {
        Vector selectableLane = new Vector();
        for(lane = -1; lane <= 1; lane++) {
            boolean selectable = true;
            for(byte j = 0; j < vehicleList.size(); j++) {
                if(collideWith((Vehicle)vehicleList.elementAt(j))) {
                    selectable = false;
                    break;
                }
            }
            if(selectable) selectableLane.addElement(new Lane(lane));
        }
        //if there are have more than one empty lane
        if(selectableLane.size() >= traffic.getEmptyLaneRequired()) {
            Lane selectedLane = (Lane)selectableLane.elementAt(RandomHelper.randInt(0, selectableLane.size()-1));
            lane = selectedLane.getLane();
        } else {
            lane = Lane.LANE_EMPTY;
        }
    }
    
    private boolean collideWith(Vehicle target) {
        if(target.lane != lane) return false;
        int top = Math.max(getY(), target.getY()) - traffic.getVehicleSpace();
        int bottom = Math.min(getY() + getHeight(), target.getY() + target.getHeight()) + traffic.getVehicleSpace();
        return (top <= bottom);
    }
    
    private boolean canTurnTo(short targetLane) {
        for(byte i = 0; i < vehicleList.size(); i++) {
            Vehicle target = (Vehicle)vehicleList.elementAt(i);
            //neu khac lan duong hoac trung voi ban than thi bo qua
            if(target.equals(this) || target.lane != targetLane) continue;
            //neu cung lan duong va chong len nhau thi ket luan la khong duoc
            int top = Math.max(getY(), target.getY()) - traffic.getVehicleSpace();
            int bottom = Math.min(getY() + getHeight(), target.getY() + target.getHeight()) + traffic.getVehicleSpace();
            if(top < bottom) return false;
        }
        //neu khong chong len bat ky phuong tien nao
        return true;
    }
    
    public void update() {
        boolean toLeft;
        float turnAccelerator = traffic.getTurnAccelerator();
        switch(state) {
            case STATE_STRAIGHT:
                if(wantTurn && getY() < turnPosition) {
                    short targetLane;
                    targetLane = (short)(lane - 1);
                    boolean canTurnRight = (targetLane >= Lane.LANE_RIGHT && canTurnTo(targetLane));
                    targetLane = (short)(lane + 1);
                    boolean canTurnLeft = (targetLane <= Lane.LANE_LEFT && canTurnTo(targetLane));
                    if(canTurnRight || canTurnLeft) {
                        if(canTurnRight && canTurnLeft) {
                            state = RandomHelper.randChance(50) ? STATE_PREPARE_RIGHT : STATE_PREPARE_LEFT;
                        } else {
                            state = canTurnRight ? STATE_PREPARE_RIGHT : STATE_PREPARE_LEFT;
                        }
                        oldX = (int)x;
                        lane += (state == STATE_PREPARE_RIGHT ? -1 : 1);
                        targetX = lane*Lane.LANE_WIDTH - pivot.x;
                        timeline = 0;
                    }
                    wantTurn = false;
                }
                break;
                
            case STATE_PREPARE_RIGHT:
            case STATE_PREPARE_LEFT:
                toLeft = (state == STATE_PREPARE_LEFT);
                tailToggle(toLeft ? TAIL_LEFT : TAIL_RIGHT);
                if(++timeline > 13) {
                    if((toLeft && x < oldX + 10) || (!toLeft && x > oldX - 10)) {
                        turn(toLeft ? turnAngleStep : -turnAngleStep);
                        stepX += toLeft ? turnAccelerator : -turnAccelerator;
                    } else {
                        state = toLeft ? STATE_TURN_LEFT : STATE_TURN_RIGHT;
                    }
                }
                break;
                
            case STATE_TURN_RIGHT:
            case STATE_TURN_LEFT:
                toLeft = (state == STATE_TURN_LEFT);
                if(x < targetX - 10 || x > targetX + 10) {
                    tailToggle(toLeft ? TAIL_LEFT: TAIL_RIGHT);
                } else if(stepX > turnAccelerator || stepX < -turnAccelerator) {
                    tailToggle(toLeft ? TAIL_LEFT: TAIL_RIGHT);
                    stepX += toLeft ? -turnAccelerator : turnAccelerator;
                    turn(toLeft ? -turnAngleStep : turnAngleStep);
                } else {
                    x = targetX;
                    stepX = 0;
                    
                    turnAngle = 0;
                    turn(0);
        
                    tailOff();
                    state = STATE_STRAIGHT;
                }
                break;
                
            case STATE_DIE_LEFT:
            case STATE_DIE_RIGHT:
                boolean isLeft = (state == STATE_DIE_LEFT);
                if(typeId == TYPE_CONTAINER) turn(isLeft ? -1 : 1);
                else turn(isLeft ? -4 : 4);
                translate(isLeft ? 6 : -6, (int)(traffic.getUnitSpeed() + stepZBonus));
                if(stepZBonus > road.getUnitSpeed()) stepZBonus -= 1.4f;
                return;
                
            case STATE_DIE_BOTTOM_LEFT:
            case STATE_DIE_BOTTOM_RIGHT:
                boolean isBottomLeft = (state == STATE_DIE_BOTTOM_LEFT);
                if(typeId == TYPE_CONTAINER) turn(isBottomLeft ? -1 : 1);
                else turn(isBottomLeft ? -4 : 4);
                translate(0, (int)(traffic.getUnitSpeed() + stepZBonus));
                if(stepZBonus > road.getUnitSpeed()) stepZBonus -= 1.4f;
                return;
        }
        translate(stepX, traffic.getUnitSpeed());
    }
    
    public void turn(float stepAngle) {
        turnAngle += stepAngle;
        setAngle((int)turnAngle);
        mVehicle.setOrientation(turnAngle, 0, 1, 0);
    }
    
    private void translate(float stepX, int stepY) {
        x += stepX;
        setX((int)x);
        setY(getY()+stepY);
        if(getY() < 450) {
            //mo hinh chi xuat hien va di chuyen khi no o trong pham vi khung nhin
            if(mVehicle == null) {
                mVehicle = resource.getModelVehicle(typeId, colorId);
                mWorld.addChild(mVehicle);
            }
            if(getY() < -150) removeMe();
            else mVehicle.setTranslation(getX()+pivot.x, 0, getY()+pivot.y);
        }
    }
    
    private void tailToggle(byte side) {
        if(mVehicle == null) return;
        
        if(++tailTicker == 3) {
            tailOn[side] = !tailOn[side];
            mVehicle.getAppearance(0).setTexture(1, tailOn[side] ? resource.getTextureTail(typeId, side) : null);
            tailTicker = 0;
        }
    }
    
    private void tailOff() {
        for(byte i = 0; i < 2; i++) {
            mVehicle.getAppearance(0).setTexture(1, null);
        }
    }
    
    public boolean contains(Point p) {
        if(state == STATE_DIE_LEFT
                || state == STATE_DIE_RIGHT
                || state == STATE_DIE_BOTTOM_LEFT
                || state == STATE_DIE_BOTTOM_RIGHT)
            return false;
        else return super.contains(p);
    }
    
    public void beKicked(byte kickedState) {
        state = kickedState;
        if(state == STATE_DIE_BOTTOM_LEFT || state == STATE_DIE_BOTTOM_RIGHT) stepZBonus = -traffic.getUnitSpeed()*2;
        if(state == STATE_DIE_LEFT || state == STATE_DIE_RIGHT) stepZBonus = -traffic.getUnitSpeed()*.5f;
    }
    
    public void removeMe() {
        vehicleList.removeElement(this);
        mWorld.removeChild(mVehicle);
        mVehicle = null;
    }
}
