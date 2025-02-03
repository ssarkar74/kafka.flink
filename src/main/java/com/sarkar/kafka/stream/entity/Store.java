package com.sarkar.kafka.stream.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Builder
@Entity
public class Store {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    private String eventId;
    private LocalDateTime updateTime;
}
