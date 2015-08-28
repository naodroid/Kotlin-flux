package com.gmail.naodroid.android.kotlin.flux_sample

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.gmail.naodroid.android.kotlin.flux.Subscription
import com.gmail.naodroid.android.kotlin.flux.subscribeStore
import kotlin.properties.Delegates

/**
 * Created by nao on 15/08/28.
 */

public class TodoActivity : Activity() {

    //views
    private val mEditText by Delegates.lazy { findViewById(R.id.todo_edit_text) as EditText }
    private val mListView by Delegates.lazy { findViewById(R.id.todo_list) as ListView }
    
    private var mAdapter = TodoAdapter(this)
    
    private var subscription : Subscription? = null
    //------------------
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.todo_activity)
        mListView.setAdapter(mAdapter)
        
        mListView.setOnItemClickListener { adapterView, view, position, id ->
            val item = mAdapter.todoList.get(position)
            TodoActions.UpdateDone(item.id, !item.done).dispatch()
        }
        
    }

    override fun onResume() {
        super.onResume()
        updateList(TodoListStore)
        this.subscription = subscribeStore(TodoListStore, ::updateList of this)
    }

    override fun onPause() {
        this.subscription?.unsubscribe()
        super.onPause()
    }
    
    //Events-------------------
    
    public fun onAddClick(view : View) {
        val text = mEditText.getText().toString()
        if (text.length() == 0) {
            return
        }
        mEditText.setText("")
        TodoActions.Add(text).dispatch()
    }
    
    private fun updateList(store : TodoListStore) {
        mAdapter.todoList = TodoListStore.todoList
    }
    
    
    
}
//------------------------------------------------
//adapter
class TodoAdapter(val context : Context) : BaseAdapter() {
    var todoList : Array<TodoStore> = Array(0, {TodoStore("")})
        set(value) {
            $todoList = value
            notifyDataSetChanged()
        }
    
    
    
    //override
    override fun getCount(): Int {
        return todoList.count()
    }

    override fun getView(position: Int, convertedView: View?, parent: ViewGroup): View? {
        val view = convertedView ?: LayoutInflater.from(context).inflate(R.layout.todo_cell, parent, false)
        
        val holder = view.getTag() as? TodoHolder ?: TodoHolder(view)
        view.setTag(holder)
        
        holder.item = todoList.get(position)
        
        return view
    }

    override fun getItem(position: Int): Any? {
        return todoList.get(position)
    }

    override fun getItemId(position: Int): Long {
        return todoList.get(position).id.toLong()
    }
}

//-------------------------------
//list view cell holder
class TodoHolder(val view : View) {
    var item : TodoStore? = null
        set(value) {
            $item = value
            update(value)    
        }
    var subscription : Subscription? = null
    
    private fun update(todo : TodoStore?) {
        unsubscribeIfNeeded()
        if (todo == null) {
            return
        }
        subscribeCurrentItem()
        updateView(todo)
    }
    
    private fun subscribeCurrentItem() {
        val item = this.item
        if (item == null) {
            return
        }
        this.subscription = this.subscribeStore(item) {
            updateView(it)
        }
    }
    private fun unsubscribeIfNeeded() {
        val old = subscription
        if (old != null) {
            old.unsubscribe()
        }
        subscription = null
    }
    
    private fun updateView(todo : TodoStore) {
        val title = view.findViewById(R.id.todo_cell_title) as? TextView
        val checked = view.findViewById(R.id.todo_cell_checked) as? CheckBox
        
        title?.setText(todo.title)
        checked?.setChecked(todo.done)
    }
}


