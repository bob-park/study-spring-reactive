package com.hwpark.ch02dataaccess.domain;

import lombok.Getter;
import lombok.Setter;

import org.springframework.boot.actuate.trace.http.HttpTrace;
import org.springframework.data.annotation.Id;

@Getter
@Setter
public class HttpTraceWrapper {

    @Id
    private String id;

    private HttpTrace httpTrace;

    public HttpTraceWrapper(HttpTrace httpTrace) {
        this.httpTrace = httpTrace;
    }
}
