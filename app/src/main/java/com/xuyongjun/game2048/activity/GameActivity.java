package com.xuyongjun.game2048.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xuyongjun.game2048.Config;
import com.xuyongjun.game2048.R;
import com.xuyongjun.game2048.view.GameView;

public class GameActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int FLAG_SCROE = 0;
    public static final int FLAG_HEIGHT_SCROE = 1;

    private TextView mTvScore; // 分数
    private TextView mTvHeightRecord; // 最高记录
    private TextView mTvGoal;// 目标分数
    private RelativeLayout mGameRootPanel; // 存放游戏面板的布局
    private Button mBtnRevert; // 撤销按钮
    private Button mBtnRestart; // 重新开始按钮
    private Button mBtnOptions; // 选项按钮

    private GameView mGameView;//游戏面板

    private static GameActivity mGame;//当前类的引用

    public GameActivity() {
        mGame = this;
    }

    /**
     * 获取当前类的引用
     *
     * @return
     */
    public static GameActivity getGameActivity() {
        return mGame;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        //将游戏面板添加到布局中
        mGameView = new GameView(this);
        mGameRootPanel.addView(mGameView);
    }

    private void initView() {
        mTvScore = (TextView) findViewById(R.id.tv_score);
        mTvGoal = (TextView) findViewById(R.id.tv_goal);
        mTvHeightRecord = (TextView) findViewById(R.id.tv_height_record);
        mGameRootPanel = (RelativeLayout) findViewById(R.id.game_root_panel);
        mBtnRevert = (Button) findViewById(R.id.btn_revert);
        mBtnRestart = (Button) findViewById(R.id.btn_restart);
        mBtnOptions = (Button) findViewById(R.id.btn_options);
        mBtnRevert.setOnClickListener(this);
        mBtnRestart.setOnClickListener(this);
        mBtnOptions.setOnClickListener(this);

        setScroe(Config.mSp.getInt(Config.KEY_HEIGHT_SCROE, 0), 1);
    }

    /**
     * 设置目标分数
     *
     * @param goal
     */
    public void setGoal(int goal) {
        mTvGoal.setText(goal + "");
    }

    /**
     * 设置分数
     *
     * @param score
     * @param flag  0 分数 ，1 最高分
     */
    public void setScroe(int score, int flag) {
        switch (flag) {
            case FLAG_SCROE:
                mTvScore.setText(score + "");
                break;
            case FLAG_HEIGHT_SCROE:
                mTvHeightRecord.setText(score + "");
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_restart:
                mGameView.startGame();
                break;
            case R.id.btn_revert://撤销
                mGameView.revertGame();
                break;
            case R.id.btn_options:
                startActivityForResult(new Intent(this, OptionsActivity.class), 0);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            //设置目标分数
            mTvGoal.setText("" + Config.mSp.getInt(Config.KEY_GAME_GOAL, 2048));
            //设置最高分数
            setScroe(Config.mSp.getInt(Config.KEY_HEIGHT_SCROE, 0), 1);
            //重新开始游戏
            mGameView.startGame();
        }
    }


    /**
     * 结束Activity的时候保存最高分数
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //当现在的分数 大于最高分才保存
        if (Config.SCORE > Config.mSp.getInt(Config.KEY_HEIGHT_SCROE, 0)) {
            SharedPreferences.Editor editor = Config.mSp.edit();
            editor.putInt(Config.KEY_HEIGHT_SCROE, Config.SCORE);
            editor.apply();
        }
    }
}
