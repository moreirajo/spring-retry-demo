package com.example.retry.demo;

import java.util.function.Function;
import java.util.function.Supplier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
@SpringBootApplication
public class RetryDemoApplication {

  public static void main(String[] args) {
    SpringApplication.run(RetryDemoApplication.class, args);
  }

  private final EmitterProcessor<String> processor = EmitterProcessor.create();

  @GetMapping("/request")
  public ResponseEntity<Mono<String>> requestSomething() {
    return ResponseEntity.status(HttpStatus.ACCEPTED)
        .body(
            Mono.just("Your request was accepted").doOnNext(str -> processor.onNext("Message A")));
  }

  @Bean
  public Supplier<Flux<String>> sender() {
    return () -> processor;
  }

  @Bean
  public Function<Flux<String>, Mono<Void>> handleMessageA() {

    return inputFlux ->
        inputFlux
            .doOnNext(input -> log.info("Message Received: {}", input))
            .map(input -> input.substring(100))
            .doOnError(error -> log.error("Some error happen", error))
            .then();
  }
}
