package org.match.validation;

import org.match.models.EntrySpectateurDto;
import org.springframework.batch.infrastructure.item.validator.ValidationException;

@FunctionalInterface
public interface SpectateurValidator {

    void validate(EntrySpectateurDto dto) throws ValidationException;
}