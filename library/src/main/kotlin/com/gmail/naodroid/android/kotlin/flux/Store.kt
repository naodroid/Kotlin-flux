package com.gmail.naodroid.android.kotlin.flux

import java.lang.ref.WeakReference
import java.util.*
import kotlin.reflect.jvm.internal.impl.descriptors.impl.SubpackagesScope

/**
 * Created by nao on 15/08/25.
 */

public abstract class Store {
    //holding <relatedObject, handlerLambda>
    private val subscriptionList = ArrayList<StoreHolder>()
            
    fun addObserver(target : Any, handler : () -> Unit) : Subscription {
        val holder = StoreHolder(target, handler)
        synchronized(this) {
            this.subscriptionList.add(holder)
        }
        return Subscription {
            this.subscriptionList.remove(holder)
        }
    }

    public fun notifyChange() {
        synchronized(this) {
            val count = subscriptionList.size()
            var index = count - 1
            while (index >= 0) {
                val holder = subscriptionList.get(index)
                if (holder.available) {
                    holder.handler()
                } else {
                    subscriptionList.remove(index)
                }
                index--
            }
        }
    }
}

//
public fun <T : Store> Any.subscribeStore(store : T, handler : (T) -> Unit) : Subscription {
    return store.addObserver(this) {
        handler(store)
    }
}

//
private class StoreHolder(target : Any, handler : () -> Unit) {
    private val target : WeakReference<Any>
    val handler : () -> Unit
    
    val available : Boolean
        get() = (this.target.get() != null)
    
    init {
        this.target = WeakReference(target)
        this.handler = handler
    }
}

