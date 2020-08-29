package com.projects.ART_Web.interfaces;

import com.projects.ART_Web.entities.Notification;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends CrudRepository<Notification, Long> {
    List<Notification> getAllByOwnerId(Long ownerId);
}
