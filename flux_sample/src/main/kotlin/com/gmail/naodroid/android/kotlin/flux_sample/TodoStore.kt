package com.gmail.naodroid.android.kotlin.flux_sample

import com.gmail.naodroid.android.kotlin.flux.Store
import com.gmail.naodroid.android.kotlin.flux.subscribeAction

/**
 * Created by nao on 15/08/28.
 */

public class TodoStore(title : String) : Store() {
    val id = TodoIdHolder.nextId()
    
    var title = title
        private set(value) {
            $title = value
            notifyChange()
        }
    var done = false
        private set(value) {
            $done = value
            println("DONE:" + $done)
            notifyChange()
        }
    
    init {
        subscribeAction(TodoActions.UpdateDone::class) {
            val self = this@TodoStore
            println("UPDATE_ACTION:" + it.id + " " + self.id)
            if (it.id == self.id) {
                self.done = it.done
            }
        }
    }
    
    //
    private object TodoIdHolder {
        private var overAllId = 0
        fun nextId() : Int {
            return ++overAllId
        }
    }
}