# Kotlin-flux
Flux library for android-kotlin

# Usage

Copy sources in library folder.

## 1.Create Action subclass

Create `Action` subclass.

```kotlin
class UpdateCountAction(val count : String) : Action() {
}
```

## 2. Create Store subclass

Create `Store` subclass, and subscribe actions to update values.

When value changed, call `notifyChanged()` method for noticing update to subscribers.

```kotlin
object Counter : Store() {
	var count : Int = 0
	   private set(valule) {
	     $count = value
	     notifyChanged()
	   }
   
    init {
    	//subscribe action
    	subscribeAction(UpdateCountAction::class) {
    		this.count = it.count
    	}
    }
}
```

## 3. Subscribe Store update

In Activity (or Fragment, Views), Need to subscribe store for updating views.

```kotlin
class MyActivity : Activity() {
	private var subscription : Subscription? = null

	override fun onResume() {
		super.onResume()
		this.subscription = subscribeStore(Counter) {
			updateViews()
		}
	}
	override fun onPause() {
		//unsubscribe store updating
		this.subscription?.unsubscrise()
		super.onPause()
	}


	fun updateViews() {
		//do anything
	}
}
```

## 4. Dispatch Action

When event happend(eg. ClickEvent), Create and dispatch `Action object` to update store values.

```kotlin
fun onClick(view : View) {
	val next = Counter.count + 1
	UpdateCountAction(next).dispatch()
}
```

Then the flux pattern will be completed.
(Event happened - Dispatching action - Store updating - view updating)









