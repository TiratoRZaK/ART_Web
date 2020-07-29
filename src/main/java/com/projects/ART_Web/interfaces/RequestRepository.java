package com.projects.ART_Web.interfaces;

import com.projects.ART_Web.entities.Request;
import com.projects.ART_Web.entities.Status;
import com.projects.ART_Web.entities.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestRepository extends CrudRepository<Request, Long> {
    boolean existsByAuthor(User user);

    List<Request> findAllByStatus(Status status);

    List<Request> findAllByOrderByStatusDesc();

    Request findRequestByAuthor(User author);

    Integer countRequestByStatus(Status status);

    Request getById(Long id);
}
