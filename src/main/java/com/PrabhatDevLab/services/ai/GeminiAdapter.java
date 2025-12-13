package com.PrabhatDevLab.services.ai;

import com.PrabhatDevLab.services.models.AiResponse;
import com.PrabhatDevLab.services.models.PromptRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class GeminiAdapter implements AIProvider {

    private String apiKey;

    // Dedicated executor for AI tasks
    private static final ExecutorService AI_EXECUTOR =
            Executors.newFixedThreadPool(4);

    // Stable & safe HTTP client
    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .readTimeout(40, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build();

    private final ObjectMapper mapper = new ObjectMapper();

    private static final String URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent";

    @Override
    public void setApiKey(String key) {
        this.apiKey = key;
    }

    @Override
    public CompletableFuture<AiResponse> completeCode(PromptRequest req) {

        return CompletableFuture.supplyAsync(() -> {
            try {
                if (apiKey == null || apiKey.isBlank())
                    throw new RuntimeException("Gemini API key missing");

                // Build request JSON
                Map<String, Object> bodyJson = Map.of(
                        "contents", List.of(
                                Map.of("parts", List.of(
                                        Map.of("text", req.getPrompt())
                                ))
                        )
                );

                HttpUrl url = HttpUrl.parse(URL).newBuilder()
                        .addQueryParameter("key", apiKey)
                        .build();

                Request request = new Request.Builder()
                        .url(url)
                        .post(RequestBody.create(
                                mapper.writeValueAsString(bodyJson),
                                MediaType.get("application/json")
                        ))
                        .build();

                Response response = client.newCall(request).execute();

                if (!response.isSuccessful()) {
                    String body = response.body() != null ? response.body().string() : "null";
                    throw new RuntimeException("Gemini HTTP " + response.code() + " — body: " + body);
                }

                return parse(response.body().string());

            } catch (Exception e) {
                throw new RuntimeException("Gemini failed: " + e.getMessage(), e);
            }
        }, AI_EXECUTOR);
    }

    private AiResponse parse(String raw) throws Exception {

        JsonNode root = mapper.readTree(raw);

        JsonNode node = root
                .path("candidates")
                .path(0)
                .path("content")
                .path("parts")
                .path(0)
                .path("text");

        if (node.isMissingNode())
            throw new RuntimeException("Gemini returned no usable text. RAW:\n" + raw);

        String text = node.asText().trim();

        // NORMALIZE & CLEAN OUTPUT
        text = text
                .replace("\r", "")
                .replace("\\n", "\n")
                .replace("\t", "    ")  // convert tabs → spaces
                .replace("```", "```"); // ensure valid code blocks

        AiResponse res = new AiResponse();
        res.setExplanation(text);
        res.setPatch(text);
        return res;
    }

    @Override
    public String providerId() {
        return "gemini";
    }
}



















//package com.PrabhatDevLab.services.ai;
//
//import com.PrabhatDevLab.services.models.AiResponse;
//import com.PrabhatDevLab.services.models.PromptRequest;
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import okhttp3.*;
//
//        import java.util.List;
//import java.util.Map;
//import java.util.concurrent.*;
//
//public class GeminiAdapter implements AIProvider {
//
//    private String apiKey;
//
//    // ---------- FIX 1: Add timeouts (MANDATORY) ----------
//    private final OkHttpClient client = new OkHttpClient.Builder()
//            .connectTimeout(20, TimeUnit.SECONDS)
//            .readTimeout(50, TimeUnit.SECONDS)
//            .writeTimeout(50, TimeUnit.SECONDS)
//            .callTimeout(60, TimeUnit.SECONDS)
//            .retryOnConnectionFailure(true)
//            .build();
//
//    private final ObjectMapper mapper = new ObjectMapper();
//
//    // ---------- FIX 2: Use a thread pool ----------
//    private static final ExecutorService EXECUTOR =
//            Executors.newFixedThreadPool(4);
//
//    // ---------- Correct endpoint based on your curl success ----------
//    private static final String URL =
//            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent";
//
//
//    @Override
//    public void setApiKey(String key) {
//        this.apiKey = key;
//    }
//
//
//    @Override
//    public CompletableFuture<AiResponse> completeCode(PromptRequest req) {
//
//        return CompletableFuture.supplyAsync(() -> {
//
//            try {
//                if (apiKey == null || apiKey.isBlank()) {
//                    throw new RuntimeException("Gemini API key missing");
//                }
//
//                // Gemini request body
//                Map<String, Object> bodyJson = Map.of(
//                        "contents", List.of(
//                                Map.of("parts", List.of(
//                                        Map.of("text", req.getPrompt())
//                                ))
//                        )
//                );
//
//                HttpUrl url = HttpUrl.parse(URL)
//                        .newBuilder()
//                        .addQueryParameter("key", apiKey)
//                        .build();
//
//                Request request = new Request.Builder()
//                        .url(url)
//                        .post(RequestBody.create(
//                                mapper.writeValueAsString(bodyJson),
//                                MediaType.parse("application/json")
//                        ))
//                        .build();
//
//                // Execute request
//                Response response = client.newCall(request).execute();
//
//                if (!response.isSuccessful()) {
//                    throw new RuntimeException(
//                            "Gemini HTTP " + response.code() +
//                                    " — body: " + (response.body() == null ? "" : response.body().string())
//                    );
//                }
//
//                String raw = response.body().string();
//                return parse(raw);
//
//            } catch (Exception e) {
//                throw new RuntimeException("Gemini failed: " + e.getMessage(), e);
//            }
//
//        }, EXECUTOR);
//    }
//
//
//    // ---------- FIXED PARSER ----------
//    private AiResponse parse(String raw) throws Exception {
//
//        JsonNode root = mapper.readTree(raw);
//
//        JsonNode textNode = root
//                .path("candidates")
//                .path(0)
//                .path("content")
//                .path("parts")
//                .path(0)
//                .path("text");
//
//        if (textNode.isMissingNode()) {
//            throw new RuntimeException("Gemini returned no usable text. Raw:\n" + raw);
//        }
//
//        String text = textNode.asText();
//
//        AiResponse res = new AiResponse();
//        res.setExplanation(text);
//        res.setPatch(text);
//        return res;
//    }
//
//
//    @Override
//    public String providerId() {
//        return "gemini";
//    }
//}
