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

/**
 *
 * @author Thinh Pham
 */
public class RacerGreen extends Racer {
    public void initialize() {
        //handle
        handleAngle = 6;
        handleFactor = 0.14f;
        maxSkewAngle = 24;
    }
    
    public int getDurability() {
        return Integer.parseInt(Profile.getInstance().getSetting(Profile.RECORD_BIKE_GREEN_DURABILITY));
    }

    protected void receiveDamage() {
        int durability = getDurability() - ((getSpeed() - 90)/6 + 2);
        if(durability < 1) durability = 1;
        Profile.getInstance().storeSetting(Profile.RECORD_BIKE_GREEN_DURABILITY, Integer.toString(durability));
    }
    
    public void fix() {
        Profile.getInstance().storeSetting(Profile.RECORD_BIKE_GREEN_DURABILITY, "100");
    }
}
