package br.assistentediscente.api.main.plataformservice;

import br.assistentediscente.api.integrator.exceptions.files.ErrorCouldNotDeleteFile;
import br.assistentediscente.api.integrator.exceptions.files.ErrorFileNotFound;
import br.assistentediscente.api.integrator.plataformeservice.EmailDetails;
import jakarta.activation.DataSource;
import jakarta.activation.FileDataSource;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.api.mailer.config.TransportStrategy;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;
import org.springframework.core.env.Environment;

import java.io.File;

public class EmailService {

    private static final String SMTP_HOST = "smtp_host";
    private static final String SMTP_PORT = "smtp_port";
    private static final String EMAIL_USER = "email_user";
    private static final String EMAIL_PASSWORD = "email_password";

    private static boolean sendEmail(EmailDetails emailDetails, Environment environment, boolean withAttachment)
            throws ErrorFileNotFound, ErrorCouldNotDeleteFile {
        try {
            validateFileExists(emailDetails.attachmentFilePath());

            Mailer mailer = buildMailer(environment);

            Email email = withAttachment ? buildEmailWithAttachment(emailDetails, environment)
                    : buildEmailWithoutAttachment(emailDetails, environment);

            Thread emailThread = new Thread(() -> {
                try {
                    mailer.sendMail(email);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    deleteFile(emailDetails.attachmentFilePath());
                }
            });

            emailThread.start();
            return true;
        } catch (ErrorFileNotFound | ErrorCouldNotDeleteFile e) {
            throw e;
        } catch (Exception e) {
            deleteFile(emailDetails.attachmentFilePath());
            return false;
        }
    }

    public static boolean sendEmailWithFileAttachment(EmailDetails emailDetails, Environment environment)
            throws ErrorFileNotFound, ErrorCouldNotDeleteFile {
        return sendEmail(emailDetails, environment, true);
    }

    public static boolean sendEmailWithoutFileAttachment(EmailDetails emailDetails, Environment environment)
            throws ErrorFileNotFound, ErrorCouldNotDeleteFile {
        return sendEmail(emailDetails, environment, false);
    }



    private static Mailer buildMailer(Environment environment) {
        String smtpHostValue = environment.getProperty(SMTP_HOST);
        int smtpPortValue = Integer.parseInt(environment.getProperty(SMTP_PORT));
        String emailUserValue = environment.getProperty(EMAIL_USER);
        String emailPasswordValue = environment.getProperty(EMAIL_PASSWORD);

        return MailerBuilder
                .withSMTPServer(smtpHostValue, smtpPortValue, emailUserValue, emailPasswordValue)
                .withTransportStrategy(TransportStrategy.SMTP_TLS)
                .withSessionTimeout(10 * 1000)
                .buildMailer();
    }

    private static Email buildEmailWithAttachment(EmailDetails emailDetails, Environment environment) {
        String emailUserValue = environment.getProperty(EMAIL_USER);
        DataSource dataSource = new FileDataSource(new File(emailDetails.attachmentFilePath()));

        return EmailBuilder.startingBlank()
                .from("Assistente Discente", emailUserValue)
                .to(emailDetails.recipientName(), emailDetails.recipientEmail())
                .withSubject(emailDetails.subject())
                .withPlainText(emailDetails.messageBody())
                .withAttachment(emailDetails.attachmentName(), dataSource)
                .buildEmail();
    }

    private static Email buildEmailWithoutAttachment(EmailDetails emailDetails, Environment environment) {
        String emailUserValue = environment.getProperty(EMAIL_USER);

        return EmailBuilder.startingBlank()
                .from("Assistente Discente", emailUserValue)
                .to(emailDetails.recipientName(), emailDetails.recipientEmail())
                .withSubject(emailDetails.subject())
                .withPlainText(emailDetails.messageBody())
                .buildEmail();
    }

    private static void validateFileExists(String filePath) throws ErrorFileNotFound {
        File file = new File(filePath);
        if(!file.exists() || !file.isFile()){
            throw new ErrorFileNotFound(new Object[]{filePath});
        }
    }

    private static void deleteFile(String filePath) throws ErrorCouldNotDeleteFile {
        File file = new File(filePath);
        if (!file.delete()){
            throw new ErrorCouldNotDeleteFile(new Object[]{filePath});
        }
    }
}
