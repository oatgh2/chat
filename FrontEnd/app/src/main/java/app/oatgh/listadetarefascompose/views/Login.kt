package app.oatgh.listadetarefascompose.views

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.ActivityNavigator
import androidx.navigation.NavController
import app.oatgh.listadetarefascompose.R
import app.oatgh.listadetarefascompose.utils.http.models.LoggedUser
import app.oatgh.listadetarefascompose.utils.http.models.LoginModel
import app.oatgh.listadetarefascompose.utils.http.models.Response
import com.google.gson.Gson
import kotlinx.coroutines.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.internal.http2.Http2Reader
import java.io.IOException
import java.util.Dictionary

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Login(navController: NavController) {
    val theme = MaterialTheme.colors;

    val loginValue = remember {
        mutableStateOf("")
    }
    val passwordValue = remember {
        mutableStateOf("")
    }
    val borderColorLogin = remember {
        mutableStateOf(Color.Transparent)
    }
    val borderColorPassword = remember {
        mutableStateOf(Color.Transparent)
    }

    val visibilityPasswordIcon = remember {
        mutableStateOf(R.drawable.visibility_on)
    }
    val visibilityPasswordDescription = remember {
        mutableStateOf("Ver Senha")
    }
    val visibilityPassword = remember {
        mutableStateOf(false)
    }

    val focusRequestPassword = remember {
        FocusRequester()
    }
    val context = LocalContext.current as Activity;

    Scaffold(
        content = {
            Column(

                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp, horizontal = 5.dp),
                ) {
                    OutlinedTextField(
                        value = loginValue.value,
                        enabled = true,
                        singleLine = true,
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent
                        ),
                        placeholder = {
                            Text("Digite o login")
                        },
                        modifier = Modifier
                            .background(color = Color.Transparent)
                            .width(width = 250.dp)
                            .border(
                                width = 1.7.dp,
                                color = borderColorLogin.value,
                                shape = RoundedCornerShape(percent = 20),
                            )
                            .onFocusChanged {
                                if (it.isFocused) {
                                    borderColorLogin.value = Color(color = 0xFF7FC7DD);
                                } else {
                                    borderColorLogin.value = Color.Transparent
                                }
                            },
                        readOnly = false,
                        onValueChange = {
                            loginValue.value = it;

                        },
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusRequestPassword.requestFocus()
                            }
                        ),
                    )


                }
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp, horizontal = 5.dp),
                ) {
                    OutlinedTextField(
                        value = passwordValue.value,
                        enabled = true,
                        singleLine = true,
                        maxLines = 1,
                        visualTransformation = if (visibilityPassword.value)
                            VisualTransformation.None
                        else
                            PasswordVisualTransformation(),
                        trailingIcon = {
                            Image(
                                painter = painterResource(id = visibilityPasswordIcon.value),
                                colorFilter= ColorFilter.tint(theme.onBackground),
                                contentDescription = visibilityPasswordDescription.value,
                                modifier = Modifier.combinedClickable(onClick = {
                                    if (visibilityPassword.value) {
                                        visibilityPassword.value = false;
                                        visibilityPasswordDescription.value = "Ver Senha";
                                        visibilityPasswordIcon.value = R.drawable.visibility_on;
                                    } else {
                                        visibilityPassword.value = true;
                                        visibilityPasswordDescription.value = "Ocultar Senha";
                                        visibilityPasswordIcon.value = R.drawable.visibility_off;
                                    }
                                })
                            )
                        },
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent
                        ),
                        placeholder = {
                            Text("Digite a senha")
                        },
                        modifier = Modifier
                            .background(color = Color.Transparent)
                            .width(width = 250.dp)
                            .border(
                                width = 1.7.dp,
                                color = borderColorPassword.value,
                                shape = RoundedCornerShape(percent = 20),
                            )
                            .focusRequester(focusRequestPassword)
                            .onFocusChanged {
                                if (it.isFocused) {
                                    borderColorPassword.value = Color(color = 0xFF7FC7DD);
                                } else {
                                    borderColorPassword.value = Color.Transparent
                                }
                            },
                        readOnly = false,
                        onValueChange = {
                            passwordValue.value = it
                        },
                        keyboardActions = KeyboardActions(onDone = {
                            sendForm(
                                context,
                                navController,
                                loginValue.value,
                                passwordValue.value
                            )
                        }),

                        )

                }
            }

        }
    )
}


fun sendForm(context: Activity, navigator: NavController, login: String, password: String) {

    val client = OkHttpClient()
    val urlBase = context.getString(R.string.url_api)
    val urlRequest = "${urlBase}/User/Login"
    try {
        val gson = Gson();
        val json = gson.toJson(LoginModel(login, password))
        val requestBody = RequestBody.create("application/json".toMediaTypeOrNull(), json)
        val request = Request.Builder()
            .url(urlRequest)
            .post(requestBody)
            .addHeader("Content-Type", "application/json")
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println(e)
            }

            override fun onResponse(call: Call, response: okhttp3.Response) {
                val responseString = response.body?.string()
                val res = gson.fromJson(responseString, Response::class.java);
                if (res.error == false) {
                    val shredPrefs = context.getPreferences(Context.MODE_PRIVATE);
                    with(shredPrefs.edit()) {
                        this.putString(
                            context.getString(R.string.pref_user),
                            gson.toJson(res.data)
                        )
                        this.apply()
                        Handler(Looper.getMainLooper()).post {
                            navigator.navigate("messageLob")
                        }
                    }
                } else {
                    Handler(Looper.getMainLooper()).post{
                        Toast.makeText(context, res.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
}
