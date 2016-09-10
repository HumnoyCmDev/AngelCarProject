package com.dollarandtrump.angelcar;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {

    @Test
    public void addition_isCorrect() throws Exception {

        Assert.assertArrayEquals(new int[]{960,540},solution(1600,900)); //แนวนอน
        Assert.assertArrayEquals(new int[]{540,960},solution(900,1600)); //แนวตั้ง

        Assert.assertArrayEquals(new int[]{960,540},solution(965,543)); //แนวนอน
        Assert.assertArrayEquals(new int[]{540,960},solution(543,965)); //แนวตั้ง

        Assert.assertArrayEquals(new int[]{800,450},solution(800,450)); //แนวนอน
        Assert.assertArrayEquals(new int[]{450,800},solution(450,800)); //แนวตั้ง

        Assert.assertArrayEquals(new int[]{960,960},solution(960,960));//สี่เหลี่ยม
        Assert.assertArrayEquals(new int[]{960,960},solution(970,970));//สี่เหลี่ยม
        Assert.assertArrayEquals(new int[]{540,540},solution(540,540));//สี่เหลี่ยม


    }

    public int[] solution(int width,int height){
        final int BASE_WIDTH = 960;
        if (width > BASE_WIDTH || height > BASE_WIDTH) {
            int solution;
            if (width > height){ //แนวนอน
                solution = (height * BASE_WIDTH)/width;
                return new int[]{BASE_WIDTH,solution};
            }else if (height > width){ // แนวตั้ง
                solution = (width * BASE_WIDTH)/height;
                return new int[]{solution,BASE_WIDTH};
            }else { //สี่เหลียม
                return new int[]{BASE_WIDTH,BASE_WIDTH};
            }
//            int max = Math.max(width, height);
//            int min = Math.min(width,height);
//            int solution = (min*BASE_WIDTH) / max;
//            return solution;
        }
        return new int[]{width,height};
    }



}