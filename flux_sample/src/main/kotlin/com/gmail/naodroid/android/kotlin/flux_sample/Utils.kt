package com.gmail.naodroid.android.kotlin.flux_sample

/**
 * Created by nao on 15/08/28.
 */

/**
 * shorthand of lambda, to designate method directly
 * ex:
 * 
 * fun onClick(handler : (View) -> Unit) {...}
 * 
 * class MyActivity {
 *    fun onCreate() {
 *       //usually
 *       onClick {
 *          onViewClick(it)   //using lambda for simple method calling
 *       }
 *       
 *       //shorthand
 *       onClick(::onViewClick of this)
 *    }
 *    
 *    fun onViewClick(view : View) {
 *       //...
 *    }
 * }
 */
fun <A, B, C> (A.(B)->C).of(receiver: A): (B)->C = {
    receiver.this(it)
}
