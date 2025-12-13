package com.PrabhatDevLab.listeners;

import com.PrabhatDevLab.services.AiFacadeService;
import com.PrabhatDevLab.services.ContextExtractor;
import com.PrabhatDevLab.services.models.AiResponse;
import com.PrabhatDevLab.ui.InlineSuggestionRenderer;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;

import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.editor.event.EditorFactoryEvent;
import com.intellij.openapi.editor.event.EditorFactoryListener;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Listens to editor typing events.
 * Detects commands like:
 *      // @PrabhatAI: refactor signup logic
 *
 * After 600ms pause, asks AI for inline suggestion.
 */
public class PrabhatAIEditorListener implements EditorFactoryListener {

    private final Timer timer = new Timer();

    @Override
    public void editorCreated(@NotNull EditorFactoryEvent event) {

        Editor editor = event.getEditor();
        Project project = editor.getProject();
        if (project == null) return;

        Document doc = editor.getDocument();
        InlineSuggestionRenderer renderer = new InlineSuggestionRenderer(project, editor);

        doc.addDocumentListener(new DocumentListener() {
            private TimerTask pendingTask;

            @Override
            public void documentChanged(@NotNull DocumentEvent event) {

                // cancel old task
                if (pendingTask != null) pendingTask.cancel();

                // schedule new one (600ms debounce)
                pendingTask = new TimerTask() {
                    @Override
                    public void run() {

                        ApplicationManager.getApplication().invokeLater(() -> {

                            String trigger = extractPrabhatCommandLine(editor);
                            if (trigger == null || trigger.isBlank()) return;

                            AiFacadeService ai = AiFacadeService.getInstance(project);

                            ai.requestCompletion(trigger)
                                    .thenAccept((AiResponse resp) -> {

                                        String suggestion = resp.getExplanation();
                                        if (suggestion == null || suggestion.isBlank()) return;

                                        renderer.showSuggestion(suggestion);
                                    })
                                    .exceptionally(ex -> {
                                        ex.printStackTrace();
                                        return null;
                                    });

                        });
                    }
                };

                timer.schedule(pendingTask, 600);
            }
        });
    }

    /**
     * Extracts ONLY the nearest @PrabhatAI: instruction line.
     */
    private String extractPrabhatCommandLine(Editor editor) {
        Document doc = editor.getDocument();
        int caret = editor.getCaretModel().getOffset();

        String[] lines = doc.getText().split("\n");

        // find the line where caret is located
        int currentLine = doc.getLineNumber(caret);

        for (int i = currentLine; i >= 0; i--) {
            String line = lines[i].trim();
            if (line.startsWith("// @PrabhatAI:")) {
                return line.replace("// @PrabhatAI:", "").trim();
            }
        }

        return null;
    }
}
