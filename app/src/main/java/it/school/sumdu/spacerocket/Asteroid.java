package it.school.sumdu.spacerocket;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.Random;

public class Asteroid {
    Bitmap asteroids[] = new Bitmap[4];
    int spikeFrame = 0;
    int spikeX, spikeY, spikeVelociy;
    Random random;

    public Asteroid(Context context){
        asteroids[0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.asteroid1);
        asteroids[1] = BitmapFactory.decodeResource(context.getResources(), R.drawable.asteroid2);
        asteroids[2] = BitmapFactory.decodeResource(context.getResources(), R.drawable.asteroid3);
        asteroids[3] = BitmapFactory.decodeResource(context.getResources(), R.drawable.asteroid4);
        random = new Random();
        resetPosition();
    }

    public Bitmap getSpike(int spikeFrame){
        return asteroids[spikeFrame];
    }

    public int getSpikeWidth(){
        return asteroids[0].getWidth();
    }

    public int getSpikeHeight(){
        return asteroids[0].getHeight();
    }

    public void resetPosition(){
        spikeX = random.nextInt(GameView.dWidth - getSpikeWidth());
        spikeY = -200 + random.nextInt(600) * -1;
        spikeVelociy = 35 + random.nextInt(16);
    }
}
