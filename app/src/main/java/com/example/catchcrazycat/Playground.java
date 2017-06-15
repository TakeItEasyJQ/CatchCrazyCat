package com.example.catchcrazycat;

import android.app.Application;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by My Computer on 2017/6/13.
 */

public class Playground extends SurfaceView implements View.OnTouchListener{       //所有场景元素都绘制与SurfaceView
    private static int WIDTH=100;
    private static final int COL=15;
    private static final int ROW=15 ;
    private static final int BLOCKS=10;  //默认添加的路障数量
    private Dot matrix[][];
    private Dot cat;
    private boolean justInit;
    public Playground(Context context) {
        super(context);
//        getHolder().setFixedSize(1080,1080);
        getHolder().addCallback(callback);
        matrix=new Dot[ROW][COL];
        for (int i=0;i<ROW;i++){
            for (int j=0;j<COL;j++){
                matrix[i][j]=new Dot(j,i);
            }
        }
        setOnTouchListener(this);
        initGame();
    }

    private Dot getDot(int x,int y){
        return matrix[y][x];
    }

    private boolean isAtEdge(Dot d){                                   //因为x，y范围为0~9，
        if (d.getX()*d.getY()==0||d.getX()+1==COL||d.getY()+1==ROW){
            return true;
        }
        return false;
    }
    private Dot getNeighbor(Dot one,int dir){
        switch (dir){
            case  1:
               return getDot(one.getX()-1,one.getY());
            case  2:
                if (one.getY()%2==0){
                    return getDot(one.getX()-1,one.getY()-1);
                }else {
                    return getDot(one.getX(),one.getY()-1);
                }

            case  3:
                if (one.getY()%2==0){
                    return getDot(one.getX(),one.getY()-1);
                }else {
                    return getDot(one.getX()+1,one.getY()-1);
                }
            case  4:
                return getDot(one.getX()+1,one.getY());
            case  5:
                if (one.getY()%2==0){
                    return getDot(one.getX(),one.getY()+1);
                }else {
                    return getDot(one.getX()+1,one.getY()+1);
                }
            case  6:
                if (one.getY()%2==0){
                    return getDot(one.getX()-1,one.getY()+1);
                }else {
                    return getDot(one.getX(),one.getY()+1);
                }
            default:
                break;
        }
        return null;
    }
    private int getDistance(Dot one,int dir){
        int distance=0;
        if (isAtEdge(one)){
            return 1;
        }
        Dot ori=one,next;
        while (true){
            next=getNeighbor(ori,dir);
            if (next.getStatus()==Dot.STATUS_ON){
                return distance*-1;
            }
            if (isAtEdge(next)){
                distance++;
                return distance;
            }
            distance++;
            ori=next;
        }
    }
    private void moveTo(Dot one){
        one.setStatus(Dot.STATUS_IN);
        getDot(cat.getX(),cat.getY()).setStatus(Dot.STATUS_OFF );
        cat.setXY(one.getX(),one.getY() );
    }
    private void move(){
        if (isAtEdge(cat)){
            lose();
            return;
        }
        List<Dot> dots=new ArrayList<>();
        List<Dot> positive=new ArrayList<>();
        HashMap<Dot,Integer> al=new HashMap<>();
        for (int i=1;i<7;i++){
            Dot neighbor=getNeighbor(cat,i);
            if (neighbor.getStatus()==Dot.STATUS_OFF){
                dots.add(neighbor);
                al.put(neighbor,i);
                if (getDistance(neighbor,i)>0){
                    positive.add(neighbor);
//                    al.put(neighbor,i);          //其实可以共用，都是通过Key来提取
                }
            }
        }
        if (dots.size()==0){
            win();
        } else if (dots.size()==1){
            moveTo(dots.get(0));
        }else  {
            if (justInit){
                int s=(int) (Math.random()*dots.size()%10);
                moveTo(dots.get(s));
                justInit=false;
            }else{
                Dot best=null;
                if (positive.size()!=0){               //有直达边界的路径  走最短的    对路长进行比较
                    int min=999;
                    for (int i=0;i<positive.size();i++){
                        int a= getDistance(positive.get(i),al.get(positive.get(i)));
                        if (a<min){
                            min=a;
                            best=positive.get(i);
                        }
                    }

                }else {                                    //没有直达边界的路径，走可走的最长的路径
                    int max=0;
                    for (int i=0;i<dots.size();i++){
                        int k=getDistance(dots.get(i),al.get(dots.get(i)));
                        if (k<=max){
                            max=k;
                            best=dots.get(i);
                        }
                    }
                }
                moveTo(best);
            }
        }

    }
    private void win(){
        Toast.makeText(getContext(),"You Win!",Toast.LENGTH_SHORT).show();
    }
    private void lose(){
        Toast.makeText(getContext(),"You Lose!",Toast.LENGTH_SHORT).show();
    }


    public  void redraw() {
        Canvas c=getHolder().lockCanvas();           //获得画布
        c.drawColor(0xFFCCCACA);
        Paint paint=new Paint();                     //获得画笔
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);    //抗锯齿
        for (int i=0;i<ROW;i++){
            int offset=0;
            if (i % 2 != 0) {
                offset=WIDTH/2;
            }
            for (int j=0;j<COL;j++){
                Dot one=getDot(j,i);
                switch (one.status){
                    case Dot.STATUS_OFF:
                        paint.setColor(0xFFEEEEEE);        //0xFF为半透明状态
                        break;
                    case Dot.STATUS_ON:
                        paint.setColor(0xFFFFAA00);
                        break;
                    case Dot.STATUS_IN:
                        paint.setColor(0xFFFF0000);
                        break;
                    default:
                        break;
                }
                c.drawOval(new RectF(one.getX()*WIDTH+offset,one.getY()*WIDTH,(one.getX()+1)*WIDTH+offset,(one.getY()+1)*WIDTH),paint);

                c.drawBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.img_restart),this.getWidth()/2-64,getBottom()-350,paint);

            }
        }
        getHolder().unlockCanvasAndPost(c);           //将所画内容更新到界面上
    }

    SurfaceHolder.Callback callback=new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            redraw();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            WIDTH=width/(COL+1);
            redraw();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {

        }
    };
    public void initGame(){
        justInit=true;
        for (int i=0;i<ROW;i++){
            for (int j=0;j<COL;j++){
                matrix[i][j].setStatus(Dot.STATUS_OFF);
            }
        }
        cat =new Dot(7,8);
        getDot(7,8).setStatus(Dot.STATUS_IN);
        for (int i=0;i<BLOCKS;){
            int x=(int)(Math.random()*1000%COL);
            int y=(int)(Math.random()*1000%ROW);
            if (getDot(x,y).getStatus()==Dot.STATUS_OFF){
                getDot(x,y).setStatus(Dot.STATUS_ON);
                i++;
            }
        }
    }

    @Override
    public  boolean onTouch(View v, MotionEvent event) {
        if (event.getAction()==MotionEvent.ACTION_UP) {               //MotionEvent.ACTION_UP 为手触后抬起那一刹那的监听
           int x,y;
            y=(int)event.getY()/WIDTH;                   //y并不是整个界面的，而是绘制出的界面的
            if (y%2==0){
                x=(int)event.getX()/WIDTH;
            }else {
                x=(int)(event.getX()-WIDTH/2)/WIDTH;
            }
            if (x+1>COL||y+1>ROW){

                int i=(int) event.getX();
                int j=(int)event.getY();
                Log.d("ontouch", "i="+i+"   j="+j+"   "+getWidth()/2+"  "+(getBottom()-400));

                if ((getWidth()/2-64)<i&&i<(getWidth()/2+64)&&getBottom()-350<j&&j<getBottom()-222){
                    initGame();
                }
            }
            else if (getDot(x,y).getStatus()==Dot.STATUS_OFF){
                getDot(x,y).setStatus(Dot.STATUS_ON);
                move();
            }
            redraw();
        }
        return true;
    }

}
