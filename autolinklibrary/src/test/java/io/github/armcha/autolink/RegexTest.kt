package io.github.armcha.autolink

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class RegexTest {
    @Test
    fun testModeMentionRegex() {
        val matcher = MODE_MENTION.toPattern().matcher("@test (@test)")
        var count = 0
        while (matcher.find()) {
            count++
            // val group = matcher.group()
            // println(group.toString().trim())
        }
        Assertions.assertEquals(2, count)
    }

    @Test
    fun testModeHashTagRegex() {
        val matcher = MODE_HASHTAG.toPattern().matcher("#test (#withinbrackets) #日本語 #testjoined1#testjoined2,#aftercomma)")
        var count = 0
        while (matcher.find()) {
            count++
            // val group = matcher.group()
            // println(group.toString().trim())
        }
        Assertions.assertEquals(6, count)
    }
}