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

    public AiFacadeService(Project project) {

        PrabhatAISettingsState st = PrabhatAISettingsState.getInstance();
        List<AIProvider> list = new ArrayList<>();

        for (String id : st.providerOrder.split(",")) {
            id = id.trim();

            switch (id) {
                case "mock":
                    if (st.enableMock)
                        list.add(new MockAIProvider());
                    break;

                case "gemini":
                    if (st.enableGemini) {
                        GeminiAdapter g = new GeminiAdapter();
                        g.setApiKey(st.geminiKey);
                        list.add(g);
                    }
                    break;

                case "openai":
                    if (st.enableOpenAI) {
                        OpenAIAdapter o = new OpenAIAdapter();
                        o.setApiKey(st.openAiKey);
                        list.add(o);
                    }
                    break;
            }
        }

        if (list.isEmpty())
            list.add(new MockAIProvider());

        this.manager = new ProviderManager(list, project);
    }

    public static AiFacadeService getInstance(Project project) {
        return project.getService(AiFacadeService.class);
    }

    public CompletableFuture<AiResponse> requestCompletion(String text) {
        PromptRequest req = new PromptRequest();
        req.setPrompt(text);
        req.setContext("");
        return manager.complete(req);
    }
}










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
