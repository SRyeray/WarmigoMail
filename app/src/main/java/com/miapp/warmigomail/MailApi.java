package com.miapp.warmigomail;

import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import javax.mail.Session;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailApi {

    /**
     * IMPORTANTE:
     * Gmail ya no permite usar contraseñas normales para aplicaciones externas (desde mayo 2022).
     * Para enviar correos desde esta app, debes generar una "contraseña de aplicación".
     * Activa la verificación en 2 pasos en tu cuenta de Gmail y crea la contraseña aquí:
     * https:// myaccount.google.com/apppasswords
     * Usa esa contraseña generada en lugar de tu clave real de Gmail.
     */

    // === Constantes ===
    // Credenciales utilizadas para autenticar la sesión del correo remitente
    private static final String EMAIL = "steven123ipe@gmail.com"; // Email remitente tuEmail@gmail.com
    private static final String PASS = "ylfdtxndnabrwdsb"; // Password remitente xxxxxx
    // Dirección que recibirá una copia del correo enviado
    private static final String EMAIL_DEL_LOCAL = "aartesanias.aqp@gmail.com"; // Email de la tienda tuEmail@tuDominio.com

    // === Variables necesarias para el envío ===
    private final String mEmailCliente;
    private final String mAsunto;
    private final String mMensaje;
    private final ResultadoDelEnvio mListener;

    // === Interfaz para devolver el resultado al final ===
    public interface ResultadoDelEnvio {
        void resultado(boolean resultado);
    }

    // === Constructor para recibir los datos del pedido ===
    public MailApi(String mEmailCliente, String mAsunto, String mMensaje, ResultadoDelEnvio mListener) {
        this.mEmailCliente = mEmailCliente;
        this.mAsunto = mAsunto;
        this.mMensaje = mMensaje;
        this.mListener = mListener;
    }

    // ==== Méthod público que inicia el envío del email ====
    public void enviar() {
        // Ejecuta la tarea en segundo plano usando ExecutorService
        ExecutorService executor = Executors.newSingleThreadExecutor();

        // Handler para volver al hilo principal al finalizar
        Handler handler = new Handler(Looper.getMainLooper());

        // Ejecutar proceso en segundo plano
        executor.execute(() -> {
            boolean resultado = false;

            try {
                // === Configuración del servidor SMTP ===
                Properties props = new Properties();
                props.put("mail.smtp.host", "smtp.gmail.com");
                props.put("mail.smtp.starttls.enable", "true");
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.port", "587");

                // === Autenticación de la sesión ===
                Session session = Session.getDefaultInstance(props, new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(EMAIL, PASS);
                    }
                });

                // === Crear y enviar el mensaje ===
                MimeMessage mm = new MimeMessage(session);
                mm.setFrom(new InternetAddress(EMAIL)); // Envia
                mm.addRecipient(Message.RecipientType.TO, new InternetAddress(mEmailCliente)); // Recibe
                mm.addRecipient(Message.RecipientType.CC, new InternetAddress(EMAIL_DEL_LOCAL)); // Copia
                mm.setSubject(mAsunto);
                mm.setText(mMensaje);

                // Enviar correo
                Transport.send(mm);
                resultado = true;

            } catch (MessagingException e) {
                Log.e("MailApi", "Error al enviar el correo", e);
            }

            // === Volver al hilo principal para actualizar UI ===
            boolean finalResultado = resultado;
            handler.post(() -> {
                if (mListener != null) mListener.resultado(finalResultado);
            });
        });
    }
}
