package br.assistentediscente.api.main.model.impl;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(length = 500)
    private String description;

    @Column(name = "completed")
    private Boolean completed;

    @Column(name = "date")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime date;

    @ManyToOne
    @JoinColumn(name = "planner_id", nullable = false)
    @JsonAlias("plannerId")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Planner planner;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    @JsonAlias("studentId")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Student student;

    @JsonProperty("plannerId")
    public Long getPlannerId() {
        return planner != null ? planner.getId() : null;
    }

    @JsonProperty("plannerName")
    public String getPlannerName() {
        return planner != null ? planner.getName() : null;
    }
}