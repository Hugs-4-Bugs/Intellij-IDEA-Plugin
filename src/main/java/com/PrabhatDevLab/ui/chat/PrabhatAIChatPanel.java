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
import com.PrabhatDevLab.ui.MarkdownRenderer;
import com.PrabhatDevLab.ui.DiffPreviewPanel;
import com.intellij.openapi.project.Project;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.CompletableFuture;

public class PrabhatAIChatPanel extends JPanel {

    private final Project project;
    private final JEditorPane chatPane;
    private final JTextField inputField;

    private final StringBuilder htmlBuffer = new StringBuilder();

    public PrabhatAIChatPanel(Project project) {
        this.project = project;

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        // REAL HTML OUTPUT PANEL (IMPORTANT FIX)
        chatPane = new JEditorPane();
        chatPane.setContentType("text/html");
        chatPane.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(chatPane);

        inputField = new JTextField();
        inputField.addActionListener(e -> sendMessage());

        JButton sendBtn = new JButton("Send");
        sendBtn.addActionListener(e -> sendMessage());

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(inputField, BorderLayout.CENTER);
        bottom.add(sendBtn, BorderLayout.EAST);

        add(scrollPane, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        // Initialize HTML template
        htmlBuffer.append("<html><head>")
                .append("<style>")
                .append("body { font-family: 'Arial'; font-size: 13px; }")
                .append(".bubble-user { background:#e3f2fd; padding:10px; margin:8px; border-radius:8px; }")
                .append(".bubble-ai { background:#f1f8e9; padding:10px; margin:8px; border-radius:8px; }")
                .append(".code-container { background:#272822; color:white; padding:10px; border-radius:6px; margin-top:6px; }")
                .append(".copy-btn { float:right; margin-bottom:6px; }")
                .append("</style>")
                .append("</head><body>");
    }

    private void sendMessage() {
        String prompt = inputField.getText().trim();
        if (prompt.isEmpty()) return;

        appendUser(prompt);
        inputField.setText("");

        AiFacadeService ai = AiFacadeService.getInstance(project);

        CompletableFuture<AiResponse> future = ai.requestCompletion(prompt);

        // SHOW "thinking..." immediately
        appendAI("<i>Thinking...</i>");

        future.thenAccept(response ->
                SwingUtilities.invokeLater(() -> handleAIResponse(response))
        ).exceptionally(ex -> {
            SwingUtilities.invokeLater(() ->
                    appendAI("<b>Error:</b> " + ex.getMessage())
            );
            return null;
        });
    }

    private void handleAIResponse(AiResponse res) {
        String html = MarkdownRenderer.render(res.getExplanation());
        appendAI(html);

        // PATCH SUPPORT
        if (res.getMultiFilePatch() != null && res.getMultiFilePatch().hasChanges()) {
            DiffPreviewPanel.getInstance(project).setMultiFilePatch(res.getMultiFilePatch());
        } else if (res.getPatchModel() != null && res.getPatchModel().hasChanges()) {
            DiffPreviewPanel.getInstance(project).setPatchModel(res.getPatchModel());
        }
    }

    private void appendUser(String msg) {
        htmlBuffer.append("<div class='bubble-user'><b>You:</b><br>")
                .append(msg.replace("\n", "<br>"))
                .append("</div>");
        refreshChat();
    }

    private void appendAI(String html) {
        htmlBuffer.append("<div class='bubble-ai'><b>PrabhatAI:</b><br>")
                .append(html)
                .append("</div>");
        refreshChat();
    }

    private void refreshChat() {
        chatPane.setText(htmlBuffer.toString() + "</body></html>");
        chatPane.setCaretPosition(chatPane.getDocument().getLength());
    }
}

