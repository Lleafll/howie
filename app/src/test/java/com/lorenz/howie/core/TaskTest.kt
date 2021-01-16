package com.lorenz.howie.core

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

    @Test
    fun isArchived() {
        assertTrue(Task("", archived = LocalDate.MAX).isArchived())
        assertTrue(Task("", archived = LocalDate.MIN).isArchived())
        assertTrue(Task("", archived = LocalDate.of(1234, 12, 12)).isArchived())
        assertFalse(Task("").isArchived())
        assertFalse(Task("", archived = null).isArchived())
    }
}