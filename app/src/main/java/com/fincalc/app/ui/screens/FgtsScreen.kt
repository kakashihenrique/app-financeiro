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
import com.fincalc.app.calc.calcFgts
import com.fincalc.app.calc.formatBRL
import com.fincalc.app.calc.parseBRL
import com.fincalc.app.ui.components.CalculatorScaffold
import com.fincalc.app.ui.components.NumberField
import com.fincalc.app.ui.components.ResultCard
import com.fincalc.app.ui.components.SectionLabel

@Composable
fun FgtsScreen(onBack: () -> Unit) {
    var salario by remember { mutableStateOf("") }
    var meses by remember { mutableStateOf("12") }
    var result by remember { mutableStateOf<com.fincalc.app.calc.FgtsResult?>(null) }

    val canCalc = parseBRL(salario) > 0.0

    CalculatorScaffold(
        title = "FGTS Mensal",
        onBack = onBack,
        onCalculate = {
            val sal = parseBRL(salario)
            val m = meses.toIntOrNull()?.coerceAtLeast(1) ?: 12
            result = calcFgts(sal, m)
        },
        calculateEnabled = canCalc
    ) {
        SectionLabel("Entradas")
        NumberField(label = "Salário bruto", value = salario, onValueChange = { salario = it }, suffix = "R$")
        NumberField(label = "Meses a projetar", value = meses, onValueChange = { meses = it.filter(Char::isDigit) }, allowDecimal = false)

        result?.let { res ->
            Spacer(Modifier.height(8.dp))
            ResultCard(
                title = "FGTS projetado",
                primaryLabel = "Saldo acumulado estimado",
                primaryValue = formatBRL(res.accumulated),
                breakdown = listOf(
                    "Depósito mensal" to formatBRL(res.monthly),
                    "Multa rescisória (40%)" to formatBRL(res.severance)
                ),
                helperText = "Rendimento estimado de 0,5% a.m. (referência). Verifique a taxa atual."
            )
        }
    }
}
