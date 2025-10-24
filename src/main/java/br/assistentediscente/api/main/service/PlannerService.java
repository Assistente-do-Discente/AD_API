package br.assistentediscente.api.main.service;

import br.assistentediscente.api.main.model.impl.Planner;
import br.assistentediscente.api.main.model.impl.Student;
import br.assistentediscente.api.main.model.impl.Task;
import br.assistentediscente.api.main.repository.PlannerRepository;
import br.assistentediscente.api.main.repository.TaskRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class PlannerService {

    private final PlannerRepository plannerRepository;
    private final TaskRepository taskRepository;

    public PlannerService(PlannerRepository plannerRepository, TaskRepository taskRepository) {
        this.plannerRepository = plannerRepository;
        this.taskRepository = taskRepository;
    }

    public Map<String, String> create(Map<String, String> parameters) {
        Planner planner = new ObjectMapper().convertValue(parameters, Planner.class);
        plannerRepository.save(planner);
        return Map.of("response", "Agenda criada com sucesso!");
    }

    public Map<String, String> createTask(Map<String, String> parameters) {
        Task task = new ObjectMapper().convertValue(parameters, Task.class);
        task.setStudent(new Student(Long.parseLong(parameters.get("studentId"))));

        Planner planner = findById(Long.parseLong(parameters.get("plannerId")));
        task.setPlanner(planner);
        planner.addTask(task);
        plannerRepository.save(planner);
        return Map.of("response", "Lembrete criado com sucesso!");
    }

    public Planner findById(Long plannerId) {
        return plannerRepository.findById(plannerId).orElse(null);
    }

    public Map<String, String> getAll(Map<String, String> parameters) {
        Long studentId = Long.parseLong(parameters.get("studentId"));
        List<Planner> planners = plannerRepository.findAllByStudent(new Student(studentId));
        if (planners.isEmpty()) {
            return Map.of("response", "Nenhuma agenda encontrada!");
        } else {
            return Map.of("response", new ObjectMapper().convertValue(planners, String.class));
        }
    }

    public Map<String, String> getAllTask(Map<String, String> parameters) {
        Long studentId = Long.parseLong(parameters.get("studentId"));
        List<Task> tasks = taskRepository.findAllByStudent(new Student(studentId));
        if (tasks.isEmpty()) {
            return Map.of("response", "Nenhuma tarefa encontrada!");
        } else {
            return Map.of("response", new ObjectMapper().convertValue(tasks, String.class));
        }
    }

    public Map<String, String> delete(Map<String, String> parameters) {
        Long plannerId = Long.parseLong(parameters.get("plannerId"));
        Student student = new Student(Long.parseLong(parameters.get("studentId")));

        if (plannerRepository.existsByIdAndStudent(plannerId, student)) {
            plannerRepository.deleteById(plannerId);
            return Map.of("response", "Agenda apagada com sucesso!");
        } else {
            return Map.of("response", "Nenhuma agenda foi encontrada!");
        }
    }

    public Map<String, String> deleteTask(Map<String, String> parameters) {
        Long taskId = Long.parseLong(parameters.get("taskId"));
        Student student = new Student(Long.parseLong(parameters.get("studentId")));

        if (taskRepository.existsByIdAndStudent(taskId, student)) {
            taskRepository.deleteById(taskId);
            return Map.of("response", "Lembrete apagado com sucesso!");
        } else {
            return Map.of("response", "Nenhum lembrete foi encontrado!");
        }
    }

    public Map<String, String> getTasksByDate(Map<String, String> parameters) {
        Long studentId = Long.parseLong(parameters.get("studentId"));
        LocalDateTime date = LocalDateTime.parse(parameters.get("date"));
        List<Task> tasks = taskRepository.findTaskByStudentAndDate(new Student(studentId), date);
        if (tasks.isEmpty()) {
            return Map.of("response", "Nenhuma tarefa encontrada!");
        } else {
            return Map.of("response", new ObjectMapper().convertValue(tasks, String.class));
        }
    }
}
