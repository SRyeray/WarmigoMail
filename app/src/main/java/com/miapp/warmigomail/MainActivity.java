package com.miapp.warmigomail;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class MainActivity extends BaseActivity {

    /** === Constantes de configuración del pedido === */
    private static final int PEDIDO_MINIMO = 1;
    private static final int PEDIDO_MAXIMO = 99;
    private static final String MENSAJE_ELEGIR_OPCION = "Debes elegir una opción";
    private static final float BURGER = 30.0f;
    private static final float INGREDIENTE = 15.0f;
    private static final float ACOMPANAMIENTO = 5.0f;

    /** === Inicio del flujo === */
    private LinearLayout mLlInicio;
    private Button mBtnIniciarPedido;

    /** === Nombre del cliente === */
    private LinearLayout mLlNombreCliente;
    private TextInputEditText mTietNombreCliente;

    /** === Email del cliente === */
    private LinearLayout mLlEmailCliente;
    private TextInputEditText mTietEmailCliente;

    /** === Cantidad del pedido === */
    private LinearLayout mLlCantidad;
    private TextView mTvCantidad;
    private Button mBtnIncrementarCantidad, mBtnDecrementarCantidad;

    /** === Vista de resumen del pedido === */
    private RelativeLayout mRlResumenDelPedido;
    private RecyclerView mRvResumen;
    private TextView mTvNombreCliente;
    private TextView mTvTotal;
    private TextView mTvNumeroItem;
    private Button mBtnResumenAtras, mBtnResumenConfirmar;

    /** === Tipo de carne === */
    private LinearLayout mLlTipoCarne;
    private TextView mTvPrecioTipoCarne;
    private RadioGroup mRgTipoCarne;

    /** === Ingredientes === */
    private LinearLayout mLlIngredientes;
    private TextView mTvPrecioIngrediente;
    private List<CheckBox> mListaIngredientes;

    /** === Salsas === */
    private LinearLayout mLlSalsas;
    private List<CheckBox> mListaSalsas;

    /** === Acompañamiento === */
    private LinearLayout mLlAcompanamiento;
    private TextView mTvPrecioAcompanamiento;
    private RadioGroup mRgTipoAcompanamiento;

    /** === Vista final de confirmación === */
    private LinearLayout mLlfinalizar;
    private TextView mTvEmailConfirmacion;
    private Button mBtnFinalizar;

    /** === Botones de navegación (entre secciones) === */
    private LinearLayout mLlBotones;
    private Button mBtnIrAdelante, mBtnIrAtras;

    /** === Adaptador del RecyclerView === */
    private ArtesaniaAdapter mAdapter;
    private final List<ArtesaniaItem> mListaHamburguesas = new ArrayList<>();

    /** === ProgressBar de carga al enviar === */
    private FrameLayout mFlFondoCarga;

    /** === Variables de control del flujo === */
    private int mPosicionItemEditando = -1;
    private int mCantidadPedido = PEDIDO_MINIMO;
    private int mCantidadIngredientes = 0;
    private int mCantidadSalsas = 0;
    private int mCantidadAcompanamiento = 0;

    /** === Información del pedido actual === */
    private String mNombreClienteText;
    private String mTipoCarnePedido = "";
    private String mIngredientesPedido = "";
    private String mSalsasPedido = "";
    private String mAcompanamientoPedido = "";

    /** === Estados de selección por paso === */
    private Boolean mCarneElegida = false;
    private Boolean mIngredientesElegidos = false;
    private Boolean mSalsasElegidas = false;
    private Boolean mAcompanamientoElegido = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inicializarVistas();
        ocultarSecciones();
        configurarRecyclerViewResumen();
        definirPrecios();
        listeners();
    }

    /** === Inicialización === */
    private void inicializarVistas() {

        // === Inicio ===
        mLlInicio = findViewById(R.id.ll_inicio);
        mBtnIniciarPedido = findViewById(R.id.btn_iniciar_pedido);

        // === Nombre del cliente ===
        mLlNombreCliente = findViewById(R.id.ll_nombre_cliente);
        mTietNombreCliente = findViewById(R.id.tiet_nombre_cliente);

        // === Email del cliente ===
        mLlEmailCliente = findViewById(R.id.ll_email_cliente);
        mTietEmailCliente = findViewById(R.id.tiet_email_cliente);

        // === Cantidad del pedido ===
        mLlCantidad = findViewById(R.id.ll_cantidad);
        mTvCantidad = findViewById(R.id.tv_cantidad);
        mBtnIncrementarCantidad = findViewById(R.id.btn_incrementar_cantidad);
        mBtnDecrementarCantidad = findViewById(R.id.btn_decrementar_cantidad);

        // === Resumen del pedido ===
        mRlResumenDelPedido = findViewById(R.id.rl_resumen_del_pedido);
        mTvNombreCliente = findViewById(R.id.tv_nombre_cliente);
        mRvResumen = findViewById(R.id.rv_resumen);
        mTvTotal = findViewById(R.id.tv_total);
        mBtnResumenAtras = findViewById(R.id.btn_resumen_atras);
        mBtnResumenConfirmar = findViewById(R.id.btn_resumen_confirmar);
        mTvNumeroItem = findViewById(R.id.tv_numero_item);

        // === Tipo de carne ===
        mLlTipoCarne = findViewById(R.id.ll_tipo_carne);
        mTvPrecioTipoCarne = findViewById(R.id.tv_precio_tipo_carne);
        mRgTipoCarne = findViewById(R.id.rg_tipo_carne);

        // === Ingredientes ===
        mLlIngredientes = findViewById(R.id.ll_ingredientes);
        mTvPrecioIngrediente = findViewById(R.id.tv_precio_ingredientes);
        CheckBox mCbIngrediente1 = findViewById(R.id.cb_ingrediente1); // Lechuga
        CheckBox mCbIngrediente2 = findViewById(R.id.cb_ingrediente2); // Tomate
        CheckBox mCbIngrediente3 = findViewById(R.id.cb_ingrediente3); // Queso Cheddar
        CheckBox mCbIngrediente4 = findViewById(R.id.cb_ingrediente4); // Tocino
        CheckBox mCbIngrediente5 = findViewById(R.id.cb_ingrediente5); // Papitas al hilo
        CheckBox mCbIngrediente6 = findViewById(R.id.cb_ingrediente6); // Ninguno
        mListaIngredientes = Arrays.asList(
                mCbIngrediente1, mCbIngrediente2, mCbIngrediente3,
                mCbIngrediente4, mCbIngrediente5, mCbIngrediente6
        );

        // === Salsas ===
        mLlSalsas = findViewById(R.id.ll_salsas);
        CheckBox mCbSalsa1 = findViewById(R.id.cb_salsas1); // Ketchup
        CheckBox mCbSalsa2 = findViewById(R.id.cb_salsas2); // Mayonesa
        CheckBox mCbSalsa3 = findViewById(R.id.cb_salsas3); // Mostaza
        CheckBox mCbSalsa4 = findViewById(R.id.cb_salsas4); // Ají
        CheckBox mCbSalsa5 = findViewById(R.id.cb_salsas5); // Papitas al hilo
        CheckBox mCbSalsa6 = findViewById(R.id.cb_salsas6); // Ninguna
        mListaSalsas = Arrays.asList(
                mCbSalsa1, mCbSalsa2, mCbSalsa3,
                mCbSalsa4, mCbSalsa5, mCbSalsa6
        );

        // === Acompañamiento ===
        mLlAcompanamiento = findViewById(R.id.ll_acompanamiento);
        mTvPrecioAcompanamiento = findViewById(R.id.tv_precio_acompanamiento);
        mRgTipoAcompanamiento = findViewById(R.id.rg_tipo_acompanamiento);

        // === Finalizar ===
        mLlfinalizar = findViewById(R.id.ll_finalizar);
        mTvEmailConfirmacion = findViewById(R.id.tv_email_confirmacion);
        mBtnFinalizar = findViewById(R.id.btn_finalizar);

        // === Botones generales ===
        mLlBotones = findViewById(R.id.ll_botones);
        mBtnIrAtras = findViewById(R.id.btn_ir_atras);
        mBtnIrAdelante = findViewById(R.id.btn_ir_adelante);

        // === ProgressBar ===
        mFlFondoCarga = findViewById(R.id.fl_fondo_carga);
    }

    private void ocultarSecciones() {
        mLlInicio.setVisibility(View.VISIBLE);
        mLlNombreCliente.setVisibility(View.GONE);
        mLlEmailCliente.setVisibility(View.GONE);
        mLlCantidad.setVisibility(View.GONE);
        mRlResumenDelPedido.setVisibility(View.GONE);
        mTvNumeroItem.setVisibility(View.GONE);
        mLlTipoCarne.setVisibility(View.GONE);
        mLlIngredientes.setVisibility(View.GONE);
        mLlSalsas.setVisibility(View.GONE);
        mLlAcompanamiento.setVisibility(View.GONE);
        mLlBotones.setVisibility(View.GONE);
        mLlfinalizar.setVisibility(View.GONE);
    }

    private void configurarRecyclerViewResumen() {

        mRvResumen.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new ArtesaniaAdapter(
                mListaHamburguesas,
                this::eliminarItem,
                this::editarItem
        );
        mRvResumen.setAdapter(mAdapter);
    }

    @SuppressLint("SetTextI18n")
    private void definirPrecios() {
        mTvPrecioTipoCarne.setText("A solo " + formatearPrecio(BURGER));
        mTvPrecioIngrediente.setText("A solo " + formatearPrecio(INGREDIENTE) + " c/u");
        mTvPrecioAcompanamiento.setText("A solo " + formatearPrecio(ACOMPANAMIENTO));
    }

    private void listeners() {

        // :: Listeners usando referencia directa a method

        // Inicio
        mBtnIniciarPedido.setOnClickListener(this::iniciarPedido);

        // Nombre del cliente
        mTietNombreCliente.addTextChangedListener(crearTextWatcher(mTietNombreCliente));

        // Email del cliente
        mTietEmailCliente.addTextChangedListener(crearTextWatcher(mTietEmailCliente));

        // Cantidad del pedido
        mBtnIncrementarCantidad.setOnClickListener(this::incrementarCantidad);
        mBtnDecrementarCantidad.setOnClickListener(this::decrementarCantidad);

        // Resumen del pedido
        mBtnResumenAtras.setOnClickListener(this::irAtras);
        mBtnResumenConfirmar.setOnClickListener(this::irAdelante);

        // Tipo de carne
        mRgTipoCarne.setOnCheckedChangeListener(((group, checkedId) -> elegirCarne()));

        // Ingredientes
        for (CheckBox cb : mListaIngredientes) {
            cb.setOnClickListener(this::elegirIngredientes);
        }

        // Salsas
        for (CheckBox cb : mListaSalsas) {
            cb.setOnClickListener(this::elegirSalsas);
        }

        // Acompañamiento
        mRgTipoAcompanamiento.setOnCheckedChangeListener(((group, checkedId) -> elegirAcompanamiento()));

        // Finalizar
        mBtnFinalizar.setOnClickListener(this::finalizar);

        // Botones
        mBtnIrAtras.setOnClickListener(this::irAtras);
        mBtnIrAdelante.setOnClickListener(this::irAdelante);
    }

    /** === Navegación entre vistas === */
    public void irAtras(View view) {

        // Nombre del cliente
        if (mLlNombreCliente.getVisibility() == View.VISIBLE) {
            mLlInicio.setVisibility(View.VISIBLE);
            mLlNombreCliente.setVisibility(View.GONE);
            mLlBotones.setVisibility(View.GONE);
            cerrarTeclado(mTietNombreCliente);
            return;
        }

        // Email del cliente
        if (mLlEmailCliente.getVisibility() == View.VISIBLE) {
            mLlNombreCliente.setVisibility(View.VISIBLE);
            mLlEmailCliente.setVisibility(View.GONE);
            cerrarTeclado(mTietEmailCliente);
            return;
        }

        // Cantidad del pedido
        if (mLlCantidad.getVisibility() == View.VISIBLE) {
            mLlEmailCliente.setVisibility(View.VISIBLE);
            mLlCantidad.setVisibility(View.GONE);
            return;
        }

        // Resumen del pedido
        if (mRlResumenDelPedido.getVisibility() == View.VISIBLE) {
            mLlCantidad.setVisibility(View.VISIBLE);
            mLlBotones.setVisibility(View.VISIBLE);
            mRlResumenDelPedido.setVisibility(View.GONE);
            return;
        }

        // Tipo de carne
        if (mLlTipoCarne.getVisibility() == View.VISIBLE) {
            mRlResumenDelPedido.setVisibility(View.VISIBLE);
            mLlTipoCarne.setVisibility(View.GONE);
            mTvNumeroItem.setVisibility(View.GONE);
            mLlBotones.setVisibility(View.GONE);
            resetearFormularioDeHamburguesa();
            return;
        }

        // Ingredientes
        if (mLlIngredientes.getVisibility() == View.VISIBLE) {
            mLlTipoCarne.setVisibility(View.VISIBLE);
            mLlIngredientes.setVisibility(View.GONE);
            return;
        }

        // Salsas
        if (mLlSalsas.getVisibility() == View.VISIBLE) {
            mLlIngredientes.setVisibility(View.VISIBLE);
            mLlSalsas.setVisibility(View.GONE);
            return;
        }

        // Acompañamientos
        if (mLlAcompanamiento.getVisibility() == View.VISIBLE) {
            mLlSalsas.setVisibility(View.VISIBLE);
            mLlAcompanamiento.setVisibility(View.GONE);
        }
    }

    private void irAdelante(View view) {

        // Nombre del cliente
        if (mLlNombreCliente.getVisibility() == View.VISIBLE) {
            if (!validarCampoNombre(mTietNombreCliente)) return;

            String nombre = mTietNombreCliente.getText() != null
                    ? mTietNombreCliente.getText().toString().trim()
                    : "";

            mNombreClienteText = "Nombre del Cliente:\n" + nombre.toUpperCase();
            mTvNombreCliente.setText(mNombreClienteText);

            mLlNombreCliente.setVisibility(View.GONE);
            mLlEmailCliente.setVisibility(View.VISIBLE);

            cerrarTeclado(mTietNombreCliente);
            return;
        }

        // Email del cliente
        if (mLlEmailCliente.getVisibility() == View.VISIBLE) {
            if (!validarCampoEmail(mTietEmailCliente)) return;

            String email = mTietEmailCliente.getText() != null
                    ? mTietEmailCliente.getText().toString().trim()
                    : "";

            mTvEmailConfirmacion.setText(email);

            mLlEmailCliente.setVisibility(View.GONE);
            mLlCantidad.setVisibility(View.VISIBLE);

            cerrarTeclado(mTietEmailCliente);
            return;
        }

        // Cantidad del pedido
        if (mLlCantidad.getVisibility() == View.VISIBLE) {
            mLlCantidad.setVisibility(View.GONE);
            mLlBotones.setVisibility(View.GONE);
            mRlResumenDelPedido.setVisibility(View.VISIBLE);
            crearItemsDelPedido();
            return;
        }

        // Resumen del pedido
        if (mRlResumenDelPedido.getVisibility() == View.VISIBLE) {
            if(!validarGlosaEnItems()) {
                mostrarAdvertencia("Aún tienes artesanías sin detalles");
                return;
            }

            enviarPedido();
            return;
        }

        // Tipo de carne
        if (mLlTipoCarne.getVisibility() == View.VISIBLE) {
            if (!mCarneElegida) {
                mostrarAdvertencia(MENSAJE_ELEGIR_OPCION);
                return;
            }

            mLlTipoCarne.setVisibility(View.GONE);
            mLlIngredientes.setVisibility(View.VISIBLE);
            return;
        }

        // Ingredientes
        if (mLlIngredientes.getVisibility() == View.VISIBLE) {
            if (!mIngredientesElegidos) {
                mostrarAdvertencia(MENSAJE_ELEGIR_OPCION);
                return;
            }

            mLlIngredientes.setVisibility(View.GONE);
            mLlSalsas.setVisibility(View.VISIBLE);
            return;
        }

        // Salsas
        if (mLlSalsas.getVisibility() == View.VISIBLE) {
            if (!mSalsasElegidas) {
                mostrarAdvertencia(MENSAJE_ELEGIR_OPCION);
                return;
            }

            mLlSalsas.setVisibility(View.GONE);
            mLlAcompanamiento.setVisibility(View.VISIBLE);
            return;
        }

        // Acompañamiento
        if (mLlAcompanamiento.getVisibility() == View.VISIBLE) {
            if (!mAcompanamientoElegido) {
                mostrarAdvertencia(MENSAJE_ELEGIR_OPCION);
                return;
            }

            if (mPosicionItemEditando != -1) {
                actualizarItemDelPedido();
            }

            mLlAcompanamiento.setVisibility(View.GONE);
            mTvNumeroItem.setVisibility(View.GONE);
            mLlBotones.setVisibility(View.GONE);
            mRlResumenDelPedido.setVisibility(View.VISIBLE);
        }
    }

    /** === Sección 1: Inicio === */
    public void iniciarPedido(View view) {
        mLlInicio.setVisibility(View.GONE);
        mLlNombreCliente.setVisibility(View.VISIBLE);
        mLlBotones.setVisibility(View.VISIBLE);
    }

    /** === Sección 2 y 3: Nombre y Email del cliente === */
    private boolean validarCampoNombre(@NonNull TextInputEditText campo) {
        // Obtener el TextInputLayout padre
        TextInputLayout layout = (TextInputLayout) campo.getParent().getParent();

        // Un TextInputEditText devuelve un objeto Editable
        Editable texto = campo.getText();
        String valor = texto != null ? texto.toString().trim() : "";

        if (valor.isEmpty()) {
            layout.setEndIconMode(TextInputLayout.END_ICON_NONE); // Oculta clear_text
            campo.setError("Campo obligatorio");
            return false;
        }

        if (valor.length() < 3) {
            layout.setEndIconMode(TextInputLayout.END_ICON_NONE); // Oculta clear_text
            campo.setError("Mínimo 3 caracteres");
            return false;
        }

        campo.setError(null);
        layout.setEndIconMode(TextInputLayout.END_ICON_CLEAR_TEXT);
        return true;
    }

    private boolean validarCampoEmail(@NonNull TextInputEditText campo) {
        TextInputLayout layout = (TextInputLayout) campo.getParent().getParent();

        Editable texto = campo.getText();
        String valor = texto != null ? texto.toString().trim() : "";

        if (valor.isEmpty()) {
            layout.setEndIconMode(TextInputLayout.END_ICON_NONE); // Oculta el ícono clear_text
            campo.setError("Campo obligatorio");
            return false;
        }

        if (!validarEmail(valor)) {
            layout.setEndIconMode(TextInputLayout.END_ICON_NONE);
            campo.setError("Email inválido");
            return false;
        }

        campo.setError(null);
        layout.setEndIconMode(TextInputLayout.END_ICON_CLEAR_TEXT); // Restaura el icono clear_text
        return true;
    }

    private boolean validarEmail(String email) {
        return email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    @NonNull
    private TextWatcher crearTextWatcher(TextInputEditText editText) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                TextInputLayout layout = (TextInputLayout) editText.getParent().getParent();

                if (editText.getError() != null) {
                    editText.setError(null);
                    layout.setEndIconMode(TextInputLayout.END_ICON_CLEAR_TEXT);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
    }

    /** === Sección 4: Cantidad del pedido === */
    public void incrementarCantidad(View view) {
        if (mCantidadPedido < PEDIDO_MAXIMO) {
            mCantidadPedido++;
        }

        mTvCantidad.setText(String.valueOf(mCantidadPedido));
    }

    public void decrementarCantidad(View view) {
        if (mCantidadPedido > PEDIDO_MINIMO) {
            mCantidadPedido--;
        }

        mTvCantidad.setText(String.valueOf(mCantidadPedido));
    }

    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    private void crearItemsDelPedido() {

        // Crea la lista de hamburguesas por defecto
        mListaHamburguesas.clear();
        for (int i = 0; i < mCantidadPedido; i++) {
            mListaHamburguesas.add(new ArtesaniaItem(
                    "Sin detalles", // detalle
                    0f,                        // precio
                    "",                        // tipoCarne
                    "",                        // ingredientes
                    "",                        // salsas
                    ""                         // acompañamiento
            ));
        }
        mAdapter.notifyDataSetChanged();

        mTvTotal.setText("Total: S/0.00");
    }

    /** === Sección 5: Resumen === */
    private boolean validarGlosaEnItems() {
        for (ArtesaniaItem item : mListaHamburguesas) {
            if (item.getDetalle().equals("Sin detalles")) {
                return false;
            }
        }
        return true;
    }

    private void enviarPedido() {
        // 1. Armar glosa del pedido
        StringBuilder glosa = new StringBuilder();

        for (int i = 0; i < mListaHamburguesas.size(); i++) {
            ArtesaniaItem item = mListaHamburguesas.get(i);
            glosa.append("------ Item").append(i + 1).append(" ------\n")
                    .append(item.getDetalle()).append("\n")
                    .append(formatearPrecio(item.getPrecio())).append("\n\n");
        }

        // 2. Preparar contenido del correo
        String emailDelCliente = mTietEmailCliente.getText() != null
                ? mTietEmailCliente.getText().toString().trim()
                : "";

        String asuntoDelEmail = "NUEVO PEDIDO";

        String textoDelEmail = mNombreClienteText + "\n\n"
                + glosa
                + "-------------------\n"
                + mTvTotal.getText();

        // 3. Mostrar progress bar y desactivar botón
        mBtnResumenConfirmar.setEnabled(false);
        mFlFondoCarga.setVisibility(View.VISIBLE);

        // 4. Enviar email
        new MailApi(emailDelCliente, asuntoDelEmail, textoDelEmail, resultado -> {
            // 5. Ocultar progress bar y reactivar botón al finalizar
            mFlFondoCarga.setVisibility(View.GONE);
            mBtnResumenConfirmar.setEnabled(true);

            if (resultado) {
                mostrarAdvertencia("¡Pedido enviado correctamente!");
                mRlResumenDelPedido.setVisibility(View.GONE);
                mLlfinalizar.setVisibility(View.VISIBLE);
            } else {
                mostrarAdvertencia("¡Falló el envío!");
            }
        }).enviar();
    }

    @SuppressLint("SetTextI18n")
    private void eliminarItem(int posicion) {
        mListaHamburguesas.remove(posicion);
        // Notificar al adaptador que se eliminó el ítem
        mAdapter.notifyItemRemoved(posicion);
        // Actualiza la lista desde esa posición
        mAdapter.notifyItemRangeChanged(posicion, mListaHamburguesas.size());
        mTvTotal.setText("Total: " + formatearPrecio(calcularTotal()));
    }

    private void editarItem(int posicion) {
        mPosicionItemEditando = posicion; // Guarda la posición globalmente

        ArtesaniaItem item = mListaHamburguesas.get(posicion);

        // Obtener valores del pedido
        mTipoCarnePedido = item.getTipoCarne();
        mIngredientesPedido = item.getIngredientes();
        mSalsasPedido = item.getSalsas();
        mAcompanamientoPedido = item.getAcompanamiento();

        // Mostrar el número de hamburguesa
        mTvNumeroItem.setText(String.valueOf(posicion + 1));

        // === Restaurar selección de carne ===
        for (int i = 0; i < mRgTipoCarne.getChildCount(); i++) {
            View child = mRgTipoCarne.getChildAt(i);
            if (child instanceof RadioButton radio) {
                if (radio.getText().toString().trim().equals(mTipoCarnePedido)) {
                    radio.setChecked(true);
                    mCarneElegida = true;
                    break;
                }
            }
        }

        // === Restaurar selección de acompañamiento ===
        for (int i = 0; i < mRgTipoAcompanamiento.getChildCount(); i++) {
            View child = mRgTipoAcompanamiento.getChildAt(i);
            if (child instanceof RadioButton radio) {
                if (radio.getText().toString().trim().equals(mAcompanamientoPedido)) {
                    radio.setChecked(true);
                    mAcompanamientoElegido = true;
                    break;
                }
            }
        }

        // === Restaurar ingredientes ===
        for (CheckBox cb : mListaIngredientes) {
            cb.setChecked(false); // Primero desmarcamos todos
            if (mIngredientesPedido.contains(cb.getText().toString().trim())) {
                cb.setChecked(true);
                mIngredientesElegidos = true;
            }
        }

        // === Restaurar salsas ===
        for (CheckBox cb : mListaSalsas) {
            cb.setChecked(false);
            if (mSalsasPedido.contains(cb.getText().toString().trim())) {
                cb.setChecked(true);
                mSalsasElegidas = true;
            }
        }

        // Ocultar y mostrar vistas necesarias
        mRlResumenDelPedido.setVisibility(View.GONE);
        mTvNumeroItem.setVisibility(View.VISIBLE);
        mLlTipoCarne.setVisibility(View.VISIBLE);
        mLlBotones.setVisibility(View.VISIBLE);
    }

    /** === Sección 6: Tipo de Carne === */
    public void elegirCarne() {
        int idSeleccionado = mRgTipoCarne.getCheckedRadioButtonId();
        if (idSeleccionado != -1) {
            RadioButton opcionSeleccionada = findViewById(idSeleccionado);
            mTipoCarnePedido = opcionSeleccionada.getText().toString().trim();
            mCarneElegida = true;
        }
    }

    /** === Sección 7: Ingredientes === */
    public void elegirIngredientes(View view) {
        CheckBox checkBox = (CheckBox) view;
        String ingrediente = checkBox.getText().toString();
        boolean esNinguno = ingrediente.equalsIgnoreCase("Ninguno");

        if (checkBox.isChecked()) {
            if (esNinguno) {
                mIngredientesPedido = "Ninguno";
                mCantidadIngredientes = 0;
                mIngredientesElegidos = true;

                // Desmarcar todos los demás ingredientes
                desmarcarOtrosIngredientes(checkBox.getId());
            } else {
                // Cada if evita duplicados y limpia texto
                if (!mIngredientesPedido.contains(ingrediente)) {
                    if (mIngredientesPedido.contains("Ninguno")) {
                        mIngredientesPedido = "";
                    }

                    mIngredientesPedido += ingrediente + ", ";
                    mCantidadIngredientes++;
                    mIngredientesElegidos = true;

                    // Desmarca "Ninguno"
                    desmarcarNinguno();
                }
            }
        } else {
            if (esNinguno) {
                mIngredientesPedido = "";
                mCantidadIngredientes = 0;
                mIngredientesElegidos = false;
            } else {
                mIngredientesPedido = mIngredientesPedido.replace(ingrediente + ", ", "");
                mCantidadIngredientes--;

                if (mCantidadIngredientes == 0) {
                    mIngredientesElegidos = false;
                }
            }
        }
    }

    private void desmarcarOtrosIngredientes(int exceptId) {
        for (CheckBox cb : mListaIngredientes) {
            if (cb.getId() != exceptId && !cb.getText().toString().equalsIgnoreCase("Ninguno")) {
                cb.setChecked(false);
            }
        }
    }

    private void desmarcarNinguno() {
        for (CheckBox cb : mListaIngredientes) {
            if (cb.getText().toString().equalsIgnoreCase("Ninguno")) {
                cb.setChecked(false);
            }
        }
    }

    /** === Sección 8: Salsas === */
    public void elegirSalsas(View view) {
        CheckBox checkBox = (CheckBox) view;
        String salsa = checkBox.getText().toString();
        boolean esNinguna = salsa.equalsIgnoreCase("Ninguna");

        if (checkBox.isChecked()) {
            if (esNinguna) {
                mSalsasPedido = "Ninguna";
                mCantidadSalsas = 0;
                mSalsasElegidas = true;

                // Desmarcar todas las demás salsas
                desmarcarOtrasSalsas(checkBox.getId());
            } else {
                // Cada if evita duplicados y limpia texto
                if (!mSalsasPedido.contains(salsa)) {
                    if (mSalsasPedido.contains("Ninguna")) {
                        mSalsasPedido = "";
                    }

                    mSalsasPedido += salsa + ", ";
                    mCantidadSalsas++;
                    mSalsasElegidas = true;

                    // Desmarcar "Ninguna"
                    desmarcarNingunaSalsa();
                }
            }
        } else {
            if (esNinguna) {
                mSalsasPedido = "";
                mCantidadSalsas = 0;
                mSalsasElegidas = false;
            } else {
                mSalsasPedido = mSalsasPedido.replace(salsa + ", ", "");
                mCantidadSalsas--;

                if (mCantidadSalsas == 0) {
                    mSalsasElegidas = false;
                }
            }
        }
    }

    private void desmarcarOtrasSalsas(int exceptId) {
        for (CheckBox cb : mListaSalsas) {
            if (cb.getId() != exceptId && !cb.getText().toString().equalsIgnoreCase("Ninguna")) {
                cb.setChecked(false);
            }
        }
    }

    private void desmarcarNingunaSalsa() {
        for (CheckBox cb : mListaSalsas) {
            if (cb.getText().toString().equalsIgnoreCase("Ninguna")) {
                cb.setChecked(false);
            }
        }
    }

    /** === Sección 9: Acompañamiento === */
    public void elegirAcompanamiento() {
        int idSeleccionado = mRgTipoAcompanamiento.getCheckedRadioButtonId();
        if (idSeleccionado != -1) {
            RadioButton opcionSeleccionada = findViewById(idSeleccionado);
            String textoSeleccionado = opcionSeleccionada.getText().toString().toLowerCase();

            if (textoSeleccionado.contains("nada")) {
                mCantidadAcompanamiento = 0;
            } else if (textoSeleccionado.contains("+")) {
                mCantidadAcompanamiento = 2;
            } else {
                mCantidadAcompanamiento = 1;
            }

            mAcompanamientoPedido = opcionSeleccionada.getText().toString().trim();
            mAcompanamientoElegido = true;
        }
    }

    @SuppressLint("SetTextI18n")
    private void actualizarItemDelPedido() {
        // 1. Obtener el objeto del item actual
        ArtesaniaItem itemActualizado = getHamburguesaItem();

        // 2. Reemplazar ítem en la lista según la posición
        mListaHamburguesas.set(mPosicionItemEditando, itemActualizado);

        // 3. Notificar al adaptador que hubo un cambio en esa posición
        mAdapter.notifyItemChanged(mPosicionItemEditando);

        // 4. Actualizar el total general del pedido
        mTvTotal.setText("Total: " + formatearPrecio(calcularTotal()));

        // 5. Mostrar mensaje de confirmación (antes de reiniciar la posición del item)
        mostrarAdvertencia("Artesanía " + (mPosicionItemEditando + 1) + " actualizada correctamente.");

        // 6. Reiniciar las variables temporales
        resetearFormularioDeHamburguesa();
    }

    @NonNull
    private ArtesaniaItem getHamburguesaItem() {
        // 1. Crear glosa del item actual
        String glosa = "Artesanía:\n" + mTipoCarnePedido
                + "\n\nAdornos:\n" + mIngredientesPedido
                + "\n\nExtras:\n" + mSalsasPedido
                + "\n\nEmpacke:\n" + mAcompanamientoPedido + "\n";

        // 2. Calcular el precio del ítem actual
        float precio = calcularPrecioItem();

        // 3. Construir y devolver el objeto del item actual
        return new ArtesaniaItem(
                glosa,
                precio,
                mTipoCarnePedido,
                mIngredientesPedido,
                mSalsasPedido,
                mAcompanamientoPedido
        );
    }

    private float calcularPrecioItem() {
        return BURGER
                + (mCantidadIngredientes * INGREDIENTE)
                + (mCantidadAcompanamiento * ACOMPANAMIENTO);
    }

    /** === Sección 10: Finalizar === */
    public void finalizar(View view) {
        mLlfinalizar.setVisibility(View.GONE);
        mLlInicio.setVisibility(View.VISIBLE);

        limpiarCamposDeEntrada();
    }

    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    private void limpiarCamposDeEntrada() {
        mTietNombreCliente.setText("");
        mTietEmailCliente.setText("");
        mCantidadPedido = PEDIDO_MINIMO;
        mTvCantidad.setText(String.valueOf(mCantidadPedido));

        // Limpiar la lista del pedido
        mListaHamburguesas.clear();
        mAdapter.notifyDataSetChanged(); // Refrescar RecyclerView
        mTvTotal.setText("Total: " + formatearPrecio(0));
    }

    /** === Utilidades === */
    @NonNull
    private String formatearPrecio(float monto) {
        return String.format(Locale.getDefault(), "S/%.2f", monto);
    }

    private float calcularTotal() {
        float total = 0.0f;

        for (ArtesaniaItem item : mListaHamburguesas) {
            total += item.getPrecio();
        }

        return total;
    }

    private void cerrarTeclado(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void mostrarAdvertencia(String mensaje) {
        Toast toast = Toast.makeText(this, mensaje, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);

        View view = toast.getView();
        if (view != null) {
            // Cambiar color de fondo del toast
            Drawable background = view.getBackground();
            if (background != null) {
                background.setColorFilter(ContextCompat.getColor(this, R.color.colorBlack), PorterDuff.Mode.SRC_IN);
            }

            // Cambiar color del texto
            TextView text = view.findViewById(android.R.id.message);
            if (text != null) {
                text.setTextColor(ContextCompat.getColor(this, R.color.colorWhite));
            }
        }

        toast.show();
    }

    private void resetearFormularioDeHamburguesa() {

        // Reiniciar contadores y textos
        mCantidadIngredientes = 0;
        mCantidadSalsas = 0;
        mCantidadAcompanamiento = 0;
        mTipoCarnePedido = "";
        mIngredientesPedido = "";
        mSalsasPedido = "";
        mAcompanamientoPedido = "";

        // Reiniciar banderas
        mCarneElegida = false;
        mIngredientesElegidos = false;
        mSalsasElegidas = false;
        mAcompanamientoElegido = false;

        // Limpiar selecciones de RadioGroups
        mRgTipoCarne.clearCheck();
        mRgTipoAcompanamiento.clearCheck();

        // Desmarcar todos los CheckBoxes
        for (CheckBox cb : mListaIngredientes) {
            cb.setChecked(false);
        }

        for (CheckBox cb : mListaSalsas) {
            cb.setChecked(false);
        }

        // Restaurar estado de edición
        mPosicionItemEditando = -1;
    }
}