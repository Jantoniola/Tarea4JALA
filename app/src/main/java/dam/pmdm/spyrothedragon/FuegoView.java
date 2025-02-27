package dam.pmdm.spyrothedragon;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Random;

import dam.pmdm.spyrothedragon.models.Particula;

public class FuegoView extends View {
    private ArrayList<Particula> particulas = new ArrayList<>();
    private Paint paint;
    private Random random;
    private final float VELOCIDADX = 1;
    private final float VELOCIDADY = 50;
private float posX=0, posY=0;
    public FuegoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        particulas.clear();
        random = new Random();
        paint = new Paint();
    }

    public void reiniciar(int x, int y) {

        particulas.clear();
        posX=x+210f;
        posY=y+160f;

    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        float rand = random.nextFloat();

        /*
        Primero modificamos las partículas existentes
        despues agregamos una nueva partícula
        visualizamos todas las partículas
         */
        modificacionParticulas();
        crearNuevaParticula();

        for (Particula p : particulas) {

            int color = Color.argb(p.getAlfa(), p.getRed(), p.getGreen(), p.getBlue());
            paint.setColor(color);

            canvas.drawCircle(p.getX(), p.getY(), p.getRadio(), paint);

        }
        postInvalidateDelayed(100);
    }

    private void crearNuevaParticula() {
        //float x =231, y = 460, velocidadX, velocidadY;
        float x =posX, y = posY, velocidadX, velocidadY;
        int radio = 50;
        velocidadX = ((random.nextFloat()) - 0.5f) * 35;
        velocidadY = (random.nextFloat()) * 45;
        Particula particula = new Particula(x, y, velocidadX, velocidadY);
        particulas.add(particula);
    }

    private void modificacionParticulas() {
        for (Particula p : particulas) {
            p.setX(p.getX() + p.getVelocidadX());
            p.setY(p.getY() + p.getVelocidadY());
            if (p.getAlfa() - 35 > 0) {
                p.setAlfa(p.getAlfa() - 40);
            } else {
                p.setAlfa(0);
            }
            if (p.getGreen() - 50 > 0) {
                p.setGreen(p.getGreen() - 50);
            } else {
                p.setGreen(0);
            }
            p.setRadio(p.getRadio() + 5);
        }
    }
}
