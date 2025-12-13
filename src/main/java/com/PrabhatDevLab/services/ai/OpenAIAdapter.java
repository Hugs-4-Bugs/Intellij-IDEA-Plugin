//package com.PrabhatDevLab.services.ai;
//
//import com.PrabhatDevLab.services.models.AiResponse;
//import com.PrabhatDevLab.services.models.PatchModel;
//import com.PrabhatDevLab.services.models.PromptRequest;
//import com.PrabhatDevLab.settings.PrabhatAISettingsState;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//import okhttp3.*;
//
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.CompletableFuture;
//
//public class OpenAIAdapter implements AIProvider {
//
//    private String apiKey;
//    private final OkHttpClient client = new OkHttpClient();
//    private final ObjectMapper mapper = new ObjectMapper();
//
//    @Override
//    public CompletableFuture<AiResponse> completeCode(PromptRequest req) {
//
//        return CompletableFuture.supplyAsync(() -> {
//            try {
//
////                String apiKey = PrabhatAISettingsState.getInstance().openAiKey;
//                String apiKey = (this.apiKey != null && !this.apiKey.isBlank())
//                        ? this.apiKey
//                        : PrabhatAISettingsState.getInstance().openAiKey;
//
//
//                if (apiKey == null || apiKey.isBlank())
//                    throw new RuntimeException("OpenAI key missing");
//
//                String bodyJson = mapper.writeValueAsString(
//                        Map.of(
//                                "model", "gpt-4o-mini",
//                                "messages", List.of(
//                                        Map.of("role", "system", "content", """
//                                                You are PrabhatAI.
//                                                Respond ONLY valid JSON:
//                                                {
//                                                    "importsToAdd": [],
//                                                    "methodReplacements": [],
//                                                    "newMethods": []
//                                                }
//                                                """),
//                                        Map.of("role", "user", "content", req.getPrompt())
//                                )
//                        )
//                );
//
//                RequestBody body = RequestBody.create(bodyJson, MediaType.get("application/json"));
//
//                Request request = new Request.Builder()
//                        .url("https://api.openai.com/v1/chat/completions")
//                        .addHeader("Authorization", "Bearer " + apiKey)
//                        .post(body)
//                        .build();
//
//                Response response = client.newCall(request).execute();
//                if (!response.isSuccessful())
//                    throw new RuntimeException("OpenAI error " + response.code());
//
//                return parseResponse(response.body().string());
//
//            } catch (Exception e) {
//                throw new RuntimeException("OpenAIAdapter failed: " + e.getMessage(), e);
//            }
//        });
//    }
//
//
//    private AiResponse parseResponse(String raw) throws Exception {
//
//        JsonNode root = mapper.readTree(raw);
//
//        // NEW OpenAI format: "content" is an array like:
//        // "content": [ { "type": "text", "text": "{json...}" } ]
//        JsonNode contentNode = root
//                .path("choices")
//                .get(0)
//                .path("message")
//                .path("content");
//
//        if (contentNode.isArray()) {
//            contentNode = contentNode.get(0).path("text");
//        }
//
//        String content = contentNode.asText(); // ← now NOT empty
//
//        JsonNode json = mapper.readTree(content);
//
//        PatchModel patch = new PatchModel();
//
//        if (json.has("importsToAdd"))
//            json.get("importsToAdd").forEach(n -> patch.importsToAdd.add(n.asText()));
//
//        if (json.has("methodReplacements"))
//            json.get("methodReplacements").forEach(n -> {
//                PatchModel.MethodBodyPatch m = new PatchModel.MethodBodyPatch();
//                m.methodName = n.get("methodName").asText();
//                m.newBody = n.get("newBody").asText();
//                patch.methodReplacements.add(m);
//            });
//
//        if (json.has("newMethods"))
//            json.get("newMethods").forEach(n -> {
//                PatchModel.MethodInsertPatch m = new PatchModel.MethodInsertPatch();
//                m.methodCode = n.get("methodCode").asText();
//                patch.newMethods.add(m);
//            });
//
//        AiResponse res = new AiResponse();
//        res.setPatch(content);
//        res.setPatchModel(patch);
//        res.setExplanation("OpenAI JSON patch generated");
//
//        return res;
//    }
//
//    @Override
//    public String providerId() {
//        return "openai";
//    }
//
//    public void setApiKey(String key) {
//        this.apiKey = key;
//    }
//
//}











//package com.PrabhatDevLab.services.ai;
//
//import com.PrabhatDevLab.services.models.AiResponse;
//import com.PrabhatDevLab.services.models.PromptRequest;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//import okhttp3.*;
//
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.CompletableFuture;
//
//public class OpenAIAdapter implements AIProvider {
//
//    private String apiKey;
//    private final OkHttpClient client = new OkHttpClient();
//    private final ObjectMapper mapper = new ObjectMapper();
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
//
//                if (apiKey == null || apiKey.isBlank())
//                    throw new RuntimeException("OpenAI key missing");
//
//                Map<String, Object> json = Map.of(
//                        "model", "gpt-4o-mini",
//                        "messages", List.of(
//                                Map.of("role", "user", "content", req.getPrompt())
//                        )
//                );
//
//                Request request = new Request.Builder()
//                        .url("https://api.openai.com/v1/chat/completions")
//                        .addHeader("Authorization", "Bearer " + apiKey)
//                        .post(RequestBody.create(
//                                mapper.writeValueAsString(json),
//                                MediaType.get("application/json")
//                        ))
//                        .build();
//
//                Response response = client.newCall(request).execute();
//                if (!response.isSuccessful())
//                    throw new RuntimeException("OpenAI HTTP " + response.code());
//
//                return parse(response.body().string());
//
//            } catch (Exception e) {
//                throw new RuntimeException("OpenAI failed: " + e.getMessage(), e);
//            }
//        });
//    }
//
//    private AiResponse parse(String raw) throws Exception {
//
//        JsonNode root = mapper.readTree(raw);
//
//        JsonNode contentNode = root
//                .path("choices")
//                .get(0)
//                .path("message")
//                .path("content");
//
//        String content;
//
//        if (contentNode.isArray()) {
//            content = contentNode.get(0).path("text").asText();
//        } else {
//            content = contentNode.asText();
//        }
//
//        AiResponse res = new AiResponse();
//        res.setPatch(content);
//        res.setExplanation(content);
//        return res;
//    }
//
//    @Override
//    public String providerId() {
//        return "openai";
//    }
//}







package com.PrabhatDevLab.services.ai;

import com.PrabhatDevLab.services.models.AiResponse;
import com.PrabhatDevLab.services.models.PromptRequest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class OpenAIAdapter implements AIProvider {

    private String apiKey;
    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void setApiKey(String key) { this.apiKey = key; }

    @Override
    public CompletableFuture<AiResponse> completeCode(PromptRequest req) {

        return CompletableFuture.supplyAsync(() -> {
            try {
                if (apiKey == null || apiKey.isBlank())
                    throw new RuntimeException("OpenAI key missing");

                Map<String, Object> json = Map.of(
                        "model", "gpt-4o-mini",
                        "messages", List.of(
                                Map.of("role", "user", "content", req.getPrompt())
                        )
                );

                Request request = new Request.Builder()
                        .url("https://api.openai.com/v1/chat/completions")
                        .addHeader("Authorization", "Bearer " + apiKey)
                        .post(RequestBody.create(
                                mapper.writeValueAsString(json),
                                MediaType.get("application/json")
                        ))
                        .build();

                Response response = client.newCall(request).execute();
                if (!response.isSuccessful())
                    throw new RuntimeException("OpenAI HTTP " + response.code());

                return parse(response.body().string());

            } catch (Exception ex) {
                throw new RuntimeException("OpenAI failed: " + ex.getMessage(), ex);
            }
        });
    }

    private AiResponse parse(String raw) throws Exception {

        JsonNode contentNode = mapper.readTree(raw)
                .path("choices").get(0)
                .path("message")
                .path("content");

        String text = contentNode.isArray()
                ? contentNode.get(0).asText()
                : contentNode.asText();

        return new AiResponse(text);
    }

    @Override
    public String providerId() { return "openai"; }
}
