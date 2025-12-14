//package com.PrabhatDevLab.services.ai;
//
//import com.PrabhatDevLab.services.context.ProjectContextExtractor;
//import com.PrabhatDevLab.services.context.ActiveFileContextExtractor;
//import com.PrabhatDevLab.services.models.AiResponse;
//import com.PrabhatDevLab.services.models.PromptRequest;
//import com.intellij.openapi.project.Project;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.CompletableFuture;
//
//public class ProviderManager {
//
//    private final List<AIProvider> providers;
//    private final Project project; // <-- ADD THIS
//
//    public ProviderManager(List<AIProvider> providers, Project project) {
//        this.providers = providers;
//        this.project = project;
//    }
//
//    /**
//     * Build the FINAL prompt injected with:
//     * - Full project structure
//     * - Current open file content
//     * - Strict rules for response format
//     */
//    private String buildFinalPrompt(String userPrompt) {
//
//        String projectContext = ProjectContextExtractor.extract(project);
//        String activeFileContext = ActiveFileContextExtractor.extract(project);
//
//        return """
//                You are PrabhatAI, running INSIDE IntelliJ IDEA.
//                You must ONLY answer based on THIS CURRENT PROJECT.
//
//                RULES:
//                - NEVER give generic explanations.
//                - NEVER describe things unrelated to this project.
//                - NEVER mention frameworks/languages that are NOT in this project.
//                - ALWAYS give short, actionable, precise code answers.
//                - ALWAYS reference exact file paths when editing.
//                - When changing files, output ONLY changed lines.
//                - If the user asks something impossible for this project, tell them directly.
//                - If a file does not exist, tell which file to create and where.
//
//                ==============================
//                PROJECT STRUCTURE:
//                %s
//                ==============================
//
//                ACTIVE FILE:
//                %s
//                ==============================
//
//                USER QUESTION:
//                %s
//                ==============================
//                """.formatted(projectContext, activeFileContext, userPrompt);
//    }
//
//    /**
//     * Public entry point.
//     * We wrap the user's prompt with contextual IntelliJ project awareness.
//     */
//    public CompletableFuture<AiResponse> complete(PromptRequest req) {
//        String finalPrompt = buildFinalPrompt(req.getPrompt());
//        PromptRequest upgraded = new PromptRequest(finalPrompt);
//
//        return tryProvider(upgraded, 0, new ArrayList<>());
//    }
//
//    /**
//     * Provider failover logic.
//     */
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











//package com.PrabhatDevLab.services.ai;
//
//import com.PrabhatDevLab.services.context.ActiveFileContextExtractor;
//import com.PrabhatDevLab.services.context.ProjectContextExtractor;
//import com.PrabhatDevLab.services.models.AiResponse;
//import com.PrabhatDevLab.services.models.PromptRequest;
//import com.intellij.openapi.project.Project;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.CompletableFuture;
//
//public class ProviderManager {
//
//    private final List<AIProvider> providers;
//    private final Project project;
//
//    public ProviderManager(List<AIProvider> providers, Project project) {
//        this.providers = providers;
//        this.project = project;
//    }
//
//    // ============================================
//    // MAIN ENTRY — wraps prompt with full context
//    // ============================================
//    public CompletableFuture<AiResponse> complete(PromptRequest req) {
//
//        String finalPrompt = buildFinalPrompt(req.getPrompt());
//
//        PromptRequest upgraded = new PromptRequest();
//        upgraded.setPrompt(finalPrompt);
//        upgraded.setContext(req.getContext());
//
//        return tryProvider(upgraded, 0, new ArrayList<>());
//    }
//
//    // ============================================
//    // BUILD CONTEXT-AWARE PROMPT
//    // ============================================
//    private String buildFinalPrompt(String userPrompt) {
//        String projectContext = ProjectContextExtractor.extract(project);
//        String active = ActiveFileContextExtractor.extract(project);
//
//        return """
//                You are PrabhatAI inside IntelliJ IDEA.
//                You MUST answer only using the CURRENT PROJECT context.
//
//                PROJECT STRUCTURE:
//                %s
//
//                ACTIVE FILE:
//                %s
//
//                USER QUESTION:
//                %s
//                """.formatted(projectContext, active, userPrompt);
//    }
//
//    // ============================================
//    // PROVIDER FAILOVER LOGIC
//    // ============================================
//    private CompletableFuture<AiResponse> tryProvider(
//            PromptRequest req,
//            int index,
//            List<String> errors
//    ) {
//        if (index >= providers.size()) {
//            CompletableFuture<AiResponse> failed = new CompletableFuture<>();
//            failed.completeExceptionally(new RuntimeException(
//                    "No AI providers succeeded:\n" + String.join("\n", errors)
//            ));
//            return failed;
//        }
//
//        AIProvider provider = providers.get(index);
//
//        // ==============================
//        // GEMINI QUOTA GUARD
//        // ==============================
//        if (provider.providerId().equals("gemini") && QuotaGuard.isGeminiBlocked()) {
//            errors.add("[gemini] Skipped — quota is blocked (auto-retry later)");
//            return tryProvider(req, index + 1, errors);
//        }
//
//        // ==============================
//        // RUN PROVIDER
//        // ==============================
//        return provider.completeCode(req)
//                .handle((response, err) -> {
//
//                    if (err == null && response != null) {
//                        return CompletableFuture.completedFuture(response);
//                    }
//
//                    // FAILURE
//                    errors.add("[%s] failed: %s".formatted(
//                            provider.providerId(),
//                            err != null ? err.getMessage() : "Unknown error"
//                    ));
//
//                    // Try next provider
//                    return tryProvider(req, index + 1, errors);
//
//                })
//                .thenCompose(x -> x);
//    }
//}





//package com.PrabhatDevLab.services.ai;
//
//import com.PrabhatDevLab.services.context.ActiveFileContextExtractor;
//import com.PrabhatDevLab.services.context.ProjectContextExtractor;
//import com.PrabhatDevLab.services.models.AiResponse;
//import com.PrabhatDevLab.services.models.PromptRequest;
//import com.intellij.openapi.project.Project;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.CompletableFuture;
//
//public class ProviderManager {
//
//    private final List<AIProvider> providers;
//    private final Project project;
//
//    public ProviderManager(List<AIProvider> providers, Project project) {
//        this.providers = providers;
//        this.project = project;
//    }
//
//    // ============================================
//    // PUBLIC ENTRY POINT
//    // ============================================
//    public CompletableFuture<AiResponse> complete(PromptRequest req) {
//
//        String finalPrompt = buildFinalPrompt(req.getPrompt());
//
//        PromptRequest upgraded = new PromptRequest();
//        upgraded.setPrompt(finalPrompt);
//        upgraded.setContext(req.getContext());
//
//        return tryProvider(upgraded, 0, new ArrayList<>());
//    }
//
//    // ============================================
//    // CONTEXT-AWARE PROMPT
//    // ============================================
//    private String buildFinalPrompt(String userPrompt) {
//        String projectContext = ProjectContextExtractor.extract(project);
//        String activeFile = ActiveFileContextExtractor.extract(project);
//
//        return """
//                You are PrabhatAI inside IntelliJ IDEA.
//                You MUST answer strictly using the CURRENT PROJECT context.
//
//                PROJECT STRUCTURE:
//                %s
//
//                ACTIVE FILE:
//                %s
//
//                USER QUESTION:
//                %s
//                """.formatted(projectContext, activeFile, userPrompt);
//    }
//
//    // ============================================
//    // FAILOVER + ASYNC SAFE EXECUTION
//    // ============================================
//    private CompletableFuture<AiResponse> tryProvider(
//            PromptRequest req,
//            int index,
//            List<String> errors
//    ) {
//
//        // No providers left → hard failure
//        if (index >= providers.size()) {
//            CompletableFuture<AiResponse> failed = new CompletableFuture<>();
//            failed.completeExceptionally(
//                    new RuntimeException("No AI providers succeeded:\n" + String.join("\n", errors))
//            );
//            return failed;
//        }
//
//        AIProvider provider = providers.get(index);
//
//        // ==============================
//        // QUOTA GUARD (GEMINI)
//        // ==============================
//        if ("gemini".equals(provider.providerId()) && QuotaGuard.isGeminiBlocked()) {
//            errors.add("[gemini] skipped — quota blocked");
//            return tryProvider(req, index + 1, errors);
//        }
//
//        // ==============================
//        // EXECUTE PROVIDER (SAFE)
//        // ==============================
//        return provider.completeCode(req)
//                .handle((response, throwable) -> {
//
//                    // SUCCESS
//                    if (throwable == null && response != null) {
//                        return CompletableFuture.completedFuture(response);
//                    }
//
//                    // FAILURE
//                    String reason = throwable != null
//                            ? throwable.getMessage()
//                            : "Unknown error";
//
//                    errors.add("[" + provider.providerId() + "] failed: " + reason);
//
//                    // Try next provider
//                    return tryProvider(req, index + 1, errors);
//                })
//                // CRITICAL: flatten nested future (NO dangling state)
//                .thenCompose(future -> future);
//    }
//}











package com.PrabhatDevLab.services.ai;

import com.PrabhatDevLab.services.context.ActiveFileContextExtractor;
import com.PrabhatDevLab.services.context.ProjectContextExtractor;
import com.PrabhatDevLab.services.models.AiResponse;
import com.PrabhatDevLab.services.models.PromptRequest;
import com.intellij.openapi.project.Project;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ProviderManager {

    private final List<AIProvider> providers;
    private final Project project;

    public ProviderManager(List<AIProvider> providers, Project project) {
        this.providers = new ArrayList<>(providers);
        this.project = project;
    }

    public CompletableFuture<AiResponse> complete(PromptRequest req) {
        // Build enhanced prompt with context
        String enhancedPrompt = enhancePromptWithContext(req.getPrompt());
        PromptRequest enhancedRequest = new PromptRequest(enhancedPrompt, req.getContext());

        System.out.println("[ProviderManager] Starting with " + providers.size() + " provider(s)");

        return tryProviders(enhancedRequest, 0)
                .thenApply(response -> {
                    System.out.println("[ProviderManager] ✅ Successfully got response");
                    return response;
                })
                .exceptionally(ex -> {
                    System.err.println("[ProviderManager] ❌ All providers failed: " + ex.getMessage());
                    return createFallbackResponse(ex);
                });
    }

    private String enhancePromptWithContext(String userPrompt) {
        try {
            String projectContext = "";
            String activeFileContext = "";

            // Safely extract context
            try {
                projectContext = ProjectContextExtractor.extract(project);
            } catch (Exception e) {
                System.err.println("[ProviderManager] Failed to extract project context: " + e.getMessage());
            }

            try {
                activeFileContext = ActiveFileContextExtractor.extract(project);
            } catch (Exception e) {
                System.err.println("[ProviderManager] Failed to extract active file context: " + e.getMessage());
            }

            // Build enhanced prompt
            StringBuilder enhanced = new StringBuilder();
            enhanced.append("You are PrabhatAI, an intelligent coding assistant in IntelliJ IDEA.\n\n");

            if (projectContext != null && !projectContext.trim().isEmpty()) {
                enhanced.append("CURRENT PROJECT CONTEXT:\n").append(projectContext).append("\n\n");
            }

            if (activeFileContext != null && !activeFileContext.trim().isEmpty()) {
                enhanced.append("ACTIVE FILE:\n").append(activeFileContext).append("\n\n");
            }

            enhanced.append("USER REQUEST:\n").append(userPrompt).append("\n\n");
            enhanced.append("Please provide a helpful, concise response. Include code examples when relevant.");

            return enhanced.toString();

        } catch (Exception e) {
            System.err.println("[ProviderManager] Error enhancing prompt: " + e.getMessage());
            return userPrompt; // Fallback to original prompt
        }
    }

    private CompletableFuture<AiResponse> tryProviders(PromptRequest req, int index) {
        if (index >= providers.size()) {
            CompletableFuture<AiResponse> future = new CompletableFuture<>();
            future.completeExceptionally(new RuntimeException("All providers failed"));
            return future;
        }

        AIProvider provider = providers.get(index);
        System.out.println("[ProviderManager] Trying provider #" + (index + 1) + ": " + provider.providerId());

        return provider.completeCode(req)
                .handle((response, error) -> {
                    if (error == null && response != null &&
                            response.getExplanation() != null &&
                            !response.getExplanation().trim().isEmpty()) {

                        // Success with this provider
                        return CompletableFuture.completedFuture(response);

                    } else {
                        // This provider failed, try next one
                        String errorMsg = error != null ? error.getMessage() : "Empty response";
                        System.err.println("[ProviderManager] Provider " + provider.providerId() + " failed: " + errorMsg);

                        return tryProviders(req, index + 1);
                    }
                })
                .thenCompose(future -> future); // Flatten the CompletableFuture
    }

    private AiResponse createFallbackResponse(Throwable ex) {
        AiResponse fallback = new AiResponse();

        String errorMessage = ex != null ? ex.getMessage() : "Unknown error";

        fallback.setExplanation(
                "## ⚠️ Service Temporarily Unavailable\n\n" +
                        "All AI providers are currently unavailable.\n\n" +
                        "**Possible reasons:**\n" +
                        "1. Network connectivity issues\n" +
                        "2. API service downtime\n" +
                        "3. Invalid API configuration\n\n" +
                        "**What you can do:**\n" +
                        "• Check your internet connection\n" +
                        "• Verify API keys in Settings > PrabhatAI\n" +
                        "• Try using Mock provider for testing\n" +
                        "• Wait a few minutes and try again\n\n" +
                        "Error details: " + (errorMessage != null ? errorMessage : "Unknown")
        );

        return fallback;
    }

    public int getAvailableProviders() {
        return providers.size();
    }
}