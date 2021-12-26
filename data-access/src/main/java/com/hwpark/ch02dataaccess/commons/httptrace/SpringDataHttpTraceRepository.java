package com.hwpark.ch02dataaccess.commons.httptrace;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.boot.actuate.trace.http.HttpTrace;
import org.springframework.boot.actuate.trace.http.HttpTraceRepository;

import com.hwpark.ch02dataaccess.domain.HttpTraceWrapper;
import com.hwpark.ch02dataaccess.repository.HttpTraceWrapperRepository;

@RequiredArgsConstructor
public class SpringDataHttpTraceRepository implements HttpTraceRepository {

    private final HttpTraceWrapperRepository repository;

    @Override
    public List<HttpTrace> findAll() {
        return repository.findAll()
            .map(HttpTraceWrapper::getHttpTrace)
            .collect(Collectors.toList());
    }

    @Override
    public void add(HttpTrace trace) {
        repository.save(new HttpTraceWrapper(trace));
    }
}
