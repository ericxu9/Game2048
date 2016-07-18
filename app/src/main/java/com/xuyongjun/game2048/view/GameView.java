package com.xuyongjun.game2048.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.EditText;
import android.widget.GridLayout;

import com.xuyongjun.game2048.Config;
import com.xuyongjun.game2048.activity.GameActivity;
import com.xuyongjun.game2048.bean.GameItem;

import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================
 * 作 者 : XYJ
 * 版 本 ： 1.0
 * 创建日期 ： 2016/7/13 19:41
 * 描 述 ：游戏主面板
 * 修订历史 ：
 * ============================================================
 **/
public class GameView extends GridLayout implements View.OnTouchListener {

    //GameView 对应矩阵
    private GameItem[][] mGameMatrix;
    //历史记录数组
    private int[][] mGameMatrixHistory;
    //历史记录分数
    private int mScoreHistory;
    //最高分数
    private int mHeightScore;
    //矩阵行列数
    private int mGameLines;
    //坐标
    private int mStartX, mStartY, mEndX, mEndY;
    //是否进行过一次合并
    private int mKeyItemNum = -1;

    private int mTarget;//目标分数

    //存放空格的List
    private List<Point> mBlanks;
    //辅助数组
    private List<Integer> mCalLists;

    public GameView(Context context) {
        super(context);
        // 获取目标分数
        mTarget = Config.mSp.getInt(Config.KEY_GAME_GOAL, 2048);
        initGameMatrix();

    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // 获取目标分数
        mTarget = Config.mSp.getInt(Config.KEY_GAME_GOAL, 2048);
        initGameMatrix();
    }

    /**
     * 初始化 View
     */
    private void initGameMatrix() {
        //移除之前游戏所有的布局
        removeAllViews();

        mScoreHistory = 0;//历史记录分数
        Config.SCORE = 0;//游戏分数
        Config.mGameLines = Config.mSp.getInt(Config.KEY_GAME_LINES, 4);// 行/列
        mGameLines = Config.mGameLines;

        mGameMatrix = new GameItem[mGameLines][mGameLines];
        mGameMatrixHistory = new int[mGameLines][mGameLines];

        //初始化集合
        mCalLists = new ArrayList<>();
        mBlanks = new ArrayList<>();

        mHeightScore = Config.mSp.getInt(Config.KEY_HEIGHT_SCROE, 0);//获取最高分
        //设置 GridLayout的 行和列
        setRowCount(mGameLines);
        setColumnCount(mGameLines);
        //设置滑动监听
        setOnTouchListener(this);

        //初始化 View 参数 , 获取每个小方块的 size，用屏幕宽/每行方块的数量
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        display.getMetrics(metrics);
        Config.mItemSize = metrics.widthPixels / mGameLines;

        initGameView(Config.mItemSize);
    }

    /**
     * 将所有小方块设置为0，随机添加两个数字上去
     *
     * @param cardSize
     */
    private void initGameView(int cardSize) {
        removeAllViews();
        GameItem card;

        for (int i = 0; i < mGameLines; i++) {
            for (int j = 0; j < mGameLines; j++) {
                card = new GameItem(getContext(), 0);
                addView(card, cardSize, cardSize);
                // 初始化GameMatrix 全部为0，空格 List 为所有
                mGameMatrix[i][j] = card;
                mBlanks.add(new Point(i, j));
            }
        }

        // 添加随机数字
        addRandomNum();
        addRandomNum();

    }

    /**
     * 重新开始游戏
     */
    public void startGame() {
        initGameMatrix();
        initGameView(Config.mItemSize);
    }

    /**
     * 添加随机数
     */
    private void addRandomNum() {
        getBlanks();
        if (mBlanks.size() > 0) {
            int randomNum = (int) (Math.random() * mBlanks.size());
            Point randomPoint = mBlanks.get(randomNum);
            mGameMatrix[randomPoint.x][randomPoint.y]
                    .setTvNumber(Math.random() > 0.2 ? 2 : 4);//5:1的比例显示2和4
            animCreate(mGameMatrix[randomPoint.x][randomPoint.y]);
        }

    }

    /**
     * 添加小方块动画
     * 0 添加方块动画 ，1 合并动画
     *
     * @param gameItem
     */
    private void animCreate(GameItem gameItem) {
            ScaleAnimation sa = new ScaleAnimation(
                    0.1f,
                    1,
                    0.1f,
                    1,
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f
            );
            sa.setDuration(100);
            gameItem.setAnimation(null);
            gameItem.getItemView().startAnimation(sa);
    }

    /**
     * 获取空格Item数组
     */
    private void getBlanks() {
        mBlanks.clear();
        for (int i = 0; i < mGameLines; i++) {
            for (int j = 0; j < mGameLines; j++) {
                if (mGameMatrix[i][j].getTvNumber() == 0)
                    mBlanks.add(new Point(i, j));
            }
        }

    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //保存移动前的状态
                saveHistoryMatrix();
                mStartX = (int) event.getX();
                mStartY = (int) event.getY();
                break;
            case MotionEvent.ACTION_MOVE:

                break;
            case MotionEvent.ACTION_UP:
                mEndX = (int) event.getX();
                mEndY = (int) event.getY();
                judgeDirection(mEndX - mStartX, mEndY - mStartY);
                //判断是否移动了
                if (isMoved()) {

                    addRandomNum();//添加一个数字
                    GameActivity.getGameActivity().setScroe(Config.SCORE, GameActivity.FLAG_SCROE);
                    if (!(mHeightScore > 0)) {
                        GameActivity.getGameActivity().setScroe(Config.SCORE,1);
                    }
                }
                checkCompleted();
                break;
        }
        return true;
    }

    /**
     * 判断游戏是否结束
     * 0:结束 1:正常 2:成功
     */
    private void checkCompleted() {
        int result = checkNums();
        if (result == 0) {
            // 保存最高分
            if (Config.SCORE > mHeightScore) {
                SharedPreferences.Editor editor = Config.mSp.edit();
                editor.putInt(Config.KEY_HEIGHT_SCROE, Config.SCORE);
                editor.apply();
                GameActivity.getGameActivity().setScroe(Config.SCORE, GameActivity.FLAG_HEIGHT_SCROE);
                //将分数置为0
                Config.SCORE = 0;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder
                    .setTitle("游戏失败").setPositiveButton("再来一次", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // 再来一次
                    startGame();
                }
            }).create().show();
        } else if (result == 1) { // 正常 不用管

        } else if (result == 2) { // 游戏胜利
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder
                    .setTitle("游戏胜利").setPositiveButton("重新开始", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // 再来一次
                    startGame();
                    dialog.dismiss();
                }
            }).create().show();

            builder.setNegativeButton("继续挑战", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SharedPreferences.Editor editor = Config.mSp.edit();
                    /*
                    提高难度
                     */
                    if (mTarget == 1024) {
                        editor.putInt(Config.KEY_HEIGHT_SCROE, 2048);
                        mTarget = 2048;
                        GameActivity.getGameActivity().setGoal(2048);
                    } else if (mTarget == 2048) {
                        editor.putInt(Config.KEY_HEIGHT_SCROE, 4096);
                        mTarget = 4096;
                        GameActivity.getGameActivity().setGoal(4096);
                    } else if (mTarget == 4096) {
                        editor.putInt(Config.KEY_HEIGHT_SCROE, 4096);
                        mTarget = 4096;
                        GameActivity.getGameActivity().setGoal(4096);
                    }
                    editor.apply();
                }
            }).create().show();
            Config.SCORE = 0;
        }
    }

    /**
     * 检测所有数字 看是否有满足条件的
     * 0结束 1正常 2成功
     */
    private int checkNums() {
        getBlanks();
        if (mBlanks.size() == 0) {//说明格子已经满了
            for (int i = 0; i < mGameLines; i++) {
                for (int j = 0; j < mGameLines; j++) {
                    /*
                    比较每个方块的右边是否存在相同的，存在则可以正常游戏
                     */
                    if (j < mGameLines - 1) {//最右边那个到顶了，所以不用比较
                        if (mGameMatrix[i][j].getTvNumber() == mGameMatrix[i][j + 1].getTvNumber()) {
                            return 1;
                        }
                    }
                    /*
                    比较每个方块的下面是否存在相同的，存在则可以正常游戏
                     */
                    if (i < mGameLines - 1) {//最下边那个到顶了，所以不用比较
                        if (mGameMatrix[i][j].getTvNumber() == mGameMatrix[i + 1][j].getTvNumber()) {
                            return 1;
                        }
                    }
                }
            }
            // 如果上面的比较都没有成立，说明游戏失败
            return 0;
        }

        // 判断游戏成功，只要遍历所有方块，看是否有目标值
        for (int i = 0; i < mGameLines; i++) {
            for (int j = 0; j < mGameLines; j++) {
                if (mGameMatrix[i][j].getTvNumber() == mTarget) {
                    return 2;
                }
            }
        }

        return 1;
    }

    /**
     * 判断是否移动过(是否需要新增Item)
     */
    private boolean isMoved() {
        for (int i = 0; i < mGameLines; i++) {
            for (int j = 0; j < mGameLines; j++) {
                if (mGameMatrixHistory[i][j] != mGameMatrix[i][j].getTvNumber()) {
                    //移动过
                    return true;
                }

            }
        }
        return false;
    }

    /**
     * 撤销上次移动
     */
    public void revertGame() {
        //第一次不能撤销
        if (isMoved()) {
            GameActivity.getGameActivity().setScroe(mScoreHistory, GameActivity.FLAG_SCROE);
            Config.SCORE = mScoreHistory;
            for (int i = 0; i < mGameLines; i++) {
                for (int j = 0; j < mGameLines; j++) {
                    mGameMatrix[i][j].setTvNumber(mGameMatrixHistory[i][j]);
                }
            }
        }

    }

    /**
     * 保存历史记录
     */
    private void saveHistoryMatrix() {
        mScoreHistory = Config.SCORE;
        for (int i = 0; i < mGameLines; i++) {
            for (int j = 0; j < mGameLines; j++) {
                mGameMatrixHistory[i][j] = mGameMatrix[i][j].getTvNumber();
            }
        }
    }

    /**
     * 判断移动方向
     *
     * @param offsetX
     * @param offsetY
     */
    private void judgeDirection(int offsetX, int offsetY) {
        int density = getDeviceDensity();
        //滑动距离
        int slideDistance = 5 * density;
        // 滑动最大的距离，不能超过这个范围
        int maxDistance = 200 * density;

        // 正常滑动
        boolean flagNormal = (Math.abs(offsetX) > slideDistance
                || Math.abs(offsetY) > slideDistance) &&
                (Math.abs(offsetX) < maxDistance && Math.abs(offsetY) < maxDistance);
        // 非正常滑动，超出范围
        boolean flagOut = Math.abs(offsetX) > maxDistance || Math.abs(offsetY) > maxDistance;

        if (flagNormal && !flagOut) {//正常
            if (Math.abs(offsetX) > Math.abs(offsetY)) {//X轴方向滑动
                if (offsetX > slideDistance) {
                    //右滑
                    swipeRight();
                } else {
                    //左滑
                    swipeLeft();
                }
            } else { // Y轴方向滑动
                if (offsetY > slideDistance) {
                    //下滑
                    swipeDown();
                } else {
                    //上滑
                    swipeUp();
                }
            }
        } else if (flagOut) { //超出范围
            // 超级权限
            final EditText editText = new EditText(getContext());
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("帮助器：添加数字").setView(editText)
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).setPositiveButton("添加", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String num = editText.getText().toString();
                    if (!TextUtils.isEmpty(num)) {
                        addSuperNum(num);
                    }
                }
            }).create().show();
        }
    }

    private void addSuperNum(String num) {
        if (checkSuperNum(num)) {
            getBlanks();
            if (mBlanks.size() > 0) {
                int randomNum = (int) (Math.random() * mBlanks.size());
                Point randomPoint = mBlanks.get(randomNum);
                mGameMatrix[randomPoint.x][randomPoint.y]
                        .setTvNumber(Integer.parseInt(num));//5:1的比例显示2和4
                animCreate(mGameMatrix[randomPoint.x][randomPoint.y]);
            }
        }
    }

    private boolean checkSuperNum(String num) {
        if (num .equals("2")||num .equals("4")||num .equals("8")
                ||num .equals("16")||num .equals("32")||num .equals("64")
                ||num .equals("128")||num .equals("256")||num .equals("512")
                ||num .equals("1024")) {

            return true;
        }

        return false;
    }

    /**
     * 获取设备密度 1dp = 多少 px,因为不同的设备密度可能不同
     */
    private int getDeviceDensity() {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm =
                (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);
        return (int) metrics.density;
    }

    /**
     * 右滑
     */
    private void swipeRight() {
        for (int i = mGameLines - 1; i >= 0; i--) {
            for (int j = mGameLines - 1; j >= 0; j--) {
                int currentNum = mGameMatrix[i][j].getTvNumber();
                if (currentNum != 0) {
                    if (mKeyItemNum == -1) {
                        mKeyItemNum = currentNum;

                    } else {
                        if (mKeyItemNum == currentNum) {
                            mCalLists.add(mKeyItemNum * 2);
                            Config.SCORE += mKeyItemNum * 2;
                            mKeyItemNum = -1;

                        } else {
                            mCalLists.add(mKeyItemNum);
                            mKeyItemNum = currentNum;
                        }
                    }
                } else {
                    continue;
                }
            }
            if (mKeyItemNum != -1) {
                mCalLists.add(mKeyItemNum);
            }

            //设置Item
            int index = mCalLists.size() - 1;
            for (int j = mGameLines - mCalLists.size(); j < mGameLines; j++) {
                mGameMatrix[i][j].setTvNumber(mCalLists.get(index));
                index--;
            }
            //设置空格
            for (int k = 0; k < mGameLines - mCalLists.size(); k++) {
                mGameMatrix[i][k].setTvNumber(0);
            }

            //清除
            mKeyItemNum = -1;
            mCalLists.clear();
            index = 0;
        }
    }

    /**
     * 左滑
     */
    private void swipeLeft() {
        for (int i = 0; i < mGameLines; i++) {
            for (int j = 0; j < mGameLines; j++) {
                int currentNum = mGameMatrix[i][j].getTvNumber();
                // 2 2 4 8 8 16
                if (currentNum != 0) {
                    if (mKeyItemNum == -1) {
                        mKeyItemNum = currentNum;
                    } else {
                        // 判断相邻两个数是否相等
                        if (mKeyItemNum == currentNum) {
                            mCalLists.add(mKeyItemNum * 2); // 4
                            Config.SCORE += mKeyItemNum * 2; //记录分数

                            mKeyItemNum = -1;

                            // 不相等
                        } else {
                            //记录没合并的数
                            mCalLists.add(mKeyItemNum);
                            mKeyItemNum = currentNum;
                        }
                    }
                } else {
                    continue;
                }
            }

            // 如果行里最后两个数没有合并，则记录最后一个数
            if (mKeyItemNum != -1) {
                mCalLists.add(mKeyItemNum);
            }
            //改变 Item值
            for (int j = 0; j < mCalLists.size(); j++) {
                mGameMatrix[i][j].setTvNumber(mCalLists.get(j));
            }
            //设置空格
            for (int k = mCalLists.size(); k < mGameLines; k++) {
                mGameMatrix[i][k].setTvNumber(0);
            }
            //重置行参数
            mKeyItemNum = -1;
            mCalLists.clear();
        }

    }

    /**
     * 下滑
     */
    private void swipeDown() {
        for (int i = mGameLines - 1; i >= 0; i--) {
            for (int j = mGameLines - 1; j >= 0; j--) {
                int currentNum = mGameMatrix[j][i].getTvNumber();
                if (currentNum != 0) {
                    if (mKeyItemNum == -1) {
                        mKeyItemNum = currentNum;
                    } else {
                        if (mKeyItemNum == currentNum) {
                            mCalLists.add(mKeyItemNum * 2);
                            Config.SCORE += mKeyItemNum * 2;
                            mKeyItemNum = -1;

                        } else {
                            mCalLists.add(mKeyItemNum);
                            mKeyItemNum = currentNum;
                        }
                    }
                } else {
                    continue;
                }
            }

            if (mKeyItemNum != -1) {
                mCalLists.add(mKeyItemNum);
            }

            // 设置空格
            for (int k = 0; k < mGameLines - mCalLists.size(); k++) {
                mGameMatrix[k][i].setTvNumber(0);
            }
            int index = mCalLists.size() - 1;
            //改变 Item
            for (int j = mGameLines - mCalLists.size(); j < mGameLines; j++) {
                mGameMatrix[j][i].setTvNumber(mCalLists.get(index));
                index--;
            }

            mKeyItemNum = -1;
            index = 0;
            mCalLists.clear();
        }
    }

    /**
     * 上滑
     */
    private void swipeUp() {
        for (int i = 0; i < mGameLines; i++) {
            for (int j = 0; j < mGameLines; j++) {
                int currentNum = mGameMatrix[j][i].getTvNumber();
                if (currentNum != 0) {
                    if (mKeyItemNum == -1) {
                        mKeyItemNum = currentNum;
                    } else {
                        // 判断相邻两个数是否相等
                        if (mKeyItemNum == currentNum) {
                            mCalLists.add(mKeyItemNum * 2);
                            Config.SCORE += mKeyItemNum * 2; //记录分数

                            mKeyItemNum = -1;
                            // 不相等
                        } else {
                            //记录没合并的数
                            mCalLists.add(mKeyItemNum);
                            mKeyItemNum = currentNum;
                        }
                    }
                } else {
                    continue;
                }
            }

            // 如果行里最后两个数没有合并，则记录最后一个数
            if (mKeyItemNum != -1) {
                mCalLists.add(mKeyItemNum);
            }
            //改变 Item值
            for (int j = 0; j < mCalLists.size(); j++) {
                mGameMatrix[j][i].setTvNumber(mCalLists.get(j));
            }
            //设置空格
            for (int k = mCalLists.size(); k < mGameLines; k++) {
                mGameMatrix[k][i].setTvNumber(0);
            }
            //重置行参数
            mKeyItemNum = -1;
            mCalLists.clear();
        }

    }


}
