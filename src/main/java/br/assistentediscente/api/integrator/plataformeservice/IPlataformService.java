package br.assistentediscente.api.integrator.plataformeservice;

import br.assistentediscente.api.integrator.exceptions.files.ErrorCouldNotCreateFile;
import br.assistentediscente.api.integrator.exceptions.files.ErrorCouldNotDeleteFile;
import br.assistentediscente.api.integrator.exceptions.files.ErrorFileNotFound;

import java.nio.file.Path;

public interface IPlataformService {

    /**
     * Responsavel por gerar um arquivo pdf a partir de um codigo html
     * @param htmlString codigo HTML a ser convertido em PDF
     * @param folderPath caminho da pasta onde será criado o arquivo
     * @param filePrefix nome padrão para o arquivo exemplo: "declaracao_"
     * @return caminho para o novo documento criado
     */
    String HTMLToPDF(String htmlString, Path folderPath, String filePrefix) throws ErrorCouldNotCreateFile;

    /**
     * Responsavel por enviar emails como arquivos anexados, após o envio o arquivo é deletado
     * @param emailDetails dados que serão utilizados para envio o email
     * @return {@code true} caso o envio e delete da mensagem ocorram corretamente
     */
    boolean sendEmailWithFileAttachment(EmailDetails emailDetails) throws ErrorFileNotFound, ErrorCouldNotDeleteFile;
}
