package com.sarkar.kafka.stream.repository;


import com.sarkar.kafka.stream.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.time.LocalDateTime;

@Repository
public interface StoreRepo extends JpaRepository<Store,Long> {
    Store findByEventId(String eventId);
    Store findByEventIdAndUpdateTimeLessThan(String eventId, LocalDateTime updateTime);
}
