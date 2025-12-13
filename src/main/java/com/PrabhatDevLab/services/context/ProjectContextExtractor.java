package com.PrabhatDevLab.services.context;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

public class ProjectContextExtractor {

    public static String extract(Project project) {
        if (project == null) return "No project loaded.";
        StringBuilder sb = new StringBuilder();

        sb.append("Project: ").append(project.getName()).append("\n\n");
        sb.append("=== Project Structure ===\n");

        VirtualFile base = project.getBaseDir();
        if (base != null) {
            scan(base, sb, 0);
        }

        return sb.toString();
    }

    private static void scan(VirtualFile file, StringBuilder sb, int depth) {
        if (file.getName().startsWith(".")) return;

        sb.append("  ".repeat(Math.max(0, depth)))
                .append(file.isDirectory() ? "[DIR] " : "- ")
                .append(file.getName()).append("\n");

        if (file.isDirectory()) {
            for (VirtualFile child : file.getChildren()) {
                scan(child, sb, depth + 1);
            }
        }
    }
}
