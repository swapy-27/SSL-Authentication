package com.mTLS.example.clientside.Controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/")
public class ClientController {

    @Autowired
    WebClient webClient;
    @GetMapping("/hello")
    public ResponseEntity <String>  getHello(){

        return new ResponseEntity<>("Hello Buddy!",HttpStatus.OK);
    }

    @GetMapping("/server-call")
    public String getDataFromServer(){
        Mono<String> dataFromServer = webClient.get()
                .uri("https://localhost:8082/server")
                .retrieve().bodyToMono(String.class);

        return dataFromServer.block();
    }


}
