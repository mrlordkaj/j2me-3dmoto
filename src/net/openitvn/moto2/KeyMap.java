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

package net.openitvn.moto2;

/**
 *
 * @author Thinh Pham
 */
abstract class KeyMap {
//#if QWERTY
//#     static final int KEY_0 = 32;
//#     static final int KEY_1 = 114;
//#     static final int KEY_2 = 116;
//#     static final int KEY_3 = 121;
//#     static final int KEY_4 = 102;
//#     static final int KEY_5 = 103;
//#     static final int KEY_6 = 104;
//#     static final int KEY_8 = 98;
//#     static final int KEY_LF = 113;
//#     static final int KEY_CF = 119;
//#     static final int KEY_RF = 101;
//#     static final int KEY_SHARP = 106;
//#     static final int KEY_UP = 1000;
//#     static final int KEY_RIGHT = 1001;
//#     static final int KEY_DOWN = 1002;
//#     static final int KEY_LEFT = 1003;
//#elif TKEY
    static final int KEY_0 = 48;
    static final int KEY_1 = 49;
    static final int KEY_2 = 50;
    static final int KEY_3 = 51;
    static final int KEY_4 = 52;
    static final int KEY_5 = 53;
    static final int KEY_6 = 54;
    static final int KEY_7 = 55;
    static final int KEY_8 = 56;
    static final int KEY_9 = 57;
    static final int KEY_LF = -6;
    static final int KEY_CF = -5;
    static final int KEY_RF = -7;
    static final int KEY_SHARP = 35;
    static final int KEY_UP = -1;
    static final int KEY_RIGHT = -4;
    static final int KEY_DOWN = -2;
    static final int KEY_LEFT = -3;
//#endif
}
