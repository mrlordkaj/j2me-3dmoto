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

import com.openitvn.game.ImageHelper;
import com.openitvn.game.NetworkHelper;
import com.openitvn.game.StringHelper;
import java.io.IOException;
import javax.microedition.m3g.Background;
import javax.microedition.m3g.Image2D;

/**
 *
 * @author Thinh Pham
 */
public class Preloader implements Runnable {
    private static final byte TASK_CHANGE_BACKGROUND = 1;
    private static final byte TASK_REGENERATE_TRAFFIC_SEGMENT = 2;
    private static final byte TASK_TEST_CONNECTION = 3;
    
    private byte task;

    private Background background;
    private String backgroundPath;
    public static void changeBackground(Background bg, byte roadType) {
        Preloader loader = new Preloader();
        loader.background = bg;
        loader.backgroundPath = "/backgrounds/bg"+roadType+".png";
        loader.task = TASK_CHANGE_BACKGROUND;
        new Thread(loader).start();
    }
    
    private int vehicleSegmentZ;
    public static void regenerateTrafficSegment(int segmentZ) {
        Preloader loader = new Preloader();
        loader.vehicleSegmentZ = segmentZ;
        loader.task = TASK_REGENERATE_TRAFFIC_SEGMENT;
        new Thread(loader).start();
    }
    
    public static void testConnection() {
        Preloader loader = new Preloader();
        loader.task = TASK_TEST_CONNECTION;
        new Thread(loader).start();
    }
    
    public void run() {
        switch(task) {
            case TASK_CHANGE_BACKGROUND:
                Image2D img = new Image2D(Image2D.RGB, ImageHelper.loadImage(backgroundPath));
                background.setImage(img);
                background.setCrop(0, 0, Main.SCREENSIZE_WIDTH, Main.SCREENSIZE_HEIGHT);
                break;
                
            case TASK_REGENERATE_TRAFFIC_SEGMENT:
                Traffic traffic = PlayScene.getInstance().getTraffic();
                for(byte i = 0; i < traffic.getVehiclePerSegment(); i++) {
                    Vehicle newVehicle = new Vehicle();
                    newVehicle.selectPosition(vehicleSegmentZ, vehicleSegmentZ+traffic.getSegmentLength());
                }
                break;
                
            case TASK_TEST_CONNECTION:
                try {
                    Thread.sleep(1000);
                    StringBuffer result = NetworkHelper.getContentViaHttp("http://m.openitvn.net/leaderboard/v2/test.php");
                    String resultCode = StringHelper.readLine(result);
                    if(resultCode.equals("1")) SplashScene.getInstance().testConnectionDone();
                } catch (InterruptedException ex) {
                } catch (IOException ex) {
                }
                break;
        }
    }

}
