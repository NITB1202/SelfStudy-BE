package com.example.selfstudybe.services;

import com.example.selfstudybe.dtos.Task.CreateTaskDto;
import com.example.selfstudybe.dtos.Task.TaskDto;
import com.example.selfstudybe.dtos.Task.UpdateTaskDto;
import com.example.selfstudybe.enums.PlanStatus;
import com.example.selfstudybe.enums.TaskStatus;
import com.example.selfstudybe.exception.CustomBadRequestException;
import com.example.selfstudybe.exception.CustomNotFoundException;
import com.example.selfstudybe.models.Plan;
import com.example.selfstudybe.models.Task;
import com.example.selfstudybe.repositories.PlanRepository;
import com.example.selfstudybe.repositories.TaskRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final PlanRepository planRepository;

    public TaskDto createTask(CreateTaskDto createTaskDto) {
        // Save new task
        Plan plan  = planRepository.findById(createTaskDto.getPlanId()).orElseThrow(
                ()-> new CustomNotFoundException("Can't find plan with id "+createTaskDto.getPlanId()));

        // Check duplicated
        if(taskRepository.existsByNameAndPlan(createTaskDto.getName(),plan))
            throw new CustomBadRequestException("Task with name "+createTaskDto.getName()+" already exists");

        // Check valid plan
        if(plan.getEndDate().isBefore(LocalDateTime.now()))
            throw new CustomBadRequestException("Can't add a task to an expired plan");

        Task task = new ModelMapper().map(createTaskDto, Task.class);
        task.setPlan(plan);
        Task savedTask = taskRepository.save(task);

        // Update plan process
        plan.setProcess(calculatePlanProcess(plan));
        if(plan.getStatus().equals(PlanStatus.COMPLETE))
            plan.setStatus(PlanStatus.INCOMPLETE);

        planRepository.save(plan);

        return new ModelMapper().map(savedTask, TaskDto.class);
    }

    public List<TaskDto> getAllTasksForPlan(UUID planId) {
        Plan plan = planRepository.findById(planId).orElseThrow(
                ()-> new CustomNotFoundException("Can't find plan with id "+planId));

        List<Task> tasks = taskRepository.findByPlan(plan);
        return new ModelMapper().map(tasks,new TypeToken<List<TaskDto>>() {}.getType());
    }

    public TaskDto updateTask(UpdateTaskDto updateTask) {
        Task task = taskRepository.findById(updateTask.getTaskId()).orElseThrow(
                ()-> new CustomNotFoundException("Can't find task with id " + updateTask.getTaskId()));
        Plan plan = task.getPlan();

        // Check duplicated
        if(updateTask.getName() != null && taskRepository.existsByNameAndPlan(updateTask.getName(),plan))
            throw new CustomBadRequestException("Task with name " + updateTask.getName()+" already exists");

        // Check valid plan
        if(plan.getEndDate().isBefore(LocalDateTime.now()))
            throw new CustomBadRequestException("Can't update a task in an expired plan");

        // Save task
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setSkipNullEnabled(true);

        modelMapper.map(updateTask, task);
        taskRepository.save(task);

        // Update plan process
        float process = calculatePlanProcess(plan);
        plan.setProcess(process);
        if(process == 1) plan.setStatus(PlanStatus.COMPLETE);
        else plan.setStatus(PlanStatus.INCOMPLETE);

        planRepository.save(plan);

        return new ModelMapper().map(task, TaskDto.class);
    }

    public void deleteTask(UUID taskId) {
        Task task = taskRepository.findById(taskId).orElseThrow(
                ()-> new CustomNotFoundException("Can't find task with id "+taskId));
        Plan plan = task.getPlan();

        // Check valid plan
        if(plan.getEndDate().isBefore(LocalDateTime.now()))
            throw new CustomBadRequestException("Can't delete a task in an expired plan");

        taskRepository.delete(task);

        float process = calculatePlanProcess(plan);
        plan.setProcess(process);
        if(process == 1) plan.setStatus(PlanStatus.COMPLETE);
        else plan.setStatus(PlanStatus.INCOMPLETE);

        planRepository.save(plan);
    }

    private float calculatePlanProcess(Plan plan) {
        List<Task> tasks = taskRepository.findByPlan(plan);

        int finishedTasks = 0;
        int totalTasks = tasks.size();

        for (Task task : tasks)
            if (task.getStatus().equals(TaskStatus.COMPLETED))
                finishedTasks++;

        return totalTasks != 0 ? (float) finishedTasks / totalTasks : 0;
    }
}
