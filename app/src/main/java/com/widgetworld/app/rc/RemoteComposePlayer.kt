package com.widgetworld.app.rc

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.remote.creation.compose.capture.RememberRemoteDocumentInline
import androidx.compose.remote.creation.compose.layout.RemoteBox
import androidx.compose.remote.creation.compose.layout.RemoteComposable
import androidx.compose.remote.creation.compose.modifier.RemoteModifier
import androidx.compose.remote.creation.compose.modifier.height
import androidx.compose.remote.creation.compose.modifier.width
import androidx.compose.remote.creation.compose.state.rdp
import androidx.compose.remote.player.compose.RemoteDocumentPlayer
import androidx.compose.remote.player.core.RemoteDocument
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.DpSize

@SuppressLint("RestrictedApi")
@Composable
@Suppress("RestrictedApiAndroidX")
fun RcDocumentPlayer(
    size: DpSize,
    content: @RemoteComposable @Composable () -> Unit
) {
    var documentState by remember { mutableStateOf<RemoteDocument?>(null) }

    Box(
        modifier = Modifier
            .size(size)
            .background(Color.White)
            .semantics {
                contentDescription = "${System.currentTimeMillis()}"
            }) {
        @Suppress("COMPOSE_APPLIER_CALL_MISMATCH") // b/446706254
        RememberRemoteDocumentInline(
            onDocument = { doc ->
                documentState = RemoteDocument(doc)
            }
        ) {
            RemoteBox(modifier = RemoteModifier.width(300.rdp).height(200.rdp)) {
                content.invoke()
            }
        }

        if (documentState != null) {
            RemoteDocumentPlayer(
                document = documentState!!.document,
                300,
                200,
                modifier = Modifier.fillMaxSize(),
                0,
                onNamedAction = { _, _, _ -> },
            )
        }
    }
}