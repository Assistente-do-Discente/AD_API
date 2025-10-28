package br.assistentediscente.api.main.model.impl;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "planner")
public class Planner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(length = 500)
    private String description;

    @OneToMany(mappedBy = "planner", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Task> tasks;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    @JsonAlias("studentId")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Student student;

    public Planner(Long id) {
        setId(id);
    }

    public Planner(String id) {
        setId(Long.valueOf(id));
    }

    public void addTask(Task task) {
        this.tasks.add(task);
    }
}
