package io.github.Rednatsumic.peliculas.service;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.lang.NonNull;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

/**
 * Servicio de envío de emails.
 * - En desarrollo puede no haber SMTP configurado: en ese caso hace NO-OP
 *   (imprime por consola) para no romper el flujo de registro.
 */
@Service
@RequiredArgsConstructor
public class MailService {

    private final ObjectProvider<JavaMailSender> mailSenderProvider;

    /**
     * Envía un email de texto plano. Si JavaMailSender no está disponible
     * (por falta de configuración), solo loguea el contenido.
     */
    public void send(@NonNull String to, @NonNull String subject, @NonNull String text){
        JavaMailSender sender = mailSenderProvider.getIfAvailable();
        if (sender == null) {
            // Sin configuración de correo, hacemos no-op (útil en desarrollo)
            System.out.println("[MailService] (NO-OP) To="+to+" | Subject="+subject+"\n"+text);
            return;
        }
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject(subject);
        msg.setText(text);
        sender.send(msg);
    }
}
