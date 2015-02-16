package com.example

import com.mongodb.casbah.Imports._

class DocumentDataStorage(xmlConfFilePath: String, environment: String = "local") {
	val conf = scala.xml.XML.loadFile(xmlConfFilePath)
	val datasource = (conf \\ "database").filter(dNode => dNode.attribute("environment").exists(env => env.text == environment))
	assert(datasource.length == 1)

	val mongoClient = MongoClient( (datasource \ "host").text , (datasource \ "port").text.toInt)

	
}

