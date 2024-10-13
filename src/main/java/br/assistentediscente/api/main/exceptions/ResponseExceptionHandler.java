package br.assistentediscente.api.main.exceptions;

import br.assistentediscente.api.integrator.exceptions.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Objects;

@ControllerAdvice
public class ResponseExceptionHandler {

    @Autowired
    private MessageSource messageSource;

    @ExceptionHandler({RuntimeException.class})
    public ResponseEntity<BusinessExceptionResponse> handleIntentNotSupported(final RuntimeException exception) {
        BusinessExceptionResponse response = new BusinessExceptionResponse();
        if(exception instanceof BusinessException) {
             return handleBusinessExceptionData((BusinessException) exception);
        } else if (Objects.nonNull(exception.getCause()) &&
                exception.getCause() instanceof BusinessException ) {
            return handleBusinessExceptionData((BusinessException) exception.getCause());
        }
        response.setStatus(HttpStatus.NOT_FOUND.value());
        response.setMessage((exception.getCause() != null) ? exception.getCause().getMessage() : exception.getMessage());
        return ResponseEntity.status(response.getStatus())
                .body(response);
    }

    private ResponseEntity<BusinessExceptionResponse> handleBusinessExceptionData(BusinessException exception) {
        BusinessExceptionResponse response = new BusinessExceptionResponse();
        response.setStatus(exception.getCode());
        if(exception.hasBusinessMessage()){
            response.setMessage(messageSource.getMessage(exception.getMessage(), exception.getParameters(), LocaleContextHolder.getLocale()));
        }else{
            response.setMessage(exception.getMessage());
        }
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
