package com.PrabhatDevLab.services.ai;

import com.PrabhatDevLab.services.models.AiResponse;
import com.PrabhatDevLab.services.models.PatchModel;
import com.PrabhatDevLab.services.models.PromptRequest;
import com.PrabhatDevLab.settings.PrabhatAISettingsState;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * REAL Claude Adapter using Anthropic Messages API.
 * Model: claude-3-5-sonnet (free trial available)
 */
public class ClaudeAdapter implements AIProvider {

    private String apiKey;
    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    // Anthropic Messages API endpoint
    private static final String CLAUDE_URL = "https://api.anthropic.com/v1/messages";


    @Override
    public CompletableFuture<AiResponse> completeCode(PromptRequest req) {

        return CompletableFuture.supplyAsync(() -> {

            try {
//                String apiKey = PrabhatAISettingsState.getInstance().claudeKey;

                String apiKey = (this.apiKey != null && !this.apiKey.isBlank())
                        ? this.apiKey
                        : PrabhatAISettingsState.getInstance().claudeKey;


                if (apiKey == null || apiKey.isBlank()) {
                    throw new RuntimeException("Claude API key missing. Set it in PrabhatAI Settings.");
                }

                String systemPrompt = """
                        You are PrabhatAI.

                        STRICT INSTRUCTION:
                        Output ONLY valid JSON following this exact format:

                        {
                          "importsToAdd": ["java.util.List"],
                          "methodReplacements": [
                            {"methodName": "signup", "newBody": "return true;"}
                          ],
                          "newMethods": [
                            {"methodCode": "public void googleAuth() {}"}
                          ]
                        }

                        DO NOT output explanations.
                        DO NOT wrap JSON in markdown fences.
                        """;

                // Build full Claude payload
                Map<String, Object> payload = Map.of(
                        "model", "claude-3-5-sonnet-20240620",
                        "max_tokens", 2048,
                        "messages", List.of(
                                Map.of("role", "system", "content", systemPrompt),
                                Map.of("role", "user", "content", req.getPrompt())
                        )
                );

                RequestBody body = RequestBody.create(
                        mapper.writeValueAsString(payload),
                        MediaType.get("application/json")
                );

                Request httpReq = new Request.Builder()
                        .url(CLAUDE_URL)
                        .addHeader("x-api-key", apiKey)
                        .addHeader("anthropic-version", "2023-06-01")
                        .post(body)
                        .build();

                Response response = client.newCall(httpReq).execute();

                if (!response.isSuccessful()) {
                    throw new RuntimeException("Claude Error: HTTP " + response.code());
                }

                return parseClaudeResponse(response.body().string());

            } catch (Exception ex) {
                throw new RuntimeException("ClaudeAdapter Error → " + ex.getMessage(), ex);
            }
        });
    }

    /**
     * Parse Claude messages response into PatchModel
     */
    private AiResponse parseClaudeResponse(String raw) throws Exception {

        JsonNode root = mapper.readTree(raw);

        JsonNode contentNode = root
                .path("content")
                .path(0)
                .path("text");

        if (contentNode.isMissingNode()) {
            throw new RuntimeException("Claude returned no valid text content.");
        }

        String text = contentNode.asText().trim();

        // Remove accidental markdown fences
        if (text.startsWith("```")) {
            text = text.replace("```json", "")
                    .replace("```", "")
                    .trim();
        }

        JsonNode json = mapper.readTree(text);

        PatchModel patch = new PatchModel();

        // Extract imports
        if (json.has("importsToAdd")) {
            json.get("importsToAdd").forEach(n -> patch.importsToAdd.add(n.asText()));
        }

        // Extract method replacements
        if (json.has("methodReplacements")) {
            json.get("methodReplacements").forEach(n -> {
                PatchModel.MethodBodyPatch p = new PatchModel.MethodBodyPatch();
                p.methodName = n.get("methodName").asText();
                p.newBody = n.get("newBody").asText();
                patch.methodReplacements.add(p);
            });
        }

        // Extract new methods
        if (json.has("newMethods")) {
            json.get("newMethods").forEach(n -> {
                PatchModel.MethodInsertPatch mp = new PatchModel.MethodInsertPatch();
                mp.methodCode = n.get("methodCode").asText();
                patch.newMethods.add(mp);
            });
        }

        AiResponse result = new AiResponse();
        result.setPatch(text);
        result.setExplanation("Claude generated structured JSON patch.");
        result.setPatchModel(patch);

        return result;
    }

    @Override
    public String providerId() {
        return "claude";
    }
    public void setApiKey(String key) {
        this.apiKey = key;
    }

}
