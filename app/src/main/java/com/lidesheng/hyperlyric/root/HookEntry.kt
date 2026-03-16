package com.lidesheng.hyperlyric.root

import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed
import com.highcapable.yukihookapi.hook.xposed.bridge.event.YukiXposedEvent
import com.highcapable.yukihookapi.hook.xposed.proxy.IYukiHookXposedInit

@InjectYukiHookWithXposed(
    isUsingResourcesHook = true
)
object HookEntry : IYukiHookXposedInit {

    override fun onHook() = YukiHookAPI.encase {
        loadApp(name = "com.android.systemui") {
            MainHook.hookSystemUIDynamicIsland()
        }
    }

    override fun onXposedEvent() {
        YukiXposedEvent.events {
            onHandleInitPackageResources {
                UnlockIslandWhitelist.init(it)
            }
        }
    }
}
