package observatory

import Visualization2._
import java.io.File
import java.nio.file.{Files, Paths}


object Main extends App {

    val tempScale : List[(Temperature, Color)] = List(
        (60, Color(255, 255, 255)),
        (32, Color(255, 0, 0)),
        (12, Color(255, 255, 0)),
        (0, Color(0, 255, 255)), 
        (-15, Color(0, 0, 255)), 
        (-27, Color(255, 0, 255)),
        (-50, Color(33, 0, 107)), 
        (-60, Color(0, 0, 0))
    )

    val devScale : List[(Temperature, Color)] = List(
        (7, Color(0, 0, 0)), 
        (4, Color(255, 0, 0)), 
        (2, Color(255, 255, 0)), 
        (0, Color(255, 255, 255)), 
        (-2, Color(0, 255, 255)), 
        (-7, Color(0, 0, 255))
    )
/*
    println("Start map")
    var y = 2015
    val yearlyData: Iterable[(observatory.Year, Iterable[(observatory.Location, Double)])]=
        Iterable((y, Extraction.locationYearlyAverageRecords(Extraction.locateTemperatures(y, "/stations.csv", s"/${y}.csv"))))

    println("Start generate")
    Interaction.generateTiles(yearlyData, tempGenerator)

    def tempGenerator(year: Int, tile: Tile, 
                    data: Iterable[(Location, Double)]): Unit = {

        val zoomdir = s"target/temperatures/$year/${tile.zoom}"
        val fn = s"$zoomdir/${tile.x}-${tile.y}.png"
        Files.createDirectories(Paths.get(zoomdir))

        val image = visualizeGrid(Manipulation.makeGrid(data), tempScale, tile)

        val res = image.output(new File(fn))
        println(s"${res}")
    }
*/

    println("Start map")
    val averageTemperaturesByYear = ((1975 to 1989).toList ++ List(2005)).map(
        (year) => (year, Extraction.locationYearlyAverageRecords(Extraction.locateTemperatures(year, "/stations.csv", s"/$year.csv")))
    ).toList

    val (normalsRaw, deviationRaw) = averageTemperaturesByYear.partition(_._1 < 1990)
    val normals = Manipulation.average(normalsRaw.map(_._2))
    println("Start generate")
    Interaction.generateTiles(deviationRaw, devGenerator)
    
    def devGenerator(year: Int, tile: Tile,
                    data: Iterable[(Location, Double)]): Unit = {
        
        val zoomdir = s"target/deviations/$year/${tile.zoom}"
        val fn = s"$zoomdir/${tile.x}-${tile.y}.png"
        Files.createDirectories(Paths.get(zoomdir))

        val image = visualizeGrid(Manipulation.deviation(data, normals), devScale, tile)
        
        val res = image.output(new File(fn))
        println(s"${res}")
    }
}