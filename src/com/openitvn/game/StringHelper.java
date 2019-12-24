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
package com.openitvn.game;

import java.util.Vector;

/**
 *
 * @author Thinh Pham
 */
public class StringHelper {
    public static String[] split(String delimiter, String splitStr) {
        StringBuffer token = new StringBuffer();
        Vector tokens = new Vector();
        // split
        char[] chars = splitStr.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (delimiter.indexOf(chars[i]) != -1) {
                // we bumbed into a delimiter
//                if (token.length() > 0) {
                    tokens.addElement(token.toString());
                    token.setLength(0);
//                }
            } else {
                token.append(chars[i]);
            }
        }
        // don't forget the "tail"...
//        if (token.length() > 0) {
            tokens.addElement(token.toString());
//        }
        // convert the vector into an array
        String[] splitArray = new String[tokens.size()];
        for (int i = 0; i < splitArray.length; i++) {
            splitArray[i] = (String) tokens.elementAt(i);
        }
        return splitArray;
    }
    
    public static String formatNumber(int number) {
        StringBuffer buffer = new StringBuffer(Integer.toString(number));
        int offset = buffer.length() - 3;
        while(offset > 0) {
            buffer.insert(offset, ",");
            offset -= 3;
        }
        return buffer.toString();
    }
    
    public static String readLine(StringBuffer sb) {
        StringBuffer rs = new StringBuffer();
        while(sb.length() > 0 && sb.charAt(0) != '\r') {
            rs.append(sb.charAt(0));
            sb.deleteCharAt(0);
        }
        try {
            sb.deleteCharAt(0);
            if(sb.charAt(0) == '\n') sb.deleteCharAt(0);
        } catch (StringIndexOutOfBoundsException ex) {
        }
        return rs.toString();
    }
}
