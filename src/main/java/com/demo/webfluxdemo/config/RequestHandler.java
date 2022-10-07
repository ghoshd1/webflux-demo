package com.demo.webfluxdemo.config;

import com.demo.webfluxdemo.dto.MultiplyRequestDto;
import com.demo.webfluxdemo.dto.Response;
import com.demo.webfluxdemo.exception.InputValidationException;
import com.demo.webfluxdemo.service.MathService;
import com.demo.webfluxdemo.service.ReactiveMathService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class RequestHandler {

  @Autowired
  private ReactiveMathService mathService;


  public Mono<ServerResponse> squareHandler(ServerRequest req) {
    Mono<Response> response = mathService.findSquare(Integer.parseInt(req.pathVariable("input")));
    return ServerResponse.ok().body(response, Response.class);
  }

  public Mono<ServerResponse> tableHandler(ServerRequest serverRequest) {
    Flux<Response> input = mathService.multiplicationTable(
        Integer.parseInt(serverRequest.pathVariable("input")));

    return ServerResponse.ok().body(input, Response.class);
  }

  public Mono<ServerResponse> streamTableHandler(ServerRequest serverRequest) {
    Flux<Response> input = mathService.multiplicationTable(
        Integer.parseInt(serverRequest.pathVariable("input")));

    return ServerResponse.ok()
        .contentType(MediaType.TEXT_EVENT_STREAM)
        .body(input, Response.class);
  }

  public Mono<ServerResponse> multiplyHandler(ServerRequest serverRequest) {
    Mono<MultiplyRequestDto> multiplyRequestDtoMono = serverRequest.bodyToMono(
        MultiplyRequestDto.class);
    Mono<Response> multiply = mathService.multiply(multiplyRequestDtoMono);
    return ServerResponse.ok().body(multiply, Response.class);
  }


  public Mono<ServerResponse> squareValidationHandler(ServerRequest serverRequest) {

    int input = Integer.parseInt(serverRequest.pathVariable("input"));
    Mono<Response> responseMono = Mono.just(input)
        .filter(integer -> integer >= 10 && integer <= 20)
        .switchIfEmpty(Mono.error(new InputValidationException(input)))
        .flatMap(integer -> mathService.findSquare(input));

    return ServerResponse.ok().body(responseMono, Response.class);

  }
}


//body should be used when u have publisher
//bodyValue should be used when u have the direct data