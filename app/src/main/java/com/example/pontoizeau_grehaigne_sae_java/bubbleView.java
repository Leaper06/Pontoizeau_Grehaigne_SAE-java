package com.example.pontoizeau_grehaigne_sae_java;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class bubbleView extends View {

    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private List<Bubble> bubbles = new ArrayList<>();
    private Random random = new Random();
    private Runnable runnable;

    public bubbleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        startAnimation();
    }

    private void startAnimation() {
        runnable = new Runnable() {
            @Override
            public void run() {
                addBubble();
                updateBubbles();
                invalidate();
                postDelayed(this, 30); // ~33 FPS
            }
        };
        post(runnable);
    }

    private void addBubble() {
        // On limite le nombre de bulles
        if (bubbles.size() < 30) {
            bubbles.add(new Bubble(getWidth(), getHeight(), random));
        }
    }

    private void updateBubbles() {
        Iterator<Bubble> it = bubbles.iterator();
        while (it.hasNext()) {
            Bubble b = it.next();
            b.y -= b.speedY; // monte vers le haut
            b.x += b.speedX; // léger mouvement horizontal
            b.radius *= 0.998; // léger rétrécissement

            // Supprime si la bulle sort de l'écran ou devient trop petite
            if (b.radius < 5 || b.y + b.radius < 0) {
                it.remove();
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Fond blanc
        canvas.drawColor(Color.WHITE);

        // Dessin des bulles
        for (Bubble b : bubbles) {
            paint.setColor(b.color);
            canvas.drawCircle(b.x, b.y, b.radius, paint);
        }
    }

    static class Bubble {
        float x, y;
        float radius;
        float speedY, speedX;
        int color;

        Bubble(int width, int height, Random random) {
            x = random.nextInt(width);
            y = height + random.nextInt(100); // commence en bas
            radius = 50 + random.nextInt(80); // grosses bulles
            speedY = 1 + random.nextFloat() * 3; // vitesse lente
            speedX = -1 + random.nextFloat() * 2; // léger mouvement horizontal

            // Couleur blanche semi-transparente façon savon
            int alpha = 60 + random.nextInt(80); // transparence 60~140/255
            color = Color.argb(alpha, 200, 220, 255);
        }
    }
}
