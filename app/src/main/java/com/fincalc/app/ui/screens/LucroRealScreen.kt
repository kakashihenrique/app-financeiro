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
import com.fincalc.app.calc.calcLucroReal
import com.fincalc.app.calc.formatBRL
import com.fincalc.app.calc.parseBRL
import com.fincalc.app.ui.components.CalculatorScaffold
import com.fincalc.app.ui.components.NumberField
import com.fincalc.app.ui.components.ResultCard
import com.fincalc.app.ui.components.SectionLabel

@Composable
fun LucroRealScreen(onBack: () -> Unit) {
    var lucro by remember { mutableStateOf("") }
    var adicoes by remember { mutableStateOf("0") }
    var exclusoes by remember { mutableStateOf("0") }
    var result by remember { mutableStateOf<com.fincalc.app.calc.LucroRealResult?>(null) }

    val canCalc = parseBRL(lucro) > 0.0

    CalculatorScaffold(
        title = "Lucro Real",
        onBack = onBack,
        onCalculate = {
            val l = parseBRL(lucro)
            val a = parseBRL(adicoes)
            val e = parseBRL(exclusoes)
            result = calcLucroReal(l, a, e)
        },
        calculateEnabled = canCalc
    ) {
        SectionLabel("Entradas")
        NumberField(label = "Lucro contábil", value = lucro, onValueChange = { lucro = it }, suffix = "R$")
        NumberField(label = "Adições", value = adicoes, onValueChange = { adicoes = it }, suffix = "R$")
        NumberField(label = "Exclusões", value = exclusoes, onValueChange = { exclusoes = it }, suffix = "R$")

        result?.let { res ->
            Spacer(Modifier.height(8.dp))
            ResultCard(
                title = "Tributos trimestre",
                primaryLabel = "Total estimado",
                primaryValue = formatBRL(res.total),
                breakdown = listOf(
                    "Lucro contábil" to formatBRL(res.lucroContabil),
                    "Lucro real" to formatBRL(res.lucroReal),
                    "IRPJ" to formatBRL(res.irpj),
                    "IRPJ adicional" to formatBRL(res.irpjAdicional),
                    "CSLL" to formatBRL(res.csll)
                )
            )
        }
    }
}
