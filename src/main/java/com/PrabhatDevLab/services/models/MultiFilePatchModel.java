package com.PrabhatDevLab.services.models;

import java.util.ArrayList;
import java.util.List;

/**
 * MultiFilePatchModel:
 * AI can modify multiple files at once.
 *
 * Used for:
 *  - Controller + Service paired edits
 *  - Repository changes
 *  - New file creation
 *  - DTO changes
 */
public class MultiFilePatchModel {

    public static class FilePatch {
        public String filePath;        // Absolute or relative path
        public PatchModel patchModel;  // Structured patch for that file
        public String newFileContent;  // For creating NEW files (optional)
    }

    public List<FilePatch> files = new ArrayList<>();

    public boolean hasChanges() {
        return !files.isEmpty();
    }
}
