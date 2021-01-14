package com.example.howie.core

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class TaskTest {
    @Test
    fun isSnoozed() {
        assertFalse(Task("", snoozed = null).isSnoozed())
        assertFalse(Task("", snoozed = LocalDate.MIN).isSnoozed())
        assertTrue(Task("", snoozed = LocalDate.MAX).isSnoozed())
        assertFalse(Task("", snoozed = LocalDate.now()).isSnoozed())
        assertTrue(Task("", snoozed = LocalDate.now().plusDays(1)).isSnoozed())
    }
}