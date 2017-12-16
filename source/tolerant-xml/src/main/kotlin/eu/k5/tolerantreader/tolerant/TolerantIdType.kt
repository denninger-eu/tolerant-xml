package eu.k5.tolerantreader.tolerant

import eu.k5.tolerantreader.xsId
import eu.k5.tolerantreader.xsIdRef

class TolerantIdType : TolerantSimpleType(xsId, xsId) {


    override fun parse(text: String): Any {
        return ""
    }

}

class TolerantIdRefType : TolerantSimpleType(xsIdRef, xsIdRef){
    override fun parse(text: String): Any {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}