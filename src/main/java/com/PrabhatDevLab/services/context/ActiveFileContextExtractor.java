package com.PrabhatDevLab.services.context;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import java.nio.charset.StandardCharsets;

public class ActiveFileContextExtractor {

    public static String extract(Project project) {
        FileEditorManager manager = FileEditorManager.getInstance(project);
        VirtualFile[] files = manager.getSelectedFiles();

        if (files.length == 0) {
            return "No active file open.";
        }

        VirtualFile file = files[0];
        String content = "";

        try {
            content = new String(file.contentsToByteArray(), StandardCharsets.UTF_8);
        } catch (Exception ignored) {}

        return "=== Active File ===\n" +
                "Name: " + file.getName() + "\n" +
                "Path: " + file.getPath() + "\n\n" +
                "=== File Content Start ===\n" +
                content + "\n=== File Content End ===\n";
    }
}
