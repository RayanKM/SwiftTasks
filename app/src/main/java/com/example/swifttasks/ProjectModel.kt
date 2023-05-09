package com.example.swifttasks

data class ProjectModel(val projectType: String? = null, val projectDescription: String? = null, val tasks: MutableList<SubTaskModel>? = null)
{
    constructor() : this(
        "",
        "",
        mutableListOf<SubTaskModel>())
}