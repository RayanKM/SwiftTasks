package com.example.swifttasks

data class SubTaskModel(val task : String?, val status : Boolean? = null){
    constructor() : this(
        "",
        null
    )
}