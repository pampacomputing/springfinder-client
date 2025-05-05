package com.pampa.springfinderclient.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pampa.springfinderclient.model.ResponseData;
import com.pampa.springfinderclient.model.User;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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
    private static final Logger logger = LoggerFactory.getLogger(ClientService.class);

    private final RestTemplate restTemplate;
    private final AtomicInteger requestCounter = new AtomicInteger(0);
    private final String apiUrl;

    public ClientService(RestTemplate restTemplate, @Value("${app.user-info.url}") String apiUrl) {
        this.restTemplate = restTemplate;
        this.apiUrl = apiUrl;
    }

    @Async
    public void sendUser(User user) {
        try {
            int currentId = requestCounter.incrementAndGet();

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
            try{
                boolean result = dir.mkdirs();
                if (result) {
                    logger.info("Diretório data criado com sucesso.");
                } else {
                    logger.warn("Não foi possível criar o diretório data.");
                }
            } catch (SecurityException e) {
                logger.error("Erro ao criar diretório data: " + e.getMessage());
            }

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


