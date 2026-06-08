package com.fincalc.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.ui.unit.dp
import com.fincalc.app.calc.RescisaoTipo
import com.fincalc.app.calc.calcRescisao
import com.fincalc.app.calc.formatBRL
import com.fincalc.app.calc.parseBRL
import com.fincalc.app.ui.components.CalculatorScaffold
import com.fincalc.app.ui.components.NumberField
import com.fincalc.app.ui.components.ResultCard
import com.fincalc.app.ui.components.SectionLabel

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
fun RescisaoScreen(onBack: () -> Unit) {
    var salario by remember { mutableStateOf("") }
    var dias by remember { mutableStateOf("30") }
    var meses by remember { mutableStateOf("6") }
    var dependentes by remember { mutableStateOf("0") }
    var tipo by remember { mutableStateOf(RescisaoTipo.SemJustaCausa) }
    var feriasVencidas by remember { mutableStateOf(false) }
    var result by remember { mutableStateOf<com.fincalc.app.calc.RescisaoResult?>(null) }

    val canCalc = parseBRL(salario) > 0.0

    CalculatorScaffold(
        title = "Rescisão CLT",
        onBack = onBack,
        onCalculate = {
            val sal = parseBRL(salario)
            val d = dias.toIntOrNull()?.coerceIn(0, 30) ?: 30
            val m = meses.toIntOrNull()?.coerceIn(0, 12) ?: 6
            val dep = dependentes.toIntOrNull() ?: 0
            result = calcRescisao(
                tipo = tipo,
                salarioBruto = sal,
                diasTrabalhados = d,
                mesesTrabalhados = m,
                temFeriasVencidas = feriasVencidas,
                dependents = dep
            )
        },
        calculateEnabled = canCalc
    ) {
        SectionLabel("Tipo de desligamento")
        androidx.compose.foundation.layout.FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            RescisaoTipo.entries.forEach { item ->
                FilterChip(
                    selected = tipo == item,
                    onClick = { tipo = item },
                    label = { Text(item.title) }
                )
            }
        }
        SectionLabel("Entradas")
        NumberField(label = "Salário bruto", value = salario, onValueChange = { salario = it }, suffix = "R$")
        NumberField(label = "Dias trabalhados no mês", value = dias, onValueChange = { dias = it.filter(Char::isDigit) }, allowDecimal = false)
        NumberField(label = "Meses no ano (a partir de jan.)", value = meses, onValueChange = { meses = it.filter(Char::isDigit) }, allowDecimal = false)
        NumberField(label = "Dependentes", value = dependentes, onValueChange = { dependentes = it.filter(Char::isDigit) }, allowDecimal = false)
        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            androidx.compose.material3.Switch(checked = feriasVencidas, onCheckedChange = { feriasVencidas = it })
            Spacer(Modifier.height(4.dp))
            Text(" Possui férias vencidas")
        }

        result?.let { res ->
            Spacer(Modifier.height(8.dp))
            ResultCard(
                title = "Total rescisão",
                primaryLabel = "Líquido a receber",
                primaryValue = formatBRL(res.totalLiquido),
                breakdown = listOf(
                    "Saldo salário" to formatBRL(res.saldoSalario),
                    "Aviso prévio indenizado" to formatBRL(res.avisoPrevio),
                    "Férias prop." to formatBRL(res.feriasProporcionais),
                    "1/3 s/ férias prop." to formatBRL(res.tercoFeriasProporcionais),
                    "Férias vencidas" to formatBRL(res.feriasVencidas + res.tercoFeriasVencidas),
                    "13º proporcional" to formatBRL(res.decimoProporcional),
                    "Multa FGTS" to formatBRL(res.multaFgts),
                    "(-) INSS" to formatBRL(-res.inss),
                    "(-) IRRF" to formatBRL(-res.irrf)
                ),
                helperText = if (res.sacarFgts) "Saque do FGTS liberado." else "Saque do FGTS não disponível neste tipo de desligamento."
            )
        }
    }
}
