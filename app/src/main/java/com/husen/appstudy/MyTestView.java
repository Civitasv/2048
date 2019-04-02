package com.husen.appstudy;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.husen.application.MyApplication;
import com.husen.entity.User;
import com.husen.greendao.UserDao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 2048小游戏
 */
public class MyTestView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private AlertDialog.Builder builder;
    // 屏幕宽度
    private int screenWidth = 0;
    // 屏幕高度
    private int screenHeight = 0;
    // 标示是否正在运行
    private boolean isRun = false;
    // SurfaceHolder对象
    SurfaceHolder sfh = null;
    // canvas对象
    private Canvas canvas;
    // Paint对象
    public static Paint paint = null;
    // 绘制文字
    public static Paint paintText = null;
    // Bitmap对象
    private Bitmap bitmap;
    // 横线数
    private static final int GRIDW_SIZE = 5;
    // 竖线数
    private static final int GRIDH_SIZE = 5;
    // 外间距
    private static int LRWIDTH = 40;
    // 外间距
    private static int UDWIDTH = 40;
    // 矩形宽
    private static float RAWWIDTH = 0;
    // 矩形长
    private static float COLUMNIDTH = 0;
    // 内间距
    private static float pad = 10;
    // 游戏界面与上方间距
    private float topDistance = 0;
    // 资源文件
    public static Resources sResources = null;
    // 按下时的X坐标
    private static float downX;
    // 按下时的Y坐标
    private static float downY;
    private int animatedValue;
    private int colorEnd;
    private int colorStart;

    // 存放每一个矩形中的值
    static int array[][] = new int[4][4];
    /**
     * 带参构造器
     *
     * @param activity activity对象
     */
    public MyTestView(Activity activity) {
        super(activity);
        isRun = true;
        init(array);
        // 开启硬件离屏缓存
        setLayerType(LAYER_TYPE_HARDWARE, null);
        paint = new Paint();
        paintText = new Paint();
        sResources = getResources();
        sfh = this.getHolder();
        sfh.addCallback(this);
    }

    public MyTestView(Context context, AttributeSet attrs) {
        super(context,attrs);
        isRun = true;
        init(array);
        // 开启硬件离屏缓存
        setLayerType(LAYER_TYPE_HARDWARE, null);
        paint = new Paint();
        paintText = new Paint();
        sResources = getResources();
        sfh = this.getHolder();
        sfh.addCallback(this);
    }

    /**
     * 数组初始化
     *
     * @param array
     * @return
     */
    public static int[][] init(int[][] array) {        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[0].length; j++) {
                array[i][j] = 0;
            }
        }
        // 随机生成一个2
        if (ChessUtil.hasEmpty(array) == true) {
            ChessUtil.randTwo(array);
        }

        return array;
    }

    /**
     * 图片缩小或放大
     *
     * @param resourcesID
     * @param scr_width
     * @param res_height
     * @return
     */
    private Bitmap CreateMatrixBitmap(int resourcesID, float scr_width,
                                     float res_height) {
        Bitmap bitMap = null;
        bitMap = BitmapFactory.decodeResource(sResources, resourcesID);
        int bitWidth = bitMap.getWidth();
        int bitHeight = bitMap.getHeight();
        float scaleWidth = scr_width / (float) bitWidth;
        float scaleHeight = res_height / (float) bitHeight;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        bitMap = Bitmap.createBitmap(bitMap, 0, 0, bitWidth, bitHeight, matrix,
                true);
        return bitMap;
    }


    /**
     * 绘制2048棋盘
     */
    public void draw() {

        screenWidth = this.getWidth();
        screenHeight = this.getHeight();
        // 设置背景
        bitmap = CreateMatrixBitmap(R.drawable.bg, screenWidth, screenHeight);
        topDistance = screenHeight / 4;
        // 设置单元格间隔，并且使横向间隔等于纵向间隔
        RAWWIDTH = (screenWidth - 2 * LRWIDTH - pad * GRIDW_SIZE) / (GRIDW_SIZE - 1);
        COLUMNIDTH = (screenHeight - 2 * UDWIDTH - pad * GRIDH_SIZE) / (GRIDH_SIZE - 1);
        if (RAWWIDTH > COLUMNIDTH) {
            RAWWIDTH = COLUMNIDTH;
        } else {
            COLUMNIDTH = RAWWIDTH;
        }
        canvas = sfh.lockCanvas();
        // Paint paint = new Paint();
        // 绘制背景
        // canvas.drawBitmap(bitmap, 0, 0, paint);
        canvas.drawColor(Color.WHITE);
        paint.setColor(Color.parseColor("#deb287"));
        paint.setStrokeWidth(2);
        paint.setStyle(Paint.Style.FILL);
        float startX = 0, startY = 0, endX = 0, endY = 0;
        // 绘制棋盘内部
        for (int i = 0; i < GRIDW_SIZE - 1; i++) {
            for (int j = 0; j < GRIDH_SIZE - 1; j++) {
                startX = LRWIDTH + i * RAWWIDTH + (i + 1) * pad;
                endX = startX + RAWWIDTH;
                startY = UDWIDTH + topDistance + j * COLUMNIDTH + (j + 1) * pad;
                endY = startY + COLUMNIDTH;
                canvas.drawRect(startX, startY, endX, endY, paint);
            }
        }
        paint.setColor(Color.parseColor("#e0c5ab"));
        paint.setStrokeWidth(pad);
        paint.setStyle(Paint.Style.STROKE);
        // 绘制棋盘每个矩形的边线
        for (int i = 0; i < GRIDW_SIZE; i++) {
            startX = LRWIDTH + RAWWIDTH * i + pad * (i + 0.5f);
            startY = UDWIDTH + topDistance;
            endY = startY + COLUMNIDTH * (GRIDH_SIZE - 1) + pad * (GRIDH_SIZE - 1);
            canvas.drawLine(startX, startY, startX, endY, paint);
        }
        for (int j = 0; j < GRIDH_SIZE; j++) {
            startX = LRWIDTH;
            startY = UDWIDTH + topDistance + COLUMNIDTH * j + pad * (j + 0.5f);
            endX = startX + RAWWIDTH * (GRIDW_SIZE - 1) + pad * (GRIDW_SIZE - 1);
            canvas.drawLine(startX, startY, endX, startY, paint);
        }
        drawNum();
        if (canvas != null) {
            sfh.unlockCanvasAndPost(canvas);
        }
    }

    /**
     * 绘制棋盘中的数
     */
    public void drawNum() {
        float startX = 0, startY = 0, endX = 0, endY = 0;
        paintText.setColor(Color.BLACK);
        paintText.setStrokeWidth(40);
        paintText.setStyle(Paint.Style.FILL);
        paintText.setTextAlign(Paint.Align.CENTER);
        paintText.setTextSize(100);
        paintText.setTypeface(Typeface.SANS_SERIF);
        paint.setStrokeWidth(2);
        paint.setStyle(Paint.Style.FILL);
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[0].length; j++) {
                if (array[j][i] != 0) {
                    if(array[j][i]==2){
                        paint.setColor(Color.parseColor("#ffcece"));
                    }
                    else if(array[j][i]==4){
                        paint.setColor(Color.parseColor("#ffc1c8"));
                    }
                    else if(array[j][i]==8){
                        paint.setColor(Color.parseColor("#ffe3b0"));
                    }
                    else if(array[j][i]==16){
                        paint.setColor(Color.parseColor("#8ed6ff"));
                    }
                    else if(array[j][i]==32){
                        paint.setColor(Color.parseColor("#65c6c4"));
                    }
                    else if(array[j][i]==64){
                        paint.setColor(Color.parseColor("#408ab4"));
                    }
                    else if(array[j][i]==128){
                        paint.setColor(Color.parseColor("#f3dcad"));
                    }
                    else if(array[j][i]==256){
                        paint.setColor(Color.parseColor("#c7004c"));
                    }
                    else if(array[j][i]==512){
                        paint.setColor(Color.parseColor("#8f71ff"));
                    }
                    else if(array[j][i]==1024){
                        paint.setColor(Color.parseColor("#a06ee1"));
                    }
                    else if(array[j][i]>1024){
                        paint.setColor(Color.parseColor("#421b9b"));
                    }
                    startX = LRWIDTH + i * RAWWIDTH + (i + 1) * pad;
                    endX = startX + RAWWIDTH;
                    startY = UDWIDTH + topDistance + j * COLUMNIDTH + (j + 1) * pad;
                    endY = startY + COLUMNIDTH;
                    canvas.drawRect(startX, startY, endX, endY, paint);
                    canvas.drawText(array[j][i] + "", (startX + endX) / 2, (startY + endY + paintText.getTextSize()) / 2, paintText);
                }

            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        //在触发时回去到起始坐标
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //将按下时的坐标存储
                downX = x;
                downY = y;
                Log.e("Tag", "=======按下时X：" + x);
                Log.e("Tag", "=======按下时Y：" + y);
                break;
            case MotionEvent.ACTION_UP:
                Log.e("Tag", "=======抬起时X：" + x);
                Log.e("Tag", "=======抬起时Y：" + y);

                //获取到距离差
                float dx = x - downX;
                float dy = y - downY;
                //防止是按下也判断
                if (Math.abs(dx) > 8 && Math.abs(dy) > 8) {
                    //通过距离差判断方向
                    int orientation = getOrientation(dx, dy);
                    ChessUtil.slide(orientation, array);
                    MainActivity.score.setText("分数"+"\n"+ChessUtil.score+"");
                    //如果还有空，则生成一个2,并保存数组


                    if (ChessUtil.hasEmpty(array) == true && ChessUtil.operate == 1) {
                        ChessUtil.randTwo(array);
                    }
                    // 如果没有空，则结束
                    if (ChessUtil.hasEmpty(array) == false && ChessUtil.isFull(array) == true) {
                        // dialog
                        builder = new AlertDialog.Builder(getContext()).setIcon(R.drawable.ic_about).setTitle("GameOver")
                                .setMessage("游戏结束！").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        //Toast.makeText(UserInfoActivity.this, "确定按钮", Toast.LENGTH_LONG).show();

                                    }
                                }).setNegativeButton("关闭", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        //Toast.makeText(UserInfoActivity.this, "关闭按钮", Toast.LENGTH_LONG).show();
                                        dialogInterface.dismiss();
                                    }
                                });
                        builder.create().show();
                        break;
                    }
                }
                break;
        }
        return true;
    }

    /**
     * 根据距离差判断 滑动方向
     *
     * @param dx X轴的距离差
     * @param dy Y轴的距离差
     * @return 滑动的方向
     */
    private int getOrientation(float dx, float dy) {
        Log.e("Tag", "========X轴距离差：" + dx);
        Log.e("Tag", "========Y轴距离差：" + dy);
        if (Math.abs(dx) > Math.abs(dy)) {
            //X轴移动
            return dx > 0 ? 'y' : 'z';
        } else {
            //Y轴移动
            return dy > 0 ? 'x' : 's';
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        isRun = true;
        new Thread(this).start();
    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }


    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isRun = false;
    }


    @Override
    public void run() {
        while (isRun) {
            try {
                Thread.sleep(100);
                synchronized (sfh) {
                    draw();
                }
            } catch (Exception e) {

            }
        }
    }
}

