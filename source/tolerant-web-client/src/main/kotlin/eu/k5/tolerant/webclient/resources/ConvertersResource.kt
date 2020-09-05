package eu.k5.tolerant.webclient.resources

import eu.k5.tolerant.converter.config.Configurations
import eu.k5.tolerant.webclient.converter.ConverterRepository
import javax.inject.Inject
import javax.ws.rs.Consumes
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Path("convert")
@Consumes("application/json", "application/x-www-form-urlencoded")
@Produces("application/json", "application/x-www-form-urlencoded")
class ConvertersResource @Inject constructor(
        private val converter: ConverterRepository
) {

    @Consumes(MediaType.APPLICATION_XML)
    @POST
    fun register(body: Configurations): String {

        return ""
    }

}