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
import com.fincalc.app.calc.calcProLabore
import com.fincalc.app.calc.formatBRL
import com.fincalc.app.calc.parseBRL
import com.fincalc.app.ui.components.CalculatorScaffold
import com.fincalc.app.ui.components.NumberField
import com.fincalc.app.ui.components.ResultCard
import com.fincalc.app.ui.components.SectionLabel

@Composable
fun ProLaboreScreen(onBack: () -> Unit) {
    var bruto by remember { mutableStateOf("") }
    var dependentes by remember { mutableStateOf("0") }
    var result by remember { mutableStateOf<com.fincalc.app.calc.ProLaboreResult?>(null) }

    val canCalc = parseBRL(bruto) > 0.0

    CalculatorScaffold(
        title = "Pró-labore",
        onBack = onBack,
        onCalculate = {
            val b = parseBRL(bruto)
            val dep = dependentes.toIntOrNull() ?: 0
            result = calcProLabore(b, dep)
        },
        calculateEnabled = canCalc
    ) {
        SectionLabel("Entradas")
        NumberField(label = "Retribuição bruta", value = bruto, onValueChange = { bruto = it }, suffix = "R$")
        NumberField(label = "Dependentes", value = dependentes, onValueChange = { dependentes = it.filter(Char::isDigit) }, allowDecimal = false)

        result?.let { res ->
            Spacer(Modifier.height(8.dp))
            ResultCard(
                title = "Pró-labore",
                primaryLabel = "Líquido a receber",
                primaryValue = formatBRL(res.liquido),
                breakdown = listOf(
                    "Retribuição bruta" to formatBRL(res.bruto),
                    "(-) INSS (11%)" to formatBRL(-res.inss),
                    "(-) IRRF" to formatBRL(-res.irrf)
                )
            )
        }
    }
}
