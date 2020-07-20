package com.projects.ART_Web.interfaces;

import com.projects.ART_Web.entities.Comment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface CommentRepository extends CrudRepository<Comment, Long> {
}
