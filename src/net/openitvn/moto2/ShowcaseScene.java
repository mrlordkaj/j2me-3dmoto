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
import net.openitvn.game.ModelHelper;
import net.openitvn.game.SoundManager;
import net.openitvn.game.StringHelper;
import net.openitvn.game.bounding.Point;
import net.openitvn.game.bounding.Rectangle;
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
import javax.microedition.m3g.Background;
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
//#if INAPPPURCHASE != ""
//# , IPaymentCanvas
//#endif
//#if Nokia_240_320_Touch
//# , CommandListener
//#endif
{
//#if ScreenHeight == 320
    public static final int VIEWPOT_HEIGHT = 210;
//#elif ScreenHeight == 400
//#     public static final int VIEWPOT_HEIGHT = 270;
//#endif
    public static final byte STATE_MAIN = 1;
    private static final byte MOVE_STAND = 0;
    private static final byte MOVE_LEFT = 1;
    private static final byte MOVE_RIGHT = 2;
    
    private static final int PRICE_LIZARD = 10000; //coins
    private static final int PRICE_SPIRIT = 100; //gems
    
    private static final byte COMMAND_REPAIR_AND_CONTINUE = 1;
    private static final byte COMMAND_BUY_LIZARD = 2;
    private static final byte COMMAND_BUY_SPIRIT = 3;
    private static final byte COMMAND_PURCHASE_LIZARD = 51;
    private static final byte COMMAND_PURCHASE_SPIRIT = 52;
    private static final byte COMMAND_PURCHASE_TOMAHAWK = 53;
    
    
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
    private Mesh[] mDaiBottom;
    
//#if ScreenHeight == 320
    private final Rectangle btnLeft = new Rectangle(0, 70, 60, 60);
    private final Rectangle btnRight = new Rectangle(Main.SCREENSIZE_WIDTH-60, 70, 60, 60);
//#elif ScreenHeight == 400
//#     private final Rectangle btnLeft = new Rectangle(0, 94, 60, 60);
//#     private final Rectangle btnRight = new Rectangle(Main.SCREENSIZE_WIDTH-60, 94, 60, 60);
//#endif
    
    private Button btnMenu, btnPlay, btnGarage, btnBuy;
    
    private ShowcaseResource resource;
    private byte moveState;
    private int angle, targetAngle;
    private byte currentMoto;
    
    private final boolean[] motoUnlocked = new boolean[Racer.TOTAL_TYPE];
    private final int[] durability = new int[Racer.TOTAL_TYPE];
    private int showDurability, durabilityBarWidth;
    private String currentControl, currentSkill, currentPrice;
    private StringBuffer showControl = new StringBuffer();
    private StringBuffer showSkill = new StringBuffer();
//#if ScreenHeight == 400
//#     private StringBuffer showPrice = new StringBuffer();
//#endif
    private int timeline;
    private int totalCoin, totalGem;
    private String strShowCoin, strShowGem;
    private void spendCoin(int price) {
        totalCoin -= price;
        strShowCoin = StringHelper.formatNumber(totalCoin) + " x";
        profile.storeSetting(Profile.RECORD_COIN, Integer.toString(totalCoin));
    }
    private void spendGem(int price) {
        totalGem -= price;
        strShowGem = StringHelper.formatNumber(totalGem) + " x";
        profile.storeSetting(Profile.RECORD_GEM, Integer.toString(totalGem));
    }
    
    private MessageQueue message;
    private Dialog dialog;
    private Image imgSnapshot;
    
    private Image imgLoading, imgBackgroundBottom, imgForeground, imgLock;
    private Main main;
    private Profile profile;

    public ShowcaseScene() {
        super(Main.getInstance(), 15);
        
        totalCoin = Integer.parseInt(profile.getSetting(Profile.RECORD_COIN));
        strShowCoin = StringHelper.formatNumber(totalCoin) + " x";
        totalGem = Integer.parseInt(profile.getSetting(Profile.RECORD_GEM));
        strShowGem = StringHelper.formatNumber(totalGem) + " x";
        
        beginLoading();
    }

    protected void prepareResource() {
        message = MessageQueue.createInstance();
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
        mDaiBottom = new Mesh[4];
        for(byte i = 0; i < mRacer.length; i++) {
            mRacer[i] = resource.getModelRacer(i);
            mMainGroup.addChild(mRacer[i]);
            mDaiBottom[i] = (Mesh)mRacer[i].getChild(3);
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
        btnMenu = new Button(resource.getImageButtonMenu(), 35, 253, 50, 35);
        btnPlay = new Button(resource.getImageButtonPlay(), 95, 253, 50, 35);
        btnGarage = new Button(resource.getImageButtonGarage(), 155, 253, 50, 35);
        btnBuy = new Button(resource.getImageButtonBuy(), 95, 253, 50, 35);
//#elif ScreenHeight == 400
//#         btnMenu = new Button(resource.getImageButtonMenu(), 35, 314, 50, 50);
//#         btnPlay = new Button(resource.getImageButtonPlay(), 95, 314, 50, 50);
//#         btnGarage = new Button(resource.getImageButtonGarage(), 155, 314, 50, 50);
//#         btnBuy = new Button(resource.getImageButtonBuy(), 95, 314, 50, 50);
//#endif
        
        motoUnlocked[Racer.TYPE_THUNDER] = profile.getSetting(Profile.RECORD_BIKE_THUNDER_UNLOCKED).equals("1");
        motoUnlocked[Racer.TYPE_LIZARD] = profile.getSetting(Profile.RECORD_BIKE_LIZARD_UNLOCKED).equals("1");
        motoUnlocked[Racer.TYPE_SPIRIT] = profile.getSetting(Profile.RECORD_BIKE_SPIRIT_UNLOCKED).equals("1");
        motoUnlocked[Racer.TYPE_TOMAHAWK] = profile.getSetting(Profile.RECORD_BIKE_TOMAHAWK_UNLOCKED).equals("1");
        durability[Racer.TYPE_THUNDER] = Integer.parseInt(profile.getSetting(Profile.RECORD_BIKE_THUNDER_DURABILITY));
        durability[Racer.TYPE_LIZARD] = Integer.parseInt(profile.getSetting(Profile.RECORD_BIKE_LIZARD_DURABILITY));
        durability[Racer.TYPE_SPIRIT] = Integer.parseInt(profile.getSetting(Profile.RECORD_BIKE_SPIRIT_DURABILITY));
        durability[Racer.TYPE_TOMAHAWK] = Integer.parseInt(profile.getSetting(Profile.RECORD_BIKE_TOMAHAWK_DURABILITY));
    }
    
    private void beginLoading() {
        new Thread(resource).start();
    }
    
    private void completeLoading() {
        imgLoading = null;
        SoundManager.getInstance().playMusic(Main.SOUND_MENU, SoundManager.TYPE_MIDI);
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
                    mDaiBottom[i].postRotate(2, 0, 1, 0);
                }
                switch(moveState) {
                    case MOVE_STAND:
                        if(showDurability < durability[currentMoto] - 10) {
                            showDurability += 10;
                            durabilityBarWidth = (int)(70.f * ((float)showDurability / 100.f));
                        } else if(showDurability != durability[currentMoto]) {
                            showDurability = durability[currentMoto];
                            durabilityBarWidth = (int)(70.f * ((float)showDurability / 100.f));
                        }
                        
                        if(timeline < 100) timeline++;
                        
                        if(timeline > 4) {
                            int showControlLength = showControl.length();
                            if(showControlLength < currentControl.length()) {
                                showControl.append(currentControl.charAt(showControlLength));
                            }
                        }
                        
                        if(timeline > 8) {
                            int showSkillLength = showSkill.length();
                            if(showSkillLength < currentSkill.length()) {
                                showSkill.append(currentSkill.charAt(showSkillLength));
                            }
                        }
                        
//#if ScreenHeight == 400
//#                         if(timeline > 12) {
//#                             int showPriceLength = showPrice.length();
//#                             if(showPriceLength < currentPrice.length()) {
//#                                 showPrice.append(currentPrice.charAt(showPriceLength));
//#                             }
//#                         }
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
        message.update();
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
                g.drawString(strShowGem, 202, 48, Graphics.BASELINE | Graphics.RIGHT);
                g.drawString(strShowCoin, 202, 72, Graphics.BASELINE | Graphics.RIGHT);
                if(!motoUnlocked[currentMoto]) {
//#if ScreenHeight == 320
                    if(moveState == MOVE_STAND) g.drawImage(imgLock, Main.SCREENSIZE_WIDTH/2, 84, Graphics.TOP | Graphics.HCENTER);
//#elif ScreenHeight == 400
//#                     if(moveState == MOVE_STAND) g.drawImage(imgLock, Main.SCREENSIZE_WIDTH/2, 110, Graphics.TOP | Graphics.HCENTER);
//#endif
                    btnBuy.paint(g);
//#if ScreenHeight != 400
                    g.drawString(currentPrice, Main.SCREENSIZE_WIDTH/2, 150, Graphics.BASELINE | Graphics.HCENTER);
//#endif
                } else {
                    btnPlay.paint(g);
                }
                btnGarage.paint(g);
//#if ScreenHeight == 400
//#                 g.setFont(Main.FontPlain);
//#                 g.drawString(showControl.toString(), 136, 253, Graphics.LEFT | Graphics.BASELINE);
//#                 g.drawString(showSkill.toString(), 136, 277, Graphics.LEFT | Graphics.BASELINE);
//#                 g.drawString(showPrice.toString(), 136, 301, Graphics.LEFT | Graphics.BASELINE);
//#                 if(durability[currentMoto] < 20) g.setColor(0xff0000);
//#                 else if(durability[currentMoto] < 50) g.setColor(0xffff00);
//#                 else g.setColor(0x00ff00);
//#                 g.fillRect(136, 218, durabilityBarWidth, 10);
//#else
                g.drawString(showControl.toString(), 136, 215, Graphics.LEFT | Graphics.BASELINE);
                g.drawString(showSkill.toString(), 136, 239, Graphics.LEFT | Graphics.BASELINE);
                if(durability[currentMoto] < 20) g.setColor(0xff0000);
                else if(durability[currentMoto] < 50) g.setColor(0xffff00);
                else g.setColor(0x00ff00);
                g.fillRect(136, 180, durabilityBarWidth, 10);
//#endif
                break;
        }
        message.paint(g);
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
        showDurability = durabilityBarWidth = 0;
        showControl = new StringBuffer();
        showSkill = new StringBuffer();
//#if ScreenHeight == 400
//#         showPrice = new StringBuffer();
//#else
        currentPrice = "";
//#endif
        moveState = MOVE_LEFT;
    }
    
    private void switchRight() {
        if(++currentMoto > 3) currentMoto = 0;
        targetAngle = angle - 90;
        if(targetAngle < 0) targetAngle += 360;
        showDurability = durabilityBarWidth = 0;
        showControl = new StringBuffer();
        showSkill = new StringBuffer();
//#if ScreenHeight == 400
//#         showPrice = new StringBuffer();
//#else
        currentPrice = "";
//#endif
        moveState = MOVE_RIGHT;
    }
    
    private void changeMoto() {
        angle = currentMoto*-90+360;
        mMainGroup.setOrientation(angle, 0, 1, 0);
        if(motoUnlocked[currentMoto]) {
            switch(currentMoto) {
                case Racer.TYPE_THUNDER:
                    currentControl = "lane align";
                    currentSkill = "brake";
                    break;

                case Racer.TYPE_LIZARD:
                    currentControl = "lane align";
                    currentSkill = "flying";
                    break;

                case Racer.TYPE_SPIRIT:
                    currentControl = "free drive";
                    currentSkill = "intangible";
                    break;

                case Racer.TYPE_TOMAHAWK:
                    currentControl = "free drive";
                    currentSkill = "knockout";
                    break;
            }
//#if ScreenHeight == 400
//#             currentPrice = "(owned)";
//#endif
        } else {
            switch(currentMoto) {
                case Racer.TYPE_LIZARD:
                    currentPrice = StringHelper.formatNumber(PRICE_LIZARD) + " coins";
                    break;

                case Racer.TYPE_SPIRIT:
                    currentPrice = StringHelper.formatNumber(PRICE_SPIRIT) + " gems";
                    break;

                case Racer.TYPE_TOMAHAWK:
//#if ScreenHeight == 400
//#                     currentPrice = "(nokia iap)";
//#else
                    currentPrice = "(in-app purchase)";
//#endif
                    break;
            }
            currentControl = "(locked)";
            currentSkill = "(locked)";
        }
        timeline = 0;
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
                    btnGarage.testHit(x, y);
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
                btnGarage.testHit(x, y);
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
                } else if(btnGarage.gotClick(x, y)) {
                    if(!motoUnlocked[currentMoto]) notifyMotoLocked();
                    else enterGarage();
                } else if(!motoUnlocked[currentMoto] && btnBuy.gotClick(x, y)) {
                    requestBuyMoto();
                } else if(btnPlay.gotClick(x, y)) {
                    requestBeginGame();
                }
                break;
        }
    }
    
//#if TKEY || QWERTY
    protected void keyPressed(int keyCode) {
        if (dialog != null) return;
        
        if (moveState == MOVE_STAND) {
            switch (keyCode) {
                case KeyMap.KEY_LF:
                    btnMenu.active = true;
                    break;

                case KeyMap.KEY_CF:
                case KeyMap.KEY_5:
                case KeyMap.KEY_0:
                    if (!motoUnlocked[currentMoto])
                        btnBuy.active = true;
                    else
                        btnPlay.active = true;
                    break;

                case KeyMap.KEY_RF:
                    btnGarage.active = true;
                    break;
            }
        }
    }
    
    protected void keyReleased(int keyCode) {
        if (dialog != null) {
            dialog.keyReleased(keyCode);
            return;
        }
        
        if (moveState == MOVE_STAND) {
            switch (keyCode) {
                case KeyMap.KEY_LF:
                    if (btnMenu.gotClick())
                        main.gotoSplash();
                    break;

                case KeyMap.KEY_CF:
                case KeyMap.KEY_5:
                case KeyMap.KEY_0:
                    if (!motoUnlocked[currentMoto] && btnBuy.gotClick())
                        requestBuyMoto();
                    else if (btnPlay.gotClick())
                        requestBeginGame();
                    break;

                case KeyMap.KEY_RF:
                    if (btnGarage.gotClick()) {
                        if (!motoUnlocked[currentMoto])
                            notifyMotoLocked();
                        else
                            enterGarage();
                    }
                    break;
                    
                case KeyMap.KEY_4:
                case KeyMap.KEY_LEFT:
                    switchLeft();
                    break;

                case KeyMap.KEY_6:
                case KeyMap.KEY_RIGHT:
                    switchRight();
                    break;
            }
        }
    }
//#endif
    
    private void unlockMoto(byte motoType) {
        motoUnlocked[motoType] = true;
        ModelHelper.applyTexture(resource.getTextureHuman(motoType), (Mesh)mRacer[motoType].getChild(Racer.MESH_HUMAN));
        ModelHelper.applyTexture(resource.getTextureBike(motoType), (Mesh)mRacer[motoType].getChild(Racer.MESH_BIKE));
        showControl = new StringBuffer();
        showSkill = new StringBuffer();
//#if ScreenHeight == 400
//#         showPrice = new StringBuffer();
//#endif
        changeMoto();
        Achievement achievement = Achievement.getInstance();
        switch(motoType) {
            case Racer.TYPE_LIZARD:
                profile.storeSetting(Profile.RECORD_BIKE_LIZARD_UNLOCKED, "1");
                achievement.triggerUnlockDesertLizard();
                break;
                
            case Racer.TYPE_SPIRIT:
                profile.storeSetting(Profile.RECORD_BIKE_SPIRIT_UNLOCKED, "1");
                achievement.triggerUnlockSpiritRaider();
                break;
                
            case Racer.TYPE_TOMAHAWK:
                profile.storeSetting(Profile.RECORD_BIKE_TOMAHAWK_UNLOCKED, "1");
                achievement.triggerUnlockSpiritRaider();
                break;
        }
    }
    
    private void beginGame() {
        profile.storeSetting(Profile.RECORD_CURRENT_BIKE, Integer.toString(currentMoto));
        main.gotoPlay();
    }
    
    private void notifyMotoLocked() {
        takeSnapshot();
        dialog = new MessageDialog(
            "LOCKED MOTO",
            "This moto is still locked|"+
            "by now, you must purchase|"+
            "it first.",
            Dialog.COMMAND_NONE, this
        );
    }
    
    private void enterGarage() {
        profile.storeSetting(Profile.RECORD_CURRENT_BIKE, Integer.toString(currentMoto));
        main.gotoGarage();
    }
    
    private void requestBuyMoto() {
        takeSnapshot();
        switch(currentMoto) {
            case Racer.TYPE_LIZARD:
                if(totalCoin < PRICE_LIZARD) {
                    dialog = new ConfirmDialog(
                        "NOT ENOUGH COINS",
                        "You have not enough coins|"+
                        "to buy this moto.|"+
                        "However, you can purchase|"+
                        "via Nokia IAP instead.|"+
                        "Take it now?",
                        COMMAND_PURCHASE_LIZARD, this
                    );
                } else {
                    dialog = new ConfirmDialog(
                        "BUY MOTO",
                        "Do you wish to spend|"+
                        StringHelper.formatNumber(PRICE_LIZARD) + " coins for this|"+
                        "moto.",
                        COMMAND_BUY_LIZARD, this
                    );
                }
                break;

            case Racer.TYPE_SPIRIT:
                if(totalGem < PRICE_SPIRIT) {
                    dialog = new ConfirmDialog(
                        "NOT ENOUGH GEMS",
                        "You have not enough gems|"+
                        "to buy this moto.|"+
                        "However, you can purchase|"+
                        "via Nokia IAP instead.|"+
                        "Take it now?",
                        COMMAND_PURCHASE_SPIRIT, this
                    );
                } else {
                    dialog = new ConfirmDialog(
                        "BUY MOTO",
                        "Do you wish to spend|"+
                        StringHelper.formatNumber(PRICE_SPIRIT) + " gems for this|"+
                        "moto.",
                        COMMAND_BUY_SPIRIT, this
                    );
                }
                break;

            case Racer.TYPE_TOMAHAWK:
                dialog = new ConfirmDialog(
                        "BUY MOTO",
                        "You can buy this moto|"+
                        "via Nokia In App Payment.|"+
                        "Are you want to do this?",
                        COMMAND_PURCHASE_TOMAHAWK, this
                    );
                break;
        }
    }
    
    private void requestBeginGame() {
        if(durability[currentMoto] <= 1) {
            takeSnapshot();
            dialog = new ConfirmDialog(
                "BIKE DAMAGED",
                "Your bike got heavy damage,|"+
                "it must be repaired first.|" +
                "Do you want spend " + Racer.calcRepairCost(durability[currentMoto]) + " coins|" +
                "to repair it now?",
                COMMAND_REPAIR_AND_CONTINUE, this
            );
        } else {
            beginGame();
        }
    }
    
    public void dispose() {
        super.dispose();
        resource.dispose();
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
//#             case Main.PRODUCT_LIZARD:
//#                 unlockMoto(Racer.TYPE_LIZARD);
//#                 break;
//#                 
//#             case Main.PRODUCT_SPIRIT:
//#                 unlockMoto(Racer.TYPE_SPIRIT);
//#                 break;
//#                 
//#             case Main.PRODUCT_TOMAHAWK:
//#                 unlockMoto(Racer.TYPE_TOMAHAWK);
//#                 break;
//#         }
//#     }
//# 
//#     public void paymentFail() {
//#     }
//#endif
    
    public void runDialogCommand(byte command) {
        dialog = null;
        imgSnapshot = null;
        switch (command) {
            case COMMAND_REPAIR_AND_CONTINUE:
                spendCoin(Racer.calcRepairCost(durability[currentMoto]));
                switch(currentMoto) {
                    case Racer.TYPE_THUNDER:
                        profile.storeSetting(Profile.RECORD_BIKE_THUNDER_DURABILITY, "100");
                        break;
                        
                    case Racer.TYPE_LIZARD:
                        profile.storeSetting(Profile.RECORD_BIKE_LIZARD_DURABILITY, "100");
                        break;
                        
                    case Racer.TYPE_SPIRIT:
                        profile.storeSetting(Profile.RECORD_BIKE_SPIRIT_DURABILITY, "100");
                        break;
                        
                    case Racer.TYPE_TOMAHAWK:
                        profile.storeSetting(Profile.RECORD_BIKE_TOMAHAWK_DURABILITY, "100");
                        break;
                }
                beginGame();
                break;
                
            case COMMAND_BUY_LIZARD:
                spendCoin(PRICE_LIZARD);
                unlockMoto(Racer.TYPE_LIZARD);
                break;
                
            case COMMAND_BUY_SPIRIT:
                spendGem(PRICE_SPIRIT);
                unlockMoto(Racer.TYPE_SPIRIT);
                break;
//#if INAPPPURCHASE != ""
//#             case COMMAND_INSTALL_PAYMENT:
//#                 try {
//#                     main.getPaymentManager().launchNPaySetup();
//#                 }
//#                 catch (IllegalStateException ex) { }
//#                 catch(ConnectionNotFoundException ex) { }
//#                 break;
//#                 
//#             case COMMAND_PURCHASE_LIZARD:
//#                 if (main.getPaymentManager().isNPayAvailable())
//#                     main.startPurchase(Main.PRODUCT_LIZARD, this);
//#                 else
//#                     notifyInstallPayment();
//#                 break;
//#                 
//#             case COMMAND_PURCHASE_SPIRIT:
//#                 if (main.getPaymentManager().isNPayAvailable())
//#                     main.startPurchase(Main.PRODUCT_SPIRIT, this);
//#                 else
//#                     notifyInstallPayment();
//#                 break;
//#                 
//#             case COMMAND_PURCHASE_TOMAHAWK:
//#                 if (main.getPaymentManager().isNPayAvailable())
//#                     main.startPurchase(Main.PRODUCT_TOMAHAWK, this);
//#                 else
//#                     notifyInstallPayment();
//#                 break;
//#else
            case COMMAND_PURCHASE_LIZARD:
            case COMMAND_PURCHASE_SPIRIT:
            case COMMAND_PURCHASE_TOMAHAWK:
                notifyInstallPayment();
                break;
//#endif
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
