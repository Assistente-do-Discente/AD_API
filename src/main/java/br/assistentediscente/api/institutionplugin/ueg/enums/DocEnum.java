package br.assistentediscente.api.institutionplugin.ueg.enums;

import java.nio.file.Path;
import java.nio.file.Paths;

public enum DocEnum {

    ATTENDANCE_DECLARATION(Paths.get("src/main/java/br/assistentediscente/api/" +
            "institutionplugin/ueg/temporary_pdf_docs/attendence_declaration"),"attendance_"),

    ACADEMIC_RECORD(Paths.get("src/main/java/br/assistentediscente/api/" +
            "institutionplugin/ueg/temporary_pdf_docs/academic_record"), "record_"),

    ;
    private final Path folderPath;
    private final String filePrefix;

    DocEnum(final Path folderPath, final String filePrefix){
        this.folderPath = folderPath;
        this.filePrefix = filePrefix;
    }

    public Path getFolderPath() {
        return folderPath;
    }

    public String getFilePrefix() {
        return filePrefix;
    }
}
