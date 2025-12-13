package com.PrabhatDevLab.settings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Stores API keys, provider toggles, and provider order for PrabhatAI.
 * IntelliJ serializes this state into: <config-dir>/PrabhatAISettings.xml
 */
@State(
        name = "PrabhatAISettingsState",
        storages = @Storage("PrabhatAISettings.xml")
)
public class PrabhatAISettingsState implements PersistentStateComponent<PrabhatAISettingsState> {

    // ==========================
    // API KEYS
    // ==========================
    public String openAiKey = "";
    public String geminiKey = "";
    public String claudeKey = "";

    // ==========================
    // PROVIDER ENABLE/DISABLE FLAGS
    // ==========================
    public boolean enableOpenAI = true;
    public boolean enableGemini = true;
    public boolean enableClaude = true;
    public boolean enableMock = true;

    // ==========================
    // PROVIDER PRIORITY ORDER
    // ==========================
    public String providerOrder = "mock,gemini,claude,openai";

    // ==========================
    // Required: save state
    // ==========================
    @Override
    public @Nullable PrabhatAISettingsState getState() {
        return this;
    }

    // ==========================
    // Required: load state
    // ==========================
    @Override
    public void loadState(@NotNull PrabhatAISettingsState state) {

        this.openAiKey = safe(state.openAiKey);
        this.geminiKey = safe(state.geminiKey);
        this.claudeKey = safe(state.claudeKey);

        this.enableOpenAI = state.enableOpenAI;
        this.enableGemini = state.enableGemini;
        this.enableClaude = state.enableClaude;
        this.enableMock = state.enableMock;

        this.providerOrder =
                (state.providerOrder == null || state.providerOrder.isBlank())
                        ? "mock,gemini,claude,openai"
                        : state.providerOrder;
    }

    private String safe(String s) {
        return (s == null ? "" : s);
    }

    // ==========================
    // Required: global accessor
    // ==========================
    public static PrabhatAISettingsState getInstance() {
        return ApplicationManager.getApplication().getService(PrabhatAISettingsState.class);
    }
}



