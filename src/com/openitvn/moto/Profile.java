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

import com.openitvn.game.RandomHelper;
import com.openitvn.game.Setting;

/**
 *
 * @author Thinh Pham
 */
public class Profile extends Setting {
    public static final String RECORD_PROFILE = "oivprofile";
    public static final byte RECORD_HIGHSCORE = 0;
    public static final byte RECORD_PLAYER_NAME = 1;
    public static final byte RECORD_COIN = 2;
    public static final byte RECORD_BIKE_BLUE_UNLOCKED = 3;
    public static final byte RECORD_BIKE_BLUE_DURABILITY = 4;
    public static final byte RECORD_BIKE_PINK_UNLOCKED = 5;
    public static final byte RECORD_BIKE_PINK_DURABILITY = 6;
    public static final byte RECORD_BIKE_GREEN_UNLOCKED = 7;
    public static final byte RECORD_BIKE_GREEN_DURABILITY = 8;
    public static final byte RECORD_BIKE_RED_UNLOCKED = 9;
    public static final byte RECORD_BIKE_RED_DURABILITY = 10;
    public static final byte RECORD_CURRENT_BIKE = 11;
    
    private static Profile instance;
    public static Profile getInstance() {
        if(instance == null) {
            //define default values
            String[] defaultRecord = new String[12];
            defaultRecord[RECORD_HIGHSCORE] = "0";
            defaultRecord[RECORD_PLAYER_NAME] = "Player" + RandomHelper.randNumberCode(6);
            defaultRecord[RECORD_COIN] = "0";
            defaultRecord[RECORD_BIKE_BLUE_UNLOCKED] = "1";
            defaultRecord[RECORD_BIKE_BLUE_DURABILITY] = "100";
            defaultRecord[RECORD_BIKE_PINK_UNLOCKED] = "0";
            defaultRecord[RECORD_BIKE_PINK_DURABILITY] = "100";
            defaultRecord[RECORD_BIKE_GREEN_UNLOCKED] = "0";
            defaultRecord[RECORD_BIKE_GREEN_DURABILITY] = "100";
            defaultRecord[RECORD_BIKE_RED_UNLOCKED] = "0";
            defaultRecord[RECORD_BIKE_RED_DURABILITY] = "100";
            defaultRecord[RECORD_CURRENT_BIKE] = Integer.toString(Racer.TYPE_BLUE);
            //call profile
            instance = new Profile(defaultRecord);
        }
        return instance;
    }
    
    private Profile(String[] defaultData) {
        super(RECORD_PROFILE, defaultData);
    }
}
