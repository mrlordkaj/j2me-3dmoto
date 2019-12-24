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

import net.openitvn.game.Button;
import net.openitvn.game.GameScene;
import net.openitvn.game.ImageHelper;
import net.openitvn.game.SoundManager;
//#if INAPPPURCHASE != ""
//# import javax.microedition.io.ConnectionNotFoundException;
//#endif
//#if Nokia_240_320_Touch
//# import javax.microedition.lcdui.Command;
//# import javax.microedition.lcdui.CommandListener;
//# import javax.microedition.lcdui.Displayable;
//#endif
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;

/**
 *
 * @author Thinh Pham
 */
public class SettingScene extends GameScene implements IDialogHolder
//#if INAPPPURCHASE != ""
//# , IPaymentCanvas 
//#endif
//#if Nokia_240_320_Touch
//# , CommandListener
//#endif
{
    private static final byte COMMAND_RESET = 1;
    private static final byte COMMAND_REMOVE_CLICK = 2;
    private static final byte COMMAND_REMOVE_PAID = 3;
//#if TKEY || QWERTY
    private static final byte ITEM_VISUAL_EFFECT = 0;
    private static final byte ITEM_ENABLE_SOUND = 1;
    private static final byte ITEM_RESET_DATA = 2;
//#endif
    
    private Image imgBackground, imgToggle;
//#if TKEY || QWERTY
    private Image imgSelector;
    private byte currentItem;
    private int selectorTop, selectorTargetTop;
    private void setCurrentItem(int value) {
        currentItem = (byte)value;
        selectorTargetTop = 78+57*currentItem;
    }
//#endif
    private Button btnMenu, btnRemove, btnVisual, btnSound, btnReset;
    private boolean highVisualEffect, enableSound, showAd;
    
    private Dialog dialog;
    private Image imgSnapshot;
    private Main main;

    public SettingScene() {
        super(Main.getInstance(), 15);

//#if TKEY || QWERTY
        setCurrentItem(ITEM_VISUAL_EFFECT);
        selectorTop = selectorTargetTop;
//#endif
    }

    protected void prepareResource() {
        highVisualEffect = Main.isHighVisualEffect();
        showAd = Main.isShowAd();
        main = Main.getInstance();
        enableSound = SoundManager.getInstance().isEnabled();
        
        imgBackground = Image.createImage(Main.SCREENSIZE_WIDTH, Main.SCREENSIZE_HEIGHT);
        Graphics g = imgBackground.getGraphics();
        g.drawImage(ImageHelper.loadImage("/images/settingTop.png"), 0, 0, Graphics.LEFT | Graphics.TOP);
        g.drawImage(ImageHelper.loadImage("/images/garageBottom.png"), 0, Garage.AREA_BOTTOM, Graphics.LEFT | Graphics.TOP);
        g.drawImage(ImageHelper.loadImage("/images/garageCenter.png"), 0, Garage.AREA_TOP, Graphics.LEFT | Graphics.TOP);
        imgToggle = ImageHelper.loadImage("/images/toggle.png");
//#if TKEY || QWERTY
        imgSelector = ImageHelper.loadImage("/images/garageSelector.png");
//#endif
        
//#if ScreenHeight == 320
        btnMenu = new Button(ImageHelper.loadImage("/images/btnSummaryMenu.png"), 35, 253, 50, 35);
        btnRemove = new Button(ImageHelper.loadImage(showAd ? "/images/btnSummaryRemoveAds.png" : "/images/btnSummaryRemoveAdsDisabled.png"), 95, 253, 111, 35);
        btnVisual = new Button(ImageHelper.loadImage("/images/btnVisual.png"), 34, 78, 172, 48);
        btnSound = new Button(ImageHelper.loadImage("/images/btnSound.png"), 34, 135, 172, 48);
        btnReset = new Button(ImageHelper.loadImage("/images/btnReset.png"), 34, 192, 172, 48);
//#elif ScreenHeight == 400
//#         btnMenu = new Button(ImageHelper.loadImage("/images/btnSummaryMenu.png"), 35, 314, 50, 50);
//#         btnRemove = new Button(ImageHelper.loadImage(showAd ? "/images/btnSummaryRemoveAds.png" : "/images/btnSummaryRemoveAdsDisabled.png"), 95, 314, 111, 50);
//#         btnVisual = new Button(ImageHelper.loadImage("/images/btnVisual.png"), 34, 86, 172, 58);
//#         btnSound = new Button(ImageHelper.loadImage("/images/btnSound.png"), 34, 162, 172, 58);
//#         btnReset = new Button(ImageHelper.loadImage("/images/btnReset.png"), 34, 238, 172, 58);
//#endif
        
//#if Nokia_240_320_Touch
//#         addCommand(new Command("Back", Command.BACK, 1));
//#         setCommandListener(this);
//#endif
    }

    protected void update() {
        if(dialog != null) {
            dialog.update();
//#if TKEY || QWERTY
            return;
//#endif
        }
        
//#if TKEY || QWERTY
        if(selectorTop < selectorTargetTop - Garage.AUTOSCROLL_STEP) selectorTop += Garage.AUTOSCROLL_STEP;
        else if(selectorTop > selectorTargetTop + Garage.AUTOSCROLL_STEP) selectorTop -= Garage.AUTOSCROLL_STEP;
        else selectorTop = selectorTargetTop;
//#endif
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
        btnSound.paint(g);
//#if ScreenHeight == 320
        g.drawImage(imgToggle, btnVisual.x + 72 + (highVisualEffect ? 40 : 0), btnVisual.y + 22 + (btnVisual.active ? 5 : 0), Graphics.TOP | Graphics.LEFT);
        g.drawImage(imgToggle, btnSound.x + 72 + (enableSound ? 40 : 0), btnSound.y + 22 + (btnSound.active ? 5 : 0), Graphics.TOP | Graphics.LEFT);
//#elif ScreenHeight == 400
//#         g.drawImage(imgToggle, btnVisual.x + 72 + (highVisualEffect ? 40 : 0), btnVisual.y + 27 + (btnVisual.active ? 5 : 0), Graphics.TOP | Graphics.LEFT);
//#         g.drawImage(imgToggle, btnSound.x + 72 + (enableSound ? 40 : 0), btnSound.y + 27 + (btnSound.active ? 5 : 0), Graphics.TOP | Graphics.LEFT);
//#endif
        btnReset.paint(g);
//#if TKEY || QWERTY
        g.drawImage(imgSelector, 34, selectorTop, Graphics.LEFT | Graphics.TOP);
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
        btnSound.testHit(x, y);
        btnReset.testHit(x, y);
    }
    
    protected void pointerDragged(int x, int y) {
        if(dialog != null) return;
        
        btnMenu.testHit(x, y);
        if(showAd) btnRemove.testHit(x, y);
        btnVisual.testHit(x, y);
        btnSound.testHit(x, y);
        btnReset.testHit(x, y);
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
        } else if(btnSound.gotClick(x, y)) {
            switchSound();
        } else if(btnReset.gotClick(x, y)) {
            requestReset();
        }
    }
    
//#if TKEY || QWERTY
    protected void keyPressed(int keyCode) {
        if (dialog != null) return;

        switch (keyCode) {
            case KeyMap.KEY_LF:
                btnMenu.active = true;
                break;
                
            case KeyMap.KEY_RF:
                if(showAd) btnRemove.active = true;
                break;
                

            case KeyMap.KEY_CF:
            case KeyMap.KEY_5:
            case KeyMap.KEY_0:
                if (currentItem == ITEM_VISUAL_EFFECT) btnVisual.active = true;
                else if(currentItem == ITEM_ENABLE_SOUND) btnSound.active = true;
                else if(currentItem == ITEM_RESET_DATA) btnReset.active = true;
                break;
        }
    }
    
    protected void keyReleased(int keyCode) {
        if (dialog != null) {
            dialog.keyReleased(keyCode);
            return;
        }
        
        switch (keyCode) {
            case KeyMap.KEY_LF:
                if (btnMenu.gotClick())
                    main.gotoSplash();
                break;
                
            case KeyMap.KEY_RF:
                if (showAd && btnRemove.gotClick())
                    requestRemoveAd();
                break;
                
            case KeyMap.KEY_2:
            case KeyMap.KEY_UP:
                if (currentItem > 0)
                    setCurrentItem(currentItem-1);
                else
                    setCurrentItem(ITEM_RESET_DATA);
                break;
                
            case KeyMap.KEY_8:
            case KeyMap.KEY_DOWN:
                if (currentItem < 2)
                    setCurrentItem(currentItem+1);
                else
                    setCurrentItem(ITEM_VISUAL_EFFECT);
                break;
                
            case KeyMap.KEY_CF:
            case KeyMap.KEY_5:
            case KeyMap.KEY_0:
                if (currentItem == ITEM_VISUAL_EFFECT && btnVisual.gotClick())
                    switchVisual();
                else if (currentItem == ITEM_ENABLE_SOUND && btnSound.gotClick())
                    switchSound();
                else if (currentItem == ITEM_RESET_DATA && btnReset.gotClick())
                    requestReset();
                break;
        }
    }
//#endif
    
    private void switchVisual() {
        highVisualEffect = !highVisualEffect;
        Main.setHightVisualEffect(highVisualEffect);
    }
    
    private void switchSound() {
        enableSound = !enableSound;
        SoundManager.getInstance().setEnabled(enableSound);
    }
    
    private void requestReset() {
        takeSnapshot();
        dialog = new ConfirmDialog(
            "RESET PROFILE",
            "This will reset all your|"+
            "personal data to default.|"+
            "Are you sure you want to|"+
            "do that?",
            COMMAND_RESET, this
        );
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
            COMMAND_REMOVE_CLICK, this
        );
    }

    public void runDialogCommand(byte command) {
        dialog = null;
        imgSnapshot = null;
        switch(command) {
            case COMMAND_RESET:
                try {
                    RecordStore.deleteRecordStore(Profile.RECORD_PROFILE);
                    RecordStore.deleteRecordStore(Achievement.RECORD_ACHIEVEMENT);
                } catch (RecordStoreException ex) { }
                break;
//#if INAPPPURCHASE != ""
//#             case COMMAND_INSTALL_PAYMENT:
//#                 try {
//#                     main.getPaymentManager().launchNPaySetup();
//#                 }
//#                 catch (IllegalStateException ex) { }
//#                 catch (ConnectionNotFoundException ex) { }
//#                 break;
//#                 
//#             case COMMAND_REMOVE_CLICK:
//#                 takeSnapshot();
//#                 dialog = new ConfirmDialog(
//#                     "ANOTHER WAY",
//#                     "You can also upgrade app|"+
//#                     "to premium version to|"+
//#                     "remove ads forever. Do|"+
//#                     "you want to upgrade now?",
//#                     COMMAND_REMOVE_PAID, this
//#                 );
//#                 break;
//#                 
//#             case COMMAND_REMOVE_PAID:
//#                 if (main.getPaymentManager().isNPayAvailable())
//#                     main.startPurchase(Main.PRODUCT_REMOVEAD, this);
//#                 else
//#                     notifyInstallPayment();
//#                 break;
//#else
            case COMMAND_REMOVE_PAID:
                notifyInstallPayment();
                break;
//#endif
        }
    }

    public void notifyInstallPayment() {
        takeSnapshot();
//#if INAPPPURCHASE != ""
//#         dialog = new ConfirmDialog(
//#             "MISSING COMPONENT",
//#             "You need to install Nokia|"+
//#             "In App Payment support|"+
//#             "to use this function!|"+
//#             "Maybe restart is required.",
//#             COMMAND_INSTALL_PAYMENT, this
//#         );
//#else
        dialog = new MessageDialog(
            "MISSING COMPONENT",
            "Sorry, this app currently|"+
            "support Nokia IAP only.|",
            Dialog.COMMAND_NONE, this
        );
//#endif
    }

//#if Nokia_240_320_Touch
//#     public void paymentSuccess(byte purchaseType) {
//#         switch(purchaseType) {
//#             case Main.PRODUCT_REMOVEAD:
//#                 showAd = false;
//#                 btnRemove = new Button(ImageHelper.loadImage("/images/btnSummaryRemoveAdsDisabled.png"), 95, 253, 111, 35);
//#                 Main.setShowAd(showAd);
//#                 break;
//#         }
//#     }
//# 
//#     public void paymentFail() {
//#     }
//#     
//#     public void commandAction(Command c, Displayable d) {
//#         switch(c.getCommandType()) {
//#             case Command.BACK:
//#                 main.gotoSplash();
//#                 break;
//#         }
//#     }
//#endif
}
