//package com.PrabhatDevLab.services;
//
//import com.PrabhatDevLab.services.ai.*;
//import com.PrabhatDevLab.services.models.AiResponse;
//import com.PrabhatDevLab.services.models.PromptRequest;
//import com.PrabhatDevLab.settings.PrabhatAISettingsState;
//
//import com.intellij.openapi.project.Project;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.CompletableFuture;
//
//public class AiFacadeService {
//
//    private final ProviderManager manager;
//
//    public AiFacadeService(Project project) {
//
//        PrabhatAISettingsState st = PrabhatAISettingsState.getInstance();
//        List<AIProvider> list = new ArrayList<>();
//
//        for (String id : st.providerOrder.split(",")) {
//            id = id.trim();
//
//            switch (id) {
//                case "mock":
//                    if (st.enableMock)
//                        list.add(new MockAIProvider());
//                    break;
//
//                case "gemini":
//                    if (st.enableGemini) {
//                        GeminiAdapter g = new GeminiAdapter();
//                        g.setApiKey(st.geminiKey);
//                        list.add(g);
//                    }
//                    break;
//
//                case "openai":
//                    if (st.enableOpenAI) {
//                        OpenAIAdapter o = new OpenAIAdapter();
//                        o.setApiKey(st.openAiKey);
//                        list.add(o);
//                    }
//                    break;
//            }
//        }
//
//        if (list.isEmpty())
//            list.add(new MockAIProvider());
//
//        this.manager = new ProviderManager(list, project);
//    }
//
//    public static AiFacadeService getInstance(Project project) {
//        return project.getService(AiFacadeService.class);
//    }
//
//    public CompletableFuture<AiResponse> requestCompletion(String text) {
//        PromptRequest req = new PromptRequest();
//        req.setPrompt(text);
//        req.setContext("");
//        return manager.complete(req);
//    }
//}
//









//package com.PrabhatDevLab.services;
//
//import com.PrabhatDevLab.services.ai.*;
//import com.PrabhatDevLab.services.models.AiResponse;
//import com.PrabhatDevLab.services.models.PromptRequest;
//import com.PrabhatDevLab.settings.PrabhatAISettingsState;
//
//import com.intellij.openapi.project.Project;
//import org.jetbrains.annotations.NotNull;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.CompletableFuture;
//
///**
// * AiFacadeService:
// * Selects the correct AI provider according to priority & settings.
// */
//public class AiFacadeService {
//
//    private final ProviderManager providerManager;
//    private final Project project;
//
//    public AiFacadeService(@NotNull Project project) {
//        this.project = project;
//
//        PrabhatAISettingsState st = PrabhatAISettingsState.getInstance();
//        List<AIProvider> providers = new ArrayList<>();
//
//        // Priority: mock,gemini,claude,openai
//        for (String id : st.providerOrder.split(",")) {
//            id = id.trim();
//
//            switch (id) {
//
//                case "mock":
//                    if (st.enableMock) {
//                        providers.add(new MockAIProvider());
//                    }
//                    break;
//
//                case "gemini":
//                    if (st.enableGemini) {
//                        GeminiAdapter g = new GeminiAdapter();
//                        g.setApiKey(st.geminiKey);
//                        providers.add(g);
//                    }
//                    break;
//
//                case "claude":
//                    if (st.enableClaude) {
//                        ClaudeAdapter c = new ClaudeAdapter();
//                        c.setApiKey(st.claudeKey);
//                        providers.add(c);
//                    }
//                    break;
//
//                case "openai":
//                    if (st.enableOpenAI) {
//                        OpenAIAdapter o = new OpenAIAdapter();
//                        o.setApiKey(st.openAiKey);   // ✅ FIXED — key now passed correctly
//                        providers.add(o);
//                    }
//                    break;
//
//                default:
//                    // ignore unknown IDs
//                    break;
//            }
//        }
//
//        // Fallback if someone disables every provider
//        if (providers.isEmpty()) {
//            providers.add(new MockAIProvider());
//        }
//
//        this.providerManager = new ProviderManager(providers);
//    }
//
//    public static AiFacadeService getInstance(Project project) {
//        return project.getService(AiFacadeService.class);
//    }
//
//    /**
//     * MAIN method used by ChatPanel.
//     */
//    public CompletableFuture<AiResponse> requestCompletion(String text) {
//        PromptRequest req = new PromptRequest();
//        req.setPrompt(text);
//        req.setContext(""); // TODO: Add PSI context later
//        return providerManager.complete(req);
//    }
//
//    /**
//     * Editor refactoring mode (patch-based request).
//     */
//    public CompletableFuture<AiResponse> completePrompt(PromptRequest req) {
//        return providerManager.complete(req);
//    }
//}














package com.PrabhatDevLab.services;

import com.PrabhatDevLab.services.ai.*;
import com.PrabhatDevLab.services.models.AiResponse;
import com.PrabhatDevLab.services.models.PromptRequest;
import com.PrabhatDevLab.settings.PrabhatAISettingsState;
import com.intellij.openapi.project.Project;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class AiFacadeService {

    private final ProviderManager manager;
    private final Project project;

    public AiFacadeService(Project project) {
        this.project = project;

        PrabhatAISettingsState st = PrabhatAISettingsState.getInstance();
        List<AIProvider> providers = new ArrayList<>();

        // Default provider order if not set
        String providerOrder = st.providerOrder != null && !st.providerOrder.isEmpty()
                ? st.providerOrder
                : "mock,openai,gemini";

        System.out.println("[AiFacade] Initializing with provider order: " + providerOrder);

        for (String id : providerOrder.split(",")) {
            id = id.trim().toLowerCase();

            switch (id) {
                case "openai":
                    if (st.enableOpenAI && isValidKey(st.openAiKey)) {
                        try {
                            OpenAIAdapter adapter = new OpenAIAdapter();
                            adapter.setApiKey(st.openAiKey.trim());
                            providers.add(adapter);
                            System.out.println("[AiFacade] ✅ OpenAI provider added");
                        } catch (Exception e) {
                            System.err.println("[AiFacade] ❌ Failed to initialize OpenAI: " + e.getMessage());
                        }
                    } else {
                        System.out.println("[AiFacade] ⚠️ OpenAI disabled or invalid key");
                    }
                    break;

                case "gemini":
                    if (st.enableGemini && isValidKey(st.geminiKey)) {
                        try {
                            GeminiAdapter adapter = new GeminiAdapter();
                            adapter.setApiKey(st.geminiKey.trim());
                            providers.add(adapter);
                            System.out.println("[AiFacade] ✅ Gemini provider added");
                        } catch (Exception e) {
                            System.err.println("[AiFacade] ❌ Failed to initialize Gemini: " + e.getMessage());
                        }
                    } else {
                        System.out.println("[AiFacade] ⚠️ Gemini disabled or invalid key");
                    }
                    break;

                case "mock":
                    if (st.enableMock) {
                        providers.add(new MockAIProvider());
                        System.out.println("[AiFacade] ✅ Mock provider added");
                    }
                    break;

                default:
                    System.out.println("[AiFacade] ⚠️ Unknown provider ID: " + id);
            }
        }

        // Ensure at least one provider exists
        if (providers.isEmpty()) {
            System.out.println("[AiFacade] ⚠️ No providers configured, using Mock as fallback");
            providers.add(new MockAIProvider());
        }

        System.out.println("[AiFacade] Total providers: " + providers.size());
        this.manager = new ProviderManager(providers, project);
    }

    private boolean isValidKey(String key) {
        return key != null && !key.trim().isEmpty() && key.trim().length() > 10;
    }

    public static AiFacadeService getInstance(Project project) {
        return project.getService(AiFacadeService.class);
    }

    public CompletableFuture<AiResponse> requestCompletion(String text) {
        if (text == null || text.trim().isEmpty()) {
            CompletableFuture<AiResponse> future = new CompletableFuture<>();
            future.completeExceptionally(new IllegalArgumentException("Prompt cannot be empty"));
            return future;
        }

        String trimmedText = text.trim();
        System.out.println("[AiFacade] Processing request: " +
                (trimmedText.length() > 100 ? trimmedText.substring(0, 100) + "..." : trimmedText));

        PromptRequest req = new PromptRequest(trimmedText, "");

        return manager.complete(req)
                .exceptionally(ex -> {
                    System.err.println("[AiFacade] Request failed: " + ex.getMessage());

                    // Create a friendly error response
                    AiResponse errorResponse = new AiResponse();
                    String errorMsg = ex.getMessage();

                    if (errorMsg.contains("API key") || errorMsg.contains("key")) {
                        errorResponse.setExplanation(
                                "## 🔑 API Key Issue\n\n" +
                                        "Please check your API key configuration:\n" +
                                        "1. Go to **Settings > PrabhatAI**\n" +
                                        "2. Ensure your API keys are valid\n" +
                                        "3. Enable the AI providers you want to use\n\n" +
                                        "Error details: " + errorMsg
                        );
                    } else if (errorMsg.contains("quota") || errorMsg.contains("limit")) {
                        errorResponse.setExplanation(
                                "## ⚠️ Quota Limit Reached\n\n" +
                                        "You've reached your API quota limit. Please:\n" +
                                        "1. Check your API provider dashboard\n" +
                                        "2. Upgrade your plan if needed\n" +
                                        "3. Try again later\n\n" +
                                        "Error details: " + errorMsg
                        );
                    } else {
                        errorResponse.setExplanation(
                                "## ❌ Error Processing Request\n\n" +
                                        "Unable to get response from AI providers.\n\n" +
                                        "**Possible solutions:**\n" +
                                        "• Check your internet connection\n" +
                                        "• Verify API keys in settings\n" +
                                        "• Try using a different AI provider\n\n" +
                                        "Error: " + errorMsg
                        );
                    }

                    return errorResponse;
                });
    }

    // Helper method to check if service is ready
    public boolean isReady() {
        return manager != null;
    }
}