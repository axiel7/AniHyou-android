package com.axiel7.anihyou.core.model.staff

import org.junit.Test
import org.junit.Assert.assertEquals

class StaffRoleTests {

    @Test
    fun `staff role with parenthesis data parses correctly`() {
        val input = "Main Animator (eps 1-12, 25)"
        val expected = "Main Animator" to " (eps 1-12, 25)"
        val result = input.parseStaffRole()
        assertEquals(expected, result)
    }

    @Test
    fun `staff role without parenthesis data parses correctly`() {
        val input = "Story"
        val expected = "Story" to ""
        val result = input.parseStaffRole()
        assertEquals(expected, result)
    }
}