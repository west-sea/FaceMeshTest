package com.google.mediapipe.examples.facemesh;

import android.graphics.Canvas;
import android.graphics.Paint;

public class Ball {
    private int x;
    private int y;
    private int radius;
    private int dx;
    private int dy;
    private int color;
    public int top;
    public int bottom;
    public int left;
    public int right;

    public Ball(int x, int y, int radius, int dx, int dy, int color) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.dx = dx;
        this.dy = dy;
        this.color = color;

        //상하좌우 끝점 좌표
        this.top = y + radius;
        this.bottom = y - radius;
        this.left = x - radius;
        this.right = x + radius;
    }

    public void update(int screenWidth, int screenHeight) {
        x += dx;
        y += dy;

        // Collision with edges
        if (x < radius || x > screenWidth - radius) {
            dx = -dx;
        }
        if (y < radius || y > screenHeight - radius) {
            dy = -dy;
        }
    }

    public void draw(Canvas canvas, Paint paint) {
        paint.setColor(color);
        canvas.drawCircle(x, y, radius, paint);
    }
}