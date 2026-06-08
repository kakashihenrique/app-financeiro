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
fun SimplesScreen(onBack: () -> Unit) {
    var rbt12 by remember { mutableStateOf("") }
    var receitaMes by remember { mutableStateOf("") }
    var anexo by remember { mutableStateOf(SimplesAnexo.AnexoI) }
    var result by remember { mutableStateOf<com.fincalc.app.calc.SimplesResult?>(null) }

    val canCalc = parseBRL(rbt12) > 0.0 && parseBRL(receitaMes) > 0.0

    CalculatorScaffold(
        title = "Simples Nacional",
        onBack = onBack,
        onCalculate = {
            val rb = parseBRL(rbt12)
            val rm = parseBRL(receitaMes)
            result = calcSimples(anexo, rb, rm)
        },
        calculateEnabled = canCalc
    ) {
        SectionLabel("Anexo")
        androidx.compose.foundation.layout.Row(horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)) {
            SimplesAnexo.entries.forEach { a ->
                androidx.compose.material3.FilterChip(
                    selected = anexo == a,
                    onClick = { anexo = a },
                    label = { androidx.compose.material3.Text(a.title) }
                )
            }
        }
        SectionLabel("Entradas")
        NumberField(label = "Receita bruta 12 meses", value = rbt12, onValueChange = { rbt12 = it }, suffix = "R$")
        NumberField(label = "Receita do mês atual", value = receitaMes, onValueChange = { receitaMes = it }, suffix = "R$")

        result?.let { res ->
            Spacer(Modifier.height(8.dp))
            ResultCard(
                title = "DAS Mensal",
                primaryLabel = "Valor estimado do DAS",
                primaryValue = formatBRL(res.dasMensal),
                breakdown = listOf(
                    "Anexo" to res.anexo.title,
                    "Alíquota nominal" to "${formatPercent(res.aliquotaNominal)}%",
                    "Alíquota efetiva" to "${formatPercent(res.aliquotaEfetiva)}%",
                    "RBT12" to formatBRL(res.receitaBruta12m)
                )
            )
        }
    }
}

private fun formatPercent(value: Double) = "%.2f".format(value * 100)
