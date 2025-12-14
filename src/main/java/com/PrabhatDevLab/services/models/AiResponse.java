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
