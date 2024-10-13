package br.assistentediscente.api.integrator.plataformeservice;

/**
 * Responsavel por armanezar os dados a serem utilizados no envio do email
 * @param recipientName nome do destinatário
 * @param recipientEmail e-mail do destinatário
 * @param subject assunto do e-mail
 * @param messageBody mensagem no corpo do e-mail
 * @param attachmentName nome a ser apresentado do arquivo enviado
 * @param attachmentFilePath caminho para o arquivo a ser enviado como anexo
 */
public record EmailDetails(
        String recipientName,
        String recipientEmail,
        String subject,
        String messageBody,
        String attachmentName,
        String attachmentFilePath
) {}
