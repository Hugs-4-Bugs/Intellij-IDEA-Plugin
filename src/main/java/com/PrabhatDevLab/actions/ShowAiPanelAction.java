package com.PrabhatDevLab.actions;

import com.PrabhatDevLab.services.AiFacadeService;
import com.PrabhatDevLab.services.ContextExtractor;
import com.PrabhatDevLab.services.tests.JUnitTestGenerator;
import com.PrabhatDevLab.services.models.MultiFilePatchModel;
import com.PrabhatDevLab.services.models.AiResponse;
import com.PrabhatDevLab.ui.DiffPreviewPanel;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;

import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;

import javax.swing.*;
import java.util.concurrent.CompletableFuture;

/**
 * ShowAiPanelAction:
 * Triggered when user clicks "Run PrabhatAI".
 *
 * Steps:
 * 1. Collect user prompt.
 * 2. Send prompt to AI.
 * 3. Display multi-file or single-file AI patch.
 * 4. If AI requested test generation → create MultiFilePatchModel for tests.
 */
public class ShowAiPanelAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {

        Project project = e.getProject();
        Editor editor = e.getData(CommonDataKeys.EDITOR);

        if (project == null || editor == null) return;

        // STEP 1 — Extract user prompt from code/comment
        String userPrompt = ContextExtractor.getPromptAtCaretOrSelection(editor);

        if (userPrompt == null || userPrompt.isBlank()) {
            Messages.showInfoMessage(project,
                    "No text selected or comment instruction detected.",
                    "PrabhatAI");
            return;
        }

        // STEP 2 — Async AI call
        AiFacadeService ai = AiFacadeService.getInstance(project);
        CompletableFuture<AiResponse> future = ai.requestCompletion(userPrompt);

        future.thenAccept(aiResponse -> SwingUtilities.invokeLater(() -> {

            try {
                // STEP 3 — Open Tool Window
                ToolWindow toolWindow =
                        ToolWindowManager.getInstance(project).getToolWindow("PrabhatAI");

                if (toolWindow != null) {
                    toolWindow.activate(null);
                }

                DiffPreviewPanel panel = DiffPreviewPanel.getInstance(project);

                // === CASE A: AI returned multi-file patches ===
                if (aiResponse.getMultiFilePatch() != null &&
                        aiResponse.getMultiFilePatch().hasChanges()) {

                    panel.setMultiFilePatch(aiResponse.getMultiFilePatch());
                    panel.setExplanation("AI generated multi-file patch.");
                    panel.updateUIFromModel();
                    return;
                }

                // === CASE B: AI wants JUnit test generation ===
                if (aiResponse.shouldGenerateTests()) {

                    JUnitTestGenerator gen = new JUnitTestGenerator();

                    MultiFilePatchModel tests = gen.generateTest(
                            aiResponse.getTestPackage(),
                            aiResponse.getTestClassName(),
                            aiResponse.getMethods()
                    );

                    panel.setMultiFilePatch(tests);
                    panel.setExplanation("AI-generated JUnit test cases.");
                    panel.updateUIFromModel();
                    return;
                }

                // === CASE C: Single-file patch (MVP) ===
                panel.setPatch(aiResponse.getPatch());
                panel.setExplanation(aiResponse.getExplanation());
                panel.updateUIFromModel();

            } catch (Exception ex) {
                Messages.showErrorDialog(project,
                        "Failed to update panel: " + ex.getMessage(),
                        "PrabhatAI");
            }

        })).exceptionally(ex -> {
            Messages.showErrorDialog(project,
                    "AI Error: " + ex.getMessage(),
                    "PrabhatAI");
            return null;
        });
    }
}
