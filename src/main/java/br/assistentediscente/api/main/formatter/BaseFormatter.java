package br.assistentediscente.api.main.formatter;

import br.assistentediscente.api.main.enums.RomanNumber;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.List;

public class BaseFormatter {

    protected MessageSource messageSource;

    public BaseFormatter(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    protected String getMessage(IFormatterResponse formatterResponse, Object... params ) {
        return messageSource.getMessage(formatterResponse.getMessage(), params,
                LocaleContextHolder.getLocale()) + " ";
    }

    protected static String switchRomanNumberToNumber(String disciplineName) {
        List<String> romanNumbers = RomanNumber.getAllRomanNumbersString();

        for (String romanNumber : romanNumbers) {
            if (disciplineName.endsWith(" "+romanNumber)) {
                disciplineName = disciplineName.substring(0, disciplineName.length()-romanNumber.length());
                disciplineName = disciplineName.concat(RomanNumber.getOrdinalByNumber(romanNumber).toUpperCase());
            }
        }
        return disciplineName;
    }
}
