# 🚀 **PrabhatAI – AI Coding Assistant for IntelliJ IDEA**

PrabhatAI is a powerful JetBrains IntelliJ plugin that integrates multiple AI models (OpenAI, Gemini, Claude, Mock), understands your **current file**, **project structure**, and **context**, and delivers accurate, project-specific suggestions inside the IDE.

Features include:
✔ AI Chat Panel (PrabhatAIChatPanel)

✔ Code diff patch preview + auto-apply

✔ Context-aware answers using project tree + active file

✔ File-aware prompts (ProviderManager)

✔ Multi-provider fallback logic (OpenAI → Gemini → Claude → Mock)

✔ Rate-limit protection with QuotaGuard

✔ Markdown → styled HTML renderer

✔ Copy-code buttons in chat

✔ Inline suggestions (experimental)

✔ Settings UI for API keys and provider ordering

---

# 📂 **Project Structure**

```
PrabhatAI/
├── build.gradle
├── settings.gradle
├── README.md
├── gradle/
│   └── wrapper/...
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com/PrabhatDevLab
│   │   │       ├── PrabhatAI.java                  # Plugin initializer
│   │   │       ├── actions
│   │   │       │   ├── ApplyPatchAction.java
│   │   │       │   └── ShowAiPanelAction.java
│   │   │       ├── listeners
│   │   │       │   └── PrabhatAIEditorListener.java
│   │   │       ├── services
│   │   │       │   ├── AiFacadeService.java
│   │   │       │   ├── ContextExtractor.java
│   │   │       │   ├── MultiFilePatchApplier.java
│   │   │       │   ├── PromptTemplateManager.java
│   │   │       │   ├── PsiPatchApplier.java
│   │   │       │   ├── ai
│   │   │       │   │   ├── AIProvider.java
│   │   │       │   │   ├── ClaudeAdapter.java
│   │   │       │   │   ├── GeminiAdapter.java
│   │   │       │   │   ├── MockAIProvider.java
│   │   │       │   │   ├── OpenAIAdapter.java
│   │   │       │   │   ├── ProviderManager.java
│   │   │       │   │   ├── QuotaGuard.java
│   │   │       │   │   └── StreamCallback.java
│   │   │       │   ├── context
│   │   │       │   │   ├── ActiveFileContextExtractor.java
│   │   │       │   │   └── ProjectContextExtractor.java
│   │   │       │   ├── models
│   │   │       │   │   ├── AiResponse.java
│   │   │       │   │   ├── MultiFilePatchModel.java
│   │   │       │   │   ├── PatchModel.java
│   │   │       │   │   └── PromptRequest.java
│   │   │       │   ├── patch
│   │   │       │   │   └── PsiPatchApplier.java
│   │   │       │   └── tests
│   │   │       │       ├── JUnitTestGenerator.java
│   │   │       │       └── TestFileCreator.java
│   │   │       ├── settings
│   │   │       │   ├── PrabhatAISettingsConfigurable.java
│   │   │       │   └── PrabhatAISettingsState.java
│   │   │       ├── ui
│   │   │       │   ├── AiToolWindowFactory.java
│   │   │       │   ├── ChatPanel.java
│   │   │       │   ├── DiffPreviewPanel.java
│   │   │       │   ├── InlineSuggestionRenderer.java
│   │   │       │   ├── MarkdownRenderer.java
│   │   │       │   └── chat/PrabhatAIChatPanel.java
│   │   │       └── util/HttpClientUtil.java
│   │   └── resources
│   │       ├── META-INF/plugin.xml
│   │       └── templates/prompt-templates.json
│   └── test
└── structure.txt
```

---

# 🧠 **How PrabhatAI Works (Internal Workflow)**

## **1. User sends a message**

`PrabhatAIChatPanel → sendMessage()`

* Adds user bubble
* Shows "Thinking…" bubble
* Calls:

```java
AiFacadeService.getInstance(project).requestCompletion(prompt)
```

---

## **2. AiFacadeService builds the final prompt**

It injects:

* Full **Project Directory Tree**
* Current Active File Content
* User Question

```java
PROJECT STRUCTURE:
<tree>

CURRENT FILE:
<open file>

USER QUESTION:
<prompt>
```

---

## **3. ProviderManager selects the first available AI provider**

Order defined in Settings:

```
Gemini → OpenAI → Claude → Mock
```

If provider fails, next one is used.

Handles:

* network errors
* invalid API key
* rate limits (QuotaGuard)
* JSON formatting issues

---

## **4. AIProvider returns an AiResponse**

AiResponse contains:

```
String explanation;          // HTML/Markdown answer
PatchModel patchModel;        // single file diff
MultiFilePatchModel multi;    // multi-file diff
```

---

## **5. PrabhatAIChatPanel renders final message**

* Removes “Thinking…”
* Formats Markdown → HTML
* Injects styling
* Renders avatars
* Makes code copyable
* Inserts AI message bubble

---

## **6. PatchPreviewPanel displays code changes**

User can apply patch → IDE modifies code safely.

---

# ⚙️ **Provider Failover Logic**

### **check 1** – Gemini

If Gemini returns HTTP 429:

```
QuotaGuard.setGeminiBlocked(60 seconds)
ProviderManager → tries next provider
ChatPanel → shows warning
```

### **check 2** – OpenAI

If OpenAI fails → fallback to Claude.

### **check 3** – Claude

If fails → fallback to Mock provider.

---

# 🌐 **API Providers & Settings**

Open:

```
Settings → Tools → PrabhatAI
```

You can configure:

* Gemini API Key
* OpenAI API Key
* Claude API Key
* Enable/disable providers
* Provider priority order
* Enable/disable mock provider

Stored in:

```
PrabhatAISettingsState.java
```

---

# 📝 **Markdown Rendering System**

File: `MarkdownRenderer.java`

Features:

* Converts Markdown → HTML
* Wraps `<pre><code>` in styled containers
* Adds “Copy” buttons automatically
* Ensures no horizontal scroll
* Dark theme compatible

---

# 💬 **Chat UI Features**

File: `PrabhatAIChatPanel.java`

* Dark theme
* Responsive chat bubbles
* Avatar icons (user + robot)
* Automatic scrolling
* No horizontal scrollbar
* Code blocks wrapped neatly
* Copy button support
* “Thinking…” placeholder bubble
* Smooth rendering

---

# 🛠️ **Build Instructions**

### **1. Install JDK 17**

JetBrains Platform requires Java 17.

### **2. Run Build**

```
./gradlew build
```

### **3. Create Plugin ZIP**

```
./gradlew buildPlugin
```

ZIP will be generated:

```
build/distributions/PrabhatAI-<version>.zip
```

---

# 📦 **Install Plugin Manually**

Inside IntelliJ:

```
Settings → Plugins → ⚙ → Install Plugin from Disk
```

Select:

```
PrabhatAI-x.y.z.zip
```

Restart IDE → Done.

---

# 🔄 **Internal Patch Workflow**

```
AI → PatchModel → DiffPreviewPanel → ApplyPatchAction → PsiPatchApplier
```

Supports:

* multi-line edits
* multi-file edits
* safe rollback
* JetBrains diff viewer

---

# 🧪 **Testing Modules**

Under `services/tests/`:

* JUnitTestGenerator.java
* TestFileCreator.java

These build files dynamically using plugin templates.

---

# 📁 **Templates**

Located at:

```
src/main/resources/templates/prompt-templates.json
```

Contains reusable prompt snippets used by ProviderManager.

---

# 🔌 **Tool Windows**

### AiToolWindowFactory

Registers your sidebar panel:

```
PrabhatAI Chat  
Patch Preview
```

---

# 💡 **Common Errors & Fixes**

### **Gemini Quota Exceeded**

Plugin will show:

```
⚠ Gemini quota exhausted. Using backup provider...
```

### **OpenAI invalid key**

ProviderManager logs & falls back silently.

### **No provider works**

Chat shows:

```
Error: No AI providers succeeded...
```

Enable Mock provider to avoid total failure.

---

# 🔧 **Development Workflow**

1. Modify code
2. Run plugin sandbox:

   ```
   ./gradlew runIde
   ```
3. Test Chat + Settings + Patch Preview
4. Build zip
5. Install and test in real IntelliJ

---

<img width="1440" height="900" alt="Screenshot 2025-12-13 at 9 54 54 PM" src="https://github.com/user-attachments/assets/bd72e3f8-b74c-4352-a796-909dbd7803b0" />

---

# ✅ **PrabhatAI Plugin — Architecture Diagram (Mermaid)**

```mermaid
flowchart TD

%% ========== UI LAYER ==========
UserInput["User Types Message\n(PrabhatAIChatPanel)"]
ChatPanel["Chat Panel UI\nPrabhatAIChatPanel"]
Markdown["MarkdownRenderer\n ↳ HTML + Code Blocks + Copy Buttons"]
DiffPanel["DiffPreviewPanel\n(Patch Viewer)"]

%% ========== SERVICE FACADE ==========
Facade["AiFacadeService\n(Entry Point for all Requests)"]

%% ========== CONTEXT SYSTEM ==========
ContextExtract["Context Extractors\n(ProjectContextExtractor, ActiveFileContextExtractor)"]
PromptReq["PromptRequest\n(Final structured prompt)"]

%% ========== PROVIDER MANAGER ==========
ProviderManager["ProviderManager\n → Provider fallback\n → QuotaGuard"]
QuotaGuard["QuotaGuard\n(Gemini 429 Detection\nCooldown Management)"]

%% ========== AI PROVIDERS ==========
subgraph AI Providers
OpenAI["OpenAIAdapter"]
Gemini["GeminiAdapter"]
Claude["ClaudeAdapter"]
Mock["MockAIProvider"]
end

%% ========== RESPONSES ==========
AIResponse["AiResponse\n(explanation + patches)"]
PatchModel["PatchModel / MultiFilePatchModel"]

%% ========== WORKFLOW ==========

UserInput --> ChatPanel
ChatPanel --> Facade
Facade --> ContextExtract
ContextExtract --> PromptReq
PromptReq --> ProviderManager

ProviderManager -->|Primary| Gemini
ProviderManager -->|Fallback 2| OpenAI
ProviderManager -->|Fallback 3| Claude
ProviderManager -->|Fallback 4| Mock

Gemini -->|429 Quota Error| QuotaGuard --> ProviderManager
Gemini --> AIResponse
OpenAI --> AIResponse
Claude --> AIResponse
Mock --> AIResponse

AIResponse --> ChatPanel
AIResponse --> Markdown
Markdown --> ChatPanel

AIResponse --> PatchModel --> DiffPanel
```

---

# ✅ **ASCII Architecture Diagram (Good for Documentation inside IntelliJ)**

```
┌──────────────────────┐        ┌────────────────────────┐
│  User (Chat Panel)   │ -----> │ PrabhatAIChatPanel UI  │
└──────────────────────┘        └───────────┬────────────┘
                                             |
                                             v
                                 ┌────────────────────────┐
                                 │    AiFacadeService     │
                                 │  (requestCompletion)   │
                                 └───────────┬────────────┘
                                             |
                 Extracts Project Context    |
                ┌────────────────────────────┘
                v
     ┌──────────────────────────────┐
     │  Context Extractors          │
     │  - ProjectContextExtractor   │
     │  - ActiveFileContextExtractor│
     └───────────────┬──────────────┘
                     |
                     v
      ┌────────────────────────────┐
      │   PromptRequest Builder    │
      └──────────────┬────────────┘
                     |
                     v
      ┌────────────────────────────┐
      │     ProviderManager        │
      │  - tries providers         │
      │  - handles failures        │
      │  - uses QuotaGuard         │
      └──────┬──────┬──────┬──────┘
             |      |      |
   ┌─────────v──┐ ┌─v──────────┐ ┌─────────v───┐ ┌───────────v──────┐
   │ Gemini      │ │ OpenAI     │ │ Claude      │ │ Mock Provider     │
   │ Adapter     │ │ Adapter    │ │ Adapter     │ │ (fallback)        │
   └──────┬──────┘ └─────┬───────┘ └──────┬──────┘ └──────────┬──────┘
          |              |               |                     |
          | Success      | Success       | Success             | Success
          v              v               v                     v
                     ┌───────────────────────────┐
                     │        AiResponse          │
                     │ explanation + patch model  │
                     └─────────────┬─────────────┘
                                   |
                   ┌───────────────┴────────────────┐
                   v                                v
    ┌───────────────────────────┐       ┌────────────────────────────┐
    │ MarkdownRenderer (HTML)   │       │ PatchPreviewPanel (Diff)    │
    └───────────────────────────┘       └────────────────────────────┘

```

---

## ✅ **Conceptual System Overview**

### **1. UI Layer**

* PrabhatAIChatPanel
* DiffPreviewPanel
* MarkdownRenderer

### **2. Service Layer**

* AiFacadeService

  * Builds final context-aware prompt
  * Sends request to ProviderManager

### **3. Context Extraction**

* Reads:

  * Active file
  * Project structure

### **4. Provider Manager + QuotaGuard**

* Tries providers in given order
* Detects Gemini quota issues
* Automatically switches provider

### **5. Providers**

* GeminiAdapter
* OpenAIAdapter
* ClaudeAdapter
* MockAIProvider

### **6. Response Handling**

* Shows AI result
* Displays patches with built-in diff viewer

  

---

# ✅ **UML CLASS DIAGRAM (Mermaid)**

Paste directly into README:

```mermaid
classDiagram
    %% ==============================
    %% UI LAYER
    %% ==============================

    class PrabhatAIChatPanel {
        - Project project
        - JEditorPane chatPane
        - JTextArea inputArea
        - StringBuilder htmlMessages
        + sendMessage()
        + appendUserMessage()
        + appendAIMessage()
        + appendThinkingBubble()
        + removeThinkingBubble()
    }

    class ChatPanel {
    }

    class DiffPreviewPanel {
        + setPatchModel()
        + setMultiFilePatch()
    }

    class MarkdownRenderer {
        + render(markdown): String
    }

    PrabhatAIChatPanel --> MarkdownRenderer
    PrabhatAIChatPanel --> DiffPreviewPanel


    %% ==============================
    %% SERVICE FACADE
    %% ==============================

    class AiFacadeService {
        - ProviderManager manager
        + requestCompletion(prompt): CompletableFuture~AiResponse~
        + getInstance(project): AiFacadeService
    }

    PrabhatAIChatPanel --> AiFacadeService


    %% ==============================
    %% CONTEXT SYSTEM
    %% ==============================

    class ProjectContextExtractor {
        + extract(project): String
    }

    class ActiveFileContextExtractor {
        + extract(project): String
    }

    class PromptRequest {
        - String prompt
        - String context
        + getPrompt()
        + getContext()
        + setPrompt()
        + setContext()
    }

    AiFacadeService --> PromptRequest
    ProviderManager --> ProjectContextExtractor
    ProviderManager --> ActiveFileContextExtractor


    %% ==============================
    %% PROVIDER MANAGER
    %% ==============================

    class ProviderManager {
        - List~AIProvider~ providers
        - Project project
        + complete(request): CompletableFuture~AiResponse~
        + tryProvider()
        + buildFinalPrompt()
    }

    class QuotaGuard {
        + isGeminiBlocked(): boolean
        + updateLastFailure(): void
    }

    ProviderManager --> QuotaGuard


    %% ==============================
    %% AI PROVIDERS
    %% ==============================

    class AIProvider {
        <<interface>>
        + completeCode(PromptRequest): CompletableFuture~AiResponse~
        + providerId(): String
    }

    class GeminiAdapter {
        + setApiKey()
        + providerId()
        + completeCode()
    }

    class OpenAIAdapter {
        + setApiKey()
        + providerId()
        + completeCode()
    }

    class ClaudeAdapter {
        + setApiKey()
        + providerId()
        + completeCode()
    }

    class MockAIProvider {
        + providerId()
        + completeCode()
    }

    AIProvider <|.. GeminiAdapter
    AIProvider <|.. OpenAIAdapter
    AIProvider <|.. ClaudeAdapter
    AIProvider <|.. MockAIProvider

    ProviderManager --> AIProvider


    %% ==============================
    %% RESPONSE MODELS
    %% ==============================

    class AiResponse {
        - String explanation
        - PatchModel patchModel
        - MultiFilePatchModel multiFilePatchModel
        + getExplanation()
        + getPatchModel()
        + getMultiFilePatch()
    }

    class PatchModel {
        + hasChanges(): boolean
    }

    class MultiFilePatchModel {
        + hasChanges(): boolean
    }

    AiResponse --> PatchModel
    AiResponse --> MultiFilePatchModel

    PrabhatAIChatPanel --> AiResponse
```

---

## ✅ This UML shows:

### **UI Layer**

* PrabhatAIChatPanel
* MarkdownRenderer
* DiffPreviewPanel

### **Service Layer**

* AiFacadeService
* ProviderManager

### **Context Layer**

* ProjectContextExtractor
* ActiveFileContextExtractor
* PromptRequest

### **AI Provider Set**

* AIProvider (interface)
* GeminiAdapter
* OpenAIAdapter
* ClaudeAdapter
* MockAIProvider

### **Response Layer**

* AiResponse
* Patch models

### **Utility**

* QuotaGuard

Everything is properly connected with UML relationships.


---

# ## **🔥 UML Sequence Diagram — Prompt → Provider → Response → Patch**

```mermaid
sequenceDiagram
    participant User as User
    participant ChatPanel as PrabhatAIChatPanel
    participant Facade as AiFacadeService
    participant Manager as ProviderManager
    participant Provider as AIProvider (Gemini/OpenAI/Claude)
    participant Diff as DiffPreviewPanel

    User ->> ChatPanel: Enter Prompt
    ChatPanel ->> Facade: requestCompletion(prompt)
    Facade ->> Manager: complete(PromptRequest)
    Manager ->> Manager: Inject Project Context\n(buildFinalPrompt)
    Manager ->> Provider: completeCode(finalPrompt)

    alt Provider success
        Provider -->> Manager: AiResponse
        Manager -->> Facade: AiResponse
        Facade -->> ChatPanel: AiResponse
        ChatPanel ->> Diff: setPatchModel() / setMultiFilePatch()
    else Provider fails
        Provider -->> Manager: Error
        Manager ->> Manager: Try next provider
    end

    ChatPanel ->> User: Render Markdown + Code Blocks + UI
```

---

# ## **🔥 UML Component Diagram — PrabhatAI Plugin**

```mermaid
flowchart TD

    subgraph UI["UI Layer"]
        ChatPanel["PrabhatAIChatPanel"]
        MarkdownRenderer["MarkdownRenderer"]
        DiffPanel["DiffPreviewPanel"]
        InlineRenderer["InlineSuggestionRenderer"]
    end

    subgraph Services["Core Services"]
        Facade["AiFacadeService"]
        ContextExtractor["ContextExtractor"]
        PatchApplier["PsiPatchApplier / MultiFilePatchApplier"]
        PromptManager["PromptTemplateManager"]
    end

    subgraph Providers["AI Providers"]
        ProviderManager["ProviderManager"]
        Gemini["GeminiAdapter"]
        OpenAI["OpenAIAdapter"]
        Claude["ClaudeAdapter"]
        Mock["MockAIProvider"]
        Quota["QuotaGuard"]
    end

    subgraph Settings["Settings"]
        SettingsState["PrabhatAISettingsState"]
        SettingsUI["PrabhatAISettingsConfigurable"]
    end

    subgraph Platform["JetBrains Platform"]
        ToolWindow["AiToolWindowFactory"]
        PSI["IntelliJ PSI APIs"]
        ProjectContext["ProjectContextExtractor"]
        FileContext["ActiveFileContextExtractor"]
    end

    ChatPanel --> Facade
    Facade --> ProviderManager
    ProviderManager --> Gemini
    ProviderManager --> OpenAI
    ProviderManager --> Claude
    ProviderManager --> Mock
    ProviderManager --> Quota

    Facade --> ContextExtractor
    PatchApplier --> PSI
    DiffPanel --> PSI

    SettingsUI --> SettingsState
    Providers --> SettingsState

    ToolWindow --> ChatPanel
```

---

# ## **🔥 JetBrains Plugin Architecture Diagram**

```mermaid
flowchart LR

    subgraph JetBrains IDE
        TW["Tool Window (AiToolWindowFactory)"]
        Editor["Editor + PSI"]
    end

    TW --> ChatPanelUI["PrabhatAIChatPanel UI"]

    subgraph PluginCore["Plugin Core"]
        FacadeSvc["AiFacadeService"]
        ProviderMgr["ProviderManager"]
        CtxExt["Context Extractors\n(Project + Active File)"]
        Patch["Patch System\n(PsiPatchApplier,\nMultiFilePatchApplier)"]
        Markdown["MarkdownRenderer"]
    end

    subgraph Providers["AI Providers Layer"]
        GAI["GeminiAdapter"]
        OAI["OpenAIAdapter"]
        CAI["ClaudeAdapter"]
        MPI["MockAIProvider"]
        QG["QuotaGuard"]
    end

    ChatPanelUI --> FacadeSvc
    FacadeSvc --> ProviderMgr
    ProviderMgr --> GAI
    ProviderMgr --> OAI
    ProviderMgr --> CAI
    ProviderMgr --> MPI
    ProviderMgr --> QG

    FacadeSvc --> CtxExt
    ChatPanelUI --> Markdown
    Patch --> Editor
```

---

# ## **🔥 High-Level System Context Diagram (C4 Model — Level 1)**

```mermaid
flowchart TB
    User(["Developer User"])

    IDE["JetBrains IDE\n(IntelliJ, WebStorm, PyCharm)"]

    Plugin["PrabhatAI Plugin\n(Your Plugin)"]

    Providers["External AI Providers\n(Gemini, OpenAI, Claude)"]

    Files["Project Source Files"]
    PSI["JetBrains PSI System"]

    User --> IDE
    IDE --> Plugin
    Plugin --> Providers
    Plugin --> PSI
    PSI --> Files
```

---


# 🙌 **Contributing**

Pull requests welcome.

You can contribute:

* New AI adapters
* Better prompt engineering
* Inline suggestions model
* Chat UI themes
* Better diff rendering
* AI autocomplete (coming soon)

---

# 🏁 **License**

MIT License (recommend adding this).





