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
                <head><meta charset="UTF-8"></head>
                <body style="font-family: Arial, sans-serif; background-color:#ffffff; margin:0; padding:0;">
                <div style="max-width:600px;margin:20px auto;background:#ffffff;border-radius:12px;
                            border:1px solid #f3f3f3;padding:25px 30px;">
                    <h1 style="text-align:center;color:#e63939;margin:0 0 5px 0;">Polygraph</h1>
                    <div style="width:90%%;height:3px;background:linear-gradient(to right,#ff6b6b,#e63939);
                                border-radius:5px;margin:15px auto;"></div>
                    <p>Hola 👋</p>
                    <p>Tu <strong>código de verificación</strong> para continuar tu registro en <b>Polygraph</b> es:</p>
                    <div style="text-align:center;font-size:28px;font-weight:bold;background:#ffe8e8;
                                padding:14px 0;border-radius:10px;color:#e63939;border:2px dashed #ff6b6b;
                                letter-spacing:4px;margin:25px 0;">%s</div>
                    <p>Regresa a la aplicación e ingresa el código. Este código expirará en <b>10 minutos</b>.</p>
                    <p>Si el código expira, solicita uno nuevo desde la aplicación.</p>
                    <hr style="margin:25px 0;border:none;border-top:1px solid #ddd;">
                    <p style="font-size:12px;color:#888;text-align:center;">
                        ¿Tienes dudas? Escríbenos a 
                        <a href="mailto:soporte.polygraph@gmail.com" style="color:#e63939;text-decoration:none;">
                            soporte.polygraph@gmail.com
                        </a><br><br>¡Estamos listos para ayudarte! 😊
                    </p>
                </div>
                </body>
                </html>
                """.formatted(codigo);

        message.setContent(html, "text/html; charset=UTF-8");

        Transport.send(message);
    }
}
