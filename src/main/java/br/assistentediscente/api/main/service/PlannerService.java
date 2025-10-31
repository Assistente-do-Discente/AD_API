package br.assistentediscente.api.main.service;

import br.assistentediscente.api.institutionplugin.ueg.converter.ParameterTool;
import br.assistentediscente.api.integrator.converter.IBaseTool;
import br.assistentediscente.api.integrator.converter.IResponseTool;
import br.assistentediscente.api.integrator.enums.ParameterType;
import br.assistentediscente.api.main.dto.ResponseToolDTO;
import br.assistentediscente.api.main.dto.ToolDTO;
import br.assistentediscente.api.main.model.impl.Planner;
import br.assistentediscente.api.main.model.impl.Student;
import br.assistentediscente.api.main.model.impl.Task;
import br.assistentediscente.api.main.repository.PlannerRepository;
import br.assistentediscente.api.main.repository.TaskRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.logging.log4j.util.Strings;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
public class PlannerService {

    private final PlannerRepository plannerRepository;
    private final TaskRepository taskRepository;
    private final StudentService studentService;

    public PlannerService(PlannerRepository plannerRepository, TaskRepository taskRepository, StudentService studentService) {
        this.plannerRepository = plannerRepository;
        this.taskRepository = taskRepository;
        this.studentService = studentService;
    }

    public IResponseTool create(Map<String, String> parameters) {
        Planner planner = objectMapper().convertValue(parameters, Planner.class);
        plannerRepository.save(planner);
        return new ResponseToolDTO("Agenda criada com sucesso!", planner);
    }

    public IResponseTool update(Map<String, String> parameters) {
        Planner plannerUpdate = objectMapper().convertValue(parameters, Planner.class);
        var plannerDb = getByIdPlanner(plannerUpdate.getId(), plannerUpdate.getStudent().getId());
        if (plannerDb == null) {
            return new ResponseToolDTO("Agenda não encontrada", "");
        }

        if (Strings.trimToNull(plannerUpdate.getName()) != null) {
            plannerDb.setName(plannerUpdate.getName());
        }
        if (Strings.trimToNull(plannerUpdate.getDescription()) != null) {
            plannerDb.setDescription(plannerUpdate.getDescription());
        }

        var updated = plannerRepository.save(plannerDb);
        return new ResponseToolDTO("Agenda atualizada com sucesso!", updated);
    }

    public IResponseTool delete(Map<String, String> parameters) {
        Long plannerId = Long.parseLong(parameters.get("plannerId"));
        Long studentId = Long.parseLong(parameters.get("studentId"));
        var plannerDb = getByIdPlanner(plannerId, studentId);
        if (plannerDb == null) {
            return new ResponseToolDTO("Agenda não encontrada", "");
        }

        plannerRepository.deleteById(plannerId);
        return new ResponseToolDTO("Agenda apagada com sucesso!", "");
    }

    public IResponseTool getAll(Map<String, String> parameters) throws JsonProcessingException {
        Long studentId = Long.parseLong(parameters.get("studentId"));
        List<Planner> planners = plannerRepository.findAllByStudent(new Student(studentId));
        if (planners.isEmpty()) {
            return new ResponseToolDTO("Nenhuma agenda encontrada!", "");
        } else {
            return new ResponseToolDTO("As agendas encontradas", planners);
        }
    }

    public IResponseTool createTask(Map<String, String> parameters) {
        Task task = objectMapper().convertValue(parameters, Task.class);
        taskRepository.save(task);
        return new ResponseToolDTO("Lembrete criado com sucesso!", task);
    }

    public IResponseTool updateTask(Map<String, String> parameters) {
        Task taskUpdate = objectMapper().convertValue(parameters, Task.class);
        var taskDb = getByIdTask(taskUpdate.getId(), taskUpdate.getStudent().getId());
        if (taskDb == null) {
            return new ResponseToolDTO("Tarefa não encontrada", "");
        }

        if (Strings.trimToNull(taskUpdate.getTitle()) != null) {
            taskDb.setTitle(taskUpdate.getTitle());
        }
        if (Strings.trimToNull(taskUpdate.getDescription()) != null) {
            taskDb.setDescription(taskUpdate.getDescription());
        }
        if (taskUpdate.getDate() != null) {
            taskDb.setDate(taskUpdate.getDate());
        }
        if (taskUpdate.getPlanner() != null && taskUpdate.getPlanner().getId() != null) {
            taskDb.setPlanner(taskUpdate.getPlanner());
        }
        if (taskUpdate.getCompleted() != null) {
            taskDb.setCompleted(taskUpdate.getCompleted());
        }

        var updated = taskRepository.save(taskDb);
        return new ResponseToolDTO("Tarefa atualizada com sucesso!", updated);
    }

    public IResponseTool deleteTask(Map<String, String> parameters) {
        Long taskId = Long.parseLong(parameters.get("taskId"));
        Long studentId = Long.parseLong(parameters.get("studentId"));
        var taskDb = getByIdTask(taskId, studentId);
        if (taskDb == null) {
            return new ResponseToolDTO("Tarefa não encontrada", "");
        }

        taskRepository.deleteById(taskId);
        return new ResponseToolDTO("Tarefa apagada com sucesso!", "");
    }

    public IResponseTool getAllTask(Map<String, String> parameters) {
        Long studentId = Long.parseLong(parameters.get("studentId"));
        List<Task> tasks = taskRepository.findAllByStudent(new Student(studentId));
        if (tasks.isEmpty()) {
            return new ResponseToolDTO("Nenhuma tarefa encontrada!", "");
        } else {
            return new ResponseToolDTO("As tarefas encontradas", tasks);
        }
    }

    public IResponseTool getTasksByDate(Map<String, String> parameters) {
        Long studentId = Long.parseLong(parameters.get("studentId"));

        LocalDate date = LocalDate.parse(parameters.get("date").substring(0, 10));
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
        List<Task> tasks = taskRepository.findTaskByStudentAndDateBetween(new Student(studentId), startOfDay, endOfDay);

        if (tasks.isEmpty()) {
            return new ResponseToolDTO("Nenhuma tarefa encontrada!", "");
        } else {
            return new ResponseToolDTO("Tarefas pela data", tasks);
        }
    }

    public IResponseTool markAsCompleted(Map<String, String> parameters) {
        Long taskId = Long.parseLong(parameters.get("taskId"));
        Long studentId = Long.parseLong(parameters.get("studentId"));
        var taskDb = getByIdTask(taskId, studentId);
        if (taskDb == null) {
            return new ResponseToolDTO("Tarefa não encontrada", "");
        }

        taskDb.setCompleted(true);

        var updated = taskRepository.save(taskDb);
        return new ResponseToolDTO("Atividade marcada como concluída!", updated);
    }

    public IResponseTool markAsNotCompleted(Map<String, String> parameters) {
        Long taskId = Long.parseLong(parameters.get("taskId"));
        Long studentId = Long.parseLong(parameters.get("studentId"));
        var taskDb = getByIdTask(taskId, studentId);
        if (taskDb == null) {
            return new ResponseToolDTO("Tarefa não encontrada", "");
        }

        taskDb.setCompleted(false);

        var updated = taskRepository.save(taskDb);
        return new ResponseToolDTO("Atividade marcada como não concluída!", updated);
    }

    private ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

    public Planner getByIdPlanner(Long id, Long studentId){
        Optional<Planner> byId = plannerRepository.findByIdAndStudent(id, new Student(studentId));
        return byId.orElse(null);
    }

    public Task getByIdTask(Long id, Long studentId){
        Optional<Task> byId = taskRepository.findByIdAndStudent(id, new Student(studentId));
        return byId.orElse(null);
    }

    public List<IBaseTool> getToolsPlanner() {
        return new ArrayList<>(List.of(
                ToolDTO.tool(
                        "createPlanner",
                        "Ferramenta para criar uma agenda que possa adicionar tarefas/lembretes",
                        this::create,
                        Map.of(
                                "name", ParameterTool.stringParam(
                                        "Nome para a agenda", ParameterType.MANDATORY, null, null
                                ),
                                "description", ParameterTool.stringParam(
                                        "Descrição para a agenda", ParameterType.OPTIONAL, null, null
                                ),
                                "studentId", ParameterTool.autoParam(
                                        "Identificador do estudante para criar a agenda, esse parâmetro é automatico e não deve ser enviado", this::getStudentId
                                )
                        )
                ),
                ToolDTO.tool(
                        "getPlanners",
                        "Ferramenta para buscar todas as agendas criadas",
                        this::getAll,
                        Map.of(
                                "studentId", ParameterTool.autoParam(
                                        "Identificador do estudante para criar a agenda, esse parâmetro é automatico e não deve ser enviado", this::getStudentId
                                )
                        )
                ),
                ToolDTO.tool(
                        "updatePlanner",
                        "Ferramenta para editar uma agenda",
                        this::update,
                        Map.of(
                                "id", ParameterTool.numberParam(
                                        "Id da agenda que será editada", ParameterType.MANDATORY, null, null
                                ),
                                "name", ParameterTool.stringParam(
                                        "Nome para a agenda", ParameterType.OPTIONAL, null, null
                                ),
                                "description", ParameterTool.stringParam(
                                        "Descrição para a agenda", ParameterType.OPTIONAL, null, null
                                ),
                                "studentId", ParameterTool.autoParam(
                                        "Identificador do estudante para criar a agenda, esse parâmetro é automatico e não deve ser enviado", this::getStudentId
                                )
                        )
                ),
                ToolDTO.tool(
                        "deletePlanner",
                        "Ferramenta para excluir a agenda pelo id e suas tarefas",
                        this::delete,
                        Map.of(
                                "plannerId", ParameterTool.numberParam(
                                        "Id da agenda a ser apagada", ParameterType.MANDATORY, null, null
                                )
                        )
                ),
                ToolDTO.tool(
                        "createTask",
                        "Ferramenta para criar tarefas/lembretes em uma agenda",
                        this::createTask,
                        Map.of(
                                "title", ParameterTool.stringParam(
                                        "Título para a tarefa/lembrete", ParameterType.MANDATORY, null, null
                                ),
                                "description", ParameterTool.stringParam(
                                        "Descrição para a tarefa/lembrete", ParameterType.OPTIONAL, null, null
                                ),
                                "date", ParameterTool.stringParam(
                                        "Data e hora para a tarefa/lembrete no formato AAAA-MM-DDTHH:MM", ParameterType.MANDATORY, null, null
                                ),
                                "plannerId", ParameterTool.numberParam(
                                        "Id do planner que será adicionado a tarefa/lembrete, utilize a ferramenta 'getPlanners' para obter o id da agenda que o usuário deseja criar a tarefa/lembrete", ParameterType.MANDATORY, null, null
                                ),
                                "studentId", ParameterTool.autoParam(
                                        "Identificador do estudante para criar a agenda, esse parâmetro é automatico e não deve ser enviado", this::getStudentId
                                )
                        )
                ),
                ToolDTO.tool(
                        "getTasks",
                        "Ferramenta para buscar todas as tarefas/lembretes criadas",
                        this::getAllTask,
                        Map.of(
                                "studentId", ParameterTool.autoParam(
                                        "Identificador do estudante para criar a agenda, esse parâmetro é automatico e não deve ser enviado", this::getStudentId
                                )
                        )
                ),
                ToolDTO.tool(
                        "updateTask",
                        "Ferramenta para editar uma tarefa/lembrete",
                        this::updateTask,
                        Map.of(
                                "id", ParameterTool.stringParam(
                                        "Id da tarefa/lembrete que será editada", ParameterType.MANDATORY, null, null
                                ),
                                "title", ParameterTool.stringParam(
                                        "Nome para a agenda", ParameterType.OPTIONAL, null, null
                                ),
                                "description", ParameterTool.stringParam(
                                        "Descrição para a agenda", ParameterType.OPTIONAL, null, null
                                ),
                                "plannerId", ParameterTool.numberParam(
                                        "Caso informado para mudar a tarefa de agenda, esse parâmetro deve ser preenchido com o identificador da agenda a qual a tarefa será movida", ParameterType.OPTIONAL, null, null
                                ),
                                "studentId", ParameterTool.autoParam(
                                        "Identificador do estudante para criar a agenda, esse parâmetro é automatico e não deve ser enviado", this::getStudentId
                                )
                        )
                ),
                ToolDTO.tool(
                        "deleteTask",
                        "Ferramenta para excluir uma tarefa/lembrete pelo id",
                        this::deleteTask,
                        Map.of(
                                "plannerId", ParameterTool.numberParam(
                                        "Id da agenda a ser apagada", ParameterType.MANDATORY, null, null
                                )
                        )
                ),
                ToolDTO.tool(
                        "getTasksByDate",
                        "Ferramenta para buscar todas as tarefas/lembretes de uma data específica",
                        this::getTasksByDate,
                        Map.of(
                                "date", ParameterTool.stringParam(
                                        "Data e hora para buscar sa tarefa/lembrete no formato AAAA-MM-DD", ParameterType.MANDATORY, null, null
                                )
                        )
                ),
                ToolDTO.tool(
                        "markAsCompleteTask",
                        "Ferramenta para marcar uma tarefa como concluida",
                        this::markAsCompleted,
                        Map.of(
                                "taskId", ParameterTool.numberParam(
                                        "Identificador da tarefa a ser concluída", ParameterType.MANDATORY, null, null
                                ),
                                "studentId", ParameterTool.autoParam(
                                        "Identificador do estudante, esse parâmetro é automatico e não deve ser enviado", this::getStudentId
                                )
                        )
                ),
                ToolDTO.tool(
                        "markAsNotCompleteTask",
                        "Ferramenta para marcar uma tarefa como não concluida",
                        this::markAsNotCompleted,
                        Map.of(
                                "taskId", ParameterTool.numberParam(
                                        "Identificador da tarefa a ser marcada como não concluída", ParameterType.MANDATORY, null, null
                                ),
                                "studentId", ParameterTool.autoParam(
                                        "Identificador do estudante, esse parâmetro é automatico e não deve ser enviado", this::getStudentId
                                )
                        )
                )
        ));
    }

    private String getStudentId() {
        return studentService.findByExternalKey(UUID.fromString(((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getSubject())).getId().toString();
    }
}
