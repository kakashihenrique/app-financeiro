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
import com.fincalc.app.calc.calcThirteenth
import com.fincalc.app.calc.formatBRL
import com.fincalc.app.calc.parseBRL
import com.fincalc.app.ui.components.CalculatorScaffold
import com.fincalc.app.ui.components.NumberField
import com.fincalc.app.ui.components.ResultCard
import com.fincalc.app.ui.components.SectionLabel

@Composable
fun ThirteenthScreen(onBack: () -> Unit) {
    var bruto by remember { mutableStateOf("") }
    var meses by remember { mutableStateOf("12") }
    var dependentes by remember { mutableStateOf("0") }
    var result by remember { mutableStateOf<com.fincalc.app.calc.ThirteenthResult?>(null) }

    val canCalc = parseBRL(bruto) > 0.0

    CalculatorScaffold(
        title = "13º Salário",
        onBack = onBack,
        onCalculate = {
            val sal = parseBRL(bruto)
            val m = meses.toIntOrNull()?.coerceIn(1, 12) ?: 12
            val dep = dependentes.toIntOrNull() ?: 0
            result = calcThirteenth(sal, m, dep)
        },
        calculateEnabled = canCalc
    ) {
        SectionLabel("Entradas")
        NumberField(label = "Salário bruto", value = bruto, onValueChange = { bruto = it }, suffix = "R$")
        NumberField(label = "Meses trabalhados no ano", value = meses, onValueChange = { meses = it.filter(Char::isDigit) }, allowDecimal = false)
        NumberField(label = "Dependentes", value = dependentes, onValueChange = { dependentes = it.filter(Char::isDigit) }, allowDecimal = false)

        result?.let { res ->
            Spacer(Modifier.height(8.dp))
            ResultCard(
                title = "13º salário",
                primaryLabel = "2ª parcela (líquida)",
                primaryValue = formatBRL(res.secondInstallment),
                breakdown = listOf(
                    "Total bruto" to formatBRL(res.gross),
                    "1ª parcela" to formatBRL(res.firstInstallment),
                    "(-) INSS" to formatBRL(-res.inss),
                    "(-) IRRF" to formatBRL(-res.irrf)
                )
            )
        }
    }
}
