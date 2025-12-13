//package com.PrabhatDevLab.services.models;
//
///**
// * PromptRequest:
// * Represents the user instruction + code context sent to AI providers.
// *
// * This class must exist so AiFacadeService and all providers
// * can access the prompt and context correctly.
// */
//public class PromptRequest {
//
//    // The natural-language instruction or selected text
//    private String prompt;
//
//    // The code context extracted by ContextExtractor
//    private String context;
//
//    public String getPrompt() {
//        return prompt;
//    }
//
//    public void setPrompt(String prompt) {
//        this.prompt = prompt;
//    }
//
//    public String getContext() {
//        return context;
//    }
//
//    public void setContext(String context) {
//        this.context = context;
//    }
//}




package com.PrabhatDevLab.services.models;

public class PromptRequest {
    private String prompt;
    private String context;

    // DEFAULT CONSTRUCTOR (REQUIRED)
    public PromptRequest() { }

    // CONVENIENCE CONSTRUCTOR
    public PromptRequest(String prompt) {
        this.prompt = prompt;
        this.context = "";
    }

    // FULL CONSTRUCTOR
    public PromptRequest(String prompt, String context) {
        this.prompt = prompt;
        this.context = context;
    }

    public String getPrompt() { return prompt; }
    public void setPrompt(String prompt) { this.prompt = prompt; }

    public String getContext() { return context; }
    public void setContext(String context) { this.context = context; }
}

