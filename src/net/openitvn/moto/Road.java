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
import javax.microedition.m3g.Group;
import javax.microedition.m3g.Mesh;
import javax.microedition.m3g.World;

/**
 *
 * @author Thinh Pham
 */
public class Road {
    public static final byte TYPE_MOUNTAIN = 0;
    public static final byte TYPE_BRIDGE = 1;
    public static final byte TYPE_SEA = 2;
    public static final byte TYPE_DESERT = 3;
    public static final byte TYPE_TUNNEL = 4;

    private final short SEGMENT_LENGTH = 300;
    private final byte SEGMENT_AMOUNT = 4;
    private final byte TYPE_LENGTH_MIN = 26;
    private final byte TYPE_LENGTH_MAX = 44;
    
    private final PlayResource resource;
    private final PlayScene scene;
    private final World mWorld;
    private final Group[] segment = new Group[SEGMENT_AMOUNT];
    private final int[] segmentZ = new int[SEGMENT_AMOUNT];

    private int unitSpeed, meterPerFrame, distance;

    public void setUnitSpeed(int value) {
        unitSpeed = value;
        meterPerFrame = unitSpeed / -10;
    }

    public int getUnitSpeed() {
        return unitSpeed;
    }

    public int getDistance() {
        return distance;
    }

    private byte oldSegmentLeft;
    private byte segmentMustBeReplaced;
    byte roadType;
    
    private final Vector coinList;

    public Vector getCoinList() {
        return coinList;
    }

    public Road() {
        resource = PlayResource.getInstance();
        scene = PlayScene.getInstance();
        mWorld = scene.getWorld();

        coinList = new Vector();
        distance = 0;

        //create startup segments
        roadType = TYPE_SEA;
        oldSegmentLeft = RandomHelper.randByte(TYPE_LENGTH_MIN, TYPE_LENGTH_MAX);
        for (byte i = 0; i < SEGMENT_AMOUNT; i++) {
            segment[i] = resource.getModelRoad(roadType);
            segmentZ[i] = i * SEGMENT_LENGTH;
            segment[i].setTranslation(0, 0, segmentZ[i]);
            mWorld.addChild(segment[i]);
        }
        segmentMustBeReplaced = 0;
        if(Main.isHighVisualEffect()) {
            mWorld.getBackground().setImage(resource.getImageBackground(roadType));
        } else {
            mWorld.getBackground().setColor(0x121212);
        }
    }

    public void update() {
        //updates segments
        for (byte i = SEGMENT_AMOUNT - 1; i >= 0; i--) {
            segmentZ[i] += unitSpeed;
            if (segmentZ[i] < -SEGMENT_LENGTH) {
                segmentZ[i] += SEGMENT_AMOUNT * SEGMENT_LENGTH;
                regenerateSegment(i);
            } else {
                segment[i].setTranslation(0, 0, segmentZ[i]);
            }
        }

        //updates powers
        for(int i = coinList.size() - 1; i >= 0; i--) {
            ((Coin)coinList.elementAt(i)).update(unitSpeed);
        }
    }

    public void updateDistance() {
        //updates meters and test level up
        distance += meterPerFrame;
        scene.getTraffic().tryLevelUp(distance);
    }

    private void regenerateSegment(byte segmentId) {
        //change road to new type if old type is run over
        if (--oldSegmentLeft == 0) {
            if (roadType == TYPE_TUNNEL) {
                roadType = RandomHelper.randByte(0, 3);
                oldSegmentLeft = RandomHelper.randByte(TYPE_LENGTH_MIN, TYPE_LENGTH_MAX);
            } else {
                roadType = TYPE_TUNNEL;
                oldSegmentLeft = 6;
            }
            segmentMustBeReplaced = SEGMENT_AMOUNT;
        }

        //switch road model to the new if needed, and move camera when active trigger
        if (segmentMustBeReplaced > 0) {
            mWorld.removeChild(segment[segmentId]);
            segment[segmentId] = resource.getModelRoad(roadType);
            mWorld.addChild(segment[segmentId]);
            segmentMustBeReplaced--;

            //move camera down when enter a tunnel
            if (segmentMustBeReplaced == 2 && roadType == TYPE_TUNNEL) {
                ((CustomCamera) mWorld.getActiveCamera()).moveDown();
            }

            //move camera up when exit a tunnel
            if (segmentMustBeReplaced == 0 && roadType != TYPE_TUNNEL) {
                ((CustomCamera) mWorld.getActiveCamera()).moveUp();
            }

            //switch background when prepare exit a tunnel
            if (Main.isHighVisualEffect() && segmentMustBeReplaced == SEGMENT_AMOUNT - 1 && roadType != TYPE_TUNNEL) {
                Preloader.changeBackground(mWorld.getBackground(), roadType);
            }
        }

        //add tunnel's gate if current segment is the last one
        if (oldSegmentLeft == 1) {
            Mesh basicGate = resource.getModelGate(TYPE_TUNNEL);
            segment[segmentId].addChild(basicGate);
            basicGate.setTranslation(0, 0, 140);

            if (roadType != TYPE_TUNNEL) {
                Mesh extraGate = resource.getModelGate(roadType);
                segment[segmentId].addChild(extraGate);
                extraGate.setTranslation(0, 0, 140);
            }
        }

        //move forward
        segment[segmentId].setTranslation(0, 0, segmentZ[segmentId]);
        
        //add power-up
        if(distance < 50/* || segmentId % 2 != 0*/) return;
        if(RandomHelper.randChance(40)) {
            int powerUpPositionX = RandomHelper.randShort(-1, 1)*Lane.LANE_WIDTH;
            int powerUpPositionZ = segmentZ[segmentId];
            for (byte i = 0; i < 5; i++) {
                powerUpPositionZ += 60;
                coinList.addElement(new Coin(powerUpPositionX, powerUpPositionZ));
            }
        }
    }
}
