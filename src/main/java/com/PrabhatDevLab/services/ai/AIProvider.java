//package com.PrabhatDevLab.services.ai;
//
//import com.PrabhatDevLab.services.models.AiResponse;
//import com.PrabhatDevLab.services.models.PromptRequest;
//
//import java.util.concurrent.CompletableFuture;
//
//public interface AIProvider {
//    CompletableFuture<AiResponse> completeCode(PromptRequest request);
//    String providerId();
//
//    default void streamCompletion(PromptRequest request, StreamCallback callback) {
//        // Optional; overridden by providers that support streaming
//        throw new UnsupportedOperationException("Streaming not implemented");
//    }
//
//}




package com.PrabhatDevLab.services.ai;

import com.PrabhatDevLab.services.models.AiResponse;
import com.PrabhatDevLab.services.models.PromptRequest;

import java.util.concurrent.CompletableFuture;

public interface AIProvider {

    CompletableFuture<AiResponse> completeCode(PromptRequest request);

    String providerId();

    default void setApiKey(String key) {}
}
