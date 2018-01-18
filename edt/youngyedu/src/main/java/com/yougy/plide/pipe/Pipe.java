package com.yougy.plide.pipe;

import java.util.ArrayList;

/**
 * Created by FH on 2018/1/11.
 */

public class Pipe {
    private ArrayList<Ball> ballList = new ArrayList<Ball>();
    private MyThread mThread;
    private Ball lastRunBall;
    private boolean beenRecycled = false;

    public Pipe() {
        mThread = new MyThread();
        mThread.start();
    }

    public void push(Ball ball){
        synchronized (ballList){
            if (lastRunBall != null && lastRunBall.getTimeStamp() > ball.getTimeStamp()){
                return;
            }
            for (int i = 0; i < ballList.size() ; ) {
                Ball ballInList = ballList.get(i);
                if (ballInList.getTimeStamp() > ball.getTimeStamp()){
                    if (ballInList.needCancleOthers()){
                        return;
                    }
                    else {
                        ballList.add(i , ball);
                        ballList.notify();
                        if (ball.needCancleOthers()){
                            mThread.cancleCurrentBall();
                        }
                        else {
                            ballList.notify();
                        }
                        ball = null;
                        break;
                    }
                }
                else {
                    if (ball.needCancleOthers()){
                        ballList.remove(ballInList);
                    }
                    else {
                        i++;
                    }
                }
            }
            if (ball != null){
                ballList.add(ball);
                ballList.notify();
                if (ball.needCancleOthers()){
                    mThread.cancleCurrentBall();
                }
                else {
                    ballList.notify();
                }
            }
        }
    }

    public Ball pop(){
        synchronized (ballList){
            if (ballList.size() != 0){
                Ball ball = ballList.get(0);
                ballList.remove(0);
                return ball;
            }
            return null;
        }
    }

    public void recycle(){
        beenRecycled = true;
        mThread.cancleCurrentBall();
        mThread = null;
    }

    public void cancleCurrentBall(){
        mThread.cancleCurrentBall();
    }

    private class MyThread extends Thread {
        private Ball currentBall;

        public void cancleCurrentBall(){
            try {
                currentBall.cancle();
                mThread.interrupt();
            }
            catch (NullPointerException e){
                //此处的空指针可以忽略
            }
        }

        @Override
        public void run() {
            while (true){
                synchronized (ballList){
                    currentBall = pop();
                    while (currentBall == null){
                        try {
                            if (beenRecycled){
                                return;
                            }
                            ballList.wait();
                            currentBall = pop();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            if (beenRecycled){
                                return;
                            }
                        }
                    }
                    lastRunBall = currentBall;
                }
                if (!interrupted()){
                    try {
                        if (beenRecycled){
                            return;
                        }
                        currentBall.run();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        if (beenRecycled){
                            return;
                        }
                    }
                    finally {
                        currentBall = null;
                    }
                }
            }
        }
    }
}
