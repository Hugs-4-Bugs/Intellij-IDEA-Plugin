//package com.PrabhatDevLab.services.ai;
//
//import com.PrabhatDevLab.services.models.AiResponse;
//import com.PrabhatDevLab.services.models.PromptRequest;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.CompletableFuture;
//
//public class ProviderManager {
//
//    private final List<AIProvider> providers;
//
//    public ProviderManager(List<AIProvider> providers) {
//        this.providers = providers;
//    }
//
//    public CompletableFuture<AiResponse> complete(PromptRequest req) {
//        return tryProvider(req, 0, new ArrayList<>());
//    }
//
//    private CompletableFuture<AiResponse> tryProvider(
//            PromptRequest req, int index, List<String> errors) {
//
//        if (index >= providers.size()) {
//            CompletableFuture<AiResponse> f = new CompletableFuture<>();
//            f.completeExceptionally(new RuntimeException(
//                    "No AI providers succeeded:\n" + String.join("\n", errors)
//            ));
//            return f;
//        }
//
//        AIProvider provider = providers.get(index);
//
//        return provider.completeCode(req)
//                .handle((result, error) -> {
//                    if (error == null)
//                        return CompletableFuture.completedFuture(result);
//
//                    errors.add("[" + provider.providerId() + "] failed: " + error.getMessage());
//                    return tryProvider(req, index + 1, errors);
//
//                }).thenCompose(x -> x);
//    }
//}




package com.PrabhatDevLab.services.ai;

import com.PrabhatDevLab.services.context.ProjectContextExtractor;
import com.PrabhatDevLab.services.context.ActiveFileContextExtractor;
import com.PrabhatDevLab.services.models.AiResponse;
import com.PrabhatDevLab.services.models.PromptRequest;
import com.intellij.openapi.project.Project;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ProviderManager {

    private final List<AIProvider> providers;
    private final Project project; // <-- ADD THIS

    public ProviderManager(List<AIProvider> providers, Project project) {
        this.providers = providers;
        this.project = project;
    }

    /**
     * Build the FINAL prompt injected with:
     * - Full project structure
     * - Current open file content
     * - Strict rules for response format
     */
    private String buildFinalPrompt(String userPrompt) {

        String projectContext = ProjectContextExtractor.extract(project);
        String activeFileContext = ActiveFileContextExtractor.extract(project);

        return """
                You are PrabhatAI, running INSIDE IntelliJ IDEA.
                You must ONLY answer based on THIS CURRENT PROJECT.

                RULES:
                - NEVER give generic explanations.
                - NEVER describe things unrelated to this project.
                - NEVER mention frameworks/languages that are NOT in this project.
                - ALWAYS give short, actionable, precise code answers.
                - ALWAYS reference exact file paths when editing.
                - When changing files, output ONLY changed lines.
                - If the user asks something impossible for this project, tell them directly.
                - If a file does not exist, tell which file to create and where.

                ==============================
                PROJECT STRUCTURE:
                %s
                ==============================

                ACTIVE FILE:
                %s
                ==============================

                USER QUESTION:
                %s
                ==============================
                """.formatted(projectContext, activeFileContext, userPrompt);
    }

    /**
     * Public entry point.
     * We wrap the user's prompt with contextual IntelliJ project awareness.
     */
    public CompletableFuture<AiResponse> complete(PromptRequest req) {
        String finalPrompt = buildFinalPrompt(req.getPrompt());
        PromptRequest upgraded = new PromptRequest(finalPrompt);

        return tryProvider(upgraded, 0, new ArrayList<>());
    }

    /**
     * Provider failover logic.
     */
    private CompletableFuture<AiResponse> tryProvider(
            PromptRequest req, int index, List<String> errors) {

        if (index >= providers.size()) {
            CompletableFuture<AiResponse> f = new CompletableFuture<>();
            f.completeExceptionally(new RuntimeException(
                    "No AI providers succeeded:\n" + String.join("\n", errors)
            ));
            return f;
        }

        AIProvider provider = providers.get(index);

        return provider.completeCode(req)
                .handle((result, error) -> {
                    if (error == null)
                        return CompletableFuture.completedFuture(result);

                    errors.add("[" + provider.providerId() + "] failed: " + error.getMessage());
                    return tryProvider(req, index + 1, errors);

                }).thenCompose(x -> x);
    }
}
