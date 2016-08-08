package com.dollarandtrump.angelcar;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    String mBastUrl ="http://angelcar.com/ios/api/ga_chatcar.php?operation=new&message=%s||%s||%s||%s||%s";
    @Test
    public void addition_isCorrect() throws Exception {
//        assertEquals("f",String.format(mBastUrl,"1","2","3","4","5"));
        for (int i = 10; i > 0 ; i--) {
            System.out.println(i);
            assertEquals(0,i);
        }
    }


}