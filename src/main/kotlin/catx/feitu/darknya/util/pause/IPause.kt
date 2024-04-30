package catx.feitu.darknya.util.pause

import catx.feitu.darknya.module.AbstractModule

interface IPause {
    fun requestPause(module: catx.feitu.darknya.module.AbstractModule): Boolean
}

interface ITimeoutPause : IPause {
    override fun requestPause(module: catx.feitu.darknya.module.AbstractModule): Boolean {
        return requestPause(module, 50L)
    }

    fun requestPause(module: catx.feitu.darknya.module.AbstractModule, timeout: Int): Boolean {
        return requestPause(module, timeout.toLong())
    }

    fun requestPause(module: catx.feitu.darknya.module.AbstractModule, timeout: Long): Boolean
}