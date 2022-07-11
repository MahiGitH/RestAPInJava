package org.example;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class RestAPIEx2 {
    public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException {

        Transcript transcript = new Transcript();
        transcript.setAudio_url("https://bit.ly/3qDXLG8");

        Gson gson = new Gson();
        String jsonRequest = gson.toJson(transcript);

        System.out.println(jsonRequest);

        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(new URI("https://api.assemblyai.com/v2/transcript"))
                .header("Authorization", APIValue.API_KEY)
                .POST(HttpRequest.BodyPublishers.ofString(jsonRequest))
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse<String> postResponse = httpClient.send(postRequest, HttpResponse.BodyHandlers.ofString());
        postResponse.body();

        System.out.println(postResponse.body());

        transcript = gson.fromJson(postResponse.body(), Transcript.class);

        System.out.println(transcript.getId());

        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(new URI("https://api.assemblyai.com/v2/transcript/" + transcript.getId()))
                .header("Authorization", APIValue.API_KEY)
               // .GET()
                .build();

        while (true) {
            HttpResponse<String> getResponse = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());

            transcript = gson.fromJson(getResponse.body(), Transcript.class);

            System.out.println(transcript.getStatus());


            if ("completed".equalsIgnoreCase(transcript.getStatus()) || "error".equalsIgnoreCase(transcript.getStatus())) {
                break;
            }

            Thread.sleep(1000);
        }

        System.out.println("Transcription completed");

        System.out.println(transcript.getText());
    }
}