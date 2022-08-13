package com.leri.smovbook.models

/**
 * @Description: XXX
 * @author DingWei
 * @version 12/8/2022 下午1:09
 */
data class ServerResult<NetworkResponseModel>(
    var code: Int,
    var msg: String,
    var data: NetworkResponseModel
)
