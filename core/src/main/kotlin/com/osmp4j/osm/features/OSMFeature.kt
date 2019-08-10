package com.osmp4j.osm.features

class OSMValue<T>(val name: String)
class OSMAttribute<T>(key: String) : OSMFeature<T>(key)

open class OSMFeature<T>(val key: String) {

    //TODO Annotation, Name, Properties, References, Restrictions

    val ADDR_CITY = attr("addr:city")
    val ADDR_CONSCRIPTIONNUMBER = attr("addr:conscriptionnumber")
    val ADDR_COUNTRY = attr("addr:country")
    val ADDR_DISTRICT = attr("addr:district")
    val ADDR_FLATS = attr("addr:flats")
    val ADDR_FULL = attr("addr:full")
    val ADDR_HAMLET = attr("addr:hamlet")
    val ADDR_HOUSENAME = attr("addr:housename")
    val ADDR_HOUSENUMBER = attr("addr:housenumber")
    val ADDR_INCLUSION = attr("addr:inclusion")
    val ADDR_INTERPOLATION = attr("addr:interpolation")
    val ADDR_PLACE = attr("addr:place")
    val ADDR_POSTCODE = attr("addr:postcode")
    val ADDR_PROVINCE = attr("addr:province")
    val ADDR_STATE = attr("addr:state")
    val ADDR_STREET = attr("addr:street")
    val ADDR_SUBDISTRICT = attr("addr:subdistrict")
    val ADDR_SUBURB = attr("addr:suburb")


    protected fun value(name: String) = OSMValue<T>(name)
    protected fun attr(name: String) = OSMAttribute<T>(name)
}