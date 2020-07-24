package com.projects.ART_Web.interfaces;

import com.projects.ART_Web.entities.Request;
import com.projects.ART_Web.entities.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface RequestRepository extends CrudRepository<Request, Long> {
    boolean existsByAuthor(User user);
}
