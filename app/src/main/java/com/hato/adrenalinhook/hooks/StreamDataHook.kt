package com.hato.adrenalinhook.hooks

import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

class StreamDataHook : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam?) {
        if (!lpparam?.packageName.equals("com.adrenalinagol.na")) {
            return
        }
        var hookedmethods = 0
        XposedBridge.log("Instalado hooks streamData ...")

        for (method in XposedHelpers.findClass(
            "com.adrenalinagol.na.PlayerActivity",
            lpparam?.classLoader
        ).declaredMethods) {
            if (method.name.equals("setFullScreenListener")) {
                XposedHelpers.findAndHookMethod(
                    "com.adrenalinagol.na.PlayerActivity",
                    lpparam?.classLoader,
                    method.name,
                    object : XC_MethodReplacement() {
                        @Throws(Throwable::class)
                        override fun replaceHookedMethod(param: MethodHookParam): Any? {
                            val thisObject = param.thisObject

                            val getStreamDataMethod =
                                thisObject.javaClass.getDeclaredMethod("getStreamData")
                            getStreamDataMethod.isAccessible = true
                            val streamDataObject = getStreamDataMethod.invoke(thisObject)

                            if (streamDataObject != null) {
                                val toStringMethod =
                                    streamDataObject.javaClass.getMethod("toString")
                                toStringMethod.isAccessible = true
                                val toStringResult = toStringMethod.invoke(streamDataObject)

                                if (toStringResult is String) {
                                    XposedBridge.log(toStringResult)
                                }
                            }

                            return XposedBridge.invokeOriginalMethod(
                                param.method,
                                param.thisObject,
                                param.args
                            )
                        }
                    }
                )
                hookedmethods++
            }
        }
    }
}