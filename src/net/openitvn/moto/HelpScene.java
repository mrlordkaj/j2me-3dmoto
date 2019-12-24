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
import javax.microedition.io.ConnectionNotFoundException;
//#if Nokia_240_320_Touch
//# import com.openitvn.game.NetworkHelper;
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
public class HelpScene extends GameScene implements IDialogHolder
//#if Nokia_240_320_Touch
//# , CommandListener
//#endif
{
    public static final String VERSION_STRING = "1.1";
    
    private static final byte STATE_MAIN = 1;
    private static final byte STATE_HELP = 2;
    private static final byte STATE_ABOUT = 3;
    
    private static final String[] about = new String[] {
        "3D MOTO",
        "v" + VERSION_STRING,
        "",
        "Developed by Openitvn Games",
        "Contact: mrlordkaj@gmail.com",
        "",
        "==============",
        "",
        "DISCLAIMER",
        "This software is provided",
        "without warranty of any kind.",
        "Developer will not be liable",
        "to any damages or loss",
        "relating to your use of",
        "this software.",
        "",
        "==============",
        "",
        "ART & PROGRAM",
        "Thinh Pham",
        "",
        "==============",
        "",
        "Copyright © 2014 Openitvn.",
        "All rights reserved.",
        "",
        "Openitvn title and Openitvn",
        "logo is a trademark of",
        "Openitvn team in Vietnam.",
    };
    
    private final int lineHeight, numLine;
    private int startLine, endLine;
    private int aboutTop;
    private void setAboutTop(int value) {
        aboutTop = value;
        startLine = (SettingScene.AREA_TOP - aboutTop) / lineHeight;
        endLine = startLine + numLine;
    }
    
    private Image imgBgTop, imgBgCenter, imgBgBottom, imgHelp;
    private Button btnClose, btnBack, btnContact, btnHowToPlay, btnAboutUs, btnMoreGames;
    
    private Dialog dialog;
    private Image imgSnapshot;
    private Main main;

    public HelpScene() {
        super(Main.getInstance(), 15);
        
        lineHeight = (int)(Main.FontPlain.getHeight()*1.2f);
        numLine = (SettingScene.AREA_BOTTOM - SettingScene.AREA_TOP) / lineHeight + 1;
        
        state = STATE_MAIN;
    }

    protected void prepareResource() {
        main = Main.getInstance();
        
        imgBgTop = ImageHelper.loadImage("/images/supportTop.png");
        imgBgCenter = ImageHelper.loadImage("/images/garageCenter.png");
        imgBgBottom = ImageHelper.loadImage("/images/garageBottom.png");
        imgHelp = ImageHelper.loadImage("/images/help.png");
        
//#if ScreenHeight == 320
//#         btnClose = new Button(ImageHelper.loadImage("/images/btnSummaryClose.png"), 35, 253, 50, 35);
//#         btnBack = new Button(ImageHelper.loadImage("/images/btnSummaryMenu.png"), 35, 253, 50, 35);
//#         btnContact = new Button(ImageHelper.loadImage("/images/btnSummaryContact.png"), 95, 253, 111, 35);
//#         btnHowToPlay = new Button(ImageHelper.loadImage("/images/btnHowToPlay.png"), 34, 78, 172, 48);
//#         btnAboutUs = new Button(ImageHelper.loadImage("/images/btnAboutUs.png"), 34, 135, 172, 48);
//#         btnMoreGames = new Button(ImageHelper.loadImage("/images/btnMoreGames.png"), 34, 192, 172, 48);
//#elif ScreenHeight == 400
        btnClose = new Button(ImageHelper.loadImage("/images/btnSummaryClose.png"), 35, 314, 50, 50);
        btnBack = new Button(ImageHelper.loadImage("/images/btnSummaryMenu.png"), 35, 314, 50, 50);
        btnContact = new Button(ImageHelper.loadImage("/images/btnSummaryContact.png"), 95, 314, 111, 50);
        btnHowToPlay = new Button(ImageHelper.loadImage("/images/btnHowToPlay.png"), 34, 86, 172, 58);
        btnAboutUs = new Button(ImageHelper.loadImage("/images/btnAboutUs.png"), 34, 162, 172, 58);
        btnMoreGames = new Button(ImageHelper.loadImage("/images/btnMoreGames.png"), 34, 238, 172, 58);
//#endif
        
//#if Nokia_240_320_Touch
//#         addCommand(new Command("Back", Command.BACK, 1));
//#         setCommandListener(this);
//#endif
    }

    protected void update() {
        if(dialog != null) {
            dialog.update();
            return;
        }
        
        switch(state) {
            case STATE_ABOUT:
                setAboutTop(aboutTop - 1);
                if(aboutTop < SettingScene.AREA_TOP - about.length*lineHeight) setAboutTop(SettingScene.AREA_BOTTOM);
                break;
        }
    }
    
    public void paint(Graphics g) {
        if(dialog != null) {
            if(imgSnapshot != null) g.drawImage(imgSnapshot, 0, 0, Graphics.LEFT | Graphics.TOP);
            dialog.paint(g);
            return;
        }
        
        if(state == STATE_HELP) {
            g.drawImage(imgHelp, 0, 0, Graphics.LEFT | Graphics.TOP);
            return;
        }
        
        g.drawImage(imgBgCenter, 0, SettingScene.AREA_TOP, Graphics.LEFT | Graphics.TOP);
        switch(state) {
            case STATE_MAIN:
                btnHowToPlay.paint(g);
                btnAboutUs.paint(g);
                btnMoreGames.paint(g);
                break;
                
            case STATE_ABOUT:
                g.setColor(0xffffff);
                g.setFont(Main.FontPlain);
                for(int i = startLine; i < endLine; i++) {
                    if(i >= 0) {
                        if(i >= about.length) break;
                        int localTop = aboutTop + (i+1)*lineHeight;
                        g.drawString(about[i], Main.SCREENSIZE_WIDTH/2, localTop, Graphics.HCENTER | Graphics.BASELINE);
                    }
                }
                break;
        }
        g.drawImage(imgBgTop, 0, 0, Graphics.LEFT | Graphics.TOP);
        g.drawImage(imgBgBottom, 0, SettingScene.AREA_BOTTOM, Graphics.LEFT | Graphics.TOP);
        if(state == STATE_ABOUT) btnBack.paint(g);
        else btnClose.paint(g);
        btnContact.paint(g);
    }
    
    private void takeSnapshot() {
        imgSnapshot = Image.createImage(Main.SCREENSIZE_WIDTH, Main.SCREENSIZE_HEIGHT);
        Graphics g = imgSnapshot.getGraphics();
        paint(g);
        g.drawImage(ImageHelper.loadImage("/images/darkScreen.png"), 0, 0, Graphics.TOP | Graphics.LEFT);
    }
    
    protected void pointerPressed(int x, int y) {
        if(dialog != null || state == STATE_HELP) return;
        
        if(state == STATE_ABOUT) btnBack.testHit(x, y);
        else if(state == STATE_MAIN) btnClose.testHit(x, y);
        btnContact.testHit(x, y);
        btnHowToPlay.testHit(x, y);
        btnAboutUs.testHit(x, y);
        btnMoreGames.testHit(x, y);
    }
    
    protected void pointerDragged(int x, int y) {
        if(dialog != null || state == STATE_HELP) return;
        
        if(state == STATE_ABOUT) btnBack.testHit(x, y);
        else if(state == STATE_MAIN) btnClose.testHit(x, y);
        btnContact.testHit(x, y);
        btnHowToPlay.testHit(x, y);
        btnAboutUs.testHit(x, y);
        btnMoreGames.testHit(x, y);
    }
    
    protected void pointerReleased(int x, int y) {
        if(dialog != null) {
            dialog.pointerReleased(x, y);
            return;
        }
        
        switch(state) {
            case STATE_MAIN:
                if(btnClose.gotClick(x, y)) main.gotoSplash();
                else if(btnHowToPlay.gotClick(x, y)) state = STATE_HELP;
                else if(btnAboutUs.gotClick(x, y)) gotoAbout();
                else if(btnMoreGames.gotClick(x, y)) moreGames();
                break;
                
            case STATE_ABOUT:
                if(btnBack.gotClick(x, y)) state = STATE_MAIN;
                break;
                
            case STATE_HELP:
                state = STATE_MAIN;
                break;
        }
        
        if(btnContact.gotClick(x, y)) {
           requestContact();
        }
    }
    
    private void requestContact() {
        takeSnapshot();
        dialog = new MessageDialog(
             "EMAIL ME",
             "For feedbacks or questions,|"+
             "you can email me at:||"+
             "mrlordkaj@gmail.com",
             Dialog.COMMAND_NONE, this
        );
    }
    
    private void gotoAbout() {
        aboutTop = SettingScene.AREA_BOTTOM;
        state = STATE_ABOUT;
    }
    
    private void moreGames() {
        try {
//#if Nokia_240_320_Touch
//#             NetworkHelper.redirectTo("http://store.ovi.mobi/publisher/Openitvn/", main);
//#else
            main.platformRequest("http://store.ovi.mobi/publisher/Openitvn/");
//#endif
        } catch (ConnectionNotFoundException ex) {
            takeSnapshot();
            dialog = new MessageDialog(
                "NETWORK ERROR",
                "Something went wrong with|"+
                "your network. Please try|"+
                "again later.",
                Dialog.COMMAND_NONE, this
            );
        }
    }

    public void runDialogCommand(byte command) {
        dialog = null;
        imgSnapshot = null;
    }

//#if Nokia_240_320_Touch
//#     public void commandAction(Command c, Displayable d) {
//#         switch(c.getCommandType()) {
//#             case Command.BACK:
//#                 switch(state) {
//#                     case STATE_MAIN:
//#                         main.gotoSplash();
//#                         break;
//#                         
//#                     default:
//#                         state = STATE_MAIN;
//#                         break;
//#                 }
//#                 break;
//#         }
//#     }
//#endif
}
