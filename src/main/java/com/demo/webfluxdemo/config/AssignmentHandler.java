package com.demo.webfluxdemo.config;

import com.demo.webfluxdemo.service.ReactiveMathService;
import java.util.function.BiFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

@Service
public class AssignmentHandler {

  @Autowired
  private ReactiveMathService mathService;



  public Mono<ServerResponse> add(ServerRequest req) {
    return getResult(req,(a,b)->ServerResponse.ok().bodyValue(a+b));
  }

  public Mono<ServerResponse> subtract(ServerRequest req) {
    return getResult(req,(a,b)->ServerResponse.ok().bodyValue(a-b));
  }
  public Mono<ServerResponse> divide(ServerRequest req) {
    return getResult(req,(a,b)->b!=0?ServerResponse.ok().bodyValue(a/b):ServerResponse.ok().bodyValue("Denominator can't be Zero"));
  }

  public Mono<ServerResponse> multiply(ServerRequest req) {
    return getResult(req,(a,b)->ServerResponse.ok().bodyValue(a*b));
  }

  private int getIntegerFromStringValue(String input){
   return  Integer.parseInt(input);
  }

  private Tuple2<Integer, Integer> getInputTuple(ServerRequest req) {
    int input1 = getIntegerFromStringValue(req.pathVariable("input1"));
    int input2 = getIntegerFromStringValue(req.pathVariable("input2"));
    return Tuples.of(input1,input2);
  }

  private Mono<ServerResponse> getResult(ServerRequest req,BiFunction<Integer,Integer,Mono<ServerResponse>>  operation){
    Tuple2<Integer, Integer> inputTuple2 = getInputTuple(req);
    return operation.apply(inputTuple2.getT1(),inputTuple2.getT2());
  }

}


//body should be used when u have publisher
//bodyValue should be used when u have the direct data