package fr.petitsplats.exception;

import java.util.HashSet;
import java.util.Set;

import javax.validation.ConstraintViolation;

import lombok.Getter;

import org.springframework.util.CollectionUtils;

public class ViolationException extends FunctionnalException {

    @Getter
    private Set<String[]> violations;

    public <T> ViolationException(Set<ConstraintViolation<T>> violations) {
        if (!CollectionUtils.isEmpty(violations)) {
            this.violations = new HashSet<String[]>();
            for (ConstraintViolation<T> constraintViolation : violations) {
                String[] violation = {
                        constraintViolation.getPropertyPath().toString(),
                        constraintViolation.getMessage() };
                this.violations.add(violation);
            }
        }
    }
}
