package com.example.howie

import org.junit.Test

class NextDayOfMonthTest {
    @Test(expected = IllegalStateException::class)
    fun `construction throws for 0`() {
        NextDayOfMonth(0)
    }

    @Test(expected = IllegalStateException::class)
    fun `construction throws for -1`() {
        NextDayOfMonth(-1)
    }

    @Test(expected = IllegalStateException::class)
    fun `construction throws for 32`() {
        NextDayOfMonth(32)
    }

    @Test()
    fun `construction does not throw for 1 to 31`() {
        for (i in 1..31) {
            NextDayOfMonth(i)
        }
    }
}