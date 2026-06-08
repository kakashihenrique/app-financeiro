package com.fincalc.app.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.BeachAccess
import androidx.compose.material.icons.outlined.Business
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.Paid
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.RequestQuote
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.SquareFoot
import androidx.compose.material.icons.outlined.WorkHistory
import androidx.compose.ui.graphics.vector.ImageVector

enum class CalculatorCategory(val title: String) {
    Trabalhista("Trabalhista"),
    Empresarial("Empresarial")
}

data class Calculator(
    val id: String,
    val title: String,
    val description: String,
    val category: CalculatorCategory,
    val icon: ImageVector
)

object CalculatorCatalog {
    val all: List<Calculator> = listOf(
        Calculator(
            id = "salario_liquido",
            title = "Salário Líquido",
            description = "INSS e IRRF mensais a partir do salário bruto",
            category = CalculatorCategory.Trabalhista,
            icon = Icons.Outlined.AttachMoney
        ),
        Calculator(
            id = "ferias",
            title = "Férias",
            description = "Valor das férias com 1/3 constitucional e descontos",
            category = CalculatorCategory.Trabalhista,
            icon = Icons.Outlined.BeachAccess
        ),
        Calculator(
            id = "decimo_terceiro",
            title = "13º Salário",
            description = "Cálculo da 1ª e 2ª parcelas com INSS e IRRF",
            category = CalculatorCategory.Trabalhista,
            icon = Icons.Outlined.CalendarMonth
        ),
        Calculator(
            id = "rescisao",
            title = "Rescisão CLT",
            description = "Saldo, aviso, férias e 13º proporcionais, FGTS",
            category = CalculatorCategory.Trabalhista,
            icon = Icons.Outlined.WorkHistory
        ),
        Calculator(
            id = "horas_extras",
            title = "Horas Extras",
            description = "Valor de horas extras com adicional configurável",
            category = CalculatorCategory.Trabalhista,
            icon = Icons.Outlined.Schedule
        ),
        Calculator(
            id = "fgts",
            title = "FGTS Mensal",
            description = "Depósito de 8% e projeção do fundo de garantia",
            category = CalculatorCategory.Trabalhista,
            icon = Icons.Outlined.AccountBalance
        ),
        Calculator(
            id = "simples_nacional",
            title = "Simples Nacional",
            description = "Alíquota efetiva por faixa de receita bruta",
            category = CalculatorCategory.Empresarial,
            icon = Icons.Outlined.Business
        ),
        Calculator(
            id = "lucro_presumido",
            title = "Lucro Presumido",
            description = "IRPJ, CSLL, PIS e Cofins sobre presunção",
            category = CalculatorCategory.Empresarial,
            icon = Icons.Outlined.ReceiptLong
        ),
        Calculator(
            id = "lucro_real",
            title = "Lucro Real",
            description = "IRPJ e CSLL sobre lucro contábil com ajustes",
            category = CalculatorCategory.Empresarial,
            icon = Icons.Outlined.ReceiptLong
        ),
        Calculator(
            id = "icms_interestadual",
            title = "ICMS Interestadual",
            description = "Difal e partilha para consumidor final não contribuinte",
            category = CalculatorCategory.Empresarial,
            icon = Icons.Outlined.SquareFoot
        ),
        Calculator(
            id = "prolabore",
            title = "Pró-labore",
            description = "INSS e IRRF sobre retirada de sócio",
            category = CalculatorCategory.Empresarial,
            icon = Icons.Outlined.Paid
        ),
        Calculator(
            id = "das",
            title = "DAS",
            description = "Documento de Arrecadação do Simples Nacional",
            category = CalculatorCategory.Empresarial,
            icon = Icons.Outlined.RequestQuote
        )
    )

    val trabalhador: List<Calculator> = all.filter { it.category == CalculatorCategory.Trabalhista }
    val empresarial: List<Calculator> = all.filter { it.category == CalculatorCategory.Empresarial }

    fun findById(id: String): Calculator? = all.firstOrNull { it.id == id }

    fun fallback(): Calculator = Calculator(
        id = "placeholder",
        title = "Em breve",
        description = "Calculadora em construção",
        category = CalculatorCategory.Trabalhista,
        icon = Icons.Outlined.Calculate
    )
}
