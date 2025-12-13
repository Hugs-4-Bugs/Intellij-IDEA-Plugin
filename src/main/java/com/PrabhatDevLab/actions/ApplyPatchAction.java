package com.PrabhatDevLab.actions;

import com.PrabhatDevLab.services.MultiFilePatchApplier;
import com.PrabhatDevLab.services.models.MultiFilePatchModel;
import com.PrabhatDevLab.ui.DiffPreviewPanel;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;

/**
 * ApplyPatchAction:
 * Applies a full MultiFilePatchModel using the PSI engine.
 * Supports multiple files, new files, imports, methods, refactors.
 */
public class ApplyPatchAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {

        Project project = e.getProject();
        if (project == null) return;

        // Pull the multi-file patch from Tool Window
        MultiFilePatchModel patchBundle =
                DiffPreviewPanel.getInstance(project).getMultiFilePatch();

        if (patchBundle == null || !patchBundle.hasChanges()) {
            Messages.showInfoMessage(project,
                    "No structured AI patch found.",
                    "PrabhatAI");
            return;
        }

        // Apply changes to ALL files involved
        new MultiFilePatchApplier(project).apply(patchBundle);

        Messages.showInfoMessage(project,
                "AI Patch applied across multiple files using PSI engine.",
                "PrabhatAI");
    }
}



























//package com.PrabhatDevLab.actions;
//
//import com.PrabhatDevLab.ui.DiffPreviewPanel;
//import com.intellij.openapi.actionSystem.AnAction;
//import com.intellij.openapi.actionSystem.AnActionEvent;
//import com.intellij.openapi.actionSystem.CommonDataKeys;
//import com.intellij.openapi.editor.Document;
//import com.intellij.openapi.editor.Editor;
//import com.intellij.openapi.project.Project;
//import com.intellij.openapi.command.WriteCommandAction;
//import com.intellij.openapi.ui.Messages;
//
///**
// * ApplyPatchAction:
// * This action takes the patch prepared by PrabhatAI (from DiffPreviewPanel)
// * and applies it to the open editor file.
// *
// * This is a VERY BASIC MVP patch applier:
// * It doesn't parse full unified diff yet — it only replaces one line.
// * Later we will replace this with a real PSI-based patch engine.
// */
//public class ApplyPatchAction extends AnAction {
//
//    @Override
//    public void actionPerformed(AnActionEvent e) {
//
//        // IntelliJ project reference
//        Project project = e.getProject();
//        // Editor reference (where the user is editing code)
//        Editor editor = e.getData(CommonDataKeys.EDITOR);
//
//        // If the action was triggered without a project or editor, stop.
//        if (project == null || editor == null) {
//            return;
//        }
//
//        // Fetch the result panel storing our patch + explanation
//        DiffPreviewPanel panel = DiffPreviewPanel.getInstance(project);
//
//        // Raw patch string (unified diff style)
//        String patch = panel.getPatch();
//        if (patch == null || patch.isEmpty()) {
//            Messages.showInfoMessage(project, "No patch available", "PrabhatAI");
//            return;
//        }
//
//        // Extract only the first "- old line" and "+ new line"
//        // MVP simple patch extractor
//        String oldText = panel.getOldTextSample();
//        String newText = panel.getNewTextSample();
//
//        if (oldText == null || newText == null) {
//            Messages.showErrorDialog(project, "Patch samples missing", "PrabhatAI");
//            return;
//        }
//
//        // The editor's document represents the file text buffer.
//        Document doc = editor.getDocument();
//        String fullText = doc.getText();
//
//        // Check if the line to replace exists in the file.
//        if (!fullText.contains(oldText)) {
//            Messages.showErrorDialog(project,
//                    "Original text not found in file. Cannot apply patch.",
//                    "PrabhatAI");
//            return;
//        }
//
//        // WriteCommandAction ensures safe write operations in IntelliJ's threading model.
//        WriteCommandAction.runWriteCommandAction(project, () -> {
//            int index = fullText.indexOf(oldText);
//            doc.replaceString(index, index + oldText.length(), newText);
//        });
//
//        Messages.showInfoMessage(project, "Patch applied (MVP). Check file.", "PrabhatAI");
//    }
//}
