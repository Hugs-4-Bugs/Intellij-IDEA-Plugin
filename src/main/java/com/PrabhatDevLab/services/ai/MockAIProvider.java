//package com.PrabhatDevLab.services.ai;
//
//import com.PrabhatDevLab.services.models.AiResponse;
//import com.PrabhatDevLab.services.models.PromptRequest;
//
//import java.util.concurrent.CompletableFuture;
//
//public class MockAIProvider implements AIProvider {
//    @Override
//    public CompletableFuture<AiResponse> completeCode(PromptRequest request) {
//        return CompletableFuture.supplyAsync(() -> {
//            // Deterministic mock: if prompt contains "google auth", return sample patch
//            String prompt = request.getPrompt() == null ? "" : request.getPrompt().toLowerCase();
//            AiResponse r = new AiResponse();
//            if (prompt.contains("google auth") || prompt.contains("oauth")) {
//                String patch = ""
//                        + "-        System.out.println(\"Hello world!\");\n"
//                        + "+        // TODO: implement Google OAuth2 signup here\n"
//                        + "+        System.out.println(\"[PrabhatAI] Google OAuth signup placeholder\");\n";
//                r.setPatch(patch);
//                r.setExplanation("Inserted placeholder for Google OAuth signup and replaced print statement.");
//            } else {
//                String patch = ""
//                        + "-        System.out.println(\"Hello world!\");\n"
//                        + "+        System.out.println(\"Namaste Prabhat!\");\n";
//                r.setPatch(patch);
//                r.setExplanation("Replaced Hello world with Namaste Prabhat for demo.");
//            }
//            return r;
//        });
//    }
//
//    @Override
//    public String providerId() {
//        return "mockai";
//    }
//}









package com.PrabhatDevLab.services.ai;

import com.PrabhatDevLab.services.models.AiResponse;
import com.PrabhatDevLab.services.models.PromptRequest;

import java.util.concurrent.CompletableFuture;

public class MockAIProvider implements AIProvider {

    @Override
    public CompletableFuture<AiResponse> completeCode(PromptRequest req) {
        return CompletableFuture.completedFuture(
                new AiResponse("Mock response: " + req.getPrompt())
        );
    }

    @Override
    public String providerId() {
        return "mock";
    }
}

