package com.hwpark.ch02dataaccess.repository;

import java.util.stream.Stream;

import org.springframework.data.repository.Repository;

import com.hwpark.ch02dataaccess.domain.HttpTraceWrapper;

public interface HttpTraceWrapperRepository extends Repository<HttpTraceWrapper, String> {

    Stream<HttpTraceWrapper> findAll();

    void save(HttpTraceWrapper trace);

}
