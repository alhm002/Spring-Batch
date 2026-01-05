package org.match.batch;

import lombok.extern.slf4j.Slf4j;
import org.match.models.EntrySpectateurDto;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.infrastructure.item.xml.StaxEventItemReader;
import org.springframework.batch.infrastructure.item.xml.builder.StaxEventItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

@Configuration
public class SpectateurXmlReader {

    @Bean
    @StepScope
    public StaxEventItemReader<EntrySpectateurDto> reader() {
         Jaxb2Marshaller unmarshaller = new Jaxb2Marshaller();
        unmarshaller.setClassesToBeBound(EntrySpectateurDto.class);

        return new StaxEventItemReaderBuilder<EntrySpectateurDto>()
                .name("SpectateurXmlReader")
                .resource(new ClassPathResource("input/spectateurs.xml")) // Attention au chemin
                .addFragmentRootElements("spectatorEntry")
                .unmarshaller(unmarshaller)
                .build();
    }
}
