package com.mahesch.trymyui.helpers

import android.content.Context
import android.util.Log
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.JsonPrimitive
import com.mahesch.trymyui.R
import com.mahesch.trymyui.model.TaskModel

class GetTaskArrayFromJson {

    companion object{

        private var TAG = GetTaskArrayFromJson.javaClass.simpleName.toUpperCase()

        fun getTaskArray(tasks: String,packageName : String,context: Context) : ArrayList<TaskModel>{

            var taskArrayList = ArrayList<TaskModel>()

            try
            {


                var jsonParser = JsonParser()

                var jsonArray = jsonParser.parse(tasks).asJsonArray

                Log.e(TAG, "jsonArray $jsonArray")

                if(!packageName.equals("",true)){
                    var taskModel = TaskModel()
                    taskModel.task = context.resources.getString(R.string.app_basic_instruction)
                    taskArrayList.add(taskModel)
                }

                for (i in 0 until jsonArray.size()) {

                    var taskmodel = TaskModel()

                    var jsonObject = jsonArray.get(i).asJsonObject

                    Log.e(TAG,"jsonObject $jsonObject")

                    if(jsonObject.has("has_sub_tasks"))
                        taskmodel.hasSubTask = jsonObject.get("has_sub_tasks").asBoolean

                    if(jsonObject.has("task"))
                        taskmodel.task = jsonObject["task"].asString

                    Log.e(TAG,"taskModel task "+taskmodel.task)

                    if(jsonObject.has("sub_tasks")){
                        var subTaskList = ArrayList<String>()

                        var jsonArray_subtask = jsonObject.getAsJsonArray("sub_tasks")
                        for(i in 0 until jsonArray_subtask.size()){
                            subTaskList.add(jsonArray_subtask[i].asString)
                            taskmodel.sub_tasks = subTaskList
                        }
                    }


                    if(jsonObject.has("task_id"))
                        taskmodel.task_id = jsonObject["task_id"].asInt

                    if(jsonObject.has("opt_for_seq"))
                        taskmodel.opt_for_seq = jsonObject.get("opt_for_seq").asBoolean

                    if(jsonObject.has("opt_for_task_completion"))
                        taskmodel.opt_for_task_completion = jsonObject.get("opt_for_task_completion").asBoolean

                    taskArrayList.add(taskmodel)


                }
            }
            catch (e: Exception){
                e.printStackTrace()
            }
            return taskArrayList
        }

    }
}