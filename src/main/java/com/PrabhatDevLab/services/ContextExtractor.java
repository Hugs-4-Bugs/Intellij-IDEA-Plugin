package com.PrabhatDevLab.services;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.Document;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Extracts natural-language commands from comments and whole-file context.
 */
public class ContextExtractor {

    private static final Pattern PRABHAT_AI_COMMENT =
            Pattern.compile("//\\s*@PrabhatAI:(.*)");

    public static String getPromptAtCaretOrSelection(Editor editor) {
        Document doc = editor.getDocument();
        String selected = editor.getSelectionModel().getSelectedText();
        int caret = editor.getCaretModel().getOffset();

        if (selected != null && !selected.isBlank()) {
            return selected;
        }

        String text = doc.getText();
        Matcher m = PRABHAT_AI_COMMENT.matcher(text);

        StringBuilder sb = new StringBuilder();

        while (m.find()) {
            sb.append(m.group(1).trim()).append("\n");
        }

        if (sb.length() > 0) {
            return sb.toString();
        }

        // fallback: use whole file content with caret context
        PsiFile psi = PsiDocumentManager.getInstance(editor.getProject())
                .getPsiFile(doc);

        return "Analyze and improve: \n\n" + (psi != null ? psi.getText() : text);
    }
}
