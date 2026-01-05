package org.match.batch;

import lombok.extern.slf4j.Slf4j;
import org.match.factory.EntrySpectateurFactory;
import org.match.models.*;
import org.match.repository.EntrySpectateurRepository;
import org.match.repository.SpectateurRepository;
import org.match.validation.SpectateurValidator;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpectateurProcessor implements ItemProcessor<EntrySpectateurDto, EntrySpectateur> {

    private final SpectateurValidator validator;
    private final EntrySpectateurRepository entrySpectateurRepository;

    public SpectateurProcessor(SpectateurValidator compositeSpectateurValidator, EntrySpectateurRepository entrySpectateurRepository) {
        this.validator = compositeSpectateurValidator;
        this.entrySpectateurRepository = entrySpectateurRepository;
    }

    @Override
    public EntrySpectateur process(EntrySpectateurDto item) throws Exception {

        validator.validate(item);

        EntrySpectateur entrySpectateur = EntrySpectateurFactory.createEntrySpectateur(item);

        long historique = entrySpectateurRepository.countEntriesBySpectatorId(item.getSpectatorId());
        long totalMatchs = historique + 1;

        SpectatorCategory category = SpectatorCategory.fromMatchCount(totalMatchs);
        entrySpectateur.getSpectateur().setSpectatorCategory(category);

        return entrySpectateur;
    }
}

