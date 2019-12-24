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
import net.openitvn.game.StringHelper;
import net.openitvn.game.bounding.Rectangle;
//#if Nokia_240_320_Touch
//# import javax.microedition.lcdui.Command;
//# import javax.microedition.lcdui.CommandListener;
//# import javax.microedition.lcdui.Displayable;
//#endif
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;

/**
 *
 * @author Thinh Pham
 */
public class AchievementScene extends GameScene
//#if Nokia_240_320_Touch
//# implements CommandListener
//#endif
{
//#if ScreenHeight == 320
    private static final int ITEM_HEIGHT = 48;
    private static final int ITEM_MARGIN = 6;
//#elif ScreenHeight == 400
//#     private static final int ITEM_HEIGHT = 58;
//#     private static final int ITEM_MARGIN = 12;
//#endif
    private Image imgBgTop, imgBgCenter, imgBgBottom;
    private Sprite sprItem;
    private Button btnMenu;
    
    private String[] strTitle;
    private String[][] strContent;
    private boolean[] unlocked;
    private String strCompleted;
    
    private int top = Garage.AREA_TOP;
     private final int height = (ITEM_HEIGHT+ITEM_MARGIN)*30-ITEM_MARGIN;
    
    private int marginTopStep = 0;
    private boolean dragging = false;
//#if TKEY || QWERTY
    private boolean scrollUp = false, scrollDown = false;
//#endif
    
    private final Main main = Main.getInstance();
    private final Rectangle btnAd = new Rectangle(0, 0, Main.SCREENSIZE_WIDTH, 60);
    private int adCountDown;

    public AchievementScene() {
        super(Main.getInstance(), 15);
        
        adCountDown = getFPS()*2;
    }

    protected void prepareResource() {
        imgBgTop = ImageHelper.loadImage("/images/achievementTop.png");
        imgBgCenter = ImageHelper.loadImage("/images/garageCenter.png");
        imgBgBottom = ImageHelper.loadImage("/images/garageBottom.png");
        sprItem = new Sprite(ImageHelper.loadImage("/images/achievementItem.png"), 172, ITEM_HEIGHT);
//#if ScreenHeight == 320
        btnMenu = new Button(ImageHelper.loadImage("/images/btnSummaryMenu.png"), 35, 253, 50, 35);
//#elif ScreenHeight == 400
//#         btnMenu = new Button(ImageHelper.loadImage("/images/btnSummaryMenu.png"), 35, 314, 50, 50);
//#endif
        
        strTitle = new String[] {
            "NOOP RACER",
            "ADVANCED RACER",
            "SUPER RACER",
            "MASTER RACER",
            "ULTIMATE RACER",
            "THE POOR",
            "THE RICH",
            "COIN COLLECTOR",
            "MONEY LOVER",
            "LUCKY GUY",
            "TOO MUCH GEMS",
            "GEMS LOVE ME",
            "I'M RACER",
            "ROAD LOVER",
            "NEVER GIVE UP",
            "ROAD IS MY LIFE",
            "DESERT LIZARD",
            "SPIRIT RAIDER",
            "DEMON TOMAHAWK",
            "RUNNING IN FEAR",
            "LIKE A BIRD",
            "I'M GHOST",
            "THE DESTROYER",
            "EVERYONE MUST DIE",
            "JESUS BEHIND ME",
            "ULTIMATE THUNDER",
            "ULTIMATE LIZARD",
            "ULTIMATE SPIRIT",
            "ULTIMATE TOMAHAWK",
            "POWERFUL RACER"
        };
        
        String[] strDescription = new String[] {
            "Run " + StringHelper.formatNumber(Achievement.CONDITION_NOOP_RACER) + " meters|in one game.",
            "Run " + StringHelper.formatNumber(Achievement.CONDITION_ADVANCED_RACER) + " meters|in one game.",
            "Run " + StringHelper.formatNumber(Achievement.CONDITION_SUPER_RACER) + " meters|in one game.",
            "Run " + StringHelper.formatNumber(Achievement.CONDITION_MASTER_RACER) + " meters|in one game.",
            "Run " + StringHelper.formatNumber(Achievement.CONDITION_ULTIMATE_RACER) + "|meters in total.",
            "Collect " + StringHelper.formatNumber(Achievement.CONDITION_THE_POOR) + " coins in|one game.",
            "Collect " + StringHelper.formatNumber(Achievement.CONDITION_THE_RICH) + " coins in|one game.",
            "Collect " + StringHelper.formatNumber(Achievement.CONDITION_COIN_COLLECTOR) + " coins in|one game.",
            "Collect " + StringHelper.formatNumber(Achievement.CONDITION_MONEY_LOVER) + " coins|in one game.",
            "Collect " + Achievement.CONDITION_LUCKY_GUY + " gems in|one game.",
            "Collect " + Achievement.CONDITION_TOO_MUCH_GEMS + " gems in|one game.",
            "Collect " + Achievement.CONDITION_GEMS_LOVE_ME + " gems in|one game.",
            "Play game " + StringHelper.formatNumber(Achievement.CONDITION_IAM_RACER) + "|times.",
            "Play game " + StringHelper.formatNumber(Achievement.CONDITION_ROAD_LOVER) + "|times.",
            "Play game " + StringHelper.formatNumber(Achievement.CONDITION_NEVER_GIVE_UP) + "|times.",
            "Play game " + StringHelper.formatNumber(Achievement.CONDITION_ROAD_IS_MY_LIFE) + "|times.",
            "Unlock Desert|Lizard moto.",
            "Unlock Spirit|Raider moto.",
            "Unlock Demon|Tomahawk moto.",
            "Perform Brake skill|" + Achievement.CONDITION_RUNNING_IN_FEAR + " times.",
            "Perform Flying skill|" + Achievement.CONDITION_LIKE_A_BIRD + " times.",
            "Perform Intangible|skill " + Achievement.CONDITION_IAM_GHOST + " times.",
            "Perform Knockout|skill " + Achievement.CONDITION_THE_DESTROYER + " times.",
            "First time to die.",
            "Use Resurrection|" + Achievement.CONDITION_JESUS_BEHIND_ME + " times.",
            "Full upgrade the|Super Thunder.",
            "Full upgrade the|Desert Lizard.",
            "Full upgrade the|Spirit Raider.",
            "Full upgrade the|Demon Tomahawk.",
            "Full upgrade all|power-ups."
        };
        
        strContent = new String[Achievement.TOTAL_ACHIEVEMENT][];
        for (byte i = 0; i < Achievement.TOTAL_ACHIEVEMENT; i++) {
            strContent[i] = StringHelper.split("|", strDescription[i]);
        }
        
        int completed = 0;
        unlocked = new boolean[Achievement.TOTAL_ACHIEVEMENT];
        Achievement achievement = Achievement.getInstance();
        for (byte i = 0; i < Achievement.TOTAL_ACHIEVEMENT; i++) {
            unlocked[i] = achievement.unlocked(i);
            if(unlocked[i]) completed++;
        }
        strCompleted = completed + "/" + Achievement.TOTAL_ACHIEVEMENT + " trophies.";
        
//#if Nokia_240_320_Touch
//#         addCommand(new Command("Back", Command.BACK, 1));
//#         setCommandListener(this);
//#endif
    }

    protected void update() {
        AdManager.autoUpdate(getFPS());
        if (adCountDown > 0)
            adCountDown--;
        
        if (dragging) return;
        
//#if TKEY || QWERTY
        if (scrollDown) {
            if (top > Garage.AREA_BOTTOM - height + Garage.AUTOSCROLL_STEP)
                top -= Garage.AUTOSCROLL_STEP;
            else
                top = Garage.AREA_BOTTOM - height;
        }
        if (scrollUp) {
            if (top < Garage.AREA_TOP - Garage.AUTOSCROLL_STEP)
                top += Garage.AUTOSCROLL_STEP;
            else
                top = Garage.AREA_TOP;
        }
//#endif
        
        if (top + height < Garage.AREA_BOTTOM - Garage.AUTOSCROLL_STEP) {
            top += Garage.AUTOSCROLL_STEP;
        }
        else if (top > Garage.AREA_TOP + Garage.AUTOSCROLL_STEP) {
            top -= Garage.AUTOSCROLL_STEP;
        }
        else {
            if (top + height < Garage.AREA_BOTTOM) top = Garage.AREA_BOTTOM - height;
            else if (top > Garage.AREA_TOP) top = Garage.AREA_TOP;
        }
    }

    public void paint(Graphics g) {
        g.drawImage(imgBgCenter, 0, Garage.AREA_TOP, Graphics.LEFT | Graphics.TOP);
        g.setFont(Main.FontPlain);
        for (byte i = 0; i < strTitle.length; i++) {
            int y = top + i*(ITEM_HEIGHT+ITEM_MARGIN);
            if(y > Garage.AREA_TOP - Garage.ITEM_HEIGHT && y < Garage.AREA_BOTTOM) drawItem(i, g);
        }
        g.drawImage(imgBgTop, 0, 0, Graphics.LEFT | Graphics.TOP);
        g.drawImage(imgBgBottom, 0, Garage.AREA_BOTTOM, Graphics.LEFT | Graphics.TOP);
        g.setColor(0xffffff);
        g.setFont(Main.FontBold);
//#if ScreenHeight == 320
        g.drawString("You completed:", 206, 264, Graphics.RIGHT | Graphics.BASELINE);
        g.drawString(strCompleted, 206, 284, Graphics.RIGHT | Graphics.BASELINE);
//#elif ScreenHeight == 400
//#         g.drawString("You completed:", 206, 326, Graphics.RIGHT | Graphics.BASELINE);
//#         g.drawString(strCompleted, 206, 354, Graphics.RIGHT | Graphics.BASELINE);
//#endif
        btnMenu.paint(g);
        
        if (Main.isShowAd() && adCountDown == 0 && AdManager.getImageAd() != null) {
            g.drawImage(AdManager.getImageAd(), Main.SCREENSIZE_WIDTH/2, 30, Graphics.HCENTER | Graphics.VCENTER);
        }
    }
    
    private void drawItem(byte itemId, Graphics g) {
        int x = Garage.AREA_LEFT;
        int y = top + itemId*(ITEM_HEIGHT+ITEM_MARGIN);
        sprItem.setPosition(x, y);
        sprItem.setFrame(unlocked[itemId] ? 1 : 0);
        sprItem.paint(g);
        
//#if ScreenHeight == 320
        g.setColor(0xaa0000);
        g.drawString(strTitle[itemId], x+42, y+14, Graphics.LEFT | Graphics.BASELINE);
        g.setColor(0x000000);
        for (byte i = 0; i < strContent[itemId].length; i++) {
            g.drawString(strContent[itemId][i], x+42, y+28+i*14, Graphics.LEFT | Graphics.BASELINE);
        }
//#elif ScreenHeight == 400
//#         g.setColor(0xaa0000);
//#         g.drawString(strTitle[itemId], x+42, y+16, Graphics.LEFT | Graphics.BASELINE);
//#         g.setColor(0x000000);
//#         for (byte i = 0; i < strContent[itemId].length; i++) {
//#             g.drawString(strContent[itemId][i], x+42, y+32+i*16, Graphics.LEFT | Graphics.BASELINE);
//#         }
//#endif
        
    }
    
    protected void pointerPressed(int x, int y) {
        if (Main.isShowAd() && btnAd.contains(x, y))
            AdManager.visitAd();
        
        if (!dragging) {
            if (x >= Garage.AREA_LEFT && x <= Garage.AREA_RIGHT && y >= Garage.AREA_TOP && y <= Garage.AREA_BOTTOM) {
                dragging = true;
                marginTopStep = y - top;
            }
            else {
                btnMenu.testHit(x, y);
            }
        }
    }
    
    protected void pointerDragged(int x, int y) {
        if (dragging) {
            top = y - marginTopStep;
        }
        else {
            btnMenu.testHit(x, y);
        }
    }
    
    protected void pointerReleased(int x, int y) {
        if (!dragging && btnMenu.gotClick(x, y)) {
            Main.getInstance().gotoSplash();
        }
        dragging = false;
    }
    
//#if TKEY || QWERTY
    protected void keyPressed(int keyCode) {
        switch (keyCode) {
            case KeyMap.KEY_LF:
                btnMenu.active = true;
                break;
                
            case KeyMap.KEY_2:
            case KeyMap.KEY_UP:
                scrollUp = true;
                break;
                
            case KeyMap.KEY_8:
            case KeyMap.KEY_DOWN:
                scrollDown = true;
                break;
                
            case KeyMap.KEY_0:
                if (Main.isShowAd())
                    AdManager.visitAd();
                break;
        }
    }
    
    protected void keyReleased(int keyCode) {
        switch (keyCode) {
            case KeyMap.KEY_LF:
                if (btnMenu.gotClick())
                    main.gotoSplash();
                break;
                
            case KeyMap.KEY_2:
            case KeyMap.KEY_UP:
                scrollUp = false;
                break;
                
            case KeyMap.KEY_8:
            case KeyMap.KEY_DOWN:
                scrollDown = false;
                break;
                
            case KeyMap.KEY_RF:
                main.gotoSplash();
                break;
        }
    }
//#endif

//#if Nokia_240_320_Touch
//#     public void commandAction(Command c, Displayable d) {
//#         switch (c.getCommandType()) {
//#             case Command.BACK:
//#                 Main.getInstance().gotoSplash();
//#                 break;
//#         }
//#     }
//#endif
}
