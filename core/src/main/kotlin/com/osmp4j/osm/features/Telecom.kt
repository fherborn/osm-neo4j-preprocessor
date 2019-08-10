package com.osmp4j.osm.features

object Telecom : Feature<Telecom>("telecom") {
    val EXCHANGE = value("exchange")
    val CONNECTION_POINT = value("connection_point")
    val SERVICE_DEVICE = value("service_device")
    val DATA_CENTER = value("data_center")
}
