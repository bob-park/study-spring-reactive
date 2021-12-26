# builder 로 사용할 container 를 만든다.
FROM adoptopenjdk/openjdk11:latest as builder
WORKDIR application
ARG JAR_FILE=build/libs/data-access.jar
COPY ${JAR_FILE} application.jar
# extract 를 통해 layer 추출
RUN java -Djarmode=layertools -jar application.jar extract

# contanier 생성
FROM adoptopenjdk/openjdk11:latest
WORKDIR application

# Builder Container 에서 추출한 여러 Layer 를 두번째 Container 에 복사 - Docker 의 계층 캐시 알고리즘이 적용됨, 그래서 서드파티 라이브러리는 캐시될 수 있다.
COPY --from=builder application/dependencies/ ./
COPY --from=builder application/spring-boot-loader/ ./
#COPY --from=builder application/snapshot-dependencies/ ./
COPY --from=builder application/application/ ./

# jara -jar 가 아니라 Spring Boot Custom Launcher 로 Applicatino 을 실행한다.
# 이 Launcher 는 Application 시작시 불필요한 Jar 파일 압축 해제를 하지 않으므로 효율적이다.
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]