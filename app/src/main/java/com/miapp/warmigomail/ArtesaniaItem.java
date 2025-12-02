package com.miapp.warmigomail;

@SuppressWarnings("ClassCanBeRecord")
public class ArtesaniaItem {
    private final String detalle;
    private final float precio;
    private final String tipoCarne;
    private final String ingredientes;
    private final String salsas;
    private final String acompanamiento;

    public ArtesaniaItem(String detalle, float precio,
                         String tipoCarne, String ingredientes,
                         String salsas, String acompanamiento) {
        this.detalle = detalle;
        this.precio = precio;
        this.tipoCarne = tipoCarne;
        this.ingredientes = ingredientes;
        this.salsas = salsas;
        this.acompanamiento = acompanamiento;
    }

    public String getDetalle() {
        return detalle;
    }

    public float getPrecio() {
        return precio;
    }

    public String getTipoCarne() {
        return tipoCarne;
    }

    public String getIngredientes() {
        return ingredientes;
    }

    public String getSalsas() {
        return salsas;
    }

    public String getAcompanamiento() {
        return acompanamiento;
    }
}
