package com.gmail.naodroid.android.kotlin.flux

/**
 * Created by nao on 15/08/25.
 */

public class Subscription(unsubscribe : () -> Unit) { 
    
    private val unsubscribeBlock : () -> Unit;
    
    init {
        this.unsubscribeBlock = unsubscribe
    }
    
    fun unsubscribe() {
        this.unsubscribeBlock()
    }
}