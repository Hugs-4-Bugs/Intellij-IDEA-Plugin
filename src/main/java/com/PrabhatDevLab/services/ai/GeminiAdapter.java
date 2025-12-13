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
///**
// * REAL Gemini Adapter
// * Uses Gemini 1.5 Flash (FREE model).
// *
// * Returns STRICT JSON -> PatchModel
// */
//public class GeminiAdapter implements AIProvider {
//
//    private String apiKey;
//
//    private final OkHttpClient client = new OkHttpClient();
//    private final ObjectMapper mapper = new ObjectMapper();
//
//    private static final String GEMINI_URL =
//            "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent";
//
//    @Override
//    public CompletableFuture<AiResponse> completeCode(PromptRequest request) {
//
//        return CompletableFuture.supplyAsync(() -> {
//            try {
////                String apiKey = PrabhatAISettingsState.getInstance().geminiKey;
//                String apiKey = (this.apiKey != null && !this.apiKey.isBlank())
//                        ? this.apiKey
//                        : PrabhatAISettingsState.getInstance().geminiKey;
//
//                if (apiKey == null || apiKey.isBlank())
//                    throw new RuntimeException("❌ Gemini API Key missing. Set in PrabhatAI Settings.");
//
//                // Force Gemini to output ONLY JSON patch format
//                String systemPrompt = """
//                        You are PrabhatAI.
//                        ONLY output valid JSON in this exact structure:
//                        {
//                          "importsToAdd": ["java.util.List"],
//                          "methodReplacements": [
//                            {"methodName": "signup", "newBody": "return true;"}
//                          ],
//                          "newMethods": [
//                            {"methodCode": "public void googleAuth() {}"}
//                          ]
//                        }
//                        DO NOT output explanations.
//                        DO NOT include words outside JSON.
//                        """;
//
//                Map<String, Object> bodyJson = Map.of(
//                        "contents", List.of(
//                                Map.of("parts", List.of(Map.of("text", systemPrompt))),
//                                Map.of("parts", List.of(Map.of("text", request.getPrompt())))
//                        )
//                );
//
//                RequestBody requestBody = RequestBody.create(
//                        mapper.writeValueAsString(bodyJson),
//                        MediaType.get("application/json")
//                );
//
//                HttpUrl url = HttpUrl.parse(GEMINI_URL)
//                        .newBuilder()
//                        .addQueryParameter("key", apiKey)
//                        .build();
//
//                Request httpRequest = new Request.Builder()
//                        .url(url)
//                        .post(requestBody)
//                        .build();
//
//                Response resp = client.newCall(httpRequest).execute();
//                if (!resp.isSuccessful()) {
//                    throw new RuntimeException("Gemini Error: HTTP " + resp.code());
//                }
//
//                return parseGeminiResponse(resp.body().string());
//
//            } catch (Exception ex) {
//                throw new RuntimeException("GeminiAdapter failure: " + ex.getMessage(), ex);
//            }
//        });
//    }
//
//    /**
//     * Parse JSON returned by Gemini into PatchModel.
//     */
//    private AiResponse parseGeminiResponse(String raw) throws Exception {
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
//            throw new RuntimeException("Gemini returned no text candidate.");
//        }
//
//        String text = textNode.asText();
//
//        JsonNode json = mapper.readTree(text);
//
//        PatchModel patch = new PatchModel();
//
//        // -------------------------
//        // importsToAdd
//        // -------------------------
//        if (json.has("importsToAdd")) {
//            json.get("importsToAdd")
//                    .forEach(n -> patch.importsToAdd.add(n.asText()));
//        }
//
//        // -------------------------
//        // methodReplacements
//        // -------------------------
//        if (json.has("methodReplacements")) {
//            json.get("methodReplacements").forEach(n -> {
//                PatchModel.MethodBodyPatch mb = new PatchModel.MethodBodyPatch();
//                mb.methodName = n.get("methodName").asText();
//                mb.newBody = n.get("newBody").asText();
//                patch.methodReplacements.add(mb);
//            });
//        }
//
//        // -------------------------
//        // newMethods
//        // -------------------------
//        if (json.has("newMethods")) {
//            json.get("newMethods").forEach(n -> {
//                PatchModel.MethodInsertPatch mp = new PatchModel.MethodInsertPatch();
//                mp.methodCode = n.get("methodCode").asText();
//                patch.newMethods.add(mp);
//            });
//        }
//
//        AiResponse res = new AiResponse();
//        res.setPatchModel(patch);
//        res.setPatch(text);
//        res.setExplanation("Gemini produced structured patch.");
//
//        return res;
//    }
//
//    @Override
//    public String providerId() {
//        return "gemini";
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
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import okhttp3.*;
//
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.CompletableFuture;
//
//public class GeminiAdapter implements AIProvider {
//
//    private String apiKey;
//    private final OkHttpClient client = new OkHttpClient();
//    private final ObjectMapper mapper = new ObjectMapper();
//
//    private static final String ENDPOINT =
//            "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent";
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
//                    throw new RuntimeException("Gemini API key missing.");
//
//                // Gemini API input format
//                Map<String, Object> bodyJson = Map.of(
//                        "contents", List.of(
//                                Map.of("parts", List.of(
//                                        Map.of("text", req.getPrompt())
//                                ))
//                        )
//                );
//
//                HttpUrl url = HttpUrl.parse(ENDPOINT)
//                        .newBuilder()
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
//                if (!response.isSuccessful())
//                    throw new RuntimeException("Gemini HTTP " + response.code());
//
//                return parse(response.body().string());
//
//            } catch (Exception e) {
//                throw new RuntimeException("Gemini failed: " + e.getMessage(), e);
//            }
//        });
//    }
//
//    private AiResponse parse(String raw) throws Exception {
//        JsonNode root = mapper.readTree(raw);
//
//        JsonNode textNode = root.path("candidates")
//                .path(0)
//                .path("content")
//                .path(0)
//                .path("parts")
//                .path(0)
//                .path("text");
//
//        if (textNode.isMissingNode())
//            throw new RuntimeException("Gemini returned no text.");
//
//        String text = textNode.asText();
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
//







package com.PrabhatDevLab.services.ai;

import com.PrabhatDevLab.services.models.AiResponse;
import com.PrabhatDevLab.services.models.PromptRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class GeminiAdapter implements AIProvider {

    private String apiKey;
    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    private static final String URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent";

    @Override
    public void setApiKey(String key) { this.apiKey = key; }

    @Override
    public CompletableFuture<AiResponse> completeCode(PromptRequest req) {

        return CompletableFuture.supplyAsync(() -> {
            try {
                if (apiKey == null || apiKey.isBlank())
                    throw new RuntimeException("Gemini key missing");

                Map<String, Object> json = Map.of(
                        "contents", List.of(
                                Map.of("parts", List.of(
                                        Map.of("text", req.getPrompt())
                                ))
                        )
                );

                HttpUrl url = HttpUrl.parse(URL)
                        .newBuilder()
                        .addQueryParameter("key", apiKey)
                        .build();

                Request request = new Request.Builder()
                        .url(url)
                        .post(RequestBody.create(mapper.writeValueAsString(json),
                                MediaType.get("application/json")))
                        .build();

                Response response = client.newCall(request).execute();
                if (!response.isSuccessful())
                    throw new RuntimeException("Gemini HTTP " + response.code());

                return parse(response.body().string());

            } catch (Exception ex) {
                throw new RuntimeException("Gemini failed: " + ex.getMessage(), ex);
            }
        });
    }

    private AiResponse parse(String raw) throws Exception {

        JsonNode node = mapper.readTree(raw)
                .path("candidates")
                .path(0)
                .path("content")
                .path(0)
                .path("parts")
                .path(0)
                .path("text");

        if (node.isMissingNode())
            throw new RuntimeException("Gemini returned no text");

        String text = node.asText();

        AiResponse res = new AiResponse();
        res.setExplanation(text);
        res.setPatch(text);
        return res;
    }

    @Override
    public String providerId() { return "gemini"; }
}
