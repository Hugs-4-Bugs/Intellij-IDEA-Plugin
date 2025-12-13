package com.PrabhatDevLab.services;

import com.PrabhatDevLab.services.models.PatchModel;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleManager;

/**
 * PsiPatchApplier:
 * Applies structured PatchModel changes to Java files using IntelliJ PSI.
 *
 * THIS IS THE REAL THING.
 */
public class PsiPatchApplier {

    private final Project project;

    public PsiPatchApplier(Project project) {
        this.project = project;
    }

    /**
     * Main entry point.
     */
    public void applyPatch(PsiFile psiFile, PatchModel patch) {

        if (!(psiFile instanceof PsiJavaFile)) {
            return;
        }

        PsiJavaFile javaFile = (PsiJavaFile) psiFile;

        WriteCommandAction.runWriteCommandAction(project, () -> {

            if (!patch.importsToAdd.isEmpty()) {
                addImports(javaFile, patch);
            }

            if (!patch.methodReplacements.isEmpty()) {
                replaceMethods(javaFile, patch);
            }

            if (!patch.newMethods.isEmpty()) {
                insertMethods(javaFile, patch);
            }

            // Auto-format after all PSI edits
            CodeStyleManager.getInstance(project).reformat(javaFile);

        });
    }

    // ============================================================
    // 1) Add Imports
    // ============================================================
    private void addImports(PsiJavaFile file, PatchModel patch) {

        PsiImportList importList = file.getImportList();
        if (importList == null) return;

        PsiElementFactory factory = JavaPsiFacade.getInstance(project).getElementFactory();

        for (String imp : patch.importsToAdd) {

            // Create correct single import statement
            PsiImportStatement stmt = factory.createImportStatementOnDemand(imp);

            // Avoid duplicates
            if (importList.findSingleClassImportStatement(imp) == null) {
                importList.add(stmt);
            }
        }
    }

    // ============================================================
    // 2) Replace Method Bodies
    // ============================================================
    private void replaceMethods(PsiJavaFile javaFile, PatchModel patch) {

        PsiElementFactory factory = JavaPsiFacade.getInstance(project).getElementFactory();

        javaFile.accept(new JavaRecursiveElementVisitor() {
            @Override
            public void visitMethod(PsiMethod method) {
                super.visitMethod(method);

                for (PatchModel.MethodBodyPatch mb : patch.methodReplacements) {

                    if (!method.getName().equals(mb.methodName)) continue;

                    PsiCodeBlock oldBody = method.getBody();
                    if (oldBody == null) continue;

                    // Wrap inside braces automatically
                    String fixedBody = "{\n" + mb.newBody + "\n}";

                    PsiCodeBlock newBody = factory.createCodeBlockFromText(fixedBody, method);
                    oldBody.replace(newBody);
                }
            }
        });
    }

    // ============================================================
    // 3) Insert New Methods
    // ============================================================
    private void insertMethods(PsiJavaFile javaFile, PatchModel patch) {

        PsiClass[] classes = javaFile.getClasses();
        if (classes.length == 0) return;

        PsiClass clazz = classes[0];
        PsiElementFactory factory = JavaPsiFacade.getInstance(project).getElementFactory();

        for (PatchModel.MethodInsertPatch mp : patch.newMethods) {

            PsiMethod newMethod = factory.createMethodFromText(mp.methodCode, clazz);
            clazz.add(newMethod);
        }
    }
}
