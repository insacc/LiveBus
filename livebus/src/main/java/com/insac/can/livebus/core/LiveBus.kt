package com.insac.can.livebus.core

import com.insac.can.livebus.utils.assertMainThread
import com.insac.can.livebus.utils.exceptionWrapper

class LiveBus {
    companion object {
        private var mInstance: LiveBus? = null
        private var mEvents: HashMap<String, LiveEventBase<out Any?>> = HashMap()
        private const val CAST_EXCEPTION_MESSAGE = "LiveEvent casting exception! LiveEventBase saved on the " +
                "bus doesn't have the LiveEvent type"

        fun getInstance(): LiveBus {
            if (mInstance == null) {
                mInstance = LiveBus()
            }

            return mInstance!!
        }
    }

    /**
     * Removes the event equals to @param liveEvent from the Bus.
     * @return true if the Bus contains the `LiveEvent` passed to the function,
     * false otherwise
     */
    fun <T> removeEvent(liveEvent: LiveEventBase<T>): Boolean {
        if (!mEvents.containsValue(liveEvent)) return false

        for (entry in mEvents.entries) {
            if (entry.value != liveEvent) continue

            mEvents.remove(entry.key)
            return true
        }

        return false
    }

    /**
     * Removes the live event specified by the tag
     * @return true if an event with the passed tag exists and removed, false otherwise
     */
    fun removeEventByTag(tag: String): Boolean {
        mEvents.remove(tag) ?: return false

        return true
    }

    private fun <T, K> setLiveEventValue(tag: String, eventValue: T, liveEventType: Class<K>) {
        assertMainThread(liveEventType.name)

        setValue(tag, eventValue, liveEventType, fun(liveEvent, eventValue) {
            liveEvent?.value = eventValue
        })
    }

    private fun <T, K> postLiveEventValue(tag: String, eventValue: T, liveEventType: Class<K>) {
        setValue(tag, eventValue, liveEventType, fun(liveEvent, eventValue) {
            liveEvent?.postValue(eventValue)
        })
    }

    private fun <T, K> setValue(tag: String, eventValue: T, liveEventType: Class<K>,
                                func: (liveEvent: LiveEventBase<T>?, value: T) -> Unit) {
        if (!mEvents.contains(tag)) {
            val liveEvent = createLiveEvent(liveEventType)
            mEvents[tag] = liveEvent
        }

        exceptionWrapper(fun() {
            func(mEvents[tag] as LiveEventBase<T>, eventValue)
        }, CAST_EXCEPTION_MESSAGE)
    }

    /**
     * This function creates a LiveEvent object and adds it to the
     * mEvents hashMap if necessary, otherwise it just updates the event's value
     *
     * @param tag The tag for the event
     * @param eventValue the value to be set to the event
     */
    @Deprecated(
            message = "This function is deprecated as of version 0.3.0. Please use setLiveEventValue(..) " +
                    "instead ",
            replaceWith = ReplaceWith("setLiveEventValue(tag, eventValue)")
    )
    fun <T> postLiveEvent(tag: String, eventValue: T) {
        setLiveEventValue(tag, eventValue, LiveEvent::class.java)
    }

    /**
     * This function creates a `SingleLiveEvent` object and adds it to the
     * mEvents hashMap if necessary, otherwise it just updates the event's value
     *
     * @param tag The tag for the event
     * @param eventValue the value to be set to the event
     */

    @Deprecated(
            message = "This function is deprecated as of version 0.3.0. Please use setSingleLiveEventValue(..) " +
                    "instead ",
            replaceWith = ReplaceWith("setSingleLiveEventValue(tag, eventValue)")
    )
    fun <T> postSingleEvent(tag: String, eventValue: T) {
        setLiveEventValue(tag, eventValue, SingleLiveEvent::class.java)
    }

    /**
     * This function creates a `StickySingleLiveEvent` object and adds it to the
     * mEvents hashMap if necessary, otherwise it just updates the event's value
     *
     * @param tag The tag for the event
     * @param eventValue the value to be set to the event
     */

    @Deprecated(
            message = "This function is deprecated as of version 0.3.0. Please use setStickySingleLiveEventValue(..) " +
                    "instead ",
            replaceWith = ReplaceWith("setStickySingleLiveEventValue(tag, eventValue)")
    )
    private fun <T> postStickySingleEvent(tag: String, eventValue: T) {
        setLiveEventValue(tag, eventValue, StickySingleLiveEvent::class.java)
    }

    /**
     * This function creates a `StickyLiveEvent` object and adds it to the
     * mEvents hashMap if necessary, otherwise it just updates the event's value
     *
     * @param tag The tag for the event
     * @param eventValue the value to be set to the event
     */

    @Deprecated(
            message = "This function is deprecated as of version 0.3.0. Please use setStickyLiveEventValue(..) " +
                    "instead ",
            replaceWith = ReplaceWith("setStickyLiveEventValue(tag, eventValue)")
    )
    fun <T> postStickyEvent(tag: String, eventValue: T) {
        setLiveEventValue(tag, eventValue, StickyLiveEvent::class.java)
    }

    /**
     * This function creates a LiveEvent object and adds it to the
     * mEvents hashMap if necessary, otherwise it just updates the event's value
     *
     * @param tag The tag for the event
     * @param eventValue the value to be set to the event
     */
    fun <T> setLiveEventValue(tag: String, eventValue: T) {
        setLiveEventValue(tag, eventValue, LiveEvent::class.java)
    }

    /**
     * This function creates a LiveEvent object and adds it to the mEvents hashMap
     * if necessary, otherwise it just updates the event's value on the background thread
     *
     * @param tag The tag for the event
     * @param eventValue the value to be set to the event
     */
    fun <T> postLiveEventValue(tag: String, eventValue: T) {
        postLiveEventValue(tag, eventValue, LiveEvent::class.java)
    }

    /**
     * This function creates a `SingleLiveEvent` object and adds it to the
     * mEvents hashMap if necessary, otherwise it just updates the event's value
     *
     * @param tag The tag for the event
     * @param eventValue the value to be set to the event
     */
    fun <T> setSingleLiveEventValue(tag: String, eventValue: T) {
        setLiveEventValue(tag, eventValue, SingleLiveEvent::class.java)
    }

    /**
     * This function creates a `SingleLiveEvent` object and adds it to the mEvents hashMap
     * if necessary, otherwise it just updates the event's value on the background thread
     *
     * @param tag The tag for the event
     * @param eventValue the value to be set to the event
     */
    fun <T> postSingleLiveEventValue(tag: String, eventValue: T) {
        postLiveEventValue(tag, eventValue, SingleLiveEvent::class.java)
    }

    /**
     * This function creates a `StickySingleLiveEvent` object and adds it to the
     * mEvents hashMap if necessary, otherwise it just updates the event's value
     *
     * @param tag The tag for the event
     * @param eventValue the value to be set to the event
     */
    fun <T> setStickySingleLiveEventValue(tag: String, eventValue: T) {
        setLiveEventValue(tag, eventValue, StickySingleLiveEvent::class.java)
    }

    /**
     * This function creates a `StickySingleLiveEvent` object and adds it to the mEvents hashMap
     * if necessary, otherwise it just updates the event's value on the background thread
     *
     * @param tag The tag for the event
     * @param eventValue the value to be set to the event
     */
    fun <T> postStickySingleLiveEventValue(tag: String, eventValue: T) {
        postLiveEventValue(tag, eventValue, StickySingleLiveEvent::class.java)
    }

    /**
     * This function creates a `StickyLiveEvent` object and adds it to the
     * mEvents hashMap if necessary, otherwise it just updates the event's value
     *
     * @param tag The tag for the event
     * @param eventValue the value to be set to the event
     */
    fun <T> setStickyLiveEventValue(tag: String, eventValue: T) {
        setLiveEventValue(tag, eventValue, StickyLiveEvent::class.java)
    }

    /**
     * This function creates a `StickyLiveEvent` object and adds it to the mEvents hashMap
     * if necessary, otherwise it just updates the event's value on the background thread
     *
     * @param tag The tag for the event
     * @param eventValue the value to be set to the event
     */
    fun <T> postStickyLiveEventValue(tag: String, eventValue: T) {
        postLiveEventValue(tag, eventValue, StickyLiveEvent::class.java)
    }

    /**
     * Removes the event identified by @param tag from the Bus.
     */
    fun removeEvent(tag: String) {
        if (!mEvents.contains(tag)) return

        mEvents.remove(tag)
    }

    fun subscribeEvent(tag: String): LiveEventBase<out Any?>? {
        if (mEvents.containsKey(tag)) {
            return mEvents[tag]
        }

        return null
    }

    /**
     * Returns the `LiveEvent` object, creates one if necessary
     * @return the LiveEvent object specified by the @param tag
     */
    fun <T> subscribeLiveEvent(tag: String, type: Class<T>): LiveEventBase<T> {
        return if (mEvents.containsKey(tag)) {
            exceptionWrapper(fun(): LiveEventBase<T> {
                return mEvents[tag] as LiveEvent<T>
            }, CAST_EXCEPTION_MESSAGE)
        } else {
            val liveEvent = LiveEvent<T>()
            mEvents[tag] = liveEvent
            liveEvent
        }
    }

    /**
     * Returns the `SingleLiveEvent` object, creates one if necessary
     * @return the `SingleLiveEvent` object specified by the @param tag
     */
    fun <T> subscribeSingleLiveEvent(tag: String, type: Class<T>): LiveEventBase<T> {
        return if (mEvents.containsKey(tag)) {
            exceptionWrapper(fun(): LiveEventBase<T> {
                return mEvents[tag] as SingleLiveEvent<T>
            }, CAST_EXCEPTION_MESSAGE)
        } else {
            val liveEvent = SingleLiveEvent<T>()
            mEvents[tag] = liveEvent
            liveEvent
        }
    }

    /**
     * Returns the `StickyLiveEvent` object, creates one if necessary
     * @return the `StickyLiveEvent` object specified by the @param tag
     */
    fun <T> subscribeStickyLiveEvent(tag: String, type: Class<T>): LiveEventBase<T> {
        return if (mEvents.containsKey(tag)) {
            exceptionWrapper(fun(): LiveEventBase<T> {
                return mEvents[tag] as StickyLiveEvent<T>
            }, CAST_EXCEPTION_MESSAGE)
        } else {
            val liveEvent = StickyLiveEvent<T>()
            mEvents[tag] = liveEvent
            liveEvent
        }
    }

    /**
     * Returns the `StickySingleLiveEvent` object, creates one if necessary
     * @return the `StickySingleLiveEvent` object specified by the @param tag
     */
    private fun <T> subscribeStickySingleLiveEvent(tag: String, type: Class<T>): LiveEventBase<T> {
        return if (mEvents.containsKey(tag)) {
            exceptionWrapper(fun(): LiveEventBase<T> {
                return mEvents[tag] as StickySingleLiveEvent<T>
            }, CAST_EXCEPTION_MESSAGE)
        } else {
            val liveEvent = StickySingleLiveEvent<T>()
            mEvents[tag] = liveEvent
            liveEvent
        }
    }
}