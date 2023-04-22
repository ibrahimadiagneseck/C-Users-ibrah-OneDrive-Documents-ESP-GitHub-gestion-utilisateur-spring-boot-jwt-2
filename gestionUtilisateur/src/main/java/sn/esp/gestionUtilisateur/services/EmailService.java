package sn.esp.gestionUtilisateur.services;

import com.sun.mail.smtp.SMTPTransport;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

import static javax.mail.Message.RecipientType.CC;
import static javax.mail.Message.RecipientType.TO;
import static sn.esp.gestionUtilisateur.constant.EmailConstant.*;

@Service
public class EmailService {

    public void sendNewPasswordEmail(String firstName, String password, String email) throws MessagingException {
        // Crée un objet de message pour l'email à envoyer
        Message message = createEmail(firstName, password, email);

        // Établit une connexion SMTP avec le serveur Gmail
        SMTPTransport smtpTransport = (SMTPTransport) getEmailSession().getTransport(SIMPLE_MAIL_TRANSFER_PROTOCOL);
        smtpTransport.connect(GMAIL_SMTP_SERVER, USERNAME, PASSWORD);

        // Envoie le message à tous les destinataires
        smtpTransport.sendMessage(message, message.getAllRecipients());

        // Ferme la connexion SMTP
        smtpTransport.close();
    }

    private Message createEmail(String firstName, String password, String email) throws MessagingException {
        // Crée un nouvel objet de message MimeMessage avec la session d'email
        Message message = new MimeMessage(getEmailSession());

        // Définit l'adresse email de l'expéditeur
        message.setFrom(new InternetAddress(FROM_EMAIL));

        // Définit l'adresse email du destinataire principal
        message.setRecipients(TO, InternetAddress.parse(email, false));

        // Définit l'adresse email en copie conforme (CC)
        message.setRecipients(CC, InternetAddress.parse(CC_EMAIL, false));

        // Définit le sujet de l'email
        message.setSubject(EMAIL_SUBJECT);

        // Définit le contenu du message de l'email
        message.setText("Bonjour " + firstName + ", \n \n Votre nouveau mot de passe de compte est : " + password + "\n \n L'équipe de support");

        // Définit la date d'envoi de l'email
        message.setSentDate(new Date());

        // Sauvegarde les modifications du message
        message.saveChanges();

        // Retourne l'objet de message créé
        return message;
    }


    private Session getEmailSession() {
        // Crée une nouvelle session d'email en utilisant les propriétés de messagerie
        Properties properties = System.getProperties();

        // Définition du serveur SMTP de Gmail comme hôte
        properties.put(SMTP_HOST, GMAIL_SMTP_SERVER);

        // Activation de l'authentification SMTP
        properties.put(SMTP_AUTH, true);

        // Définition du port SMTP par défaut
        properties.put(SMTP_PORT, DEFAULT_PORT);

        // Activation de la connexion TLS au démarrage
        properties.put(SMTP_STARTTLS_ENABLE, true);

        // Exigence de la connexion TLS obligatoire
        properties.put(SMTP_STARTTLS_REQUIRED, true);

        // Retourne une instance de Session avec les propriétés configurées
        return Session.getInstance(properties, null);

    }
}
