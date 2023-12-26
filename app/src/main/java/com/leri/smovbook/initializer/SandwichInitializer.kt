@file:Suppress("unused")

package com.leri.smovbook.initializer

import android.content.Context
import androidx.startup.Initializer
import com.leri.smovbook.operator.GlobalResponseOperator
import com.skydoves.sandwich.SandwichInitializer

class SandwichInitializer : Initializer<Unit> {

  override fun create(context: Context) {

    // initialize global sandwich operator
    SandwichInitializer.sandwichOperators = mutableListOf(GlobalResponseOperator<Unit>(context))
  }

  override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}
