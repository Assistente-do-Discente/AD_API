package br.assistentediscente.api.main.plataformservice;

import br.assistentediscente.api.integrator.exceptions.files.ErrorCouldNotCreateFile;
import br.assistentediscente.api.integrator.exceptions.files.ErrorCouldNotDeleteFile;
import br.assistentediscente.api.integrator.exceptions.files.ErrorFileNotFound;
import br.assistentediscente.api.integrator.plataformeservice.EmailDetails;
import br.assistentediscente.api.integrator.plataformeservice.IPlataformService;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.nio.file.Path;

@Service
public class PlataformServiceImpl implements IPlataformService {

    private final Environment environment;

    public PlataformServiceImpl(Environment environment) {
        this.environment = environment;
    }

    public String HTMLToPDF(String htmlString, Path folderPath, String filePrefix)
            throws ErrorCouldNotCreateFile {
        return HTMLToPDF.generate(htmlString, folderPath, filePrefix);
    }

    public boolean sendEmailWithFileAttachment(EmailDetails emailDetails)
            throws ErrorFileNotFound, ErrorCouldNotDeleteFile {
        return EmailService.sendEmailWithFileAttachment(emailDetails, environment);
    }
}
