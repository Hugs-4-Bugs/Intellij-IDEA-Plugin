package com.PrabhatDevLab.ui;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.Inlay;
import com.intellij.openapi.editor.InlayModel;
import com.intellij.openapi.editor.EditorCustomElementRenderer;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

/**
 * InlineSuggestionRenderer - handles inline ghost text suggestions,
 * TAB-to-accept logic, and rendering.
 */
public class InlineSuggestionRenderer {

    private final Project project;
    private final Editor editor;

    private Inlay<?> currentInlay;
    private String lastSuggestion = null;  // <--- FIX: store suggestion here

    public InlineSuggestionRenderer(Project project, Editor editor) {
        this.project = project;
        this.editor = editor;
    }

    /**
     * Show ghost inline suggestion at caret.
     */
    public void showSuggestion(@NotNull String suggestionText) {
        clearSuggestion();
        this.lastSuggestion = suggestionText;   // <--- FIX: store it

        int offset = editor.getCaretModel().getOffset();
        InlayModel inlayModel = editor.getInlayModel();

        currentInlay = inlayModel.addInlineElement(
                offset,
                new SimpleSuggestionRenderer(suggestionText)
        );
    }

    public void updateSuggestion(@NotNull String text) {
        this.lastSuggestion = text;
        if (currentInlay != null && currentInlay.isValid()) {
            currentInlay.update();  // Forces IntelliJ to repaint with updated text
        }
    }


    /**
     * Clear the current inline ghost text.
     */
    public void clearSuggestion() {
        if (currentInlay != null && currentInlay.isValid()) {
            currentInlay.dispose();
        }
        currentInlay = null;
        lastSuggestion = null;
    }

    /**
     * Accepts the ghost text and inserts it into the file.
     * Called when user presses TAB.
     */
    public void applySuggestion() {
        if (!hasSuggestion() || lastSuggestion == null) return;

        editor.getDocument().insertString(
                editor.getCaretModel().getOffset(),
                lastSuggestion
        );

        clearSuggestion();
    }

    public boolean hasSuggestion() {
        return currentInlay != null && currentInlay.isValid();
    }

    /**
     * Renderer that draws faint gray inline text.
     */
    private static class SimpleSuggestionRenderer implements EditorCustomElementRenderer {

        private final String suggestion;

        public SimpleSuggestionRenderer(String suggestion) {
            this.suggestion = suggestion;
        }

        @Override
        public void paint(@NotNull Inlay inlay,
                          @NotNull Graphics g,
                          @NotNull Rectangle targetRegion,
                          @NotNull TextAttributes textAttributes) {

            Editor editor = inlay.getEditor();
            Font font = editor.getContentComponent().getFont();
            FontMetrics fm = editor.getContentComponent().getFontMetrics(font);

            g.setColor(Color.GRAY);
            g.setFont(font);
            int y = targetRegion.y + fm.getAscent();
            String text = ((InlineSuggestionRenderer) inlay.getRenderer()).lastSuggestion;
            g.drawString(text, targetRegion.x, y);
        }

        @Override
        public int calcWidthInPixels(@NotNull Inlay inlay) {
            Editor editor = inlay.getEditor();
            Font font = editor.getContentComponent().getFont();
            FontMetrics fm = editor.getContentComponent().getFontMetrics(font);
            return fm.stringWidth(suggestion);
        }
    }
}
