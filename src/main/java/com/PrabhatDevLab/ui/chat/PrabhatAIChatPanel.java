//package com.PrabhatDevLab.ui.chat;
//
//import com.PrabhatDevLab.services.AiFacadeService;
//import com.PrabhatDevLab.services.models.AiResponse;
//import com.PrabhatDevLab.ui.DiffPreviewPanel;
//import com.intellij.openapi.project.Project;
//
//import javax.swing.*;
//import java.awt.*;
//import java.util.concurrent.CompletableFuture;
//
///**
// * ChatPanel:
// * A simple AI chat-like interface inside IntelliJ.
// *
// * Features:
// *  - User input box
// *  - Scrollable conversation history
// *  - Sends prompts to PrabhatAI
// *  - Shows AI replies
// *  - If reply contains patches → forwarded to DiffPreviewPanel
// */
//public class PrabhatAIChatPanel extends JPanel {
//
//    private final Project project;
//    private final JTextArea chatArea;
//    private final JTextField inputField;
//
//    public PrabhatAIChatPanel(Project project) {
//        this.project = project;
//
//        setLayout(new BorderLayout());
//
//        chatArea = new JTextArea();
//        chatArea.setEditable(false);
//        chatArea.setLineWrap(true);
//
//        JScrollPane scrollPane = new JScrollPane(chatArea);
//
//        inputField = new JTextField();
//        inputField.addActionListener(e -> sendMessage());
//
//        add(scrollPane, BorderLayout.CENTER);
//        add(inputField, BorderLayout.SOUTH);
//    }
//
//    private void sendMessage() {
//        String prompt = inputField.getText().trim();
//        if (prompt.isEmpty()) return;
//
//        appendUser(prompt);
//        inputField.setText("");
//
//        AiFacadeService ai = AiFacadeService.getInstance(project);
//        CompletableFuture<AiResponse> future = ai.requestCompletion(prompt);
//
//        future.thenAccept(response -> SwingUtilities.invokeLater(() -> {
//            appendAI(response.getExplanation());
//
//            // Forward patches to Tool Window
//            if (response.getMultiFilePatch() != null &&
//                    response.getMultiFilePatch().hasChanges()) {
//
//                DiffPreviewPanel.getInstance(project).setMultiFilePatch(response.getMultiFilePatch());
//            } else if (response.getPatchModel() != null &&
//                    response.getPatchModel().hasChanges()) {
//
//                DiffPreviewPanel.getInstance(project).setPatchModel(response.getPatchModel());
//            }
//
//        })).exceptionally(ex -> {
//            appendAI("⚠️ Error: " + ex.getMessage());
//            return null;
//        });
//    }
//
//    private void appendUser(String msg) {
//        chatArea.append("👤 You: " + msg + "\n\n");
//    }
//
//    private void appendAI(String msg) {
//        chatArea.append("🤖 PrabhatAI: " + msg + "\n\n");
//    }
//}





package com.PrabhatDevLab.ui.chat;

import com.PrabhatDevLab.services.AiFacadeService;
import com.PrabhatDevLab.services.models.AiResponse;
import com.PrabhatDevLab.ui.DiffPreviewPanel;
import com.intellij.openapi.project.Project;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.CompletableFuture;

public class PrabhatAIChatPanel extends JPanel {

    private final Project project;
    private final JTextArea chatArea;
    private final JTextField inputField;

    public PrabhatAIChatPanel(Project project) {
        this.project = project;

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        // CHAT OUTPUT
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(chatArea);

        // INPUT FIELD
        inputField = new JTextField();
        inputField.addActionListener(e -> sendMessage());

        JButton sendBtn = new JButton("Send");
        sendBtn.addActionListener(e -> sendMessage());

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(inputField, BorderLayout.CENTER);
        bottom.add(sendBtn, BorderLayout.EAST);

        add(scrollPane, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
    }

    private void sendMessage() {
        String prompt = inputField.getText().trim();
        if (prompt.isEmpty()) return;

        appendUser(prompt);
        inputField.setText("");

        AiFacadeService ai = AiFacadeService.getInstance(project);

        CompletableFuture<AiResponse> future =
                ai.requestCompletion(prompt);

        future.thenAccept(response ->
                SwingUtilities.invokeLater(() -> handleAIResponse(response))
        ).exceptionally(ex -> {
            SwingUtilities.invokeLater(() ->
                    appendAI("⚠️ Error: " + ex.getMessage())
            );
            return null;
        });
    }

    private void handleAIResponse(AiResponse res) {
        appendAI(res.getExplanation());

        // PATCH FORWARDING
        if (res.getMultiFilePatch() != null && res.getMultiFilePatch().hasChanges()) {
            DiffPreviewPanel.getInstance(project).setMultiFilePatch(res.getMultiFilePatch());
        } else if (res.getPatchModel() != null && res.getPatchModel().hasChanges()) {
            DiffPreviewPanel.getInstance(project).setPatchModel(res.getPatchModel());
        }
    }

    private void appendUser(String msg) {
        chatArea.append("👤 You:\n" + msg + "\n\n");
        scrollToBottom();
    }

    private void appendAI(String msg) {
        chatArea.append("🤖 PrabhatAI:\n" + msg + "\n\n");
        scrollToBottom();
    }

    private void scrollToBottom() {
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }
}



