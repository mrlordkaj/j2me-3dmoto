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

import com.openitvn.game.math.MD5;
import java.io.IOException;

/**
 *
 * @author Thinh Pham
 */
public class Leaderboard implements Runnable {
    private static final int GAME_ID = 4;
    private static final String URI_PREFIX = "http://m.openitvn.net/leaderboard/v2/";
    private static final byte COMMAND_SUBMIT_SCORE = 1;
    private static final byte COMMAND_GET_RANK = 2;
    private static final byte COMMAND_VIEW_ALL = 3;
    
    private static int score;
    private static String username;
    private static String deviceId;
    private final ILeaderboardCaller caller;
    private final byte execCommand;
    
    private Leaderboard(byte command, ILeaderboardCaller callback) {
        execCommand = command;
        caller = callback;
    }
    
    public static void submitScore(int score, String username, String deviceId, ILeaderboardCaller caller)
    {
        Leaderboard.score = score;
        Leaderboard.username = username;
        Leaderboard.deviceId = deviceId;
        new Thread(new Leaderboard(COMMAND_SUBMIT_SCORE, caller)).start();
    }
    
    public static void getRank(int score, String deviceId, ILeaderboardCaller caller)
    {
        Leaderboard.score = score;
        Leaderboard.deviceId = deviceId;
        new Thread(new Leaderboard(COMMAND_GET_RANK, caller)).start();
    }
    
    public static void viewAll(ILeaderboardCaller caller) {
        new Thread(new Leaderboard(COMMAND_VIEW_ALL, caller)).start();
    }

    public void run() {
        switch(execCommand) {
            case COMMAND_SUBMIT_SCORE:
                try {
                    Thread.sleep(3000);
//#if SATSACRYPTO
                    String hash = MD5.getHashString(GAME_ID+"."+deviceId+"."+score);
//#else
//#                     String hash = MD5.getHash(GAME_ID+"."+deviceId+"."+score);
//#endif
                    String uri = URI_PREFIX+"?act=submit&gameid="+GAME_ID+"&deviceid="+deviceId+"&score="+score+"&hash="+hash+"&name="+NetworkHelper.uriEncode(username);
                    StringBuffer result = NetworkHelper.getContentViaHttp(uri);
                    String resultCode = StringHelper.readLine(result);
                    if(resultCode.equals("1")) caller.onSubmitSuccess();
                    else caller.onSubmitFail();
                } catch (IOException ex) {
                    caller.onSubmitFail();
                } catch (InterruptedException ex) {
                    caller.onSubmitFail();
                }
                break;
                
            case COMMAND_GET_RANK:
                try {
                    if (score == 0)
                    {
                        caller.onGetRankSuccess(-1);
                        return;
                    }
                    Thread.sleep(2500);
//#if SATSACRYPTO
                    String hash = MD5.getHashString(GAME_ID+"."+deviceId+"."+score);
//#else
//#                     String hash = MD5.getHash(GAME_ID+"."+deviceId+"."+score);
//#endif
                    String uri = URI_PREFIX+"?act=myrank&gameid="+GAME_ID+"&deviceid="+deviceId+"&score="+score+"&hash="+hash;
                    StringBuffer result = NetworkHelper.getContentViaHttp(uri);
                    String resultCode = StringHelper.readLine(result);
                    if(resultCode.equals("1")) caller.onGetRankSuccess(Integer.parseInt(StringHelper.readLine(result)));
                    else caller.onGetRankFail();
                } catch (IOException ex) {
                    caller.onGetRankFail();
                } catch (InterruptedException ex) {
                    caller.onGetRankFail();
                }
                break;
                
            case COMMAND_VIEW_ALL:
                try {
                    Thread.sleep(2000);
                    String uri = URI_PREFIX+"?act=viewall&gameid="+GAME_ID;
                    StringBuffer result = NetworkHelper.getContentViaHttp(uri);
                    String resultCode = StringHelper.readLine(result);
                    if(resultCode.equals("1")) caller.onViewAllSuccess(result);
                    else caller.onViewAllFail();
                } catch (IOException ex) {
                    caller.onViewAllFail();
                } catch (InterruptedException ex) {
                    caller.onViewAllFail();
                }
                break;
        }
    }
}
