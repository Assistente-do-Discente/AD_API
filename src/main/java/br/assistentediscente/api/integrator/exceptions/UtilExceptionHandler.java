package br.assistentediscente.api.integrator.exceptions;

import lombok.SneakyThrows;

import java.util.Objects;

public class UtilExceptionHandler {

    /**
     * Metodo para tratar as exeções
     * @param exceptionToValidate exception a qual deve ser tratada
     * @param runtimeException exception default a ser lançada caso a que estourou não seja BusinessException
     */
    @SneakyThrows
    public static void handleException(Exception exceptionToValidate, RuntimeException runtimeException) {
        if(exceptionToValidate instanceof BusinessException){
            throw exceptionToValidate;
        }
        if (exceptionHasDeeperCause(exceptionToValidate)){
            if (exceptionToValidate.getCause() instanceof BusinessException){
                throw exceptionToValidate.getCause();
            }
            throw new RuntimeException(exceptionToValidate.getCause());
        }
        throw runtimeException;
    }


    private static boolean exceptionHasDeeperCause(Exception exception) {
        return Objects.nonNull(exception.getCause());
    }
}
