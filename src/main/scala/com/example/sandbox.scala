package com.example

import com.mongodb.casbah.Imports._

class DocumentDataStorage(xmlConfFilePath: String, environment: String = "local") {
	val conf = scala.xml.XML.loadFile(xmlConfFilePath)
	val datasource = (conf \\ "database").filter(dNode => dNode.attribute("environment").exists(env => env.text == environment))
	assert(datasource.length == 1)

	lazy val mongoClient = MongoClient( (datasource \ "host").text , (datasource \ "port").text.toInt)
	lazy val db = mongoClient("filetagger")
	lazy val collection = db("filetagger")
	

	def addFile(fileName: String) {
		collection.insert(MongoDBObject("name" -> fileName, "tags" -> List[String]()))
	}

	def addTagToFile(fileName: String, tag: String) : Boolean = {
		var resultOfOperation : Boolean = false

		collection.findOne(MongoDBObject("name" -> fileName)).foreach { row =>
			var tags = row.as[List[String]]("tags")
			if (!tags.contains(tag)) {
				val tagList = tags ++ tag
				val update = $set("tags" -> tagList)
				val result = collection.update(row, update, upsert=true)
				assert(result.getN == 1)
				resultOfOperation = true
			}			
		}
		return resultOfOperation  
	}

	


	
}

