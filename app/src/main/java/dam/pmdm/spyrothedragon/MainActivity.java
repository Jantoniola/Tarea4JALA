package dam.pmdm.spyrothedragon;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import dam.pmdm.spyrothedragon.databinding.ActivityMainBinding;
import dam.pmdm.spyrothedragon.databinding.GuiaBienvenidaBinding;
import dam.pmdm.spyrothedragon.databinding.GuiaColeccionablesBinding;
import dam.pmdm.spyrothedragon.databinding.GuiaInformacionBinding;
import dam.pmdm.spyrothedragon.databinding.GuiaMundosBinding;
import dam.pmdm.spyrothedragon.databinding.GuiaPersonajesBinding;
import dam.pmdm.spyrothedragon.databinding.GuiaResumenBinding;
import dam.pmdm.spyrothedragon.databinding.FuegoBinding;


public class MainActivity extends AppCompatActivity {
    private FuegoBinding fuegoBinding;
    private GuiaBienvenidaBinding guiaBienvenidaBinding;
    private GuiaPersonajesBinding guiaPersonajesBinding;
    private GuiaMundosBinding guiaMundosBinding;
    private GuiaColeccionablesBinding guiaColeccionablesBinding;
    private GuiaInformacionBinding guiaInformacionBinding;
    private GuiaResumenBinding guiaResumenBinding;
    private ActivityMainBinding binding;
    private NavController navController = null;
    // Lo igualaremos a lo guardado en sharedPreferent
    private boolean needguide = true;
    private SoundPool sonidos;
    int avanzar, retroceder;
    private MediaPlayer mediaPlayer;
    private boolean puedeRetroceder = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Asignamos a las variables tipo binding creadas cada uno de los layout
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        fuegoBinding=binding.includeFuego;
        guiaBienvenidaBinding = binding.includeBienvenida;
        guiaPersonajesBinding = binding.includePersonajes;
        guiaMundosBinding = binding.includeMundos;
        guiaColeccionablesBinding = binding.includeColeccionable;
        guiaInformacionBinding = binding.includeInformacion;
        guiaResumenBinding = binding.includeResumen;


        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        Fragment navHostFragment = getSupportFragmentManager().findFragmentById(R.id.navHostFragment);
        if (navHostFragment != null) {
            navController = NavHostFragment.findNavController(navHostFragment);
            NavigationUI.setupWithNavController(binding.navView, navController);
            NavigationUI.setupActionBarWithNavController(this, navController);
        }

        binding.navView.setOnItemSelectedListener(this::selectedBottomMenu);
        float c = binding.navView.getTop();

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.navigation_characters ||
                    destination.getId() == R.id.navigation_worlds ||
                    destination.getId() == R.id.navigation_collectibles ||
                    destination.getId() == R.id.videoFragment) {
                // Para las pantallas de los tabs, no queremos que aparezca la flecha de atrás
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            } else {
                // Si se navega a una pantalla donde se desea mostrar la flecha de atrás, habilítala
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        });


        /*
        En el caso de que entremos en la guia, debemos desactivar la posibilidad de que se retroceda
        con el botón de retroceso ya que cuando se hace, actúa sobre la aplicacion
        principal y no actúa sobre las acciones y animaciones que tenemos en la guia.
        Debemos volver a activarla al salir de la guia.
        Como está la bandera inicializada a true, si no entramos en la guia tenemos por defecto el botón
        de retroceso activo.
         */

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (puedeRetroceder) {
                    // Si el botón de retroceder está habilitado, permite el comportamiento predeterminado
                    setEnabled(false); // Desactiva el callback temporalmente
                    MainActivity.super.onBackPressed(); // Llama al comportamiento predeterminado
                } else {
                    // Si está desactivado, no hace nada
                }
            }
        };

        // Registrar el callback en el dispatcher
        getOnBackPressedDispatcher().addCallback(this, callback);

        // savedInstanceState será null al iniciar la aplicación, pero tendrá datos en el caso de que paso por aquí al girar el movil, por ejemplo
        // De lo contrario en el caso de girar el móvil, iniciaría la guía o al menos comprobaría si debe hacerlo.
        if (savedInstanceState == null) {
            // Si no se ha visto la guia antes (SharePreferent) visualiza la guia
            SharedPreferences sharedPreferences = getSharedPreferences("Preferencias", MODE_PRIVATE);
            needguide = sharedPreferences.getBoolean("guide", true);
            if (needguide) {
                puedeRetroceder = false;
                //Cargamos y reproducimos la cancion de Spyro de forma cíclica
                mediaPlayer = MediaPlayer.create(this, R.raw.cancionspyro);
                mediaPlayer.setLooping(true);
                mediaPlayer.start();

                // Cargamos dentro de la SoundPool los sonidos que asignaremos a los botones de avanzar y retroceder
                sonidos = new SoundPool.Builder().setMaxStreams(2).build();
                avanzar = sonidos.load(this, R.raw.avanzar, 1);
                retroceder = sonidos.load(this, R.raw.atras, 1);

                // Esperamos hasta que estén cargados los sonidos. Cuando lo estén continuamos con la guia
                sonidos.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                    @Override
                    public void onLoadComplete(SoundPool soundPool, int i, int i1) {
                        if (i1 == 0) {

                            // Creamos todos los listener para no crearlos más de una vez al avanzar y retroceder en la guia
                            inicializaBotones();

                            // Visualizamos la guia.
                            visualizalaguia();
                        }
                    }
                });

            }
        }
    }

    private void inicializaBotones() {
        guiaBienvenidaBinding.exitGuide.setOnClickListener(this::salirguia);
        guiaBienvenidaBinding.botonComenzar.setOnClickListener(this::entrarGuia);
        guiaPersonajesBinding.avanzar.setOnClickListener(this::irMundos);
        guiaPersonajesBinding.exitGuide.setOnClickListener(this::salirguia);
        guiaMundosBinding.avanzar.setOnClickListener(this::irColeccionables);
        guiaMundosBinding.atras.setOnClickListener(this::irPersonajes);
        guiaMundosBinding.exitGuide.setOnClickListener(this::salirguia);
        guiaColeccionablesBinding.avanzar.setOnClickListener(this::irInformacion);
        guiaColeccionablesBinding.atras.setOnClickListener(this::volverMundos);
        guiaColeccionablesBinding.exitGuide.setOnClickListener(this::salirguia);
        guiaInformacionBinding.avanzar.setOnClickListener(this::irInformacion);
        guiaInformacionBinding.atras.setOnClickListener(this::volverColeccionables);
        guiaInformacionBinding.exitGuide.setOnClickListener(this::salirguia);
    }


    private void visualizalaguia() {
        guiaBienvenidaBinding.guiaBienvenida.setVisibility(View.VISIBLE);

    }

    private void entrarGuia(View view) {
        if (sonidos != null) {
            sonidos.play(avanzar, 1f, 1f, 0, 0, 1f);
        }
        guiaBienvenidaBinding.guiaBienvenida.setVisibility(View.GONE);
        pantallaPersonajes();
    }

    // Aquí entramos en al pantalla de personajes con su animación correspondiente
    private void pantallaPersonajes() {
        guiaPersonajesBinding.guiaPersonajes.setVisibility(View.VISIBLE);

        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(guiaPersonajesBinding.textoguia, "alpha", 0f, 1f);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(guiaPersonajesBinding.marcador, "scaleX", 1f, 0.5f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(guiaPersonajesBinding.marcador, "scaleY", 1f, 0.5f);
        guiaPersonajesBinding.marcador.setX(-78f);
        scaleX.setRepeatCount(3);
        scaleY.setRepeatCount(3);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet
                .play(scaleX)
                .with(scaleY)
                .before(fadeIn);
        animatorSet.setDuration(800);
        animatorSet.start();
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                ObjectAnimator desplazar = ObjectAnimator.ofFloat(guiaPersonajesBinding.avanzar, "translationY", 0, binding.navHostFragment.getHeight() / 2);

                AnimatorSet animatorSet1 = new AnimatorSet();
                animatorSet1.play(desplazar);
                animatorSet1.setInterpolator(new OvershootInterpolator());
                animatorSet1.start();


            }
        });
    }

    // Entramos en la parte de la guia en la que mostramos la ventana de Acerca de...
    private void irInformacion(View view) {
        if (sonidos != null) {
            sonidos.play(avanzar, 1f, 1f, 0, 0, 1f);
        }
    }

    // Entramos en la parte de la guia en la que enseñamos la pestaña Mundos
    private void irMundos(View view) {
        if (sonidos != null) {
            sonidos.play(avanzar, 1f, 1f, 0, 0, 1f);
        }
        ObjectAnimator desplazarsigu = ObjectAnimator.ofFloat(guiaPersonajesBinding.avanzar, "translationY", binding.navHostFragment.getHeight() / 2f, 0);
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(guiaPersonajesBinding.textoguia, "alpha", 1f, 0f);
        ObjectAnimator desplazar = ObjectAnimator.ofFloat(guiaPersonajesBinding.marcador, "translationX", 0, (binding.navView.getWidth() / 3) - 78f);
        AnimatorSet animatorSet1 = new AnimatorSet();
        animatorSet1
                .play(fadeOut)
                .with(desplazarsigu)
                .before(desplazar);
        //animatorSet1.setInterpolator(new OvershootInterpolator());
        animatorSet1.setDuration(1000);
        animatorSet1.start();
        animatorSet1.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                navController.navigate(R.id.action_navigation_characters_to_navigation_worlds);
                guiaPersonajesBinding.guiaPersonajes.setVisibility(View.GONE);
                pantallaMundos();
            }
        });

    }

    //Pulsamos el botón de Volver desde Coleccionables
    private void volverMundos(View view) {
        if (sonidos != null) {
            sonidos.play(retroceder, 1f, 1f, 0, 0, 1f);
        }
        ObjectAnimator desplazarsigu = ObjectAnimator.ofFloat(guiaColeccionablesBinding.avanzar, "translationY", binding.navHostFragment.getHeight() / 2f, 0);
        ObjectAnimator desplazarAtr = ObjectAnimator.ofFloat(guiaColeccionablesBinding.atras, "translationY", binding.navHostFragment.getHeight() / 2f, 0);
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(guiaColeccionablesBinding.textoguia, "alpha", 1f, 0f);
        ObjectAnimator desplazar = ObjectAnimator.ofFloat(guiaColeccionablesBinding.marcador, "translationX", 0, (binding.navView.getWidth() / -3) + 78f);
        AnimatorSet animatorSet1 = new AnimatorSet();
        animatorSet1
                .play(fadeOut)
                .with(desplazarsigu)
                .with(desplazarAtr)
                .before(desplazar);
        animatorSet1.setDuration(1000);
        animatorSet1.start();
        animatorSet1.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                navController.navigate(R.id.action_navigation_collectibles_to_navigation_worlds);
                guiaColeccionablesBinding.guiaColeccionables.setVisibility(View.GONE);
                guiaColeccionablesBinding.marcador.setTranslationX(0f);
                pantallaMundos();
            }
        });
    }

    // Volvemos desde la pantalla de la guia que muestra el Acerca de
    private void volverColeccionables(View view) {
        if (sonidos != null) {
            sonidos.play(retroceder, 1f, 1f, 0, 0, 1f);
        }
    }

    // Vamos a la pantalla de la guia que explica la pestaña de Personajes
    private void irPersonajes(View view) {
        if (sonidos != null) {
            sonidos.play(retroceder, 1f, 1f, 0, 0, 1f);
        }
        ObjectAnimator desplazarsigu = ObjectAnimator.ofFloat(guiaMundosBinding.avanzar, "translationY", binding.navHostFragment.getHeight() / 2f, 0);
        ObjectAnimator desplazarAtr = ObjectAnimator.ofFloat(guiaMundosBinding.atras, "translationY", binding.navHostFragment.getHeight() / 2f, 0);
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(guiaMundosBinding.textoguia, "alpha", 1f, 0f);
        ObjectAnimator desplazar = ObjectAnimator.ofFloat(guiaMundosBinding.marcador, "translationX", 0, binding.navView.getWidth() / -3f);
        AnimatorSet animatorSet1 = new AnimatorSet();
        animatorSet1
                .play(fadeOut)
                .with(desplazarsigu)
                .with(desplazarAtr)
                .before(desplazar);
        animatorSet1.setInterpolator(new OvershootInterpolator());
        animatorSet1.setDuration(1000);
        animatorSet1.start();
        animatorSet1.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                navController.navigate(R.id.action_navigation_worlds_to_navigation_characters);
                guiaMundosBinding.guiaMundos.setVisibility(View.GONE);
                guiaMundosBinding.marcador.setTranslationX(0f);
                pantallaPersonajes();
            }
        });


    }

    // Pulsamos el botón de avanzar en la pantalla de la guia Mundos para que avance hasta coleccionables
    private void irColeccionables(View view) {
        if (sonidos != null) {
            sonidos.play(avanzar, 1f, 1f, 0, 0, 1f);
        }
        ObjectAnimator desplazarsigu = ObjectAnimator.ofFloat(guiaMundosBinding.avanzar, "translationY", binding.navHostFragment.getHeight() / 2f, 0);
        ObjectAnimator desplazarAtr = ObjectAnimator.ofFloat(guiaMundosBinding.atras, "translationY", binding.navHostFragment.getHeight() / 2f, 0);
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(guiaMundosBinding.textoguia, "alpha", 1f, 0f);
        ObjectAnimator desplazar = ObjectAnimator.ofFloat(guiaMundosBinding.marcador, "translationX", 0, binding.navView.getWidth() / 3f);
        AnimatorSet animatorSet1 = new AnimatorSet();
        animatorSet1
                .play(fadeOut)
                .with(desplazarsigu)
                .with(desplazarAtr)
                .before(desplazar);
        animatorSet1.setInterpolator(new OvershootInterpolator());
        animatorSet1.setDuration(1000);
        animatorSet1.start();
        animatorSet1.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                navController.navigate(R.id.action_navigation_worlds_to_navigation_collectibles);
                guiaMundosBinding.guiaMundos.setVisibility(View.GONE);
                guiaMundosBinding.marcador.setTranslationX(0f);
                pantallaColeccionables();
            }
        });
    }

    // Entramos en la pantalla coleccionables
    private void pantallaColeccionables() {

        guiaColeccionablesBinding.guiaColeccionables.setVisibility(View.VISIBLE);
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(guiaColeccionablesBinding.textoguia, "alpha", 0f, 1f);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(guiaColeccionablesBinding.marcador, "scaleX", 1f, 0.5f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(guiaColeccionablesBinding.marcador, "scaleY", 1f, 0.5f);
        guiaColeccionablesBinding.marcador.setTranslationX(78f);
        scaleX.setRepeatCount(3);
        scaleY.setRepeatCount(3);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet
                .play(scaleX)
                .with(scaleY)
                .before(fadeIn);
        animatorSet.setDuration(800);
        animatorSet.start();
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                ObjectAnimator desplazar = ObjectAnimator.ofFloat(guiaColeccionablesBinding.avanzar, "translationY", 0, binding.navHostFragment.getHeight() / 2);
                ObjectAnimator desplazarAtras = ObjectAnimator.ofFloat(guiaColeccionablesBinding.atras, "translationY", 0, binding.navHostFragment.getHeight() / 2);
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet
                        .play(desplazarAtras)
                        .with(desplazar);
                animatorSet.setDuration(800);
                animatorSet.start();
            }
        });

    }

    // Entramos en la parte de la guia que explica la pestaña de Mundos
    private void pantallaMundos() {
        guiaMundosBinding.guiaMundos.setVisibility(View.VISIBLE);
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(guiaMundosBinding.textoguia, "alpha", 0f, 1f);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(guiaMundosBinding.marcador, "scaleX", 1f, 0.5f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(guiaMundosBinding.marcador, "scaleY", 1f, 0.5f);

        scaleX.setRepeatCount(3);
        scaleY.setRepeatCount(3);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet
                .play(scaleX)
                .with(scaleY)
                .before(fadeIn);
        animatorSet.setDuration(800);
        animatorSet.start();
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                ObjectAnimator desplazar = ObjectAnimator.ofFloat(guiaMundosBinding.avanzar, "translationY", 0, binding.navHostFragment.getHeight() / 2);
                ObjectAnimator desplazarAtras = ObjectAnimator.ofFloat(guiaMundosBinding.atras, "translationY", 0, binding.navHostFragment.getHeight() / 2);
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet
                        .play(desplazarAtras)
                        .with(desplazar);
                animatorSet.setDuration(800);
                animatorSet.start();
            }
        });
    }


    // Salimos de la guia
    // Escribimos en los SharedPreferent para que no vuelva a mostrarse la guia
    // Liberamos el MediaPlayer.
    // Después de reproducir el último sonido liberamos también el SoundPool
    private void salirguia(View view) {
        SharedPreferences sharedPreferences = getSharedPreferences("Preferencias", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("guide", false);
        //*********************Volver a activar despues de las pruebas
        //editor.apply();
        //Ponemos el primer elemento del menu
        selectedBottomMenu(binding.navView.getMenu().getItem(0));

        if (sonidos != null) {
            sonidos.play(retroceder, 1f, 1f, 0, 0, 1f);
            sonidos.release();
        }

        if (mediaPlayer != null) {
            mediaPlayer.release();
        }

        // escondemos la guia sea cual sea la pantalla visualizada
        guiaBienvenidaBinding.guiaBienvenida.setVisibility(View.GONE);
        guiaPersonajesBinding.guiaPersonajes.setVisibility(View.GONE);
        guiaMundosBinding.guiaMundos.setVisibility(View.GONE);
        guiaColeccionablesBinding.guiaColeccionables.setVisibility(View.GONE);
        guiaInformacionBinding.guiaInformacion.setVisibility(View.GONE);
        guiaResumenBinding.guiaResumen.setVisibility(View.GONE);

        // Volvemos a activar el botón de retroceso

        puedeRetroceder = true;


    }

    private boolean selectedBottomMenu(@NonNull MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.nav_characters)
            navController.navigate(R.id.navigation_characters);
        else if (menuItem.getItemId() == R.id.nav_worlds)
            navController.navigate(R.id.navigation_worlds);
        else
            navController.navigate(R.id.navigation_collectibles);
        return true;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Infla el menú
        getMenuInflater().inflate(R.menu.about_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Gestiona el clic en el ítem de información
        if (item.getItemId() == R.id.action_info) {
            showInfoDialog();  // Muestra el diálogo
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showInfoDialog() {
        // Crear un diálogo de información
        new AlertDialog.Builder(this)
                .setTitle(R.string.title_about)
                .setMessage(R.string.text_about)
                .setPositiveButton(R.string.accept, null)
                .show();
    }
public void visualizaFuego(int x, int y){
        fuegoBinding.fuegoId.reiniciar(x,y);
        fuegoBinding.layoutFuego.setVisibility(View.VISIBLE);
        fuegoBinding.layoutFuego.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fuegoBinding.layoutFuego.setVisibility(View.GONE);
            }
        });

}

}