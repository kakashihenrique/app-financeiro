package com.fincalc.app.ui.screens

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fincalc.app.calc.calcSalaryNet
import com.fincalc.app.calc.formatBRL
import com.fincalc.app.calc.parseBRL
import com.fincalc.app.ui.components.CalculatorScaffold
import com.fincalc.app.ui.components.NumberField
import com.fincalc.app.ui.components.ResultCard
import com.fincalc.app.ui.components.SectionLabel

@Composable
fun SalaryNetScreen(onBack: () -> Unit) {
    var bruto by remember { mutableStateOf("") }
    var dependentes by remember { mutableStateOf("0") }
    var outrosDescontos by remember { mutableStateOf("0") }
    var result by remember { mutableStateOf<com.fincalc.app.calc.SalaryNetResult?>(null) }

    val canCalc = parseBRL(bruto) > 0.0

    CalculatorScaffold(
        title = "Salário Líquido",
        onBack = onBack,
        onCalculate = {
            val gross = parseBRL(bruto)
            val dep = dependentes.toIntOrNull() ?: 0
            val other = parseBRL(outrosDescontos)
            result = calcSalaryNet(gross, dep, other)
        },
        calculateEnabled = canCalc
    ) {
        SectionLabel("Entradas")
        NumberField(label = "Salário bruto", value = bruto, onValueChange = { bruto = it }, suffix = "R$")
        NumberField(label = "Número de dependentes", value = dependentes, onValueChange = { dependentes = it.filter(Char::isDigit) }, allowDecimal = false)
        NumberField(label = "Outros descontos (mensal)", value = outrosDescontos, onValueChange = { outrosDescontos = it }, suffix = "R$")

        result?.let { res ->
            Spacer(Modifier.height(8.dp))
            ResultCard(
                title = "Salário líquido",
                primaryLabel = "Valor a receber por mês",
                primaryValue = formatBRL(res.net),
                breakdown = listOf(
                    "Salário bruto" to formatBRL(res.gross),
                    "(-) INSS" to formatBRL(-res.inss),
                    "(-) IRRF" to formatBRL(-res.irrf)
                )
            )
        }
    }
}
