package com.projects.ART_Web.interfaces;

import com.projects.ART_Web.entities.Article;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ArticleRepository extends CrudRepository<Article, Long> {
}
