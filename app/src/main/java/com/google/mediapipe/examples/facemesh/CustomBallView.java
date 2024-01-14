package com.google.mediapipe.examples.facemesh;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CustomBallView extends View {
    private boolean ballsInitialized = false;
    private List<Ball> balls;
    private Paint paint;
    private Handler handler;
    private Runnable runnable;

    public CustomBallView(Context context) {
        super(context);
        init();
    }

    private void init() {
        paint = new Paint();
        balls = new ArrayList<>();

        // Initialize handler and runnable for animation
        handler = new Handler(Looper.getMainLooper());
        runnable = new Runnable() {
            @Override
            public void run() {
                invalidate();
                handler.postDelayed(this, 16); // approximately 60 FPS
            }
        };
        handler.post(runnable);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (!ballsInitialized && w > 0 && h > 0) {
            createBalls(w, h);
            ballsInitialized = true;
        }
    }

    private void createBalls(int width, int height) {
        Random random = new Random();
        int numberOfBalls = 20;
        for (int i = 0; i < numberOfBalls; i++) {
            int radius = 30;
            int randomX = radius + random.nextInt(width - 2 * radius);
            int randomY = radius + random.nextInt(height - 2 * radius);
            int randomDx = random.nextInt(10) - 5;
            int randomDy = random.nextInt(10) - 5;
            int randomColor = Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));
            balls.add(new Ball(randomX, randomY, radius, randomDx, randomDy, randomColor));
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (Ball ball : balls) {
            ball.update(getWidth(), getHeight());
            ball.draw(canvas, paint);
        }
    }

    //ball 먹는거 판정/공 없애기
    public void updateBallsWithMouthCoordinates(float mouthTop, float mouthBottom, float mouthLeft, float mouthRight) {

        Log.v("mouth index", "mouthTop: "+ mouthTop*getHeight() + "mouthBottom: "+ mouthBottom);
        for (int i = 0; i < balls.size(); i++) {
            Ball ball = balls.get(i);
            Log.v("ball index", "ballTop: "+ ball.top + "ballBottom: "+ ball.bottom);
            // Ball 클래스에 정의된 top, bottom, left, right는 공의 위치를 나타내는 좌표입니다.
            // 여기서는 입의 좌표와 비교하여 공이 입 안에 있는지 확인합니다.
            //if (ball.top < mouthTop*getHeight() && ball.bottom > mouthBottom*getHeight() && ball.left > mouthLeft*getWidth() && ball.right < mouthRight*getWidth()) {
            if (ball.top < getHeight()-mouthTop*getHeight()) {
                // 조건에 맞는 경우, 공을 제거합니다.
                balls.remove(i);
                i--; // 리스트에서 항목을 제거한 후 인덱스를 조정합니다.
                Log.v("Eat!!!", "eat ball");
            }
        }
    }

}