package com.PrabhatDevLab.services.tests;

import com.PrabhatDevLab.services.models.MultiFilePatchModel;
import com.PrabhatDevLab.services.models.PatchModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Converts AI instructions into a MultiFilePatchModel
 * that creates a corresponding JUnit test file.
 *
 * The AI sends:
 *  {
 *    "className": "UserService",
 *    "methods": ["signup", "login"],
 *    "package": "com.app.service"
 *  }
 *
 * This builder creates a NEW file:
 *  src/test/java/com/app/service/UserServiceTest.java
 */
public class JUnitTestGenerator {

    public MultiFilePatchModel generateTest(
            String packageName,
            String className,
            List<String> methods) {

        MultiFilePatchModel multi = new MultiFilePatchModel();

        // Build test class content
        String fileContent = buildJUnitFile(packageName, className, methods);

        MultiFilePatchModel.FilePatch fp = new MultiFilePatchModel.FilePatch();
        fp.filePath = "src/test/java/" + packageName.replace('.', '/') + "/" + className + "Test.java";
        fp.newFileContent = fileContent;

        multi.files.add(fp);

        return multi;
    }

    private String buildJUnitFile(String pkg, String className, List<String> methods) {

        StringBuilder sb = new StringBuilder();

        sb.append("package ").append(pkg).append(";\n\n");

        sb.append("import org.junit.jupiter.api.Test;\n");
        sb.append("import static org.junit.jupiter.api.Assertions.*;\n\n");

        sb.append("public class ").append(className).append("Test {\n\n");

        for (String m : methods) {
            sb.append("    @Test\n");
            sb.append("    void ").append(m).append("Test() {\n");
            sb.append("        // TODO: Implement test logic for ").append(m).append("\n");
            sb.append("        // Arrange\n");
            sb.append("        // Act\n");
            sb.append("        // Assert\n");
            sb.append("    }\n\n");
        }

        sb.append("}\n");

        return sb.toString();
    }
}
