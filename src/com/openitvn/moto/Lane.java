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
public class Lane {
    public static final short LANE_WIDTH = 28;
    public static final short LANE_EMPTY = -2;
    public static final short LANE_RIGHT = -1;
    public static final short LANE_CENTER = 0;
    public static final short LANE_LEFT = 1;
    
    private final short lane;
    public short getLane() { return lane; }

    public Lane(short lane) {
        this.lane = lane;
    }
}
