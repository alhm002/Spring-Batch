package org.match.validation;


import org.match.models.EntrySpectateurDto;
import org.springframework.batch.infrastructure.item.validator.ValidationException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CompositeSpectateurValidator implements SpectateurValidator {

    private final List<SpectateurValidator> validators;

    public CompositeSpectateurValidator(List<SpectateurValidator> validators) {
        this.validators = validators.stream()
                .filter(v -> !(v instanceof CompositeSpectateurValidator))
                .toList();
    }

    @Override
    public void validate(EntrySpectateurDto dto) throws ValidationException {
        for (SpectateurValidator validator : validators) {
            validator.validate(dto);
        }
    }
}
