package com.polygraph.util;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;

public class EmailUtil {

    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final int SMTP_PORT = 587;
    // 👇 Usa SIEMPRE tu correo + contraseña de APLICACIÓN (16 caracteres)
    private static final String SMTP_USER = "carlos.tocarruncho2001@gmail.com";
    private static final String SMTP_PASSWORD = "uumqebewapwvflcd";

    public static void enviarCodigoVerificacion(String destinatario, String codigo) throws MessagingException {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", String.valueOf(SMTP_PORT));
        props.put("mail.smtp.ssl.trust", SMTP_HOST);
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");

        // Timeouts para que no se quede colgado
        props.put("mail.smtp.connectiontimeout", "10000"); // 10s
        props.put("mail.smtp.timeout", "10000");
        props.put("mail.smtp.writetimeout", "10000");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SMTP_USER, SMTP_PASSWORD);
            }
        });

        // 🔍 Activa esto si quieres ver TODO lo que hace JavaMail en consola
        // session.setDebug(true);

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(SMTP_USER, false));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
        message.setSubject("Código de verificación - Polygraph");

        String html = """
        <!DOCTYPE html>
        <html lang="es">
        <head>
            <meta charset="UTF-8">
            <title>Código de verificación Polygraph</title>
        </head>
        <body style="margin:0;padding:0;background-color:#f5f5f5;font-family:Arial,Helvetica,sans-serif;">

            <!-- CINTA SUPERIOR -->
            <div style="background-color:#b0202a;padding:18px 0;text-align:center;">
                <!-- Si tienes logo, reemplaza el texto por una imagen -->
                <span style="color:#ffffff;font-size:20px;font-weight:bold;letter-spacing:0.08em;">
                    POLYGRAPH SERVICE
                </span>
            </div>

            <!-- CONTENEDOR PRINCIPAL -->
            <div style="
                max-width:640px;
                margin:24px auto 32px auto;
                background-color:#ffffff;
                border-radius:10px;
                border:1px solid #e4e4e4;
                box-shadow:0 2px 6px rgba(0,0,0,0.04);
                padding:28px 32px;
            ">
                <!-- TÍTULO -->
                <h2 style="margin:0 0 16px 0;color:#333333;font-size:19px;font-weight:600;text-align:left;">
                    Código de acceso a Polygraph
                </h2>

                <!-- DESCRIPCIÓN INICIAL -->
                <p style="margin:0 0 10px 0;color:#555555;font-size:14px;line-height:1.5;">
                    Te enviamos este correo porque estás realizando un proceso de registro y/o recuperacion de contraseña inicio de sesión en
                    <strong>Polygraph</strong>.
                </p>

                <p style="margin:10px 0 12px 0;color:#333333;font-size:14px;line-height:1.5;">
                    Tu <strong>código de verificación</strong> es:
                </p>

                <!-- CÓDIGO DESTACADO -->
                <div style="
                    text-align:center;
                    font-size:30px;
                    font-weight:bold;
                    letter-spacing:6px;
                    color:#b0202a;
                    background-color:#fff4f4;
                    border:1px solid #f1b7b7;
                    border-radius:8px;
                    padding:16px 0;
                    margin:20px 0 18px 0;
                ">
                    %s
                </div>

                <!-- INFORMACIÓN DE VIGENCIA -->
                <p style="margin:0 0 8px 0;color:#555555;font-size:14px;line-height:1.5;">
                    Este código tiene una vigencia de <strong>10 minutos</strong> a partir del momento de su envío.
                    Por favor, regresa a la aplicación e ingrésalo exactamente como aparece.
                </p>

                <p style="margin:8px 0 18px 0;color:#555555;font-size:14px;line-height:1.5;">
                    Si el código expira o no reconoces esta solicitud, puedes ignorar este mensaje o generar un nuevo
                    código desde la aplicación.
                </p>

                <!-- LÍNEA DIVISORIA -->
                <hr style="margin:22px 0;border:none;border-top:1px solid #e0e0e0;">

                <!-- PIE DE PÁGINA -->
                <p style="font-size:12px;color:#888888;line-height:1.5;text-align:center;margin:0;">
                    ¿Tienes dudas o necesitas ayuda?<br>
                    Escríbenos a
                    <a href="mailto:soporte.polygraph@gmail.com"
                       style="color:#b0202a;text-decoration:none;font-weight:500;">
                        soporte.polygraph@gmail.com
                    </a>.
                </p>

                <p style="font-size:11px;color:#aaaaaa;line-height:1.5;text-align:center;margin:18px 0 0 0;">
                    Este mensaje ha sido enviado automáticamente, por favor no respondas a este correo.
                </p>
            </div>

        </body>
        </html>
        """.formatted(codigo);


        message.setContent(html, "text/html; charset=UTF-8");

        Transport.send(message);
    }
}
