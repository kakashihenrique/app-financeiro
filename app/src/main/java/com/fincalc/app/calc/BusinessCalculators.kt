package com.fincalc.app.calc

/** Simples Nacional - alíquota efetiva por faixa de receita. */
enum class SimplesAnexo(val title: String, val brackets: List<BrazilianTaxes.SimplesBracket>) {
    AnexoI("Anexo I - Comércio", BrazilianTaxes.simplesAnexo1),
    AnexoII("Anexo II - Indústria", BrazilianTaxes.simplesAnexo2)
}

data class SimplesResult(
    val anexo: SimplesAnexo,
    val receitaBruta12m: Double,
    val aliquotaNominal: Double,
    val aliquotaEfetiva: Double,
    val dasMensal: Double
)

fun calcSimples(anexo: SimplesAnexo, receitaBruta12m: Double, receitaMes: Double): SimplesResult {
    val rbt12 = receitaBruta12m.coerceIn(0.01, 4_800_000.0)
    val b = anexo.brackets.first { rbt12 <= it.upTo }
    val effective = (((rbt12 * b.nominalRate) - b.deduction) / rbt12).coerceAtLeast(0.0)
    val das = receitaMes.coerceAtLeast(0.0) * effective
    return SimplesResult(
        anexo = anexo,
        receitaBruta12m = rbt12,
        aliquotaNominal = b.nominalRate,
        aliquotaEfetiva = effective,
        dasMensal = das
    )
}

/** Lucro Presumido - IRPJ, CSLL, PIS, Cofins. */
data class LucroPresumidoResult(
    val basePresumida: Double,
    val irpj: Double,
    val irpjAdicional: Double,
    val csll: Double,
    val pis: Double,
    val cofins: Double,
    val total: Double
)

fun calcLucroPresumido(
    receitaTrimestral: Double,
    presuncaoPercent: Double,
    outrasDeducoes: Double = 0.0
): LucroPresumidoResult {
    val safeRevenue = receitaTrimestral.coerceAtLeast(0.0)
    val basePresumida = maxOf(0.0, safeRevenue * presuncaoPercent.coerceAtLeast(0.0) - outrasDeducoes.coerceAtLeast(0.0))
    val irpjBase = basePresumida
    val irpj = irpjBase * BrazilianTaxes.IRPJ_PRESUMIDO_RATE
    val irpjAdicional = maxOf(0.0, irpjBase - BrazilianTaxes.IRPJ_QUARTERLY_SURCHARGE_LIMIT) *
        BrazilianTaxes.IRPJ_PRESUMIDO_SURCHARGE_RATE
    val csll = basePresumida * BrazilianTaxes.CSLL_PRESUMIDO_RATE
    val pis = safeRevenue * BrazilianTaxes.PIS_RATE
    val cofins = safeRevenue * BrazilianTaxes.COFINS_RATE
    val total = irpj + irpjAdicional + csll + pis + cofins
    return LucroPresumidoResult(
        basePresumida = basePresumida,
        irpj = irpj,
        irpjAdicional = irpjAdicional,
        csll = csll,
        pis = pis,
        cofins = cofins,
        total = total
    )
}

/** Lucro Real - IRPJ e CSLL sobre lucro contábil com ajustes. */
data class LucroRealResult(
    val lucroContabil: Double,
    val lucroReal: Double,
    val irpj: Double,
    val irpjAdicional: Double,
    val csll: Double,
    val total: Double
)

fun calcLucroReal(
    lucroContabil: Double,
    adicoes: Double = 0.0,
    exclusoes: Double = 0.0
): LucroRealResult {
    val lucroReal = maxOf(0.0, lucroContabil + adicoes - exclusoes)
    val irpj = lucroReal * BrazilianTaxes.IRPJ_PRESUMIDO_RATE
    val adicional = maxOf(0.0, lucroReal - BrazilianTaxes.IRPJ_QUARTERLY_SURCHARGE_LIMIT) *
        BrazilianTaxes.IRPJ_PRESUMIDO_SURCHARGE_RATE
    val csll = lucroReal * BrazilianTaxes.CSLL_PRESUMIDO_RATE
    return LucroRealResult(
        lucroContabil = lucroContabil,
        lucroReal = lucroReal,
        irpj = irpj,
        irpjAdicional = adicional,
        csll = csll,
        total = irpj + adicional + csll
    )
}

/** ICMS interestadual com Difal. */
data class IcmsInterestadualResult(
    val baseCalculo: Double,
    val icmsOrigem: Double,
    val icmsDestino: Double,
    val difal: Double
)

fun calcIcmsInterestadual(
    valorOperacao: Double,
    aliquotaInterestadual: Double,
    aliquotaInternaDestino: Double,
    consumidorFinalNaoContribuinte: Boolean = true
): IcmsInterestadualResult {
    val icmsOrigem = valorOperacao * aliquotaInterestadual
    val icmsDestino = valorOperacao * aliquotaInternaDestino
    val difal = if (consumidorFinalNaoContribuinte) maxOf(0.0, icmsDestino - icmsOrigem) else 0.0
    return IcmsInterestadualResult(
        baseCalculo = valorOperacao,
        icmsOrigem = icmsOrigem,
        icmsDestino = icmsDestino,
        difal = difal
    )
}

/** Pró-labore - INSS 11% e IRRF. */
data class ProLaboreResult(
    val bruto: Double,
    val inss: Double,
    val irrf: Double,
    val liquido: Double
)

fun calcProLabore(bruto: Double, dependents: Int = 0): ProLaboreResult {
    val safeGross = bruto.coerceAtLeast(0.0)
    val baseInss = safeGross.coerceAtMost(BrazilianTaxes.PRO_LABORE_MAX)
    val inss = minOf(baseInss * BrazilianTaxes.PRO_LABORE_RATE, BrazilianTaxes.PRO_LABORE_CEILING)
    val irrfBase = maxOf(0.0, safeGross - inss)
    val irrf = computeIrrf(irrfBase, dependents, useSimplified = true, taxableIncome = safeGross)
    return ProLaboreResult(bruto = safeGross, inss = inss, irrf = irrf, liquido = safeGross - inss - irrf)
}
