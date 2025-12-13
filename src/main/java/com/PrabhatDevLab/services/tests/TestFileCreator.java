package com.PrabhatDevLab.services.tests;

import com.PrabhatDevLab.services.models.MultiFilePatchModel;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.JavaDirectoryService;
import com.intellij.psi.PsiManager;

import java.nio.charset.StandardCharsets;

/**
 * Writes new test files into src/test/java via PSI.
 */
public class TestFileCreator {

    private final Project project;

    public TestFileCreator(Project project) {
        this.project = project;
    }

    public void createTestFile(MultiFilePatchModel.FilePatch fp) {

        WriteCommandAction.runWriteCommandAction(project, () -> {

            try {
                String fullPath = fp.filePath;

                VirtualFile base = project.getBaseDir();
                VirtualFile testFile = base.findFileByRelativePath(fullPath);

                if (testFile == null) {
                    // Create directory structure
                    testFile = createDirectories(fullPath);
                }

                if (testFile != null) {
                    testFile.setBinaryContent(fp.newFileContent.getBytes(StandardCharsets.UTF_8));
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    // Create missing folders (src/test/java/com/app/service/)
    private VirtualFile createDirectories(String filePath) throws Exception {

        VirtualFile base = project.getBaseDir();
        String[] segments = filePath.split("/");

        VirtualFile current = base;

        for (int i = 0; i < segments.length - 1; i++) {
            VirtualFile child = current.findChild(segments[i]);
            if (child == null) {
                child = current.createChildDirectory(this, segments[i]);
            }
            current = child;
        }

        return current.createChildData(this, segments[segments.length - 1]);
    }
}
