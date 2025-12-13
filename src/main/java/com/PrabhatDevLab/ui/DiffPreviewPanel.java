package com.PrabhatDevLab.ui;

import com.PrabhatDevLab.services.models.PatchModel;
import com.PrabhatDevLab.services.models.MultiFilePatchModel;
import com.intellij.openapi.project.Project;

import javax.swing.*;
import java.awt.*;

/**
 * DiffPreviewPanel:
 * Shows:
 *   - AI explanation
 *   - Patch text (raw)
 *   - Structured PatchModel (single-file)
 *   - MultiFilePatchModel (multi-file patches)
 */
public class DiffPreviewPanel {

    private static DiffPreviewPanel instance;

    private final Project project;

    // UI components
    private final JTextArea explanationArea = new JTextArea();
    private final JTextArea patchArea = new JTextArea();

    // Raw patch (OLD MVP)
    private String patchText;
    private String oldTextSample;
    private String newTextSample;

    // Structured patch (single file)
    private PatchModel patchModel;

    // NEW: Structured patch for MULTIPLE files
    private MultiFilePatchModel multiFilePatch;

    private JPanel mainPanel;


    // ============================================================
    //   Constructor + Singleton Accessor
    // ============================================================
    private DiffPreviewPanel(Project project) {
        this.project = project;
    }

    public static DiffPreviewPanel getInstance(Project project) {
        if (instance == null) {
            instance = new DiffPreviewPanel(project);
        }
        return instance;
    }


    // ============================================================
    //   OLD MVP METHODS (Kept for backward compatibility)
    // ============================================================
    public void setPatch(String patchText) {
        this.patchText = patchText;
        patchArea.setText(patchText);
    }

    public String getPatch() {
        return patchText;
    }

    public void setExplanation(String explanation) {
        explanationArea.setText(explanation);
    }

    public void setOldTextSample(String oldTextSample) {
        this.oldTextSample = oldTextSample;
    }

    public void setNewTextSample(String newTextSample) {
        this.newTextSample = newTextSample;
    }

    public String getOldTextSample() {
        return oldTextSample;
    }

    public String getNewTextSample() {
        return newTextSample;
    }


    // ============================================================
    //   NEW — STRUCTURED PATCH MODEL SUPPORT
    // ============================================================

    /** Store PatchModel for single-file PSI patching */
    public void setPatchModel(PatchModel model) {
        this.patchModel = model;
        this.multiFilePatch = null; // disable multi-file when single-file is used
        updateUIFromModel();
    }

    public PatchModel getPatchModel() {
        return this.patchModel;
    }


    /** Store MultiFilePatchModel for multi-file PSI patching */
    public void setMultiFilePatch(MultiFilePatchModel model) {
        this.multiFilePatch = model;
        this.patchModel = null; // disable single-file when multi-file is used
        updateUIFromModel();
    }

    public MultiFilePatchModel getMultiFilePatch() {
        return this.multiFilePatch;
    }


    // ============================================================
    //   UI Update Logic
    // ============================================================
    public void updateUIFromModel() {

        // ---------- MULTI-FILE PATCH (new system) ----------
        if (multiFilePatch != null && multiFilePatch.hasChanges()) {
            StringBuilder sb = new StringBuilder();
            sb.append("Multi-file AI patch detected:\n\n");

            multiFilePatch.files.forEach(fp -> {
                sb.append("• File: ").append(fp.filePath).append("\n");

                if (fp.newFileContent != null) {
                    sb.append("   - NEW FILE will be created.\n");
                }

                if (fp.patchModel != null && fp.patchModel.hasChanges()) {
                    sb.append("   - Contains structured PSI patch.\n");
                }

                sb.append("\n");
            });

            explanationArea.setText(sb.toString());
            return;
        }

        // ---------- SINGLE-FILE PATCH ----------
        if (patchModel != null && patchModel.hasChanges()) {
            explanationArea.setText("Single-file PSI patch detected.\n");
            return;
        }

        // ---------- FALLBACK ----------
        explanationArea.setText("No AI patch available.");
    }


    // ============================================================
    //   Main UI Panel
    // ============================================================
    public JPanel getMainPanel() {
        if (mainPanel == null) {
            mainPanel = new JPanel();
            mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

            mainPanel.add(new JLabel("AI Explanation:"));
            mainPanel.add(new JScrollPane(explanationArea));

            mainPanel.add(new JLabel("Patch Preview:"));
            mainPanel.add(new JScrollPane(patchArea));
        }
        return mainPanel;
    }
}
