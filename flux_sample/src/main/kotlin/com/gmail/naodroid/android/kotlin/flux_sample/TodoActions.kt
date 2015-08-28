package com.gmail.naodroid.android.kotlin.flux_sample

import com.gmail.naodroid.android.kotlin.flux.Action

/**
 * Created by nao on 15/08/28.
 */
public object TodoActions {
    public class Add(val title : String) : Action() {
    }
    public class Delete(val id : Int) : Action() {
    }
    public class UpdateDone(val id : Int, val done : Boolean) : Action() {
    }
}

