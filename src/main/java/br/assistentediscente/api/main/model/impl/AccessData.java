package br.assistentediscente.api.main.model.impl;

import br.assistentediscente.api.main.model.IAccessData;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import static jakarta.persistence.GenerationType.SEQUENCE;

@Entity
@Table(name = "access_data")
@Getter
@Setter
public class AccessData implements IAccessData {

    @SequenceGenerator(
            name = "access_data_generator_sequence",
            sequenceName = "access_data_sequence",
            allocationSize = 1
    )

    @GeneratedValue(
            strategy = SEQUENCE,
            generator = "access_data_generator_sequence"
    )

    @Id
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id",
            referencedColumnName = "id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_student_access_data"))
    private Student student;

    @Column(name = "token_key", nullable = false, length = 100)
    private String key;

    @Column(name = "token_value", nullable = false, length = 300)
    private String value;

}
