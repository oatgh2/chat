package app.oatgh.listadetarefascompose.utils.http.models

import com.google.gson.annotations.SerializedName

data class LoggedUser(
                      @SerializedName(value= "id")
                      var Id: Int,
                      @SerializedName(value= "userName")
                      var UserName: String,
                      @SerializedName(value= "jwt")
                      var JWT: String)