# ⭐ **PrabhatAI – Your Intelligent Code Assistant for JetBrains IDEs**

**PrabhatAI** is a powerful AI-driven coding assistant designed to work *inside your JetBrains IDE* (IntelliJ IDEA, WebStorm, PyCharm, etc.).
It understands your **current project**, analyzes **active files**, and generates **accurate, context-aware responses**, code fixes, documentation, tests, and patches — all without leaving the editor.

---

## 🚀 Key Features

### **⚡ Smart, Project-Aware Chat**

* Reads your **project structure**
* Understands the **active file and cursor context**
* Generates **project-specific** answers — not generic explanations
* Clean, modern chat UI with avatars, bubble messages, dark theme

### **🧠 Multi-Provider AI Engine**

Supports multiple AI providers with failover:

* **Gemini**
* **OpenAI**
* **Claude**
* **Custom Mock Provider** (offline mode)

Automatically switches providers when one fails or hits quota limits.

### **🛠️ One-Click Code Patch Generation**

PrabhatAI can:

* Modify existing files
* Create new files
* Generate multi-file patches
* Preview changes in a dedicated **Diff Panel**

Everything is applied safely using IntelliJ’s PSI APIs.

---

## 💡 Productivity Features

### **📘 Markdown Rendering with Code Copy Buttons**

* Beautifully formatted AI responses
* Syntax-highlighted code blocks
* One-click “Copy” button for every code block
* No horizontal scrolling — responsive layout

### **🧪 Automatic Test Generation**

* Generates JUnit tests
* Creates new test files when missing

### **📝 Code Documentation + Refactoring Suggestions**

* Explains complex code
* Suggests improvements
* Annotates code with comments and reasoning

### **🧩 Plugin Actions**

* **“Ask PrabhatAI”** tool window
* **Inline suggestions** as you type
* **Apply Patch** action

---

## 🔐 Authentication & Provider Settings

Access all configuration under:

```
Settings → Tools → PrabhatAI
```

Configure:

* API keys (Gemini, OpenAI, Claude)
* Provider priority order
* Toggle mock provider
* Quota protection

---

## 🧵 Intelligent Streaming Output

PrabhatAI simulates **typing animation** so responses feel natural and readable — no overwhelming walls of text.

---

## 🧰 Designed for Real-World Development

Whether you're working on Java, Kotlin, Python, JavaScript/TypeScript, Spring Boot, backend systems, or full-stack applications — PrabhatAI deeply analyzes your current project and responds accordingly.

---

## 🖼️ Modern UI Experience

✔ Dark-themed chat
✔ Rounded message bubbles
✔ User + AI avatars
✔ Smooth scrolling
✔ No clutter, clean visual hierarchy

---

## 📦 Installation

1. Go to **Settings → Plugins → Marketplace**
2. Search **“PrabhatAI”**
3. Click **Install**
4. Open the **PrabhatAI Tool Window**
5. Start chatting directly with your code

---

## ⚙️ Requirements

* IntelliJ Platform **2023.3+**
* Java **17+**
* Internet connection for cloud AI providers
* Optional: Local mock mode (offline)

---

## 🧑‍💻 Open for Contributions

PRs, feature ideas, and bug reports are welcome!
This project is actively evolving with improvements in:

* Patch accuracy
* Context extraction
* New AI provider support
* On-device inference support (coming soon)

---

## ⭐ Why Developers Love PrabhatAI

* Not generic — answers are **based on YOUR code**
* Generates **correct file paths** and **actual patches**
* Embedded inside the IDE (no switching tools)
* Faster workflow, cleaner code, fewer mistakes

