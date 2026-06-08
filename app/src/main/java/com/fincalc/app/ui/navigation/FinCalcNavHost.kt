package com.fincalc.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.fincalc.app.ui.home.HomeScreen
import com.fincalc.app.ui.screens.SalaryNetScreen
import com.fincalc.app.ui.screens.VacationScreen
import com.fincalc.app.ui.screens.ThirteenthScreen
import com.fincalc.app.ui.screens.RescisaoScreen
import com.fincalc.app.ui.screens.OvertimeScreen
import com.fincalc.app.ui.screens.FgtsScreen
import com.fincalc.app.ui.screens.SimplesScreen
import com.fincalc.app.ui.screens.LucroPresumidoScreen
import com.fincalc.app.ui.screens.LucroRealScreen
import com.fincalc.app.ui.screens.IcmsInterestadualScreen
import com.fincalc.app.ui.screens.ProLaboreScreen
import com.fincalc.app.ui.screens.DasScreen

object Routes {
    const val Home = "home"
    const val Calculator = "calc/{id}"
    fun calculator(id: String) = "calc/$id"
}

@Composable
fun FinCalcNavHost() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Routes.Home) {
        composable(Routes.Home) {
            HomeScreen(onCalculatorClick = { id -> navController.navigate(Routes.calculator(id)) })
        }
        composable(
            route = Routes.Calculator,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id").orEmpty()
            when (id) {
                "salario_liquido" -> SalaryNetScreen(onBack = { navController.popBackStack() })
                "ferias" -> VacationScreen(onBack = { navController.popBackStack() })
                "decimo_terceiro" -> ThirteenthScreen(onBack = { navController.popBackStack() })
                "rescisao" -> RescisaoScreen(onBack = { navController.popBackStack() })
                "horas_extras" -> OvertimeScreen(onBack = { navController.popBackStack() })
                "fgts" -> FgtsScreen(onBack = { navController.popBackStack() })
                "simples_nacional" -> SimplesScreen(onBack = { navController.popBackStack() })
                "lucro_presumido" -> LucroPresumidoScreen(onBack = { navController.popBackStack() })
                "lucro_real" -> LucroRealScreen(onBack = { navController.popBackStack() })
                "icms_interestadual" -> IcmsInterestadualScreen(onBack = { navController.popBackStack() })
                "prolabore" -> ProLaboreScreen(onBack = { navController.popBackStack() })
                "das" -> DasScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}
