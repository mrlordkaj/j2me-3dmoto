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

import java.security.DigestException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author Thinh Pham
 */
public class MD5 {
    
    private static final int S11 = 7;
    private static final int S12 = 12;
    private static final int S13 = 17;
    private static final int S14 = 22;
    private static final int S21 = 5;
    private static final int S22 = 9;
    private static final int S23 = 14;
    private static final int S24 = 20;
    private static final int S31 = 4;
    private static final int S32 = 11;
    private static final int S33 = 16;
    private static final int S34 = 23;
    private static final int S41 = 6;
    private static final int S42 = 10;
    private static final int S43 = 15;
    private static final int S44 = 21;

    private static final char[] PADDING = {
        128, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0 };

    private final char bytBuffer[] = new char[64];
    private final int lngState[] = new int[4];
    private int lngByteCount = 0;
    
    private MD5() {
        this.init();
    }
    
    private static int[] decode(char bytBlock[]) {
        int lngBlock[] = new int[16];
        int j = 0;
        for (int i = 0; i < bytBlock.length; i += 4) {
            lngBlock[j++] = bytBlock[i]
                    + bytBlock[i + 1] * 256
                    + bytBlock[i + 2] * 65536
                    + bytBlock[i + 3] * 16777216;
        }
        return (lngBlock);
    }

    private static void transform(int lngState[], char bytBlock[]) {
        int lngA = lngState[0];
        int lngB = lngState[1];
        int lngC = lngState[2];
        int lngD = lngState[3];
        int x[];

        x = decode(bytBlock);

        /* Round 1 */
        lngA = ff(lngA, lngB, lngC, lngD, x[0], S11, 0xd76aa478); /* 1 */
        lngD = ff(lngD, lngA, lngB, lngC, x[1], S12, 0xe8c7b756); /* 2 */
        lngC = ff(lngC, lngD, lngA, lngB, x[2], S13, 0x242070db); /* 3 */
        lngB = ff(lngB, lngC, lngD, lngA, x[3], S14, 0xc1bdceee); /* 4 */
        lngA = ff(lngA, lngB, lngC, lngD, x[4], S11, 0xf57c0faf); /* 5 */
        lngD = ff(lngD, lngA, lngB, lngC, x[5], S12, 0x4787c62a); /* 6 */
        lngC = ff(lngC, lngD, lngA, lngB, x[6], S13, 0xa8304613); /* 7 */
        lngB = ff(lngB, lngC, lngD, lngA, x[7], S14, 0xfd469501); /* 8 */
        lngA = ff(lngA, lngB, lngC, lngD, x[8], S11, 0x698098d8); /* 9 */
        lngD = ff(lngD, lngA, lngB, lngC, x[9], S12, 0x8b44f7af); /* 10 */
        lngC = ff(lngC, lngD, lngA, lngB, x[10], S13, 0xffff5bb1); /* 11 */
        lngB = ff(lngB, lngC, lngD, lngA, x[11], S14, 0x895cd7be); /* 12 */
        lngA = ff(lngA, lngB, lngC, lngD, x[12], S11, 0x6b901122); /* 13 */
        lngD = ff(lngD, lngA, lngB, lngC, x[13], S12, 0xfd987193); /* 14 */
        lngC = ff(lngC, lngD, lngA, lngB, x[14], S13, 0xa679438e); /* 15 */
        lngB = ff(lngB, lngC, lngD, lngA, x[15], S14, 0x49b40821); /* 16 */

        /* Round 2 */
        lngA = gg(lngA, lngB, lngC, lngD, x[1], S21, 0xf61e2562); /* 17 */
        lngD = gg(lngD, lngA, lngB, lngC, x[6], S22, 0xc040b340); /* 18 */
        lngC = gg(lngC, lngD, lngA, lngB, x[11], S23, 0x265e5a51); /* 19 */
        lngB = gg(lngB, lngC, lngD, lngA, x[0], S24, 0xe9b6c7aa); /* 20 */
        lngA = gg(lngA, lngB, lngC, lngD, x[5], S21, 0xd62f105d); /* 21 */
        lngD = gg(lngD, lngA, lngB, lngC, x[10], S22, 0x2441453); /* 22 */
        lngC = gg(lngC, lngD, lngA, lngB, x[15], S23, 0xd8a1e681); /* 23 */
        lngB = gg(lngB, lngC, lngD, lngA, x[4], S24, 0xe7d3fbc8); /* 24 */
        lngA = gg(lngA, lngB, lngC, lngD, x[9], S21, 0x21e1cde6); /* 25 */
        lngD = gg(lngD, lngA, lngB, lngC, x[14], S22, 0xc33707d6); /* 26 */
        lngC = gg(lngC, lngD, lngA, lngB, x[3], S23, 0xf4d50d87); /* 27 */
        lngB = gg(lngB, lngC, lngD, lngA, x[8], S24, 0x455a14ed); /* 28 */
        lngA = gg(lngA, lngB, lngC, lngD, x[13], S21, 0xa9e3e905); /* 29 */
        lngD = gg(lngD, lngA, lngB, lngC, x[2], S22, 0xfcefa3f8); /* 30 */
        lngC = gg(lngC, lngD, lngA, lngB, x[7], S23, 0x676f02d9); /* 31 */
        lngB = gg(lngB, lngC, lngD, lngA, x[12], S24, 0x8d2a4c8a); /* 32 */

        /* Round 3 */
        lngA = hh(lngA, lngB, lngC, lngD, x[5], S31, 0xfffa3942); /* 33 */
        lngD = hh(lngD, lngA, lngB, lngC, x[8], S32, 0x8771f681); /* 34 */
        lngC = hh(lngC, lngD, lngA, lngB, x[11], S33, 0x6d9d6122); /* 35 */
        lngB = hh(lngB, lngC, lngD, lngA, x[14], S34, 0xfde5380c); /* 36 */
        lngA = hh(lngA, lngB, lngC, lngD, x[1], S31, 0xa4beea44); /* 37 */
        lngD = hh(lngD, lngA, lngB, lngC, x[4], S32, 0x4bdecfa9); /* 38 */
        lngC = hh(lngC, lngD, lngA, lngB, x[7], S33, 0xf6bb4b60); /* 39 */
        lngB = hh(lngB, lngC, lngD, lngA, x[10], S34, 0xbebfbc70); /* 40 */
        lngA = hh(lngA, lngB, lngC, lngD, x[13], S31, 0x289b7ec6); /* 41 */
        lngD = hh(lngD, lngA, lngB, lngC, x[0], S32, 0xeaa127fa); /* 42 */
        lngC = hh(lngC, lngD, lngA, lngB, x[3], S33, 0xd4ef3085); /* 43 */
        lngB = hh(lngB, lngC, lngD, lngA, x[6], S34, 0x4881d05); /* 44 */
        lngA = hh(lngA, lngB, lngC, lngD, x[9], S31, 0xd9d4d039); /* 45 */
        lngD = hh(lngD, lngA, lngB, lngC, x[12], S32, 0xe6db99e5); /* 46 */
        lngC = hh(lngC, lngD, lngA, lngB, x[15], S33, 0x1fa27cf8); /* 47 */
        lngB = hh(lngB, lngC, lngD, lngA, x[2], S34, 0xc4ac5665); /* 48 */

        /* Round 4 */
        lngA = ii(lngA, lngB, lngC, lngD, x[0], S41, 0xf4292244); /* 49 */
        lngD = ii(lngD, lngA, lngB, lngC, x[7], S42, 0x432aff97); /* 50 */
        lngC = ii(lngC, lngD, lngA, lngB, x[14], S43, 0xab9423a7); /* 51 */
        lngB = ii(lngB, lngC, lngD, lngA, x[5], S44, 0xfc93a039); /* 52 */
        lngA = ii(lngA, lngB, lngC, lngD, x[12], S41, 0x655b59c3); /* 53 */
        lngD = ii(lngD, lngA, lngB, lngC, x[3], S42, 0x8f0ccc92); /* 54 */
        lngC = ii(lngC, lngD, lngA, lngB, x[10], S43, 0xffeff47d); /* 55 */
        lngB = ii(lngB, lngC, lngD, lngA, x[1], S44, 0x85845dd1); /* 56 */
        lngA = ii(lngA, lngB, lngC, lngD, x[8], S41, 0x6fa87e4f); /* 57 */
        lngD = ii(lngD, lngA, lngB, lngC, x[15], S42, 0xfe2ce6e0); /* 58 */
        lngC = ii(lngC, lngD, lngA, lngB, x[6], S43, 0xa3014314); /* 59 */
        lngB = ii(lngB, lngC, lngD, lngA, x[13], S44, 0x4e0811a1); /* 60 */
        lngA = ii(lngA, lngB, lngC, lngD, x[4], S41, 0xf7537e82); /* 61 */
        lngD = ii(lngD, lngA, lngB, lngC, x[11], S42, 0xbd3af235); /* 62 */
        lngC = ii(lngC, lngD, lngA, lngB, x[2], S43, 0x2ad7d2bb); /* 63 */
        lngB = ii(lngB, lngC, lngD, lngA, x[9], S44, 0xeb86d391); /* 64 */

        lngState[0] = (lngState[0] + lngA) & 0xFFFFFFFF;
        lngState[1] = (lngState[1] + lngB) & 0xFFFFFFFF;
        lngState[2] = (lngState[2] + lngC) & 0xFFFFFFFF;
        lngState[3] = (lngState[3] + lngD) & 0xFFFFFFFF;

        /* clear senstive information */
        //x = decode(pad);
    }

    private static int ff(int lngA,
            int lngB,
            int lngC,
            int lngD,
            int lngX,
            int lngS,
            int lngAC) {
        lngA = (lngA + (lngB & lngC | (~lngB) & lngD) + lngX + lngAC) & 0xFFFFFFFF;
        lngA = ((lngA << lngS) | (lngA >>> (32L - lngS))) & 0xFFFFFFFF;
        lngA = (lngA + lngB) & 0xFFFFFFFF;
        return (lngA);
    }

    private static int gg(int lngA,
            int lngB,
            int lngC,
            int lngD,
            int lngX,
            int lngS,
            int lngAC) {
        lngA = (lngA + (lngB & lngD | lngC & ~lngD) + lngX + lngAC) & 0xFFFFFFFF;
        lngA = ((lngA << lngS) | (lngA >>> (32L - lngS))) & 0xFFFFFFFF;
        lngA = (lngA + lngB) & 0xFFFFFFFF;
        return (lngA);
    }

    private static int hh(int lngA,
            int lngB,
            int lngC,
            int lngD,
            int lngX,
            int lngS,
            int lngAC) {
        lngA = (lngA + (lngB ^ lngC ^ lngD) + lngX + lngAC) & 0xFFFFFFFF;
        lngA = ((lngA << lngS) | (lngA >>> (32L - lngS))) & 0xFFFFFFFF;
        lngA = (lngA + lngB) & 0xFFFFFFFF;
        return (lngA);
    }

    private static int ii(int lngA,
            int lngB,
            int lngC,
            int lngD,
            int lngX,
            int lngS,
            int lngAC) {
        lngA = (lngA + (lngC ^ (lngB | ~lngD)) + lngX + lngAC) & 0xFFFFFFFF;
        lngA = ((lngA << lngS) | (lngA >>> (32L - lngS))) & 0xFFFFFFFF;
        lngA = (lngA + lngB) & 0xFFFFFFFF;
        return (lngA);
    }

    private void update(char bytInput[], int lngLen) {
        int index = (int) (this.lngByteCount % 64);
        int i;
        this.lngByteCount += lngLen;
        int partLen = 64 - index;

        if (lngLen >= partLen) {
            for (int j = 0; j < partLen; ++j) {
                this.bytBuffer[j + index] = bytInput[j];
            }
            transform(this.lngState, this.bytBuffer);

            for (i = partLen; i + 63 < lngLen; i += 64) {
                for (int j = 0; j < 64; ++j) {
                    this.bytBuffer[j] = bytInput[j + i];
                }
                transform(this.lngState, this.bytBuffer);
            }
            index = 0;
        } else {
            i = 0;
        }

        for (int j = 0; j < lngLen - i; ++j) {
            this.bytBuffer[index + j] = bytInput[i + j];
        }

    }

    private void md5Final() {
        char bytBits[] = new char[8];
        int index, padLen;
        long bits = this.lngByteCount * 8;

        bytBits[0] = (char) (bits & 0xffL);
        bytBits[1] = (char) ((bits >>> 8) & 0xff);
        bytBits[2] = (char) ((bits >>> 16) & 0xff);
        bytBits[3] = (char) ((bits >>> 24) & 0xff);
        bytBits[4] = (char) ((bits >>> 32) & 0xff);
        bytBits[5] = (char) ((bits >>> 40) & 0xff);
        bytBits[6] = (char) ((bits >>> 48) & 0xff);
        bytBits[7] = (char) ((bits >>> 56) & 0xff);

        index = (int) this.lngByteCount % 64;
        if (index < 56) {
            padLen = 56 - index;
        } else {
            padLen = 120 - index;
        }
        update(PADDING, padLen);
        update(bytBits, 8);

    }

    private StringBuffer toHexString() {
        StringBuffer sb = new StringBuffer();
        for (int j = 0; j < 4; ++j) {
            for (int i = 0; i < 32; i += 8) {
                int b = (this.lngState[j] >>> i) & 0xFF;
                if (b < 16)
                    sb.append("0");
                sb.append(Integer.toHexString(b));
            }
        }
        return sb;
    }

    private void init() {
        this.lngByteCount = 0;
        this.lngState[0] = 0x67452301;
        this.lngState[1] = 0xefcdab89;
        this.lngState[2] = 0x98badcfe;
        this.lngState[3] = 0x10325476;
    }

    /**
     * MAIN routine with test data set.
     */
    public static String getHash(String str) {
        MD5 md5 = new MD5();
        char chrData[];
        chrData = str.toCharArray();
        md5.update(chrData, chrData.length);
        md5.md5Final();
        return md5.toHexString().toString();
    }
    
    public static String getHashString(String clearText) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] textData = clearText.getBytes();
            md.update(textData, 0, textData.length);
            byte[] hashData = new byte[16];
            int bytesNum = md.digest(hashData, 0, hashData.length);
            StringBuffer hexStringBuffer = new StringBuffer();
            for (int i = 0; i < bytesNum; i++) {
                String hex = Integer.toHexString(0xFF & hashData[i]);
                if (hex.length() == 1)
                    hexStringBuffer.append('0');
                hexStringBuffer.append(hex);
            }
            return hexStringBuffer.toString();
        } catch (NoSuchAlgorithmException ex) {
        } catch (DigestException ex) { }
        return getHash(clearText);
    }
}
