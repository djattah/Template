package com.l24o.vyatich.common

import com.l24o.vyatich.BuildConfig

inline fun inDebugMode(block: (() -> Unit)) {
    if (BuildConfig.DEBUG) {
        block.invoke()
    }
}
