package com.fincalc.app.calc

/** Cálculo do salário líquido mensal (INSS + IRRF). */
data class SalaryNetResult(
    val gross: Double,
    val inss: Double,
    val irrf: Double,
    val net: Double
)

fun calcSalaryNet(gross: Double, dependents: Int = 0, otherDeductions: Double = 0.0): SalaryNetResult {
    val safeGross = gross.coerceAtLeast(0.0)
    val safeDeductions = otherDeductions.coerceAtLeast(0.0)
    val inss = computeInssWork(safeGross)
    val irrfBase = maxOf(0.0, safeGross - inss - safeDeductions)
    val irrf = computeIrrf(irrfBase, dependents, useSimplified = true, taxableIncome = safeGross)
    return SalaryNetResult(
        gross = safeGross,
        inss = inss,
        irrf = irrf,
        net = safeGross - inss - irrf - safeDeductions
    )
}

/** Férias com 1/3 constitucional e adiantamento opcional. */
data class VacationResult(
    val base: Double,
    val grossVacation: Double,
    val oneThird: Double,
    val advance: Double,
    val inss: Double,
    val irrf: Double,
    val net: Double
)

fun calcVacation(
    base: Double,
    daysOff: Int = 30,
    advanceParcel: Boolean = false,
    dependents: Int = 0
): VacationResult {
    val safeBase = base.coerceAtLeast(0.0)
    val proportional = safeBase * daysOff.coerceIn(1, 30) / 30.0
    val oneThird = proportional / 3.0
    val gross = proportional + oneThird
    val advance = if (advanceParcel) proportional / 2.0 else 0.0
    val inss = computeInssWork(gross)
    val irrfBase = maxOf(0.0, gross - inss)
    val irrf = computeIrrf(irrfBase, dependents, useSimplified = true, taxableIncome = gross)
    return VacationResult(
        base = safeBase,
        grossVacation = gross,
        oneThird = oneThird,
        advance = advance,
        inss = inss,
        irrf = irrf,
        net = gross - inss - irrf - advance
    )
}

/** 13º salário - suporta cálculo de 1ª parcela (adiantamento) ou 2ª parcela (com descontos). */
data class ThirteenthResult(
    val gross: Double,
    val inss: Double,
    val irrf: Double,
    val firstInstallment: Double,
    val secondInstallment: Double
)

fun calcThirteenth(
    grossMonthly: Double,
    workedMonths: Int,
    dependents: Int = 0
): ThirteenthResult {
    val total = grossMonthly.coerceAtLeast(0.0) * workedMonths.coerceIn(0, 12) / 12.0
    val firstInstallment = total / 2.0
    val inss = computeInssWork(total)
    val irrfBase = maxOf(0.0, total - inss)
    val irrf = computeIrrf(irrfBase, dependents, useSimplified = true, taxableIncome = total)
    val secondInstallment = total - firstInstallment - inss - irrf
    return ThirteenthResult(
        gross = total,
        inss = inss,
        irrf = irrf,
        firstInstallment = firstInstallment,
        secondInstallment = secondInstallment
    )
}

/** Rescisão CLT - cobre os principais eventos. */
enum class RescisaoTipo(val title: String) {
    SemJustaCausa("Demissão sem justa causa"),
    ComJustaCausa("Demissão por justa causa"),
    PedidoDemissao("Pedido de demissão"),
    Acordo("Acordo entre partes (Art. 484-A)")
}

data class RescisaoResult(
    val tipo: RescisaoTipo,
    val saldoSalario: Double,
    val avisoPrevio: Double,
    val feriasProporcionais: Double,
    val tercoFeriasProporcionais: Double,
    val feriasVencidas: Double,
    val tercoFeriasVencidas: Double,
    val decimoProporcional: Double,
    val multaFgts: Double,
    val inss: Double,
    val irrf: Double,
    val totalBruto: Double,
    val totalLiquido: Double,
    val sacarFgts: Boolean
)

fun calcRescisao(
    tipo: RescisaoTipo,
    salarioBruto: Double,
    diasTrabalhados: Int,
    mesesTrabalhados: Int,
    temFeriasVencidas: Boolean,
    dependents: Int = 0
): RescisaoResult {
    val salario = salarioBruto.coerceAtLeast(0.0)
    val meses = mesesTrabalhados.coerceIn(0, 12)
    val saldoSalario = salario * diasTrabalhados.coerceIn(0, 30) / 30.0
    val avisoPrevioIndenizado = when (tipo) {
        RescisaoTipo.SemJustaCausa -> salario
        RescisaoTipo.Acordo -> salario / 2.0
        else -> 0.0
    }
    val feriasProp = if (tipo == RescisaoTipo.ComJustaCausa) 0.0 else salario * meses / 12.0
    val tercoFeriasProp = feriasProp / 3.0
    val feriasVencidas = if (temFeriasVencidas) salario + salario / 3.0 else 0.0
    val decimoProp = if (tipo == RescisaoTipo.ComJustaCausa) 0.0 else salario * meses / 12.0

    val sacarFgts = tipo == RescisaoTipo.SemJustaCausa || tipo == RescisaoTipo.Acordo
    val multaRate = when (tipo) {
        RescisaoTipo.SemJustaCausa -> 0.40
        RescisaoTipo.Acordo -> 0.20
        else -> 0.0
    }
    val multaFgts = if (multaRate > 0.0) {
        // Apenas o valor de referência; o saldo do FGTS é depositado pelo empregador.
        salario * meses * BrazilianTaxes.FGTS_RATE * multaRate
    } else 0.0

    val totalBruto = saldoSalario + avisoPrevioIndenizado + feriasProp + tercoFeriasProp +
        feriasVencidas + decimoProp
    val inssSaldo = computeInssWork(saldoSalario)
    val inssDecimo = computeInssWork(decimoProp)
    val inss = inssSaldo + inssDecimo
    val irrfSaldo = computeIrrf(
        baseAfterInss = maxOf(0.0, saldoSalario - inssSaldo),
        dependents = dependents,
        useSimplified = true,
        taxableIncome = saldoSalario
    )
    val irrfDecimo = computeIrrf(
        baseAfterInss = maxOf(0.0, decimoProp - inssDecimo),
        dependents = dependents,
        useSimplified = true,
        taxableIncome = decimoProp
    )
    val irrf = irrfSaldo + irrfDecimo

    return RescisaoResult(
        tipo = tipo,
        saldoSalario = saldoSalario,
        avisoPrevio = avisoPrevioIndenizado,
        feriasProporcionais = feriasProp,
        tercoFeriasProporcionais = tercoFeriasProp,
        feriasVencidas = if (temFeriasVencidas) salario else 0.0,
        tercoFeriasVencidas = if (temFeriasVencidas) salario / 3.0 else 0.0,
        decimoProporcional = decimoProp,
        multaFgts = multaFgts,
        inss = inss,
        irrf = irrf,
        totalBruto = totalBruto,
        totalLiquido = totalBruto - inss - irrf,
        sacarFgts = sacarFgts
    )
}

/** Horas extras com adicional e DSR reflexo simplificado. */
data class OvertimeResult(
    val baseHour: Double,
    val overtimeHours: Double,
    val bonusRate: Double,
    val grossOvertime: Double,
    val dsrReflex: Double,
    val total: Double
)

fun calcOvertime(
    salarioBruto: Double,
    horasMensais: Int = 220,
    horasExtras: Double,
    adicionalPercent: Double = 50.0
): OvertimeResult {
    val valorHora = salarioBruto.coerceAtLeast(0.0) / horasMensais.coerceAtLeast(1)
    val adicional = adicionalPercent.coerceAtLeast(0.0) / 100.0
    val safeExtras = horasExtras.coerceAtLeast(0.0)
    val brutoHoras = safeExtras * valorHora * (1 + adicional)
    // DSR reflexo: (horas extras / dias úteis) * domingos/feriados
    val diasUteis = 22.0
    val dsr = brutoHoras / diasUteis * 8
    return OvertimeResult(
        baseHour = valorHora,
        overtimeHours = safeExtras,
        bonusRate = adicional,
        grossOvertime = brutoHoras,
        dsrReflex = dsr,
        total = brutoHoras + dsr
    )
}

/** FGTS mensal e projeção com taxa de rendimento 0,5% a.m. (TR + 3% a.a. aproximadamente). */
data class FgtsResult(
    val gross: Double,
    val monthly: Double,
    val accumulated: Double,
    val severance: Double
)

fun calcFgts(
    salarioBruto: Double,
    meses: Int,
    taxaRendimentoMensal: Double = 0.005
): FgtsResult {
    val safeSalary = salarioBruto.coerceAtLeast(0.0)
    val depositoMensal = safeSalary * BrazilianTaxes.FGTS_RATE
    var saldo = 0.0
    repeat(meses.coerceAtLeast(0)) {
        saldo = (saldo + depositoMensal) * (1 + taxaRendimentoMensal.coerceAtLeast(0.0))
    }
    val multa = saldo * BrazilianTaxes.FGTS_SEVERANCE_RATE
    return FgtsResult(
        gross = safeSalary,
        monthly = depositoMensal,
        accumulated = saldo,
        severance = multa
    )
}
