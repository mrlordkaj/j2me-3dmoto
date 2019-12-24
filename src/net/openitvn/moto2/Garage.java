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
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;

/**
 *
 * @author Thinh Pham
 */
public class Garage {
    public static final int ITEM_WIDTH = 172;
//#if ScreenHeight == 320
    public static final int ITEM_HEIGHT = 48;
    public static final int AREA_LEFT = 34;
    public static final int AREA_TOP = 78;
    public static final int AREA_RIGHT = 206;
    public static final int AREA_BOTTOM = 245;
//#elif ScreenHeight == 400
//#     public static final int ITEM_HEIGHT = 58;
//#     public static final int AREA_LEFT = 34;
//#     public static final int AREA_TOP = 86;
//#     public static final int AREA_RIGHT = 206;
//#     public static final int AREA_BOTTOM = 296;
//#endif
    public static final int AUTOSCROLL_STEP = 28;
    
    public static final byte TOTAL_ITEM = 8;
    public static final byte ITEM_NONE = -1;
    public static final byte ITEM_DURABILITY = 0;
    public static final byte ITEM_COLOR = 1;
    public static final byte ITEM_HANDLE = 2;
    public static final byte ITEM_CHARGER = 3;
    public static final byte ITEM_SKILL = 4;
    public static final byte ITEM_FLASH = 5;
    public static final byte ITEM_DOUBLE = 6;
    public static final byte ITEM_MAGNET = 7;
    
//#if ScreenHeight == 320
    private static final short[] OFFSET_TOP = new short[] { 20, 74, 128, 182, 236, 320, 374, 428 };
//#elif ScreenHeight == 400
//#     private static final short[] OFFSET_TOP = new short[] { 26, 96, 166, 236, 306, 414, 484, 554 };
//#endif
    
    private final int left = 34;
    private final Button[] items = new Button[TOTAL_ITEM];
    
    private int top = AREA_TOP;
    private final int height = OFFSET_TOP[OFFSET_TOP.length - 1] + ITEM_HEIGHT;
    public void setTop(int value) {
        top = value;
        for(byte i = 0; i < items.length; i++) {
            items[i].y = top + OFFSET_TOP[i];
        }
    }
    
    private int durability, durabilityBarWidth, repairCost;
    public void setDurability(int value) {
        durability = value;
        durabilityBarWidth = (int)(90.f * ((float)durability / 100.f));
        repairCost = Racer.calcRepairCost(durability);
    }
    public int getDurability() { return durability; }
    public int getRepairCost() { return repairCost; }
    
    private final int[] upgradeLevel = new int[TOTAL_ITEM], upgradeCost = new int[TOTAL_ITEM];
    public void setUpgradeLevel(byte type, int value) {
        upgradeLevel[type] = value;
        if(upgradeLevel[type] == 5) {
            upgradeCost[type] = 0;
        } else {
            upgradeCost[type] = 400;
            for(byte i = 1; i < upgradeLevel[type]; i++) upgradeCost[type] *= 2;
        }
    }
    public int getUpgradeLevel(byte type) { return upgradeLevel[type]; }
    public int getUpgradeCost(byte type) { return upgradeCost[type]; }
    
    public void upgradeItem(byte type) {
        if(upgradeLevel[type] < 5) setUpgradeLevel(type, upgradeLevel[type] + 1);
    }
    
    private byte activePowerUp;
    public void setActivePowerUp(byte value) { activePowerUp = value; }
    
    public String getUpgradeData() { return upgradeLevel[ITEM_HANDLE] + "" + upgradeLevel[ITEM_CHARGER] + "" + upgradeLevel[ITEM_SKILL]; }
    
    private int prevY, marginTopStep = 0;
    private boolean dragging = false, touching = false;
//#if TKEY || QWERTY
    private final Image imgSelector;
    private byte currentItem;
    private int selectorTop, selectorTargetTop;
    private boolean autoScroll;
    private void setCurrentItem(int value) {
        currentItem = (byte)value;
        selectorTargetTop = OFFSET_TOP[currentItem];
    }
//#endif
    
    private final Sprite sprUpgrade;
    private final Image imgActive;
    
    public Garage(Image[] images) {
        GarageResource resource = GarageResource.getInstance();
        
        for  (byte i = 0; i < items.length; i++) {
            items[i] = new Button(images[i], left, top + OFFSET_TOP[i], ITEM_WIDTH, ITEM_HEIGHT);
        }
        sprUpgrade = new Sprite(resource.getImageUpgradeLevel(), 90, 10);
        imgActive = resource.getImageActive();
        
        
//#if TKEY || QWERTY
        imgSelector = resource.getImageItemSelector();
        setCurrentItem(ITEM_DURABILITY);
        selectorTop = selectorTargetTop;
//#endif
    }
    
    public void update() {
        if (dragging) return;
        
        if (top + height < AREA_BOTTOM - AUTOSCROLL_STEP) {
            setTop(top + AUTOSCROLL_STEP);
        }
        else if (top > AREA_TOP + AUTOSCROLL_STEP) {
            setTop(top - AUTOSCROLL_STEP);
        }
        else {
            if (top + height < AREA_BOTTOM)
                setTop(AREA_BOTTOM - height);
            else if (top > AREA_TOP)
                setTop(AREA_TOP);
        }
        
//#if TKEY || QWERTY
        if (selectorTop < selectorTargetTop - AUTOSCROLL_STEP)
            selectorTop += AUTOSCROLL_STEP;
        else if (selectorTop > selectorTargetTop + AUTOSCROLL_STEP)
            selectorTop -= AUTOSCROLL_STEP;
        else
            selectorTop = selectorTargetTop;
        
        if (autoScroll) {
            int absoluteSelectorTop = selectorTop + top;
            if (absoluteSelectorTop < AREA_TOP)
                setTop(top+AUTOSCROLL_STEP);
            else if (absoluteSelectorTop + ITEM_HEIGHT > AREA_BOTTOM)
                setTop(top-AUTOSCROLL_STEP);
        }
//#endif
    }
    
    public void paint(Graphics g) {
        g.setFont(Main.FontBold);
        g.setColor(0xffffff);
        g.drawString("CURRENT MOTO", left, top, Graphics.LEFT | Graphics.TOP);
//#if ScreenHeight == 320
        g.drawString("POWER-UP", left, top + 300, Graphics.LEFT | Graphics.TOP);
//#elif ScreenHeight == 400
//#         g.drawString("POWER-UP", left, top + 388, Graphics.LEFT | Graphics.TOP);
//#endif
        for (byte i = 0; i < items.length; i++) {
            if (items[i].y > AREA_TOP - ITEM_HEIGHT && items[i].y < AREA_BOTTOM)
                items[i].paint(g);
        }
        
        int offsetY;
        // durability button content
        offsetY = items[ITEM_DURABILITY].active ? 5 : 0;
        if(durability < 20) g.setColor(0xff0000);
        else if(durability < 50) g.setColor(0xffff00);
        else g.setColor(0x00ff00);
//#if ScreenHeight == 320
        g.fillRect(left + 43, top + OFFSET_TOP[ITEM_DURABILITY] + 18 + offsetY, durabilityBarWidth, 10);
        g.setColor(0x000000);
        g.drawString(Integer.toString(repairCost), left + 60, top + OFFSET_TOP[ITEM_DURABILITY] + 42 + offsetY, Graphics.LEFT | Graphics.BASELINE);
//#elif ScreenHeight == 400
//#         g.fillRect(left + 43, top + OFFSET_TOP[ITEM_DURABILITY] + 23 + offsetY, durabilityBarWidth, 10);
//#         g.setColor(0x000000);
//#         g.drawString(Integer.toString(repairCost), left + 60, top + OFFSET_TOP[ITEM_DURABILITY] + 49 + offsetY, Graphics.LEFT | Graphics.BASELINE);
//#endif
        
        drawButtonContent(ITEM_HANDLE, g);
        drawButtonContent(ITEM_CHARGER, g);
        drawButtonContent(ITEM_SKILL, g);
        drawToggleContent(ITEM_FLASH, activePowerUp == Power.TYPE_FLASH, g);
        drawToggleContent(ITEM_DOUBLE, activePowerUp == Power.TYPE_DOUBLE, g);
        drawToggleContent(ITEM_MAGNET, activePowerUp == Power.TYPE_MAGNET, g);
        
//#if TKEY || QWERTY
        g.drawImage(imgSelector, AREA_LEFT, top + selectorTop, Graphics.LEFT | Graphics.TOP);
//#endif
    }
    
    private void drawToggleContent(byte item, boolean isActive, Graphics g) {
//#if ScreenHeight == 320
        int offsetY = items[item].active ? 5 : 0;
        sprUpgrade.setFrame(upgradeLevel[item]-1);
        sprUpgrade.setPosition(left + 43, top + OFFSET_TOP[item] + 18 + offsetY);
        sprUpgrade.paint(g);
        g.setColor(0x000000);
        g.drawString(Integer.toString(upgradeCost[item]), left + 60, top + OFFSET_TOP[item] + 42 + offsetY, Graphics.LEFT | Graphics.BASELINE);
        if (isActive) {
            g.drawImage(imgActive, left + 5, top + OFFSET_TOP[item] + 22 + offsetY, Graphics.LEFT | Graphics.TOP);
        }
//#elif ScreenHeight == 400
//#         int offsetY = items[item].active ? 5 : 0;
//#         sprUpgrade.setFrame(upgradeLevel[item]-1);
//#         sprUpgrade.setPosition(left + 43, top + OFFSET_TOP[item] + 23 + offsetY);
//#         sprUpgrade.paint(g);
//#         g.setColor(0x000000);
//#         g.drawString(Integer.toString(upgradeCost[item]), left + 60, top + OFFSET_TOP[item] + 47 + offsetY, Graphics.LEFT | Graphics.BASELINE);
//#         if (isActive) {
//#             g.drawImage(imgActive, left + 5, top + OFFSET_TOP[item] + 27 + offsetY, Graphics.LEFT | Graphics.TOP);
//#         }
//#endif
    }
    
    private void drawButtonContent(byte item, Graphics g) {
        int offsetY = items[item].active ? Button.DOWN_OFFSET : 0;
        sprUpgrade.setFrame(upgradeLevel[item]-1);
//#if ScreenHeight == 320
        sprUpgrade.setPosition(left + 43, top + OFFSET_TOP[item] + 18 + offsetY);
        sprUpgrade.paint(g);
        g.setColor(0x000000);
        g.drawString(Integer.toString(upgradeCost[item]), left + 60, top + OFFSET_TOP[item] + 42 + offsetY, Graphics.LEFT | Graphics.BASELINE);
//#elif ScreenHeight == 400
//#         sprUpgrade.setPosition(left + 43, top + OFFSET_TOP[item] + 23 + offsetY);
//#         sprUpgrade.paint(g);
//#         g.setColor(0x000000);
//#         g.drawString(Integer.toString(upgradeCost[item]), left + 60, top + OFFSET_TOP[item] + 49 + offsetY, Graphics.LEFT | Graphics.BASELINE);
//#endif
    }
    
    public void pointerPressed(int x, int y) {
        touching = true;
        marginTopStep = y - top;
        prevY = y;

        if (!dragging) {
            for (byte i = 0; i < items.length; i++) {
                items[i].testHit(x, y);
            }
        }
    }
    
    public boolean pointerDragged(int x, int y) {
        if (!dragging) {
            if (!touching) return false;
            if (Math.abs(prevY - y) > 10) {
                for (byte i = 0; i < items.length; i++) {
                    items[i].active = false;
                }
                dragging = true;
//#if TKEY || QWERTY
                autoScroll = false;
//#endif
            } else {
                for (byte i = 0; i < items.length; i++) {
                    items[i].testHit(x, y);
                }
            }
        } else {
            setTop(y - marginTopStep);
        }
        return dragging;
    }
    
    public byte pointerReleased(int x, int y) {
        if (dragging || x < AREA_LEFT || x > AREA_RIGHT || y < AREA_TOP || y > AREA_BOTTOM) {
            touching = false;
            dragging = false;
            return ITEM_NONE;
        }
        
        byte activeItem = ITEM_NONE;
        for (byte i = 0; i < items.length; i++) {
            if(items[i].gotClick(x, y)) activeItem = i;
        }
        touching = false;
        dragging = false;
        return activeItem;
    }

//#if TKEY || QWERTY
    public void keyPressed(int keyCode) {
        switch (keyCode) {
            case KeyMap.KEY_5:
            case KeyMap.KEY_0:
            case KeyMap.KEY_CF:
                autoScroll = true;
                items[currentItem].active = true;
                break;
        }
    }
    
    public byte keyReleased(int keyCode) {
        switch (keyCode) {
            case KeyMap.KEY_2:
            case KeyMap.KEY_UP:
                if (currentItem > 0) {
                    setCurrentItem(currentItem-1);
                    autoScroll = true;
                }
                break;
                
            case KeyMap.KEY_8:
            case KeyMap.KEY_DOWN:
                if (currentItem < 7) {
                    setCurrentItem(currentItem+1);
                    autoScroll = true;
                }
                break;
                
            case KeyMap.KEY_5:
            case KeyMap.KEY_0:
            case KeyMap.KEY_CF:
                if (items[currentItem].gotClick())
                    return currentItem;
                break;
        }
        return ITEM_NONE;
    }
//#endif
}
