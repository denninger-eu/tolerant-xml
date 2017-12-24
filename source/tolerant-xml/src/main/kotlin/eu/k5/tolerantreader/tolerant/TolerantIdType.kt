package eu.k5.tolerantreader.tolerant

import eu.k5.tolerantreader.*

class TolerantIdType : TolerantSimpleType(xsId, xsId) {

    override fun parse(context: BindContext, text: String): Any = text.trim()

}

class TolerantIdRefType : TolerantSimpleType(xsIdRef, xsIdRef) {
    override fun parse(context: BindContext, text: String): IdRefType {
        val entity = context.getEntityById(text.trim())
        if (entity != null) {
            return IdRefType(entity, text.trim())
        }
        return IdRefType(null, text.trim())
    }

}


class IdRefType(
        val entity: Any?,
        val id: String
)

