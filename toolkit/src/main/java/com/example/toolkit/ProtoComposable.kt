package com.example.toolkit

import androidx.compose.runtime.ComposableTargetMarker

@Retention(AnnotationRetention.BINARY)
@ComposableTargetMarker(description = "Proto Composable")
@Target(
    AnnotationTarget.FILE,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.TYPE,
    AnnotationTarget.TYPE_PARAMETER,
)
public annotation class ProtoComposable
