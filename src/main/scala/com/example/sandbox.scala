package com.example

import sys.process._
import java.io.File
import javax.activation.MimetypesFileTypeMap
import scala.collection.mutable.ListBuffer
import scala.language.postfixOps // for exec ! postfix

import java.util.concurrent.Executors
import java.util.concurrent.Callable
import java.util.concurrent.TimeUnit
import scala.collection.JavaConversions._


class SlideShowForFolder(rootFolderPath: String, timeToShowImage: Long = 2L) {
	val mimetypesFileTypeMap = new MimetypesFileTypeMap()
	mimetypesFileTypeMap.addMimeTypes("image png tif jpg jpeg bmp")

	var curList = Set.empty[File]
	var iter = curList.iterator : Iterator[File] //iter for curList
	var curFile = null : File
	
	private def isImage(possibleImg: File) : Boolean = { 
		return mimetypesFileTypeMap.getContentType( possibleImg).substring(0,5).equalsIgnoreCase("image")
	}

	def init : Boolean = {
		val f = new File(rootFolderPath)
		if (f.isDirectory) {
			val allFiles : List[File] = f.listFiles.toList
			curList ++= allFiles.filter(file => isImage(file))
			iter = curList.iterator
			return true
		}
		return false;
	}

	def showFile(f : File) : Unit = {
		showFile(f.getAbsolutePath)
	}

	def showNext() : Unit = {
		showFile(next)
	}

	def next : File = {
		if (iter.hasNext) {
			curFile = iter.next
		} else {
			iter = curList.iterator
			if (!curList.isEmpty) next 
		}
		return curFile
	}

	def showFile(s : String) : Unit = {
		//Super hacky way of doing this and you need to have eom
		//on your system. 
		val pool = Executors.newSingleThreadScheduledExecutor()
		val toRun = new Callable[Unit]() {
				override def call() {
					s"eom $s" !	;
				}
			}
		val tasks = List(toRun)
		pool.invokeAll(tasks,
			timeToShowImage,
			TimeUnit.SECONDS
		)
		pool.shutdown
		"killall eom" !	
	}

	def slideShowForNSeconds(timeToRunInSeconds: Long) : Unit = {
		init
		val now = java.util.Calendar.getInstance().getTime.getTime
		val later = now + (timeToRunInSeconds*1000)
		while(java.util.Calendar.getInstance().getTime.getTime < later) {
			showNext
		}
	}
}
