package com.sarkar.kafka.stream.entity;

import lombok.*;

import jakarta.persistence.*;
import java.time.LocalDateTime;


@AllArgsConstructor
@NoArgsConstructor
@Entity
@ToString
@Getter
@Setter
@Builder
public class Store {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    @Column(unique=true)
    private String eventId;
    private LocalDateTime updateTime;
}
