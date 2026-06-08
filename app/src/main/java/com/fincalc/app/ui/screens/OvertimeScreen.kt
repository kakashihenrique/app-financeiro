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
import com.fincalc.app.calc.calcOvertime
import com.fincalc.app.calc.formatBRL
import com.fincalc.app.calc.parseBRL
import com.fincalc.app.ui.components.CalculatorScaffold
import com.fincalc.app.ui.components.NumberField
import com.fincalc.app.ui.components.ResultCard
import com.fincalc.app.ui.components.SectionLabel

@Composable
fun OvertimeScreen(onBack: () -> Unit) {
    var salario by remember { mutableStateOf("") }
    var horas by remember { mutableStateOf("220") }
    var extras by remember { mutableStateOf("10") }
    var adicional by remember { mutableStateOf("50") }
    var result by remember { mutableStateOf<com.fincalc.app.calc.OvertimeResult?>(null) }

    val canCalc = parseBRL(salario) > 0.0

    CalculatorScaffold(
        title = "Horas Extras",
        onBack = onBack,
        onCalculate = {
            val sal = parseBRL(salario)
            val h = horas.toIntOrNull()?.coerceAtLeast(1) ?: 220
            val e = extras.toDoubleOrNull() ?: 0.0
            val a = adicional.toDoubleOrNull() ?: 50.0
            result = calcOvertime(sal, h, e, a)
        },
        calculateEnabled = canCalc
    ) {
        SectionLabel("Entradas")
        NumberField(label = "Salário bruto", value = salario, onValueChange = { salario = it }, suffix = "R$")
        NumberField(label = "Horas mensais (base)", value = horas, onValueChange = { horas = it.filter(Char::isDigit) }, allowDecimal = false)
        NumberField(label = "Horas extras no mês", value = extras, onValueChange = { extras = it }, suffix = "h")
        NumberField(label = "Adicional (%)", value = adicional, onValueChange = { adicional = it }, suffix = "%")

        result?.let { res ->
            Spacer(Modifier.height(8.dp))
            ResultCard(
                title = "Horas extras",
                primaryLabel = "Total a pagar no mês",
                primaryValue = formatBRL(res.total),
                breakdown = listOf(
                    "Valor da hora" to formatBRL(res.baseHour),
                    "Horas extras" to "${res.overtimeHours}",
                    "Adicional aplicado" to "${(res.bonusRate * 100).toInt()}%",
                    "Horas extras brutas" to formatBRL(res.grossOvertime),
                    "DSR reflexo" to formatBRL(res.dsrReflex)
                )
            )
        }
    }
}
