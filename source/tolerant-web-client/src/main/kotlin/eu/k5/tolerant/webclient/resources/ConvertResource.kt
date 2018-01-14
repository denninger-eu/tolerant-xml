package  eu.k5.tolerant.webclient.resources

import eu.k5.tolerant.converter.TolerantConverterRequest
import eu.k5.tolerant.webclient.converter.Converter
import javax.inject.Inject
import javax.ws.rs.*
import javax.ws.rs.core.Response

@Path("convert")
@Consumes("application/json", "application/x-www-form-urlencoded")
@Produces("application/json", "application/x-www-form-urlencoded")
class ConvertResource @Inject constructor(
        private val converter: Converter
) {


    @GET
    fun alive(): String = "yes"

    @GET
    @Path("targets")
    fun targets(): List<ConverterTarget> {
        return converter.listAvailable().map {
            ConverterTarget(key = it.key, name = it.name)
        }.toCollection(ArrayList())
    }


    @POST
    @Path("request")
    fun convert(body: ConvertRequest): Response {
        val request = TolerantConverterRequest()
        request.content = body.content
        val result = converter.convert(body.target, request)

        return Response.ok(result).build()
    }

}

data class ConverterTarget(

        var key: String? = null,
        var name: String? = null


)


data class ConvertRequest(
        var target: String? = null,
        var content: String? = null

)

