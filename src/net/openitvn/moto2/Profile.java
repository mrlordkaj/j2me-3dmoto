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

import net.openitvn.game.RandomHelper;
import net.openitvn.game.Setting;

/**
 *
 * @author Thinh Pham
 */
public class Profile extends Setting {
    public static final String RECORD_PROFILE = "oivprofile";
    public static final byte RECORD_HIGHSCORE = 0;
    public static final byte RECORD_PLAYER_NAME = 1;
    public static final byte RECORD_GEM = 2;
    public static final byte RECORD_COIN = 3;
    public static final byte RECORD_BIKE_THUNDER_UNLOCKED = 4;
    public static final byte RECORD_BIKE_THUNDER_UPGRADE = 5;
    public static final byte RECORD_BIKE_THUNDER_DURABILITY = 6;
    public static final byte RECORD_BIKE_THUNDER_COLOR = 7;
    public static final byte RECORD_BIKE_LIZARD_UNLOCKED = 8;
    public static final byte RECORD_BIKE_LIZARD_UPGRADE = 9;
    public static final byte RECORD_BIKE_LIZARD_DURABILITY = 10;
    public static final byte RECORD_BIKE_LIZARD_COLOR = 11;
    public static final byte RECORD_BIKE_SPIRIT_UNLOCKED = 12;
    public static final byte RECORD_BIKE_SPIRIT_UPGRADE = 13;
    public static final byte RECORD_BIKE_SPIRIT_DURABILITY = 14;
    public static final byte RECORD_BIKE_SPIRIT_COLOR = 15;
    public static final byte RECORD_BIKE_TOMAHAWK_UNLOCKED = 16;
    public static final byte RECORD_BIKE_TOMAHAWK_UPGRADE = 17;
    public static final byte RECORD_BIKE_TOMAHAWK_DURABILITY = 18;
    public static final byte RECORD_BIKE_TOMAHAWK_COLOR = 19;
    public static final byte RECORD_POWERUP_FLASH_LEVEL = 20;
    public static final byte RECORD_POWERUP_MAGNET_LEVEL = 21;
    public static final byte RECORD_POWERUP_DOUBLE_LEVEL = 22;
    public static final byte RECORD_CURRENT_BIKE = 23;
    public static final byte RECORD_CURRENT_POWERUP = 24;
    
    private static Profile instance;
    public static Profile getInstance() {
        if(instance == null) {
            //define default values
            String[] defaultRecord = new String[25];
            defaultRecord[RECORD_HIGHSCORE] = "0";
            defaultRecord[RECORD_PLAYER_NAME] = "Player" + RandomHelper.randNumberCode(6);
            defaultRecord[RECORD_GEM] = "10";
            defaultRecord[RECORD_COIN] = "0";
            defaultRecord[RECORD_BIKE_THUNDER_UNLOCKED] = "1";
            defaultRecord[RECORD_BIKE_THUNDER_UPGRADE] = "111";
            defaultRecord[RECORD_BIKE_THUNDER_DURABILITY] = "100";
            defaultRecord[RECORD_BIKE_THUNDER_COLOR] = Integer.toString(Racer.COLOR_BLUE);
            defaultRecord[RECORD_BIKE_LIZARD_UNLOCKED] = "0";
            defaultRecord[RECORD_BIKE_LIZARD_UPGRADE] = "111";
            defaultRecord[RECORD_BIKE_LIZARD_DURABILITY] = "100";
            defaultRecord[RECORD_BIKE_LIZARD_COLOR] = Integer.toString(Racer.COLOR_BLUE);
            defaultRecord[RECORD_BIKE_SPIRIT_UNLOCKED] = "0";
            defaultRecord[RECORD_BIKE_SPIRIT_UPGRADE] = "111";
            defaultRecord[RECORD_BIKE_SPIRIT_DURABILITY] = "100";
            defaultRecord[RECORD_BIKE_SPIRIT_COLOR] = Integer.toString(Racer.COLOR_BLUE);
            defaultRecord[RECORD_BIKE_TOMAHAWK_UNLOCKED] = "0";
            defaultRecord[RECORD_BIKE_TOMAHAWK_UPGRADE] = "111";
            defaultRecord[RECORD_BIKE_TOMAHAWK_DURABILITY] = "100";
            defaultRecord[RECORD_BIKE_TOMAHAWK_COLOR] = Integer.toString(Racer.COLOR_BLUE);
            defaultRecord[RECORD_POWERUP_FLASH_LEVEL] = "1";
            defaultRecord[RECORD_POWERUP_MAGNET_LEVEL] = "1";
            defaultRecord[RECORD_POWERUP_DOUBLE_LEVEL] = "1";
            defaultRecord[RECORD_CURRENT_BIKE] = Integer.toString(Racer.TYPE_THUNDER);
            defaultRecord[RECORD_CURRENT_POWERUP] = Integer.toString(Power.TYPE_FLASH);
            //call profile
            instance = new Profile(defaultRecord);
        }
        return instance;
    }
    
    private Profile(String[] defaultData) {
        super(RECORD_PROFILE, defaultData);
    }
}
