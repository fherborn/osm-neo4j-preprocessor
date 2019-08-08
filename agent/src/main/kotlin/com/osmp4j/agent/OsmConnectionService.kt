package com.osmp4j.agent

import de.westnordost.osmapi.OsmConnection
import de.westnordost.osmapi.map.MapDataDao
import de.westnordost.osmapi.map.data.BoundingBox
import de.westnordost.osmapi.map.data.Node
import de.westnordost.osmapi.map.data.Relation
import de.westnordost.osmapi.map.data.Way
import de.westnordost.osmapi.map.handler.MapDataHandler
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Service


@Service
class OsmConnectionService @Autowired constructor(private val osmConnectionFactory: OsmConnectionFactory){

    private val logger = LoggerFactory.getLogger(OsmConnectionService::class.java)

    //fun requestMapData(boundingBox: BoundingBox) {
    fun requestMapData() {

        logger.debug("Loading data from remote")
        val boundingBox = BoundingBox(7.84754, 51.02836, 7.84938, 51.0298)


        val con = osmConnectionFactory.getConnection()

        val mapDao = MapDataDao(con)
        mapDao.getMap(boundingBox, MyMapDataHandler())


    }





}

@Configuration
class OsmConnectionConf {
    @Bean
    fun osmConnectionFactory() = OsmConnectionFactory()
}

class OsmConnectionFactory {
    fun getConnection() : OsmConnection =  OsmConnection(
            "https://api.openstreetmap.org/api/0.6/",
            "OSM4J", null)
}



class MyMapDataHandler : MapDataHandler {
    override fun handle(bounds: BoundingBox?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun handle(node: Node?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun handle(way: Way?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun handle(relation: Relation?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}