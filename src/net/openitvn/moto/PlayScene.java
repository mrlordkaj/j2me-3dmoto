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
import net.openitvn.game.ILeaderboardCaller;
import net.openitvn.game.ImageHelper;
import net.openitvn.game.Leaderboard;
import net.openitvn.game.bounding.Rectangle;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.game.Sprite;
import javax.microedition.m3g.Background;
import javax.microedition.m3g.Graphics3D;
import javax.microedition.m3g.Light;
import javax.microedition.m3g.World;
import javax.microedition.sensor.Data;
import javax.microedition.sensor.DataListener;
import javax.microedition.sensor.SensorConnection;

/**
 *
 * @author Thinh Pham
 */
public class PlayScene extends GameScene implements IDialogHolder, ILeaderboardCaller,
        DataListener, CommandListener {
    public static final byte STATE_READY = 1;
    public static final byte STATE_PLAY = 2;
    public static final byte STATE_PAUSE = 3;
    public static final byte STATE_RESUME = 4;
    public static final byte STATE_SUMMARY = 6;
    
    private static final byte COMMAND_MENU = 1;
    private static final byte COMMAND_RESTART = 2;
    private static final byte COMMAND_REPAIR = 3;
    private static final byte COMMAND_FIX_AND_CONTINUE = 4;
    
    private static final short CONTROLLER_CENTER = 172;
    
    private Button btnMenu, btnResume, btnRestart, btnPause, btnSubmit, btnRepair;
    private Image imgCoin;
    private Sprite sprReady;
    
    private MessageQueue message;
    private int totalCoin;
    private int coin, coinTick;
    public boolean chestCollected;
    private void payCoin(int price) {
        totalCoin -= price;
        profile.storeSetting(Profile.RECORD_COIN, Integer.toString(totalCoin));
    }
    
    private boolean newRecord;
    private int record;
    private String playerName;
    private TextBox txtPlayerName;
    
    private int timeline;
    
    private Graphics3D mGraphics3D;
    private World mWorld;
    public World getWorld() { return mWorld; }
    private CustomCamera mCamera;
    private Background mBackground;
    
    private Traffic traffic;
    public Traffic getTraffic() { return traffic; }
    
    private final Profile profile = Profile.getInstance();
    private final Main main = Main.getInstance();
    private PlayResource resource;
    private Road road;
    public Road getRoad() { return road; }
    private Racer racer;
    public Racer getRacer() { return racer; }
    
    private final float kph2UpfFactor;
    public float getKph2UpfFactor() { return kph2UpfFactor; }
    
    private Dialog dialog;
    private Image imgBackground, imgLoading;
    
    private int durabilityBarWidth, durability;
    
    private boolean showTouchString;
    private final Rectangle btnAd1 = new Rectangle(0, Main.SCREENSIZE_HEIGHT-60, Main.SCREENSIZE_WIDTH, 60);
    private final Rectangle btnAd2 = new Rectangle(0, 0, Main.SCREENSIZE_WIDTH, 60);
    
    private boolean isTouchOnly;
    private int controllerX;
    private boolean returnControl;
    private Image imgHandle;
    
    public PlayScene() {
        super(Main.getInstance(), 13);
        
        kph2UpfFactor = (100.f/36.f)/(float)getFPS();
        
        //saved data
        totalCoin = Integer.parseInt(profile.getSetting(Profile.RECORD_COIN));
        record = Integer.parseInt(profile.getSetting(Profile.RECORD_HIGHSCORE));
        playerName = profile.getSetting(Profile.RECORD_PLAYER_NAME);
        
        beginLoading();
    }
    
    public static PlayScene getInstance() {
        if(instance instanceof PlayScene) {
            return (PlayScene)instance;
        }
        return null;
    }
    
    private void beginLoading() {
        new Thread(resource).start();
    }
    
    private void completeLoading() {
        imgLoading = null;
        prepare3DResource();
        prepare2DResource();
        
        beginGame();
    }

    protected void prepareResource() {
        message = MessageQueue.createInstance();
        resource = PlayResource.createInstance();
        isTouchOnly = !Main.isOpenAccelerometer();
        
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
        mWorld.setBackground(mBackground);
        
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
    }
    
    private void prepare2DResource() {
        imgCoin = resource.getImageCoin();
        sprReady = new Sprite(resource.getImageReady(), 87, 43);
        imgHandle = resource.getImageHandle();
//#if ScreenHeight == 320
//#         sprReady.setPosition(77, 110);
//#else
        sprReady.setPosition(77, 150);
//#endif
        btnPause = new Button(resource.getImageButtonPause(), Main.SCREENSIZE_WIDTH - 10 - 46, 10, 46, 30);
    }
    
    protected void update() {
        if(state != STATE_PLAY) AdManager.autoUpdate(getFPS());
        
        switch(state) {
            case STATE_LOAD:
                if(resource.loadingComplete()) {
                    if(Main.isShowAd()) {
                        if(timeline > 0) {
                            timeline--;
                        } else {
                            timeline = getFPS()/2;
                            showTouchString = !showTouchString;
                        }
                    } else completeLoading();
                }
                break;
                
            case STATE_READY:
                road.update();
                racer.update();
                //update count-down
                timeline++;
                if(timeline == getFPS()*2) sprReady.setFrame(1);
                else if(timeline == getFPS()*3) sprReady.setFrame(2);
                else if(timeline == getFPS()*4) sprReady.setFrame(3);
                else if(timeline == getFPS()*5) beginGo();
                break;
                
            case STATE_PLAY:
                if(isTouchOnly && returnControl) {
                    touchDrive(CONTROLLER_CENTER);
                    returnControl = false;
                }
                if(racer.getState() != Racer.STATE_DIE) mCamera.update();
                road.update();
                road.updateDistance();
                //check new record
                if(!newRecord && road.getDistance() > record) {
                    newRecord = true;
                    MessageQueue.addMessage(Message.TYPE_RECORD);
                }
                traffic.update();
                racer.update();
                break;
                
            case STATE_PAUSE:
                if(btnMenu.x < 10) {
                    btnMenu.x += 20;
                    btnRestart.x -= 20;
                }
                break;
                
            case STATE_RESUME:
                timeline++;
                if(timeline == getFPS()*1) sprReady.setFrame(1);
                else if(timeline == getFPS()*2) sprReady.setFrame(2);
                else if(timeline == getFPS()*3) state = STATE_PLAY;
                break;
        }
        if(dialog != null) dialog.update();
        message.update();
    }
    
    public void paint(Graphics g) {
        g.setFont(Main.FontBold);
        
        switch(state) {
            case STATE_LOAD:
                g.setColor(0x000000);
                g.fillRect(0, 0, Main.SCREENSIZE_WIDTH, Main.SCREENSIZE_HEIGHT);
                g.setColor(0xffffff);
                if(resource.loadingComplete()) {
                    if(showTouchString) {
                        g.drawString("touch to continue", Main.SCREENSIZE_WIDTH/2, Main.SCREENSIZE_HEIGHT/2, Graphics.HCENTER | Graphics.BASELINE);
                    }
                    if(AdManager.getImageAd() != null) {
                        g.drawImage(AdManager.getImageAd(), Main.SCREENSIZE_WIDTH/2, Main.SCREENSIZE_HEIGHT-30, Graphics.HCENTER | Graphics.VCENTER);
                    }
                } else {
                    g.drawRect(47, Main.SCREENSIZE_HEIGHT/2-5, 146, 10);
                    g.fillRect(50, Main.SCREENSIZE_HEIGHT/2-2, resource.getLoadingBarWidth(), 4);
                    g.drawImage(imgLoading, 50+resource.getLoadingBarWidth(), Main.SCREENSIZE_HEIGHT/2-12, Graphics.HCENTER | Graphics.BOTTOM);
                }
                break;
                
            case STATE_READY:
                viewpotRender(g);
                if(timeline >= getFPS()) sprReady.paint(g);
                break;
                
            case STATE_PLAY:
                //draw game viewpot
                viewpotRender(g);
                //draw collected coins
                if(coinTick > 0) {
                    coinTick--;
                    String strCoin = coin + " x";
                    g.drawImage(imgCoin, Main.SCREENSIZE_WIDTH - 10, 50, Graphics.TOP | Graphics.RIGHT);
                    g.setColor(0x000000);
                    g.drawString(strCoin, Main.SCREENSIZE_WIDTH - 30, 50, Graphics.TOP | Graphics.RIGHT);
                    g.setColor(0xff6e41);
                    g.drawString(strCoin, Main.SCREENSIZE_WIDTH - 31, 49, Graphics.TOP | Graphics.RIGHT);
                }
                break;
                
            case STATE_PAUSE:
                g.drawImage(imgBackground, 0, 0, Graphics.LEFT | Graphics.TOP);
                btnMenu.paint(g);
                btnResume.paint(g);
                btnRestart.paint(g);
                if(Main.isShowAd() && AdManager.getImageAd() != null) {
                    g.drawImage(AdManager.getImageAd(), Main.SCREENSIZE_WIDTH/2, Main.SCREENSIZE_HEIGHT-30, Graphics.HCENTER | Graphics.VCENTER);
                }
                break;
                
            case STATE_RESUME:
                viewpotRender(g);
                sprReady.paint(g);
                break;
                
            case STATE_SUMMARY:
                g.drawImage(imgBackground, 0, 0, Graphics.LEFT | Graphics.TOP);
                btnSubmit.paint(g);
                btnMenu.paint(g);
                btnRepair.paint(g);
                btnRestart.paint(g);
//#if ScreenHeight == 320
//#                 g.setColor(0xff0000);
//#                 g.drawString(road.getDistance() + " m", 205, 87, Graphics.RIGHT | Graphics.BASELINE);
//#                 g.setColor(0x00ff00);
//#                 g.drawString(record + " m", 205, 112, Graphics.RIGHT | Graphics.BASELINE);
//#                 g.setColor(0xffff00);
//#                 g.drawString("+ "+coin, 205, 139, Graphics.RIGHT | Graphics.BASELINE);
//#                 g.drawString(Integer.toString(totalCoin), 205, 164, Graphics.RIGHT | Graphics.BASELINE);
//#                 g.setColor(0xffffff);
//#                 g.drawString(playerName, 42, 228, Graphics.LEFT | Graphics.BASELINE);
//#                 if(durability < 20) g.setColor(0xff0000);
//#                 else if(durability < 50) g.setColor(0xffff00);
//#                 else g.setColor(0x00ff00);
//#                 g.fillRect(125, 180, durabilityBarWidth, 11);
//#elif ScreenHeight == 400
                g.setColor(0xff0000);
                g.drawString(road.getDistance() + " m", 205, 95, Graphics.RIGHT | Graphics.BASELINE);
                g.setColor(0x00ff00);
                g.drawString(record + " m", 205, 132, Graphics.RIGHT | Graphics.BASELINE);
                g.setColor(0xffff00);
                g.drawString("+ "+coin, 205, 167, Graphics.RIGHT | Graphics.BASELINE);
                g.drawString(Integer.toString(totalCoin), 205, 203, Graphics.RIGHT | Graphics.BASELINE);
                g.setColor(0xffffff);
                g.drawString(playerName, 42, 288, Graphics.LEFT | Graphics.BASELINE);
                if(durability < 20) g.setColor(0xff0000);
                else if(durability < 50) g.setColor(0xffff00);
                else g.setColor(0x00ff00);
                g.fillRect(125, 228, durabilityBarWidth, 11);
//#endif
                
                if(Main.isShowAd() && AdManager.getImageAd() != null) {
                    g.drawImage(AdManager.getImageAd(), Main.SCREENSIZE_WIDTH/2, 30, Graphics.HCENTER | Graphics.VCENTER);
                }
                break;
        }
        if(dialog != null) dialog.paint(g);
        message.paint(g);
    }
    
    private void viewpotRender(Graphics g) {
        //draw 3d
        mGraphics3D.bindTarget(g);
        mGraphics3D.render(mWorld);
        mGraphics3D.releaseTarget();
        //draw controller
        racer.paint(g);
        //draw distance
        g.setColor(0xffffff);
        g.drawString(Integer.toString(road.getDistance()), 195, Main.SCREENSIZE_HEIGHT-62, Graphics.BASELINE | Graphics.HCENTER);
        //draw handle
        if(isTouchOnly) g.drawImage(imgHandle, controllerX, Main.SCREENSIZE_HEIGHT-18, Graphics.HCENTER | Graphics.BOTTOM);
        //draw pause button
        btnPause.paint(g);
    }
    
    private void takeSnapshot(byte forState) {
        imgBackground = Image.createImage(Main.SCREENSIZE_WIDTH, Main.SCREENSIZE_HEIGHT);
        Graphics g = imgBackground.getGraphics();
        switch(forState) {
            case STATE_PAUSE:
                viewpotRender(g);
                g.drawImage(ImageHelper.loadImage("/images/darkScreen.png"), 0, 0, Graphics.TOP | Graphics.LEFT);
                g.drawImage(ImageHelper.loadImage("/images/paused.png"), Main.SCREENSIZE_WIDTH/2, 34, Graphics.TOP | Graphics.HCENTER);
                break;
        }
    }
    
    protected void pointerPressed(int x, int y) {
        if(dialog != null) return;
        
        switch(state) {
            case STATE_LOAD:
                if(resource.loadingComplete() && Main.isShowAd() && btnAd1.contains(x, y)) {
                    AdManager.visitAd();
                }
                break;
                
            case STATE_PLAY:
                btnPause.testHit(x, y);
                
                if(isTouchOnly && y > Main.SCREENSIZE_HEIGHT-100) touchDrive((short)x);
                break;
                
            case STATE_PAUSE:
                if(Main.isShowAd() && btnAd1.contains(x, y)) AdManager.visitAd();
                btnMenu.testHit(x, y);
                btnResume.testHit(x, y);
                btnRestart.testHit(x, y);
                break;
                
            case STATE_SUMMARY:
                if(Main.isShowAd() && btnAd2.contains(x, y)) AdManager.visitAd();
                btnSubmit.testHit(x, y);
                btnMenu.testHit(x, y);
                btnRepair.testHit(x, y);
                btnRestart.testHit(x, y);
                break;
        }
    }
    
    protected void pointerDragged(int x, int y) {
        if(dialog != null) return;
        
        switch(state) {
            case STATE_PLAY:
                btnPause.testHit(x, y);
                
                if(isTouchOnly) {
                    if(y > Main.SCREENSIZE_HEIGHT-100) touchDrive((short)x);
                    else returnControl = true;
                }
                break;
                
            case STATE_PAUSE:
                btnMenu.testHit(x, y);
                btnResume.testHit(x, y);
                btnRestart.testHit(x, y);
                break;
                
            case STATE_SUMMARY:
                btnSubmit.testHit(x, y);
                btnMenu.testHit(x, y);
                btnRepair.testHit(x, y);
                btnRestart.testHit(x, y);
                break;
        }
    }
    
    protected void pointerReleased(int x, int y) {
        if(dialog != null) {
            dialog.pointerReleased(x, y);
            return;
        }
        
        switch(state) {
            case STATE_LOAD:
                if(resource.loadingComplete() && !btnAd1.contains(x, y)) completeLoading();
                break;
                
            case STATE_PLAY:
                if(btnPause.gotClick(x, y)) hideNotify();
                
                returnControl = true;
                break;
                
            case STATE_PAUSE:
                if(btnResume.gotClick(x, y)) resume();
                else if(btnMenu.gotClick(x, y)) requestGotoMenu();
                else if(btnRestart.gotClick(x, y)) requestRestart();
                break;
                
            case STATE_SUMMARY:
                if(btnSubmit.gotClick(x, y)) requestChangeName();
                else if(btnMenu.gotClick(x, y)) main.gotoSplash();
                else if(btnRepair.gotClick(x, y)) requestRepair();
                else if(btnRestart.gotClick(x, y)) requestPlayAgain();
                break;
        }
    }
    
    private void requestGotoMenu() {
        dialog = new ConfirmDialog(
            "MAIN MENU",
            "This will terminate your|"+
            "current game. Are you|"+
            "sure you want do that?",
            COMMAND_MENU, this
        );
    }
    
    private void requestRestart() {
        dialog = new ConfirmDialog(
            "RESTART GAME",
            "This will terminate your|"+
            "current game. Are you|"+
            "sure you want do that?",
            COMMAND_RESTART, this
        );
    }
    
    private void requestPlayAgain() {
        if(durability <= 1) {
            dialog = new ConfirmDialog(
                "BIKE DAMAGED",
                "Your bike got heavy damage,|"+
                "it must be repaired first.|" +
                "Do you want spend " + Racer.calcRepairCost(durability) + " coins|" +
                "to repair it now?",
                COMMAND_FIX_AND_CONTINUE, this
            );
        } else {
            beginGame();
        }
    }
    
    private void requestRepair() {
        if(durability < 100) {
            dialog = new ConfirmDialog(
                "REPAIR MOTO",
                "Do you want spend " + Racer.calcRepairCost(durability) + " coins|" +
                "to repair your moto now?",
                COMMAND_REPAIR, this
            );
        }
    }
    
    private void requestChangeName() {
        txtPlayerName = new TextBox("Enter your name:", playerName, 14, 0);
        txtPlayerName.addCommand(new Command("OK", Command.OK, 1));
        txtPlayerName.setCommandListener(this);
        Display.getDisplay(main).setCurrent(txtPlayerName);
    }
    
    private void beginGame() {
        removeMenuResource();
        
        while(mWorld.getChildCount() > 2) {
            mWorld.removeChild(mWorld.getChild(2));
        }
        newRecord = (record < 1);
        chestCollected = false;
        road = new Road();
        traffic = new Traffic();
        switch(resource.getRacerType()) {
            case Racer.TYPE_RED:
                racer = new RacerRed();
                break;
                
            case Racer.TYPE_GREEN:
                racer = new RacerGreen();
                break;
                
            case Racer.TYPE_PINK:
                racer = new RacerPink();
                break;
                
            default:
                racer = new RacerBlue();
                break;
        }
        racer.initialize();
        racer.beginReady();
        mCamera.reset();
        traffic.levelUp();
        coin = coinTick = 0;
        sprReady.setFrame(0);
        timeline = 0;
        controllerX = CONTROLLER_CENTER;
        returnControl = false;
        state = STATE_READY;
    }
    
    private void beginGo() {
        racer.beginGo();
        state = STATE_PLAY;
    }
    
    public void beginSummary() {
        totalCoin += coin;
        profile.storeSetting(Profile.RECORD_COIN, Integer.toString(totalCoin));
        
        durability = racer.getDurability();
        durabilityBarWidth = (int)(80.f * ((float)durability / 100.f));
        imgBackground = ImageHelper.loadImage("/images/summary.png");
//#if ScreenHeight == 320
//#         btnSubmit = new Button(ImageHelper.loadImage("/images/btnSummarySubmit.png"), 155, 206, 50, 30);
//#         btnMenu = new Button(ImageHelper.loadImage("/images/btnSummaryMenu.png"), 35, 253, 50, 35);
//#         btnRestart = new Button(ImageHelper.loadImage("/images/btnSummaryRestart.png"), 95, 253, 50, 35);
//#         btnRepair = new Button(ImageHelper.loadImage("/images/btnSummaryGarage.png"), 155, 253, 50, 35);
//#elif ScreenHeight == 400
        btnSubmit = new Button(ImageHelper.loadImage("/images/btnSummarySubmit.png"), 155, 266, 50, 30);
        btnMenu = new Button(ImageHelper.loadImage("/images/btnSummaryMenu.png"), 35, 314, 50, 50);
        btnRestart = new Button(ImageHelper.loadImage("/images/btnSummaryRestart.png"), 95, 314, 50, 50);
        btnRepair = new Button(ImageHelper.loadImage("/images/btnSummaryGarage.png"), 155, 314, 50, 50);
//#endif
        state = STATE_SUMMARY;
        
        if(newRecord) {
            record = road.getDistance();
            profile.storeSetting(Profile.RECORD_HIGHSCORE, Integer.toString(record));
            submitScore();
        }
    }
    
    private void submitScore() {
        Leaderboard.submitScore(record, playerName, Main.getDeviceId(), this);
        dialog = new IndicatorDialog("Submitting score", 160, 80, this);
    }
    
    public void addCoin() {
        coin++;
        coinTick = 40;
    }
    
    protected void hideNotify() {
        if(state != STATE_PLAY) return;
        
//#if ScreenHeight == 320
//#         btnMenu = new Button(ImageHelper.loadImage("/images/btnSummaryMenu.png"), -70, 175, 50, 35);
//#         btnResume = new Button(ImageHelper.loadImage("/images/btnMainPlay.png"), 80, 150, 80, 60);
//#         btnRestart = new Button(ImageHelper.loadImage("/images/btnSummaryRestart.png"), 260, 175, 50, 35);
//#elif ScreenHeight == 400
        btnMenu = new Button(ImageHelper.loadImage("/images/btnSummaryMenu.png"), -70, 175, 50, 50);
        btnResume = new Button(ImageHelper.loadImage("/images/btnMainPlay.png"), 80, 165, 80, 60);
        btnRestart = new Button(ImageHelper.loadImage("/images/btnSummaryRestart.png"), 260, 175, 50, 50);
//#endif
        takeSnapshot(STATE_PAUSE);
        state = STATE_PAUSE;
    }
    
    private void resume() {
        removeMenuResource();
        sprReady.setFrame(0);
        timeline = 0;
        state = STATE_RESUME;
    }
    
    private void removeMenuResource() {
        imgBackground = null;
        btnMenu = null;
        btnResume = null;
        btnRestart = null;
        btnRepair = null;
        btnSubmit = null;
    }
    
    public void dispose() {
        super.dispose();
        resource.dispose();
    }

    public void runDialogCommand(byte command) {
        dialog = null;
        switch(command) {
            case COMMAND_MENU:
                main.gotoSplash();
                break;
                
            case COMMAND_RESTART:
                beginGame();
                break;
                
            case COMMAND_REPAIR:
                payCoin(Racer.calcRepairCost(racer.getDurability()));
                racer.fix();
                durability = 100;
                durabilityBarWidth = 80;
                break;
                
            case COMMAND_FIX_AND_CONTINUE:
                payCoin(Racer.calcRepairCost(racer.getDurability()));
                racer.fix();
                beginGame();
                break;
        }
    }

    public void commandAction(Command c, Displayable d) {
        switch(c.getCommandType()) {
            case Command.OK:
                String newName = txtPlayerName.getString();
                if(!newName.equals("") && !newName.equals(playerName)) {
                    playerName = newName;
                    profile.storeSetting(Profile.RECORD_PLAYER_NAME, playerName);
                }
                Display.getDisplay(main).setCurrent(this);
                txtPlayerName = null;
                submitScore();
                break;
                
//#if Nokia_240_320_Touch
//#             case Command.BACK:
//#                 if(state == STATE_PLAY) hideNotify();
//#                 else if(state == STATE_SUMMARY) main.gotoShowcase();
//#                 break;
//#endif
        }
    }

    public void onSubmitSuccess() {
        if(dialog != null) dialog.forceClose();
    }

    public void onSubmitFail() {
        dialog = new MessageDialog(
            "CONNECTION FAILED",
            "Something went wrong. We|"+
            "are not able to submit your|"+
            "score to server at this time.|"+
            "Please try again later!",
            Dialog.COMMAND_NONE, this
        );
    }

    public void onGetRankSuccess(int rank) { }
    public void onGetRankFail() { }
    public void onView7Success(StringBuffer reader) { }
    public void onView7Fail() { }
    public void onViewAllSuccess(StringBuffer reader) { }
    public void onViewAllFail() { }
    public void onRemoveSuccess() { }
    public void onRemoveFail() { }
    
    private void touchDrive(short x) {
        if(x < CONTROLLER_CENTER-60) x = CONTROLLER_CENTER-60;
        else if(x > CONTROLLER_CENTER+60) x = CONTROLLER_CENTER+60;
        short xData = (short)((CONTROLLER_CENTER-x)*0.4f);
        racer.updateSkewAngle(xData);
        controllerX = x;
        returnControl = false;
    }

    public void dataReceived(SensorConnection sensor, Data[] data, boolean isDataLost) {
        if(state != STATE_PLAY) return;
        
        short xData = (short)(data[0].getDoubleValues()[0]*16);
        racer.updateSkewAngle(xData);
        
//        controllerX = (int)(CONTROLLER_CENTER-xData*2.5f);
//        if(controllerX < CONTROLLER_CENTER-60) controllerX = CONTROLLER_CENTER-60;
//        else if(controllerX > CONTROLLER_CENTER+60) controllerX = CONTROLLER_CENTER+60;
    }
}
