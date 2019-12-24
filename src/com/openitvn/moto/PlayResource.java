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

import com.openitvn.game.ImageHelper;
import com.openitvn.game.ModelHelper;
import com.openitvn.game.ResourceLoader;
import java.util.Vector;
import javax.microedition.lcdui.Image;
import javax.microedition.m3g.Group;
import javax.microedition.m3g.Image2D;
import javax.microedition.m3g.Mesh;
import javax.microedition.m3g.Texture2D;
import javax.microedition.m3g.World;

/**
 *
 * @author Thinh Pham
 */
public class PlayResource extends ResourceLoader {
    private static final byte MISC_COIN = 0;
    
    public static PlayResource createInstance() {
        instance = new PlayResource();
        return (PlayResource)instance;
    }
    public static PlayResource getInstance() { 
        if(instance instanceof PlayResource) return (PlayResource)instance; 
        else return null;
    }
    
    private Group modelRacer;
    public Group getModelRacer() { return modelRacer; }
    
    private Mesh modelHumanDie;
    public Mesh getModelHumanDie() { return modelHumanDie; }
    
    public Group getModelBikeDie() {
        Group model = new Group();
        Mesh bike = (Mesh)modelRacer.getChild(Racer.MESH_BIKE).duplicate();
        bike.setOrientation(90, 0, 0, -1);
        bike.setTranslation(0, 2, 0);
        model.addChild(bike);
        model.setOrientation(90, 0, 1, 0);
        return model;
    }
    
    private final Mesh[] modelVehicle = new Mesh[4];
    private final Texture2D[][] textureVehicle = new Texture2D[Vehicle.TOTAL_TYPE][Vehicle.TOTAL_COLOR];
    public Mesh getModelVehicle(byte typeId, byte colorId) {
        Mesh mVehicle = (Mesh)modelVehicle[typeId].duplicate();
        ModelHelper.applyTexture(textureVehicle[typeId][colorId], mVehicle);
        return mVehicle;
    }
    private final Texture2D[][] textureTail = new Texture2D[Vehicle.TOTAL_TYPE][2];
    public Texture2D getTextureTail(byte typeId, byte side) { return textureTail[typeId][side]; } 
    
    private Group[] modelRoad;
    public Group getModelRoad(byte roadType) { return (Group)modelRoad[roadType].duplicate(); }
    
    private Mesh[] modelGate;
    public Mesh getModelGate(byte roadType) { return (Mesh)modelGate[roadType].duplicate(); }
    
    private Mesh modelCoin;
    public Mesh getModelCoin() { return (Mesh)modelCoin.duplicate(); }
    
    public Image2D getImageBackground(byte roadType) {
        return new Image2D(Image2D.RGB, ImageHelper.loadImage("/backgrounds/bg"+roadType+".png"));
    }
    
    private Image imageControlBackground;
    public Image getImageControlBackground() { return imageControlBackground; }
    
    private Image imageButtonPause;
    public Image getImageButtonPause() { return imageButtonPause; }
    
    private Image imageCoin;
    public Image getImageCoin() { return imageCoin; }
    
    private Image imageHandle;
    public Image getImageHandle() { return imageHandle; }
    
    private Image imageMessageRecord;
    public Image getImageMessageRecord() { return imageMessageRecord; }
    
    private Image imageMessageLevel;
    public Image getImageMessageLevel() { return imageMessageLevel; }
    
    private Image imageReady;
    public Image getImageReady() { return imageReady; }
    
    private byte racerType;
    public byte getRacerType() { return racerType; }
    
    private PlayResource() {
        super(140);
    }
    
    protected void prepareResource() {
        String qualityPrefix = Main.isHighVisualEffect() ? "h" : "l";
        World tempWorld;

        //load road models from resource
        tempWorld = ModelHelper.loadWorld("/models/"+qualityPrefix+"Road");
        modelRoad = new Group[tempWorld.getChildCount()];
        for(byte i = 0; i < modelRoad.length; i++) {
            modelRoad[i] = (Group)ModelHelper.extractNode(i, tempWorld);
            ((Mesh)modelRoad[i].getChild(0)).getAppearance(0).getCompositingMode().setColorWriteEnable(true);
            ((Mesh)modelRoad[i].getChild(1)).getAppearance(0).getCompositingMode().setColorWriteEnable(true);
        }
        try {
            Thread.sleep(300);
            setProgress(20);
        } catch (InterruptedException ex) { }

        //load vehicle models
        tempWorld = ModelHelper.loadWorld("/models/vehicle");
        for(byte i = 0; i < modelVehicle.length; i++) {
            modelVehicle[i] = (Mesh)ModelHelper.extractNode(i, tempWorld);
            modelVehicle[i].getAppearance(0).getCompositingMode().setColorWriteEnable(true);
            modelVehicle[i].getAppearance(0).setTexture(1, null);
        }
        try {
            Thread.sleep(100);
            setProgress(30);
        } catch (InterruptedException ex) { }
        
        { //load vehicles' textures
            Vector texturePack = ModelHelper.loadTexturePack("/models/vehicle");
            for(byte i = 0; i < Vehicle.TOTAL_TYPE; i++) {
                for(byte j = 0; j < Vehicle.TOTAL_COLOR; j++) {
                    int texId = i*Vehicle.TOTAL_COLOR+j;
                    textureVehicle[i][j] = ModelHelper.extractTexture(texId, texturePack);
                }
            }
        }
        { //load vehicles' tails
            Vector texturePack = ModelHelper.loadTexturePack("/models/tail");
            for(byte i = 0; i < Vehicle.TOTAL_TYPE; i++) {
                for(byte j = 0; j < 2; j++) {
                    int texId = i*2+j;
                    textureTail[i][j] = ModelHelper.extractTexture(texId, texturePack);
                }
            }
        }
        try {
            Thread.sleep(100);
            setProgress(40);
        } catch (InterruptedException ex) { }

        //load tunnel's gate models from resource
        tempWorld = ModelHelper.loadWorld("/models/"+qualityPrefix+"Gate");
        modelGate = new Mesh[tempWorld.getChildCount()];
        for(byte i = 0; i < modelGate.length; i++) {
            modelGate[i] = (Mesh)ModelHelper.extractNode(i, tempWorld);
            modelGate[i].getAppearance(0).getCompositingMode().setColorWriteEnable(true);
        }
        try {
            Thread.sleep(200);
            setProgress(50);
        } catch (InterruptedException ex) { }

        //load racer from resource
        racerType = Byte.parseByte(Profile.getInstance().getSetting(Profile.RECORD_CURRENT_BIKE));
        tempWorld = ModelHelper.loadWorld("/models/racer");
        modelRacer = (Group)ModelHelper.extractNode(racerType, tempWorld);
        ((Mesh)modelRacer.getChild(Racer.MESH_BIKE)).getAppearance(0).getCompositingMode().setColorWriteEnable(true);
        ((Mesh)modelRacer.getChild(Racer.MESH_HUMAN)).getAppearance(0).getCompositingMode().setColorWriteEnable(true);
        { //change moto's texture
            Vector texturePack = ModelHelper.loadTexturePack("/models/moto");
            ModelHelper.applyTexture(
                ModelHelper.extractTexture(racerType, texturePack),
                (Mesh)modelRacer.getChild(Racer.MESH_BIKE)
            );
        }
        { //apply human's texture
            Vector texturePack = ModelHelper.loadTexturePack("/models/human");
            Texture2D texHuman = ModelHelper.extractTexture(racerType, texturePack);
            ModelHelper.applyTexture(
                    texHuman,
                    (Mesh)modelRacer.getChild(Racer.MESH_HUMAN)
            );
            //load dead human from resource
            modelHumanDie = (Mesh)ModelHelper.extractNode(1, tempWorld);
            ModelHelper.applyTexture(texHuman, modelHumanDie);
        }
        try {
            Thread.sleep(200);
            setProgress(60);
        } catch (InterruptedException ex) { }

        //load background
        imageControlBackground = ImageHelper.loadImage("/images/controlBackground.png");
        try {
            Thread.sleep(300);
            setProgress(80);
        } catch (InterruptedException ex) { }

        imageButtonPause = ImageHelper.loadImage("/images/btnPause.png");
        imageCoin = ImageHelper.loadImage("/images/coin.png");
        imageMessageRecord = ImageHelper.loadImage("/images/msgRecord.png");
        imageMessageLevel = ImageHelper.loadImage("/images/msgLevel.png");
        imageHandle = ImageHelper.loadImage("/images/handle.png");
        try {
            Thread.sleep(100);
            setProgress(90);
        } catch (InterruptedException ex) { }

        //load powerup and misc
        tempWorld = ModelHelper.loadWorld("/models/misc");
        modelCoin = (Mesh)ModelHelper.extractNode(MISC_COIN, tempWorld);
        modelCoin.getAppearance(0).getCompositingMode().setColorWriteEnable(true);

        imageReady = ImageHelper.loadImage("/images/ready.png");
        try {
            Thread.sleep(100);
            setProgress(100);
        } catch (InterruptedException ex) { }
    }
    
    public void dispose() {
        instance = null;
    }
}
