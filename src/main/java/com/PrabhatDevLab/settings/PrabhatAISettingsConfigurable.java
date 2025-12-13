package com.PrabhatDevLab.settings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PrabhatAISettingsConfigurable implements Configurable {

    private JPanel mainPanel;

    private JTextField openAiKeyField;
    private JTextField geminiKeyField;
    private JTextField claudeKeyField;

    private JCheckBox enableMockCheck;
    private JCheckBox enableGeminiCheck;
    private JCheckBox enableClaudeCheck;
    private JCheckBox enableOpenAICheck;

    private JList<String> providerList;
    private DefaultListModel<String> providerListModel;
    private JButton upButton;
    private JButton downButton;

    // ❗ DO NOT MAKE FINAL — IntelliJ can initialize late
    private PrabhatAISettingsState state;

    public PrabhatAISettingsConfigurable() {
        // safe initialization — returns non-null when services are ready
        this.state = ApplicationManager.getApplication().getService(PrabhatAISettingsState.class);
    }

    @Override
    public @Nls(capitalization = Nls.Capitalization.Title) String getDisplayName() {
        return "PrabhatAI";
    }

    @Override
    public @Nullable JComponent createComponent() {
        if (mainPanel == null) {
            buildUI();
        }

        // Make sure state is always loaded fresh
        if (state == null) {
            state = PrabhatAISettingsState.getInstance();
        }

        reset();
        return mainPanel;
    }

    // -------------------------
    // Build UI
    // -------------------------
    private void buildUI() {

        mainPanel = new JPanel(new BorderLayout(12, 12));

        JPanel top = new JPanel();
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));

        top.add(label("OpenAI API Key:"));
        openAiKeyField = new JTextField();
        top.add(openAiKeyField);

        top.add(label("Gemini API Key:"));
        geminiKeyField = new JTextField();
        top.add(geminiKeyField);

        top.add(label("Claude API Key:"));
        claudeKeyField = new JTextField();
        top.add(claudeKeyField);

        top.add(Box.createVerticalStrut(10));
        top.add(label("Enable Providers:"));

        enableMockCheck = new JCheckBox("Mock Provider");
        enableGeminiCheck = new JCheckBox("Google Gemini");
        enableClaudeCheck = new JCheckBox("Claude AI");
        enableOpenAICheck = new JCheckBox("OpenAI");

        top.add(enableMockCheck);
        top.add(enableGeminiCheck);
        top.add(enableClaudeCheck);
        top.add(enableOpenAICheck);

        mainPanel.add(top, BorderLayout.NORTH);

        // Provider priority center panel
        JPanel center = new JPanel(new BorderLayout(5, 5));
        center.setBorder(BorderFactory.createTitledBorder("Provider Priority (Top = Highest)"));

        providerListModel = new DefaultListModel<>();
        providerList = new JList<>(providerListModel);
        providerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scroll = new JScrollPane(providerList);

        upButton = new JButton("↑ Move Up");
        downButton = new JButton("↓ Move Down");

        upButton.addActionListener(this::onMoveUp);
        downButton.addActionListener(this::onMoveDown);

        JPanel btnPanel = new JPanel(new GridLayout(2, 1, 4, 4));
        btnPanel.add(upButton);
        btnPanel.add(downButton);

        center.add(scroll, BorderLayout.CENTER);
        center.add(btnPanel, BorderLayout.EAST);

        mainPanel.add(center, BorderLayout.CENTER);
    }

    private Component label(String text) {
        JLabel l = new JLabel(text);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private void onMoveUp(ActionEvent e) {
        int i = providerList.getSelectedIndex();
        if (i > 0) {
            String item = providerListModel.remove(i);
            providerListModel.add(i - 1, item);
            providerList.setSelectedIndex(i - 1);
        }
    }

    private void onMoveDown(ActionEvent e) {
        int i = providerList.getSelectedIndex();
        if (i >= 0 && i < providerListModel.size() - 1) {
            String item = providerListModel.remove(i);
            providerListModel.add(i + 1, item);
            providerList.setSelectedIndex(i + 1);
        }
    }

    private String[] toArray(DefaultListModel<String> m) {
        String[] arr = new String[m.size()];
        for (int i = 0; i < m.size(); i++) arr[i] = m.get(i);
        return arr;
    }

    // -------------------------
    // isModified()
    // -------------------------
    @Override
    public boolean isModified() {

        if (state == null) return false;

        if (!openAiKeyField.getText().equals(state.openAiKey)) return true;
        if (!geminiKeyField.getText().equals(state.geminiKey)) return true;
        if (!claudeKeyField.getText().equals(state.claudeKey)) return true;

        if (enableMockCheck.isSelected() != state.enableMock) return true;
        if (enableGeminiCheck.isSelected() != state.enableGemini) return true;
        if (enableClaudeCheck.isSelected() != state.enableClaude) return true;
        if (enableOpenAICheck.isSelected() != state.enableOpenAI) return true;

        String newOrder = String.join(",", toArray(providerListModel));
        return !newOrder.equals(state.providerOrder);
    }

    // -------------------------
    // apply()
    // -------------------------
    @Override
    public void apply() throws ConfigurationException {

        if (state == null) return;

        for (String p : toArray(providerListModel)) {
            if (!List.of("mock", "gemini", "claude", "openai").contains(p)) {
                throw new ConfigurationException("Invalid provider: " + p);
            }
        }

        state.openAiKey = openAiKeyField.getText().trim();
        state.geminiKey = geminiKeyField.getText().trim();
        state.claudeKey = claudeKeyField.getText().trim();

        state.enableMock = enableMockCheck.isSelected();
        state.enableGemini = enableGeminiCheck.isSelected();
        state.enableClaude = enableClaudeCheck.isSelected();
        state.enableOpenAI = enableOpenAICheck.isSelected();

        state.providerOrder = String.join(",", toArray(providerListModel));
    }

    // -------------------------
    // reset()
    // -------------------------
    @Override
    public void reset() {
        if (state == null) return;

        openAiKeyField.setText(state.openAiKey);
        geminiKeyField.setText(state.geminiKey);
        claudeKeyField.setText(state.claudeKey);

        enableMockCheck.setSelected(state.enableMock);
        enableGeminiCheck.setSelected(state.enableGemini);
        enableClaudeCheck.setSelected(state.enableClaude);
        enableOpenAICheck.setSelected(state.enableOpenAI);

        providerListModel.clear();

        Arrays.stream(state.providerOrder.split(","))
                .filter(s -> !s.isBlank())
                .forEach(providerListModel::addElement);

        if (!providerListModel.isEmpty())
            providerList.setSelectedIndex(0);
    }
}

