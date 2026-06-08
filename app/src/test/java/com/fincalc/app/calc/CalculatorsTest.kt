package com.fincalc.app.calc

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CalculatorsTest {
    @Test
    fun inssUses2026ProgressiveBracketsAndCeiling() {
        assertEquals(121.58, computeInssWork(1_621.00), 0.01)
        assertEquals(988.09, computeInssWork(20_000.00), 0.01)
    }

    @Test
    fun irrfApplies2026MonthlyReduction() {
        assertEquals(0.0, computeIrrf(4_000.0, taxableIncome = 5_000.0), 0.01)
        assertTrue(computeIrrf(7_000.0, taxableIncome = 8_000.0) > 0.0)
    }

    @Test
    fun thirteenthChargesInssOnFullBenefit() {
        val result = calcThirteenth(grossMonthly = 4_000.0, workedMonths = 12)
        assertEquals(computeInssWork(4_000.0), result.inss, 0.01)
        assertEquals(result.gross - result.firstInstallment - result.inss - result.irrf, result.secondInstallment, 0.01)
    }

    @Test
    fun agreementUsesHalfNoticeAndTwentyPercentFgtsFine() {
        val result = calcRescisao(
            tipo = RescisaoTipo.Acordo,
            salarioBruto = 3_000.0,
            diasTrabalhados = 15,
            mesesTrabalhados = 6,
            temFeriasVencidas = false
        )

        assertEquals(1_500.0, result.avisoPrevio, 0.01)
        assertEquals(288.0, result.multaFgts, 0.01)
        assertTrue(result.sacarFgts)
    }

    @Test
    fun moneyFormattingHandlesNegativeValues() {
        assertEquals("-R$ 1.234,56", formatBRL(-1_234.56))
        assertEquals("R$ 1.234,56", formatBRL(1_234.56))
    }

    @Test
    fun simplesClampsRevenueAtLegalLimitInsteadOfCrashing() {
        val result = calcSimples(SimplesAnexo.AnexoI, 5_000_000.0, 10_000.0)
        assertEquals(4_800_000.0, result.receitaBruta12m, 0.01)
        assertTrue(result.dasMensal > 0.0)
    }
}
