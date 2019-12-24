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

import InneractiveSDK.IADView;
import java.util.Vector;
import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.lcdui.Image;

/**
 *
 * @author Thinh Pham
 */
public class AdManager implements Runnable {
    public static final String INNERACTIVE_APPID = "Openitvn_3dmoto_Nokia";
    public static final String VSERV_APPID = "ddbbefff";
    
    private static Image imgAd;
    public static Image getImageAd() { return imgAd; }
    private static String strAd = "";
    
    public void run() {
        Vector adData = IADView.getBannerAdData(Main.getInstance(), INNERACTIVE_APPID);
        try {
            imgAd = (Image)adData.elementAt(0);
            strAd = (String)adData.elementAt(1);
        } catch(Exception ex) {
        }
    }
    
    private static int timeline = 0;
    public static void autoUpdate(int fps) {
        if(timeline > 0) timeline--;
        else {
            new Thread(new AdManager()).start();
            timeline = fps*20;
        }
    }
    
    public static void visitAd() {
        if(!strAd.equals("")) {
            try {
                Main main = Main.getInstance();
                main.platformRequest(strAd);
                main.adVisited();
            } catch (ConnectionNotFoundException ex) { }
        }
    }
}
