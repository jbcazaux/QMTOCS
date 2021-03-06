package fr.petitsplats.web.controllers;

import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import fr.petitsplats.MethodNotAllowedException;
import fr.petitsplats.exception.ViolationException;

public abstract class AbstractController {

    @ExceptionHandler(ViolationException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Set<String[]> notFound(ViolationException ex) {
        return ex.getViolations();
    }

    @ExceptionHandler(MethodNotAllowedException.class)
    @ResponseStatus(value = HttpStatus.METHOD_NOT_ALLOWED)
    public void methodNotAllowed() {

    }

}
