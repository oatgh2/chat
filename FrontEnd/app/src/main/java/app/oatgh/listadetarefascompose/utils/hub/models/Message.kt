package app.oatgh.listadetarefascompose.utils.hub.models

import java.util.UUID

data class Message(var id: UUID, var user: String, var text: String, var isAudio: Boolean)
