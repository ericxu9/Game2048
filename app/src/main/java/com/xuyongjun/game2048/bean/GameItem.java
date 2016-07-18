package com.xuyongjun.game2048.bean;

import android.content.Context;
import android.graphics.Color;
import android.text.TextPaint;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.xuyongjun.game2048.Config;

/**
 * ============================================================
 * 作 者 : XYJ
 * 版 本 ： 1.0
 * 创建日期 ： 2016/7/12 22:33
 * 描 述 ：小方块，游戏操作的最小单元；
 * 游戏的面板使用的是 GridLayout，在设计小方块之后，只要将不同的属性的小方块添加到
 * GridLayout中即可。
 *
 * 基于效率考虑，让小方块继承自FrameLayout，FrameLayout是几大布局中最轻量级的布局，并给
 * GameItem中给小方块设置了要显示的数字，并根据要显示的数字设置相应的颜色。
 * 修订历史 ：
 * ============================================================
 **/
public class GameItem extends FrameLayout {

    // 小方块显示的数字
    private int mCardNumber;
    // 要显示数字的TextView
    private TextView mTvNumber;
    // Text 布局属性
    private LayoutParams mParams;

    public GameItem(Context context,int cardNumber) {
        super(context);
        this.mCardNumber = cardNumber;
        //初始化 Item
        initCardItem();
    }

    /**
     * 初始化 Item
     */
    private void initCardItem() {
        //设置小方块默认颜色为灰色，从而使整个面板颜色为灰色
        setBackgroundColor(Color.GRAY);

        mTvNumber = new TextView(getContext());
        setTvNumber(mCardNumber);

        /*
        修改 5*5 或 6*6 时字体太大
         */
        int gameLines = Config.mSp.getInt(Config.KEY_GAME_LINES,4);//默认4行/列
        if (gameLines == 4) {
            mTvNumber.setTextSize(35);
        } else if (gameLines == 5) {
            mTvNumber.setTextSize(25);
        } else {
            mTvNumber.setTextSize(20);
        }

        TextPaint tp = mTvNumber.getPaint();
        //设置字体为粗体
        tp.setFakeBoldText(true);
        mTvNumber.setGravity(Gravity.CENTER);

        //设置TextView布局参数，并添加到小方块布局中
        mParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mParams.setMargins(8,8,8,8);
        addView(mTvNumber,mParams);
    }

    public View getItemView () {
        return mTvNumber;
    }

    public int getTvNumber () {
        return mCardNumber;
    }

    public void setTvNumber(int number) {
        this.mCardNumber = number;
        if (number == 0) {
            mTvNumber.setText("");
        } else {
            mTvNumber.setText(number+"");
        }

        /*
        设置小方块颜色
         */
        switch (number) {
            case 0:
                mTvNumber.setBackgroundColor(0xff666666);
                break;
            case 2:
                mTvNumber.setBackgroundColor(0xffeee5db);
                break;
            case 4:
                mTvNumber.setBackgroundColor(0xffeee0ca);
                break;
            case 8:
                mTvNumber.setBackgroundColor(0xfff2c17a);
                break;
            case 16:
                mTvNumber.setBackgroundColor(0xfff59667);
                break;
            case 32:
                mTvNumber.setBackgroundColor(0xfff68c6f);
                break;
            case 64:
                mTvNumber.setBackgroundColor(0xfff66e3c);
                break;
            case 128:
                mTvNumber.setBackgroundColor(0xffedcf74);
                break;
            case 256:
                mTvNumber.setBackgroundColor(0xffedcc64);
                break;
            case 512:
                mTvNumber.setBackgroundColor(0xffedc854);
                break;
            case 1024:
                mTvNumber.setBackgroundColor(0xffedc54f);
                break;
            case 2048:
                mTvNumber.setBackgroundColor(0xffedc32e);
                break;
            default:
                mTvNumber.setBackgroundColor(0xff3c4a34);
                break;
        }
    }
}
