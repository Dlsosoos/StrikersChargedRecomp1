package com.pedro.strikersrecomp

import android.content.Context

/** Guarda ISO escolhido, config e cheats em SharedPreferences. */
class Store(ctx: Context) {
    private val p = ctx.getSharedPreferences("strikers", Context.MODE_PRIVATE)

    var isoUri: String?
        get() = p.getString("iso", null)
        set(v) {
            p.edit().putString("iso", v).apply()
        }

    fun flag(key: String, def: Boolean = false): Boolean = p.getBoolean(key, def)
    fun setFlag(key: String, v: Boolean) {
        p.edit().putBoolean(key, v).apply()
    }

    fun number(key: String, def: Float): Float = p.getFloat(key, def)
    fun setNumber(key: String, v: Float) {
        p.edit().putFloat(key, v).apply()
    }
}
