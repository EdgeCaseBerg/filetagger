package com.example

import com.mongodb.casbah.Imports._

class DocumentDataStorage(xmlConfFilePath: String, environment: String = "local") {
	val conf = scala.xml.XML.loadFile(xmlConfFilePath)
	val datasource = (conf \\ "database").filter(dNode => dNode.attribute("environment").exists(env => env.text == environment))
	assert(datasource.length == 1)

	val mongoClient = MongoClient( (datasource \ "host").text , (datasource \ "port").text.toInt)
	val db = mongoClient("filetagger")
	val collection = db("filetagger")
	

	def addFile(fileName: String) {
		collection.insert(MongoDBObject("name" -> fileName, "tags" -> List[String]()))
	}

	def addTagToFile(fileName: String, tag: String) : Boolean = {
		var resultOfOperation : Boolean = false

		//This foreach method on the findOne seems wrong, i must be missing something 
		//with options
		collection.findOne(MongoDBObject("name" -> fileName)).foreach { c =>
			c.getAs[List[String]]("tags").foreach { tags => 
					if (!tags.contains(tag)) {
						val tagList = tags ++ tag
						val update = $set("tags" -> tagList)
						val result = collection.update(c, update, upsert=true)
						assert(result.getN == 1)
						resultOfOperation = true
					}		
				}
			}
		return resultOfOperation  
	}


	
}

