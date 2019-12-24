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

import com.nokia.mid.ui.DeviceControl;
import net.openitvn.game.Button;
import net.openitvn.game.GameScene;
import net.openitvn.game.ImageHelper;
//#if Nokia_240_320_Touch
//# import javax.microedition.lcdui.Command;
//# import javax.microedition.lcdui.CommandListener;
//# import javax.microedition.lcdui.Displayable;
//#endif
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/**
 *
 * @author Thinh Pham
 */
public class SettingScene extends GameScene implements IDialogHolder
//#if Nokia_240_320_Touch
//# , CommandListener
//#endif
{
    public static final int ITEM_WIDTH = 172;
//#if ScreenHeight == 320
//#     public static final int ITEM_HEIGHT = 48;
//#     public static final int AREA_LEFT = 34;
//#     public static final int AREA_TOP = 78;
//#     public static final int AREA_RIGHT = 206;
//#     public static final int AREA_BOTTOM = 245;
//#elif ScreenHeight == 400
    public static final int ITEM_HEIGHT = 58;
    public static final int AREA_LEFT = 34;
    public static final int AREA_TOP = 86;
    public static final int AREA_RIGHT = 206;
    public static final int AREA_BOTTOM = 296;
//#endif
    public static final int AUTOSCROLL_STEP = 28;
    
    private Image imgBackground, imgToggle;
    private Button btnMenu, btnRemove, btnVisual, btnAccelerometer, btnBacklight;
    private boolean highVisualEffect, openAccelerometer, showAd;
    private byte backlight;
    
    private Dialog dialog;
    private Image imgSnapshot;
    private Main main;

    public SettingScene() {
        super(Main.getInstance(), 15);
    }

    protected void prepareResource() {
        highVisualEffect = Main.isHighVisualEffect();
        openAccelerometer = Main.isOpenAccelerometer();
        backlight = Main.getBacklight();
        showAd = Main.isShowAd();
        main = Main.getInstance();
        
        imgBackground = Image.createImage(Main.SCREENSIZE_WIDTH, Main.SCREENSIZE_HEIGHT);
        Graphics g = imgBackground.getGraphics();
        g.drawImage(ImageHelper.loadImage("/images/settingTop.png"), 0, 0, Graphics.LEFT | Graphics.TOP);
        g.drawImage(ImageHelper.loadImage("/images/garageBottom.png"), 0, AREA_BOTTOM, Graphics.LEFT | Graphics.TOP);
        g.drawImage(ImageHelper.loadImage("/images/garageCenter.png"), 0, AREA_TOP, Graphics.LEFT | Graphics.TOP);
        imgToggle = ImageHelper.loadImage("/images/toggle.png");
        
//#if ScreenHeight == 320
//#         btnMenu = new Button(ImageHelper.loadImage("/images/btnSummaryMenu.png"), 35, 253, 50, 35);
//#         btnRemove = new Button(ImageHelper.loadImage(showAd ? "/images/btnSummaryRemoveAds.png" : "/images/btnSummaryRemoveAdsDisabled.png"), 95, 253, 111, 35);
//#         btnVisual = new Button(ImageHelper.loadImage("/images/btnVisual.png"), 34, 78, 172, 48);
//#         btnAccelerometer = new Button(ImageHelper.loadImage("/images/btnAccelerometer.png"), 34, 135, 172, 48);
//#         btnBacklight = new Button(ImageHelper.loadImage("/images/btnBacklight.png"), 34, 192, 172, 48);
//#elif ScreenHeight == 400
        btnMenu = new Button(ImageHelper.loadImage("/images/btnSummaryMenu.png"), 35, 314, 50, 50);
        btnRemove = new Button(ImageHelper.loadImage(showAd ? "/images/btnSummaryRemoveAds.png" : "/images/btnSummaryRemoveAdsDisabled.png"), 95, 314, 111, 50);
        btnVisual = new Button(ImageHelper.loadImage("/images/btnVisual.png"), 34, 86, 172, 58);
        btnAccelerometer = new Button(ImageHelper.loadImage("/images/btnAccelerometer.png"), 34, 162, 172, 58);
        btnBacklight = new Button(ImageHelper.loadImage("/images/btnBacklight.png"), 34, 238, 172, 58);
//#endif
        
//#if Nokia_240_320_Touch
//#         addCommand(new Command("Back", Command.BACK, 1));
//#         setCommandListener(this);
//#endif
    }

    protected void update() {
        if(dialog != null) {
            dialog.update();
        }
    }
    
    public void paint(Graphics g) {
        if(dialog != null) {
            if(imgSnapshot != null) g.drawImage(imgSnapshot, 0, 0, Graphics.LEFT | Graphics.TOP);
            dialog.paint(g);
            return;
        }
        g.drawImage(imgBackground, 0, 0, Graphics.LEFT | Graphics.TOP);
        btnMenu.paint(g);
        btnRemove.paint(g);
        btnVisual.paint(g);
        btnAccelerometer.paint(g);
        btnBacklight.paint(g);
//#if ScreenHeight == 320
//#         g.drawImage(imgToggle, btnVisual.x + 72 + (highVisualEffect ? 40 : 0), btnVisual.y + 22 + (btnVisual.active ? 5 : 0), Graphics.TOP | Graphics.LEFT);
//#         g.drawImage(imgToggle, btnAccelerometer.x + 72 + (openAccelerometer ? 40 : 0), btnAccelerometer.y + 22 + (btnAccelerometer.active ? 5 : 0), Graphics.TOP | Graphics.LEFT);
//#         g.drawImage(imgToggle, btnBacklight.x + 72 + (backlight*20), btnBacklight.y + 22 + (btnBacklight.active ? 5 : 0), Graphics.LEFT | Graphics.TOP);
//#elif ScreenHeight == 400
        g.drawImage(imgToggle, btnVisual.x + 72 + (highVisualEffect ? 40 : 0), btnVisual.y + 27 + (btnVisual.active ? 5 : 0), Graphics.TOP | Graphics.LEFT);
        g.drawImage(imgToggle, btnAccelerometer.x + 72 + (openAccelerometer ? 40 : 0), btnAccelerometer.y + 27 + (btnAccelerometer.active ? 5 : 0), Graphics.TOP | Graphics.LEFT);
        g.drawImage(imgToggle, btnBacklight.x + 72, btnBacklight.y + 27 + (btnBacklight.active ? 5 : 0), Graphics.LEFT | Graphics.TOP);
//#endif
    }
    
    private void takeSnapshot() {
        imgSnapshot = Image.createImage(Main.SCREENSIZE_WIDTH, Main.SCREENSIZE_HEIGHT);
        Graphics g = imgSnapshot.getGraphics();
        paint(g);
        g.drawImage(ImageHelper.loadImage("/images/darkScreen.png"), 0, 0, Graphics.TOP | Graphics.LEFT);
    }
    
    protected void pointerPressed(int x, int y) {
        if(dialog != null) return;
        
        btnMenu.testHit(x, y);
        if(showAd) btnRemove.testHit(x, y);
        btnVisual.testHit(x, y);
        btnAccelerometer.testHit(x, y);
        btnBacklight.testHit(x, y);
    }
    
    protected void pointerDragged(int x, int y) {
        if(dialog != null) return;
        
        btnMenu.testHit(x, y);
        if(showAd) btnRemove.testHit(x, y);
        btnVisual.testHit(x, y);
        btnAccelerometer.testHit(x, y);
        btnBacklight.testHit(x, y);
    }
    
    protected void pointerReleased(int x, int y) {
        if(dialog != null) {
            dialog.pointerReleased(x, y);
            return;
        }
        
        if(btnMenu.gotClick(x, y)) {
            main.gotoSplash();
        } else if(showAd && btnRemove.gotClick(x, y)) {
            requestRemoveAd();
        } else if(btnVisual.gotClick(x, y)) {
            switchVisual();
        } else if(btnAccelerometer.gotClick(x, y)) {
            switchAccelerometer();
        } else if(btnBacklight.gotClick(x, y)) {
            changeBacklight();
        }
    }
    
    private void switchAccelerometer() {
        openAccelerometer = !openAccelerometer;
        if(openAccelerometer && !main.prepareAccelerometer()) {
            openAccelerometer = false;
            takeSnapshot();
            dialog = new MessageDialog(
                "SENSOR ERROR!",
                "Couldn't connect to the|"+
                "acceleroemeter sensor.",
                Dialog.COMMAND_NONE, this
            );
        }
        Main.setOpenAccelerometer(openAccelerometer);
    }
    
    private void switchVisual() {
        highVisualEffect = !highVisualEffect;
        Main.setHightVisualEffect(highVisualEffect);
    }
    
    private void changeBacklight() {
        if(++backlight > 2) backlight = 0;
        Main.setBacklight(backlight);
        int backlightLevel = 40+backlight*30;
        DeviceControl.setLights(0, backlightLevel);
        GameScene.setBacklightLevel(backlightLevel);
    }
    
    private void requestRemoveAd() {
        takeSnapshot();
        dialog = new MessageDialog(
            "REMOVE ADS",
            "Tired with ads? Just visit|"+
            "any banner, all ads will be|"+
            "disabled for next 4 hours.|"+
            "Of course, test connections|"+
            "are no longer required too.",
            Dialog.COMMAND_NONE, this
        );
    }

    public void runDialogCommand(byte command) {
        dialog = null;
        imgSnapshot = null;
    }

//#if Nokia_240_320_Touch
//#     public void commandAction(Command c, Displayable d) {
//#         switch(c.getCommandType()) {
//#             case Command.BACK:
//#                 main.gotoSplash();
//#                 break;
//#         }
//#     }
//#endif
}
