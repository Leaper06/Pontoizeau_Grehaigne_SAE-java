package com.example.pontoizeau_grehaigne_sae_java;
// Déclare le package dans lequel se trouve la classe

import android.content.Context;
// Permet d'utiliser le contexte Android

import android.graphics.Canvas;
// Permet de dessiner sur une surface graphique

import android.graphics.Color;
// Permet d'utiliser des couleurs

import android.graphics.Paint;
// Permet de définir comment on dessine (couleur, style, etc.)

import android.util.AttributeSet;
// Permet de récupérer les attributs XML de la vue

import android.view.View;
// Classe de base pour créer une vue personnalisée

import java.util.ArrayList;
// Permet d'utiliser une liste dynamique

import java.util.Iterator;
// Permet de parcourir une collection avec suppression sécurisée

import java.util.List;
// Interface List

import java.util.Random;
// Permet de générer des valeurs aléatoires

public class bubbleView extends View {
// Déclare une classe bubbleView qui hérite de View

    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    // Déclare et instancie un objet Paint avec l'anti-aliasing activé

    private List<Bubble> bubbles = new ArrayList<>();
    // Déclare et instancie une liste de bulles

    private Random random = new Random();
    // Déclare et instancie un générateur de nombres aléatoires

    private Runnable runnable;
    // Déclare un Runnable pour gérer l'animation

    public bubbleView(Context context, AttributeSet attrs) {
        // Constructeur de la vue appelé depuis le XML
        super(context, attrs);
        // Appelle le constructeur de la classe View
        startAnimation();
        // Lance l'animation des bulles
    }

    private void startAnimation() {
        // Méthode qui initialise et démarre l'animation
        runnable = new Runnable() {
            // Instancie un Runnable
            @Override
            public void run() {
                // Méthode exécutée à chaque frame
                addBubble();
                // Ajoute une nouvelle bulle si possible
                updateBubbles();
                // Met à jour la position et la taille des bulles
                invalidate();
                // Demande le redessin de la vue
                postDelayed(this, 30); // ~33 FPS
                // Relance le Runnable après 30 ms
            }
        };
        post(runnable);
        // Lance le Runnable
    }

    private void addBubble() {
        // Méthode qui ajoute une bulle
        // On limite le nombre de bulles
        if (bubbles.size() < 30) {
            // Vérifie qu'il y a moins de 30 bulles
            bubbles.add(new Bubble(getWidth(), getHeight(), random));
            // Instancie une nouvelle bulle et l'ajoute à la liste
        }
    }

    private void updateBubbles() {
        // Méthode qui met à jour les bulles
        Iterator<Bubble> it = bubbles.iterator();
        // Déclare un itérateur pour parcourir la liste
        while (it.hasNext()) {
            // Tant qu'il reste des bulles
            Bubble b = it.next();
            // Récupère la bulle suivante
            b.y -= b.speedY; // monte vers le haut
            // Diminue la position verticale de la bulle
            b.x += b.speedX; // léger mouvement horizontal
            // Modifie la position horizontale de la bulle
            b.radius *= 0.998; // léger rétrécissement
            // Réduit légèrement le rayon de la bulle

            // Supprime si la bulle sort de l'écran ou devient trop petite
            if (b.radius < 5 || b.y + b.radius < 0) {
                // Vérifie si la bulle est trop petite ou hors de l'écran
                it.remove();
                // Supprime la bulle de la liste
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // Méthode appelée pour dessiner la vue
        super.onDraw(canvas);
        // Appelle le dessin par défaut de la vue

        // Fond blanc
        canvas.drawColor(Color.WHITE);
        // Remplit le fond avec la couleur blanche

        // Dessin des bulles
        for (Bubble b : bubbles) {
            // Parcourt toutes les bulles
            paint.setColor(b.color);
            // Définit la couleur de la bulle
            canvas.drawCircle(b.x, b.y, b.radius, paint);
            // Dessine un cercle représentant la bulle
        }
    }

    static class Bubble {
        // Déclare une classe interne Bubble
        float x, y;
        // Déclare les coordonnées de la bulle
        float radius;
        // Déclare le rayon de la bulle
        float speedY, speedX;
        // Déclare les vitesses verticale et horizontale
        int color;
        // Déclare la couleur de la bulle

        Bubble(int width, int height, Random random) {
            // Constructeur de la bulle
            x = random.nextInt(width);
            // Initialise la position horizontale aléatoirement
            y = height + random.nextInt(100); // commence en bas
            // Initialise la position verticale sous l'écran
            radius = 50 + random.nextInt(80); // grosses bulles
            // Initialise le rayon de la bulle
            speedY = 1 + random.nextFloat() * 3; // vitesse lente
            // Initialise la vitesse verticale
            speedX = -1 + random.nextFloat() * 2; // léger mouvement horizontal
            // Initialise la vitesse horizontale

            // Couleur blanche semi-transparente façon savon
            int alpha = 60 + random.nextInt(80); // transparence 60~140/255
            // Initialise la transparence
            color = Color.argb(alpha, 200, 220, 255);
            // Initialise la couleur de la bulle
        }
    }
}
