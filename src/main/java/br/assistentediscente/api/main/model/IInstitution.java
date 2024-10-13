package br.assistentediscente.api.main.model;

public interface IInstitution {

   Long getId();
   String getShortName();
   String getSaudationPhrase();
   String getPluginClass();
   String getUsernameFieldName();
   String getPasswordFieldName();
}
