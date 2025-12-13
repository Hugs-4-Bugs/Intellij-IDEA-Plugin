package com.PrabhatDevLab.services.patch;

import com.PrabhatDevLab.services.models.PatchModel;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleManager;
import org.jetbrains.annotations.NotNull;

public class PsiPatchApplier {

    private final Project project;

    public PsiPatchApplier(Project project) {
        this.project = project;
    }

    public void apply(@NotNull PsiFile file, @NotNull PatchModel patch) {

        WriteCommandAction.runWriteCommandAction(project, () -> {

            PsiElementFactory factory = JavaPsiFacade.getElementFactory(project);

            // 1. Add imports
            if (!patch.importsToAdd.isEmpty() && file instanceof PsiJavaFile javaFile) {
                PsiImportList importList = javaFile.getImportList();

                for (String imp : patch.importsToAdd) {
                    PsiImportStatement stmt = factory.createImportStatementOnDemand(imp);
                    importList.add(stmt);
                }
            }

            // 2. Replace method bodies
            for (PatchModel.MethodBodyPatch m : patch.methodReplacements) {
                replaceMethodBody(file, m.methodName, m.newBody);
            }

            // 3. Insert new methods
            for (PatchModel.MethodInsertPatch m : patch.newMethods) {
                insertMethod(file, m.methodCode);
            }

            // 4. Reformat entire file
            CodeStyleManager.getInstance(project).reformat(file);
        });
    }

    private void replaceMethodBody(PsiFile file, String methodName, String newBody) {

        PsiMethod[] methods = ((PsiJavaFile) file)
                .getClasses()[0]
                .findMethodsByName(methodName, false);

        if (methods.length == 0) return;

        PsiMethod method = methods[0];

        PsiElementFactory factory = JavaPsiFacade.getElementFactory(project);
        PsiCodeBlock newBlock = factory.createCodeBlockFromText("{ " + newBody + " }", method);

        method.getBody().replace(newBlock);
    }

    private void insertMethod(PsiFile file, String methodCode) {

        PsiClass cls = ((PsiJavaFile) file).getClasses()[0];

        PsiElementFactory factory = JavaPsiFacade.getElementFactory(project);
        PsiMethod newMethod = factory.createMethodFromText(methodCode, cls);

        cls.add(newMethod);
    }
}
