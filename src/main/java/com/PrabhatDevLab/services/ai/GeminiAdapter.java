//package com.PrabhatDevLab.services.ai;
//
//import com.PrabhatDevLab.services.models.AiResponse;
//import com.PrabhatDevLab.services.models.PromptRequest;
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import okhttp3.*;
//
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.*;
//
//public class GeminiAdapter implements AIProvider {
//
//    private String apiKey;
//
//    // Dedicated executor for AI tasks
//    private static final ExecutorService AI_EXECUTOR =
//            Executors.newFixedThreadPool(4);
//
//    // Stable & safe HTTP client
//    private final OkHttpClient client = new OkHttpClient.Builder()
//            .connectTimeout(10, TimeUnit.SECONDS)
//            .writeTimeout(20, TimeUnit.SECONDS)
//            .readTimeout(40, TimeUnit.SECONDS)
//            .retryOnConnectionFailure(true)
//            .build();
//
//    private final ObjectMapper mapper = new ObjectMapper();
//
//    private static final String URL =
//            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent";
//
//    @Override
//    public void setApiKey(String key) {
//        this.apiKey = key;
//    }
//
//    @Override
//    public CompletableFuture<AiResponse> completeCode(PromptRequest req) {
//
//        return CompletableFuture.supplyAsync(() -> {
//            try {
//                if (apiKey == null || apiKey.isBlank())
//                    throw new RuntimeException("Gemini API key missing");
//
//                // Build request JSON
//                Map<String, Object> bodyJson = Map.of(
//                        "contents", List.of(
//                                Map.of("parts", List.of(
//                                        Map.of("text", req.getPrompt())
//                                ))
//                        )
//                );
//
//                HttpUrl url = HttpUrl.parse(URL).newBuilder()
//                        .addQueryParameter("key", apiKey)
//                        .build();
//
//                Request request = new Request.Builder()
//                        .url(url)
//                        .post(RequestBody.create(
//                                mapper.writeValueAsString(bodyJson),
//                                MediaType.get("application/json")
//                        ))
//                        .build();
//
//                Response response = client.newCall(request).execute();
//
//                if (!response.isSuccessful()) {
//                    String body = response.body() != null ? response.body().string() : "null";
//                    throw new RuntimeException("Gemini HTTP " + response.code() + " — body: " + body);
//                }
//
//                return parse(response.body().string());
//
//            } catch (Exception e) {
//                throw new RuntimeException("Gemini failed: " + e.getMessage(), e);
//            }
//        }, AI_EXECUTOR);
//    }
//
//    private AiResponse parse(String raw) throws Exception {
//
//        JsonNode root = mapper.readTree(raw);
//
//        JsonNode node = root
//                .path("candidates")
//                .path(0)
//                .path("content")
//                .path("parts")
//                .path(0)
//                .path("text");
//
//        if (node.isMissingNode())
//            throw new RuntimeException("Gemini returned no usable text. RAW:\n" + raw);
//
//        String text = node.asText().trim();
//
//        // NORMALIZE & CLEAN OUTPUT
//        text = text
//                .replace("\r", "")
//                .replace("\\n", "\n")
//                .replace("\t", "    ")  // convert tabs → spaces
//                .replace("```", "```"); // ensure valid code blocks
//
//        AiResponse res = new AiResponse();
//        res.setExplanation(text);
//        res.setPatch(text);
//        return res;
//    }
//
//    @Override
//    public String providerId() {
//        return "gemini";
//    }
//}



















//package com.PrabhatDevLab.services.ai;
//
//import com.PrabhatDevLab.services.models.AiResponse;
//import com.PrabhatDevLab.services.models.PromptRequest;
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import okhttp3.*;
//
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.*;
//
//public class GeminiAdapter implements AIProvider {
//
//    private String apiKey;
//
//    // Dedicated executor for AI tasks
//    private static final ExecutorService AI_EXECUTOR =
//            Executors.newFixedThreadPool(4);
//
//    // Stable HTTP client
//    private final OkHttpClient client = new OkHttpClient.Builder()
//            .connectTimeout(10, TimeUnit.SECONDS)
//            .writeTimeout(20, TimeUnit.SECONDS)
//            .readTimeout(40, TimeUnit.SECONDS)
//            .retryOnConnectionFailure(true)
//            .build();
//
//    private final ObjectMapper mapper = new ObjectMapper();
//
//    private static final String URL =
//            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent";
//
//    @Override
//    public void setApiKey(String key) {
//        this.apiKey = key;
//    }
//
//    @Override
//    public CompletableFuture<AiResponse> completeCode(PromptRequest req) {
//
//        return CompletableFuture.supplyAsync(() -> {
//            try {
//                if (apiKey == null || apiKey.isBlank())
//                    throw new RuntimeException("Gemini API key missing");
//
//                // BODY JSON
//                Map<String, Object> bodyJson = Map.of(
//                        "contents", List.of(
//                                Map.of("parts", List.of(
//                                        Map.of("text", req.getPrompt())
//                                ))
//                        )
//                );
//
//                HttpUrl url = HttpUrl.parse(URL).newBuilder()
//                        .addQueryParameter("key", apiKey)
//                        .build();
//
//                Request request = new Request.Builder()
//                        .url(url)
//                        .post(RequestBody.create(
//                                mapper.writeValueAsString(bodyJson),
//                                MediaType.get("application/json")
//                        ))
//                        .build();
//
//                Response response = client.newCall(request).execute();
//
//                if (!response.isSuccessful()) {
//                    String body = response.body() != null ? response.body().string() : "null";
//                    throw new RuntimeException("Gemini HTTP " + response.code() + " — body: " + body);
//                }
//
//                return parse(response.body().string());
//
//            } catch (Exception ex) {
//                handleQuotaIfNeeded(ex);
//                throw new RuntimeException("Gemini failed: " + ex.getMessage(), ex);
//            }
//        }, AI_EXECUTOR);
//    }
//
//    /**
//     * Detect quota exhaustion and apply QuotaGuard
//     */
//    private void handleQuotaIfNeeded(Exception ex) {
//        String msg = ex.getMessage();
//        if (msg == null) return;
//
//        if (!msg.contains("RESOURCE_EXHAUSTED")) return;
//
//        long retrySeconds = 60; // fallback default
//
//        try {
//            // Extract "retryDelay": "45s"
//            String marker = "retryDelay";
//            int idx = msg.indexOf(marker);
//            if (idx != -1) {
//                String sub = msg.substring(idx);
//                String num = sub.replaceAll("[^0-9]", ""); // extract digits only
//                if (!num.isEmpty()) retrySeconds = Long.parseLong(num);
//            }
//        } catch (Exception ignored) { }
//
//        QuotaGuard.blockGeminiFor(retrySeconds);
//
//        System.err.println("🔒 Gemini quota exceeded. Blocked for " + retrySeconds + " seconds.");
//    }
//
//    private AiResponse parse(String raw) throws Exception {
//
//        JsonNode root = mapper.readTree(raw);
//
//        JsonNode node = root
//                .path("candidates")
//                .path(0)
//                .path("content")
//                .path("parts")
//                .path(0)
//                .path("text");
//
//        if (node.isMissingNode())
//            throw new RuntimeException("Gemini returned no usable text. RAW:\n" + raw);
//
//        String text = node.asText().trim();
//
//        // Normalize
//        text = text
//                .replace("\r", "")
//                .replace("\\n", "\n")
//                .replace("\t", "    ")
//                .replace("```", "```");
//
//        AiResponse res = new AiResponse();
//        res.setExplanation(text);
//        res.setPatch(text);
//
//        return res;
//    }
//
//    @Override
//    public String providerId() {
//        return "gemini";
//    }
//}





package com.PrabhatDevLab.services.ai;

import com.PrabhatDevLab.services.models.AiResponse;
import com.PrabhatDevLab.services.models.PromptRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class GeminiAdapter implements AIProvider {

    private String apiKey = "";
    private static final String API_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent";


    // CRITICAL: Longer timeout
    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)  // Increased from default
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();

    private final ObjectMapper mapper = new ObjectMapper();

    public void setApiKey(String key) {
        this.apiKey = key;
    }

    @Override
    public String providerId() {
        return "gemini";
    }

    @Override
    public CompletableFuture<AiResponse> completeCode(PromptRequest req) {
        CompletableFuture<AiResponse> future = new CompletableFuture<>();

        if (apiKey == null || apiKey.trim().isEmpty()) {
            future.completeExceptionally(new RuntimeException("Gemini API key not configured"));
            return future;
        }

        try {
            String requestBody = String.format("""
            {
              "contents": [{
                "parts": [{
                  "text": "%s"
                }]
              }],
              "generationConfig": {
                "temperature": 0.7,
                "maxOutputTokens": 2048
              }
            }
            """, escapeJson(req.getPrompt()));

            Request request = new Request.Builder()
                    .url(API_URL + "?key=" + apiKey)
                    .post(RequestBody.create(requestBody, MediaType.parse("application/json")))
                    .addHeader("Content-Type", "application/json")
                    .build();

            System.out.println("[Gemini] Sending request...");

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    System.err.println("[Gemini] Request failed: " + e.getMessage());
                    future.completeExceptionally(
                            new RuntimeException("Gemini failed: " + e.getMessage())
                    );
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        String body = response.body().string();
                        System.out.println("[Gemini] Response received: " + response.code());

                        if (!response.isSuccessful()) {
                            future.completeExceptionally(
                                    new RuntimeException("Gemini HTTP " + response.code() + ": " + body)
                            );
                            return;
                        }

                        JsonNode root = mapper.readTree(body);
                        JsonNode candidates = root.path("candidates");

                        if (candidates.isMissingNode() || candidates.isEmpty()) {
                            future.completeExceptionally(
                                    new RuntimeException("Gemini returned empty response")
                            );
                            return;
                        }

                        String text = candidates.get(0)
                                .path("content")
                                .path("parts")
                                .get(0)
                                .path("text")
                                .asText();

                        AiResponse aiResponse = new AiResponse();
                        aiResponse.setExplanation(text);

                        System.out.println("[Gemini] Success!");
                        future.complete(aiResponse);

                    } catch (Exception e) {
                        System.err.println("[Gemini] Parse error: " + e.getMessage());
                        future.completeExceptionally(
                                new RuntimeException("Gemini parse error: " + e.getMessage())
                        );
                    }
                }
            });

        } catch (Exception e) {
            System.err.println("[Gemini] Setup error: " + e.getMessage());
            future.completeExceptionally(
                    new RuntimeException("Gemini setup error: " + e.getMessage())
            );
        }

        return future;
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}