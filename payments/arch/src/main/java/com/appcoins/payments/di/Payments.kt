package com.appcoins.payments.di

import android.app.Application
import android.content.Context
import com.appcoins.payments.arch.Environment
import com.appcoins.payments.arch.Logger
import com.appcoins.payments.arch.MutedLogger
import com.appcoins.payments.arch.WalletProvider
import kotlin.reflect.KProperty

object Payments {

  private lateinit var application: Application

  lateinit var environment: Environment
    private set
  lateinit var logger: Logger
    private set

  val context: Context
    get() = application.applicationContext

  fun init(
    application: Application,
    environment: Environment,
    logger: Logger = MutedLogger,
  ): Payments {
    Payments.application = application
    Payments.environment = environment
    Payments.logger = logger
    return this
  }
}

var Payments.walletProvider: WalletProvider by lateInit()

class LateInit<T : Any>(private val initDefault: (Payments.() -> T)?) {
  private var initialized = false
  private lateinit var value: T

  operator fun getValue(paymentsModule: Payments, property: KProperty<*>): T =
    if (initialized) {
      value
    } else {
      value = initDefault?.let { paymentsModule.it() }
        ?: throw IllegalStateException("Value is not initialised nor it has default")
      initialized = true
      value
    }

  operator fun setValue(paymentsModule: Payments, property: KProperty<*>, t: T) {
    value = t
    initialized = true
  }
}

fun <T : Any> lateInit(default: (Payments.() -> T)? = null): LateInit<T> = LateInit(default)

class LazyInit<T : Any>(private val init: Payments.() -> T) {
  private var initialized = false
  lateinit var value: T

  operator fun getValue(paymentsModule: Payments, property: KProperty<*>): T =
    if (initialized) {
      value
    } else {
      value = paymentsModule.init()
      initialized = true
      value
    }
}

fun <T : Any> lazyInit(init: Payments.() -> T): LazyInit<T> = LazyInit(init)
