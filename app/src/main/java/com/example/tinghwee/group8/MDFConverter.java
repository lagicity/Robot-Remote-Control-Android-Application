package com.example.tinghwee.group8;

import android.util.Log;
import java.util.ArrayList;

public class MDFConverter {
    public static final int UNEXPLORED = 0,
            OPEN = 1,
            OBSTACLE = 2;
    static int[][] _map = new int[20][15];

    public static ArrayList<Integer> hexToBin2(String mdf) {
        ArrayList<Integer> DecimalArr1 = new ArrayList();

        int i;
        for (i = 0; i < mdf.length(); i++) {
            //equivalent to multiplying the result by 16 and adding the value of the new digit, but uses bit operations for performance
            try{
                DecimalArr1.add(i,Character.digit(mdf.charAt(i), 16));
            }
            catch (Exception e){
                DecimalArr1.add(0);
            }
        }

        ArrayList BinaryArray = convertToBinary(DecimalArr1);
        return BinaryArray;
    }

    public static ArrayList convertToBinary(ArrayList decimalArr){
        ArrayList<Integer> BinaryArr2 = new ArrayList();

        int tempBINL = 0;
        int count = 0;
        for(int j = 0; j<decimalArr.size(); j++){
            tempBINL = (int) decimalArr.get(j);
            String tempBIN = Integer.toBinaryString(tempBINL);
            if (tempBIN.length() == 0){
                for (int p = 0; p< 4; p++) {
                    BinaryArr2.add(0);
                }
            }
            else if(tempBIN.length() == 1){
                for (int p =0; p<3; p++){
                    BinaryArr2.add(0);
                }
                BinaryArr2.add(Integer.parseInt(tempBIN));
            }
            else if (tempBIN.length() == 2){
                for (int p =0; p<2; p++){
                    BinaryArr2.add(0);
                }
                for (int k =0; k<2; k++) {
                    char bin = tempBIN.charAt(k);
                    int binn = Character.getNumericValue(bin);
                    BinaryArr2.add(binn);
                }
            }
            else if(tempBIN.length()== 3){
                BinaryArr2.add(0);
                for (int k =0; k<3; k++) {
                    char bin = tempBIN.charAt(k);
                    int binn = Character.getNumericValue(bin);
                    BinaryArr2.add(binn);
                }
            }
            else {
                for (int k =0; k<4; k++) {
                    char bin = tempBIN.charAt(k);
                    int binn = Character.getNumericValue(bin);
                    BinaryArr2.add(binn);
                }
            }
        }
        return BinaryArr2;
    }


    public static int[][] mdfToMap(byte[] bitstream) {

        byte curChar = bitstream[0];
        byte curChar2 = bitstream.length > 38 ? bitstream[38] : 0;

        int i = 5, j = 7;
        int x, y;
        int oi = 0, oj = 38;

        for (y = 0; y < 20; y++) {
            for (x = 0; x < 15; x++) {
                if ((curChar & (0x1 << i)) > 0) {
                    _map[19 - y][x] = (curChar2 & (0x1 << j)) > 0 ? OBSTACLE : OPEN;
                    j--;
                } else
                    _map[19 - y][x] = UNEXPLORED;
                i--;

                if (i < 0) {
                    curChar = bitstream[++oi];
                    i = 7;
                }
                if (j < 0) {
                    curChar2 = bitstream[++oj];
                    j = 7;
                }

            }
        }

        return _map;
    }
}
