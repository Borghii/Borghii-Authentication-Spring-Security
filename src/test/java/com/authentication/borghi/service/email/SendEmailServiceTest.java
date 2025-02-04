package com.authentication.borghi.service.email;

import com.authentication.borghi.service.email.SendEmailService;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

//@SpringBootTest // Usa las propiedades de application.properties
//@ActiveProfiles("test")
//class SendEmailServiceTest {
//
//    @Autowired
//    private SendEmailService sendEmailService;
//
//    @Autowired
//    private GreenMailBean greenMailBean;
//
//
//    @BeforeAll
//    static void startMailServer() {
//        greenMail = new GreenMail(new ServerSetup(3025, null, "smtp")); // Usa el puerto de application.properties
//        greenMail.start();
//    }
//
//    @AfterAll
//    static void stopMailServer() {
//        greenMail.stop();
//    }
//
//    @Test
//    void testSendEmail() throws Exception {
//        // Enviar un correo con el servicio real
//        String recipient = "test@ejemplo.com";
//        String subject = "Asunto de prueba";
//        String body = "Mensaje de prueba";
//
//        sendEmailService.sendEmail(recipient, subject, body);
//
//        // Verificar que el correo fue recibido
//        MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
//        assertEquals(1, receivedMessages.length);
//        assertEquals(subject, receivedMessages[0].getSubject());
//        assertTrue(receivedMessages[0].getContent().toString().trim().contains(body));
//    }
//}
