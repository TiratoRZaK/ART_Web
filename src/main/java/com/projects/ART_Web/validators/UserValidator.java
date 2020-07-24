package com.projects.ART_Web.validators;

import com.projects.ART_Web.entities.User;
import com.projects.ART_Web.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class UserValidator implements Validator {
    @Autowired
    private UserService userService;

    @Override
    public boolean supports(Class<?> aClass) {
        return User.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        User user = (User) o;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "Required.email","Это поле обязательно для заполнения.");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "Required.name","Это поле обязательно для заполнения.");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "Required.password","Это поле обязательно для заполнения.");
        if (userService.loadUserByUsername(user.getEmail()) != null) {
            errors.rejectValue("email", "Duplicate.email","Эта почта уже зарегистрирована.");
        }
        if (user.getPassword().length() < 8) {
            errors.rejectValue("password", "Size.password","Пароль должен содержать минимум 8 символов.");
        }
        if (user.getName().length() < 2) {
            errors.rejectValue("name", "Size.name","Имя не может быть короче 2 символов.");
        }
        if (!user.getPassword().equals(user.getConfirmPassword())) {
            errors.rejectValue("confirmPassword", "Different.password","Пароли не совпадают.");
        }
    }
}
