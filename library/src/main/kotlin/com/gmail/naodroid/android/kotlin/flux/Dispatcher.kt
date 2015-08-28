package com.gmail.naodroid.android.kotlin.flux

import android.os.Handler
import android.os.Looper
import java.lang.ref.WeakReference
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.jvm.java

/**
 * Created by nao on 15/08/26.
 */

public fun <T : Action> Any.subscribeAction(action : KClass<T>, handler : (T) -> Unit) {
    Dispatcher.subscribe(this, action, handler)
}

public object Dispatcher {
    private val map = WeakHashMap<String, ArrayList<ActionHolder>>()
    private var dispatchingList : ArrayList<ActionHolder>? = null
    private var dispatchingAction : Action? = null
    
    public fun <T : Action> subscribe(target : Any, action : KClass<T>, handler : (T) -> Unit) : Subscription {
        synchronized(this) {
            val holder = ActionHolder(target) {
                val p = it as? T
                if (p != null) {
                    handler(p)
                }
            }
            val act = action.java as Class<Any>
            val list = getListForClass(act, true)
            list?.add(holder)
            
            return Subscription {
                list?.remove(holder)
            }
        }
    }
    public fun dispatch(action : Action) {
        //call on main thread
        if (Looper.getMainLooper().getThread() != Thread.currentThread()) {
            Handler().post {
                dispatch(action)
            }
            return
        }
        if (this.dispatchingAction != null) {
            throw IllegalStateException("Can't dispatch action while dispatching other action:" + action);
        }

        val list = getListForClass(action.javaClass, false)
        if (list == null) {
            return;
        }
        list.forEach { it.dispatched = false }
        
        this.dispatchingAction = action;
        this.dispatchingList = list;

        var index = list.count() - 1
        while (index >= 0) {
            val holder = list.get(index)
            if (holder == null || !holder.available) {
                list.remove(index)
            } else if (!holder.dispatched) {
                holder.dispatch(action)
            }
            index--
        }
        
        this.dispatchingAction = null
        this.dispatchingList = null
    }
    
    public fun waitFor(clazz : KClass<Any>) {
        val list = this.dispatchingList
        val action = this.dispatchingAction
        if (list == null || action == null) {
            return
        }
        
        var index = list.count() - 1
        while (index >= 0) {
            val holder = list.get(index)
            if (holder == null || !holder.available) {
                list.remove(index)
            } else if (!holder.dispatched) {
                val obj = holder.target.get()
                if (obj != null) {
                    try {
                        val c = clazz.javaClass.cast(obj)
                        holder.dispatch(action)
                    } catch (e : ClassCastException) {
                    }
                }
            }
            index--
        }
    }
    
    
    private synchronized fun getListForClass(jClass : Class<Any>, create : Boolean = false) : ArrayList<ActionHolder>? {
        val name = jClass.getName()
        val list = map[name]
        if (list != null || !create) {
            return list
        }
        val newList = ArrayList<ActionHolder>()
        map[name] = newList
        return newList
    }
    
}



private class ActionHolder(target : Any, handler : (Any) -> Unit) {
    var dispatched = false
    val target : WeakReference<Any>
    val handler : (Any) -> Unit
    
    val available : Boolean
        get() {
            return target.get() != null
        }
    
    init {
        this.target = WeakReference(target)
        this.handler = handler
    }
    
    fun dispatch(action : Action) {
        if (this.dispatched) {
            return
        }
        val t = this.target.get()
        if (t == null) {
            return
        }
        this.dispatched = true
        this.handler(action)
    }
}


