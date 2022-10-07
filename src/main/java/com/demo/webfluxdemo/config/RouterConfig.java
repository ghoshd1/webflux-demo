package com.demo.webfluxdemo.config;

import ch.qos.logback.classic.spi.LoggingEventVO;
import com.demo.webfluxdemo.dto.InputFailedValidationResponse;
import com.demo.webfluxdemo.exception.InputValidationException;
import java.util.function.BiFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Configuration
public class RouterConfig {

@Autowired
RequestHandler requestHandler;



  @Bean
  public RouterFunction<ServerResponse> responseEntityRouterFunction0(){

    return RouterFunctions.route()
        .GET("router/square/{input}",requestHandler::squareHandler)
        .GET("router/table/{input}",requestHandler::tableHandler)
        .GET("router/table/{input}/stream",requestHandler::streamTableHandler)
        .POST("router/multiply",requestHandler::multiplyHandler)
        .GET("router/square/{input}/throw",requestHandler::squareValidationHandler)
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
