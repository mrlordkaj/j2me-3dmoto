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

import net.openitvn.game.GameScene;
import net.openitvn.game.ImageHelper;
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
public class InstructionScene extends GameScene implements IDialogHolder
//#if Nokia_240_320_Touch
//# , CommandListener
//#endif
{
    private static final byte COMMAND_SHOW_MOTO = 1;
    private static final byte COMMAND_SHOW_HANDLE_1 = 2;
    private static final byte COMMAND_SHOW_HANDLE_2 = 3;
    private static final byte COMMAND_SHOW_CAR = 4;
    private static final byte COMMAND_SHOW_COIN_1 = 5;
    private static final byte COMMAND_SHOW_COIN_2 = 6;
    private static final byte COMMAND_SHOW_SKILL_1 = 7;
    private static final byte COMMAND_SHOW_SKILL_2 = 8;
    private static final byte COMMAND_SHOW_POWERUP_1 = 9;
    private static final byte COMMAND_SHOW_POWERUP_2 = 10;
    private static final byte COMMAND_SHOW_SPEEDOMETER = 11;
    private static final byte COMMAND_SHOW_DISTANCE_1 = 12;
    private static final byte COMMAND_SHOW_DISTANCE_2 = 13;
    private static final byte COMMAND_SHOW_PAUSE = 14;
    private static final byte COMMAND_DONE = 15;
    
    private static final byte STATE_PREPARE = 1;
    private static final byte STATE_MAIN = 2;
    private static final byte STATE_DONE = 3;
    
    private Image imgScreenshot, imgDarkScreen;
    private Image imgMoto, imgDrive, imgCar, imgCoin;
    private Image imgSkill, imgPowerUp;
    private Image imgSpeedometer, imgDistance, imgPause;
    private final Main main = Main.getInstance();
    
    private int timeline;
    private boolean show;
    
    private Dialog dialog;
    private ToolTip tooltip;

    public InstructionScene() {
        super(Main.getInstance(), 15);
        
        timeline = getFPS()/2;
        state = STATE_PREPARE;
    }

    protected void prepareResource() {
        imgScreenshot = ImageHelper.loadImage("/help/screenshot.png");
        imgDarkScreen = ImageHelper.loadImage("/images/darkScreen.png");
        imgMoto = ImageHelper.loadImage("/help/moto.png");
        imgDrive = ImageHelper.loadImage("/help/drive.png");
        imgCar = ImageHelper.loadImage("/help/car.png");
        imgCoin = ImageHelper.loadImage("/help/coin.png");
        imgSkill = ImageHelper.loadImage("/help/skill.png");
        imgPowerUp = ImageHelper.loadImage("/help/powerup.png");
        imgSpeedometer = ImageHelper.loadImage("/help/speedometer.png");
        imgDistance = ImageHelper.loadImage("/help/distance.png");
        imgPause = ImageHelper.loadImage("/help/pause.png");
        
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
//            case STATE_PREPARE:
//                if(--timeline == 0) {
//                    
//                }
//                break;
                
            case STATE_MAIN:
                if(tooltip != null) tooltip.update();
                break;
                
            case STATE_DONE:
            case STATE_PREPARE:
                if(--timeline == 0) {
                    timeline = getFPS()/2;
                    show = !show;
                }
                break;
        }
    }
    
    public void paint(Graphics g) {
        g.drawImage(imgScreenshot, 0, 0, Graphics.TOP | Graphics.LEFT);
        
        if(dialog != null) {
            g.drawImage(imgDarkScreen, 0, 0, Graphics.LEFT | Graphics.TOP);
            dialog.paint(g);
        }
        
        switch(state) {
            case STATE_MAIN:
                g.drawImage(imgDarkScreen, 0, 0, Graphics.LEFT | Graphics.TOP);
                if(tooltip != null) {
                    switch(tooltip.getCommand()-1) {
//#if ScreenHeight == 320
                        case COMMAND_SHOW_MOTO:
                            g.drawImage(imgMoto, 105, 158, Graphics.LEFT | Graphics.TOP);
                            break;
                            
                        case COMMAND_SHOW_HANDLE_1:
                        case COMMAND_SHOW_HANDLE_2:
                            g.drawImage(imgDrive, 0, 269, Graphics.LEFT | Graphics.TOP);
                            break;
                            
                        case COMMAND_SHOW_CAR:
                            g.drawImage(imgCar, 45, 80, Graphics.LEFT | Graphics.TOP);
                            break;
                            
                        case COMMAND_SHOW_COIN_1:
                        case COMMAND_SHOW_COIN_2:
                            g.drawImage(imgCoin, 112, 112, Graphics.LEFT | Graphics.TOP);
                            break;
                            
                        case COMMAND_SHOW_SKILL_1:
                        case COMMAND_SHOW_SKILL_2:
                            g.drawImage(imgSkill, 0, 236, Graphics.LEFT | Graphics.TOP);
                            break;
                            
                        case COMMAND_SHOW_POWERUP_1:
                        case COMMAND_SHOW_POWERUP_2:
                            g.drawImage(imgPowerUp, 155, 236, Graphics.LEFT | Graphics.TOP);
                            break;
                            
                        case COMMAND_SHOW_SPEEDOMETER:
                            g.drawImage(imgSpeedometer, 81, 256, Graphics.LEFT | Graphics.TOP);
                            break;
                            
                        case COMMAND_SHOW_DISTANCE_1:
                        case COMMAND_SHOW_DISTANCE_2:
                            g.drawImage(imgDistance, 7, 7, Graphics.LEFT | Graphics.TOP);
                            break;
                            
                        case COMMAND_SHOW_PAUSE:
                            g.drawImage(imgPause, 181, 7, Graphics.LEFT | Graphics.TOP);
                            break;
//#elif ScreenHeight == 400
//#                             case COMMAND_SHOW_MOTO:
//#                             g.drawImage(imgMoto, 101, 197, Graphics.LEFT | Graphics.TOP);
//#                             break;
//#                             
//#                         case COMMAND_SHOW_HANDLE_1:
//#                         case COMMAND_SHOW_HANDLE_2:
//#                             g.drawImage(imgDrive, 0, 349, Graphics.LEFT | Graphics.TOP);
//#                             break;
//#                             
//#                         case COMMAND_SHOW_CAR:
//#                             g.drawImage(imgCar, 143, 34, Graphics.LEFT | Graphics.TOP);
//#                             break;
//#                             
//#                         case COMMAND_SHOW_COIN_1:
//#                         case COMMAND_SHOW_COIN_2:
//#                             g.drawImage(imgCoin, 33, 154, Graphics.LEFT | Graphics.TOP);
//#                             break;
//#                             
//#                         case COMMAND_SHOW_SKILL_1:
//#                         case COMMAND_SHOW_SKILL_2:
//#                             g.drawImage(imgSkill, 0, 316, Graphics.LEFT | Graphics.TOP);
//#                             break;
//#                             
//#                         case COMMAND_SHOW_POWERUP_1:
//#                         case COMMAND_SHOW_POWERUP_2:
//#                             g.drawImage(imgPowerUp, 155, 316, Graphics.LEFT | Graphics.TOP);
//#                             break;
//#                             
//#                         case COMMAND_SHOW_SPEEDOMETER:
//#                             g.drawImage(imgSpeedometer, 81, 336, Graphics.LEFT | Graphics.TOP);
//#                             break;
//#                             
//#                         case COMMAND_SHOW_DISTANCE_1:
//#                         case COMMAND_SHOW_DISTANCE_2:
//#                             g.drawImage(imgDistance, 7, 7, Graphics.LEFT | Graphics.TOP);
//#                             break;
//#                             
//#                         case COMMAND_SHOW_PAUSE:
//#                             g.drawImage(imgPause, 181, 7, Graphics.LEFT | Graphics.TOP);
//#                             break;
//#endif
                    }
                }
                tooltip.paint(g);
                break;
                
            case STATE_DONE:
            case STATE_PREPARE:
                if(dialog == null && show) {
                    g.setFont(Main.FontBold);
                    g.setColor(0x000000);
                    g.drawString("tap to continue", Main.SCREENSIZE_WIDTH/2, Main.SCREENSIZE_HEIGHT-80, Graphics.HCENTER | Graphics.BASELINE);
                    g.setColor(0xffffff);
                    g.drawString("tap to continue", Main.SCREENSIZE_WIDTH/2-1, Main.SCREENSIZE_HEIGHT-80-1, Graphics.HCENTER | Graphics.BASELINE);
                }
                break;
        }
    }
    
    protected void pointerReleased(int x, int y) {
        if(dialog != null) {
            dialog.pointerReleased(x, y);
            return;
        }
        
        switch(state) {
            case STATE_PREPARE:
                beginInstruction();
                break;
                
            case STATE_MAIN:
                if(tooltip != null) tooltip.pointerReleased(x, y);
                break;
                
            case STATE_DONE:
                main.gotoHelp();
                break;
        }
    }
    
//#if TKEY || QWERTY
    protected void keyReleased(int keyCode) {
        if (dialog != null) {
            dialog.keyReleased(keyCode);
            return;
        }
        
        switch (keyCode) {
            case KeyMap.KEY_RF:
                main.gotoHelp();
                return;
        }
        
        switch (state) {
            case STATE_PREPARE:
                beginInstruction();
                break;
                
            case STATE_MAIN:
                if (tooltip != null)
                    tooltip.keyReleased(keyCode);
                break;
                
            case STATE_DONE:
                main.gotoHelp();
                break;
        }
    }
//#endif
    
    private void beginInstruction() {
        dialog = new MessageDialog(
            "INSTRUCTION",
            "Welcome to 3D Moto 2!|"+
            "I'm going to explain some|"+
            "important things about|"+
            "gameplay for you. Listen!",
            COMMAND_SHOW_MOTO, this
        );
    }

    public void runDialogCommand(byte command) {
        dialog = null;
        tooltip = null;
        switch (command) {
            case COMMAND_SHOW_MOTO:
                tooltip = new ToolTip(
                    "Here is your moto,|"+
                    "you must keep it|"+
                    "running in safe as|"+
                    "long as posible!",
//#if ScreenHeight == 320
                    120, 110,
//#elif ScreenHeight == 400
//#                     120, 130,
//#endif
                    COMMAND_SHOW_HANDLE_1, this
                );
                state = STATE_MAIN;
                break;
                
            case COMMAND_SHOW_HANDLE_1:
                tooltip = new ToolTip(
                    "Use these buttons,|"+
                    "to drive the moto|"+
                    "left or right.",
//#if ScreenHeight == 320
                    120, 269,
//#elif ScreenHeight == 400
//#                     120, 349,
//#endif
                    COMMAND_SHOW_HANDLE_2, this
                );
                break;
                
            case COMMAND_SHOW_HANDLE_2:
                tooltip = new ToolTip(
                    "You can only press,|"+
                    "or hold when they|"+
                    "are lighted.|"+
                    "(depend control type)",
//#if ScreenHeight == 320
                    120, 269,
//#elif ScreenHeight == 400
//#                     120, 349,
//#endif
                    COMMAND_SHOW_CAR, this
                );
                break;
                
            case COMMAND_SHOW_CAR:
                tooltip = new ToolTip(
                    "This car is a|"+
                    "obstacle, avoid|"+
                    "colission with it.",
//#if ScreenHeight == 320
                    160, 96,
//#elif ScreenHeight == 400
//#                     160, 96,
//#endif
                    COMMAND_SHOW_COIN_1, this
                );
                break;
                
            case COMMAND_SHOW_COIN_1:
                tooltip = new ToolTip(
                    "Don't forget collect|"+
                    "coins on your road!",
//#if ScreenHeight == 320
                    120, 164,
//#elif ScreenHeight == 400
//#                     129, 166,
//#endif
                    COMMAND_SHOW_COIN_2, this
                );
                break;
                
            case COMMAND_SHOW_COIN_2:
                tooltip = new ToolTip(
                    "They are need for|"+
                    "repair, upgrade, or|"+
                    "buy new moto.",
//#if ScreenHeight == 320
                    120, 164,
//#elif ScreenHeight == 400
//#                     129, 166,
//#endif
                    COMMAND_SHOW_SKILL_1, this
                );
                break;
                
            case COMMAND_SHOW_SKILL_1:
                tooltip = new ToolTip(
                    "Each moto have|"+
                    "a special skill.",
//#if ScreenHeight == 320
                    110, 260,
//#elif ScreenHeight == 400
//#                     110, 340,
//#endif
                    COMMAND_SHOW_SKILL_2, this
                );
                break;
                
            case COMMAND_SHOW_SKILL_2:
                tooltip = new ToolTip(
                    "When skill bar|"+
                    "filled, you can|"+
                    "active skill.",
//#if ScreenHeight == 320
                    110, 260,
//#elif ScreenHeight == 400
//#                     110, 340,
//#endif
                    COMMAND_SHOW_POWERUP_1, this
                );
                break;
                
            case COMMAND_SHOW_POWERUP_1:
                tooltip = new ToolTip(
                    "Power-up is same,|"+
                    "but can be filled|"+
                    "by coins only.",
//#if ScreenHeight == 320
                    130, 260,
//#elif ScreenHeight == 400
//#                     130, 340,
//#endif
                    COMMAND_SHOW_POWERUP_2, this
                );
                break;
                
            case COMMAND_SHOW_POWERUP_2:
                tooltip = new ToolTip(
                    "You can change|"+
                    "power-up type|"+
                    "in Garage at|"+
                    "anytime.",
//#if ScreenHeight == 320
                    130, 260,
//#elif ScreenHeight == 400
//#                     130, 340,
//#endif
                    COMMAND_SHOW_SPEEDOMETER, this
                );
                break;
                
            case COMMAND_SHOW_SPEEDOMETER:
                tooltip = new ToolTip(
                    "Speedometer shows|"+
                    "your current speed.",
//#if ScreenHeight == 320
                    120, 210,
//#elif ScreenHeight == 400
//#                     120, 290,
//#endif
                    COMMAND_SHOW_DISTANCE_1, this
                );
                break;
                
            case COMMAND_SHOW_DISTANCE_1:
                tooltip = new ToolTip(
                    "Here is the distance|"+
                    "you reached (in meter).",
                    90, 86,
                    COMMAND_SHOW_DISTANCE_2, this
                );
                break;
                
            case COMMAND_SHOW_DISTANCE_2:
                tooltip = new ToolTip(
                    "It is also your|"+
                    "score in the|"+
                    "leaderboard.",
                    90, 86,
                    COMMAND_SHOW_PAUSE, this
                );
                break;
                
            case COMMAND_SHOW_PAUSE:
                tooltip = new ToolTip(
                    "Last thing, you|"+
                    "can pause game|"+
                    "at anytime.",
                    170, 86,
                    COMMAND_DONE, this
                );
                break;
                
            case COMMAND_DONE:
                dialog = new MessageDialog(
                    "INSTRUCTION",
                    "I'm done with you.|"+
                    "Let's enjoy, explore and|"+
                    "challenge yourself now.|"+
                    "Have fun!",
                    Dialog.COMMAND_NONE, this
                );
                timeline = getFPS()/2;
                state = STATE_DONE;
                break;
        }
    }

//#if Nokia_240_320_Touch
//#     public void commandAction(Command c, Displayable d) {
//#         switch(c.getCommandType()) {
//#             case Command.BACK:
//#                 main.gotoHelp();
//#                 break;
//#         }
//#     }
//#endif
}
