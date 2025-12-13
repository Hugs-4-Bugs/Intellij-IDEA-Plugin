package com.PrabhatDevLab.ui;

import org.intellij.markdown.ast.ASTNode;
import org.intellij.markdown.flavours.gfm.GFMFlavourDescriptor;
import org.intellij.markdown.html.HtmlGenerator;
import org.intellij.markdown.parser.MarkdownParser;
import kotlin.jvm.functions.Function3;

import java.util.Collections;

public class MarkdownRenderer {

    public static String render(String markdown) {
        if (markdown == null || markdown.isEmpty()) return "";

        // Markdown flavour
        GFMFlavourDescriptor flavour = new GFMFlavourDescriptor();
        MarkdownParser parser = new MarkdownParser(flavour);

        // AST
        ASTNode ast = parser.buildMarkdownTreeFromString(markdown);

        // Correct constructor for 233.x
        HtmlGenerator generator = new HtmlGenerator(
                markdown,
                ast,
                flavour,
                false              // include src positions = false
        );

        // NO-OP renderer required by generateHtml(Function3)
        Function3<ASTNode, CharSequence, Iterable<? extends CharSequence>, Iterable<? extends CharSequence>> renderer =
                (node, text, children) -> Collections.emptyList();

        String html = generator.generateHtml(renderer);

        String styledHtml = """
        <style>
            body {
                background-color: #1e1e1e;
                color: #e6e6e6;
                font-family: JetBrains Mono, sans-serif;
                line-height: 1.5;
            }

            pre, code {
                background: #2d2d2d;
                border-radius: 6px;
                padding: 8px;
                color: #f8f8f8;
                font-size: 13px;
                overflow-x: auto;
            }

            .code-container {
                position: relative;
                background: #2d2d2d;
                border-radius: 6px;
                padding-top: 30px;
                margin-bottom: 12px;
            }

            .copy-btn {
                position: absolute;
                top: 6px;
                right: 6px;
                background: #444;
                color: #fff;
                border: none;
                padding: 4px 8px;
                font-size: 12px;
                border-radius: 4px;
                cursor: pointer;
            }
            .copy-btn:hover {
                background: #666;
            }
        </style>
        <script>
            function copyCode(btn) {
                const text = btn.nextElementSibling.innerText;
                navigator.clipboard.writeText(text);
            }
        </script>
        """ + enhanceCodeBlocks(html);

        return styledHtml;

    }

    private static String enhanceCodeBlocks(String html) {
        return html.replace("<pre><code>",
                "<div class='code-container'>" +
                        "<button class='copy-btn' onclick='copyCode(this)'>Copy</button>" +
                        "<pre><code>"
        ).replace("</code></pre>", "</code></pre></div>");
    }

}
