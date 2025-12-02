package com.miapp.warmigomail;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowInsetsController;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        configurarBarrasDelSistema();
    }

    // Configurar el color de la status bar y navigation bar para toda la app
    private void configurarBarrasDelSistema() {
        Window window = getWindow();

        // 1. Quita modo translúcido para poder aplicar colores sólidos
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // 2. Color de la barra de estado (status bar - superior)
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

        // 3. Color de la barra de navegación (navigation bar - inferior con botones)
        window.setNavigationBarColor(ContextCompat.getColor(this, R.color.colorBlack));

        // 4. Cambiar color de íconos y texto de status bar (modo oscuro o claro)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android 11+ (API 30+)
            WindowInsetsController insetsController = window.getInsetsController();
            if (insetsController != null) {
                // Texto e iconos blancos
                insetsController.setSystemBarsAppearance(
                        0,
                        WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                );
            }
        } else {
            // Android 6 a 10
            int flags = window.getDecorView().getSystemUiVisibility();
            // Texto e iconos blancos
            flags &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            window.getDecorView().setSystemUiVisibility(flags);
        }
    }
}
