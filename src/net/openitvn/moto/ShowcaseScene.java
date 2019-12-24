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

import net.openitvn.game.Button;
import net.openitvn.game.GameScene;
import net.openitvn.game.ImageHelper;
import net.openitvn.game.StringHelper;
import net.openitvn.game.bounding.Point;
import net.openitvn.game.bounding.Rectangle;
//#if Nokia_240_320_Touch
//# import javax.microedition.lcdui.Command;
//# import javax.microedition.lcdui.CommandListener;
//# import javax.microedition.lcdui.Displayable;
//#endif
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.m3g.Background;
import javax.microedition.m3g.CompositingMode;
import javax.microedition.m3g.Graphics3D;
import javax.microedition.m3g.Group;
import javax.microedition.m3g.Light;
import javax.microedition.m3g.Mesh;
import javax.microedition.m3g.World;

/**
 *
 * @author Thinh Pham
 */
public class ShowcaseScene extends GameScene implements IDialogHolder
//#if Nokia_240_320_Touch
//# , CommandListener
//#endif
{
//#if ScreenHeight == 320
//#     public static final int VIEWPOT_HEIGHT = 210;
//#elif ScreenHeight == 400
    public static final int VIEWPOT_HEIGHT = 270;
//#endif
    public static final byte STATE_MAIN = 1;
    private static final byte MOVE_STAND = 0;
    private static final byte MOVE_LEFT = 1;
    private static final byte MOVE_RIGHT = 2;
    
    private static final int PRICE_PINK = 1000;
    private static final int PRICE_GREEN = 3000;
    private static final int PRICE_RED = 6000;
    
    private static final byte COMMAND_REPAIR_AND_CONTINUE = 1;
    private static final byte COMMAND_REPAIR = 2;
    private static final byte COMMAND_BUY_PINK = 3;
    private static final byte COMMAND_BUY_GREEN = 4;
    private static final byte COMMAND_BUY_RED = 5;
    
    public static ShowcaseScene getInstance() {
        if(instance instanceof ShowcaseScene) return (ShowcaseScene)instance;
        else return null;
    }
    
    private Graphics3D mGraphics3D;
    private World mWorld;
    public World getWorld() { return mWorld; }
    private CustomCamera mCamera;
    private Background mBackground;
    
    private Group mMainGroup;
    private Group[] mRacer;
    private CompositingMode[] mRacerCompositing;
    
//#if ScreenHeight == 320
//#     private final Rectangle btnLeft = new Rectangle(0, 70, 60, 60);
//#     private final Rectangle btnRight = new Rectangle(Main.SCREENSIZE_WIDTH-60, 70, 60, 60);
//#elif ScreenHeight == 400
    private final Rectangle btnLeft = new Rectangle(0, 94, 60, 60);
    private final Rectangle btnRight = new Rectangle(Main.SCREENSIZE_WIDTH-60, 94, 60, 60);
//#endif
    
    private Button btnMenu, btnPlay, btnBuy, btnRepair;
    
    private ShowcaseResource resource;
    private byte moveState;
    private int angle, targetAngle;
    private byte currentMoto;
    
    private final boolean[] motoUnlocked = new boolean[Racer.TOTAL_TYPE];
    private final int[] motoDurability = new int[Racer.TOTAL_TYPE];
    private final int[] motoSpeed = new int[] { 65, 65, 65, 65 };
    private final int[] motoHandle = new int[] { 45, 60, 75, 90 };
    private int showDurability, durabilityBarWidth;
    private int showSpeed, speedBarWidth;
    private int showHandle, handleBarWidth;
    private String currentPrice;
//#if ScreenHeight == 400
    private StringBuffer showPrice = new StringBuffer();
//#endif
    private int totalCoin;
    private String strShowCoin;
    private void spendCoin(int price) {
        totalCoin -= price;
        strShowCoin = StringHelper.formatNumber(totalCoin) + " x";
        profile.storeSetting(Profile.RECORD_COIN, Integer.toString(totalCoin));
    }
    
    private Dialog dialog;
    private Image imgSnapshot;
    
    private Image imgLoading, imgBackgroundBottom, imgForeground, imgLock;
    private Main main;
    private Profile profile;

    public ShowcaseScene() {
        super(Main.getInstance(), 15);
        
        totalCoin = Integer.parseInt(profile.getSetting(Profile.RECORD_COIN));
        strShowCoin = StringHelper.formatNumber(totalCoin) + " x";
        
        beginLoading();
    }

    protected void prepareResource() {
        main = Main.getInstance();
        profile = Profile.getInstance();
        resource = ShowcaseResource.createInstance();
        
        imgLoading = ImageHelper.loadImage("/images/loading.png");
//#if Nokia_240_320_Touch
//#         addCommand(new Command("Back", Command.BACK, 1));
//#         setCommandListener(this);
//#endif
    }
    
    private void prepare3DResource() {
        //create scene's world
        mGraphics3D = Graphics3D.getInstance();
        mWorld = new World();
        
        //create scene's light
        Light mLight = new Light();
        mLight.setColor(0xffffff);
        mLight.setMode(Light.AMBIENT);
        mLight.setIntensity(4);
        mWorld.addChild(mLight);
        
        mBackground = new Background();
        mBackground.setImageMode(Background.BORDER, Background.BORDER);
        mWorld.setBackground(mBackground);
        if(Main.isHighVisualEffect()) {
            mBackground.setImage(resource.getImageBackgroundTop());
            mBackground.setCrop(0, 0, Main.SCREENSIZE_WIDTH, Main.SCREENSIZE_HEIGHT);
        } else {
            mBackground.setColor(0xff008ad2);
        }
        
        //create scene's camera
        mCamera = new CustomCamera(mBackground);
        mCamera.setPerspective(
            60,
            (float)Main.SCREENSIZE_WIDTH / (float)Main.SCREENSIZE_HEIGHT,
            1,
            600
        );
        mWorld.addChild(mCamera);
        mWorld.setActiveCamera(mCamera);
        mCamera.setTranslation(0, 29, -100);
        mCamera.setOrientation(180, 0, 1, 1f/3f);
        
        mMainGroup = new Group();
        mWorld.addChild(mMainGroup);
        
        mRacer = new Group[4];
        mRacerCompositing = new CompositingMode[4];
        for(byte i = 0; i < mRacer.length; i++) {
            mRacer[i] = resource.getModelRacer(i);
            mMainGroup.addChild(mRacer[i]);
            mRacerCompositing[i] = ((Mesh)mRacer[i].getChild(0)).getAppearance(0).getCompositingMode();
        }
        mRacer[0].setTranslation(0, 0, -50);
        mRacer[1].setTranslation(-50, 0, 0);
        mRacer[2].setTranslation(0, 0, 50);
        mRacer[3].setTranslation(50, 0, 0);
    }
    
    private void prepare2DResource() {
        imgBackgroundBottom = resource.getImageBackgroundBottom();
        imgForeground = resource.getImageForeground();
        imgLock = resource.getImageLock();
        
//#if ScreenHeight == 320
//#         btnMenu = new Button(resource.getImageButtonMenu(), 35, 253, 50, 35);
//#         btnBuy = new Button(resource.getImageButtonBuy(), 95, 253, 50, 35);
//#         btnPlay = new Button(resource.getImageButtonPlay(), 95, 253, 50, 35);
//#         btnRepair = new Button(resource.getImageButtonRepair(), 155, 253, 50, 35);
//#elif ScreenHeight == 400
        btnMenu = new Button(resource.getImageButtonMenu(), 35, 314, 50, 50);
        btnBuy = new Button(resource.getImageButtonBuy(), 95, 314, 50, 50);
        btnPlay = new Button(resource.getImageButtonPlay(), 95, 314, 50, 50);
        btnRepair = new Button(resource.getImageButtonRepair(), 155, 314, 50, 50);
//#endif
        
        motoUnlocked[Racer.TYPE_BLUE] = profile.getSetting(Profile.RECORD_BIKE_BLUE_UNLOCKED).equals("1");
        motoUnlocked[Racer.TYPE_PINK] = profile.getSetting(Profile.RECORD_BIKE_PINK_UNLOCKED).equals("1");
        motoUnlocked[Racer.TYPE_GREEN] = profile.getSetting(Profile.RECORD_BIKE_GREEN_UNLOCKED).equals("1");
        motoUnlocked[Racer.TYPE_RED] = profile.getSetting(Profile.RECORD_BIKE_RED_UNLOCKED).equals("1");
        motoDurability[Racer.TYPE_BLUE] = Integer.parseInt(profile.getSetting(Profile.RECORD_BIKE_BLUE_DURABILITY));
        motoDurability[Racer.TYPE_PINK] = Integer.parseInt(profile.getSetting(Profile.RECORD_BIKE_PINK_DURABILITY));
        motoDurability[Racer.TYPE_GREEN] = Integer.parseInt(profile.getSetting(Profile.RECORD_BIKE_GREEN_DURABILITY));
        motoDurability[Racer.TYPE_RED] = Integer.parseInt(profile.getSetting(Profile.RECORD_BIKE_RED_DURABILITY));
    }
    
    private void beginLoading() {
        new Thread(resource).start();
    }
    
    private void completeLoading() {
        imgLoading = null;
        prepare3DResource();
        prepare2DResource();
        
        currentMoto = Byte.parseByte(profile.getSetting(Profile.RECORD_CURRENT_BIKE));
        changeMoto();
        state = STATE_MAIN;
    }

    protected void update() {
        AdManager.autoUpdate(getFPS());
        
        if(dialog != null) {
            dialog.update();
            return;
        }
        
        switch(state) {
            case STATE_LOAD:
                if(resource.loadingComplete()) completeLoading();
                break;
                
            case STATE_MAIN:
                for(byte i = 0; i < mRacer.length; i++) {
                    mRacer[i].postRotate(-1f, 0, 1, 0);
                }
                switch(moveState) {
                    case MOVE_STAND:
                        //rise-up durability bar
                        if(showDurability < motoDurability[currentMoto] - 10) {
                            showDurability += 10;
                            durabilityBarWidth = (int)(70.f * ((float)showDurability / 100.f));
                        } else if(showDurability != motoDurability[currentMoto]) {
                            showDurability = motoDurability[currentMoto];
                            durabilityBarWidth = (int)(70.f * ((float)showDurability / 100.f));
                        }
                        
                        //rise-up speed bar
                        if(showSpeed < motoSpeed[currentMoto] - 10) {
                            showSpeed += 10;
                            speedBarWidth = (int)(70.f * ((float)showSpeed / 100.f));
                        } else if(showSpeed != motoSpeed[currentMoto]) {
                            showSpeed = motoSpeed[currentMoto];
                            speedBarWidth = (int)(70.f * ((float)showSpeed / 100.f));
                        }
                        
                        //rise-up handle bar
                        if(showHandle < motoHandle[currentMoto] - 10) {
                            showHandle += 10;
                            handleBarWidth = (int)(70.f * ((float)showHandle / 100.f));
                        } else if(showHandle != motoHandle[currentMoto]) {
                            showHandle = motoHandle[currentMoto];
                            handleBarWidth = (int)(70.f * ((float)showHandle / 100.f));
                        }
                        
//#if ScreenHeight == 400
                        int showPriceLength = showPrice.length();
                        if(showPriceLength < currentPrice.length()) {
                            showPrice.append(currentPrice.charAt(showPriceLength));
                        }
//#endif
                        break;
                        
                    case MOVE_LEFT:
                        if(angle != targetAngle) {
                            angle += 5; //phai la uoc cua 90
                            if(angle > 360) angle -= 360;
                            mMainGroup.setOrientation(angle, 0, 1, 0);
                        }
                        else changeMoto();
                        break;
                        
                    case MOVE_RIGHT:
                        if(angle != targetAngle) {
                            angle -= 5; //phai la uoc cua 90
                            if(angle < 0) angle += 360;
                            mMainGroup.setOrientation(angle, 0, 1, 0);
                        }
                        else changeMoto();
                        break;
                }
                break;
        }
    }
    
    public void paint(Graphics g) {
        if(dialog != null) {
            if(imgSnapshot != null) g.drawImage(imgSnapshot, 0, 0, Graphics.LEFT | Graphics.TOP);
            dialog.paint(g);
            return;
        }
        
        switch(state) {
            case STATE_LOAD:
                g.setColor(0x000000);
                g.fillRect(0, 0, Main.SCREENSIZE_WIDTH, Main.SCREENSIZE_HEIGHT);
                g.setFont(Main.FontBold);
                g.setColor(0xffffff);
                g.drawRect(47, Main.SCREENSIZE_HEIGHT/2-5, 146, 10);
                g.fillRect(50, Main.SCREENSIZE_HEIGHT/2-2, resource.getLoadingBarWidth(), 4);
                g.drawImage(imgLoading, 50+resource.getLoadingBarWidth(), Main.SCREENSIZE_HEIGHT/2-12, Graphics.HCENTER | Graphics.BOTTOM);
                break;
                
            case STATE_MAIN:
                g.setFont(Main.FontBold);
                mGraphics3D.bindTarget(g);
                mGraphics3D.render(mWorld);
                mGraphics3D.releaseTarget();
                if(Main.isHighVisualEffect()) {
                    g.drawImage(imgBackgroundBottom, 0, VIEWPOT_HEIGHT, Graphics.LEFT | Graphics.TOP);
                } else {
                    g.setColor(0x008ad2);
                    g.fillRect(0, ShowcaseScene.VIEWPOT_HEIGHT, 0, Main.SCREENSIZE_HEIGHT - ShowcaseScene.VIEWPOT_HEIGHT);
                }
                g.drawImage(imgForeground, 0, 0, Graphics.LEFT | Graphics.TOP);
                btnMenu.paint(g);
                g.setColor(0xffffff);
                g.drawString(strShowCoin, 202, 60, Graphics.BASELINE | Graphics.RIGHT);
                if(!motoUnlocked[currentMoto]) {
//#if ScreenHeight == 320
//#                     if(moveState == MOVE_STAND) g.drawImage(imgLock, Main.SCREENSIZE_WIDTH/2, 84, Graphics.TOP | Graphics.HCENTER);
//#elif ScreenHeight == 400
                    if(moveState == MOVE_STAND) g.drawImage(imgLock, Main.SCREENSIZE_WIDTH/2, 110, Graphics.TOP | Graphics.HCENTER);
//#endif
                    btnBuy.paint(g);
//#if ScreenHeight != 400
//#                     g.drawString(currentPrice, Main.SCREENSIZE_WIDTH/2, 150, Graphics.BASELINE | Graphics.HCENTER);
//#endif
                } else {
                    btnPlay.paint(g);
                }
                btnRepair.paint(g);
                g.setColor(0x00ff00);
//#if ScreenHeight == 400
                g.fillRect(136, 267, handleBarWidth, 10);
                g.fillRect(136, 243, speedBarWidth, 10);
                g.setColor(0xffffff);
                g.drawString(showPrice.toString(), 136, 301, Graphics.LEFT | Graphics.BASELINE);
                if(motoDurability[currentMoto] < 20) g.setColor(0xff0000);
                else if(motoDurability[currentMoto] < 50) g.setColor(0xffff00);
                else g.setColor(0x00ff00);
                g.fillRect(136, 219, durabilityBarWidth, 10);
//#else
//#                 g.fillRect(136, 229, handleBarWidth, 10);
//#                 g.fillRect(136, 205, speedBarWidth, 10);
//#                 if(motoDurability[currentMoto] < 20) g.setColor(0xff0000);
//#                 else if(motoDurability[currentMoto] < 50) g.setColor(0xffff00);
//#                 else g.setColor(0x00ff00);
//#                 g.fillRect(136, 181, durabilityBarWidth, 10);
//#endif
                break;
        }
    }
    
    private void takeSnapshot() {
        imgSnapshot = Image.createImage(Main.SCREENSIZE_WIDTH, Main.SCREENSIZE_HEIGHT);
        Graphics g = imgSnapshot.getGraphics();
        paint(g);
        g.drawImage(ImageHelper.loadImage("/images/darkScreen.png"), 0, 0, Graphics.TOP | Graphics.LEFT);
    }
    
    private void switchLeft() {
        if(--currentMoto < 0) currentMoto = 3;
        targetAngle = angle + 90;
        if(targetAngle > 360) targetAngle -= 360;
        updateNextMoto();
        moveState = MOVE_LEFT;
    }
    
    private void switchRight() {
        if(++currentMoto > 3) currentMoto = 0;
        targetAngle = angle - 90;
        if(targetAngle < 0) targetAngle += 360;
        updateNextMoto();
        moveState = MOVE_RIGHT;
    }
    
    private void updateNextMoto() {
        mRacerCompositing[currentMoto].setColorWriteEnable(true);
        showDurability = durabilityBarWidth = 0;
        showSpeed = speedBarWidth = 0;
        showHandle = handleBarWidth = 0;
//#if ScreenHeight == 400
        showPrice = new StringBuffer();
//#else
//#         currentPrice = "";
//#endif
    }
    
    private void changeMoto() {
        angle = currentMoto*-90+360;
        mMainGroup.setOrientation(angle, 0, 1, 0);
        for(byte i = 0; i < mRacer.length; i++) {
            mRacerCompositing[i].setColorWriteEnable(i == currentMoto);
        }
        
//#if ScreenHeight == 400
            currentPrice = "(owned)";
//#endif
        if(!motoUnlocked[currentMoto]) {
            switch(currentMoto) {
                case Racer.TYPE_PINK:
                    currentPrice = StringHelper.formatNumber(PRICE_PINK) + " coins";
                    break;

                case Racer.TYPE_GREEN:
                    currentPrice = StringHelper.formatNumber(PRICE_GREEN) + " coins";
                    break;

                case Racer.TYPE_RED:
                    currentPrice = StringHelper.formatNumber(PRICE_RED) + " coins";
                    break;
            }
        }
        moveState = MOVE_STAND;
    }
    
    protected void pointerPressed(int x, int y) {
        if(dialog != null) return;
        
        Point p = new Point(x, y);
        switch(state) {
            case STATE_MAIN:
                if(moveState == MOVE_STAND) {
                    if(btnLeft.contains(p)) switchLeft();
                    else if(btnRight.contains(p)) switchRight();
                    btnMenu.testHit(x, y);
                    if(!motoUnlocked[currentMoto]) btnBuy.testHit(x, y);
                    else btnPlay.testHit(x, y);
                    btnRepair.testHit(x, y);
                }
                break;
        }
    }
    
    protected void pointerDragged(int x, int y) {
        if(dialog != null) return;
        
        switch(state) {
            case STATE_MAIN:
                btnMenu.testHit(x, y);
                if(!motoUnlocked[currentMoto]) btnBuy.testHit(x, y);
                else btnPlay.testHit(x, y);
                btnRepair.testHit(x, y);
                break;
        }
    }
    
    protected void pointerReleased(int x, int y) {
        if(dialog != null) {
            dialog.pointerReleased(x, y);
            return;
        }
        
        switch(state) {
            case STATE_MAIN:
                if(btnMenu.gotClick(x, y)) {
                    main.gotoSplash();
                } else if(!motoUnlocked[currentMoto] && btnBuy.gotClick(x, y)) {
                    requestBuyMoto();
                } else if(btnPlay.gotClick(x, y)) {
                    requestBeginGame();
                } else if(btnRepair.gotClick(x, y)) {
                    requestRepair();
                }
                break;
        }
    }
    
    private void unlockMoto(byte motoType) {
        motoUnlocked[motoType] = true;
        showSpeed = 0;
        showHandle = 0;
//#if ScreenHeight == 400
        showPrice = new StringBuffer();
//#endif
        changeMoto();
        switch(motoType) {
            case Racer.TYPE_PINK:
                profile.storeSetting(Profile.RECORD_BIKE_PINK_UNLOCKED, "1");
                break;
                
            case Racer.TYPE_GREEN:
                profile.storeSetting(Profile.RECORD_BIKE_GREEN_UNLOCKED, "1");
                break;
                
            case Racer.TYPE_RED:
                profile.storeSetting(Profile.RECORD_BIKE_RED_UNLOCKED, "1");
                break;
        }
    }
    
    private void beginGame() {
        profile.storeSetting(Profile.RECORD_CURRENT_BIKE, Integer.toString(currentMoto));
        main.gotoPlay();
    }
    
    private void requestBuyMoto() {
        takeSnapshot();
        switch(currentMoto) {
            case Racer.TYPE_PINK:
                if(totalCoin < PRICE_PINK) {
                    dialog = new MessageDialog(
                        "NOT ENOUGH COINS",
                        "You have not enough coins|"+
                        "to buy this moto. Play|"+
                        "and collect more coins.",
                        Dialog.COMMAND_NONE, this
                    );
                } else {
                    dialog = new ConfirmDialog(
                        "BUY MOTO",
                        "Do you wish to spend|"+
                        StringHelper.formatNumber(PRICE_PINK) + " coins for this|"+
                        "moto.",
                        COMMAND_BUY_PINK, this
                    );
                }
                break;

            case Racer.TYPE_GREEN:
                if(totalCoin < PRICE_GREEN) {
                    dialog = new MessageDialog(
                        "NOT ENOUGH COINS",
                        "You have not enough coins|"+
                        "to buy this moto. Play|"+
                        "and collect more coins.",
                        Dialog.COMMAND_NONE, this
                    );
                } else {
                    dialog = new ConfirmDialog(
                        "BUY MOTO",
                        "Do you wish to spend|"+
                        StringHelper.formatNumber(PRICE_GREEN) + " coins for this|"+
                        "moto.",
                        COMMAND_BUY_GREEN, this
                    );
                }
                break;

            case Racer.TYPE_RED:
                if(totalCoin < PRICE_RED) {
                    dialog = new MessageDialog(
                        "NOT ENOUGH COINS",
                        "You have not enough coins|"+
                        "to buy this moto. Play|"+
                        "and collect more coins.",
                        Dialog.COMMAND_NONE, this
                    );
                } else {
                    dialog = new ConfirmDialog(
                        "BUY MOTO",
                        "Do you wish to spend|"+
                        StringHelper.formatNumber(PRICE_RED) + " coins for this|"+
                        "moto.",
                        COMMAND_BUY_RED, this
                    );
                }
                break;
        }
    }
    
    private void requestBeginGame() {
        if(motoDurability[currentMoto] <= 1) {
            takeSnapshot();
            dialog = new ConfirmDialog(
                "BIKE DAMAGED",
                "Your bike got heavy damage,|"+
                "it must be repaired first.|" +
                "Do you want spend " + Racer.calcRepairCost(motoDurability[currentMoto]) + " coins|" +
                "to repair it now?",
                COMMAND_REPAIR_AND_CONTINUE, this
            );
        } else {
            beginGame();
        }
    }
    
    private void requestRepair() {
        if(motoDurability[currentMoto] < 100) {
            takeSnapshot();
            dialog = new ConfirmDialog(
                "REPAIR MOTO",
                "Do you want spend " + Racer.calcRepairCost(motoDurability[currentMoto]) + " coins|" +
                "to repair your moto now?",
                COMMAND_REPAIR, this
            );
        }
    }
    
    public void dispose() {
        super.dispose();
        resource.dispose();
    }
    
    public void runDialogCommand(byte command) {
        dialog = null;
        imgSnapshot = null;
        switch(command) {
            case COMMAND_REPAIR_AND_CONTINUE:
            case COMMAND_REPAIR:
                spendCoin(Racer.calcRepairCost(motoDurability[currentMoto]));
                switch(currentMoto) {
                    case Racer.TYPE_BLUE:
                        profile.storeSetting(Profile.RECORD_BIKE_BLUE_DURABILITY, "100");
                        break;
                        
                    case Racer.TYPE_PINK:
                        profile.storeSetting(Profile.RECORD_BIKE_PINK_DURABILITY, "100");
                        break;
                        
                    case Racer.TYPE_GREEN:
                        profile.storeSetting(Profile.RECORD_BIKE_GREEN_DURABILITY, "100");
                        break;
                        
                    case Racer.TYPE_RED:
                        profile.storeSetting(Profile.RECORD_BIKE_RED_DURABILITY, "100");
                        break;
                }
                if(command == COMMAND_REPAIR_AND_CONTINUE) beginGame();
                break;
                
            case COMMAND_BUY_PINK:
                spendCoin(PRICE_PINK);
                unlockMoto(Racer.TYPE_PINK);
                break;
                
            case COMMAND_BUY_GREEN:
                spendCoin(PRICE_GREEN);
                unlockMoto(Racer.TYPE_GREEN);
                break;
                
            case COMMAND_BUY_RED:
                spendCoin(PRICE_RED);
                unlockMoto(Racer.TYPE_RED);
                break;
        }
    }
    
//#if Nokia_240_320_Touch
//#     public void commandAction(Command c, Displayable d) {
//#         switch(c.getCommandType()) {
//#             case Command.BACK:
//#                 if(state != STATE_LOAD) main.gotoSplash();
//#                 break;
//#         }
//#     }
//#endif
}
