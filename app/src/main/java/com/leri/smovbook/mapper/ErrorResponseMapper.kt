package com.leri.smovbook.mapper


import com.leri.smovbook.models.network.SmovErrorResponse
import com.skydoves.sandwich.ApiErrorModelMapper
import com.skydoves.sandwich.ApiResponse
import com.skydoves.sandwich.message

/**
 * A mapper for mapping [ApiResponse.Failure.Error] response as custom [SmovErrorResponse] instance.
 *
 * @see [ApiErrorModelMapper](https://github.com/skydoves/sandwich#apierrormodelmapper)
 */
object ErrorResponseMapper : ApiErrorModelMapper<SmovErrorResponse> {

  /**
   * maps the [ApiResponse.Failure.Error] to the [SmovErrorResponse] using the mapper.
   *
   * @param apiErrorResponse The [ApiResponse.Failure.Error] error response from the network request.
   * @return A customized [SmovErrorResponse] error response.
   */
  override fun map(apiErrorResponse: ApiResponse.Failure.Error<*>): SmovErrorResponse {
    return SmovErrorResponse(apiErrorResponse.statusCode.code, apiErrorResponse.message())
  }
}
