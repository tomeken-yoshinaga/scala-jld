package jukaiScala.main

/**
  * Created by kenta-yoshinaga on 2017/01/16.
  */

import java.io._

import jukaiScala.keras._
import jukaiScala.util._
import breeze.linalg.argmax

import scala.collection.mutable.ListBuffer

class kerasParser(modelPath: String, tablePath: String) {

  private val model = KerasModel(modelPath)
  private val table = LookupTable(tablePath)

  private val tagset: Map[Int, String] =  Map(0 -> "B", 1 -> "I", 2 -> "O")

  def parsing(str: String): Unit = {
    // For dummy input to indicate beginning of sentence.
    val s = "\n" + str
    val inputData = table.encode(s)
    val outputData = model.convert(inputData)

    val tags = for {
      i <- 1 until outputData.rows
      maxID = argmax(outputData(i, ::))
    } yield maxID

    val ranges = ListBuffer[(Int, Int)]()
    var bpos = -1

    for(i <- tags.indices){
      tagset(tags(i)) match{
        case "B" =>
          if (bpos == -1) {
            if(i > 0)
              ranges += ((0, i))
          }
          else {
            if(bpos != i)
              ranges +=((bpos, i))
          }
          bpos = i
        case "O" =>
          ranges += ((bpos, i))
          bpos = i + 1
        case _ if i == tags.indices.last =>
          ranges += ((bpos, i+1))
        case _ =>
      }
    }
    println(ranges.mkString(","))
    ranges.foreach(x => println(str.substring(x._1, x._2)))
  }

}

object kerasParser{

  def apply(modelPath:String, tablePath: String) = new kerasParser(modelPath, tablePath)

  def main(args: Array[String]): Unit = {
    val modelPath = args(0)
    val tablePath = args(1)

    val s = new kerasParser(modelPath, tablePath)
    println(s.parsing(args(2)))
  }
}