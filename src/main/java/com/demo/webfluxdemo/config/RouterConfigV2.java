package com.demo.webfluxdemo.config;

import com.demo.webfluxdemo.dto.InputFailedValidationResponse;
import com.demo.webfluxdemo.exception.InputValidationException;
import java.util.function.BiFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicate;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Configuration
public class RouterConfigV2 {

@Autowired
RequestHandler requestHandler;


  @Bean
  public RouterFunction<ServerResponse> highLevelRouter(){

    return RouterFunctions.route()
        .path("router2",this::responseEntityRouterFunction)
        .build();
  }

//"router1/square/{input}"
  private RouterFunction<ServerResponse> responseEntityRouterFunction(){

    return RouterFunctions.route()
        .GET("square/{input}", RequestPredicates.path("*/1?").or(RequestPredicates.path("*/20")),requestHandler::squareHandler)
        .GET("square/{input}", res-> ServerResponse.badRequest().bodyValue("only 10-19 allowed"))
        .GET("table/{input}",requestHandler::tableHandler)
        .GET("table/{input}/stream",requestHandler::streamTableHandler)
        .POST("multiply",requestHandler::multiplyHandler)
        .GET("square/{input}/throw",requestHandler::squareValidationHandler)
        .onError(InputValidationException.class,exceptionHandler())
        .build();
  }

  private BiFunction<Throwable, ServerRequest, Mono<ServerResponse>> exceptionHandler(){
    return ((throwable, serverRequest) -> {
      InputValidationException e = (InputValidationException) throwable;
      InputFailedValidationResponse response = new InputFailedValidationResponse();
      response.setMessage(e.getMessage());
      response.setInput(e.getInput());
      response.setErrorCode(400);

      return ServerResponse.ok().bodyValue(response);
    });
  }

}
