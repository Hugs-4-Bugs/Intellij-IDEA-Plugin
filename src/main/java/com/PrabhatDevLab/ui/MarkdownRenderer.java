//package com.PrabhatDevLab.ui;
//
//import org.intellij.markdown.ast.ASTNode;
//import org.intellij.markdown.flavours.gfm.GFMFlavourDescriptor;
//import org.intellij.markdown.html.HtmlGenerator;
//import org.intellij.markdown.parser.MarkdownParser;
//import kotlin.jvm.functions.Function3;
//
//import java.util.Collections;
//
//public class MarkdownRenderer {
//
//    public static String render(String markdown) {
//        if (markdown == null || markdown.isEmpty()) return "";
//
//        MarkdownParser parser = new MarkdownParser(new GFMFlavourDescriptor());
//        ASTNode ast = parser.buildMarkdownTreeFromString(markdown);
//
//        HtmlGenerator generator = new HtmlGenerator(
//                markdown,
//                ast,
//                new GFMFlavourDescriptor(),
//                false
//        );
//
//        Function3<ASTNode, CharSequence, Iterable<? extends CharSequence>, Iterable<? extends CharSequence>>
//                renderer = (node, text, children) -> Collections.emptyList();
//
//        String html = generator.generateHtml(renderer);
//
//        return wrapCodeBlocks(html);
//    }
//
//    private static String wrapCodeBlocks(String html) {
//        return html.replace("<pre><code>",
//                """
//                <div class='code-container'>
//                    <a href='copy://CODE_BLOCK' class='copy-btn'>Copy</a>
//                    <pre><code>
//                """
//        ).replace("</code></pre>", "</code></pre></div>");
//    }
//}






//package com.PrabhatDevLab.ui;
//
//import org.intellij.markdown.ast.ASTNode;
//import org.intellij.markdown.flavours.gfm.GFMFlavourDescriptor;
//import org.intellij.markdown.html.HtmlGenerator;
//import org.intellij.markdown.parser.MarkdownParser;
//import kotlin.jvm.functions.Function3;
//
//import java.util.Collections;
//
//public final class MarkdownRenderer {
//
//    private static final GFMFlavourDescriptor FLAVOUR = new GFMFlavourDescriptor();
//
//    private MarkdownRenderer() {}
//
//    public static String render(String markdown) {
//        if (markdown == null || markdown.isBlank()) {
//            return "";
//        }
//
//        MarkdownParser parser = new MarkdownParser(FLAVOUR);
//        ASTNode ast = parser.buildMarkdownTreeFromString(markdown);
//
//        HtmlGenerator generator = new HtmlGenerator(
//                markdown,
//                ast,
//                FLAVOUR,
//                false
//        );
//
//        // IMPORTANT:
//        // This is the ONLY compatible way in IntelliJ Platform markdown
//        Function3<
//                ASTNode,
//                CharSequence,
//                Iterable<? extends CharSequence>,
//                Iterable<? extends CharSequence>
//                > emptyRenderer = (node, text, children) -> Collections.emptyList();
//
//        return generator.generateHtml(emptyRenderer);
//    }
//}



//package com.PrabhatDevLab.ui;
//
//import org.intellij.markdown.ast.ASTNode;
//import org.intellij.markdown.flavours.gfm.GFMFlavourDescriptor;
//import org.intellij.markdown.html.HtmlGenerator;
//import org.intellij.markdown.parser.MarkdownParser;
//
//import java.net.URI;
//
//public final class MarkdownRenderer {
//
//    private static final GFMFlavourDescriptor FLAVOUR = new GFMFlavourDescriptor();
//    private static final MarkdownParser PARSER = new MarkdownParser(FLAVOUR);
//
//    private MarkdownRenderer() {}
//
//    public static String render(String markdown) {
//        if (markdown == null || markdown.trim().isEmpty()) {
//            return "<p>No response</p>";
//        }
//
//        try {
//            // Parse markdown to AST
//            ASTNode ast = PARSER.buildMarkdownTreeFromString(markdown);
//
//            // Generate HTML with proper signature
//            HtmlGenerator generator = new HtmlGenerator(markdown, ast, FLAVOUR, false);
//
//            // The generateHtml method requires a URI resolver and a renderer
//            // We use null for URI resolver (no external links processing)
//            String html = generator.generateHtml(
//                    (node, text, children) -> {
//                        // Default rendering - just return children as-is
//                        return children;
//                    }
//            );
//
//            // Post-process: clean up code blocks
//            html = cleanupCodeBlocks(html);
//
//            return html;
//
//        } catch (Exception e) {
//            // Fallback: return escaped text
//            return "<pre>" + escapeHtml(markdown) + "</pre>";
//        }
//    }
//
//    private static String cleanupCodeBlocks(String html) {
//        // Remove language class attributes that might cause issues
//        html = html.replaceAll("<code class=\"language-[^\"]+\">", "<code>");
//
//        // Ensure code blocks have proper structure
//        html = html.replaceAll("<pre><code>", "<pre><code class=\"code-block\">");
//
//        return html;
//    }
//
//    private static String escapeHtml(String text) {
//        if (text == null) return "";
//        return text
//                .replace("&", "&amp;")
//                .replace("<", "&lt;")
//                .replace(">", "&gt;")
//                .replace("\"", "&quot;")
//                .replace("'", "&#39;");
//    }
//}













package com.PrabhatDevLab.ui;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class MarkdownRenderer {

    private MarkdownRenderer() {}

    public static String render(String markdown) {
        if (markdown == null || markdown.trim().isEmpty()) {
            return "<p>No response</p>";
        }

        try {
            return convertMarkdownToHtml(markdown);
        } catch (Exception e) {
            System.err.println("[MarkdownRenderer] Error: " + e.getMessage());
            // Fallback: plain text with line breaks
            return "<pre style='white-space: pre-wrap; word-wrap: break-word;'>" +
                    escapeHtml(markdown) + "</pre>";
        }
    }

    private static String convertMarkdownToHtml(String markdown) {
        StringBuilder html = new StringBuilder();
        String[] lines = markdown.split("\n");

        boolean inCodeBlock = false;
        boolean inList = false;
        StringBuilder codeBlock = new StringBuilder();

        for (String line : lines) {
            // Check for code blocks
            if (line.trim().startsWith("```")) {
                if (!inCodeBlock) {
                    // Start of code block
                    inCodeBlock = true;
                    codeBlock.setLength(0);
                    continue;
                } else {
                    // End of code block
                    inCodeBlock = false;
                    String codeContent = codeBlock.toString().trim();
                    html.append(createFormattedCodeBlock(codeContent));
                    continue;
                }
            }

            if (inCodeBlock) {
                codeBlock.append(line).append("\n");
                continue;
            }

            // Process regular markdown lines
            String processedLine = processMarkdownLine(line);

            // Handle lists
            if (line.trim().matches("^[-*+]\\s+.*")) {
                if (!inList) {
                    html.append("<ul>\n");
                    inList = true;
                }
                String listItem = line.trim().substring(1).trim();
                html.append("<li>").append(processInlineMarkdown(listItem)).append("</li>\n");
            } else {
                if (inList) {
                    html.append("</ul>\n");
                    inList = false;
                }

                // Handle headers
                if (line.trim().startsWith("# ")) {
                    html.append("<h1>").append(line.substring(2).trim()).append("</h1>\n");
                } else if (line.trim().startsWith("## ")) {
                    html.append("<h2>").append(line.substring(3).trim()).append("</h2>\n");
                } else if (line.trim().startsWith("### ")) {
                    html.append("<h3>").append(line.substring(4).trim()).append("</h3>\n");
                } else if (!line.trim().isEmpty()) {
                    html.append("<p>").append(processedLine).append("</p>\n");
                } else {
                    html.append("<br>\n");
                }
            }
        }

        // Close any open list
        if (inList) {
            html.append("</ul>\n");
        }

        return html.toString();
    }

    private static String processMarkdownLine(String line) {
        String processed = line;

        // Bold: **text** or __text__
        processed = processed.replaceAll("\\*\\*(.*?)\\*\\*", "<strong>$1</strong>");
        processed = processed.replaceAll("__(.*?)__", "<strong>$1</strong>");

        // Italic: *text* or _text_
        processed = processed.replaceAll("\\*(?!\\*)(.*?)\\*", "<em>$1</em>");
        processed = processed.replaceAll("_(.*?)_", "<em>$1</em>");

        // Inline code: `code`
        Pattern inlineCodePattern = Pattern.compile("`([^`]+)`");
        Matcher matcher = inlineCodePattern.matcher(processed);
        StringBuffer buffer = new StringBuffer();

        while (matcher.find()) {
            String code = matcher.group(1);
            matcher.appendReplacement(buffer,
                    "<code class='inline-code'>" + escapeHtml(code) + "</code>");
        }
        matcher.appendTail(buffer);
        processed = buffer.toString();

        return processed;
    }

    private static String processInlineMarkdown(String text) {
        // Process inline formatting without creating paragraphs
        String processed = text;

        processed = processed.replaceAll("\\*\\*(.*?)\\*\\*", "<strong>$1</strong>");
        processed = processed.replaceAll("\\*(.*?)\\*", "<em>$1</em>");
        processed = processed.replaceAll("`([^`]+)`",
                "<code class='inline-code'>$1</code>");

        return processed;
    }

    private static String createFormattedCodeBlock(String code) {
        String escapedCode = escapeHtml(code);
        return String.format("""
            <div class="code-block">
                <div class="code-header">
                    <span>Code</span>
                    <a href="copy://%s" class="copy-btn">Copy</a>
                </div>
                <pre><code>%s</code></pre>
            </div>
            """,
                escapedCode.replace("\n", "\\n"),
                escapedCode
        );
    }

    private static String escapeHtml(String text) {
        if (text == null) return "";

        return text
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}