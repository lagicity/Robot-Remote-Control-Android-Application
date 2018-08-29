package com.example.tinghwee.group8;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.ArrayList;


public class Map extends View implements View.OnTouchListener{
    private static final String TAG = "Array";
    private static final int SIZE = 35;
    private int gridSize;
    private int numCol = 20;
    private int count = 0;

    private Robot robot;
    private int[] gridArray;
    private MapThread thread;
    private ArrayList obstacleArray;
    private ArrayList unexploredArray;
    private ArrayList exploredArray;
    private ArrayList waypointArray;

    private ArrayList newobstacleArray = new ArrayList(); //to hold new obstacleArray

    public static int cellCol, cellRow;

    public interface Listener {
        public void onCollide();
        public void onWaypointHit();
    }

    public Map(Context context, int[] array) {
        super(context);
        robot = new Robot();
        thread = new MapThread(this);
        thread.startThread();
    }
    public boolean onTouch(View v, MotionEvent event){
        float xpoint = event.getX();
        float ypoint = event.getY();

        // Convert xpoint and ypoint to cell coordinate
        cellCol = 1 + (int) Math.floor(xpoint / (getWidth()/20));
        cellRow = 1 + (int) Math.floor((getHeight() - ypoint) / (getHeight()/15));

        System.out.println(cellCol);
        System.out.println(cellRow);
        return false;
    }

    @Override
    public void onDraw(Canvas canvas) { //splitting the map into columns
        RelativeLayout mapView = (RelativeLayout) getRootView().findViewById(R.id.mapView);
        canvas.drawColor(Color.BLACK);
        gridSize = ((mapView.getMeasuredWidth()) - (mapView.getMeasuredWidth() / numCol)) / numCol;
        // Log.d("TESTING", "VALUE : " + mapView.getMeasuredWidth());
        robot.drawMapString(canvas, gridSize);
    }

    public void setGridArray(int[] gridArray) {
        this.gridArray = gridArray;
    }

    public void setObstacleArray(ArrayList obstacleArray) {
        newobstacleArray.clear();
            for (int i = 0; i < obstacleArray.size(); i++) {
                int gridNo = ((int) obstacleArray.get(i));
                int temp = (gridNo + 2); //AMDTOOL and Nexus difference by 2 gridsquares
                int temp2 = (gridNo + 2);
                int androidGridNo = temp;
                if (temp <= 15){
                    count = 0;
                }
                else {
                    while (temp2 > 15) {
                        temp2 = temp2 - 15;
                        count++; //find out which row it is at
                    }
                }
                newobstacleArray.add(androidGridNo); //use a new array to hold the obstacleArray
                System.out.println(newobstacleArray);
                count = 0;
            }
        this.obstacleArray = newobstacleArray;
    }

    public void setUnExploredArray(ArrayList unexploredArray) {
        this.unexploredArray = unexploredArray;
    }

    public void setExploredArray(ArrayList exploredArray) {
        int temp;
        ArrayList addedExploredArray = new ArrayList();
        for (int i = 0; i < exploredArray.size(); i++) {
            temp = Integer.parseInt(exploredArray.get(i).toString()) + 1;
            addedExploredArray.add(temp);
        }
        //Log.d("debug", "explored array + 2 = " + addedExploredArray);
        this.exploredArray = addedExploredArray ;
    }

    public void setWaypointArray(ArrayList waypointArray) {
        this.waypointArray = waypointArray;
    }

    public void updateMap() {
        robot.setGridArray(gridArray);
        robot.setObstacleArray(obstacleArray);
        robot.setUnExploredArray(unexploredArray);
        robot.setExploredArray(exploredArray);
        if (waypointArray != null)
            robot.setWaypointArray(waypointArray);
    }
}
