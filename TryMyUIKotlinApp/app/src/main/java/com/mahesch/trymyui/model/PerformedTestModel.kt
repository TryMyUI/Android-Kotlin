package com.mahesch.trymyui.model

class PerformedTestModel {
    var mid: Int
    var mstatus: String
    var mdate: String
    var mtest: String
    var murl: String
    var mvideo_url: String
    var mscenario: String
    var mscenario1: String
    var mscore = 0
    var mcomment: String? = null
    var title_with_id: String

    constructor(
        id: Int,
        status: String,
        date: String,
        test1: String,
        url: String,
        video_url: String,
        scenario: String,
        scenario1: String,
        score: Int,
        comment: String?,
        title_with_id: String
    ) {
        mid = id
        mstatus = status
        mdate = date
        mtest = test1
        murl = url
        mvideo_url = video_url
        mscenario = scenario
        mscenario1 = scenario1
        mscore = score
        mcomment = comment
        this.title_with_id = title_with_id
    }

    constructor(
        id: Int,
        status: String,
        date: String,
        test1: String,
        url: String,
        video_url: String,
        scenario: String,
        scenario1: String,
        title_with_id: String
    ) {
        mid = id
        mstatus = status
        mdate = date
        mtest = test1
        murl = url
        mvideo_url = video_url
        mscenario = scenario
        mscenario1 = scenario1
        this.title_with_id = title_with_id
    }

}