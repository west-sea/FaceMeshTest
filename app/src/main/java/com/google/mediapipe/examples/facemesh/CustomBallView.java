package com.google.mediapipe.examples.facemesh;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

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
    private Integer score = 0;


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
        int numberOfBalls = 5;
        for (int i = 0; i < numberOfBalls; i++) {
            int radius = 30;
            //int randomX = radius+400 + random.nextInt(width - 2 * radius - 400);
            //int randomY = radius+300 + random.nextInt(height - 2 * radius - 300);
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
            // Log.d(TAG, "Ball Position - X: " + ball.getX() + ", Y: " + ball.getY());
        }
    }

    //ball 먹는거 판정/공 없애기
    public void updateBallsWithMouthCoordinates(float mouthTop, float mouthBottom, float mouthLeft, float mouthRight) {

        //Log.v("Mouth Coordinates", "mouthLeft: " + (mouthLeft * getWidth()) + ", mouthRight: " + (mouthRight * getWidth())+", mouthTop: " + (mouthTop * getHeight()) + ", mouthBottom: " + (mouthBottom * getHeight()));

        // 공의 개수 만큼 count
        for (int i = 0; i < balls.size(); i++) {
            // i번째 공에 대해서
            Ball ball = balls.get(i);
            // 좌표상에서의 ball의 y좌표 위 및 아래
            Log.v("Mouth Coordinates", "mouthLeft: " + (mouthLeft * getWidth()) + ", mouthRight: " + (mouthRight * getWidth())+", mouthTop: " + (mouthTop * getHeight()) + ", mouthBottom: " + (mouthBottom * getHeight()));
            Log.v("Ball Coordinates", "Ball X: " + ball.getX() + ", Ball Y: " + ball.getY());

            // 여기서는 입의 좌표와 비교하여 공이 입 안에 있는지 확인합니다.
            //if (ball.top < mouthTop*getHeight() && ball.bottom > mouthBottom*getHeight() && ball.left > mouthLeft*getWidth() && ball.right < mouthRight*getWidth()) {

            // ball의 윗부분이 입의 윗부분보다 위에 있을 경우
            //if (ball.top < mouthTop * getHeight()) {

            // 조건에 맞는 경우, 공을 제거합니다.
            // 조건1] 공의 윗부분이 입술의 윗부분보다 아래에 있을때           - 보류
            // 조건2] 공의 아랫부분이 입술의 아랫부분보다 위쪽에 있을때        - 보류
            // 조건3] 공의 왼쪽이 입술의 왼쪽보다 오른쪽에 있을때            - 적용
            // 조건4] 공의 오른쪽이 입술의 오른쪽보다 왼쪽에 있을때           - 적용
            if(((float) ball.bottom < mouthBottom*getHeight()*1.05) && ((float) ball.top > mouthTop*getHeight()*0.95) && ((float) ball.left>mouthLeft*getWidth()*0.7) && ((float) ball.right<mouthRight*getWidth()*1.3)){
                Log.d("Ball Removed", "Ball X: " + ball.getX() + ", Ball Y: " + ball.getY() + " removed by mouth coordinates mouthLeft: " + (mouthLeft * getWidth()) + ", mouthRight: " + (mouthRight * getWidth()));
                balls.remove(i);
                i--; // 리스트에서 항목을 제거한 후 인덱스를 조정합니다.
                Log.d("Eat", "Ball eaten!!!");

                //점수
                score = score + 1;
                MyGlobals.getInstance().setScore(score);
            }
        }
    }

}