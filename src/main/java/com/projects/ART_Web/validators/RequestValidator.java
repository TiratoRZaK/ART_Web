package com.projects.ART_Web.validators;

import com.projects.ART_Web.entities.Request;
import com.projects.ART_Web.interfaces.RequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class RequestValidator implements Validator {
    @Autowired
    public RequestRepository requestRepository;

    @Override
    public boolean supports(Class<?> aClass) {
        return Request.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        Request request = (Request) o;

        if (requestRepository.existsByAuthor(request.getAuthor())) {
            errors.rejectValue("author", "Duplicate.author", "У вас уже имеется заявка, дождитесь её обработки или отмените.");
        }
        if (request.getAge() < 18) {
            errors.rejectValue("age", "Min.age", "Консультации доступны только совершеннолетним.");
        }
        if (request.getName().length() < 2) {
            errors.rejectValue("name", "Size.name", "Имя не может быть короче 2 символов.");
        }
        if (request.getDate().before(request.getDate_create())) {
            errors.rejectValue("date", "Incorrect.date", "Простите, но я ещё не изобрела машину времени, чтобы проводить консультации в прошлом.");
        }
        if (request.getProblem().length() < 20) {
            errors.rejectValue("problem", "Size.problem", "Пожалуйста расскажите о своей проблеме поподробнее.");
        }
    }
}
