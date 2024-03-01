package app.oatgh.listadetarefascompose

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import app.oatgh.listadetarefascompose.ui.theme.ListaDeTarefasComposeTheme
import app.oatgh.listadetarefascompose.utils.customs.CustomActivity
import app.oatgh.listadetarefascompose.views.Login
import app.oatgh.listadetarefascompose.views.MessageLob

class MainActivity : CustomActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observerActivity();
        setContent {
            ApplicationLayout()
        }
    }
    @Preview
    @Composable()
    fun ApplicationLayout(){
        ListaDeTarefasComposeTheme {
            val navController: NavHostController = rememberNavController();
            val loggedUser = getUser();
            var startDestination = "";

            if (loggedUser != null) {
                startDestination = "messageLob";
            } else {
                startDestination = "login";
            }
            NavHost(navController = navController, startDestination = startDestination) {
                composable(
                    route = "messageLob",
                ) {
                    MessageLob(navController)
                }
                composable(
                    route = "login",
                ) {
                    Login(navController)
                }
            }
        }
    }
}

