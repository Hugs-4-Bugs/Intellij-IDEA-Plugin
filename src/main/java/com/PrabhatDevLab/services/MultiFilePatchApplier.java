package com.PrabhatDevLab.services;

import com.PrabhatDevLab.services.models.MultiFilePatchModel;
import com.PrabhatDevLab.services.models.PatchModel;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;

import java.nio.charset.StandardCharsets;

/**
 * MultiFilePatchApplier:
 * Applies structured patches to multiple files.
 */
public class MultiFilePatchApplier {

    private final Project project;

    public MultiFilePatchApplier(Project project) {
        this.project = project;
    }

    /**
     * Main entry: apply patch to all files AI requested.
     */
    public void apply(MultiFilePatchModel multi) {

        WriteCommandAction.runWriteCommandAction(project, () -> {

            PsiManager psiManager = PsiManager.getInstance(project);

            for (MultiFilePatchModel.FilePatch fp : multi.files) {

                VirtualFile vf = resolveFile(fp.filePath);
                if (vf == null) continue;

                PsiFile psiFile = psiManager.findFile(vf);
                if (psiFile == null) continue;

                // =============================
                // 1) NEW FILE CREATION
                // =============================
                if (fp.newFileContent != null && !fp.newFileContent.isBlank()) {
                    try {
                        vf.setBinaryContent(fp.newFileContent.getBytes(StandardCharsets.UTF_8));
                    } catch (Exception ignored) {}
                    continue;
                }

                // =============================
                // 2) APPLY STRUCTURED PATCH
                // =============================
                if (fp.patchModel != null && fp.patchModel.hasChanges()) {
                    new PsiPatchApplier(project).applyPatch(psiFile, fp.patchModel);
                }
            }
        });
    }

    /**
     * Resolves file paths:
     * - Absolute paths → returned directly
     * - Relative paths → resolved against project base dir
     */
    private VirtualFile resolveFile(String path) {

        VirtualFile file = project.getBaseDir().findFileByRelativePath(path);

        if (file != null) return file;

        // Try absolute path fallback:
        return com.intellij.openapi.vfs.LocalFileSystem.getInstance().findFileByPath(path);
    }
}
