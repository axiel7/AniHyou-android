package com.axiel7.anihyou

import com.axiel7.anihyou.utils.MarkdownUtils.formatCompatibleMarkdown
import org.junit.Test

class BasicUnitTest {
    @Test
    fun markdownCompatible() {
        val text = "As much fun as the show was to watch, even with all the delays, I cannot stress enough that the manga is ultimately better.\n" +
                "\n" +
                "~~~img450(https://media.discordapp.net/attachments/862989995455283200/1084397722267815936/image.png?width=960&height=540)\n" +
                "\n" +
                "img450(https://cdn.discordapp.com/attachments/862989995455283200/1084397721894527027/cdf58180-65e4-4a4c-b333-a5dc7b6c4d17.png) \n" +
                "~~~ \n" +
                "\n" +
                "I know this isnt necessarily fair to do but this scene deserved so much better.\n" +
                "\n" +
                "With the exception of a few elf moments that work better in motion and they put the effort in, the manga always does it better."
        print(text.formatCompatibleMarkdown())
        assert(true)
    }
}
