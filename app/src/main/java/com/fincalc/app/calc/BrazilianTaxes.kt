package com.fincalc.app.calc

import kotlin.math.max
import kotlin.math.min
import kotlin.math.abs
import kotlin.math.roundToLong

/**
 * Tabelas fiscais brasileiras usadas em cálculos trabalhistas e empresariais.
 * Valores trabalhistas atualizados para 2026.
 */
object BrazilianTaxes {
    // INSS 2026 (empregado, empregado doméstico e trabalhador avulso)
    val inssWork = listOf(
        InssBracket(0.0, 1_621.00, 0.075),
        InssBracket(1_621.00, 2_902.84, 0.09),
        InssBracket(2_902.84, 4_354.27, 0.12),
        InssBracket(4_354.27, 8_475.55, 0.14)
    )
    const val INSS_WAGE_CEILING = 8_475.55
    const val INSS_MAX_CONTRIBUTION = 988.09

    // IRRF 2026 (mensal)
    val irrfMonthly = listOf(
        IrrfBracket(0.0, 2_428.80, 0.0, 0.0),
        IrrfBracket(2_428.80, 2_826.65, 0.075, 182.16),
        IrrfBracket(2_826.65, 3_751.05, 0.15, 394.16),
        IrrfBracket(3_751.05, 4_664.68, 0.225, 675.49),
        IrrfBracket(4_664.68, Double.MAX_VALUE, 0.275, 908.73)
    )
    const val IRRF_SIMPLIFIED_DEDUCTION = 607.20
    const val IRRF_DEPENDENT_DEDUCTION = 189.59

    // INSS retido sobre pró-labore em 2026
    const val PRO_LABORE_MAX = 8_475.55
    const val PRO_LABORE_RATE = 0.11
    const val PRO_LABORE_CEILING = 932.31

    // FGTS
    const val FGTS_RATE = 0.08
    const val FGTS_SEVERANCE_RATE = 0.40

    // Simples Nacional - Anexo I (Comércio) e Anexo II (Indústria) - faixas com alíquota nominal e parcela a deduzir
    data class SimplesBracket(val upTo: Double, val nominalRate: Double, val deduction: Double)
    val simplesAnexo1 = listOf(
        SimplesBracket(180_000.00, 0.04, 0.0),
        SimplesBracket(360_000.00, 0.073, 5_940.0),
        SimplesBracket(720_000.00, 0.095, 13_860.0),
        SimplesBracket(1_800_000.00, 0.107, 22_500.0),
        SimplesBracket(3_600_000.00, 0.143, 87_300.0),
        SimplesBracket(4_800_000.00, 0.19, 378_000.0)
    )
    val simplesAnexo2 = listOf(
        SimplesBracket(180_000.00, 0.045, 0.0),
        SimplesBracket(360_000.00, 0.078, 5_940.0),
        SimplesBracket(720_000.00, 0.1, 13_860.0),
        SimplesBracket(1_800_000.00, 0.112, 22_500.0),
        SimplesBracket(3_600_000.00, 0.147, 85_500.0),
        SimplesBracket(4_800_000.00, 0.3, 720_000.0)
    )

    // Lucro Presumido - presunção por atividade
    val presuncao = mapOf(
        "Comércio e Indústria" to 0.08,
        "Serviços em geral" to 0.32,
        "Profissionais (médicos, advogados, etc.)" to 0.32
    )
    const val IRPJ_PRESUMIDO_RATE = 0.15
    const val IRPJ_PRESUMIDO_SURCHARGE_RATE = 0.10
    const val IRPJ_QUARTERLY_SURCHARGE_LIMIT = 60_000.0
    const val CSLL_PRESUMIDO_RATE = 0.09
    const val PIS_RATE = 0.0065
    const val COFINS_RATE = 0.03

    // ICMS interestadual
    const val ICMS_INTERNO_DEFAULT = 0.18
    const val ICMS_INTER_7 = 0.07
    const val ICMS_INTER_12 = 0.12
    const val DIFAL_DESTINATION_FLOOR = 0.07
}

data class InssBracket(val from: Double, val to: Double, val rate: Double)
data class IrrfBracket(val from: Double, val to: Double, val rate: Double, val deduction: Double)

/** Helpers de cálculo monetário. */
fun Double.toMoney(): Long = (this * 100).roundToLong()
fun Long.fromCentsToBRL(): Double = this / 100.0
fun formatBRL(value: Double): String {
    val cents = (abs(value) * 100).roundToLong()
    val whole = cents / 100
    val centsPart = (cents % 100).toInt()
    val wholeStr = whole.toString().reversed().chunked(3).joinToString(".").reversed()
    val prefix = if (value < 0) "-R$" else "R$"
    val centsStr = centsPart.toString().padStart(2, '0')
    return "$prefix $wholeStr,$centsStr"
}

fun computeInssWork(gross: Double): Double {
    var remaining = max(0.0, gross)
    var total = 0.0
    for (b in BrazilianTaxes.inssWork) {
        if (remaining <= 0.0) break
        val top = min(remaining, b.to - b.from)
        if (top > 0) {
            total += top * b.rate
            remaining -= top
        }
    }
    return min(total, BrazilianTaxes.INSS_MAX_CONTRIBUTION)
}

fun computeIrrf(
    baseAfterInss: Double,
    dependents: Int = 0,
    useSimplified: Boolean = true,
    taxableIncome: Double = baseAfterInss
): Double {
    val safeTaxableIncome = taxableIncome.coerceAtLeast(0.0)
    val inssAndOtherDeductions = max(0.0, safeTaxableIncome - baseAfterInss)
    val legalDeductions = inssAndOtherDeductions +
        dependents.coerceAtLeast(0) * BrazilianTaxes.IRRF_DEPENDENT_DEDUCTION
    val deduction = if (useSimplified) {
        max(legalDeductions, BrazilianTaxes.IRRF_SIMPLIFIED_DEDUCTION)
    } else {
        legalDeductions
    }
    val calcBase = max(0.0, safeTaxableIncome - deduction)
    if (calcBase <= 0.0) return 0.0
    val b = BrazilianTaxes.irrfMonthly.first { calcBase <= it.to }
    val taxBeforeReduction = max(0.0, calcBase * b.rate - b.deduction)
    val reduction = when {
        safeTaxableIncome <= 5_000.0 -> min(312.89, taxBeforeReduction)
        safeTaxableIncome <= 7_350.0 -> max(0.0, 978.62 - 0.133145 * safeTaxableIncome)
        else -> 0.0
    }
    return max(0.0, taxBeforeReduction - reduction)
}
