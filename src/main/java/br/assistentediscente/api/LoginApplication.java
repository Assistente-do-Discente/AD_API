package br.assistentediscente.api;

import br.assistentediscente.api.main.model.impl.AIApiData;
import br.assistentediscente.api.main.model.impl.Institution;
import br.assistentediscente.api.main.repository.AIApiDataRepository;
import br.assistentediscente.api.main.repository.InstitutionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

@SpringBootApplication(scanBasePackages = {
		"br.assistentediscente.api.*"
})
@EntityScan(basePackageClasses = {Jsr310JpaConverters.class},
		basePackages = {
				"br.assistentediscente.api.*"
		})
public class LoginApplication {


	public static void main(String[] args) {
		SpringApplication.run(LoginApplication.class, args);
		System.out.println("Fim inicialização");
	}

	@Bean
	public CommandLineRunner
	commandLineRunner(InstitutionRepository institutionRepository, AIApiDataRepository aiApiDataRepository) {
		return args -> {

			institutionRepository.saveAndFlush(Institution
					.builder()
							.shortName("UEG")
							.saudationPhrase("Entre com seu login do ADMS")
							.pluginClass("ueg.UEGPlugin")
							.usernameFieldName("CPF")
							.passwordFieldName("Senha")
					.build());

			aiApiDataRepository.saveAndFlush(AIApiData
					.builder()
					.shortName("Maritalk")
							.active(true)
							.keyEnvPath("maritalk.key")
							.urlApi("https://chat.maritaca.ai/api/chat/inference")
							.paramsApi("{\"messages\":[{\"role\":\"user\", \"content\": \"?\"}]," +
									"\"do_sample\": \"False\",\"max_tokens\": 40," +
									"\"temperature\": 0.5,\"model\": \"sabia-3\"}")
							.classpathApi("maritalk_api.MaritalkApi")
					.build());
		};
	}
}
