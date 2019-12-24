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
package com.openitvn.moto;

import com.openitvn.game.Button;
import com.openitvn.game.GameScene;
import com.openitvn.game.ILeaderboardCaller;
import com.openitvn.game.ImageHelper;
import com.openitvn.game.Leaderboard;
import com.openitvn.game.StringHelper;
import com.openitvn.game.bounce.Rectangle;
import java.util.Vector;
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
public class LeaderboardScene extends GameScene implements ILeaderboardCaller
//#if Nokia_240_320_Touch
//# , CommandListener
//#endif
{
    private static final byte STATE_FETCHING = 1;
    private static final byte STATE_FETCHED = 2;
    private static final byte STATE_FAILED = 3;
    
    private Image imgBgTop, imgBgCenter, imgBgBottom;
    private Button btnMenu;
    
    private String strRank;
    private String strRecord;
    private final Vector lines = new Vector();
    private int myScore;
    
    private final Main main = Main.getInstance();
    private final Rectangle btnAd = new Rectangle(0, 0, Main.SCREENSIZE_WIDTH, 60);
    private int adCountDown;
    
    private int marginTopStep = 0;
    private boolean dragging = false;
    private int targetTop;
    
    private final int numLine, lineHeight;
    private int top, startLine, endLine, height;
    private void setTop(int value) {
        top = value;
        startLine = (SettingScene.AREA_TOP - top) / lineHeight;
        endLine = startLine + numLine;
    }

    public LeaderboardScene() {
        super(Main.getInstance(), 15);
        
//#if ScreenHeight == 320
//#         lineHeight = (int)(Main.FontPlain.getHeight()*1.2f);
//#elif ScreenHeight == 400
        lineHeight = (int)(Main.FontPlain.getHeight()*1.4f);
//#endif
        numLine = (SettingScene.AREA_BOTTOM - SettingScene.AREA_TOP) / lineHeight + 1;
        adCountDown = getFPS()*2;
        
        fetchData();
    }

    protected void prepareResource() {
        imgBgTop = ImageHelper.loadImage("/images/leaderboardTop.png");
        imgBgCenter = ImageHelper.loadImage("/images/garageCenter.png");
        imgBgBottom = ImageHelper.loadImage("/images/garageBottom.png");
//#if ScreenHeight == 320
//#         btnMenu = new Button(ImageHelper.loadImage("/images/btnSummaryMenu.png"), 35, 253, 50, 35);
//#elif ScreenHeight == 400
        btnMenu = new Button(ImageHelper.loadImage("/images/btnSummaryMenu.png"), 35, 314, 50, 50);
//#endif
        
        strRank = "fetching rank...";
        myScore = Integer.parseInt(Profile.getInstance().getSetting(Profile.RECORD_HIGHSCORE));
        strRecord = "record: " + myScore;
        
//#if Nokia_240_320_Touch
//#         addCommand(new Command("Back", Command.BACK, 1));
//#         setCommandListener(this);
//#endif
    }
    
    private void fetchData() {
        state = STATE_FETCHING;
        Leaderboard.getRank(myScore, Main.getDeviceId(), this);
        Leaderboard.viewAll(this);
    }

    protected void update() {
        AdManager.autoUpdate(getFPS());
        if(adCountDown > 0) adCountDown--;
        
        if(dragging) return;
        
        if (targetTop > top + SettingScene.AUTOSCROLL_STEP) setTop(top + SettingScene.AUTOSCROLL_STEP);
        else if (targetTop < top - SettingScene.AUTOSCROLL_STEP) setTop(top - SettingScene.AUTOSCROLL_STEP);
        else setTop(targetTop);
    }
    
    public void paint(Graphics g) {
        g.drawImage(imgBgCenter, 0, SettingScene.AREA_TOP, Graphics.LEFT | Graphics.TOP);
        g.setColor(0xffffff);
        g.setFont(Main.FontBold);
        switch(state) {
            case STATE_FETCHING:
                g.drawString("fetching data...", Main.SCREENSIZE_WIDTH/2, Main.SCREENSIZE_HEIGHT/2, Graphics.HCENTER | Graphics.BASELINE);
                break;
                
            case STATE_FETCHED:
                for(int i = startLine; i < endLine; i++) {
                    if(i >= 0) {
                        if(i >= lines.size()) break;
                        int localTop = top + (i+1)*lineHeight;
                        String[] line = (String[])lines.elementAt(i);
                        g.drawString(Integer.toString(i+1), SettingScene.AREA_LEFT, localTop, Graphics.LEFT | Graphics.BASELINE);
                        g.drawString(line[0], SettingScene.AREA_LEFT + 20, localTop, Graphics.LEFT | Graphics.BASELINE);
                        g.drawString(line[1], SettingScene.AREA_RIGHT, localTop, Graphics.RIGHT | Graphics.BASELINE);
                    }
                }
                break;
                
            case STATE_FAILED:
                g.drawString("connection failed", Main.SCREENSIZE_WIDTH/2, Main.SCREENSIZE_HEIGHT/2, Graphics.HCENTER | Graphics.BASELINE);
                break;
        }
        g.drawImage(imgBgTop, 0, 0, Graphics.LEFT | Graphics.TOP);
        g.drawImage(imgBgBottom, 0, SettingScene.AREA_BOTTOM, Graphics.LEFT | Graphics.TOP);
//#if ScreenHeight == 320
//#         g.drawString(strRecord, 206, 264, Graphics.RIGHT | Graphics.BASELINE);
//#         g.drawString(strRank, 206, 284, Graphics.RIGHT | Graphics.BASELINE);
//#elif ScreenHeight == 400
        g.drawString(strRecord, 206, 326, Graphics.RIGHT | Graphics.BASELINE);
        g.drawString(strRank, 206, 354, Graphics.RIGHT | Graphics.BASELINE);
//#endif
        btnMenu.paint(g);
        
        if(Main.isShowAd() && adCountDown == 0 && AdManager.getImageAd() != null) {
            g.drawImage(AdManager.getImageAd(), Main.SCREENSIZE_WIDTH/2, 30, Graphics.HCENTER | Graphics.VCENTER);
        }
    }
    
    protected void pointerPressed(int x, int y) {
        if(Main.isShowAd() && btnAd.contains(x, y)) AdManager.visitAd();
        
        if(!dragging) {
            if(x >= SettingScene.AREA_LEFT && x <= SettingScene.AREA_RIGHT && y >= SettingScene.AREA_TOP && y <= SettingScene.AREA_BOTTOM) {
                dragging = true;
                marginTopStep = y - top;
            } else {
                btnMenu.testHit(x, y);
            }
        }
    }
    
    protected void pointerDragged(int x, int y) {
        if (dragging) {
            targetTop = y - marginTopStep;
            setTop(targetTop);
        } else {
            btnMenu.testHit(x, y);
        }
    }
    
    protected void pointerReleased(int x, int y) {
        if(!dragging) {
            if(btnMenu.gotClick(x, y)) {
                main.gotoSplash();
            }
        } else {
            if (top > SettingScene.AREA_TOP || height < SettingScene.AREA_BOTTOM - SettingScene.AREA_TOP) targetTop = SettingScene.AREA_TOP;
            else if (top < SettingScene.AREA_BOTTOM - height) targetTop = SettingScene.AREA_BOTTOM - height;
        }
        dragging = false;
    }

    public void onGetRankSuccess(int rank) {
        if(rank > 0) {
            strRank = "rank: #" + rank;
        } else {
            strRank = "not ranked yet";
        }
    }

    public void onGetRankFail() {
        strRank = "connection failed";
    }

    public void onViewAllSuccess(StringBuffer reader) {
        while(reader.length() > 0) {
            lines.addElement(StringHelper.split(":", StringHelper.readLine(reader)));
        }
        height = lineHeight*lines.size() + 4;
        targetTop = SettingScene.AREA_TOP;
        setTop(targetTop);
        state = STATE_FETCHED;
    }

    public void onViewAllFail() {
        state = STATE_FAILED;
    }

    public void onSubmitSuccess() { }
    public void onSubmitFail() { }
    public void onView7Success(StringBuffer reader) { }
    public void onView7Fail() { }
    public void onRemoveSuccess() { }
    public void onRemoveFail() { }

//#if Nokia_240_320_Touch
//#     public void commandAction(Command c, Displayable d) {
//#         switch(c.getCommandType()) {
//#             case Command.BACK:
//#                 Main.getInstance().gotoSplash();
//#                 break;
//#         }
//#     }
//#endif
}
