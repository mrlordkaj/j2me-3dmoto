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
package net.openitvn.game;

import java.util.Random;

/**
 *
 * @author Thinh Pham
 */
public class RandomHelper {
    public static Random rand = new Random();
    
    public static byte randByte(int min, int max) {
        if(max > 127) max = 127;
        if(min < 0) min = 0;
        return (byte)randInt(min, max);
    }
    
    public static short randShort(int min, int max) {
        if(max > 32767) max = 32767;
        if(min < -32768) min = -32768;
        return (short)randInt(min, max);
    }
    
    public static int randInt(int min, int max) {
        return rand.nextInt(max-min+1) + min;
    }
    
    public static float randFloat(float min, float max) {
        return rand.nextFloat()*(max-min) + min;
    }
    
    public static double randDouble(double min, double max) {
        return rand.nextDouble()*(max-min) + min;
    }
    
    public static boolean randChance(int percent) {
        return (randByte(0, 100) < percent);
    }
    
    public static byte randChance(int[] percent, int max) {
        int chance = randInt(0, max);
        int curPercent = 0;
        for(byte i = 0; i < percent.length; i++) {
            curPercent += percent[i];
            if(curPercent > chance) return i;
        }
        
        return -1;
    }
    
    public static String randNumberCode(int length) {
        char[] chars = "0123456789".toCharArray();
        StringBuffer sb = new StringBuffer(length);
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        return sb.toString();
    }
    
    public static String randStringCode(int length) {
        char[] chars = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        StringBuffer sb = new StringBuffer(length);
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        return sb.toString();
    }
}
