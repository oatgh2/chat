package app.oatgh.listadetarefascompose.views

import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.media.MediaRecorder.AudioEncoder
import android.media.MediaRecorder.AudioSource
import android.media.MediaRecorder.OutputFormat
import android.net.Uri
import android.util.Base64
import android.widget.Toast
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import kotlin.concurrent.thread
import app.oatgh.listadetarefascompose.R
import app.oatgh.listadetarefascompose.utils.customs.CustomActivity
import app.oatgh.listadetarefascompose.utils.hub.models.Message
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.microsoft.signalr.HubConnectionState
import kotlinx.coroutines.*
import java.io.*


private fun manipuleEvents(connection: HubConnection, state: Map<String, Any>) {


}

private fun dataToMessage(data: Any?): List<Message> {
    val json = GsonBuilder().create();
    val serializedContent = json.toJson(data);
    val listOfMessages: List<Message> = json.fromJson(
        serializedContent,
        arrayListOf<Message>().javaClass
    )
    return listOfMessages;
}


private fun getLastVisibleIndex(listState: LazyListState): Int {
    val layoutInfo = listState.layoutInfo
    val lastIndex = layoutInfo.totalItemsCount - 1
    var lastVisibleIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: lastIndex

    if (lastVisibleIndex > lastIndex) {
        lastVisibleIndex = lastIndex
    }

    return lastVisibleIndex
}

fun fileToBytes(tempFile: File): ByteArray {
    val inputStream = FileInputStream(tempFile)
    val outputStream = ByteArrayOutputStream()
    val buffer = ByteArray(1024)
    var bytesRead: Int
    try {
        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
            outputStream.write(buffer, 0, bytesRead)
        }
    } catch (e: IOException) {
        e.printStackTrace()
    } finally {
        inputStream.close()
    }
    return outputStream.toByteArray()
}

fun byteArrayToBase64(byteArray: ByteArray): String {
    return Base64.encodeToString(byteArray, Base64.DEFAULT)
}

fun fileToBase64(tempFile: File): String {
    val byteArray = fileToBytes(tempFile)
    return byteArrayToBase64(byteArray)
}

fun grantedPermissions(context: CustomActivity): Boolean {
    return ContextCompat.checkSelfPermission(
        context,
        android.Manifest.permission.RECORD_AUDIO
    ) != PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(
        context,
        android.Manifest.permission.READ_EXTERNAL_STORAGE
    ) != PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(
        context,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    ) != PackageManager.PERMISSION_GRANTED
}

@Composable
fun MessageLob(navController: NavController) {

    val context = LocalContext.current as CustomActivity;
    val theme = MaterialTheme.colors;
    val connection: HubConnection =
        HubConnectionBuilder.create("${context.getString(R.string.url_api)}/hub")
            .withHeader("Authorization", "Bearer ${context.getJwt()}")
            .build();


    val isConnected = remember { mutableStateOf(false) }
    val messages = remember { mutableStateListOf<Message>() }
    val scrollState = rememberLazyListState()
    val messageKeyboardState = remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    var isRecording by remember { mutableStateOf(false) }
    var audioString64 by remember { mutableStateOf("") }
    var startTimeMillis: Long = 0

    var havePermissions by remember {
        mutableStateOf(grantedPermissions(context))
    }
    var mediaRecorder: MediaRecorder? = null


    val outputStream = File.createTempFile("tempAudio", ".amr")


    connection.on("get_all_messages", { data ->
        val json = GsonBuilder().create();
        val serializedContent = json.toJson(data);
        val listOfMessages: List<Message> = json.fromJson(
            serializedContent,
            object : TypeToken<List<Message>>() {}.type
        )
        messages.clear();
        messages.addAll(listOfMessages)
        coroutineScope.launch {
            scrollState.animateScrollToItem(if (messages.lastIndex == -1) 0 else messages.lastIndex)
        }
    }, arrayListOf<Message>().javaClass)

    connection.on("get_message", { data ->
        messages.add(data);
    }, Message::class.java);

    DisposableEffect(key1 = connection) {
        val thWait = thread {
            while (true) {
                isConnected.value = connection.connectionState == HubConnectionState.CONNECTED
                if (!isConnected.value) messages.clear()

                try {
                    Thread.sleep(350)
                } catch (e: InterruptedException) {
                    break;
                }
            }
        }

        onDispose {
            thWait.interrupt()
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                backgroundColor = theme.background,
                title = {
                    @OptIn(ExperimentalFoundationApi::class)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isConnected.value) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Column {
                                    Text(text = "Logado", color = theme.onBackground)
                                    Text(
                                        text = if (context.getUser() != null) context.getUser()!!
                                            .UserName else "",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.ExtraLight
                                    )
                                }
                                ExtendableMenu(navController)
                            }
                            Image(
                                painter = painterResource(id = R.drawable.stop_btn),
                                contentDescription = "Desconectar",
                                colorFilter = ColorFilter.tint(
                                    theme.onBackground
                                ),
                                modifier = Modifier.combinedClickable {
                                    connection.stop();
                                }
                            )
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Column {
                                    Text(text = "Desconectado", color = theme.onBackground)
                                    Text(
                                        text = if (context.getUser() != null) context.getUser()!!
                                            .UserName else "",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.ExtraLight
                                    )
                                }
                                ExtendableMenu(navController)
                            }
                            Image(
                                modifier = Modifier.combinedClickable {
                                    connection.start().blockingAwait();
                                    if (messages.isEmpty()) connection.invoke("GetMessages")
                                },
                                colorFilter = ColorFilter.tint(
                                    theme.onBackground
                                ),
                                imageVector = ImageVector.vectorResource(id = R.drawable.play_btn),
                                contentDescription = "Conectar"
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (getLastVisibleIndex(scrollState) < messages.lastIndex)
                FloatingActionButton(
                    modifier = Modifier
                        .width(34.2.dp)
                        .height(34.2.dp),
                    backgroundColor = theme.primary,
                    onClick = {
                        coroutineScope.launch {
                            scrollState.animateScrollToItem(index = messages.lastIndex)
                        }
                    },
                    content = {
                        Image(
                            painter = painterResource(id = R.drawable.down_double_arrow),
                            colorFilter = ColorFilter.tint(theme.background),
                            contentDescription = "Descer para o fim"
                        )
                    })
        },
        content = {
            Box(
                modifier = Modifier
                    .padding(bottom = it.calculateBottomPadding())
                    .background(theme.background)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(theme.background),
                    state = scrollState,
                    verticalArrangement = Arrangement.Top,

                    ) {
                    items(messages.size) { index ->
                        val message = messages[index]
                        if (!message.isAudio) {
                            MessageBubble(text = message.text, user = message.user)
                        } else {
                            AudioSlide(audioId = message.text, user = message.user)
                        }
                    }
                }
            }

        },
        bottomBar = {
            Row {
                @OptIn(ExperimentalFoundationApi::class)
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = messageKeyboardState.value,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        textColor = theme.onBackground,
                        backgroundColor = theme.background
                    ),
                    onValueChange = { messageKeyboardState.value = it },
                    placeholder = { if(audioString64.isEmpty()) Text("Digite o texto")
                    else  Text("Audio capturado")},
                    readOnly = audioString64.isNotEmpty(),
                    trailingIcon = {
                        Row {
                            Image(
                                painter = painterResource(
                                    id = if (!isRecording) R.drawable.mic_button
                                    else R.drawable.stop_btn
                                ),
                                colorFilter = ColorFilter.tint(theme.onBackground),
                                contentDescription = "Enviar Audio",
                                modifier = Modifier.combinedClickable(onClick = {
                                    if (isRecording) {
                                        isRecording = false;
                                        mediaRecorder!!.apply {
                                            stop()
                                            audioString64 = fileToBase64(outputStream)
                                            release()
                                        }
                                    } else {
                                        if (ContextCompat.checkSelfPermission(
                                                context,
                                                android.Manifest.permission.RECORD_AUDIO
                                            ) != PackageManager.PERMISSION_GRANTED
                                        ) {
                                            val permissions = arrayOf(
                                                android.Manifest.permission.RECORD_AUDIO,
                                                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                                android.Manifest.permission.READ_EXTERNAL_STORAGE
                                            )
                                            ActivityCompat.requestPermissions(
                                                context,
                                                permissions,
                                                101
                                            )
                                        } else {
                                            if (mediaRecorder == null)
                                                mediaRecorder = MediaRecorder(context)
                                            isRecording = true;
                                            mediaRecorder!!.apply {
                                                setAudioSource(AudioSource.MIC)
                                                setOutputFormat(OutputFormat.THREE_GPP)
                                                setAudioEncoder(AudioEncoder.AMR_NB)
                                                setOutputFile(outputStream.absolutePath)
                                                prepare()
                                                start()
                                            }
                                        }

                                    }
                                })
                            )
                            Image(
                                painter = painterResource(id = R.drawable.send_btn),
                                colorFilter = ColorFilter.tint(theme.onBackground),
                                contentDescription = "Enviar",
                                modifier = Modifier.combinedClickable(onClick = {
                                    if (isConnected.value) {
                                        if (audioString64.isNotEmpty()) {
                                            connection.invoke(
                                                "SendAudio",
                                                audioString64
                                            ).blockingAwait()
                                            coroutineScope.launch {
                                                scrollState.animateScrollToItem(
                                                    if (messages.lastIndex == -1) 0
                                                    else messages.lastIndex
                                                )
                                                audioString64 = ""
                                            }

                                            messageKeyboardState.value = ""
                                        } else {
                                            if (messageKeyboardState.value.isNotEmpty()) {
                                                connection.invoke(
                                                    "SendMessage",
                                                    messageKeyboardState.value
                                                ).blockingAwait()
                                            }
                                        }


                                    } else {
                                        if (!isConnected.value)
                                            Toast.makeText(
                                                context, "Chat desconectado",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                    }
                                })
                            )
                        }

                    },
                    keyboardActions = KeyboardActions(
                        onAny = {

                        }
                    )
                )
            }
        }
    )
}


@Composable
fun MessageBubble(text: String, user: String) {
    val context = LocalContext.current as CustomActivity;
    val theme = MaterialTheme.colors;
    val localUser = context.getUser()!!;
    val bubbleColor = if (localUser.UserName == user) theme.primary else theme.secondary;
    val bubblePos = if (localUser.UserName == user) Arrangement.End else Arrangement.Start;
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 200.dp)
            .padding(10.dp),
        horizontalArrangement = bubblePos,
    ) {
        Surface(
            modifier = Modifier
                .widthIn(max = 200.dp)
                .fillMaxWidth(),
            elevation = 4.dp,
            shape = RoundedCornerShape(8.dp),
            color = bubbleColor
        ) {
            Column(
                modifier = Modifier
                    .wrapContentWidth()
                    .fillMaxWidth()
                    .background(Color.Transparent)
                    .padding(10.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(text = user, color = theme.primaryVariant, fontWeight = FontWeight.Bold)
                Text(text = text, color = theme.onBackground, fontWeight = FontWeight.W400)
            }
        }
    }
}


@Composable
fun AudioSlide(audioId: String, user: String) {
    val context = LocalContext.current as CustomActivity;
    val theme = MaterialTheme.colors;
    val localUser = context.getUser()!!;
    val bubbleColor = if (localUser.UserName == user) theme.primary else theme.secondary;
    val bubblePos = if (localUser.UserName == user) Arrangement.End else Arrangement.Start;

    val audioSource = MediaPlayer();
    audioSource.apply {
        setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()
        )
        setDataSource(context, Uri
            .parse("${context.getString(R.string.url_api)}/File/Audio/${audioId}"))
        prepare()
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 200.dp)
            .padding(10.dp),
        horizontalArrangement = bubblePos,
    ) {
        Surface(
            modifier = Modifier
                .widthIn(max = 200.dp)
                .fillMaxWidth(),
            elevation = 4.dp,
            shape = RoundedCornerShape(8.dp),
            color = bubbleColor
        ) {
            Column(
                modifier = Modifier
                    .wrapContentWidth()
                    .fillMaxWidth()
                    .background(Color.Transparent)
                    .padding(10.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(text = user, color = theme.primaryVariant, fontWeight = FontWeight.Bold)
                Slider(
                    value = audioSource.currentPosition.toFloat(),
                    valueRange = 0f..1f,
                    onValueChange = {

                    }
                )
            }
        }
    }
}

@Composable
fun ExtendableMenu(navController: NavController) {
    val context = LocalContext.current as CustomActivity;
    val theme = MaterialTheme.colors

    var isExpanded by remember { mutableStateOf(false) }
    val transition = updateTransition(targetState = isExpanded, label = "Expand Transition")
    var rotation by remember { mutableStateOf(0f) }

    if (isExpanded) rotation = 180f else rotation = 0f

    IconButton(onClick = { isExpanded = !isExpanded }) {
        Icon(
            painter = painterResource(id = R.drawable.down_single_arrow),
            contentDescription = "",
            modifier = Modifier.rotate(rotation)
        )
    }
    if (isExpanded) {
        DropdownMenu(
            modifier = Modifier
                .background(theme.background),
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false }) {
            DropdownMenuItem(onClick =
            {
                context.logout()
                navController.navigate("login")
            }) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Sair", color = theme.onBackground)
                    Image(
                        painter = painterResource(id = R.drawable.exit_btn),
                        colorFilter = ColorFilter.tint(theme.onBackground),
                        contentDescription = "Sair"
                    )
                }
            }
        }
    }
}

