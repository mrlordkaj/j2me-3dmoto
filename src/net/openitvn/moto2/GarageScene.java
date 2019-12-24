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
import javax.microedition.m3g.Camera;
import javax.microedition.m3g.Graphics3D;
import javax.microedition.m3g.Group;
import javax.microedition.m3g.Light;
import javax.microedition.m3g.Mesh;
import javax.microedition.m3g.World;

/**
 *
 * @author Thinh Pham
 */
public class GarageScene extends GameScene implements IDialogHolder
//#if INAPPPURCHASE != ""
//# , IPaymentCanvas
//#endif
//#if Nokia_240_320_Touch
//# , CommandListener
//#endif
    {
    public static final byte STATE_MAIN = 1;
    public static final byte COMMAND_REPAIR = 1;
    public static final byte COMMAND_HANDLE = 2;
    public static final byte COMMAND_CHARGER = 3;
    public static final byte COMMAND_SKILL = 4;
    public static final byte COMMAND_FLASH = 5;
    public static final byte COMMAND_DOUBLE = 6;
    public static final byte COMMAND_MAGNET = 7;
    public static final byte COMMAND_BUY_COIN = 51;
    
    private Image imgViewpot;
    private Graphics3D mGraphics3D;
    private World mWorld;
    private Camera mCamera;
    private Background mBackground;
    private Group mRacer;
    private Mesh mBike, mHuman;
    private byte racerColor, racerType;
    
    private int totalCoin;
    private void spendCoin(int price) {
        totalCoin -= price;
        profile.storeSetting(Profile.RECORD_COIN, Integer.toString(totalCoin));
    }
    
    private GarageResource resource;
    private Main main;
    private Image imgLoading, imgBgTop, imgBgCenter, imgBgBottom;
    private Button btnShowcase;
    
    private MessageQueue message;
    private final Achievement achievement = Achievement.getInstance();
    private Garage list;
    private final Profile profile = Profile.getInstance();
    private Dialog dialog;
    private Image imgSnapshot;

    public GarageScene() {
        super(Main.getInstance(), 15);
        
        totalCoin = Integer.parseInt(profile.getSetting(Profile.RECORD_COIN));
        
        beginLoading();
    }
    
    protected void prepareResource() {
        main = Main.getInstance();
        message = MessageQueue.createInstance();
        resource = GarageResource.createInstance();
        
        imgLoading = ImageHelper.loadImage("/images/loading.png");
//#if Nokia_240_320_Touch
//#         addCommand(new Command("Back", Command.BACK, 1));
//#         setCommandListener(this);
//#endif
    }
    
    private void prepare3DResource() {
//#if ScreenHeight == 320
        imgViewpot = Image.createImage(80, 75);
//#elif ScreenHeight == 400
//#         imgViewpot = Image.createImage(80, 104);
//#endif
        
        //create scene's world
        mGraphics3D = Graphics3D.getInstance();
        mWorld = new World();
        
        //create scene's light
        Light mLight = new Light();
        mLight.setColor(0xffffff);
        mLight.setMode(Light.AMBIENT);
        mLight.setIntensity(6);
        mWorld.addChild(mLight);
        
        mBackground = new Background();
        mBackground.setImageMode(Background.BORDER, Background.BORDER);
        mWorld.setBackground(mBackground);
        mBackground.setImage(resource.getImageViewpotBackground());
        //mBackground.setColor(0xff008ad2);
        
        //create scene's camera
        mCamera = new Camera();
        mCamera.setPerspective(
            45,
            (float)imgViewpot.getWidth() / (float)imgViewpot.getHeight(),
            1,
            600
        );
        mWorld.addChild(mCamera);
        mWorld.setActiveCamera(mCamera);
        mCamera.setOrientation(180, 0, 1, 1.f/3.f);
//#if ScreenHeight == 320
        switch(resource.getRacerType()) {
            case Racer.TYPE_THUNDER:
                mCamera.setTranslation(0, 22, -22);
                break;
                
            case Racer.TYPE_LIZARD:
                mCamera.setTranslation(0, 26, -24);
                break;
                
            case Racer.TYPE_SPIRIT:
                mCamera.setTranslation(0, 25, -25);
                break;
                
            case Racer.TYPE_TOMAHAWK:
                mCamera.setTranslation(0, 27, -27);
                break;
        }
//#elif ScreenHeight == 400
//#         switch(resource.getRacerType()) {
//#             case Racer.TYPE_THUNDER:
//#                 mCamera.setTranslation(0, 28, -28);
//#                 break;
//#                 
//#             case Racer.TYPE_LIZARD:
//#                 mCamera.setTranslation(0, 32, -30);
//#                 break;
//#                 
//#             case Racer.TYPE_SPIRIT:
//#                 mCamera.setTranslation(0, 31, -31);
//#                 break;
//#                 
//#             case Racer.TYPE_TOMAHAWK:
//#                 mCamera.setTranslation(0, 35, -35);
//#                 break;
//#         }
//#endif
        
        mRacer = resource.getModelRacer();
        mWorld.addChild(mRacer);
        mBike = (Mesh)mRacer.getChild(Racer.MESH_BIKE);
        mHuman = (Mesh)mRacer.getChild(Racer.MESH_HUMAN);
    }
    
    private void prepare2DResource() {
        imgBgTop = resource.getImageGarageTop();
        imgBgCenter = resource.getImageGarageCenter();
        imgBgBottom = resource.getImageGarageBottom();
//#if ScreenHeight == 320
        btnShowcase = new Button(resource.getImageButtonShowcase(), 35, 253, 50, 35);
//#elif ScreenHeight == 400
//#         btnShowcase = new Button(resource.getImageButtonShowcase(), 35, 314, 50, 50);
//#endif
    }
    
    private void beginLoading() {
        new Thread(resource).start();
    }
    
    private void completeLoading() {
        imgLoading = null;
        prepare3DResource();
        prepare2DResource();
        
        list = new Garage(resource.getImageItems());
        racerType = resource.getRacerType();
        racerColor = resource.getRacerColor();
        String bikeUpgradeData = "111";
        String bikeDurabilityData = "100";
        switch(racerType) {
            case Racer.TYPE_THUNDER:
                bikeDurabilityData = profile.getSetting(Profile.RECORD_BIKE_THUNDER_DURABILITY);
                bikeUpgradeData = profile.getSetting(Profile.RECORD_BIKE_THUNDER_UPGRADE);
                break;
                
            case Racer.TYPE_LIZARD:
                bikeDurabilityData = profile.getSetting(Profile.RECORD_BIKE_LIZARD_DURABILITY);
                bikeUpgradeData = profile.getSetting(Profile.RECORD_BIKE_LIZARD_UPGRADE);
                break;
                
            case Racer.TYPE_SPIRIT:
                bikeDurabilityData = profile.getSetting(Profile.RECORD_BIKE_SPIRIT_DURABILITY);
                bikeUpgradeData = profile.getSetting(Profile.RECORD_BIKE_SPIRIT_UPGRADE);
                break;
                
            case Racer.TYPE_TOMAHAWK:
                bikeDurabilityData = profile.getSetting(Profile.RECORD_BIKE_TOMAHAWK_DURABILITY);
                bikeUpgradeData = profile.getSetting(Profile.RECORD_BIKE_TOMAHAWK_UPGRADE);
                break;
        }
        list.setDurability(Integer.parseInt(bikeDurabilityData));
        list.setUpgradeLevel(Garage.ITEM_HANDLE, Integer.parseInt(bikeUpgradeData.substring(0, 1)));
        list.setUpgradeLevel(Garage.ITEM_CHARGER, Integer.parseInt(bikeUpgradeData.substring(1, 2)));
        list.setUpgradeLevel(Garage.ITEM_SKILL, Integer.parseInt(bikeUpgradeData.substring(2, 3)));
        list.setUpgradeLevel(Garage.ITEM_FLASH, Integer.parseInt(profile.getSetting(Profile.RECORD_POWERUP_FLASH_LEVEL)));
        list.setUpgradeLevel(Garage.ITEM_DOUBLE, Integer.parseInt(profile.getSetting(Profile.RECORD_POWERUP_DOUBLE_LEVEL)));
        list.setUpgradeLevel(Garage.ITEM_MAGNET, Integer.parseInt(profile.getSetting(Profile.RECORD_POWERUP_MAGNET_LEVEL)));
        list.setActivePowerUp(Byte.parseByte(profile.getSetting(Profile.RECORD_CURRENT_POWERUP)));
        
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
                mRacer.postRotate(2, 0, 1, 0);
                list.update();
                break;
        }
        message.update();
    }
    
    public void paint(Graphics g) {
        if(dialog != null) {
            if(imgSnapshot != null) g.drawImage(imgSnapshot, 0, 0, Graphics.TOP | Graphics.LEFT);
            dialog.paint(g);
            return;
        }
        
        switch(state) {
            case STATE_LOAD:
                g.setColor(0x000000);
                g.fillRect(0, 0, Main.SCREENSIZE_WIDTH, Main.SCREENSIZE_HEIGHT);
                g.setColor(0xffffff);
                g.drawRect(47, Main.SCREENSIZE_HEIGHT/2-5, 146, 10);
                g.fillRect(50, Main.SCREENSIZE_HEIGHT/2-2, resource.getLoadingBarWidth(), 4);
                g.drawImage(imgLoading, 50+resource.getLoadingBarWidth(), Main.SCREENSIZE_HEIGHT/2-12, Graphics.HCENTER | Graphics.BOTTOM);
                break;
                
            case STATE_MAIN:
                Graphics viewpotGraphic = imgViewpot.getGraphics();
                mGraphics3D.bindTarget(viewpotGraphic);
                mGraphics3D.render(mWorld);
                mGraphics3D.releaseTarget();

                g.drawImage(imgBgCenter, 0, Garage.AREA_TOP, Graphics.LEFT | Graphics.TOP);
                list.paint(g);
                g.drawImage(imgBgTop, 0, 0, Graphics.LEFT | Graphics.TOP);
                g.drawImage(imgBgBottom, 0, Garage.AREA_BOTTOM, Graphics.LEFT | Graphics.TOP);
                g.drawImage(imgViewpot, Main.SCREENSIZE_WIDTH-imgViewpot.getWidth(), Garage.AREA_BOTTOM, Graphics.LEFT | Graphics.TOP);
                g.setColor(0xffffff);
//#if ScreenHeight == 320
                g.drawString(Integer.toString(totalCoin), 116, 283, Graphics.LEFT | Graphics.BASELINE);
//#elif ScreenHeight == 400
//#                 g.drawString(Integer.toString(totalCoin), 116, 354, Graphics.LEFT | Graphics.BASELINE);
//#endif
                btnShowcase.paint(g);
                break;
        }
        message.paint(g);
    }
    
    private void takeSnapshot() {
        if(state != STATE_MAIN) return;
        
        imgSnapshot = Image.createImage(Main.SCREENSIZE_WIDTH, Main.SCREENSIZE_HEIGHT);
        Graphics g = imgSnapshot.getGraphics();
        paint(g);
        g.drawImage(resource.getImageDarkScreen(), 0, 0, Graphics.LEFT | Graphics.TOP);
    }
    
    protected void pointerPressed(int x, int y) {
        if(dialog != null) return;
        
        switch(state) {
            case STATE_MAIN:
                if(x >= Garage.AREA_LEFT && x <= Garage.AREA_RIGHT && y >= Garage.AREA_TOP && y <= Garage.AREA_BOTTOM) {
                    list.pointerPressed(x, y);
                    return;
                }
                btnShowcase.testHit(x, y);
                break;
        }
    }
    
    protected void pointerDragged(int x, int y) {
        if(dialog != null) return;
        
        switch(state) {
            case STATE_MAIN:
                if(list.pointerDragged(x, y)) return;
                btnShowcase.testHit(x, y);
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
                switch(list.pointerReleased(x, y)) {
                    case Garage.ITEM_DURABILITY:
                        requestRepair();
                        break;
                        
                    case Garage.ITEM_COLOR:
                        changeColor();
                        break;
                        
                    case Garage.ITEM_HANDLE:
                        upgradeHandle();
                        break;
                        
                    case Garage.ITEM_CHARGER:
                        upgradeCharger();
                        break;
                        
                    case Garage.ITEM_SKILL:
                        upgradeSkill();
                        break;
                        
                    case Garage.ITEM_FLASH:
                        if(x < 80) setActivePowerUp(Power.TYPE_FLASH);
                        else upgradeFlash();
                        break;
                        
                    case Garage.ITEM_DOUBLE:
                        if(x < 80) setActivePowerUp(Power.TYPE_DOUBLE);
                        else upgradeDouble();
                        break;
                        
                    case Garage.ITEM_MAGNET:
                        if(x < 80) setActivePowerUp(Power.TYPE_MAGNET);
                        else upgradeMagnet();
                        break;
                        
                    case Garage.ITEM_NONE:
                        if(btnShowcase.gotClick(x, y)) {
                            main.gotoShowcase();
                        }
                        break;
                }
                break;
        }
    }
    
//#if TKEY || QWERTY
    protected void keyPressed(int keyCode) {
        if (dialog != null) return;
        
        switch(state) {
            case STATE_MAIN:
                switch(keyCode) {
                    case KeyMap.KEY_LF:
                        btnShowcase.active = true;
                        break;
                        
                    default:
                        list.keyPressed(keyCode);
                        break;
                }
                break;
        }
    }
    
    protected void keyReleased(int keyCode) {
        if (dialog != null) {
            dialog.keyReleased(keyCode);
            return;
        }
        
        switch (state) {
            case STATE_MAIN:
                switch (list.keyReleased(keyCode)) {
                    case Garage.ITEM_DURABILITY:
                        requestRepair();
                        break;
                        
                    case Garage.ITEM_COLOR:
                        changeColor();
                        break;
                        
                    case Garage.ITEM_HANDLE:
                        upgradeHandle();
                        break;
                        
                    case Garage.ITEM_CHARGER:
                        upgradeCharger();
                        break;
                        
                    case Garage.ITEM_SKILL:
                        upgradeSkill();
                        break;
                        
                    case Garage.ITEM_FLASH:
                        if (keyCode == KeyMap.KEY_0)
                            setActivePowerUp(Power.TYPE_FLASH);
                        else
                            upgradeFlash();
                        break;
                        
                    case Garage.ITEM_DOUBLE:
                        if (keyCode == KeyMap.KEY_0)
                            setActivePowerUp(Power.TYPE_DOUBLE);
                        else
                            upgradeDouble();
                        break;
                        
                    case Garage.ITEM_MAGNET:
                        if (keyCode == KeyMap.KEY_0)
                            setActivePowerUp(Power.TYPE_MAGNET);
                        else
                            upgradeMagnet();
                        break;
                        
                    case Garage.ITEM_NONE:
                        switch (keyCode) {
                            case KeyMap.KEY_LF:
                                if (btnShowcase.gotClick())
                                    main.gotoShowcase();
                                break;
                                
                            case KeyMap.KEY_RF:
                                main.gotoShowcase();
                                break;
                        }
                        break;
                }
                break;
        }
    }
//#endif
    
    private void requestRepair() {
        takeSnapshot();
        if(totalCoin < list.getRepairCost()) {
            buyMoreCoins();
        } else if(list.getDurability() < 100) {
            dialog = new ConfirmDialog(
                "REPAIR MOTO",
                "Bring your bike to full|"+
                "durability.",
                COMMAND_REPAIR, this
            );
        }
    }
    
    private void changeColor() {
        if(++racerColor > 3) racerColor = 0;
        ModelHelper.applyTexture(resource.getTextureBike(racerColor), mBike);
        ModelHelper.applyTexture(resource.getTextureHuman(racerColor), mHuman);
        switch(racerType) {
            case Racer.TYPE_THUNDER:
                profile.storeSetting(Profile.RECORD_BIKE_THUNDER_COLOR, Integer.toString(racerColor));
                break;

            case Racer.TYPE_LIZARD:
                profile.storeSetting(Profile.RECORD_BIKE_LIZARD_COLOR, Integer.toString(racerColor));
                break;

            case Racer.TYPE_SPIRIT:
                profile.storeSetting(Profile.RECORD_BIKE_SPIRIT_COLOR, Integer.toString(racerColor));
                break;

            case Racer.TYPE_TOMAHAWK:
                profile.storeSetting(Profile.RECORD_BIKE_TOMAHAWK_COLOR, Integer.toString(racerColor));
                break;
        }
    }
    
    private void upgradeHandle() {
        takeSnapshot();
        if(totalCoin < list.getUpgradeCost(Garage.ITEM_HANDLE)) {
            buyMoreCoins();
        } else if(list.getUpgradeLevel(Garage.ITEM_HANDLE) < 5) {
            dialog = new ConfirmDialog(
                "UPGRADE HANDLE",
                "This gives moto's handle|"+
                "more sensitively.",
                COMMAND_HANDLE, this
            );
        }
    }
    
    private void upgradeCharger() {
        takeSnapshot();
        if(totalCoin < list.getUpgradeCost(Garage.ITEM_CHARGER)) {
            buyMoreCoins();
        } else if(list.getUpgradeLevel(Garage.ITEM_CHARGER) < 5) {
            dialog = new ConfirmDialog(
                "UPGRADE CHARGER",
                "Energy for using skills|"+
                "will be regenerated more|"+
                "quickly.",
                COMMAND_CHARGER, this
            );
        }
    }
    
    private void upgradeSkill() {
        takeSnapshot();
        if(totalCoin < list.getUpgradeCost(Garage.ITEM_SKILL)) {
            buyMoreCoins();
        } else if(list.getUpgradeLevel(Garage.ITEM_SKILL) < 5) {
            dialog = new ConfirmDialog(
                "UPGRADE SKILL",
                "This makes moto's skill|"+
                "more powerful.",
                COMMAND_SKILL, this
            );
        }
    }
    
    private void upgradeFlash() {
        takeSnapshot();
        if(totalCoin < list.getUpgradeCost(Garage.ITEM_FLASH)) {
            buyMoreCoins();
        } else if(list.getUpgradeLevel(Garage.ITEM_FLASH) < 5) {
            dialog = new ConfirmDialog(
                "UPGRADE FLASH",
                "Increases duration of|"+
                "Flash power-ups.",
                COMMAND_FLASH, this
            );
        }
    }
    
    private void upgradeDouble() {
        takeSnapshot();
        if(totalCoin < list.getUpgradeCost(Garage.ITEM_DOUBLE)) {
            buyMoreCoins();
        } else if(list.getUpgradeLevel(Garage.ITEM_DOUBLE) < 5) {
            dialog = new ConfirmDialog(
                "UPGRADE DOUBLE",
                "Increases duration of|"+
                "Double power-ups.",
                COMMAND_DOUBLE, this
            );
        }
    }
    
    private void upgradeMagnet() {
        takeSnapshot();
        if(totalCoin < list.getUpgradeCost(Garage.ITEM_MAGNET)) {
            buyMoreCoins();
        } else if(list.getUpgradeLevel(Garage.ITEM_MAGNET) < 5) {
            dialog = new ConfirmDialog(
                "UPGRADE MAGNET",
                "Increases duration of|"+
                "Magnet power-ups.",
                COMMAND_MAGNET, this
            );
        }
    }
    
    private void setActivePowerUp(byte powerUpType) {
        profile.storeSetting(Profile.RECORD_CURRENT_POWERUP, Integer.toString(powerUpType));
        list.setActivePowerUp(powerUpType);
    }
    
    private void buyMoreCoins() {
        dialog = new ConfirmDialog(
            "NOT ENOUGH COINS",
            "You have not enough coins|"+
            "to request this upgrade.|"+
            "Do you wish to buy some|"+
            "more?",
            COMMAND_BUY_COIN, this
        );
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
//#             case Main.PRODUCT_COIN:
//#                 spendCoin(-10000);
//#                 break;
//#         }
//#     }
//# 
//#     public void paymentFail() {
//#     }
//#endif
    
    public void dispose() {
        super.dispose();
        resource.dispose();
    }

    public void runDialogCommand(byte command) {
        dialog = null;
        imgSnapshot = null;
        switch (command) {
            case COMMAND_REPAIR:
                spendCoin(list.getRepairCost());
                switch(racerType) {
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
                list.setDurability(100);
                break;
                
            case COMMAND_HANDLE:
            case COMMAND_CHARGER:
            case COMMAND_SKILL:
                if (command == COMMAND_HANDLE) {
                    spendCoin(list.getUpgradeCost(Garage.ITEM_HANDLE));
                    list.upgradeItem(Garage.ITEM_HANDLE);
                } else if (command == COMMAND_CHARGER) {
                    spendCoin(list.getUpgradeCost(Garage.ITEM_CHARGER));
                    list.upgradeItem(Garage.ITEM_CHARGER);
                } else {
                    spendCoin(list.getUpgradeCost(Garage.ITEM_SKILL));
                    list.upgradeItem(Garage.ITEM_SKILL);
                }
                String upgradeData = list.getUpgradeData();
                switch (racerType) {
                    case Racer.TYPE_THUNDER:
                        profile.storeSetting(Profile.RECORD_BIKE_THUNDER_UPGRADE, upgradeData);
                        if (list.getUpgradeData().equals("555"))
                            achievement.triggerUltimateThunder();
                        break;
                        
                    case Racer.TYPE_LIZARD:
                        profile.storeSetting(Profile.RECORD_BIKE_LIZARD_UPGRADE, upgradeData);
                        if (list.getUpgradeData().equals("555"))
                            achievement.triggerUltimateLizard();
                        break;
                        
                    case Racer.TYPE_SPIRIT:
                        profile.storeSetting(Profile.RECORD_BIKE_SPIRIT_UPGRADE, upgradeData);
                        if (list.getUpgradeData().equals("555"))
                            achievement.triggerUltimateSpirit();
                        break;
                        
                    case Racer.TYPE_TOMAHAWK:
                        profile.storeSetting(Profile.RECORD_BIKE_TOMAHAWK_UPGRADE, upgradeData);
                        if (list.getUpgradeData().equals("555"))
                            achievement.triggerUltimateTomahawk();
                        break;
                }
                break;
                
            case COMMAND_FLASH:
                spendCoin(list.getUpgradeCost(Garage.ITEM_FLASH));
                list.setUpgradeLevel(Garage.ITEM_FLASH, list.getUpgradeLevel(Garage.ITEM_FLASH) + 1);
                profile.storeSetting(Profile.RECORD_POWERUP_FLASH_LEVEL, Integer.toString(list.getUpgradeLevel(Garage.ITEM_FLASH)));
                checkFullPowerUp();
                break;
                
            case COMMAND_DOUBLE:
                spendCoin(list.getUpgradeCost(Garage.ITEM_DOUBLE));
                list.setUpgradeLevel(Garage.ITEM_DOUBLE, list.getUpgradeLevel(Garage.ITEM_DOUBLE) + 1);
                profile.storeSetting(Profile.RECORD_POWERUP_DOUBLE_LEVEL, Integer.toString(list.getUpgradeLevel(Garage.ITEM_DOUBLE)));
                checkFullPowerUp();
                break;
                
            case COMMAND_MAGNET:
                spendCoin(list.getUpgradeCost(Garage.ITEM_MAGNET));
                list.setUpgradeLevel(Garage.ITEM_MAGNET, list.getUpgradeLevel(Garage.ITEM_MAGNET) + 1);
                profile.storeSetting(Profile.RECORD_POWERUP_MAGNET_LEVEL, Integer.toString(list.getUpgradeLevel(Garage.ITEM_MAGNET)));
                checkFullPowerUp();
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
//#             case COMMAND_BUY_COIN:
//#                 if (main.getPaymentManager().isNPayAvailable())
//#                     main.startPurchase(Main.PRODUCT_COIN, this);
//#                 else
//#                     notifyInstallPayment();
//#                 break;
//#else
                case COMMAND_BUY_COIN:
                notifyInstallPayment();
                break;
//#endif
        }
    }
    
    private void checkFullPowerUp() {
        if (list.getUpgradeLevel(Garage.ITEM_FLASH) == 5 &&
                list.getUpgradeLevel(Garage.ITEM_DOUBLE) == 5 &&
                list.getUpgradeLevel(Garage.ITEM_MAGNET) == 5) {
            achievement.triggerPowerfulRacer();
        }
    }

//#if Nokia_240_320_Touch
//#     public void commandAction(Command c, Displayable d) {
//#         switch (c.getCommandType()) {
//#             case Command.BACK:
//#                 if (state != STATE_LOAD) main.gotoShowcase();
//#                 break;
//#         }
//#     }
//#endif
}
