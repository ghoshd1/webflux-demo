package com.demo.webfluxdemo.config;

import java.util.function.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicate;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest.Headers;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class AssignmentConfig {

@Autowired
AssignmentHandler assignmentHandler;


  Predicate<Headers> requiredHeaderAvailable=headers -> {
    System.out.println(headers.header("OP").size());
    return headers.header("OP").size()>0;
  };


  @Bean
  public RouterFunction<ServerResponse> responseEntityRouterAssignmentFunction(){

    return RouterFunctions.route()
        .GET("arithmatic/{input1}/{input2}", getRequestPredicates("+"), assignmentHandler::add)
        .GET("arithmatic/{input1}/{input2}", getRequestPredicates("-"), assignmentHandler::subtract)
        .GET("arithmatic/{input1}/{input2}", getRequestPredicates("/"), assignmentHandler::divide)
        .GET("arithmatic/{input1}/{input2}", getRequestPredicates("*"), assignmentHandler::multiply)
        .GET("arithmatic/{input1}/{input2}", response -> ServerResponse.badRequest().bodyValue("OP should be +,-,/,* "))
        .build();
  }

private RequestPredicate getRequestPredicates(String operation){
  return RequestPredicates.headers(requiredHeaderAvailable).and(RequestPredicates.headers(getOperatorMatchPredicate(operation)));
}
  private Predicate<Headers> getOperatorMatchPredicate(String operator)
  {
  // return header -> operator.equals(header.asHttpHeaders().toSingleValueMap().get("OP"));

       return headers -> {
         System.out.println(headers.header("OP").get(0).equals(operator.trim()));
         System.out.println(headers.header("OP").get(0)+"\t"+operator.trim());
         return headers.header("OP").get(0).equals(operator.trim());
       };
  }

}
