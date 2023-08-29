package com.hato.adrenalinhook.hooks

import android.content.Context
import android.content.Intent
import de.robv.android.xposed.IXposedHookLoadPackage
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
            if (method.name.equals("showDNSDialog") || method.name.equals("showRootDialog")) {

                XposedBridge.log("Intercentando metodo: $method")

                XposedHelpers.findAndHookMethod(
                    "com.adrenalinagol.na.SplashActivity",
                    lpparam?.classLoader,
                    method.name,
                    object : XC_MethodReplacement() {
                        @Throws(Throwable::class)
                        override fun replaceHookedMethod(param: MethodHookParam): Any? {
                            val context = param.thisObject as Context

                            try {
                                val mainActivityClassName = "com.adrenalinagol.na.MainActivity"
                                val mainActivityClass =
                                    context.classLoader.loadClass(mainActivityClassName)

                                val intent = Intent(context, mainActivityClass)

                                context.startActivity(intent)
                            } catch (e: ClassNotFoundException) {
                                e.printStackTrace()
                            }

                            return null
                        }
                    }
                )
                hookedmethods++
            }
        }

    }
}