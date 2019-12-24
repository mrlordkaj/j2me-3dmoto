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

import net.openitvn.game.ImageHelper;
import net.openitvn.game.ModelHelper;
import net.openitvn.game.ResourceLoader;
import java.util.Vector;
import javax.microedition.lcdui.Graphics;
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
    private static final byte MISC_SLOPE = 0;
    private static final byte MISC_COIN = 1;
    private static final byte MISC_GEM = 2;
    private static final byte MISC_MAGNET = 3;
    private static final byte MISC_CHEST = 4;
    private static final byte MISC_FLASH = 5;
    private static final byte MISC_DOUBLE = 6;
    
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
    
    private Texture2D textureBikeDefault;
    public Texture2D getTextureBikeDefault() { return textureBikeDefault; }
    
    private Texture2D textureHumanDefault;
    public Texture2D getTextureHumanDefault() { return textureHumanDefault; }
    
    private Mesh modelHumanDie;
    public Mesh getModelHumanDie() { return modelHumanDie; }
    
    public Group getModelBikeDie() {
        Group model = new Group();
        Mesh bike = (Mesh)modelRacer.getChild(Racer.MESH_BIKE).duplicate();
        bike.getAppearance(0).setTexture(0, textureBikeDefault);
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
    
    private Mesh modelSlope;
    public Mesh getModelSlope() { return modelSlope; }
    
    private Texture2D textureSkill;
    public Texture2D getTextureSkill() { return textureSkill; }
    
    private Texture2D textureFlash;
    public Texture2D getTextureFlash() { return textureFlash; }
    
    private Mesh[] modelPowerUp;
    public Mesh getModelPowerUp(byte powerUpType) { return (Mesh)modelPowerUp[powerUpType].duplicate(); }
    
    public Image2D getImageBackground(byte roadType) {
        return new Image2D(Image2D.RGB, ImageHelper.loadImage("/backgrounds/bg"+roadType+".png"));
    }
    
    private Image imageControlBackground;
    public Image getImageControlBackground() { return imageControlBackground; }
    
    private Image imageControlLeft;
    public Image getImageControlLeft() { return imageControlLeft; }
    
    private Image imageControlRight;
    public Image getImageControlRight() { return imageControlRight; }
    
    private Image imageControlLeftActive;
    public Image getImageControlLeftActive() { return imageControlLeftActive; }
    
    private Image imageControlRightActive;
    public Image getImageControlRightActive() { return imageControlRightActive; }
    
    private Image imageControlSkill;
    public Image getImageControlSkill() { return imageControlSkill; }
    
    private Image imageControlPowerUp;
    public Image getImageControlPowerUp() { return imageControlPowerUp; }
    
    private Image imageDistanceBackground;
    public Image getImageDistanceBackground() { return imageDistanceBackground; }
    
    private Image imageButtonPause;
    public Image getImageButtonPause() { return imageButtonPause; }
    
    private Image imageCoin;
    public Image getImageCoin() { return imageCoin; }
    
    private Image imageFlashBar;
    public Image getImageFlashBar() { return imageFlashBar; }
    
    private Image imageDoubleBar;
    public Image getImageDoubleBar() { return imageDoubleBar; }
    
    private Image imageMagnetBar;
    public Image getImageMagnetBar() { return imageMagnetBar; }
    
    private Image imageMessageGem;
    public Image getImageMessageGem() { return imageMessageGem; }
    
    private Image imageMessageChest;
    public Image getImageMessageChest() { return imageMessageChest; }
    
    private Image imageMessageRecord;
    public Image getImageMessageRecord() { return imageMessageRecord; }
    
    private Image imageMessageLevel;
    public Image getImageMessageLevel() { return imageMessageLevel; }
    
    private Image imageReady;
    public Image getImageReady() { return imageReady; }
    
    private byte powerUpType;
    public byte getPowerUpType() { return powerUpType; }
    
    private byte racerType;
    public byte getRacerType() { return racerType; }
    
    private byte racerColor;
    public byte getRacerColor() { return racerColor; }
    
    private PlayResource() {
        super(140);
    }
    
    protected void prepareResource() {
        boolean highVisualEffect = Main.isHighVisualEffect();
        String qualityPrefix = highVisualEffect ? "h" : "l";
        World tempWorld;
        Image tempImage;

        //load road models from resource
        tempWorld = ModelHelper.loadWorld("/models/"+qualityPrefix+"Road");
        modelRoad = new Group[tempWorld.getChildCount()];
        for(byte i = 0; i < modelRoad.length; i++) {
            modelRoad[i] = (Group)ModelHelper.extractNode(i, tempWorld);
            ((Mesh)modelRoad[i].getChild(0)).getAppearance(0).getCompositingMode().setColorWriteEnable(true);
            ((Mesh)modelRoad[i].getChild(1)).getAppearance(0).getCompositingMode().setColorWriteEnable(true);
        }
        try {
            Thread.sleep(500);
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
        switch(racerType) {
            case Racer.TYPE_THUNDER:
                racerColor = Byte.parseByte(Profile.getInstance().getSetting(Profile.RECORD_BIKE_THUNDER_COLOR));
                break;

            case Racer.TYPE_LIZARD:
                racerColor = Byte.parseByte(Profile.getInstance().getSetting(Profile.RECORD_BIKE_LIZARD_COLOR));
                break;

            case Racer.TYPE_SPIRIT:
                racerColor = Byte.parseByte(Profile.getInstance().getSetting(Profile.RECORD_BIKE_SPIRIT_COLOR));
                break;

            default:
                racerColor = Byte.parseByte(Profile.getInstance().getSetting(Profile.RECORD_BIKE_TOMAHAWK_COLOR));
                break;
        }

        tempWorld = ModelHelper.loadWorld("/models/racer");
        modelRacer = (Group)ModelHelper.extractNode(racerType, tempWorld);
        ((Mesh)modelRacer.getChild(Racer.MESH_BIKE)).getAppearance(0).getCompositingMode().setColorWriteEnable(true);
        ((Mesh)modelRacer.getChild(Racer.MESH_HUMAN)).getAppearance(0).getCompositingMode().setColorWriteEnable(true);
        { //apply human's texture
            Vector texturePack = ModelHelper.loadTexturePack("/models/human");
            textureHumanDefault = ModelHelper.extractTexture(racerColor, texturePack);
            ModelHelper.applyTexture(textureHumanDefault, (Mesh)modelRacer.getChild(Racer.MESH_HUMAN));
        }
        { //apply moto's texture
            Vector texturePack = ModelHelper.loadTexturePack("/models/moto");
            int texId = racerType*Racer.TOTAL_COLOR+racerColor;
            textureBikeDefault = ModelHelper.extractTexture(texId, texturePack);
            ModelHelper.applyTexture(textureBikeDefault, (Mesh)modelRacer.getChild(Racer.MESH_BIKE));
        }
        //load dead human from resource
        modelHumanDie = (Mesh)ModelHelper.extractNode(4, tempWorld);
        ModelHelper.applyTexture(textureHumanDefault, modelHumanDie);
        try {
            Thread.sleep(200);
            setProgress(60);
        } catch (InterruptedException ex) { }

        //prepare texture skill
        if(racerType == Racer.TYPE_SPIRIT) textureSkill = generateSkillTexture(0x00d8ff);
        else if(racerType == Racer.TYPE_TOMAHAWK) textureSkill = generateSkillTexture(0xff8585);
        textureFlash = generateSkillTexture(0xffff00);

        //load background
        imageControlBackground = ImageHelper.loadImage("/images/controlBackground.png");
        //load left button
        tempImage = ImageHelper.loadImage("/images/controlButton.png");
        (imageControlLeft = Image.createImage(36, 30)).getGraphics().drawImage(tempImage, 0, 0, Graphics.TOP | Graphics.LEFT);
        (imageControlLeftActive = Image.createImage(36, 30)).getGraphics().drawImage(tempImage, -36, 0, Graphics.TOP | Graphics.LEFT);
        //load right button
        (imageControlRight = Image.createImage(36, 30)).getGraphics().drawImage(tempImage, 0, -30, Graphics.TOP | Graphics.LEFT);
        (imageControlRightActive = Image.createImage(36, 30)).getGraphics().drawImage(tempImage, -36, -30, Graphics.TOP | Graphics.LEFT);
        //load skill button
        (imageControlSkill = Image.createImage(30, 60)).getGraphics().drawImage(tempImage, -(30*racerType+72), 0, Graphics.TOP | Graphics.LEFT);
        //load power-up button
        powerUpType = Byte.parseByte(Profile.getInstance().getSetting(Profile.RECORD_CURRENT_POWERUP));
        switch(powerUpType) {
            case Power.TYPE_MAGNET:
                (imageControlPowerUp = Image.createImage(30, 60)).getGraphics().drawImage(tempImage, -192, 0, Graphics.TOP | Graphics.LEFT);
                break;

            case Power.TYPE_DOUBLE:
                (imageControlPowerUp = Image.createImage(30, 60)).getGraphics().drawImage(tempImage, -222, 0, Graphics.TOP | Graphics.LEFT);
                break;

            case Power.TYPE_FLASH:
                (imageControlPowerUp = Image.createImage(30, 60)).getGraphics().drawImage(tempImage, -252, 0, Graphics.TOP | Graphics.LEFT);
                break;

            default:
                imageControlPowerUp = null;
                break;
        }
        try {
            Thread.sleep(300);
            setProgress(80);
        } catch (InterruptedException ex) { }

        imageDistanceBackground = ImageHelper.loadImage("/images/distanceBackground.png");
        imageButtonPause = ImageHelper.loadImage("/images/btnPause.png");
        imageCoin = ImageHelper.loadImage("/images/coin.png");
        imageFlashBar = ImageHelper.loadImage("/images/barFlash.png");
        imageDoubleBar = ImageHelper.loadImage("/images/barDouble.png");
        imageMagnetBar = ImageHelper.loadImage("/images/barMagnet.png");
        imageMessageGem = ImageHelper.loadImage("/images/msgGem.png");
        imageMessageChest = ImageHelper.loadImage("/images/msgChest.png");
        imageMessageRecord = ImageHelper.loadImage("/images/msgRecord.png");
        imageMessageLevel = ImageHelper.loadImage("/images/msgLevel.png");
        try {
            Thread.sleep(100);
            setProgress(90);
        } catch (InterruptedException ex) { }

        //load powerup and misc
        tempWorld = ModelHelper.loadWorld("/models/"+qualityPrefix+"Misc");
        modelSlope = (Mesh)ModelHelper.extractNode(MISC_SLOPE, tempWorld);
        modelPowerUp = new Mesh[6];
        modelPowerUp[Power.TYPE_COIN] = (Mesh)ModelHelper.extractNode(MISC_COIN, tempWorld);
        modelPowerUp[Power.TYPE_GEM] = (Mesh)ModelHelper.extractNode(MISC_GEM, tempWorld);
        modelPowerUp[Power.TYPE_MAGNET] = (Mesh)ModelHelper.extractNode(MISC_MAGNET, tempWorld);
        modelPowerUp[Power.TYPE_CHEST] = (Mesh)ModelHelper.extractNode(MISC_CHEST, tempWorld);
        modelPowerUp[Power.TYPE_FLASH] = (Mesh)ModelHelper.extractNode(MISC_FLASH, tempWorld);
        modelPowerUp[Power.TYPE_DOUBLE] = (Mesh)ModelHelper.extractNode(MISC_DOUBLE, tempWorld);
        for(byte i = 0; i < modelPowerUp.length; i++) modelPowerUp[i].getAppearance(0).getCompositingMode().setColorWriteEnable(true);

        imageReady = ImageHelper.loadImage("/images/ready.png");
        try {
            Thread.sleep(100);
            setProgress(100);
        } catch (InterruptedException ex) { }
    }
    
    private Texture2D generateSkillTexture(int color) {
        Image img = Image.createImage(1, 1);
        Graphics g = img.getGraphics();
        g.setColor(color);
        g.fillRect(0, 0, 1, 1);
        Image2D img2D = new Image2D(Image2D.RGB, img);
        return new Texture2D(img2D);
    }
    
    public void dispose() {
        instance = null;
    }
}
