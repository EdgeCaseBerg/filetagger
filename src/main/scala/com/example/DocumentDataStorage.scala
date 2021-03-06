package com.example

import com.mongodb.casbah.Imports._
import scala.collection.mutable.ListBuffer

class DocumentDataStorage(xmlConfFilePath: String, environment: String = "local") {
	val conf = scala.xml.XML.loadFile(xmlConfFilePath)
	val datasource = (conf \\ "database").filter(dNode => dNode.attribute("environment").exists(env => env.text == environment))
	assert(datasource.length == 1)

	/* Don't make a connection until we really need to */
	lazy val mongoClient = MongoClient( (datasource \ "host").text , (datasource \ "port").text.toInt)
	lazy val db = mongoClient("filetagger")
	lazy val collection = db("filetagger")
	

	def addFile(fileName: String) : Unit = {
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
		resultOfOperation  
	}

	def getFilesByTags(tag: String) : List[String] = {
		val listBuffer = ListBuffer.empty[String] 
		collection.find(MongoDBObject("tags" -> tag)).foreach { row => 
			listBuffer += row.as[String]("name")
		}
		listBuffer.toList
	}
}

