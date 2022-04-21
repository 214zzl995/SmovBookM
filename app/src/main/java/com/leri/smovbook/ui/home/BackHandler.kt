package com.leri.smovbook.ui.home

import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocal
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * This [Composable] can be used with a [LocalBackPressedDispatcher] to intercept a back press.
 *
 * @param onBackPressed (Event) What to do when back is intercepted
 *  这里处理的貌似是 返回被拦截时应该做的事情。。。
 */
@Composable
fun BackPressHandler(onBackPressed: () -> Unit) {
    // Safely update the current `onBack` lambda when a new one is provided
    // 这个应该是获取一个新的安全的onBack
    val currentOnBackPressed by rememberUpdatedState(onBackPressed) //这个rememberUpdatedState的功能是让值变化而不触发组件的重组 直接用也没事 但是 可能会内存泄露？？

    // Remember in Composition a back callback that calls the `onBackPressed` lambda
    val backCallback = remember {
        object : OnBackPressedCallback(true) {  //OnBackPressedCallback(true)
            override fun handleOnBackPressed() {
                currentOnBackPressed()  //这里修改了 抽屉状态 因为 定义了 rememberUpdatedState 所以其他组件去修改抽屉状态不会触发重组
            }
        }
    }

    val backDispatcher =
        LocalBackPressedDispatcher.current  // 返回最近的 CompositionLocalProvider 组件提供的值，该组件直接或间接调用使用此属性的可组合函数。就是返回他的值

    // Whenever there's a new dispatcher set up the callback
    DisposableEffect(backDispatcher) {   //DisposableEffect 是一个可组合函数，当它的入参改变时，它会把内部产生的所有 effect 清除掉，重置为初始状态
        backDispatcher.addCallback(backCallback)
        println("触发监听")
        // When the effect leaves the Composition, or there's a new dispatcher, remove the callback
        onDispose {
            println("移除监听")
            backCallback.remove()     //离开effect时 解除监听
        }
    }
    //解析一下上面这串代码 currentOnBackPressed 定义了一个不会触发重组的方法 这个方法的内容是关闭抽屉
    //然后设置了一个监听程序 当触发了系统的返回时 执行这个关闭抽屉的方法
    //然后当 backDispatcher 这个全局变量改变时 执行 block中的内容 当抽屉打开时 就是 onCommit 时 这个监听已经注册了

    //问题？ 为什么这里要使用rememberUpdatedState 而不是其他的 remember 因为 onBackPressed 可能会被改变？ 那当改变时会发生什么？
    //scope.launch {drawerState.close()} 这个值什么情况下会重载？
    //当点击外部而不是返回关闭抽屉时 这个 组件又会被重载一遍是么


    //这里的要点的 三个生命周期
    //onActive（or onEnter）：当 Composable 首次进入组件树时
    //onCommit（or onUpdate）：UI 随着 recomposition 发生更新时
    //onDispose（or onLeave）：当 Composable 从组件树移除时
}

/**
 * This [CompositionLocal] is used to provide an [OnBackPressedDispatcher]:
 *
 * ```
 * CompositionLocalProvider(
 *     LocalBackPressedDispatcher provides requireActivity().onBackPressedDispatcher
 * ) { }
 * ```
 *
 * and setting up the callbacks with [BackPressHandler].
 */
val LocalBackPressedDispatcher =
    staticCompositionLocalOf<OnBackPressedDispatcher> { error("No Back Dispatcher provided") }
