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
import com.fincalc.app.calc.BrazilianTaxes
import com.fincalc.app.calc.calcIcmsInterestadual
import com.fincalc.app.calc.formatBRL
import com.fincalc.app.calc.parseBRL
import com.fincalc.app.ui.components.CalculatorScaffold
import com.fincalc.app.ui.components.NumberField
import com.fincalc.app.ui.components.ResultCard
import com.fincalc.app.ui.components.SectionLabel

@Composable
fun IcmsInterestadualScreen(onBack: () -> Unit) {
    var valor by remember { mutableStateOf("") }
    var aliquotaInter by remember { mutableStateOf("12") }
    var aliquotaDestino by remember { mutableStateOf("18") }
    var consumidorFinal by remember { mutableStateOf(true) }
    var result by remember { mutableStateOf<com.fincalc.app.calc.IcmsInterestadualResult?>(null) }

    val canCalc = parseBRL(valor) > 0.0

    CalculatorScaffold(
        title = "ICMS Interestadual",
        onBack = onBack,
        onCalculate = {
            val v = parseBRL(valor)
            val inter = (aliquotaInter.toDoubleOrNull() ?: 12.0) / 100.0
            val destino = (aliquotaDestino.toDoubleOrNull() ?: 18.0) / 100.0
            result = calcIcmsInterestadual(v, inter, destino, consumidorFinal)
        },
        calculateEnabled = canCalc
    ) {
        SectionLabel("Entradas")
        NumberField(label = "Valor da operação", value = valor, onValueChange = { valor = it }, suffix = "R$")
        NumberField(label = "Alíquota interestadual", value = aliquotaInter, onValueChange = { aliquotaInter = it }, suffix = "%")
        NumberField(label = "Alíquota interna destino", value = aliquotaDestino, onValueChange = { aliquotaDestino = it }, suffix = "%")
        androidx.compose.foundation.layout.Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            androidx.compose.material3.Switch(checked = consumidorFinal, onCheckedChange = { consumidorFinal = it })
            Spacer(Modifier.height(4.dp))
            androidx.compose.material3.Text(" Consumidor final não contribuinte (Difal)")
        }

        result?.let { res ->
            Spacer(Modifier.height(8.dp))
            ResultCard(
                title = "ICMS",
                primaryLabel = "DIFAL a recolher",
                primaryValue = formatBRL(res.difal),
                breakdown = listOf(
                    "Base de cálculo" to formatBRL(res.baseCalculo),
                    "ICMS origem" to formatBRL(res.icmsOrigem),
                    "ICMS destino" to formatBRL(res.icmsDestino)
                )
            )
        }
        Spacer(Modifier.height(8.dp))
        androidx.compose.material3.Text(
            text = "Piso de alíquota interestadual: ${(BrazilianTaxes.ICMS_INTER_7 * 100).toInt()}% ou ${(BrazilianTaxes.ICMS_INTER_12 * 100).toInt()}% conforme estado de origem.",
            style = androidx.compose.material3.MaterialTheme.typography.bodyMedium
        )
    }
}
