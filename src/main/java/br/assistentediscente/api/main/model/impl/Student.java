package br.assistentediscente.api.main.model.impl;

import br.assistentediscente.api.integrator.institutions.KeyValue;
import br.assistentediscente.api.main.model.IInstitution;
import br.assistentediscente.api.main.model.IStudent;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static jakarta.persistence.GenerationType.SEQUENCE;

@Entity
@Data
@Table(name = "student")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Student implements IStudent {

    @SequenceGenerator(
            name = "student_generator_sequence",
            sequenceName = "student_data_sequence",
            allocationSize = 1
    )

    @GeneratedValue(
            strategy = SEQUENCE,
            generator = "student_generator_sequence"
    )

    @Id
    @Column(name = "id")
    private Long userKey;

    @Column(name = "external_id", length = 40, nullable = false, unique = true)
    private UUID externalKey;

    @Column(name = "roles", length = 20)
    private String roles;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "institution_id", nullable = false,
            foreignKey = @ForeignKey(name ="fk_student_institution"))
    private Institution institution;


    @OneToMany(mappedBy = "student",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER)
    private Set<AccessData> accessData;

    public Student(Long userKey){
        this.userKey = userKey;
    }

    public Long getId(){
        return userKey;
    }

    @Transient
    private List<KeyValue> keyValueList = new ArrayList<>();

    public List<KeyValue> getKeyValueList(){

        if (this.keyValueList == null || this.keyValueList.isEmpty()) {
            List<KeyValue> keyValues =  new ArrayList<>();

            for (AccessData accessData : this.accessData){
                KeyValue keyValue = KeyValue.builder()
                        .key(accessData.getKey())
                        .value(accessData.getValue())
                        .build();
                keyValues.add(keyValue);
            }
            setKeyValueList(keyValues);
            return this.keyValueList;
        }

        return this.keyValueList;
    }

    public IInstitution getEducationalInstitution(){
        return institution;
    }
}
