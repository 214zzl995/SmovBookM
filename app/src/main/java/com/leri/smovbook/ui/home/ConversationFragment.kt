package com.leri.smovbook.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.leri.smovbook.MainViewModel
import com.leri.smovbook.R
import com.leri.smovbook.data.exampleUiState
import com.leri.smovbook.theme.JetchatTheme

//尝试在这里做一个两次返回才能退出的功能
class ConversationFragment : Fragment() {

    private val activityViewModel: MainViewModel by activityViewModels()  //这里取状态是用 activityViewModels  //这里应该也要定义一个类似的值值列表为 服务器地址 可以从扫描二维码的界面传输过来
    private val conversationViewModel: ConversationViewModel by activityViewModels()
//    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(inflater.context).apply {
        layoutParams = LayoutParams(MATCH_PARENT, MATCH_PARENT)
        setContent {
            CompositionLocalProvider(
                LocalBackPressedDispatcher provides requireActivity().onBackPressedDispatcher
            ) {
                JetchatTheme {
                    val serverUrl by conversationViewModel.serverUrl.collectAsState() //我需要在他变更时修改数据的值

                    ConversationContent(
                        uiState = exampleUiState,
                        navigate = { nav ->
                            findNavController().navigate(
                                nav
                            )
                        },
                        onNavIconPressed = {
                            activityViewModel.openDrawer()
                        },
                        modifier = Modifier.windowInsetsPadding(
                            WindowInsets
                                .navigationBars
                                .only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)
                        ),
                        conversationViewModel = conversationViewModel
                    )
                }
            }
        }
    }
}

