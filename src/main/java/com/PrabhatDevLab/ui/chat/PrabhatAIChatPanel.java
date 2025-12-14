//package com.PrabhatDevLab.ui.chat;
//
//import com.PrabhatDevLab.services.AiFacadeService;
//import com.PrabhatDevLab.services.models.AiResponse;
//import com.PrabhatDevLab.ui.DiffPreviewPanel;
//import com.PrabhatDevLab.ui.MarkdownRenderer;
//import com.intellij.openapi.project.Project;
//
//import javax.swing.*;
//import javax.swing.border.EmptyBorder;
//import java.awt.*;
//import java.awt.datatransfer.StringSelection;
//import java.util.concurrent.CompletableFuture;
//
//public class PrabhatAIChatPanel extends JPanel {
//
//    private final Project project;
//    private final JEditorPane chatPane;
//    private final JTextArea inputArea;
//
//    private final StringBuilder htmlMessages = new StringBuilder();
//
//    private static final String USER_ICON = "https://i.imgur.com/e9G4F0b.png";
//    private static final String AI_ICON = "https://i.imgur.com/yYpP5qE.png";
//
//    public PrabhatAIChatPanel(Project project) {
//        this.project = project;
//
//        setLayout(new BorderLayout());
//        setBorder(new EmptyBorder(8, 8, 8, 8));
//
//        // ===========================
//        // CHAT DISPLAY PANEL
//        // ===========================
//        chatPane = new JEditorPane();
//        chatPane.setContentType("text/html");
//        chatPane.setEditable(false);
//
//        // Copy-button handler
//        chatPane.addHyperlinkListener(e -> {
//            if ("copy".equals(e.getDescription())) {
//                String text = e.getURL().toString().replace("copy://", "");
//                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(text), null);
//                JOptionPane.showMessageDialog(this, "Copied!", "Clipboard", JOptionPane.INFORMATION_MESSAGE);
//            }
//        });
//
//        JScrollPane scrollPane = new JScrollPane(chatPane);
//        scrollPane.getVerticalScrollBar().setUnitIncrement(10);
//        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
//
//        // ===========================
//        // INPUT AREA
//        // ===========================
//        inputArea = new JTextArea(3, 20);
//        inputArea.setLineWrap(true);
//        inputArea.setWrapStyleWord(true);
//        inputArea.setFont(new Font("JetBrains Mono", Font.PLAIN, 13));
//
//        // Enter behaviour
//        inputArea.addKeyListener(new java.awt.event.KeyAdapter() {
//            @Override
//            public void keyPressed(java.awt.event.KeyEvent e) {
//                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
//                    if (e.isShiftDown()) {
//                        inputArea.append("\n");
//                    } else {
//                        e.consume();
//                        sendMessage();
//                    }
//                }
//            }
//        });
//
//        JButton sendBtn = new JButton("➤");
//        sendBtn.setPreferredSize(new Dimension(50, 40));
//        sendBtn.addActionListener(e -> sendMessage());
//
//        JPanel inputPanel = new JPanel(new BorderLayout());
//        inputPanel.add(new JScrollPane(inputArea), BorderLayout.CENTER);
//        inputPanel.add(sendBtn, BorderLayout.EAST);
//
//        add(scrollPane, BorderLayout.CENTER);
//        add(inputPanel, BorderLayout.SOUTH);
//
//        // ===========================
//        // START HTML (NO BODY CONTENT)
//        // ===========================
//        htmlMessages.append(""); // messages only, clean, no broken HTML
//        refreshChat(); // apply base CSS
//    }
//
//    // ================================
//    // SEND MESSAGE
//    // ================================
//    private void sendMessage() {
//
//        String prompt = inputArea.getText().trim();
//        if (prompt.isEmpty()) return;
//
//        appendUserMessage(prompt);
//        inputArea.setText("");
//
//        appendThinkingBubble();
//
//        AiFacadeService.getInstance(project)
//                .requestCompletion(prompt)
//                .thenAccept(res -> SwingUtilities.invokeLater(() -> handleAIResponse(res)))
//                .exceptionally(ex -> {
//
//                    // ▌ ADD THIS SNIPPET HERE — EXACT SPOT
//                    if (ex.getMessage() != null && ex.getMessage().contains("quota exceeded")) {
//                        SwingUtilities.invokeLater(() ->
//                                appendAIMessage("<b>⚠ Gemini quota exhausted.</b><br>Using backup provider...")
//                        );
//                        return null; // do not show the raw error
//                    }
//                    // -----------------------------------------------------
//
//                    SwingUtilities.invokeLater(() ->
//                            appendAIMessage("<b>Error:</b> " + ex.getMessage())
//                    );
//                    return null;
//                });
//    }
//
//    // ================================
//    // HANDLE RESPONSE
//    // ================================
//    private void handleAIResponse(AiResponse res) {
//
//        removeThinkingBubble();
//
//        String html = MarkdownRenderer.render(res.getExplanation());
//
//        appendAIMessage(html);
//
//        if (res.getMultiFilePatch() != null && res.getMultiFilePatch().hasChanges())
//            DiffPreviewPanel.getInstance(project).setMultiFilePatch(res.getMultiFilePatch());
//
//        if (res.getPatchModel() != null && res.getPatchModel().hasChanges())
//            DiffPreviewPanel.getInstance(project).setPatchModel(res.getPatchModel());
//    }
//
//    // ================================
//    // THINKING BUBBLE
//    // ================================
//    private void appendThinkingBubble() {
//        htmlMessages.append(
//                """
//                <div class='msg-row'>
//                    <img class='icon' src='%s'>
//                    <div class='bubble ai thinking' id='thinking'>Thinking...</div>
//                </div>
//                """.formatted(AI_ICON)
//        );
//        refreshChat();
//    }
//
//    private void removeThinkingBubble() {
//        int index = htmlMessages.indexOf("id='thinking'");
//        if (index != -1) {
//            int divStart = htmlMessages.lastIndexOf("<div", index);
//            int divEnd = htmlMessages.indexOf("</div>", index) + 6;
//            htmlMessages.delete(divStart, divEnd);
//        }
//        refreshChat();
//    }
//
//    // ================================
//    // ADD USER MESSAGE
//    // ================================
//    private void appendUserMessage(String text) {
//        htmlMessages.append(
//                """
//                <div class='msg-row right'>
//                    <div class='bubble user'><b>You</b><br>%s</div>
//                    <img class='icon' src='%s'>
//                </div>
//                """
//                        .formatted(text.replace("\n", "<br>"), USER_ICON)
//        );
//        refreshChat();
//    }
//
//
//    // ================================
//    // ADD AI MESSAGE
//    // ================================
//    private void appendAIMessage(String html) {
//        htmlMessages.append(
//                """
//                <div class='msg-row'>
//                    <img class='icon' src='%s'>
//                    <div class='bubble ai'><b>PrabhatAI</b><br>%s</div>
//                </div>
//                """
//                        .formatted(AI_ICON, html)
//        );
//        refreshChat();
//    }
//
//
//    // ================================
//    // RENDER FULL CHAT HTML
//    // ================================
//    private void refreshChat() {
//
//        String fullHtml = """
//        <html><head>
//        <style>
//            body {
//                background: #1e1e1e;
//                color: #ddd;
//                font-family: JetBrains Mono, monospace;
//                padding: 10px;
//                width: 100%;
//                overflow-x: hidden;
//            }
//
//            .msg-row {
//                display: flex;
//                gap: 10px;
//                margin: 12px 0;
//                width: 100%;
//            }
//
//            .msg-row.right {
//                justify-content: flex-end;
//            }
//
//            .icon {
//                width: 32px;
//                height: 32px;
//                border-radius: 50%;
//            }
//
//            .bubble {
//                padding: 12px;
//                border-radius: 10px;
//                max-width: 85%;
//                line-height: 1.5;
//                word-wrap: break-word;
//                background: #232323;
//            }
//
//            .bubble.user {
//                background: #2a3947;
//                border-left: 4px solid #6ab0ff;
//            }
//
//            .bubble.ai {
//                background: #232323;
//                border-left: 4px solid #4e9cff;
//            }
//
//            .thinking {
//                font-style: italic;
//                color: #8ab4f8;
//            }
//
//            /* CODE BLOCKS */
//            pre, code {
//                white-space: pre-wrap !important;
//                word-break: break-word !important;
//                overflow-x: hidden !important;
//            }
//        </style>
//        </head><body>
//        """ + htmlMessages + "</body></html>";
//
//        chatPane.setText(fullHtml);
//        chatPane.setCaretPosition(chatPane.getDocument().getLength());
//    }
//}










//package com.PrabhatDevLab.ui.chat;
//
//import com.PrabhatDevLab.services.AiFacadeService;
//import com.PrabhatDevLab.services.models.AiResponse;
//import com.PrabhatDevLab.ui.DiffPreviewPanel;
//import com.PrabhatDevLab.ui.MarkdownRenderer;
//import com.intellij.openapi.project.Project;
//
//import javax.swing.*;
//import javax.swing.border.EmptyBorder;
//import javax.swing.text.html.HTMLEditorKit;
//import java.awt.*;
//import java.awt.datatransfer.StringSelection;
//import java.util.concurrent.atomic.AtomicBoolean;
//
//public class PrabhatAIChatPanel extends JPanel {
//
//    private enum ChatState { IDLE, THINKING }
//
//    private final Project project;
//    private final JEditorPane chatPane;
//    private final JTextArea inputArea;
//
//    private final AtomicBoolean requestInFlight = new AtomicBoolean(false);
//    private ChatState state = ChatState.IDLE;
//
//    private final StringBuilder messages = new StringBuilder();
//
//    private static final String USER_ICON = "https://i.imgur.com/e9G4F0b.png";
//    private static final String AI_ICON   = "https://i.imgur.com/yYpP5qE.png";
//
//    public PrabhatAIChatPanel(Project project) {
//        this.project = project;
//        setLayout(new BorderLayout());
//        setBorder(new EmptyBorder(8, 8, 8, 8));
//
//        chatPane = new JEditorPane();
//        chatPane.setEditable(false);
//        chatPane.setEditorKit(new HTMLEditorKit());
//        chatPane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true);
//
//        chatPane.addHyperlinkListener(e -> {
//            if ("copy".equals(e.getDescription())) {
//                StringSelection sel = new StringSelection(
//                        e.getURL().toString().replace("copy://", "")
//                );
//                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(sel, null);
//            }
//        });
//
//        JScrollPane scrollPane = new JScrollPane(chatPane);
//        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
//        scrollPane.getVerticalScrollBar().setUnitIncrement(12);
//
//        inputArea = new JTextArea(3, 20);
//        inputArea.setLineWrap(true);
//        inputArea.setWrapStyleWord(true);
//        inputArea.setFont(new Font("JetBrains Mono", Font.PLAIN, 13));
//
//        inputArea.addKeyListener(new java.awt.event.KeyAdapter() {
//            public void keyPressed(java.awt.event.KeyEvent e) {
//                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER && !e.isShiftDown()) {
//                    e.consume();
//                    sendMessage();
//                }
//            }
//        });
//
//        JButton sendBtn = new JButton("➤");
//        sendBtn.addActionListener(e -> sendMessage());
//
//        JPanel inputPanel = new JPanel(new BorderLayout());
//        inputPanel.add(new JScrollPane(inputArea), BorderLayout.CENTER);
//        inputPanel.add(sendBtn, BorderLayout.EAST);
//
//        add(scrollPane, BorderLayout.CENTER);
//        add(inputPanel, BorderLayout.SOUTH);
//
//        render();
//    }
//
//    private void sendMessage() {
//        if (requestInFlight.get()) return;
//
//        String prompt = inputArea.getText().trim();
//        if (prompt.isEmpty()) return;
//
//        requestInFlight.set(true);
//        state = ChatState.THINKING;
//
//        appendUser(prompt);
//        inputArea.setText("");
//        render();
//
//        AiFacadeService.getInstance(project)
//                .requestCompletion(prompt)
//                .thenAccept(res -> SwingUtilities.invokeLater(() -> handleResponse(res)))
//                .exceptionally(ex -> {
//                    SwingUtilities.invokeLater(() -> {
//                        appendAI("<b>Error:</b> " + ex.getMessage());
//                        finishRequest();
//                    });
//                    return null;
//                });
//    }
//
//    private void handleResponse(AiResponse res) {
//        appendAI(MarkdownRenderer.render(res.getExplanation()));
//
//        if (res.getMultiFilePatch() != null && res.getMultiFilePatch().hasChanges())
//            DiffPreviewPanel.getInstance(project).setMultiFilePatch(res.getMultiFilePatch());
//
//        if (res.getPatchModel() != null && res.getPatchModel().hasChanges())
//            DiffPreviewPanel.getInstance(project).setPatchModel(res.getPatchModel());
//
//        finishRequest();
//    }
//
//    private void finishRequest() {
//        state = ChatState.IDLE;
//        requestInFlight.set(false);
//        render();
//    }
//
//    private void appendUser(String text) {
//        messages.append("""
//        <div class='row right'>
//          <div class='bubble user'>%s</div>
//          <img class='icon' src='%s'/>
//        </div>
//        """.formatted(escape(text), USER_ICON));
//    }
//
//    private void appendAI(String html) {
//        messages.append("""
//        <div class='row'>
//          <img class='icon' src='%s'/>
//          <div class='bubble ai'>
//            %s
//            <div class='copy'>
//              <a href='copy://%s'>Copy</a>
//            </div>
//          </div>
//        </div>
//        """.formatted(AI_ICON, html, escape(stripHtml(html))));
//    }
//
//    private void render() {
//        String thinking = state == ChatState.THINKING
//                ? "<div class='thinking'>🤖 Thinking...</div>"
//                : "";
//
//        chatPane.setText("""
//        <html>
//        <head>
//        <style>
//        body { background:#1e1e1e; color:#ddd; font-family:JetBrains Mono; padding:10px; }
//        .row { display:flex; gap:10px; margin:10px 0; }
//        .right { justify-content:flex-end; }
//        .bubble { padding:12px; border-radius:10px; max-width:80%%; word-wrap:break-word; }
//        .user { background:#2a3947; }
//        .ai { background:#232323; }
//        .icon { width:32px; height:32px; border-radius:50%%; }
//        pre, code { white-space:pre-wrap; word-break:break-word; }
//        .copy { text-align:right; font-size:11px; opacity:0.7; }
//        .thinking { font-style:italic; color:#8ab4f8; }
//        </style>
//        </head>
//        <body>
//        %s
//        %s
//        </body>
//        </html>
//        """.formatted(messages, thinking));
//
//        chatPane.setCaretPosition(chatPane.getDocument().getLength());
//    }
//
//    private static String escape(String s) {
//        return s.replace("&","&amp;")
//                .replace("<","&lt;")
//                .replace(">","&gt;")
//                .replace("\"","&quot;")
//                .replace("\n","<br>");
//    }
//
//    private static String stripHtml(String s) {
//        return s.replaceAll("<[^>]+>", "");
//    }
//}




package com.PrabhatDevLab.ui.chat;

import com.PrabhatDevLab.services.AiFacadeService;
import com.PrabhatDevLab.services.models.AiResponse;
import com.PrabhatDevLab.ui.DiffPreviewPanel;
import com.PrabhatDevLab.ui.MarkdownRenderer;
import com.intellij.openapi.project.Project;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.concurrent.atomic.AtomicBoolean;

public class PrabhatAIChatPanel extends JPanel {

    private final Project project;
    private final JEditorPane chatPane;
    private final JTextArea inputArea;
    private final JButton sendBtn;

    private final AtomicBoolean requestInFlight = new AtomicBoolean(false);
    private final StringBuilder messages = new StringBuilder();

    private static final String USER_ICON = "👤";
    private static final String AI_ICON = "🤖";

    public PrabhatAIChatPanel(Project project) {
        this.project = project;
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(8, 8, 8, 8));

        // ===== CHAT PANE =====
        chatPane = new JEditorPane();
        chatPane.setContentType("text/html");
        chatPane.setEditable(false);
        chatPane.setEditorKit(new HTMLEditorKit());
        chatPane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);

        // ===== COPY HANDLER (FIXED) =====
        chatPane.addHyperlinkListener(e -> {
            if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                String desc = e.getDescription();
                if (desc != null && desc.startsWith("copy://")) {
                    String textToCopy = desc.substring(7); // Remove "copy://"
                    StringSelection selection = new StringSelection(textToCopy);
                    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);

                    // Visual feedback
                    JOptionPane.showMessageDialog(
                            this,
                            "Code copied to clipboard!",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(chatPane);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // ===== INPUT AREA =====
        inputArea = new JTextArea(3, 20);
        inputArea.setLineWrap(true);
        inputArea.setWrapStyleWord(true);
        inputArea.setFont(new Font("JetBrains Mono", Font.PLAIN, 13));

        inputArea.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    if (!e.isShiftDown()) {
                        e.consume();
                        sendMessage();
                    }
                }
            }
        });

        // ===== SEND BUTTON =====
        sendBtn = new JButton("Send ➤");
        sendBtn.setFont(new Font("JetBrains Mono", Font.BOLD, 12));
        sendBtn.addActionListener(e -> sendMessage());

        JPanel inputPanel = new JPanel(new BorderLayout(5, 0));
        inputPanel.add(new JScrollPane(inputArea), BorderLayout.CENTER);
        inputPanel.add(sendBtn, BorderLayout.EAST);

        add(scrollPane, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);

        renderInitialState();
    }

    private void sendMessage() {
        // Prevent multiple requests
        if (requestInFlight.get()) {
            return;
        }

        String prompt = inputArea.getText().trim();
        if (prompt.isEmpty()) {
            return;
        }

        // Lock UI
        requestInFlight.set(true);
        sendBtn.setEnabled(false);
        inputArea.setEnabled(false);

        // Add user message
        appendUserMessage(prompt);
        inputArea.setText("");

        // Show thinking state BEFORE making request
        showThinkingState();

        // Make async request
        AiFacadeService.getInstance(project)
                .requestCompletion(prompt)
                .thenAccept(response -> SwingUtilities.invokeLater(() -> handleSuccess(response)))
                .exceptionally(ex -> {
                    SwingUtilities.invokeLater(() -> handleError(ex));
                    return null;
                });
    }

    private void handleSuccess(AiResponse response) {
        // CRITICAL: Remove thinking state FIRST
        removeThinkingState();

        // Append AI response
        String renderedMarkdown = MarkdownRenderer.render(response.getExplanation());
        appendAIMessage(renderedMarkdown);

        // Handle patches
        if (response.getMultiFilePatch() != null && response.getMultiFilePatch().hasChanges()) {
            DiffPreviewPanel.getInstance(project).setMultiFilePatch(response.getMultiFilePatch());
        }

        if (response.getPatchModel() != null && response.getPatchModel().hasChanges()) {
            DiffPreviewPanel.getInstance(project).setPatchModel(response.getPatchModel());
        }

        // Unlock UI
        unlockUI();
    }

    private void handleError(Throwable ex) {
        // Remove thinking state
        removeThinkingState();

        // Show error
        String errorMsg = "<div style='color:#ff6b6b; padding:10px; background:#2d1f1f; border-radius:8px;'>"
                + "<b>❌ Error:</b> " + escape(ex.getMessage()) + "</div>";
        appendAIMessage(errorMsg);

        // Unlock UI
        unlockUI();
    }

    private void unlockUI() {
        requestInFlight.set(false);
        sendBtn.setEnabled(true);
        inputArea.setEnabled(true);
        inputArea.requestFocus();
    }

    private void appendUserMessage(String text) {
        messages.append(String.format("""
        <div class='message-row user-row'>
          <div class='message-bubble user-bubble'>
            <div class='icon'>%s</div>
            <div class='content'>%s</div>
          </div>
        </div>
        """, USER_ICON, escape(text)));

        renderMessages();
    }

    private void appendAIMessage(String htmlContent) {
        // Extract code blocks for copy functionality
        String processedContent = addCopyButtons(htmlContent);

        messages.append(String.format("""
        <div class='message-row ai-row'>
          <div class='message-bubble ai-bubble'>
            <div class='icon'>%s</div>
            <div class='content'>%s</div>
          </div>
        </div>
        """, AI_ICON, processedContent));

        renderMessages();
    }

    private void showThinkingState() {
        String currentHtml = buildFullHtml(messages.toString(), true);
        chatPane.setText(currentHtml);
        scrollToBottom();
    }

    private void removeThinkingState() {
        // Just re-render without thinking indicator
        renderMessages();
    }

    private void renderMessages() {
        String html = buildFullHtml(messages.toString(), false);
        chatPane.setText(html);
        scrollToBottom();
    }

    private void renderInitialState() {
        messages.append("""
        <div class='message-row ai-row'>
          <div class='message-bubble ai-bubble'>
            <div class='icon'>🤖</div>
            <div class='content'>
              <h3>Welcome to PrabhatAI!</h3>
              <p>I'm your intelligent coding assistant. Ask me anything about your code!</p>
            </div>
          </div>
        </div>
        """);
        renderMessages();
    }

    private String buildFullHtml(String messagesHtml, boolean showThinking) {
        String thinkingIndicator = showThinking
                ? "<div class='thinking-indicator'><span class='dot'></span><span class='dot'></span><span class='dot'></span> Thinking...</div>"
                : "";

        return String.format("""
        <!DOCTYPE html>
        <html>
        <head>
        <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        
        body {
          background: #1e1e1e;
          color: #e0e0e0;
          font-family: 'Segoe UI', 'JetBrains Mono', monospace;
          padding: 16px;
          line-height: 1.6;
        }
        
        .message-row {
          display: flex;
          margin-bottom: 16px;
          width: 100%%;
          animation: slideIn 0.3s ease-out;
        }
        
        @keyframes slideIn {
          from { opacity: 0; transform: translateY(10px); }
          to { opacity: 1; transform: translateY(0); }
        }
        
        .user-row { justify-content: flex-end; }
        .ai-row { justify-content: flex-start; }
        
        .message-bubble {
          display: flex;
          gap: 12px;
          max-width: 85%%;
          padding: 14px 16px;
          border-radius: 12px;
          word-wrap: break-word;
          overflow-wrap: break-word;
        }
        
        .user-bubble {
          background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%);
          color: white;
          flex-direction: row-reverse;
        }
        
        .ai-bubble {
          background: #2d2d2d;
          border: 1px solid #3d3d3d;
        }
        
        .icon {
          font-size: 24px;
          flex-shrink: 0;
          width: 32px;
          height: 32px;
          display: flex;
          align-items: center;
          justify-content: center;
        }
        
        .content {
          flex: 1;
          min-width: 0;
          word-break: break-word;
        }
        
        /* Code blocks - FIXED */
        pre {
          background: #1a1a1a !important;
          border: 1px solid #404040;
          border-radius: 8px;
          padding: 16px;
          margin: 12px 0;
          overflow-x: auto;
          max-width: 100%%;
          position: relative;
        }
        
        code {
          font-family: 'JetBrains Mono', 'Consolas', monospace;
          font-size: 13px;
          color: #a9b7c6;
          white-space: pre;
          display: block;
          overflow-x: auto;
        }
        
        .copy-btn {
          position: absolute;
          top: 8px;
          right: 8px;
          background: #4CAF50;
          color: white;
          border: none;
          padding: 6px 12px;
          border-radius: 6px;
          cursor: pointer;
          font-size: 11px;
          font-weight: bold;
          text-decoration: none;
          display: inline-block;
        }
        
        .copy-btn:hover {
          background: #45a049;
        }
        
        /* Inline code */
        :not(pre) > code {
          background: #2d2d2d;
          color: #ff6b6b;
          padding: 2px 6px;
          border-radius: 4px;
          font-size: 12px;
          white-space: normal;
          display: inline;
        }
        
        /* Headers */
        h1, h2, h3, h4 {
          margin: 16px 0 12px 0;
          color: #8ab4f8;
        }
        
        h1 { font-size: 24px; }
        h2 { font-size: 20px; }
        h3 { font-size: 18px; }
        
        /* Lists */
        ul, ol {
          margin: 12px 0;
          padding-left: 24px;
        }
        
        li {
          margin: 6px 0;
        }
        
        /* Links */
        a {
          color: #8ab4f8;
          text-decoration: none;
        }
        
        a:hover {
          text-decoration: underline;
        }
        
        /* Paragraphs */
        p {
          margin: 10px 0;
        }
        
        /* Blockquotes */
        blockquote {
          border-left: 4px solid #667eea;
          padding-left: 16px;
          margin: 12px 0;
          color: #b0b0b0;
          font-style: italic;
        }
        
        /* Thinking indicator */
        .thinking-indicator {
          display: flex;
          align-items: center;
          gap: 8px;
          padding: 12px 16px;
          background: #2d2d2d;
          border-radius: 12px;
          color: #8ab4f8;
          font-style: italic;
          margin: 12px 0;
          animation: pulse 1.5s ease-in-out infinite;
        }
        
        @keyframes pulse {
          0%%, 100%% { opacity: 0.6; }
          50%% { opacity: 1; }
        }
        
        .dot {
          width: 8px;
          height: 8px;
          background: #8ab4f8;
          border-radius: 50%%;
          display: inline-block;
          animation: bounce 1.4s ease-in-out infinite;
        }
        
        .dot:nth-child(2) { animation-delay: 0.2s; }
        .dot:nth-child(3) { animation-delay: 0.4s; }
        
        @keyframes bounce {
          0%%, 80%%, 100%% { transform: translateY(0); }
          40%% { transform: translateY(-8px); }
        }
        
        /* Tables */
        table {
          border-collapse: collapse;
          width: 100%%;
          margin: 12px 0;
        }
        
        th, td {
          border: 1px solid #404040;
          padding: 8px 12px;
          text-align: left;
        }
        
        th {
          background: #2d2d2d;
          font-weight: bold;
        }
        </style>
        </head>
        <body>
        %s
        %s
        </body>
        </html>
        """, messagesHtml, thinkingIndicator);
    }

    private String addCopyButtons(String html) {
        // Find code blocks and add copy buttons
        return html.replaceAll(
                "<pre><code>(.*?)</code></pre>",
                "<pre><a href='copy://$1' class='copy-btn'>Copy</a><code>$1</code></pre>"
        );
    }

    private void scrollToBottom() {
        SwingUtilities.invokeLater(() -> {
            chatPane.setCaretPosition(chatPane.getDocument().getLength());
        });
    }

    private static String escape(String text) {
        if (text == null) return "";
        return text
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;")
                .replace("\n", "<br>");
    }
}