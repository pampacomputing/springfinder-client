package com.pampa.springfinderclient.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pampa.springfinderclient.model.ResponseData;
import com.pampa.springfinderclient.model.User;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ClientService {
    private static Logger logger = LoggerFactory.getLogger(ClientService.class);

    private final RestTemplate restTemplate;
    private final AtomicInteger requestCounter = new AtomicInteger(0);

    public ClientService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Async
    public void sendUser(User user) {
        try {
            int currentId = requestCounter.incrementAndGet();

            String apiUrl = "https://26.76.169.248:9000/user-info";

            ResponseEntity<ResponseData> response = restTemplate.postForEntity(apiUrl, user, ResponseData.class);

            if (response.getStatusCode() == HttpStatus.FOUND && response.getBody() != null) {
                ArrayList<User> receivedUsers = response.getBody().getData();
                ResponseData responseData = new ResponseData(currentId, receivedUsers);
                saveResponseAsJson(responseData);
                logger.info("Resposta salva com sucesso para id_request {}", responseData.getId_request());
            } else {
                logger.warn("Resposta inesperada da API: {}", response.getStatusCode());
            }
        } catch (Exception e) {
            logger.error("Erro ao consultar API", e);
        }
    }

    private void saveResponseAsJson(ResponseData responseData) {
        File dir = new File("data");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        ObjectMapper mapper = new ObjectMapper();
        File file = new File("data/responses.json");

        List<ResponseData> list = new ArrayList<>();

        try {
            if (file.exists()) {
                list = Arrays.asList(mapper.readValue(file, ResponseData[].class));
                list = new ArrayList<>(list);
            }
            list.add(responseData);
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, list);
        } catch (IOException e) {
            System.err.println("Erro ao salvar JSON: " + e.getMessage());
        }
    }

    public List<ResponseData> getAllResponses() {
        ObjectMapper mapper = new ObjectMapper();
        File file = new File("data/responses.json");
        try {
            if (file.exists()) {
                return Arrays.asList(mapper.readValue(file, ResponseData[].class));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return List.of();
    }

    @PreDestroy
    public void cleanUpOnShutdown() {
        File file = new File("data/responses.json");
        if (file.exists()) {
            boolean deleted = file.delete();
            if (deleted) {
                logger.info("responses.json apagado com sucesso ao encerrar a aplicação.");
            } else {
                logger.warn("Não foi possível apagar responses.json.");
            }
        }
    }

}


