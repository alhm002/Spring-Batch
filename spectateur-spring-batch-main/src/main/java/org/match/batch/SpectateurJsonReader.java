package org.match.batch;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.match.models.EntrySpectateurDto;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.infrastructure.item.ExecutionContext;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.batch.infrastructure.item.ItemStreamException;
import org.springframework.batch.infrastructure.item.ItemStreamReader;
import org.springframework.batch.infrastructure.item.json.JacksonJsonObjectReader;
import org.springframework.batch.infrastructure.item.json.JsonItemReader;
import org.springframework.batch.infrastructure.item.json.builder.JsonItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
@StepScope
public class SpectateurJsonReader implements ItemStreamReader<EntrySpectateurDto> {

    private JsonParser parser;
    private ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private boolean initialized = false;

    @Override
    public EntrySpectateurDto read() throws Exception {
        if (!initialized) {
            initialized = true;
            InputStream is = new ClassPathResource("input/spectateurs.json").getInputStream();
            parser = mapper.getFactory().createParser(is);
            parser.nextToken(); // Start array
        }

        if (parser.nextToken() == JsonToken.END_ARRAY) return null;

        return mapper.readValue(parser, EntrySpectateurDto.class);
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException { }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException { }

    @Override
    public void close() throws ItemStreamException { }

    /*
    StepScope → @StepScope + @Component

    Restartable → via ItemStreamReader (open/update/close)
    (ici simple stub, peut être amélioré pour vraie reprise)

    Streaming JSON → Jackson JsonParser,
    pas de chargement complet en mémoire
    * */
}

