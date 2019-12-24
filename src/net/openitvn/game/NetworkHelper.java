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

import java.io.IOException;
import java.io.InputStream;
import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.midlet.MIDlet;

/**
 *
 * @author Thinh Pham
 */
public class NetworkHelper {
    private final static String hex = 
"%00%01%02%03%04%05%06%07%08%09%0a%0b%0c%0d%0e%0f%10%11%12%13%14%15%16%17%18%19%1a%1b%1c%1d%1e%1f" + 
"%20%21%22%23%24%25%26%27%28%29%2a%2b%2c%2d%2e%2f%30%31%32%33%34%35%36%37%38%39%3a%3b%3c%3d%3e%3f" + 
"%40%41%42%43%44%45%46%47%48%49%4a%4b%4c%4d%4e%4f%50%51%52%53%54%55%56%57%58%59%5a%5b%5c%5d%5e%5f" + 
"%60%61%62%63%64%65%66%67%68%69%6a%6b%6c%6d%6e%6f%70%71%72%73%74%75%76%77%78%79%7a%7b%7c%7d%7e%7f" + 
"%80%81%82%83%84%85%86%87%88%89%8a%8b%8c%8d%8e%8f%90%91%92%93%94%95%96%97%98%99%9a%9b%9c%9d%9e%9f" +
"%a0%a1%a2%a3%a4%a5%a6%a7%a8%a9%aa%ab%ac%ad%ae%af%b0%b1%b2%b3%b4%b5%b6%b7%b8%b9%ba%bb%bc%bd%be%bf" +
"%c0%c1%c2%c3%c4%c5%c6%c7%c8%c9%ca%cb%cc%cd%ce%cf%d0%d1%d2%d3%d4%d5%d6%d7%d8%d9%da%db%dc%dd%de%df" +
"%e0%e1%e2%e3%e4%e5%e6%e7%e8%e9%ea%eb%ec%ed%ee%ef%f0%f1%f2%f3%f4%f5%f6%f7%f8%f9%fa%fb%fc%fd%fe%ff";
    
    private static String hex(int sym) { return(hex.substring(sym*3, sym*3 + 3));}
    
    public static String uriEncode(String s) {
        StringBuffer sbuf = new StringBuffer();
        int len = s.length();
        for (int i = 0; i < len; i++) {
            int ch = s.charAt(i);
            if ('A' <= ch && ch <= 'Z') { // 'A'..'Z'
                sbuf.append((char)ch);
            } else if ('a' <= ch && ch <= 'z') { // 'a'..'z'
                sbuf.append((char)ch);
            } else if ('0' <= ch && ch <= '9') { // '0'..'9'
                sbuf.append((char)ch);
            } else if (ch == ' ') { // space
                sbuf.append('+');
            } else if (ch == '-' || ch == '_'   //these characters don't need encoding
                    || ch == '.' || ch == '*') {
                sbuf.append((char)ch);
            } else if (ch <= 0x007f) { // other ASCII
                sbuf.append(hex(ch));
            } else if (ch <= 0x07FF) { // non-ASCII <= 0x7FF
                sbuf.append(hex(0xc0 | (ch >> 6)));
                sbuf.append(hex(0x80 | (ch & 0x3F)));
            } else { // 0x7FF < ch <= 0xFFFF
                sbuf.append(hex(0xe0 | (ch >> 12)));
                sbuf.append(hex(0x80 | ((ch >> 6) & 0x3F)));
                sbuf.append(hex(0x80 | (ch & 0x3F)));
            }
        }
        return sbuf.toString();
    }
    
    public static StringBuffer getContentViaHttp(String url) throws IOException {
        HttpConnection c;
        InputStream is;
        StringBuffer sb = new StringBuffer();
        c = (HttpConnection)Connector.open(url, Connector.READ_WRITE, true);
        c.setRequestMethod(HttpConnection.GET); //default
        is = c.openInputStream(); // transition to connected!
        int ch;
//        for(int ccnt=0; ccnt < 150; ccnt++) { // get the title.
//            ch = is.read();
//            if (ch == -1) break;
//            sb.append((char)ch);
//        }
        while((ch = is.read()) != -1) {
            sb.append((char)ch);
        }
        is.close();
        c.close();
        return sb;
    }
    
    public static void redirectTo(String targetUrl, MIDlet midlet) throws ConnectionNotFoundException {
        StringBuffer sb = new StringBuffer("http://mrlordkaj.byethost13.com/services/redirect.php?url=");
        sb.append(uriEncode(targetUrl));
        midlet.platformRequest(sb.toString());
    }
}
