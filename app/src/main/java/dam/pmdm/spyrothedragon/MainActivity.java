package dam.pmdm.spyrothedragon;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ActionBarOverlayLayout;
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

public class MainActivity extends AppCompatActivity {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
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
                    destination.getId() == R.id.navigation_collectibles) {
                // Para las pantallas de los tabs, no queremos que aparezca la flecha de atrás
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            } else {
                // Si se navega a una pantalla donde se desea mostrar la flecha de atrás, habilítala
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        });

        // Si no se ha visto la guia antes (SharePreferent) visualiza la guia
        SharedPreferences sharedPreferences = getSharedPreferences("Preferencias", MODE_PRIVATE);
        needguide = sharedPreferences.getBoolean("guide", true);
        if (needguide) {
            inicializaBotones();
            visualizalaguia();
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
        guiaBienvenidaBinding.guiaBienvenida.setVisibility(View.GONE);
        pantallaPersonajes();
    }

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
    private void irInformacion(View view) {
    }
    private void irMundos(View view) {

        ObjectAnimator desplazarsigu = ObjectAnimator.ofFloat(guiaPersonajesBinding.avanzar, "translationY", binding.navHostFragment.getHeight() / 2f, 0);
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(guiaPersonajesBinding.textoguia, "alpha", 1f, 0f);
        ObjectAnimator desplazar = ObjectAnimator.ofFloat(guiaPersonajesBinding.marcador, "translationX", 0, (binding.navView.getWidth() / 3)-78f);
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
    private void volverMundos(View view) {
        ObjectAnimator desplazarsigu = ObjectAnimator.ofFloat(guiaColeccionablesBinding.avanzar, "translationY", binding.navHostFragment.getHeight() / 2f, 0);
        ObjectAnimator desplazarAtr = ObjectAnimator.ofFloat(guiaColeccionablesBinding.atras, "translationY", binding.navHostFragment.getHeight() / 2f, 0);
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(guiaColeccionablesBinding.textoguia, "alpha", 1f, 0f);
        ObjectAnimator desplazar = ObjectAnimator.ofFloat(guiaColeccionablesBinding.marcador, "translationX", 0, (binding.navView.getWidth() / -3)+78f);
        AnimatorSet animatorSet1 = new AnimatorSet();
        animatorSet1
                .play(fadeOut)
                .with(desplazarsigu)
                .with(desplazarAtr)
                .before(desplazar);
        //animatorSet1.setInterpolator(new OvershootInterpolator());
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
    private void volverColeccionables(View view) {
    }
    private void irPersonajes(View view) {
        ObjectAnimator desplazarsigu = ObjectAnimator.ofFloat(guiaMundosBinding.avanzar, "translationY", binding.navHostFragment.getHeight() / 2f, 0);
        ObjectAnimator desplazarAtr = ObjectAnimator.ofFloat(guiaMundosBinding.atras, "translationY", binding.navHostFragment.getHeight() / 2f, 0);
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(guiaMundosBinding.textoguia, "alpha", 1f, 0f);
        ObjectAnimator desplazar = ObjectAnimator.ofFloat(guiaMundosBinding.marcador, "translationX", 0,binding.navView.getWidth() / -3f);
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

    private void irColeccionables(View view) {
        ObjectAnimator desplazarsigu = ObjectAnimator.ofFloat(guiaMundosBinding.avanzar, "translationY", binding.navHostFragment.getHeight() / 2f, 0);
        ObjectAnimator desplazarAtr = ObjectAnimator.ofFloat(guiaMundosBinding.atras, "translationY", binding.navHostFragment.getHeight() / 2f, 0);
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(guiaMundosBinding.textoguia, "alpha", 1f, 0f);
        ObjectAnimator desplazar = ObjectAnimator.ofFloat(guiaMundosBinding.marcador, "translationX", 0,binding.navView.getWidth() / 3f);
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


    private void salirguia(View view) {
        SharedPreferences sharedPreferences = getSharedPreferences("Preferencias", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("guide", false);
        //*********************Volver a activar despues de las pruebas
        //editor.apply();
        //Ponemos el primer elemento del menu
        selectedBottomMenu(binding.navView.getMenu().getItem(0));


        // escondemos la guia sea cual sea la pantalla visualizada

        guiaBienvenidaBinding.guiaBienvenida.setVisibility(View.GONE);
        guiaPersonajesBinding.guiaPersonajes.setVisibility(View.GONE);
        guiaMundosBinding.guiaMundos.setVisibility(View.GONE);
        guiaColeccionablesBinding.guiaColeccionables.setVisibility(View.GONE);
        guiaInformacionBinding.guiaInformacion.setVisibility(View.GONE);
        guiaResumenBinding.guiarResumen.setVisibility(View.GONE);


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


}