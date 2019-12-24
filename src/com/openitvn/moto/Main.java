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

import com.nokia.mid.ui.DeviceControl;
import com.openitvn.game.GameScene;
import com.openitvn.game.RandomHelper;
import com.openitvn.game.Setting;
import java.io.IOException;
import java.util.Calendar;
import java.util.Hashtable;
import javax.microedition.io.Connector;
import javax.microedition.lcdui.Font;
import javax.microedition.midlet.*;
import javax.microedition.sensor.ChannelInfo;
import javax.microedition.sensor.SensorConnection;
import javax.microedition.sensor.SensorInfo;
import javax.microedition.sensor.SensorManager;
import vAdEngine.VservInterface;
import vAdEngine.VservManager;

/**
 * @author Thinh Pham
 */
public class Main extends MIDlet implements VservInterface {
//#if ScreenWidth == 240 && ScreenHeight == 400
    public static final int SCREENSIZE_WIDTH = 240;
    public static final int SCREENSIZE_HEIGHT = 400;
//#elif ScreenWidth == 240 && ScreenHeight == 320
//#     public static final int SCREENSIZE_WIDTH = 240;
//#     public static final int SCREENSIZE_HEIGHT = 320;
//#endif
    private static final int BUFFER_SIZE = 3;
    private static final String RESOURCE_SETTING = "oivsetting";
    public static final byte SETTING_VISUAL_EFFECT = 0;
    public static final byte SETTING_ADVISIT_TIME = 1;
    public static final byte SETTING_DEVICE_ID = 2;
    public static final byte SETTING_ACCELEROMETER = 3;
    public static final byte SETTING_BACKLIGHT = 4;
    
    public static final byte PRODUCT_COIN = 0;
    public static final byte PRODUCT_GEM = 1;
    public static final byte PRODUCT_LIZARD = 2;
    public static final byte PRODUCT_SPIRIT = 3;
    public static final byte PRODUCT_TOMAHAWK = 4;
    public static final byte PRODUCT_REMOVEAD = 5;
    
    public static final Font FontBold = Font.getFont(Font.FACE_MONOSPACE, Font.STYLE_BOLD, Font.SIZE_SMALL);
    public static final Font FontPlain = Font.getFont(Font.FACE_MONOSPACE, Font.STYLE_PLAIN, Font.SIZE_SMALL);
    
    private static Main instance;
    public static Main getInstance() { return instance; }
    
    private GameScene scene;
    
    private static Setting setting;
    
    //open accelerometer
    private static SensorConnection iConnection;
    private static boolean openAccelerometer;
    public static boolean isOpenAccelerometer() { return openAccelerometer; }
    public static void setOpenAccelerometer(boolean value) {
        openAccelerometer = value;
        setting.storeSetting(SETTING_ACCELEROMETER, openAccelerometer ? "1" : "0");
    }
    
    //visual effect quality
    private static boolean highVisualEffect;
    public static boolean isHighVisualEffect() { return highVisualEffect; }
    public static void setHightVisualEffect(boolean value) {
        highVisualEffect = value;
        setting.storeSetting(SETTING_VISUAL_EFFECT, highVisualEffect ? "1" : "0");
    }
    
    //backlight level
    private static byte backlight;
    public static byte getBacklight() { return backlight; }
    public static void setBacklight(byte value) {
        backlight = value;
        setting.storeSetting(SETTING_BACKLIGHT, Integer.toString(backlight));
    }
    
    //show ads
    private static boolean showAd;
    public static boolean isShowAd() { return showAd; }
    public void adVisited() {
        showAd = false;
        long currentTime = Calendar.getInstance().getTime().getTime();
        setting.storeSetting(SETTING_ADVISIT_TIME, Long.toString(currentTime));
    }
    
    public static String getDeviceId() { return setting.getSetting(SETTING_DEVICE_ID); }
    
    public void startApp() {
        instance = this;
        
        //Profile.getInstance().storeSetting(Profile.RECORD_COIN, "1000000");
        
        String[] defaultSetting = new String[5];
        defaultSetting[SETTING_VISUAL_EFFECT] = "1";
        defaultSetting[SETTING_ADVISIT_TIME] = "0";
        defaultSetting[SETTING_DEVICE_ID] = RandomHelper.randStringCode(32);
        defaultSetting[SETTING_ACCELEROMETER] = "1";
        defaultSetting[SETTING_BACKLIGHT] = "1";
        setting = new Setting(RESOURCE_SETTING, defaultSetting);
        //visual effect quality
        highVisualEffect = setting.getSetting(SETTING_VISUAL_EFFECT).equals("1");
        //show ads
        long adsTime = Long.parseLong(setting.getSetting(SETTING_ADVISIT_TIME));
        long currentTime = Calendar.getInstance().getTime().getTime();
        showAd = (currentTime - adsTime > 14400000);
        //open accelerometer
        openAccelerometer = setting.getSetting(SETTING_ACCELEROMETER).equals("1");
        if(openAccelerometer && !prepareAccelerometer()) {
            setOpenAccelerometer(false);
        }
        //backlight
        backlight = Byte.parseByte(setting.getSetting(SETTING_BACKLIGHT));
        int backlightLevel = 40+backlight*30;
        DeviceControl.setLights(0, backlightLevel);
        GameScene.setBacklightLevel(backlightLevel);
        
        if(showAd) {
            Hashtable vservConfigTableAd = new Hashtable();
            vservConfigTableAd.put("zoneId", AdManager.VSERV_APPID);
            vservConfigTableAd.put("showAt", "start");
            VservManager vservManager = new VservManager(this, vservConfigTableAd);
        } else {
            scene = new SplashScene();
        }
    }
    
    public void pauseApp() {
    }
    
    public void destroyApp(boolean unconditional) {
        scene.dispose();
        try {
            if(iConnection != null) iConnection.close();
        } catch (IOException ex) {
        }
    }
    
    public boolean prepareAccelerometer() {
        if(iConnection != null) return true;
        
        try {
            SensorInfo[] infos = SensorManager.findSensors("acceleration", null);
            if(infos.length == 0) return false;

            // INT data type is preferred
            int i;
            for(i = 0; i < infos.length; i++) {
                if(infos[i].getChannelInfos()[0].getDataType() == ChannelInfo.TYPE_INT) break;
            }

            iConnection = (i==infos.length) ? (SensorConnection)Connector.open(infos[0].getUrl()):
                        (SensorConnection)Connector.open(infos[i].getUrl());
        } catch (Exception ex) {
            return false;
        }
        return true;
    }
    
    public void gotoSplash() {
        scene.dispose();
        scene = new SplashScene(SplashScene.STATE_SPLASH);
    }
    
    public void gotoShowcase() {
        scene.dispose();
        scene = new ShowcaseScene();
    }
    
    public void gotoPlay() {
        scene.dispose();
        scene = new PlayScene();
        
        if(openAccelerometer) iConnection.setDataListener((PlayScene)scene, BUFFER_SIZE);
    }
    
    public void gotoLeaderboard() {
        scene.dispose();
        scene = new LeaderboardScene();
    }
    
    public void gotoSetting() {
        scene.dispose();
        scene = new SettingScene();
    }
    
    public void gotoHelp() {
        scene.dispose();
        scene = new HelpScene();
    }
    
    public void exitGame() {
        if(showAd) {
            Hashtable vservConfigTableAd = new Hashtable();
            vservConfigTableAd.put("zoneId", AdManager.VSERV_APPID);
            vservConfigTableAd.put("showAt", "end");
            VservManager vservManager = new VservManager(this, vservConfigTableAd);
        } else {
            notifyDestroyed();
        }
    }

    public void constructorMainApp() {
    }

    public void startMainApp() {
        scene = new SplashScene();
    }

    public void resumeMainApp() {
    }
}
