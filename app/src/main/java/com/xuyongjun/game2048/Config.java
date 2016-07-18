package com.xuyongjun.game2048;

import android.app.Application;
import android.content.SharedPreferences;

/**
 * ============================================================
 * 作 者 : XYJ
 * 版 本 ： 1.0
 * 创建日期 ： 2016/7/13 19:00
 * 描 述 ：用户保存游戏的部分配置信息（使用 SharedPreference）
 * 修订历史 ：
 * ============================================================
 **/
public class Config extends Application{
    /**
     * SharedPreference对象
     */
    public static SharedPreferences mSp;

    /**
     * GameView 行列数
     */
    public static int mGameLines;

    /**
     * 游戏目标分数
     */
    public static int mGameGoal;

    /**
     * Item 宽高
     */
    public static int mItemSize;

    /**
     * 记录游戏分数
     */
    public static int SCORE = 0;

    public static String SP_FILE = "SP_HEIGHT_SCROE";

    public static String KEY_HEIGHT_SCROE = "KEY_HEIGHT_SCROE";

    public static String KEY_GAME_LINES = "KEY_GAME_LINES";

    public static String KEY_GAME_GOAL = "KEY_GAME_GOAL";

    @Override
    public void onCreate() {
        super.onCreate();
        mSp = getSharedPreferences(SP_FILE,MODE_PRIVATE);
        mGameLines = mSp.getInt(KEY_GAME_LINES,4);//默认4行/列
        mGameGoal = mSp.getInt(KEY_GAME_GOAL,2048);//目标分数
        mItemSize = 0;
    }
}
