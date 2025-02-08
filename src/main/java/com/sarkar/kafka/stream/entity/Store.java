package com.sarkar.kafka.stream.entity;

import lombok.*;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@ToString
@Getter
@Setter
public class Store {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    private String eventId;
    private LocalDateTime updateTime;
}
