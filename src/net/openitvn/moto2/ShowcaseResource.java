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
public class ShowcaseResource extends ResourceLoader {
    private static final byte MISC_DAI_BOTTOM = 5;
    private static final byte MISC_DAI_TOP = 6;
    
    public static ShowcaseResource createInstance() {
        instance = new ShowcaseResource();
        return (ShowcaseResource)instance;
    }
    public static ShowcaseResource getInstance() { 
        if(instance instanceof ShowcaseResource) return (ShowcaseResource)instance; 
        else return null;
    }
    
    private Image2D imageBackgroundTop;
    public Image2D getImageBackgroundTop() { return imageBackgroundTop; }
    
    private Image imageBackgroundBottom;
    public Image getImageBackgroundBottom() { return imageBackgroundBottom; }
    
    private Image imageForeground;
    public Image getImageForeground() { return imageForeground; }
    
    private final Texture2D[] textureHuman = new Texture2D[Racer.TOTAL_TYPE];
    public Texture2D getTextureHuman(byte type) { return textureHuman[type]; }
    
    private final Texture2D[] textureBike = new Texture2D[Racer.TOTAL_TYPE];
    public Texture2D getTextureBike(byte type) { return textureBike[type]; }
    
    private final Group[] modelRacer = new Group[Racer.TOTAL_TYPE];
    public Group getModelRacer(byte type) { return modelRacer[type]; }
    
    private Image imageButtonMenu;
    public Image getImageButtonMenu() { return imageButtonMenu; }
    
    private Image imageButtonPlay;
    public Image getImageButtonPlay() { return imageButtonPlay; }
    
    private Image imageButtonGarage;
    public Image getImageButtonGarage() { return imageButtonGarage; }
    
    private Image imageButtonBuy;
    public Image getImageButtonBuy() { return imageButtonBuy; }
    
    private Image imageLock;
    public Image getImageLock() { return imageLock; }
    
    private ShowcaseResource() {
        super(140);
    }
    
    protected void prepareResource() {
        World tempWorld;
        Image tempImage;

        tempWorld = ModelHelper.loadWorld("/models/racer");
        //load dai model
        Mesh modelDaiBottom = (Mesh)ModelHelper.extractNode(MISC_DAI_BOTTOM, tempWorld);
        modelDaiBottom.getAppearance(0).getCompositingMode().setColorWriteEnable(true);
        Mesh modelDaiTop = (Mesh)ModelHelper.extractNode(MISC_DAI_TOP, tempWorld);
        modelDaiTop.getAppearance(0).getCompositingMode().setColorWriteEnable(true);
        try {
            Thread.sleep(300);
            setProgress(20);
        } catch (InterruptedException ex) { }
        
        { //load humans' textures
            Vector texturePack = ModelHelper.loadTexturePack("/models/human");
            for(byte i = 0; i < Racer.TOTAL_TYPE; i++) {
                byte racerColor = isBikeUnlocked(i) ? getBikeColor(i) : Racer.COLOR_BLUE;
                textureHuman[i] = ModelHelper.extractTexture(racerColor, texturePack);
            }
        }
        { //load motos' textures
            Vector texturePack = ModelHelper.loadTexturePack("/models/moto");
            for(byte i = 0; i < Racer.TOTAL_TYPE; i++) {
                byte racerColor = isBikeUnlocked(i) ? getBikeColor(i) : Racer.COLOR_BLUE;
                int texId = i*Racer.TOTAL_COLOR+racerColor;
                textureBike[i] = ModelHelper.extractTexture(texId, texturePack);
            }
        }
        try {
            Thread.sleep(100);
            setProgress(40);
        } catch (InterruptedException ex) { }
        
        //load racer models
        for(byte i = 0; i < 4; i++) {
            modelRacer[i] = (Group)ModelHelper.extractNode(i, tempWorld);
            if(isBikeUnlocked(i)) {
                ModelHelper.applyTexture(textureBike[i], (Mesh)modelRacer[i].getChild(Racer.MESH_BIKE));
                ModelHelper.applyTexture(textureHuman[i], (Mesh)modelRacer[i].getChild(Racer.MESH_HUMAN));
            }
            modelRacer[i].addChild((Mesh)modelDaiTop.duplicate());
            modelRacer[i].addChild((Mesh)modelDaiBottom.duplicate());
            modelRacer[i].postRotate(180, 0, 1, 0);
        }
        try {
            Thread.sleep(400);
            setProgress(60);
        } catch (InterruptedException ex) { }

        //load background
        Image imgBgTop = Image.createImage(Main.SCREENSIZE_WIDTH, ShowcaseScene.VIEWPOT_HEIGHT);
        tempImage = ImageHelper.loadImage("/backgrounds/galaxy.png");
        imgBgTop.getGraphics().drawImage(tempImage, 0, 0, Graphics.LEFT | Graphics.TOP);
        imageBackgroundTop = new Image2D(Image2D.RGB, imgBgTop);
        imageBackgroundBottom = Image.createImage(Main.SCREENSIZE_WIDTH, Main.SCREENSIZE_HEIGHT - ShowcaseScene.VIEWPOT_HEIGHT);
        imageBackgroundBottom.getGraphics().drawImage(tempImage, 0, imageBackgroundBottom.getHeight(), Graphics.LEFT | Graphics.BOTTOM);
        try {
            Thread.sleep(100);
            setProgress(80);
        } catch (InterruptedException ex) { }

        //load images
        imageForeground = ImageHelper.loadImage("/images/selectMoto.png");
        imageButtonMenu = ImageHelper.loadImage("/images/btnSummaryMenu.png");
        imageButtonPlay = ImageHelper.loadImage("/images/btnSummaryPlay.png");
        imageButtonGarage = ImageHelper.loadImage("/images/btnSummaryGarage.png");
        imageButtonBuy = ImageHelper.loadImage("/images/btnSummaryBuy.png");
        imageLock = ImageHelper.loadImage("/images/lock.png");
        try {
            Thread.sleep(400);
            setProgress(100);
        } catch (InterruptedException ex) { }
    }
    
    private boolean isBikeUnlocked(byte type) {
        byte recordLine;
        switch(type) {
            case Racer.TYPE_THUNDER:
                recordLine = Profile.RECORD_BIKE_THUNDER_UNLOCKED;
                break;
                
            case Racer.TYPE_LIZARD:
                recordLine = Profile.RECORD_BIKE_LIZARD_UNLOCKED;
                break;
                
            case Racer.TYPE_SPIRIT:
                recordLine = Profile.RECORD_BIKE_SPIRIT_UNLOCKED;
                break;
                
            default:
                recordLine = Profile.RECORD_BIKE_TOMAHAWK_UNLOCKED;
                break;
        }
        return Profile.getInstance().getSetting(recordLine).equals("1");
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
