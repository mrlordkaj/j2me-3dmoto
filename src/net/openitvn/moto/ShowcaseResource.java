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

import net.openitvn.game.ImageHelper;
import net.openitvn.game.ModelHelper;
import net.openitvn.game.ResourceLoader;
import java.util.Vector;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.m3g.CompositingMode;
import javax.microedition.m3g.Group;
import javax.microedition.m3g.Image2D;
import javax.microedition.m3g.Mesh;
import javax.microedition.m3g.World;

/**
 *
 * @author Thinh Pham
 */
public class ShowcaseResource extends ResourceLoader {
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
    
    private final Group[] modelRacer = new Group[Racer.TOTAL_TYPE];
    public Group getModelRacer(byte type) { return modelRacer[type]; }
    
    private Image imageButtonMenu;
    public Image getImageButtonMenu() { return imageButtonMenu; }
    
    private Image imageButtonRepair;
    public Image getImageButtonRepair() { return imageButtonRepair; }
    
    private Image imageButtonPlay;
    public Image getImageButtonPlay() { return imageButtonPlay; }
    
    private Image imageButtonBuy;
    public Image getImageButtonBuy() { return imageButtonBuy; }
    
    private Image imageLock;
    public Image getImageLock() { return imageLock; }
    
    private ShowcaseResource() {
        super(140);
    }
    
    protected void prepareResource() {
        Image tempImage;
        
        { //load racers' models
            World tempWorld = ModelHelper.loadWorld("/models/racer");
            Vector motoTexturePack = ModelHelper.loadTexturePack("/models/moto");
            Vector humanTexturePack = ModelHelper.loadTexturePack("/models/human");
            for(byte i = 0; i < 4; i++) {
                modelRacer[i] = (Group)ModelHelper.extractNode(0, tempWorld);
                ModelHelper.applyTexture(ModelHelper.extractTexture(i, motoTexturePack), (Mesh)modelRacer[i].getChild(Racer.MESH_BIKE));
                ModelHelper.applyTexture(ModelHelper.extractTexture(i, humanTexturePack), (Mesh)modelRacer[i].getChild(Racer.MESH_HUMAN));
                modelRacer[i].postRotate(180, 0, 1, 0);
                CompositingMode compositing = (CompositingMode)((Mesh)modelRacer[i].getChild(0)).getAppearance(0).getCompositingMode().duplicate();
                ((Mesh)modelRacer[i].getChild(Racer.MESH_BIKE)).getAppearance(0).setCompositingMode(compositing);
                ((Mesh)modelRacer[i].getChild(Racer.MESH_HUMAN)).getAppearance(0).setCompositingMode(compositing);
            }
        }
        try {
            Thread.sleep(800);
            setProgress(40);
        } catch (InterruptedException ex) { }

        //load background
        Image imgBgTop = Image.createImage(Main.SCREENSIZE_WIDTH, ShowcaseScene.VIEWPOT_HEIGHT);
        tempImage = ImageHelper.loadImage("/backgrounds/galaxy.png");
        imgBgTop.getGraphics().drawImage(tempImage, 0, 0, Graphics.LEFT | Graphics.TOP);
        imageBackgroundTop = new Image2D(Image2D.RGB, imgBgTop);
        imageBackgroundBottom = Image.createImage(Main.SCREENSIZE_WIDTH, Main.SCREENSIZE_HEIGHT - ShowcaseScene.VIEWPOT_HEIGHT);
        imageBackgroundBottom.getGraphics().drawImage(tempImage, 0, imageBackgroundBottom.getHeight(), Graphics.LEFT | Graphics.BOTTOM);
        try {
            Thread.sleep(200);
            setProgress(80);
        } catch (InterruptedException ex) { }

        //load images
        imageForeground = ImageHelper.loadImage("/images/selectMoto.png");
        imageButtonMenu = ImageHelper.loadImage("/images/btnSummaryMenu.png");
        imageButtonRepair = ImageHelper.loadImage("/images/btnSummaryGarage.png");
        imageButtonPlay = ImageHelper.loadImage("/images/btnSummaryPlay.png");
        imageButtonBuy = ImageHelper.loadImage("/images/btnSummaryBuy.png");
        imageLock = ImageHelper.loadImage("/images/lock.png");
        try {
            Thread.sleep(800);
            setProgress(100);
        } catch (InterruptedException ex) { }
    }
}
