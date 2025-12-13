package com.PrabhatDevLab.ui;

import com.PrabhatDevLab.ui.chat.PrabhatAIChatPanel;
import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;

public class AiToolWindowFactory implements ToolWindowFactory {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {

        try {
            DiffPreviewPanel diffPanel = DiffPreviewPanel.getInstance(project);
            if (diffPanel == null) {
                throw new IllegalStateException("DiffPreviewPanel.getInstance(project) returned null");
            }

            PrabhatAIChatPanel prabhatAIChatPanel = new PrabhatAIChatPanel(project);

            ContentFactory factory = ContentFactory.getInstance();

            Content chatContent = factory.createContent(prabhatAIChatPanel, "Chat", false);
            Content diffContent = factory.createContent(diffPanel.getMainPanel(), "Patch Preview", false);

            toolWindow.getContentManager().addContent(chatContent);
            toolWindow.getContentManager().addContent(diffContent);

        } catch (Exception e) {
            com.intellij.openapi.diagnostic.Logger.getInstance(AiToolWindowFactory.class)
                    .error("Failed to create PrabhatAI Tool Window", e);
        }
    }
}
