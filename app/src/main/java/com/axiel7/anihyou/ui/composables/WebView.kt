package com.axiel7.anihyou.ui.composables

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toArgb
import com.axiel7.anihyou.utils.ColorUtils.hexToString

@Composable
fun generateHtml(html: String) = """
    <HTML>
    <head>
        <meta name='viewport' content='width=device-width, shrink-to-fit=YES, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, user-scalable=no'>
    </head>
    ${generateCSS()}
    <BODY>
    <div id="anihyou">$html</div>
    </BODY>
    </HTML>
""".trimIndent()

@Composable
fun generateCSS(): String {
    return """
    <style type='text/css'>
        ${
            baseCss(
                backgroundColor = MaterialTheme.colorScheme.background.toArgb().hexToString(),
                fontColor = MaterialTheme.colorScheme.onBackground.toArgb().hexToString(),
                linkColor = MaterialTheme.colorScheme.secondary.toArgb().hexToString()
            )
        }
        body {
            margin: 0;
            padding: 0;
        }
    </style>
    """.trimIndent()
}

fun baseCss(
    backgroundColor: String,
    fontColor: String,
    linkColor: String
) = """
    body {background-color: $backgroundColor;}
    img{max-height: 100%; min-height: 100%; height:auto; max-width: 100%; width:auto;margin-bottom:5px; border-radius: 8px;}
    h1, h2, h3, h4, h5, h6, p, div, dl, ol, ul, pre, blockquote {text-align:left; line-height: 170%; font-family: 'Arial' !important; color: $fontColor; }
    iframe{width:100%; height:250px;}
    a:link {color: $linkColor;}
    A {text-decoration: none;}
""".trimIndent()