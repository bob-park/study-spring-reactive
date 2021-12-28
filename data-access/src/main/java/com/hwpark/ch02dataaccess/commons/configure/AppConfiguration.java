package com.hwpark.ch02dataaccess.commons.configure;

import java.net.URI;
import java.util.Collections;
import java.util.Map;

import org.bson.Document;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.actuate.trace.http.HttpTrace;
import org.springframework.boot.actuate.trace.http.HttpTraceRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.convert.NoOpDbRefResolver;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

import com.hwpark.ch02dataaccess.commons.httptrace.SpringDataHttpTraceRepository;
import com.hwpark.ch02dataaccess.domain.HttpTraceWrapper;
import com.hwpark.ch02dataaccess.repository.HttpTraceWrapperRepository;

@Configuration
public class AppConfiguration {

    @Bean
    public HttpTraceRepository httpTraceRepository(HttpTraceWrapperRepository repository) {
//        return new InMemoryHttpTraceRepository();// in memory 기반

        return new SpringDataHttpTraceRepository(repository); // Custom
    }

    @Bean
    public MappingMongoConverter mappingMongoConverter(MongoMappingContext context) {
        MappingMongoConverter mappingConverter = new MappingMongoConverter(
            NoOpDbRefResolver.INSTANCE, context);

        mappingConverter.setCustomConversions(
            new MongoCustomConversions(Collections.singletonList(CONVERTER)));

        return mappingConverter;
    }

//    static Converter<Document, HttpTraceWrapper> CONVERTER =
//        source -> {
//            Document httpTrace = source.get("httpTrace", Document.class);
//            Document request = httpTrace.get("request", Document.class);
//            Document response = httpTrace.get("response", Document.class);
//
//            return new HttpTraceWrapper(new HttpTrace( //
//                new HttpTrace.Request( //
//                    request.getString("method"), //
//                    URI.create(request.getString("uri")), //
//                    request.get("headers", Map.class), //
//                    null),
//                new HttpTrace.Response( //
//                    response.getInteger("status"), //
//                    response.get("headers", Map.class)),
//                httpTrace.getDate("timestamp").toInstant(), //
//                null, //
//                null, //
//                httpTrace.getLong("timeTaken")));
//        };

    // lambda 안되네 - 왜 안될까?
    static Converter<Document, HttpTraceWrapper> CONVERTER = //
        new Converter<>() { //
            @Override
            public HttpTraceWrapper convert(Document document) {
                Document httpTrace = document.get("httpTrace", Document.class);
                Document request = httpTrace.get("request", Document.class);
                Document response = httpTrace.get("response", Document.class);

                return new HttpTraceWrapper(new HttpTrace( //
                    new HttpTrace.Request( //
                        request.getString("method"), //
                        URI.create(request.getString("uri")), //
                        request.get("headers", Map.class), //
                        null),
                    new HttpTrace.Response( //
                        response.getInteger("status"), //
                        response.get("headers", Map.class)),
                    httpTrace.getDate("timestamp").toInstant(), //
                    null, //
                    null, //
                    httpTrace.getLong("timeTaken")));
            }
        };

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

}
