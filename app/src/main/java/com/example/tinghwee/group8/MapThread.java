package com.example.tinghwee.group8;

public class MapThread extends Thread {

    private final static int SLEEP_TIME = 300;

    private boolean running = false;
    private Map Arena = null;

    public MapThread(Map Arena) {
        super();
        this.Arena = Arena;
        super.start();
    }

    public void startThread() {
        running = true;
    }

    public void run() {
        try {
            while (true) {
                Arena.updateMap();
                Arena.postInvalidate();
                sleep(SLEEP_TIME);
                while(running == false) {
                    try {
                        sleep(SLEEP_TIME);
                    } catch (InterruptedException e) {}
                }
            }
        } catch (InterruptedException ie) {}
    }
}

