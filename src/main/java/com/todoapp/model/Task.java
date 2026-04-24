package com.todoapp.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 500)
    private String description;

    private boolean completed;

    @Column(name = "position_order")
    private Integer position = 0;

    @Column(length = 50)
    private String category;

    @Column(name = "due_date")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dueDate;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public boolean isOverdue() {
        if (completed || dueDate == null) return false;
        return dueDate.isBefore(LocalDate.now());
    }

    public boolean isDueSoon() {
        if (completed || dueDate == null) return false;
        LocalDate now = LocalDate.now();
        return dueDate.isEqual(now) || dueDate.isEqual(now.plusDays(1));
    }
}