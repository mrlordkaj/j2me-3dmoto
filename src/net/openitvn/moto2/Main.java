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

//#if INAPPPURCHASE != ""
//# import com.nokia.payment.NPayException;
//# import com.nokia.payment.NPayListener;
//# import com.nokia.payment.NPayManager;
//# import com.nokia.payment.ProductData;
//# import com.nokia.payment.PurchaseData;
//#endif
import net.openitvn.game.GameScene;
import net.openitvn.game.RandomHelper;
import net.openitvn.game.Setting;
import java.util.Calendar;
import java.util.Hashtable;
import javax.microedition.lcdui.Font;
import javax.microedition.midlet.*;
import vAdEngine.VservInterface;
import vAdEngine.VservManager;

/**
 * @author Thinh Pham
 */
public class Main extends MIDlet implements VservInterface
//#if INAPPPURCHASE != ""
//# , NPayListener
//#endif
{
//#if ScreenWidth == 240 && ScreenHeight == 400
//#     public static final int SCREENSIZE_WIDTH = 240;
//#     public static final int SCREENSIZE_HEIGHT = 400;
//#elif ScreenWidth == 240 && ScreenHeight == 320
    public static final int SCREENSIZE_WIDTH = 240;
    public static final int SCREENSIZE_HEIGHT = 320;
//#endif
    
    private static final String RESOURCE_SETTING = "oivsetting";
    public static final byte SETTING_VISUAL_EFFECT = 0;
    public static final byte SETTING_AD_REMOVED = 1;
    public static final byte SETTING_DEVICE_ID = 2;
    public static final byte SETTING_ADVISIT_TIME = 3;
    
    public static final byte PRODUCT_COIN = 0;
    public static final byte PRODUCT_GEM = 1;
    public static final byte PRODUCT_LIZARD = 2;
    public static final byte PRODUCT_SPIRIT = 3;
    public static final byte PRODUCT_TOMAHAWK = 4;
    public static final byte PRODUCT_REMOVEAD = 5;
    
    public static final String SOUND_MENU = "/sounds/menu.mid";
    public static final Font FontBold = Font.getFont(Font.FACE_MONOSPACE, Font.STYLE_BOLD, Font.SIZE_SMALL);
    public static final Font FontPlain = Font.getFont(Font.FACE_MONOSPACE, Font.STYLE_PLAIN, Font.SIZE_SMALL);
    
    private static Main instance;
    public static Main getInstance() { return instance; }
    
    private GameScene scene;
    
    private static Setting setting;
    
    private static boolean highVisualEffect;
    public static boolean isHighVisualEffect() { return highVisualEffect; }
    public static void setHightVisualEffect(boolean value) {
        highVisualEffect = value;
        setting.storeSetting(SETTING_VISUAL_EFFECT, highVisualEffect ? "1" : "0");
    }
    
    private static boolean showAd;
    public static boolean isShowAd() { return showAd; }
    public static void setShowAd(boolean value) {
        showAd = value;
        setting.storeSetting(SETTING_AD_REMOVED, showAd ? "0" : "1");
    }
    public void adVisited() {
        showAd = false;
        long currentTime = Calendar.getInstance().getTime().getTime();
        setting.storeSetting(SETTING_ADVISIT_TIME, Long.toString(currentTime));
    }
    
    public static String getDeviceId() { return setting.getSetting(SETTING_DEVICE_ID); }
    
    public void startApp() {
        instance = this;
        
//        Profile.getInstance().storeSetting(Profile.RECORD_COIN, "1000000");
//        Profile.getInstance().storeSetting(Profile.RECORD_GEM, "1000000");
        
        String[] defaultSetting = new String[4];
        defaultSetting[SETTING_VISUAL_EFFECT] = "0";
        defaultSetting[SETTING_AD_REMOVED] = "0";
        defaultSetting[SETTING_DEVICE_ID] = RandomHelper.randStringCode(32);
        defaultSetting[SETTING_ADVISIT_TIME] = "0";
        setting = new Setting(RESOURCE_SETTING, defaultSetting);
        highVisualEffect = setting.getSetting(SETTING_VISUAL_EFFECT).equals("1");
        //show ads
        showAd = !setting.getSetting(SETTING_AD_REMOVED).equals("1");
        if(showAd) {
            long visitTime = Long.parseLong(setting.getSetting(SETTING_ADVISIT_TIME));
            long currentTime = Calendar.getInstance().getTime().getTime();
            showAd = (currentTime - visitTime > 14400000);
        }
        
        if(showAd) {
            Hashtable vservConfigTableAd = new Hashtable();
            vservConfigTableAd.put("zoneId", AdManager.VSERV_APPID);
            vservConfigTableAd.put("showAt", "start");
            VservManager vservManager = new VservManager(this, vservConfigTableAd);
        } else startMainApp();
    }
    
    public void pauseApp() {
    }
    
    public void destroyApp(boolean unconditional) {
        scene.dispose();
    }
    
    public void gotoSplash() {
        scene.dispose();
        scene = new SplashScene(SplashScene.STATE_SPLASH);
    }
    
    public void gotoShowcase() {
        scene.dispose();
        scene = new ShowcaseScene();
    }
    
    public void gotoGarage() {
        scene.dispose();
        scene = new GarageScene();
    }
    
    public void gotoPlay() {
        scene.dispose();
        scene = new PlayScene();
    }
    
    public void gotoLeaderboard() {
        scene.dispose();
        scene = new LeaderboardScene();
    }
    
    public void gotoAchievement() {
        scene.dispose();
        scene = new AchievementScene();
    }
    
    public void gotoSetting() {
        scene.dispose();
        scene = new SettingScene();
    }
    
    public void gotoHelp() {
        scene.dispose();
        scene = new HelpScene();
    }
    
    public void gotoInstruction() {
        scene.dispose();
        scene = new InstructionScene();
    }
    
    public void exitGame() {
        if (showAd) {
            Hashtable vservConfigTableAd = new Hashtable();
            vservConfigTableAd.put("zoneId", AdManager.VSERV_APPID);
            vservConfigTableAd.put("showAt", "end");
            VservManager vservManager = new VservManager(this, vservConfigTableAd);
        } else notifyDestroyed();
    }
    
//#if INAPPPURCHASE != ""
//#     private NPayManager paymentManager;
//#     public NPayManager getPaymentManager() { return paymentManager; }
//#     private final String[] productIds = new String[6];
//#     
//#if Nokia_240_320_Touch
//#     private IPaymentCanvas callback;
//#     private byte purchaseType;
//#endif
//#     
//#     private void preparePaymentService() {
//#         try {
//#             paymentManager = new NPayManager(this);
//#             paymentManager.setNPayListener(this);
//# 
//#             productIds[PRODUCT_COIN] = "1322411";
//#             productIds[PRODUCT_GEM] = "1322412";
//#             productIds[PRODUCT_LIZARD] = "1322413";
//#             productIds[PRODUCT_SPIRIT] = "1322414";
//#             productIds[PRODUCT_TOMAHAWK] = "1322415";
//#             //productIds[PRODUCT_TOMAHAWK] = "success-1";
//#             productIds[PRODUCT_REMOVEAD] = "1322416";
//# 
//#             paymentManager.getProductData(productIds);
//#         } catch (NPayException e) {
//#         }
//#     }
//#     
//#     public void startPurchase(byte purchaseType, IPaymentCanvas callback) {
//#         try {
//#             paymentManager.purchaseProduct(productIds[purchaseType]);
//#if Nokia_240_320_Touch
//#             this.purchaseType = purchaseType;
//#             this.callback = callback;
//#endif
//#         } catch (NPayException ex) {
//#         }
//#     }
//# 
//#     public void purchaseCompleted(PurchaseData data) {
//#         if (data.getStatus() == PurchaseData.PURCHASE_SUCCESS
//#             || data.getStatus() == PurchaseData.PURCHASE_RESTORE_SUCCESS) {
//#if Nokia_240_320_Touch
//#             callback.paymentSuccess(purchaseType);
//#else
//#             String productId = data.getProductId();
//#             for(byte i = 0; i < productIds.length; i++) {
//#                 if(productId.equals(productIds[i])) {
//#                     Profile profile = Profile.getInstance();
//#                     Achievement achievement = Achievement.getInstance();
//#                     switch(i) {
//#                         case PRODUCT_COIN:
//#                             int coin = Integer.parseInt(profile.getSetting(Profile.RECORD_COIN));
//#                             coin += 10000;
//#                             profile.storeSetting(Profile.RECORD_COIN, Integer.toString(coin));
//#                             break;
//#                             
//#                         case PRODUCT_GEM:
//#                             int gem = Integer.parseInt(profile.getSetting(Profile.RECORD_GEM));
//#                             gem += 10000;
//#                             profile.storeSetting(Profile.RECORD_GEM, Integer.toString(gem));
//#                             break;
//#                             
//#                         case PRODUCT_LIZARD:
//#                             profile.storeSetting(Profile.RECORD_BIKE_LIZARD_UNLOCKED, "1");
//#                             achievement.triggerUnlockDesertLizard();
//#                             break;
//#                             
//#                         case PRODUCT_SPIRIT:
//#                             profile.storeSetting(Profile.RECORD_BIKE_SPIRIT_UNLOCKED, "1");
//#                             achievement.triggerUnlockSpiritRaider();
//#                             break;
//#                             
//#                         case PRODUCT_TOMAHAWK:
//#                             profile.storeSetting(Profile.RECORD_BIKE_TOMAHAWK_UNLOCKED, "1");
//#                             achievement.triggerUnlockDemonTomahawk();
//#                             break;
//#                             
//#                         case PRODUCT_REMOVEAD:
//#                             setting.storeSetting(SETTING_AD_REMOVED, "1");
//#                             break;
//#                     }
//#                     break;
//#                 }
//#             }
//#endif
//#         } else if (data.getStatus() == PurchaseData.PURCHASE_FAILED) {
//#if Nokia_240_320_Touch
//#             callback.paymentFail();
//#else
//#             
//#endif
//#         }
//#if Nokia_240_320_Touch
//#             callback = null;
//#endif
//#     }
//# 
//#     public void productDataReceived(ProductData[] dataItems) {
//#     }
//#endif

    public void constructorMainApp() {
    }

    public void startMainApp() {
        scene = new SplashScene();
//#if INAPPPURCHASE != ""
//#             preparePaymentService();
//#endif
    }

    public void resumeMainApp() {
    }
}
