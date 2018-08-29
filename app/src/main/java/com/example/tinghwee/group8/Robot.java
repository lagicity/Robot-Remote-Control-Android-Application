package com.example.tinghwee.group8;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;
import android.widget.EditText;

import java.util.ArrayList;

public class Robot {
    //constant define
    private int SIZE = 20;
    private int x;
    private int y;
    private Paint paint = null;
    private int count = 0; //counting the rows

    private int[] gridArray;
    private Canvas tempCanvas;
    private ArrayList obstacleArray;
    private ArrayList unexploredArray;
    private ArrayList exploredArray;
    private ArrayList waypointArray;

    public static final int UP = 1;
    public static final int DOWN = 2;
    public static final int LEFT = 3;
    public static final int RIGHT = 4;

    public static final int MOVE_FORWARD = 5;
    public static final int MOVE_BACKWARD = 6;
    public static final int TURN_LEFT = 7;
    public static final int TURN_RIGHT = 8;

    public static int current_x = 0;
    public static int current_y = 0;
    public static int direction = UP;

    public Robot() {
        super();
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
    }

    public void drawCell(Canvas canvas, int i, int j, int color, int gridSize) {
        x  = (gridSize*i);
        y = (gridSize*j);
        SIZE = gridSize;

        paint.setColor(color);
        canvas.drawRect(new RectF(x - SIZE / 2, y - SIZE / 2, x + SIZE / 2, y + SIZE / 2), paint);
    }

    public void setCenterTo(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void drawMapString(Canvas canvas, int gridSize) {
        this.tempCanvas = canvas;

        int height= gridArray[0];
        int width = gridArray[1];
        int headX = gridArray[2];
        int headY = gridArray[3];
        int bodyX = gridArray[4];
        int bodyY = gridArray[5];

        boolean hori;

        if (headX == bodyX) {
            hori = false;
        }
        else {
            hori = true;
        }

        //drawing the gridMap
        for (int i=1; i <= width; i++) {
            for (int j=1; j <=height; j++) {
                drawCell(canvas, i, j, Color.parseColor("#FFFFFF"), gridSize);
            }
        }

        //Explored cells---------------------------------------------------------------------------
        for (int i = 0; i < exploredArray.size(); i++) {
            double keyValue = Math.ceil((int) (exploredArray.get(i)) / 15);
            int exploredX = (int) (20 - keyValue);
            int exploredY = (int) ((int) (exploredArray.get(i)) - (keyValue * 15));


            if (exploredY == 0) {
                drawCell(canvas, exploredX + 1, 15, Color.parseColor("#9A9898"), gridSize);
            } else {
                drawCell(canvas, exploredX, exploredY, Color.parseColor("#9A9898"), gridSize);
            }
        }

        //------------------------------------------------------------------------------------------
        //row 1 , col 3 (Start Zone)
        for (int i=1; i<=3;i++){
            for (int j=1;j<=3;j++){
                drawCell(canvas, i, j,Color.parseColor("#00ff19") , gridSize);
            }
        }
        // Finish Zone
        // col
        for (int i=18; i<=20;i++){
            // row
            for (int j=13;j<=15;j++){
                drawCell(canvas, i, j,Color.parseColor("#ff0000") , gridSize);
            }
        }
        //------------------------------------------------------------------------------------------
        // Draw Start Point - Horizontal
        if (hori == true) {
            // Draw Robot Rear  -1 , +2 , -2 , +1
            for (int i=bodyX-1; i<bodyX+2; i++) {
                for (int j=bodyY-2; j<bodyY+1; j++) {
                    drawCell(canvas, i, j, Color.parseColor("#003366"), gridSize);
                }
            }
            // Draw Robot Head 0 , 0 , -1 , +0
            if (headX>0 && headY>0) {
                for (int i=headY-1; i< headY+0; i++) {
                    drawCell(canvas, headX, i, Color.parseColor("#24cdd4"), gridSize);
                }
            }
        }
        else {
            //Vertical - Draw Robot Rear
            for (int i = bodyX - 2; i < bodyX + 1; i++) {
                for (int j = bodyY - 2; j < bodyY + 1; j++) {
                    drawCell(canvas, i, j, Color.parseColor("#003366"), gridSize);
                }
            }
            // Draw Robot Head
            if (headX>0 && headY>0) {
                for (int i=headX-1; i< headX+0; i++) {
                    drawCell(canvas, i, headY, Color.parseColor("#24cdd4"), gridSize);
                }
            }
        }
        //------------------------------------------------------------------------------------------
        //Draw Obstacles Grid
        if (obstacleArray != null) {
            for (int i = 0; i < obstacleArray.size(); i++) {
                double keyValue = Math.ceil((int) (obstacleArray.get(i)) / 15); //round up to whole number
                int temp = (int)obstacleArray.get(i);
                while (temp > 15) {
                    temp = temp - 15;
                    count++; //find out which row it is at
                }

                // Obstacles Colour
                if (count < 0) { //if the gridNo is less than 15
                    int obstacleY1 = ((int)(obstacleArray.get(i)));
                    drawCell(canvas, count +1, obstacleY1, Color.BLACK, gridSize);
                } else {
                    int obstacleY2 = (int) (((int)(obstacleArray.get(i))) - (count * 15));
                    drawCell(canvas, count +1, obstacleY2, Color.BLACK, gridSize);
                }
                count = 0;
            }
        }

        if (waypointArray == null || waypointArray.size() == 0) {
        } else {
            int waypointRow = Integer.parseInt(waypointArray.get(0).toString());
            int waypointColumn = Integer.parseInt(waypointArray.get(1).toString());
            drawCell(canvas, waypointRow, waypointColumn, Color.YELLOW, gridSize);
        }


        //------------------------------------------------------------------------------------------
        // Draw UnExplored Cells
        for (int i = 0; i < unexploredArray.size(); i++) {
            double keyValue = Math.ceil((int) (unexploredArray.get(i)) / 15);
            int exploredX = (int)(20 - keyValue);
            int exploredY = (int)((int)(unexploredArray.get(i)) - (keyValue * 15));

            // Unexplored Colour
            if(exploredY == 0){
                drawCell(canvas, exploredX + 1, 15, Color.parseColor("#FFFFFF"), gridSize);
            }
            else{
                drawCell(canvas, exploredX, exploredY, Color.parseColor("#FFFFFF"), gridSize);
            }
        }
    }
    //------------------------------------------------------------------------------------------
    //cannot remove below, it's re-configuring the map after update.
    public void setGridArray(int[] gridArray) {
        this.gridArray = gridArray;
    }

    public void setObstacleArray(ArrayList obstacleArray) {
        this.obstacleArray = obstacleArray;
    }
    public void setUnExploredArray(ArrayList unexploredArray) {
        this.unexploredArray = unexploredArray;
    }
    public void setExploredArray(ArrayList exploredArray) {
        this.exploredArray = exploredArray;
    }
    public void setWaypointArray(ArrayList waypointArray) {
        this.waypointArray = waypointArray;
    }

    public void updatePosition(int action) {
        if (action == MOVE_FORWARD) {
            if (direction == UP) {
                current_y += 1;
            } else if (direction == DOWN) {
                current_y -= 1;
            } else if (direction == LEFT) {
                current_x -= 1;
            } else if (direction == RIGHT) {
                current_x += 1;
            }
        } else if (action == MOVE_BACKWARD) {
            if (direction == UP) {
                current_y -= 1;
            } else if (direction == DOWN) {
                current_y += 1;
            } else if (direction == LEFT) {
                current_x += 1;
            } else if (direction == RIGHT) {
                current_x -= 1;
            }
        } else if (action == TURN_LEFT) {
            if (direction == UP) {
                direction = LEFT;
            } else if (direction == DOWN) {
                direction = RIGHT;
            } else if (direction == LEFT) {
                direction = DOWN;
            } else if (direction == RIGHT) {
                direction = UP;
            }
        } else if (action == TURN_RIGHT) {
            if (direction == UP) {
                direction = RIGHT;
            } else if (direction == DOWN) {
                direction = LEFT;
            } else if (direction == LEFT) {
                direction = UP;
            } else if (direction == RIGHT) {
                direction = DOWN;
            }
        }
    }

    public void moveForward() {
        updatePosition(MOVE_FORWARD);
    }

    public void moveBackward() {
        updatePosition(MOVE_BACKWARD);
    }

    public void turnLeft() {
        updatePosition(TURN_LEFT);
    }

    public void turnRight() {
        updatePosition(TURN_RIGHT);
    }


    public void setDirection(int dir) {
        direction = dir;
    }

    public void setCurrentX(int x) {
        current_x = x;
    }

    public void setCurrentY(int y) {
        current_y = y;
    }

    public int getCurrentX() {
        return current_x;
    }

    public int getCurrentY() {
        return current_y;
    }

    public int getDirection() {
        return direction;
    }
}

