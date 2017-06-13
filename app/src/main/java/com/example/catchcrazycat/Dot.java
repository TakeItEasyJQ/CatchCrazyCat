package com.example.catchcrazycat;

/**
 * Created by My Computer on 2017/6/13.
 */

public class Dot {                  //dot类只为了记录格子的坐标和状态
    int x,y;
    int status;
    public static final int STATUS_ON=1;                               //路障
    public static final int STATUS_OFF=0;                               //灰色格子
    public static final int STATUS_IN=9;                                  //猫的位置

    public Dot(int x, int y) {
        this.x = x;
        this.y = y;
        status=STATUS_OFF;

    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getStatus() {
        return status;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setStatus(int status) {
        this.status = status;
    }
    public void setXY(int x,int y){
        this.x=x;
        this.y=y;
    }
}
