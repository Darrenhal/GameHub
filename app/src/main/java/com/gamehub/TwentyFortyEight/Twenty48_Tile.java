package com.gamehub.TwentyFortyEight;

import android.util.Log;

public class Twenty48_Tile {

    private static Twenty48_Color[] COLORS;

    static {
        COLORS = new Twenty48_Color[11];
        COLORS[0] = new Twenty48_Color(238, 228, 218);
        COLORS[1] = new Twenty48_Color(237, 224, 200);
        COLORS[2] = new Twenty48_Color(242, 177, 121);
        COLORS[3] = new Twenty48_Color(245, 149, 99);
        COLORS[4] = new Twenty48_Color(246, 124, 95);
        COLORS[5] = new Twenty48_Color(246, 94, 59);
        COLORS[6] = new Twenty48_Color(237, 207, 114);
        COLORS[7] = new Twenty48_Color(237, 204, 97);
        COLORS[8] = new Twenty48_Color(237, 200, 80);
        COLORS[9] = new Twenty48_Color(237, 197, 63);
        COLORS[10] = new Twenty48_Color(237, 194, 46);
    }

    public int value;
    public Twenty48_Color color;
    public int startX, startY, endX, endY;

    public Twenty48_Tile(int startX, int startY, int endX, int endY, int value) {
        this.value = 2;
        int index = (int) (Math.log(value)/Math.log(2));
        if (index < 10)  {
            this.color = COLORS[index-1];
        } else {
            this.color = COLORS[10];
        }
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.value = value;
    }

    public void upgrade() {

    }
}


