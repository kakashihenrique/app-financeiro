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
import com.fincalc.app.calc.calcVacation
import com.fincalc.app.calc.formatBRL
import com.fincalc.app.calc.parseBRL
import com.fincalc.app.ui.components.CalculatorScaffold
import com.fincalc.app.ui.components.NumberField
import com.fincalc.app.ui.components.ResultCard
import com.fincalc.app.ui.components.SectionLabel

@Composable
fun VacationScreen(onBack: () -> Unit) {
    var salario by remember { mutableStateOf("") }
    var dias by remember { mutableStateOf("30") }
    var dependentes by remember { mutableStateOf("0") }
    var adiantar by remember { mutableStateOf(false) }
    var result by remember { mutableStateOf<com.fincalc.app.calc.VacationResult?>(null) }

    val canCalc = parseBRL(salario) > 0.0

    CalculatorScaffold(
        title = "Férias",
        onBack = onBack,
        onCalculate = {
            val sal = parseBRL(salario)
            val d = dias.toIntOrNull()?.coerceIn(1, 30) ?: 30
            val dep = dependentes.toIntOrNull() ?: 0
            result = calcVacation(sal, d, adiantar, dep)
        },
        calculateEnabled = canCalc
    ) {
        SectionLabel("Entradas")
        NumberField(label = "Salário base", value = salario, onValueChange = { salario = it }, suffix = "R$")
        NumberField(label = "Dias de férias", value = dias, onValueChange = { dias = it.filter(Char::isDigit) }, allowDecimal = false)
        NumberField(label = "Dependentes", value = dependentes, onValueChange = { dependentes = it.filter(Char::isDigit) }, allowDecimal = false)
        androidx.compose.foundation.layout.Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            androidx.compose.material3.Switch(
                checked = adiantar,
                onCheckedChange = { adiantar = it }
            )
            androidx.compose.material3.Text(" Adiantar 1ª parcela do 13º")
        }

        result?.let { res ->
            Spacer(Modifier.height(8.dp))
            ResultCard(
                title = "Férias",
                primaryLabel = "Líquido a receber",
                primaryValue = formatBRL(res.net),
                breakdown = listOf(
                    "Férias" to formatBRL(res.grossVacation - res.oneThird),
                    "(+) 1/3 constitucional" to formatBRL(res.oneThird),
                    "(-) INSS" to formatBRL(-res.inss),
                    "(-) IRRF" to formatBRL(-res.irrf),
                    "Abono (adiantamento)" to formatBRL(-res.advance)
                )
            )
        }
    }
}
