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
public class GarageResource extends ResourceLoader {
    public static GarageResource createInstance() {
        instance = new GarageResource();
        return (GarageResource)instance;
    }
    public static GarageResource getInstance() { 
        if(instance instanceof GarageResource) return (GarageResource)instance;
        else return null; 
    }
    
    private Group modelRacer;
    public Group getModelRacer() { return modelRacer; }
    
    private final Texture2D[] textureBike = new Texture2D[4];
    public Texture2D getTextureBike(byte colorId) { return textureBike[colorId]; }
    
    private final Texture2D[] textureHuman = new Texture2D[4];
    public Texture2D getTextureHuman(byte colorId) { return textureHuman[colorId]; }
    
    private Image imageButtonShowcase;
    public Image getImageButtonShowcase() { return imageButtonShowcase; }
    
    private Image2D imageViewpotBackground;
    public Image2D getImageViewpotBackground() { return imageViewpotBackground; }
    
    private Image imageGarageTop;
    public Image getImageGarageTop() { return imageGarageTop; }
    
    private Image imageGarageCenter;
    public Image getImageGarageCenter() { return imageGarageCenter; }
    
    public Image imageGarageBottom;
    public Image getImageGarageBottom() { return imageGarageBottom; }
    
    private Image imageDarkScreen;
    public Image getImageDarkScreen() { return imageDarkScreen; }
    
    private Image imageUpgradeLevel;
    public Image getImageUpgradeLevel() { return imageUpgradeLevel; }
    
    private Image imageActive;
    public Image getImageActive() { return imageActive; }
    
//#if TKEY || QWERTY
    private Image imageItemSelector;
    public Image getImageItemSelector() { return imageItemSelector; }
//#endif
    
    private final Image[] imageItem = new Image[8];
    public Image[] getImageItems() { return imageItem; }
    public Image getImageItem(int itemId) { return imageItem[itemId]; }
    
    private byte racerType;
    public byte getRacerType() { return racerType; }
    
    private byte racerColor;
    public byte getRacerColor() { return racerColor; }
    
    private GarageResource() {
        super(140);
    }
    
    protected void prepareResource() {
        Profile profile = Profile.getInstance();
        World tempWorld;
        Image tempImage;

        //load racer model from resource
        racerType = Byte.parseByte(profile.getSetting(Profile.RECORD_CURRENT_BIKE));
        tempWorld = ModelHelper.loadWorld("/models/racer");
        modelRacer = (Group)ModelHelper.extractNode(racerType, tempWorld);
        ((Mesh)modelRacer.getChild(Racer.MESH_BIKE)).getAppearance(0).getCompositingMode().setColorWriteEnable(true);
        ((Mesh)modelRacer.getChild(Racer.MESH_HUMAN)).getAppearance(0).getCompositingMode().setColorWriteEnable(true);
        try {
            Thread.sleep(100);
            setProgress(30);
        } catch (InterruptedException ex) { }
        
        { //load humans' textures
            Vector texturePack = ModelHelper.loadTexturePack("/models/human");
            for(byte i = 0; i < Racer.TOTAL_TYPE; i++) {
                textureHuman[i] = ModelHelper.extractTexture(i, texturePack);
            }
        }
        { //load motos' textures
            Vector texturePack = ModelHelper.loadTexturePack("/models/moto");
            for(byte i = 0; i < Racer.TOTAL_TYPE; i++) {
                int texId = racerType*Racer.TOTAL_COLOR+i;
                textureBike[i] = ModelHelper.extractTexture(texId, texturePack);
            }
        }
        try {
            Thread.sleep(100);
            setProgress(50);
        } catch (InterruptedException ex) { }
        //change texture
        racerColor = getBikeColor(racerType);
        ModelHelper.applyTexture(textureBike[racerColor], (Mesh)modelRacer.getChild(Racer.MESH_BIKE));
        ModelHelper.applyTexture(textureHuman[racerColor], (Mesh)modelRacer.getChild(Racer.MESH_HUMAN));
        try {
            Thread.sleep(100);
            setProgress(60);
        } catch (InterruptedException ex) { }

        //prepare images from resource
        tempImage = ImageHelper.loadImage("/images/garageItem.png");
        for(byte i = 0; i < imageItem.length; i++) {
            imageItem[i] = Image.createImage(Garage.ITEM_WIDTH, Garage.ITEM_HEIGHT);
            imageItem[i].getGraphics().drawImage(tempImage, 0, -Garage.ITEM_HEIGHT*i, Graphics.TOP | Graphics.LEFT);
        }
        tempImage = ImageHelper.loadImage("/images/garageBottom.png");
//#if ScreenHeight == 320
        imageGarageBottom = Image.createImage(160, 75);
        {
            Graphics g = imageGarageBottom.getGraphics();
            g.drawImage(tempImage, 0, 0, Graphics.LEFT | Graphics.TOP);
            g.drawImage(ImageHelper.loadImage("/images/coin.png"), 96, 26, Graphics.LEFT | Graphics.TOP);
            g.setColor(0xffffff);
            g.setFont(Main.FontBold);
            g.drawString("You have:", 96, 20, Graphics.LEFT | Graphics.BASELINE);
        }
        {
            Image imgViewpot = Image.createImage(80, 75);
            imgViewpot.getGraphics().drawImage(tempImage, imgViewpot.getWidth(), 0, Graphics.RIGHT | Graphics.TOP);
            imageViewpotBackground = new Image2D(Image2D.RGB, imgViewpot);
        }
//#elif ScreenHeight == 400
//#             imageGarageBottom = Image.createImage(160, 104);
//#             {
//#                 Graphics g = imageGarageBottom.getGraphics();
//#                 g.drawImage(tempImage, 0, 0, Graphics.LEFT | Graphics.TOP);
//#                 g.drawImage(ImageHelper.loadImage("/images/coin.png"), 96, 46, Graphics.LEFT | Graphics.TOP);
//#                 g.setColor(0xffffff);
//#                 g.setFont(Main.FontBold);
//#                 g.drawString("You have:", 96, 30, Graphics.LEFT | Graphics.BASELINE);
//#             }
//#             {
//#                 Image imgViewpot = Image.createImage(80, 104);
//#                 imgViewpot.getGraphics().drawImage(tempImage, imgViewpot.getWidth(), 0, Graphics.RIGHT | Graphics.TOP);
//#                 imageViewpotBackground = new Image2D(Image2D.RGB, imgViewpot);
//#             }
//#endif

        imageGarageTop = ImageHelper.loadImage("/images/garageTop.png");
        imageGarageCenter = ImageHelper.loadImage("/images/garageCenter.png");
        imageDarkScreen = ImageHelper.loadImage("/images/darkScreen.png");
        imageUpgradeLevel = ImageHelper.loadImage(("/images/upgradeLevel.png"));
        imageButtonShowcase = ImageHelper.loadImage("/images/btnSummaryClose.png");
        imageActive = ImageHelper.loadImage("/images/active.png");
//#if TKEY || QWERTY
        imageItemSelector = ImageHelper.loadImage("/images/garageSelector.png");
//#endif
        try {
            Thread.sleep(300);
            setProgress(100);
        } catch (InterruptedException ex) { }
    }
    
    private byte getBikeColor(byte type) {
        byte recordLine;
        switch(type) {
            case Racer.TYPE_THUNDER:
                recordLine = Profile.RECORD_BIKE_THUNDER_COLOR;
                break;
                
            case Racer.TYPE_LIZARD:
                recordLine = Profile.RECORD_BIKE_LIZARD_COLOR;
                break;
                
            case Racer.TYPE_SPIRIT:
                recordLine = Profile.RECORD_BIKE_SPIRIT_COLOR;
                break;
                
            default:
                recordLine = Profile.RECORD_BIKE_TOMAHAWK_COLOR;
                break;
        }
        return Byte.parseByte(Profile.getInstance().getSetting(recordLine));
    }
}
