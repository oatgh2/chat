package app.oatgh.listadetarefascompose.utils.customs

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.PersistableBundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import app.oatgh.listadetarefascompose.utils.http.models.LoggedUser
import app.oatgh.listadetarefascompose.R
import app.oatgh.listadetarefascompose.utils.http.models.Response
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

abstract class CustomActivity : ComponentActivity() {

    protected fun observerActivity(){
        CoroutineScope(Dispatchers.IO).launch{
            while (true){
                observeDeslogRequest()
                delay(1000)
            }
        }
    }


    private fun observeDeslogRequest(){
        val shredPrefs = this.getPreferences(Context.MODE_PRIVATE);
        val wantToDeslog = shredPrefs.getBoolean(
            getString(app.oatgh.listadetarefascompose.R.string.deslog_request), false
        )
        if (wantToDeslog) {
            val urlBase = getString(R.string.url_api)
            val urlRequest = "${urlBase}/User/DeslogUser"
            val request = Request.Builder()
                .url(urlRequest)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer ${
                    shredPrefs.getString(
                        getString(app.oatgh.listadetarefascompose.R.string.deslog_user_request), ""
                    )
                }")
                .build()

            val client = OkHttpClient()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    println(e)
                }

                override fun onResponse(call: Call, response: okhttp3.Response) {
                    try {
                        val responseString = response.body?.string()
                        val gson = Gson();
                        val res = gson.fromJson(responseString, Response::class.java);
                        if (res.error == false) {
                            with(shredPrefs.edit()) {
                                this.putString(
                                    getString(app.oatgh.listadetarefascompose.R.string.deslog_user_request),
                                    ""
                                )
                                this.putBoolean(
                                    getString(app.oatgh.listadetarefascompose.R.string.deslog_request),
                                    false
                                );
                                this.apply()
                            }
                        }
                    }catch (e: java.lang.Exception){

                    }

                }
            })


        }
    }

    public fun getUser(): LoggedUser? {
        val gson = GsonBuilder().create()
        val shredPrefs = this.getPreferences(Context.MODE_PRIVATE);
        val loggedUserSerialized: String? = shredPrefs.getString(
            super.getString(R.string.pref_user), null
        );

        if (loggedUserSerialized != null)
            return gson.fromJson(loggedUserSerialized, LoggedUser::class.java);
        else
            return null
    }

    public fun getJwt(): String? {
        val user = getUser()
        if (user != null)
            return user.JWT
        else
            return null
    }

    public fun logout() {
        val shredPrefs = this.getPreferences(Context.MODE_PRIVATE);
        with(shredPrefs.edit()) {
            val user = getUser();
            this.putString(getString(R.string.deslog_user_request), user!!.JWT)
            this.putBoolean(getString(R.string.deslog_request), true);
            this.putString(getString(R.string.pref_user), "")
            this.apply()
        }
    }


}