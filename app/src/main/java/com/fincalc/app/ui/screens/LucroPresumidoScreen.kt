package com.fincalc.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fincalc.app.calc.BrazilianTaxes
import com.fincalc.app.calc.calcLucroPresumido
import com.fincalc.app.calc.formatBRL
import com.fincalc.app.calc.parseBRL
import com.fincalc.app.ui.components.CalculatorScaffold
import com.fincalc.app.ui.components.NumberField
import com.fincalc.app.ui.components.ResultCard
import com.fincalc.app.ui.components.SectionLabel

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
fun LucroPresumidoScreen(onBack: () -> Unit) {
    var receita by remember { mutableStateOf("") }
    var presuncao by remember { mutableStateOf("Comércio e Indústria") }
    var deducoes by remember { mutableStateOf("0") }
    var result by remember { mutableStateOf<com.fincalc.app.calc.LucroPresumidoResult?>(null) }

    val canCalc = parseBRL(receita) > 0.0

    CalculatorScaffold(
        title = "Lucro Presumido",
        onBack = onBack,
        onCalculate = {
            val r = parseBRL(receita)
            val p = BrazilianTaxes.presuncao[presuncao] ?: 0.08
            val d = parseBRL(deducoes)
            result = calcLucroPresumido(r, p, d)
        },
        calculateEnabled = canCalc
    ) {
        SectionLabel("Atividade")
        androidx.compose.foundation.layout.FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            BrazilianTaxes.presuncao.keys.forEach { key ->
                androidx.compose.material3.FilterChip(
                    selected = presuncao == key,
                    onClick = { presuncao = key },
                    label = { androidx.compose.material3.Text(key) }
                )
            }
        }
        SectionLabel("Entradas")
        NumberField(label = "Receita trimestral", value = receita, onValueChange = { receita = it }, suffix = "R$")
        NumberField(label = "Outras deduções", value = deducoes, onValueChange = { deducoes = it }, suffix = "R$")

        result?.let { res ->
            Spacer(Modifier.height(8.dp))
            ResultCard(
                title = "Tributos trimestre",
                primaryLabel = "Total estimado",
                primaryValue = formatBRL(res.total),
                breakdown = listOf(
                    "Base presumida" to formatBRL(res.basePresumida),
                    "IRPJ" to formatBRL(res.irpj),
                    "IRPJ adicional" to formatBRL(res.irpjAdicional),
                    "CSLL" to formatBRL(res.csll),
                    "PIS" to formatBRL(res.pis),
                    "Cofins" to formatBRL(res.cofins)
                )
            )
        }
    }
}
