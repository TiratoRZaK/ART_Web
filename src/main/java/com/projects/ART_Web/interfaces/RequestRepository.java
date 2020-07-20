package com.projects.ART_Web.interfaces;

import com.projects.ART_Web.entities.Request;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface RequestRepository extends CrudRepository<Request, Long> {
}
