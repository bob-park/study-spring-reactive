package com.hwpark.ch02dataaccess;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.thymeleaf.TemplateEngine;

import reactor.blockhound.BlockHound;

@SpringBootApplication
public class Ch02DataAccessApplication {

    public static void main(String[] args) {

        /*
         * Blocking Method 호출을 확인하고 exception 을 던진다.
         ! 하지만, Spring thymeleaf 사용 시 Templates 를 읽는 과정에 FileInputStream.readBytes() 를 실행하여 정상적으로 사용할 수 없다.
        */
//        BlockHound.install();

        /*
         * 따라서, thymeleaf 에서 Template 을 읽은 부분에 사용된 Blocking 호출 부분인 TemplateEngine.process() 만 콕 집어서 허용하면 템플릿을 제외한 다른 곳에서 Blocking Method 호출을 막을 수 있다.
         */
        BlockHound.builder()
            .allowBlockingCallsInside(TemplateEngine.class.getCanonicalName(), "process")
            .install();

        SpringApplication.run(Ch02DataAccessApplication.class, args);
    }

}
