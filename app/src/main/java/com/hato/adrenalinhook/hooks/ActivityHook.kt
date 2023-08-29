package com.hato.adrenalinhook.hooks

import android.content.Context
import android.content.Intent
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.XposedHelpers.findClass
import de.robv.android.xposed.callbacks.XC_LoadPackage

class ActivityHook : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam?) {
        if (!lpparam?.packageName.equals("com.adrenalinagol.na")) {
            return
        }
        var hookedmethods = 0
        XposedBridge.log("Instalado hooks comprobaciones...")

        for (method in findClass(
            "com.adrenalinagol.na.SplashActivity",
            lpparam?.classLoader
        ).declaredMethods) {
            if (method.name.equals("showDNSDialog")) {
                var params: MutableList<Any> = ArrayList()
                params.addAll(method.parameterTypes.toList())
                params.add(object : XC_MethodReplacement() {
                    @Throws(Throwable::class)
                    override fun replaceHookedMethod(param: MethodHookParam?): Any? {
                        var context: Context = param?.thisObject as Context
                        XposedBridge.log("Ejecutando función interceptada...")
                        try {
                            val mainActivityClassName: String = "com.adrenalinagol.na.MainActivity"
                            var mainActivityClass: Class<*>? =
                                context.classLoader.loadClass(mainActivityClassName)
                            var intent: Intent = Intent(context, mainActivityClass)
                            context.startActivity(intent)
                            XposedBridge.log("Ejecutada correctamente!")
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        return null
                    }
                })

                XposedBridge.log("Intercentando metodo: $method")

                XposedHelpers.findAndHookMethod("com.adrenalinagol.na.SplashActivity",
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