package com.mahesch.trymyui.model

import java.io.Serializable
import java.util.*

class TaskModel : Serializable {

    var hasSubTask = false
    var task: String? = null
    var sub_tasks: ArrayList<String>? = null
    var task_id = 0

    var opt_for_task_completion = false
    var opt_for_seq = false
}