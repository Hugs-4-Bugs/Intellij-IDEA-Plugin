package com.PrabhatDevLab.services.models;

import java.util.ArrayList;
import java.util.List;

/**
 * PatchModel:
 * Defines structured AST-level operations for patching Java code.
 *
 * This is WAY safer than text replace.
 */
public class PatchModel {

    // -----------------------------------------
    // Import Additions
    // -----------------------------------------
    public List<String> importsToAdd = new ArrayList<>();

    // -----------------------------------------
    // Method Body Replacements
    // -----------------------------------------
    public static class MethodBodyPatch {
        public String methodName;  // Method to replace
        public String newBody;     // New body (no braces needed)
    }
    public List<MethodBodyPatch> methodReplacements = new ArrayList<>();

    // -----------------------------------------
    // New Methods to Insert
    // -----------------------------------------
    public static class MethodInsertPatch {
        public String methodCode;  // Full method code (with signature + body)
    }
    public List<MethodInsertPatch> newMethods = new ArrayList<>();

    // -----------------------------------------
    // Check if there is any change
    // -----------------------------------------
    public boolean hasChanges() {
        return !importsToAdd.isEmpty()
                || !methodReplacements.isEmpty()
                || !newMethods.isEmpty();
    }
}
