//package com.PrabhatDevLab.services.models;
//
//import java.util.List;
//
///**
// * AiResponse:
// * Holds:
// *  - Explanation text
// *  - Raw diff text (optional)
// *  - Single-file PSI PatchModel
// *  - Multi-file patch model
// *  - JUnit test generation metadata
// */
//public class AiResponse {
//
//    // =========================
//    // BASIC (OLD MVP)
//    // =========================
//    private String explanation;
//    private String patch;
//    private PatchModel patchModel;
//
//    // =========================
//    // NEW: MULTI-FILE PATCH SUPPORT
//    // =========================
//    private MultiFilePatchModel multiFilePatch;
//
//    // =========================
//    // NEW: TEST GENERATION SUPPORT
//    // =========================
//    private boolean generateTests = false;
//    private String testPackage;
//    private String testClassName;
//    private List<String> methods;  // test methods to generate
//
//    // ============================================================
//    // GETTERS + SETTERS
//    // ============================================================
//    public String getExplanation() {
//        return explanation;
//    }
//
//    public void setExplanation(String explanation) {
//        this.explanation = explanation;
//    }
//
//    public String getPatch() {
//        return patch;
//    }
//
//    public void setPatch(String patch) {
//        this.patch = patch;
//    }
//
//    public PatchModel getPatchModel() {
//        return patchModel;
//    }
//
//    public void setPatchModel(PatchModel patchModel) {
//        this.patchModel = patchModel;
//    }
//
//
//    // ============================================================
//    // MULTI-FILE PATCH MODEL
//    // ============================================================
//    public MultiFilePatchModel getMultiFilePatch() {
//        return multiFilePatch;
//    }
//
//    public void setMultiFilePatch(MultiFilePatchModel multiFilePatch) {
//        this.multiFilePatch = multiFilePatch;
//    }
//
//
//    // ============================================================
//    // TEST GENERATION FLAGS
//    // ============================================================
//    public boolean shouldGenerateTests() {
//        return generateTests;
//    }
//
//    public void setGenerateTests(boolean generateTests) {
//        this.generateTests = generateTests;
//    }
//
//    public String getTestPackage() {
//        return testPackage;
//    }
//
//    public void setTestPackage(String testPackage) {
//        this.testPackage = testPackage;
//    }
//
//    public String getTestClassName() {
//        return testClassName;
//    }
//
//    public void setTestClassName(String testClassName) {
//        this.testClassName = testClassName;
//    }
//
//    public List<String> getMethods() {
//        return methods;
//    }
//
//    public void setMethods(List<String> methods) {
//        this.methods = methods;
//    }
//
//
//    // ============================================================
//    // OPTIONAL HELPER (INLINE SUGGESTION)
//    // ============================================================
//    public String getNewTextSample() {
//        if (patchModel != null &&
//                !patchModel.methodReplacements.isEmpty()) {
//            return patchModel.methodReplacements.get(0).newBody;
//        }
//        return "";
//    }
//
//
//    // Constructor
//    public AiResponse() {
//    }
//
//    public AiResponse(String explanation) {
//        this.explanation = explanation;
//    }
//
//}




package com.PrabhatDevLab.services.models;

import java.util.List;

public class AiResponse {

    private String explanation;
    private String patch;
    private PatchModel patchModel;
    private MultiFilePatchModel multiFilePatch;

    private boolean generateTests = false;
    private String testPackage;
    private String testClassName;
    private List<String> methods;

    // ===== FIXED CONSTRUCTORS =====
    public AiResponse() { }

    public AiResponse(String s) {
        this.explanation = s;
        this.patch = s;
    }

    // ===== GETTERS / SETTERS =====
    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }

    public String getPatch() { return patch; }
    public void setPatch(String patch) { this.patch = patch; }

    public PatchModel getPatchModel() { return patchModel; }
    public void setPatchModel(PatchModel patchModel) { this.patchModel = patchModel; }

    public MultiFilePatchModel getMultiFilePatch() { return multiFilePatch; }
    public void setMultiFilePatch(MultiFilePatchModel multiFilePatch) { this.multiFilePatch = multiFilePatch; }

    public boolean shouldGenerateTests() { return generateTests; }
    public void setGenerateTests(boolean generateTests) { this.generateTests = generateTests; }

    public String getTestPackage() { return testPackage; }
    public void setTestPackage(String testPackage) { this.testPackage = testPackage; }

    public String getTestClassName() { return testClassName; }
    public void setTestClassName(String testClassName) { this.testClassName = testClassName; }

    public List<String> getMethods() { return methods; }
    public void setMethods(List<String> methods) { this.methods = methods; }
}
