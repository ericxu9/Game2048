package com.xuyongjun.game2048.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.xuyongjun.game2048.Config;
import com.xuyongjun.game2048.R;

/**
 * ============================================================
 * 作 者 : xyj
 * 版 本 ： 1.0
 * 创建日期 ： 2016-7-18
 * 描 述 : 游戏设置Activity
 * 修订历史 ：
 * ============================================================
 **/

public class OptionsActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnLines;//行/列数
    private Button btnGoal;//目标分数
    private Button btnBack;//返回
    private Button btnOk;//确定

    private String[] lines = {"4", "5", "6"};
    private String[] goals = {"1024","2048","4096"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initView();
    }

    private void initView() {
        btnBack = (Button) findViewById(R.id.btn_back);
        btnGoal = (Button) findViewById(R.id.btn_goal);
        btnLines = (Button) findViewById(R.id.btn_lines);
        btnOk = (Button) findViewById(R.id.btn_ok);
        // 动态改变文字信息
        btnGoal.setText(Config.mSp.getInt(Config.KEY_GAME_GOAL,2048)+"");
        btnLines.setText(Config.mSp.getInt(Config.KEY_GAME_LINES,4)+"");

        btnBack.setOnClickListener(this);
        btnLines.setOnClickListener(this);
        btnGoal.setOnClickListener(this);
        btnOk.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                this.finish();
                break;
            case R.id.btn_ok:
                saveConfig();
                setResult(RESULT_OK);
                finish();
                break;

            case R.id.btn_goal:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setItems(goals , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        btnGoal.setText(goals[which]);
                    }
                }).create().show();
                break;

            case R.id.btn_lines:
                AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
                builder2.setItems(lines , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        btnLines.setText(lines[which]);
                    }
                }).create().show();
                break;

        }
    }

    /**
     * 保存信息
     */
    private void saveConfig() {
        SharedPreferences.Editor editor = Config.mSp.edit();
        editor.putInt(Config.KEY_GAME_GOAL,
                Integer.parseInt(btnGoal.getText().toString()));
        editor.putInt(Config.KEY_GAME_LINES,
                Integer.parseInt(btnLines.getText().toString()));
        editor.commit();
    }
}
