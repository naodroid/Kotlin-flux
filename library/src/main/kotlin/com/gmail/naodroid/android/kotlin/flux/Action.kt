package com.gmail.naodroid.android.kotlin.flux

import kotlin.reflect.KClass

/**
 * Created by nao on 15/08/26.
 */

public abstract class Action {
    public fun dispatch() {
        Dispatcher.dispatch(this)
    }   
}

