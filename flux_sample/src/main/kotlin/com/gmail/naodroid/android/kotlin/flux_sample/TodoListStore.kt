package com.gmail.naodroid.android.kotlin.flux_sample

import com.gmail.naodroid.android.kotlin.flux.Dispatcher
import com.gmail.naodroid.android.kotlin.flux.Store
import com.gmail.naodroid.android.kotlin.flux.subscribeAction
import java.util.*

/**
 * Created by nao on 15/08/28.
 */

public object TodoListStore : Store() {
    private val todoListInner = ArrayList<TodoStore>()   
    
    private val dummy = Array(0) {TodoStore("")}
    
    public val todoList : Array<TodoStore>
        get() = this.todoListInner.toArray(dummy)
    
    init {
        subscribeAction(TodoActions.Add::class, ::addTodo of this)
        subscribeAction(TodoActions.Delete::class, ::deleteTodo of this)
    }
    
    //action handlers---------------------
    private fun addTodo(action : TodoActions.Add) {
        val todo = TodoStore(action.title)
        todoListInner.add(todo)
        notifyChange()
    }
    private fun deleteTodo(action : TodoActions.Delete) {
        val todo = findTodoFromId(action.id)
        if (todo == null) {
            return
        }
        this.todoListInner.remove(todo)
        notifyChange()
    }
    
    //utility method----------------------
    private fun findTodoFromId(id : Int) : TodoStore? {
        for (todo in todoListInner) {
            if (todo.id == id) {
                return todo
            }
        }
        return null
    }
    
    
}

