package br.assistentediscente.api.main.model.impl;

import br.assistentediscente.api.main.model.IInstitution;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.SEQUENCE;

@Entity
@Table(name = "institution")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Institution implements IInstitution {

    @SequenceGenerator(
            name = "institution_generator_sequence",
            sequenceName = "institution_sequence",
            allocationSize = 1
    )

    @GeneratedValue(
            strategy = SEQUENCE,
            generator = "institution_generator_sequence"
    )

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "short_name", length = 10, nullable = false, unique = true)
    private String shortName;

    @Column(name = "saudation_phrase", length = 50, nullable = false)
    private String saudationPhrase;

    @Column(name = "plugin_class", length = 50, nullable = false, unique = true)
    private String pluginClass;

    @Column(name = "username_field_name", length = 15, nullable = false)
    private String usernameFieldName;

    @Column(name = "password_field_name", length = 15, nullable = false)
    private String passwordFieldName;

}
