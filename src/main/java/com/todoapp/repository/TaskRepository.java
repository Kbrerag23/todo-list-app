package com.todoapp.repository;

import com.todoapp.model.Task;
import com.todoapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUserOrderByPositionAscCreatedAtDesc(User user);
    List<Task> findByUserId(Long userId);

    @Query("SELECT COUNT(t) FROM Task t WHERE DATE(t.createdAt) = CURRENT_DATE")
    long countTasksCreatedToday();
}