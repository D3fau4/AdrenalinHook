package com.hato.adrenalinhook.hooks

import android.content.Context
import android.content.Intent
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import java.lang.reflect.Method

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
            if(method.name.equals("setFullScreenListener")){
                var params: MutableList<Any> = ArrayList()
                params.addAll(method.parameterTypes.toList())
                params.add(object : XC_MethodReplacement() {
                    @Throws(Throwable::class)
                    override fun replaceHookedMethod(param: MethodHookParam?): Any? {
                        var thisObject = param?.thisObject;
                        var getStreamDataMethod = thisObject?.javaClass?.getDeclaredMethod("getStreamData")
                        if (getStreamDataMethod != null) {
                            getStreamDataMethod.isAccessible = true
                            var streamDataObject = getStreamDataMethod.invoke(thisObject)
                            if(streamDataObject != null){
                                var toStringMethod = getStreamDataMethod.javaClass.getMethod("toString")
                                toStringMethod.isAccessible = true
                                var toStringResult = toStringMethod.invoke(streamDataObject)
                                if(toStringResult is String){
                                    val streamDataToString = toStringResult as String
                                    XposedBridge.log(streamDataToString)
                                }
                            }
                        }
                        return null
                    }
                })

                XposedBridge.log("Intercentando metodo: $method")

                XposedHelpers.findAndHookMethod("com.adrenalinagol.na.PlayerActivity",
                    lpparam?.classLoader, method.name, params.toTypedArray(), object : XC_MethodHook() {
                        override fun beforeHookedMethod(param: MethodHookParam?) {
                            super.beforeHookedMethod(param)
                        }

                        override fun afterHookedMethod(param: MethodHookParam?) {
                            super.afterHookedMethod(param)
                        }
                    })
                hookedmethods++
            }
        }
    }
}