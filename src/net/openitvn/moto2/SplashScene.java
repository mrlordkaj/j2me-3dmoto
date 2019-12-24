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
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import net.openitvn.game.bounding.Rectangle;
//#if Nokia_240_320_Touch
//# import net.openitvn.game.NetworkHelper;
//# import javax.microedition.io.ConnectionNotFoundException;
//# import javax.microedition.lcdui.Command;
//# import javax.microedition.lcdui.CommandListener;
//# import javax.microedition.lcdui.Displayable;
//#endif

/**
 *
 * @author Thinh Pham
 */
public class SplashScene extends GameScene implements IDialogHolder
//#if Nokia_240_320_Touch
//# , CommandListener
//#endif
{
    public static final byte STATE_LOGO = 1;
    public static final byte STATE_SOUND = 2;
    public static final byte STATE_ON_SOUND = 3;
    public static final byte STATE_SPLASH = 4;
    public static final byte STATE_MENU = 5;
    
    private static final byte COMMAND_QUIT = 1;
    
    private final Rectangle btnAd = new Rectangle(0, 0, Main.SCREENSIZE_WIDTH, 60);
    private final Rectangle btnYes = new Rectangle(0, Main.SCREENSIZE_HEIGHT - 60, 80, 60);
    private final Rectangle btnNo = new Rectangle(Main.SCREENSIZE_WIDTH - 80, Main.SCREENSIZE_HEIGHT - 60, 80, 60);
    private final SoundManager sound = SoundManager.getInstance();
    
    private final Main main = Main.getInstance();
    private Image imgSplash, imgSnapshot;
    private Button btnPlay, btnLeaderboard, btnAchievement, btnSetting, btnHelp;
//#if Nokia_240_320_Touch
//#     private Button btnRate;
//#else
    private Button btnExit;
//#endif
    private int timeline = 0;
    private boolean showSplashString;
    private final String strSplash = "touch to continue";
    private Dialog dialog;
    private MessageQueue message;
    
    private int connectTimeout = 20;
    private static boolean connectionTested = false;
    
    public static SplashScene getInstance() {
        if(instance instanceof SplashScene) {
            return (SplashScene)instance;
        }
        return null;
    }
    
    public SplashScene() {
        super(Main.getInstance(), 15);
        state = STATE_LOGO;
    }
    
    public SplashScene(byte startupState) {
        super(Main.getInstance(), 10);
        switch(startupState) {
            case STATE_SPLASH:
                gotoSplash();
                break;
                
            default:
                state = startupState;
                break;
        }
    }

    protected void prepareResource() {
        message = MessageQueue.getInstance();
        imgSplash = ImageHelper.loadImage("/images/logo.png");
//#if Nokia_240_320_Touch
//#         btnPlay = new Button(ImageHelper.loadImage("/images/btnMainPlay.png"), 80, Main.SCREENSIZE_HEIGHT/2-30, 80, 60);
//#         btnLeaderboard = new Button(ImageHelper.loadImage("/images/btnMainLeaderboard.png"), 6, Main.SCREENSIZE_HEIGHT-70, 40, 40);
//#         btnAchievement = new Button(ImageHelper.loadImage("/images/btnMainAchievement.png"), 53, Main.SCREENSIZE_HEIGHT-70, 40, 40);
//#         btnSetting = new Button(ImageHelper.loadImage("/images/btnMainSetting.png"), 100, Main.SCREENSIZE_HEIGHT-70, 40, 40);
//#         btnRate = new Button(ImageHelper.loadImage("/images/btnMainRate.png"), 147, Main.SCREENSIZE_HEIGHT-70, 40, 40);
//#         btnHelp = new Button(ImageHelper.loadImage("/images/btnMainHelp.png"), 194, Main.SCREENSIZE_HEIGHT-70, 40, 40);
//#else
        btnPlay = new Button(ImageHelper.loadImage("/images/btnMainPlay.png"), 80, Main.SCREENSIZE_HEIGHT/2-30, 80, 60);
        btnLeaderboard = new Button(ImageHelper.loadImage("/images/btnMainLeaderboard.png"), 6, Main.SCREENSIZE_HEIGHT-70, 40, 40);
        btnAchievement = new Button(ImageHelper.loadImage("/images/btnMainAchievement.png"), 53, Main.SCREENSIZE_HEIGHT-70, 40, 40);
        btnSetting = new Button(ImageHelper.loadImage("/images/btnMainSetting.png"), 100, Main.SCREENSIZE_HEIGHT-70, 40, 40);
        btnHelp = new Button(ImageHelper.loadImage("/images/btnMainHelp.png"), 147, Main.SCREENSIZE_HEIGHT-70, 40, 40);
        btnExit = new Button(ImageHelper.loadImage("/images/btnMainExit.png"), 194, Main.SCREENSIZE_HEIGHT-70, 40, 40);
//#endif
        
//#if Nokia_240_320_Touch
//#         addCommand(new Command("Back", Command.BACK, 1));
//#         setCommandListener(this);
//#endif
    }
    
    private void gotoSplash() {
        sound.playMusic(Main.SOUND_MENU, SoundManager.TYPE_MIDI);
        timeline = 0;
        imgSplash = ImageHelper.loadImage("/images/splash.png");
        state = STATE_SPLASH;
    }
    
    private void gotoSound() {
        imgSplash = ImageHelper.loadImage("/images/sound.png");
        state = STATE_SOUND;
    }
    
    private void takeSnapshot() {
        imgSnapshot = Image.createImage(Main.SCREENSIZE_WIDTH, Main.SCREENSIZE_HEIGHT);
        Graphics g = imgSnapshot.getGraphics();
        g.drawImage(ImageHelper.loadImage("/images/splash.png"), 0, 0, Graphics.TOP | Graphics.LEFT);
        g.drawImage(ImageHelper.loadImage("/images/darkScreen.png"), 0, 0, Graphics.TOP | Graphics.LEFT);
    }
    
    protected void update() {
        AdManager.autoUpdate(getFPS());
        
        if(dialog != null) {
            dialog.update();
            if(Main.isShowAd() && dialog instanceof IndicatorDialog) {
                if(dialog.getState() == Dialog.STATE_OPEN) {
                    if(--timeline < 0) {
                        if(--connectTimeout < 0) testConnectionDone();
                        timeline = getFPS();
                    }
                }
            }
            return;
        }
        
        switch(state) {
            case STATE_LOGO:
                if(timeline < getFPS()*2) timeline++;
                else gotoSound();
                break;
                
            case STATE_ON_SOUND:
                if(timeline < getFPS()*1) timeline++;
                else gotoSplash();
                break;
                
            case STATE_SPLASH:
                if(++timeline == 5) {
                    showSplashString = !showSplashString;
                    timeline = 0;
                }
                message.update();
                break;
                
            case STATE_MENU:
                message.update();
                break;
        }
    }
    
    public void paint(Graphics g) {
        if(dialog != null) {
            if(imgSnapshot != null) g.drawImage(imgSnapshot, 0, 0, Graphics.LEFT | Graphics.TOP);
            dialog.paint(g);
            if(Main.isShowAd() && dialog instanceof IndicatorDialog) {
                if(dialog.getState() == Dialog.STATE_OPEN) {
                    g.setFont(Main.FontPlain);
                    g.setColor(0xff0000);
                    g.drawString("(timeout "+connectTimeout+")", Main.SCREENSIZE_WIDTH/2, Main.SCREENSIZE_HEIGHT/2+22, Graphics.HCENTER | Graphics.BASELINE);
                }
            }
            return;
        }
        
        switch(state) {
            case STATE_LOGO:
                g.setColor(0x000000);
                g.fillRect(0, 0, Main.SCREENSIZE_WIDTH, Main.SCREENSIZE_HEIGHT);
                g.drawImage(imgSplash, Main.SCREENSIZE_WIDTH/2, Main.SCREENSIZE_HEIGHT/2, Graphics.HCENTER | Graphics.VCENTER);
                break;
                
            case STATE_SOUND:
                g.setColor(0x000000);
                g.fillRect(0, 0, Main.SCREENSIZE_WIDTH, Main.SCREENSIZE_HEIGHT);
                g.drawImage(imgSplash, Main.SCREENSIZE_WIDTH/2, Main.SCREENSIZE_HEIGHT/2, Graphics.HCENTER | Graphics.VCENTER);
                g.setColor(0xffffff);
                g.setFont(Main.FontBold);
                g.drawString("Do you want sound?", Main.SCREENSIZE_WIDTH/2, Main.SCREENSIZE_HEIGHT/2-48, Graphics.HCENTER | Graphics.BASELINE);
                g.drawString("Yes", 10, Main.SCREENSIZE_HEIGHT - 10, Graphics.LEFT | Graphics.BASELINE);
                g.drawString("No", Main.SCREENSIZE_WIDTH - 10, Main.SCREENSIZE_HEIGHT - 10, Graphics.RIGHT | Graphics.BASELINE);
                g.setFont(Main.FontPlain);
                g.setColor(0x494949);
                g.drawString("Disable sound may help game", Main.SCREENSIZE_WIDTH/2, Main.SCREENSIZE_HEIGHT/2+52, Graphics.HCENTER | Graphics.BASELINE);
                g.drawString("runs smoothly on weak devices.", Main.SCREENSIZE_WIDTH/2, Main.SCREENSIZE_HEIGHT/2+68, Graphics.HCENTER | Graphics.BASELINE);
                break;
                
            case STATE_SPLASH:
                g.drawImage(imgSplash, 0, 0, Graphics.TOP | Graphics.LEFT);
                if(showSplashString) {
                    g.setFont(Main.FontBold);
                    g.setColor(0x000000);
                    g.drawString(strSplash, Main.SCREENSIZE_WIDTH/2, Main.SCREENSIZE_HEIGHT - 40, Graphics.BASELINE | Graphics.HCENTER);
                    g.setColor(0xffffff);
                    g.drawString(strSplash, Main.SCREENSIZE_WIDTH/2-1, Main.SCREENSIZE_HEIGHT - 41, Graphics.BASELINE | Graphics.HCENTER);
                }
                message.paint(g);
                break;
                
            case STATE_MENU:
                g.drawImage(imgSnapshot, 0, 0, Graphics.TOP | Graphics.LEFT);
                btnPlay.paint(g);
                btnLeaderboard.paint(g);
                btnAchievement.paint(g);
                btnSetting.paint(g);
                btnHelp.paint(g);
//#if Nokia_240_320_Touch
//#                 btnRate.paint(g);
//#else
                btnExit.paint(g);
//#endif
                if(Main.isShowAd() && AdManager.getImageAd() != null) {
                    g.drawImage(AdManager.getImageAd(), Main.SCREENSIZE_WIDTH/2, 30, Graphics.HCENTER | Graphics.VCENTER);
                }
                message.paint(g);
                break;
        }
    }
    
    protected void pointerPressed(int x, int y) {
        if(dialog != null) return;
        
        switch(state) {
            case STATE_MENU:
                if(Main.isShowAd() && btnAd.contains(x, y)) AdManager.visitAd();
                btnPlay.testHit(x, y);
                btnLeaderboard.testHit(x, y);
                btnAchievement.testHit(x, y);
                btnSetting.testHit(x, y);
                btnHelp.testHit(x, y);
//#if Nokia_240_320_Touch
//#                 btnRate.testHit(x, y);
//#else
                btnExit.testHit(x, y);
//#endif
                break;
        }
    }
    
    protected void pointerDragged(int x, int y) {
        if(dialog != null) return;
        
        switch(state) {
            case STATE_MENU:
                btnPlay.testHit(x, y);
                btnLeaderboard.testHit(x, y);
                btnAchievement.testHit(x, y);
                btnSetting.testHit(x, y);
                btnHelp.testHit(x, y);
//#if Nokia_240_320_Touch
//#                 btnRate.testHit(x, y);
//#else
                btnExit.testHit(x, y);
//#endif
                break;
        }
    }
    
    protected void pointerReleased(int x, int y) {
        if(dialog != null) {
            dialog.pointerReleased(x, y);
            return;
        }
        
        switch(state) {
            case STATE_SOUND:
                if(btnYes.contains(x, y)) {
                    setSoundEnabled();
                } else if(btnNo.contains(x, y)) {
                    setSoundDisabled();
                }
                break;
                
            case STATE_SPLASH:
                openMainMenu();
                break;
                
            case STATE_MENU:
                if(btnPlay.gotClick(x, y)) {
                    main.gotoShowcase();
                } else if(btnLeaderboard.gotClick(x, y)) {
                    main.gotoLeaderboard();
                } else if(btnAchievement.gotClick(x, y)) {
                    main.gotoAchievement();
                } else if(btnSetting.gotClick(x, y)) {
                    main.gotoSetting();
                } else if(btnHelp.gotClick(x, y)) {
                    main.gotoHelp();
                }
//#if Nokia_240_320_Touch
//#                 else if(btnRate.gotClick(x, y)) {
//#                     try {
//#                         NetworkHelper.redirectTo("http://store.ovi.mobi/content/600603/comments/add", main);
//#                     } catch (ConnectionNotFoundException ex) {
//#                         takeSnapshot();
//#                         dialog = new MessageDialog(
//#                             "NETWORK ERROR",
//#                             "Something went wrong with|"+
//#                             "your network. Please try|"+
//#                             "again later.",
//#                             Dialog.COMMAND_NONE, this
//#                         );
//#                     }
//#                 }
//#else
                else if(btnExit.gotClick(x, y)) {
                    requestExit();
                }
//#endif
                break;
        }
    }
    
//#if TKEY || QWERTY
    protected void keyPressed(int keyCode) {
        if (dialog != null) return;
        
        switch (state) {
            case STATE_MENU:
                switch (keyCode) {
                    case KeyMap.KEY_5:
                    case KeyMap.KEY_CF:
                        btnPlay.active = true;
                        break;
                        
                    case KeyMap.KEY_1:
                        btnLeaderboard.active = true;
                        break;
                        
                    case KeyMap.KEY_2:
                        btnAchievement.active = true;
                        break;
                        
                    case KeyMap.KEY_3:
                        btnSetting.active = true;
                        break;
                        
                    case KeyMap.KEY_4:
                        btnHelp.active = true;
                        break;
                        
                    case KeyMap.KEY_6:
                        btnExit.active = true;
                        break;
                        
                    case KeyMap.KEY_0:
                        if (Main.isShowAd())
                            AdManager.visitAd();
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
            case STATE_SOUND:
                switch (keyCode) {
                    case KeyMap.KEY_LF:
                        setSoundEnabled();
                        break;
                        
                    case KeyMap.KEY_RF:
                        setSoundDisabled();
                        break;
                }
                break;
                
            case STATE_SPLASH:
                switch (keyCode) {
                    case KeyMap.KEY_RF:
                        requestExit();
                        break;

                        
                    default:
                        openMainMenu();
                        break;
                }
                break;
                
            case STATE_MENU:
                switch (keyCode) {
                    case KeyMap.KEY_5:
                    case KeyMap.KEY_CF:
                        if(btnPlay.gotClick()) main.gotoShowcase();
                        break;
                        
                    case KeyMap.KEY_1:
                        if(btnLeaderboard.gotClick()) main.gotoLeaderboard();
                        break;
                        
                    case KeyMap.KEY_2:
                        if(btnAchievement.gotClick()) main.gotoAchievement();
                        break;
                        
                    case KeyMap.KEY_3:
                        if(btnSetting.gotClick()) main.gotoSetting();
                        break;
                        
                    case KeyMap.KEY_4:
                        if(btnHelp.gotClick()) main.gotoHelp();
                        break;
                        
                    case KeyMap.KEY_6:
                        if(btnExit.gotClick()) requestExit();
                        break;
                        
                    case KeyMap.KEY_RF:
                        gotoSplash();
                        break;
                }
                break;
        }
    }
//#endif
    
    public void testConnectionDone() {
        connectionTested = true;
        dialog.forceClose();
        state = STATE_MENU;
    }

//#if Nokia_240_320_Touch
//#     public void commandAction(Command c, Displayable d) {
//#         switch(c.getCommandType()) {
//#             case Command.BACK:
//#                 switch(state) {
//#                     case STATE_MENU:
//#                         gotoSplash();
//#                         break;
//#                         
//#                     case STATE_SPLASH:
//#                         requestExit();
//#                         break;
//#                 }
//#                 break;
//#         }
//#     }
//#endif
    
    private void setSoundEnabled() {
        sound.setEnabled(true);
        sound.sound("/sounds/accept.wav", SoundManager.TYPE_WAVE);
        timeline = 0;
        state = STATE_ON_SOUND;
    }
    
    private void setSoundDisabled() {
        sound.setEnabled(false);
        gotoSplash();
    }
    
    private void openMainMenu() {
        takeSnapshot();
        if (Main.isShowAd() && !connectionTested) {
            timeline = getFPS();
            connectTimeout = 20;
            dialog = new IndicatorDialog(
                "connecting to server",
                180, 80,
                this
            );
            Preloader.testConnection();
        }
        else {
            state = STATE_MENU;
        }
    }
    
    private void requestExit() {
        takeSnapshot();
        dialog = new ConfirmDialog("QUIT APP",
            "Do you want to quit|"+
            "this app now?",
            COMMAND_QUIT, this
        );
    }

    public void runDialogCommand(byte command) {
        dialog = null;
        switch (command) {
            case COMMAND_QUIT:
                main.exitGame();
                break;
        }
    }
}
