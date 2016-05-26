package com.dollarandtrump.angelcar.view.snappy;

/**
 * Created by humnoyDeveloper on 24/5/59.
 */
public interface ISnappyLayoutManager {
    int getPositionForVelocity(int velocityX, int velocityY);
    int getFixScrollPos();
}
