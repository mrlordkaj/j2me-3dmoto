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

import net.openitvn.game.Setting;

/**
 *
 * @author Thinh Pham
 */
public class Achievement extends Setting {
    public static final String RECORD_ACHIEVEMENT = "oivachievement";
    public static final int TOTAL_ACHIEVEMENT = 30;
    public static final byte TYPE_NOOP_RACER = 0;
    public static final byte TYPE_ADVANCED_RACER = 1;
    public static final byte TYPE_SUPER_RACER = 2;
    public static final byte TYPE_MASTER_RACER = 3;
    public static final byte TYPE_ULTIMATE_RACER = 4;
    public static final byte TYPE_THE_POOR = 5;
    public static final byte TYPE_THE_RICH = 6;
    public static final byte TYPE_COIN_COLLECTOR = 7;
    public static final byte TYPE_MONEY_LOVER = 8;
    public static final byte TYPE_LUCKY_GUY = 9;
    public static final byte TYPE_TOO_MUCH_GEMS = 10;
    public static final byte TYPE_GEMS_LOVE_ME = 11;
    public static final byte TYPE_IAM_RACER = 12;
    public static final byte TYPE_ROAD_LOVER = 13;
    public static final byte TYPE_NEVER_GIVE_UP = 14;
    public static final byte TYPE_ROAD_IS_MY_LIFE = 15;
    public static final byte TYPE_DESERT_LIZARD = 16;
    public static final byte TYPE_SPIRIT_RAIDER = 17;
    public static final byte TYPE_DEMON_TOMAHAWK = 18;
    public static final byte TYPE_RUNNING_IN_FEAR = 19;
    public static final byte TYPE_LIKE_A_BIRD = 20;
    public static final byte TYPE_IAM_GHOST = 21;
    public static final byte TYPE_THE_DESTROYER = 22;
    public static final byte TYPE_EVERYONE_MUST_DIE = 23;
    public static final byte TYPE_JESUS_BEHIND_ME = 24;
    public static final byte TYPE_ULTIMATE_THUNDER = 25;
    public static final byte TYPE_ULTIMATE_LIZARD = 26;
    public static final byte TYPE_ULTIMATE_SPIRIT = 27;
    public static final byte TYPE_ULTIMATE_TOMAHAWK = 28;
    public static final byte TYPE_POWERFUL_RACER = 29;
    
    public static final byte RECORD_PLAY_TIMES = 30;
    
    public static final int CONDITION_NOOP_RACER = 1000;
    public static final int CONDITION_ADVANCED_RACER = 5000;
    public static final int CONDITION_SUPER_RACER = 12000;
    public static final int CONDITION_MASTER_RACER = 22000;
    public static final int CONDITION_ULTIMATE_RACER = 1500000;
    public static final int CONDITION_THE_POOR = 100;
    public static final int CONDITION_THE_RICH = 200;
    public static final int CONDITION_COIN_COLLECTOR = 500;
    public static final int CONDITION_MONEY_LOVER = 1000;
    public static final int CONDITION_LUCKY_GUY = 2;
    public static final int CONDITION_TOO_MUCH_GEMS = 3;
    public static final int CONDITION_GEMS_LOVE_ME = 4;
    public static final int CONDITION_IAM_RACER = 10;
    public static final int CONDITION_ROAD_LOVER = 200;
    public static final int CONDITION_NEVER_GIVE_UP = 1000;
    public static final int CONDITION_ROAD_IS_MY_LIFE = 5000;
    public static final int CONDITION_RUNNING_IN_FEAR = 50;
    public static final int CONDITION_LIKE_A_BIRD = 50;
    public static final int CONDITION_IAM_GHOST = 50;
    public static final int CONDITION_THE_DESTROYER = 200;
    public static final int CONDITION_JESUS_BEHIND_ME = 10;
    
    private static Achievement instance;
    public static Achievement getInstance() {
        if (instance == null) {
            // define default values
            String[] defaultRecord = new String[31];
            for (byte i = 0; i < defaultRecord.length; i++) {
                defaultRecord[i] = "0";
            }
            defaultRecord[TYPE_ULTIMATE_RACER] = Integer.toString(CONDITION_ULTIMATE_RACER);
            defaultRecord[TYPE_RUNNING_IN_FEAR] = Integer.toString(CONDITION_RUNNING_IN_FEAR);
            defaultRecord[TYPE_LIKE_A_BIRD] = Integer.toString(CONDITION_LIKE_A_BIRD);
            defaultRecord[TYPE_IAM_GHOST] = Integer.toString(CONDITION_IAM_GHOST);
            defaultRecord[TYPE_THE_DESTROYER] = Integer.toString(CONDITION_THE_DESTROYER);
            defaultRecord[TYPE_JESUS_BEHIND_ME] = Integer.toString(CONDITION_JESUS_BEHIND_ME);
            // call profile
            instance = new Achievement(defaultRecord);
        }
        return instance;
    }
    
    private final boolean[] check = new boolean[TOTAL_ACHIEVEMENT];
    public boolean unlocked(byte type) { return !check[type]; }
    
    private Achievement(String[] defaultRecord) {
        super(RECORD_ACHIEVEMENT, defaultRecord);
        
        check[TYPE_NOOP_RACER] = getSetting(TYPE_NOOP_RACER).equals("0");
        check[TYPE_ADVANCED_RACER] = getSetting(TYPE_ADVANCED_RACER).equals("0");
        check[TYPE_SUPER_RACER] = getSetting(TYPE_SUPER_RACER).equals("0");
        check[TYPE_MASTER_RACER] = getSetting(TYPE_MASTER_RACER).equals("0");
        needForUltimateRacer = Integer.parseInt(getSetting(TYPE_ULTIMATE_RACER));
        check[TYPE_ULTIMATE_RACER] = needForUltimateRacer > 0;
        check[TYPE_THE_POOR] = getSetting(TYPE_THE_POOR).equals("0");
        check[TYPE_THE_RICH] = getSetting(TYPE_THE_RICH).equals("0");
        check[TYPE_COIN_COLLECTOR] = getSetting(TYPE_COIN_COLLECTOR).equals("0");
        check[TYPE_MONEY_LOVER] = getSetting(TYPE_MONEY_LOVER).equals("0");
        check[TYPE_LUCKY_GUY] = getSetting(TYPE_LUCKY_GUY).equals("0");
        check[TYPE_TOO_MUCH_GEMS] = getSetting(TYPE_TOO_MUCH_GEMS).equals("0");
        check[TYPE_GEMS_LOVE_ME] = getSetting(TYPE_GEMS_LOVE_ME).equals("0");
        check[TYPE_IAM_RACER] = getSetting(TYPE_IAM_RACER).equals("0");
        check[TYPE_ROAD_LOVER] = getSetting(TYPE_ROAD_LOVER).equals("0");
        check[TYPE_NEVER_GIVE_UP] = getSetting(TYPE_NEVER_GIVE_UP).equals("0");
        check[TYPE_ROAD_IS_MY_LIFE] = getSetting(TYPE_ROAD_IS_MY_LIFE).equals("0");
        check[TYPE_DESERT_LIZARD] = getSetting(TYPE_DESERT_LIZARD).equals("0");
        check[TYPE_SPIRIT_RAIDER] = getSetting(TYPE_SPIRIT_RAIDER).equals("0");
        check[TYPE_DEMON_TOMAHAWK] = getSetting(TYPE_DEMON_TOMAHAWK).equals("0");
        needForRunningInFear = Integer.parseInt(getSetting(TYPE_RUNNING_IN_FEAR));
        check[TYPE_RUNNING_IN_FEAR] = needForRunningInFear > 0;
        needForLikeABird = Integer.parseInt(getSetting(TYPE_LIKE_A_BIRD));
        check[TYPE_LIKE_A_BIRD] = needForLikeABird > 0;
        needForIamGhost = Integer.parseInt(getSetting(TYPE_IAM_GHOST));
        check[TYPE_IAM_GHOST] = needForIamGhost > 0;
        needForTheDestroyer = Integer.parseInt(getSetting(TYPE_THE_DESTROYER));
        check[TYPE_THE_DESTROYER] = needForTheDestroyer > 0;
        check[TYPE_EVERYONE_MUST_DIE] = getSetting(TYPE_EVERYONE_MUST_DIE).equals("0");
        needForJesusBehindMe = Integer.parseInt(getSetting(TYPE_JESUS_BEHIND_ME));
        check[TYPE_JESUS_BEHIND_ME] = needForJesusBehindMe > 0;
        check[TYPE_ULTIMATE_THUNDER] = getSetting(TYPE_ULTIMATE_THUNDER).equals("0");
        check[TYPE_ULTIMATE_LIZARD] = getSetting(TYPE_ULTIMATE_LIZARD).equals("0");
        check[TYPE_ULTIMATE_SPIRIT] = getSetting(TYPE_ULTIMATE_SPIRIT).equals("0");
        check[TYPE_ULTIMATE_TOMAHAWK] = getSetting(TYPE_ULTIMATE_TOMAHAWK).equals("0");
        check[TYPE_POWERFUL_RACER] = getSetting(TYPE_POWERFUL_RACER).equals("0");
    }
    
    private int needForUltimateRacer;
    public void triggerDistanceAchievement(int distance) {
        if (check[TYPE_NOOP_RACER] && distance >= CONDITION_NOOP_RACER) {
            MessageQueue.addAchievement("noop|racer");
            storeSetting(TYPE_NOOP_RACER, "1");
            check[TYPE_NOOP_RACER] = false;
        }
        else if (check[TYPE_ADVANCED_RACER] && distance >= CONDITION_ADVANCED_RACER) {
            MessageQueue.addAchievement("advanced|racer");
            storeSetting(TYPE_ADVANCED_RACER, "1");
            check[TYPE_ADVANCED_RACER] = false;
        }
        else if (check[TYPE_SUPER_RACER] && distance >= CONDITION_SUPER_RACER) {
            MessageQueue.addAchievement("super|racer");
            storeSetting(TYPE_SUPER_RACER, "1");
            check[TYPE_SUPER_RACER] = false;
        }
        else if (check[TYPE_MASTER_RACER] && distance >= CONDITION_MASTER_RACER) {
            MessageQueue.addAchievement("master|racer");
            storeSetting(TYPE_MASTER_RACER, "1");
            check[TYPE_MASTER_RACER] = false;
        }
        else if (check[TYPE_ULTIMATE_RACER] && distance >= needForUltimateRacer) {
            MessageQueue.addAchievement("ultimate|racer");
            storeSetting(TYPE_ULTIMATE_RACER, "0");
            check[TYPE_ULTIMATE_RACER] = false;
        }
    }
    public void updateNeedForUltimateRacer(int distance) {
        if (check[TYPE_ULTIMATE_RACER]) {
            needForUltimateRacer -= distance;
            storeSetting(TYPE_ULTIMATE_RACER, Integer.toString(needForUltimateRacer));
        }
    }
    
    public void triggerCoinAchievement(int coin) {
        if (check[TYPE_THE_POOR] && coin >= CONDITION_THE_POOR) {
            MessageQueue.addAchievement("the poor");
            storeSetting(TYPE_THE_POOR, "1");
            check[TYPE_THE_POOR] = false;
        }
        else if (check[TYPE_THE_RICH] && coin >= CONDITION_THE_RICH) {
            MessageQueue.addAchievement("the rich");
            storeSetting(TYPE_THE_RICH, "1");
            check[TYPE_THE_RICH] = false;
        }
        else if (check[TYPE_COIN_COLLECTOR] && coin >= CONDITION_COIN_COLLECTOR) {
            MessageQueue.addAchievement("coin|collector");
            storeSetting(TYPE_COIN_COLLECTOR, "1");
            check[TYPE_COIN_COLLECTOR] = false;
        }
        else if (check[TYPE_MONEY_LOVER] && coin >= CONDITION_MONEY_LOVER) {
            MessageQueue.addAchievement("money|lover");
            storeSetting(TYPE_MONEY_LOVER, "1");
            check[TYPE_MONEY_LOVER] = false;
        }
    }
    
    public void triggerGemAchievement(int gem) {
        if (check[TYPE_LUCKY_GUY] && gem >= CONDITION_LUCKY_GUY) {
            MessageQueue.addAchievement("lucky guy");
            storeSetting(TYPE_LUCKY_GUY, "1");
            check[TYPE_LUCKY_GUY] = false;
        }
        else if (check[TYPE_TOO_MUCH_GEMS] && gem >= CONDITION_TOO_MUCH_GEMS) {
            MessageQueue.addAchievement("too much|gems");
            storeSetting(TYPE_TOO_MUCH_GEMS, "1");
            check[TYPE_TOO_MUCH_GEMS] = false;
        }
        else if (check[TYPE_GEMS_LOVE_ME] && gem >= CONDITION_GEMS_LOVE_ME) {
            MessageQueue.addAchievement("gems|love me");
            storeSetting(TYPE_GEMS_LOVE_ME, "1");
            check[TYPE_GEMS_LOVE_ME] = false;
        }
    }
    
    public void triggerBeginAchievement() {
        int playTimes = Integer.parseInt(getSetting(RECORD_PLAY_TIMES)) + 1;
        storeSetting(RECORD_PLAY_TIMES, Integer.toString(playTimes));
        if (check[TYPE_IAM_RACER] && playTimes >= CONDITION_IAM_RACER) {
            MessageQueue.addAchievement("i'm racer");
            storeSetting(TYPE_IAM_RACER, "1");
            check[TYPE_IAM_RACER] = false;
        }
        else if (check[TYPE_ROAD_LOVER] && playTimes >= CONDITION_ROAD_LOVER) {
            MessageQueue.addAchievement("road lover");
            storeSetting(TYPE_ROAD_LOVER, "1");
            check[TYPE_ROAD_LOVER] = false;
        }
        else if (check[TYPE_NEVER_GIVE_UP] && playTimes >= CONDITION_NEVER_GIVE_UP) {
            MessageQueue.addAchievement("never|give up");
            storeSetting(TYPE_NEVER_GIVE_UP, "1");
            check[TYPE_NEVER_GIVE_UP] = false;
        }
        else if (check[TYPE_ROAD_IS_MY_LIFE] && playTimes >= CONDITION_ROAD_IS_MY_LIFE) {
            MessageQueue.addAchievement("road is|my life");
            storeSetting(TYPE_ROAD_IS_MY_LIFE, "1");
            check[TYPE_ROAD_IS_MY_LIFE] = false;
        }
    }
    
    private int needForRunningInFear;
    public void triggerRunningInFear() {
        if (check[TYPE_RUNNING_IN_FEAR]) {
            needForRunningInFear--;
            if (needForRunningInFear == 0) {
                MessageQueue.addAchievement("running|in fear");
                check[TYPE_RUNNING_IN_FEAR] = false;
            }
            storeSetting(TYPE_RUNNING_IN_FEAR, Integer.toString(needForRunningInFear));
        }
    }
    
    private int needForLikeABird;
    public void triggerLikeABird() {
        if (check[TYPE_LIKE_A_BIRD]) {
            needForLikeABird--;
            if (needForLikeABird == 0) {
                MessageQueue.addAchievement("like a|bird");
                check[TYPE_LIKE_A_BIRD] = false;
            }
            storeSetting(TYPE_LIKE_A_BIRD, Integer.toString(needForLikeABird));
        }
    }
    
    private int needForIamGhost;
    public void triggerIamGhost() {
        if (check[TYPE_IAM_GHOST]) {
            needForIamGhost--;
            if (needForIamGhost == 0) {
                MessageQueue.addAchievement("i'm|ghost");
                check[TYPE_IAM_GHOST] = false;
            }
            storeSetting(TYPE_IAM_GHOST, Integer.toString(needForIamGhost));
        }
    }
    
    private int needForTheDestroyer;
    public void triggerTheDestroyer() {
        if (check[TYPE_THE_DESTROYER]) {
            needForTheDestroyer--;
            if (needForTheDestroyer == 0) {
                MessageQueue.addAchievement("the|destroyer");
                check[TYPE_THE_DESTROYER] = false;
            }
            storeSetting(TYPE_THE_DESTROYER, Integer.toString(needForTheDestroyer));
        }
    }
    
    public void triggerEveryOneMustDie() {
        if (check[TYPE_EVERYONE_MUST_DIE]) {
            MessageQueue.addAchievement("everyone|must die");
            storeSetting(TYPE_EVERYONE_MUST_DIE, "1");
            check[TYPE_EVERYONE_MUST_DIE] = false;
        }
    }
    
    private int needForJesusBehindMe;
    public void triggerJesusBehindMe() {
        if (check[TYPE_JESUS_BEHIND_ME]) {
            needForJesusBehindMe--;
            if (needForJesusBehindMe == 0) {
                MessageQueue.addAchievement("jesus|behind me");
                check[TYPE_JESUS_BEHIND_ME] = false;
            }
            storeSetting(TYPE_JESUS_BEHIND_ME, Integer.toString(needForJesusBehindMe));
        }
    }
    
    public void triggerUnlockDesertLizard() {
        if (check[TYPE_DESERT_LIZARD]) {
            MessageQueue.addAchievement("desert|lizard");
            check[TYPE_DESERT_LIZARD] = false;
            storeSetting(TYPE_DESERT_LIZARD, "1");
        }
    }
    
    public void triggerUnlockSpiritRaider() {
        if (check[TYPE_SPIRIT_RAIDER]) {
            MessageQueue.addAchievement("spirit|raider");
            check[TYPE_SPIRIT_RAIDER] = false;
            storeSetting(TYPE_SPIRIT_RAIDER, "1");
        }
    }
    
    public void triggerUnlockDemonTomahawk() {
        if (check[TYPE_DEMON_TOMAHAWK]) {
            MessageQueue.addAchievement("demon|tomahawk");
            check[TYPE_DEMON_TOMAHAWK] = false;
            storeSetting(TYPE_DEMON_TOMAHAWK, "1");
        }
    }
    
    public void triggerUltimateThunder() {
        if (check[TYPE_ULTIMATE_THUNDER]) {
            MessageQueue.addAchievement("ultimate|thunder");
            check[TYPE_ULTIMATE_THUNDER] = false;
            storeSetting(TYPE_ULTIMATE_THUNDER, "1");
        }
    }
    
    public void triggerUltimateLizard() {
        if (check[TYPE_ULTIMATE_LIZARD]) {
            MessageQueue.addAchievement("ultimate|lizard");
            check[TYPE_ULTIMATE_LIZARD] = false;
            storeSetting(TYPE_ULTIMATE_LIZARD, "1");
        }
    }
    
    public void triggerUltimateSpirit() {
        if (check[TYPE_ULTIMATE_SPIRIT]) {
            MessageQueue.addAchievement("ultimate|spirit");
            check[TYPE_ULTIMATE_SPIRIT] = false;
            storeSetting(TYPE_ULTIMATE_SPIRIT, "1");
        }
    }
    
    public void triggerUltimateTomahawk() {
        if (check[TYPE_ULTIMATE_TOMAHAWK]) {
            MessageQueue.addAchievement("ultimate|tomahawk");
            check[TYPE_ULTIMATE_TOMAHAWK] = false;
            storeSetting(TYPE_ULTIMATE_TOMAHAWK, "1");
        }
    }
    
    public void triggerPowerfulRacer() {
        if (check[TYPE_POWERFUL_RACER]) {
            MessageQueue.addAchievement("powerful|racer");
            check[TYPE_POWERFUL_RACER] = false;
            storeSetting(TYPE_POWERFUL_RACER, "1");
        }
    }
}
