package br.assistentediscente.api.main.reflection;

import br.assistentediscente.api.integrator.exceptions.institution.InstitutionPackageNotFoundException;
import br.assistentediscente.api.integrator.exceptions.serviceplugin.*;
import br.assistentediscente.api.integrator.institutions.IBaseInstitutionPlugin;
import br.assistentediscente.api.integrator.plataformeservice.IPlataformService;
import br.assistentediscente.api.integrator.serviceplugin.parameters.AParameter;
import br.assistentediscente.api.integrator.serviceplugin.parameters.ParameterValue;
import br.assistentediscente.api.integrator.serviceplugin.service.IServicePlugin;
import br.assistentediscente.api.main.dto.ServiceAndActivationNames;
import br.assistentediscente.api.main.model.IInstitution;
import br.assistentediscente.api.main.model.IStudent;
import br.assistentediscente.api.main.service.AIService;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.lang.reflect.Constructor;
import java.util.*;

import static br.assistentediscente.api.integrator.exceptions.UtilExceptionHandler.handleException;

public class Reflection {

    private final String generalServicesPluginPackage = "br.assistentediscente.api.main.serviceplugin.service.impl";
    private final String generalServicesParameterPackage = "br.assistentediscente.api.main.serviceplugin.parameters.impl";

    /**
     Instacia um novo BaseInstitutionRequest da universidade utilizando o construtor com o parametro de IStudent para
     setar os tokens dos requests
     */
    protected IBaseInstitutionPlugin getInstitutionPlugin(String institutionPackage,IStudent student){
        try {
            Class<?> institutionRequestClass = getInstitutionPluginClass(institutionPackage, student.getEducationalInstitution());
            Constructor<?> institutionRequestConstructor = institutionRequestClass.getConstructor(IStudent.class);

            return (IBaseInstitutionPlugin) institutionRequestConstructor.newInstance(student);
        }catch (Exception e){
            handleException(e, new InstitutionPackageNotFoundException());
        }
        return null;
    }

    /**
     * Cria um Class a partir de um IBaseInstitution usando o pluginClass
     */
    private  Class<?> getInstitutionPluginClass(String institutionPackage, IInstitution baseInstitution){
        String institutionPluginClassName = baseInstitution.getPluginClass();
        try {
            return Class.forName(institutionPackage + institutionPluginClassName);
        }catch (Exception e){
            throw new InstitutionPackageNotFoundException(new Object[]{baseInstitution.getShortName()});
        }
    }

    protected IBaseInstitutionPlugin getInstitutionPlugin(String institutionPackage, IInstitution baseInstitution){
        try{
            Class<?> institutionRequestClass = getInstitutionPluginClass(institutionPackage,baseInstitution);
            Constructor<?> institutionRequestConstructor = institutionRequestClass.getConstructor();

            return (IBaseInstitutionPlugin) institutionRequestConstructor.newInstance();
        }catch (Exception e){
            handleException(e, new InstitutionPackageNotFoundException());
        }
        return null;
    }


    /**
     * Metodo responsavel por instanciar e chamar o serviço de acordo com o {@code activationServiceName}
     * @param activationServiceName Nome proximo dos nomes do serviço a ser chamado
     * @param institutionPlugin Instituição do usurio que chamou o serviço e ira fornecer os dados
     * @param parameters Parametros utilizados pelo serviço em sua execução e passados pelo usuario
     * @param aiService Serviço de AI para encontrar o serviço a partir de comparação com os
     *                  nomes do serviço e o recebido em {@code activaionServiceName}
     * @param plataformService Serviços fornecidos pelo AD para auxiliar os dev de instituição com serviços comuns e prontos
     * @return Mensagem final para o usuario, de acordo com a execução do serviço
     */
    protected String invokeServicePlugin(String activationServiceName, IBaseInstitutionPlugin institutionPlugin,
                                         Map<String,String> parameters, AIService aiService,
                                         IPlataformService plataformService){
        try {
            activationServiceName = activationServiceName.replace("-", " ");
            List<Class<? extends IServicePlugin>> servicesClass = new ArrayList<>(getAllGeneralServicesClass().stream().toList());
            servicesClass.addAll(institutionPlugin.getAllServicePlugins());
            IServicePlugin servicePlugin = getServicePluginByActivationName(activationServiceName, servicesClass, aiService);

            Set<ParameterValue> parameterValues = getParameterValues(servicePlugin,
                    institutionPlugin, parameters);

            return servicePlugin.doService(institutionPlugin, parameterValues, plataformService);
        }catch (Exception e){
            handleException(e, new ServiceCouldNotExecute());
            return null;
        }
    }

    private Set<ParameterValue> getParameterValues(IServicePlugin service,
                                                    IBaseInstitutionPlugin institutionPlugin,
                                                    Map<String, String> parameterMap) {

        if (!service.getParameters().isEmpty()) {
            try {
                List<AParameter> serviceParameters = service.getParameters();

                Set<ParameterValue> parameterValues = new HashSet<>();
                Set<Class<? extends AParameter>> generalParametersClass = getAllGeneralServicesParametersClass();

                for (AParameter serviceParameter : serviceParameters) {
                    AParameter parameter = getAParameterFromGeneralByName(serviceParameter.getName(),
                            generalParametersClass);

                    if (Objects.nonNull(parameter)) {
                        getParameterValueFromGeneral(institutionPlugin,
                                parameterMap, parameterValues, parameter);
                    }
                }

                // para caso o service tenha um parametro proprio da instituição
                parameterValues.addAll(service.getParameterValues(institutionPlugin, parameterMap));
                checkAllParametersFilled(serviceParameters, parameterValues);

                return parameterValues;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return Set.of();
    }

    private static void checkAllParametersFilled(List<AParameter> serviceParameters, Set<ParameterValue> parameterValues) {
        if (!serviceParameters.isEmpty()) {
            if (!parameterValues.isEmpty() && parameterValues.size() == serviceParameters.size()) {
                Set<String> missingParameters = getMissingParameters(serviceParameters, parameterValues);

                if (!missingParameters.isEmpty()) {
                    throw new ServiceParameterNotFound("Missing parameters: " + String.join(", ", missingParameters));
                }
            } else {
                throw new ServiceParameterNotFilled();
            }
        }
    }

    private static Set<String> getMissingParameters(List<AParameter> serviceParameters,
                                                    Set<ParameterValue> parameterValues) {
        Set<String> missingParameters = new HashSet<>();

        for (AParameter serviceParameter : serviceParameters) {
            boolean found = false;
            for (ParameterValue parameterValue : parameterValues) {
                if (serviceParameter.getName().equals(parameterValue.getParameter().getName())) {
                    found = Objects.nonNull(parameterValue.getValue());
                    break;
                }
            }
            if (!found) {
                missingParameters.add(serviceParameter.getName());
            }
        }
        return missingParameters;
    }

    private static void getParameterValueFromGeneral(IBaseInstitutionPlugin institutionPlugin,
                                                     Map<String, String> parameterMap,
                                                     Set<ParameterValue> parameterValues, AParameter parameter) {
        switch (parameter.getType()) {

            case AUTO -> {
                parameterValues.add(new ParameterValue(parameter,
                        parameter.getValueFromInstitution(institutionPlugin)));
            }
            case FIXED -> {
                parameterValues.add(new ParameterValue(parameter, parameter.getDefaultValue()));

            }
            case OPTIONAL -> {
                if (parameterMap.containsKey(parameter.getName())) {
                    Object value = parameter.getObjectValue(parameterMap.get(parameter.getName()));
                    parameterValues.add(new ParameterValue(parameter, value));

                } else {
                    parameterValues.add(new ParameterValue(parameter, parameter.getDefaultValue()));
                }
            }
            case MANDATORY -> {
                if (parameterMap.containsKey(parameter.getName())) {
                    Object value = parameter.getObjectValue(parameterMap.get(parameter.getName()));
                    parameterValues.add(new ParameterValue(parameter, value));
                } else {
                    throw new MandatoryParameterNotFilled(new Object[]{parameter.getName()});
                }

            }
            default -> throw new ParameterTypeNotSupported();
        }
    }

    private IServicePlugin getServicePluginByActivationName(String activationServiceName,
                                                            List<Class<? extends IServicePlugin>> generalServicesClass,
                                                            AIService aiService) {
        try {
            List<ServiceAndActivationNames> serviceAndActivationNames = new ArrayList<>();

            for (Class<? extends IServicePlugin> generalServiceClass : generalServicesClass) {
                IServicePlugin serviceObject = generalServiceClass.getDeclaredConstructor()
                        .newInstance();

                serviceAndActivationNames.add(new ServiceAndActivationNames(serviceObject,
                        serviceObject.getClass().getSimpleName(), serviceObject.getActivationName()));
            }

            String serviceName = aiService.getServicePluginByActivationName(activationServiceName,
                    serviceAndActivationNames);

            if (Objects.nonNull(serviceName) && !serviceName.isEmpty()
                    && !serviceName.equalsIgnoreCase("NENHUM")) {
                Optional<ServiceAndActivationNames> matchingService = serviceAndActivationNames.stream()
                        .filter(serviceAndActivationName ->
                                serviceAndActivationName.serviceName().equalsIgnoreCase(serviceName))
                        .findFirst();

                if (matchingService.isPresent()) {
                    return matchingService.get().service();
                }
            }

        }catch (Exception e){
            throw new ServicePluginNotFound();
        }
        throw new ServicePluginNotFound();
    }

    private Set<Class<? extends IServicePlugin>> getAllGeneralServicesClass() {
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage(generalServicesPluginPackage))
                .setScanners(new SubTypesScanner(false))
                .filterInputsBy(new FilterBuilder().includePackage(generalServicesPluginPackage)));

       return reflections.getSubTypesOf(IServicePlugin.class);

    }

    private Set<Class<? extends AParameter> > getAllGeneralServicesParametersClass() {
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage(generalServicesParameterPackage))
                .setScanners(new SubTypesScanner(false))
                .filterInputsBy(new FilterBuilder().includePackage(generalServicesParameterPackage)));

        return reflections.getSubTypesOf(AParameter.class);

    }

    private AParameter getAParameterFromGeneralByName(String parameterName, Set<Class<? extends AParameter>> generalParametersClass) throws Exception {
        for (Class<? extends AParameter> parameterClass : generalParametersClass) {
            if (parameterClass.getSimpleName().equalsIgnoreCase(parameterName)) {
                return parameterClass.getDeclaredConstructor().newInstance();
            }
        }
        return null;
    }

    protected List<String> getFirstActivationNameForServices(Set<Class<? extends IServicePlugin>> servicesClass) {
        try {
            return servicesClass.stream()
                    .map(serviceClass -> {
                        try {
                            IServicePlugin serviceObject = serviceClass.getDeclaredConstructor().newInstance();
                            return serviceObject.getActivationName().stream().findFirst().orElse(null);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .filter(Objects::nonNull)
                    .toList();
        }catch (Exception e){
            return List.of("Não foi possível encontrar os serviços próprios da sua instituição," +
                    " entre em contato com eles para mais informações.");
        }
    }

}
