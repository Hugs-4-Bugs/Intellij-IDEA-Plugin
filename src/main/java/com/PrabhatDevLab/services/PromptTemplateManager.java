package com.PrabhatDevLab.services;

public class PromptTemplateManager {
    public static String buildPrompt(String userText, String context) {
        if (userText == null || userText.isBlank()) userText = "Please improve this code.";
        StringBuilder sb = new StringBuilder();
        sb.append("Context:\n").append(context).append("\n\n");
        sb.append("Task:\n").append(userText).append("\n\n");
        sb.append("Return patch in unified minimal format with lines starting '- ' for removed and '+ ' for added.\n");
        return sb.toString();
    }
}
