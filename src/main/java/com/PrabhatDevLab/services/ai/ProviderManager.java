package com.PrabhatDevLab.services.ai;

import com.PrabhatDevLab.services.models.AiResponse;
import com.PrabhatDevLab.services.models.PromptRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ProviderManager {

    private final List<AIProvider> providers;

    public ProviderManager(List<AIProvider> providers) {
        this.providers = providers;
    }

    public CompletableFuture<AiResponse> complete(PromptRequest req) {
        return tryProvider(req, 0, new ArrayList<>());
    }

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





























//package com.PrabhatDevLab.services.ai;
//
//import com.PrabhatDevLab.services.models.AiResponse;
//import com.PrabhatDevLab.services.models.PromptRequest;
//
//import java.util.LinkedHashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.CompletableFuture;
//
///**
// * ProviderManager with proper failover + detailed error reporting.
// *
// * Example error message now becomes:
// *
// * No AI providers succeeded.
// * [gemini] Gemini API Key missing.
// * [openai] 401 Unauthorized
// * [claude] Missing key
// *
// */
//public class ProviderManager {
//
//    private final List<AIProvider> providers;
//
//    public ProviderManager(List<AIProvider> providers) {
//        this.providers = providers;
//    }
//
//    /**
//     * Execute provider chain with automatic failover + aggregated error reporting.
//     */
//    public CompletableFuture<AiResponse> complete(PromptRequest req) {
//
//        Map<String, Throwable> errors = new LinkedHashMap<>();
//
//        return tryProvider(0, req, errors)
//                .thenApply(result -> {
//                    if (result != null) return result;
//
//                    // Build detailed error message
//                    StringBuilder sb = new StringBuilder("No AI providers succeeded.\n");
//
//                    errors.forEach((id, ex) ->
//                            sb.append("[").append(id).append("] ")
//                                    .append(ex.getMessage()).append("\n"));
//
//                    throw new RuntimeException(sb.toString());
//                });
//    }
//
//    private CompletableFuture<AiResponse> tryProvider(
//            int index,
//            PromptRequest req,
//            Map<String, Throwable> errors
//    ) {
//        if (index >= providers.size()) {
//            return CompletableFuture.completedFuture(null);
//        }
//
//        AIProvider provider = providers.get(index);
//
//        return provider.completeCode(req)
//                .handle((result, error) -> {
//                    if (error == null) {
//                        // SUCCESS
//                        return CompletableFuture.completedFuture(result);
//                    }
//
//                    // RECORD FAILURE
//                    errors.put(provider.providerId(), error.getCause() != null ? error.getCause() : error);
//
//                    // TRY NEXT PROVIDER
//                    return tryProvider(index + 1, req, errors);
//                })
//                .thenCompose(f -> f); // flatten
//    }
//}
