/*
 *     Digital Patrol Guard
 *     EventBus.kt
 *     Created by ImamSyahrudin on 18/9/2022
 *     Copyright © 2022 imamSyahrudin. All rights reserved.
 *
 *     Last modified 9/18/22, 11:21 AM
 */

package ai.digital.patrol.helper

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject

class EventBus {
    companion object {
        val publisher: PublishSubject<Any> = PublishSubject.create()

        inline fun <reified T : Any> subscribe(): Observable<T> {
            return publisher.filter {
                it is T
            }.map {
                it as T
            }
        }

        fun post(event: Any) {
            publisher.onNext(event)
        }
    }
}