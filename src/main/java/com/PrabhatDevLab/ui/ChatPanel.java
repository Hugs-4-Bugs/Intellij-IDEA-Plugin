package com.PrabhatDevLab.ui;

import com.PrabhatDevLab.services.AiFacadeService;
import com.PrabhatDevLab.services.models.AiResponse;
import com.intellij.openapi.project.Project;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.CompletableFuture;

public class ChatPanel extends JPanel {

    private final JTextArea chatArea;
    private final JTextField inputField;
    private final JButton sendButton;

    private final Project project;

    public ChatPanel(Project project) {
        this.project = project;

        setLayout(new BorderLayout(10, 10));

        // CHAT HISTORY AREA
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);

        JScrollPane scroll = new JScrollPane(chatArea);
        add(scroll, BorderLayout.CENTER);

        // BOTTOM INPUT BAR
        JPanel inputPanel = new JPanel(new BorderLayout(6, 6));
        inputField = new JTextField();
        sendButton = new JButton("Send");

        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        add(inputPanel, BorderLayout.SOUTH);

        // ENTER or CLICK
        inputField.addActionListener(e -> sendMessage());
        sendButton.addActionListener(e -> sendMessage());
    }

    private void appendMsg(String who, String txt) {
        chatArea.append(who + ": " + txt + "\n\n");
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }

    private void sendMessage() {
        String userMsg = inputField.getText().trim();
        if (userMsg.isEmpty()) return;

        appendMsg("You", userMsg);
        inputField.setText("");

        AiFacadeService ai = AiFacadeService.getInstance(project);

        CompletableFuture<AiResponse> future = ai.requestCompletion(userMsg);

        future.thenAccept(res -> {
            SwingUtilities.invokeLater(() -> {
                String reply =
                        (res.getExplanation() != null && !res.getExplanation().isBlank())
                                ? res.getExplanation()
                                : res.getPatch();

                if (reply == null || reply.isBlank()) reply = res.getPatch(); // fallback
                if (reply == null || reply.isBlank()) reply = "No response.";

                appendMsg("PrabhatAI", reply);
            });
        }).exceptionally(ex -> {
            SwingUtilities.invokeLater(() ->
                    appendMsg("PrabhatAI", "⚠ ERROR: " + ex.getMessage()));
            return null;
        });
    }
}
