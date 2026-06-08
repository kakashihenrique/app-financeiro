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
import com.fincalc.app.calc.SimplesAnexo
import com.fincalc.app.calc.calcSimples
import com.fincalc.app.calc.formatBRL
import com.fincalc.app.calc.parseBRL
import com.fincalc.app.ui.components.CalculatorScaffold
import com.fincalc.app.ui.components.NumberField
import com.fincalc.app.ui.components.ResultCard
import com.fincalc.app.ui.components.SectionLabel

@Composable
fun DasScreen(onBack: () -> Unit) {
    var rbt12 by remember { mutableStateOf("") }
    var receitaMes by remember { mutableStateOf("") }
    var result by remember { mutableStateOf<com.fincalc.app.calc.SimplesResult?>(null) }

    val canCalc = parseBRL(rbt12) > 0.0 && parseBRL(receitaMes) > 0.0

    CalculatorScaffold(
        title = "DAS",
        onBack = onBack,
        onCalculate = {
            val rb = parseBRL(rbt12)
            val rm = parseBRL(receitaMes)
            result = calcSimples(SimplesAnexo.AnexoI, rb, rm)
        },
        calculateEnabled = canCalc
    ) {
        SectionLabel("Entradas")
        NumberField(label = "Receita bruta 12 meses", value = rbt12, onValueChange = { rbt12 = it }, suffix = "R$")
        NumberField(label = "Receita do mês", value = receitaMes, onValueChange = { receitaMes = it }, suffix = "R$")

        result?.let { res ->
            Spacer(Modifier.height(8.dp))
            ResultCard(
                title = "DAS a pagar",
                primaryLabel = "Valor do DAS",
                primaryValue = formatBRL(res.dasMensal),
                breakdown = listOf(
                    "Alíquota efetiva" to "${formatDasPercent(res.aliquotaEfetiva)}%",
                    "RBT12" to formatBRL(res.receitaBruta12m)
                ),
                helperText = "Estimativa simplificada. Confirme os valores no Portal do Simples Nacional."
            )
        }
    }
}

private fun formatDasPercent(value: Double) = "%.2f".format(value * 100)
