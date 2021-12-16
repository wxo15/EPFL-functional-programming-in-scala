package observatory

import java.time.LocalDate
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.rdd.RDD
import scala.io.Source
/**
  * 1st milestone: data extraction
  */
object Extraction extends ExtractionInterface {

  @transient lazy val conf: SparkConf = new SparkConf().setMaster("local").setAppName("StackOverflow")
  @transient lazy val sc: SparkContext = new SparkContext(conf)
  sc.setLogLevel("WARN")

  /**
    * @param year             Year number
    * @param stationsFile     Path of the stations resource file to use (e.g. "/stations.csv")
    * @param temperaturesFile Path of the temperatures resource file to use (e.g. "/1975.csv")
    * @return A sequence containing triplets (date, location, temperature)
    */
/**
    * Case class representing a station unique identifier
    *
    * @param stn  STN identifier
    * @param wban WBAN identifier
    */
  private case class StationIdentifier(stn: String, wban: String)

  /**
    * Case class representing a temperature at a specific date
    *
    * @param localDate   Date for this data point
    * @param temperature Temperature for this data point
    */
  private case class TemperatureData(localDate: LocalDate, temperature: Temperature)

  
  def locateTemperatures(year: Year, stationsFile: String, temperaturesFile: String): Iterable[(LocalDate, Location, Temperature)] = {
    def fileToRDD(file: String): RDD[String] = {
      val fileseq = Source.fromInputStream(getClass.getResourceAsStream(file), "utf-8")
      sc.parallelize(fileseq.getLines().toList)
    }

    def parseStations(line: String): Option[(StationIdentifier, Location)] = {
      val arr = line.split(",")
      if (arr.length != 4 || arr(2).isEmpty || arr(3).isEmpty)
        None
      else {
        val id = StationIdentifier(arr(0), arr(1))
        val location = Location(arr(2).toDouble, arr(3).toDouble)
        Some((id, location))
      }
    }

    def parseTemperature(line: String): Option[(StationIdentifier, TemperatureData)] = {
      val arr = line.split(",")
      if (arr.length != 5 || arr(2).isEmpty || arr(3).isEmpty)
        None
      else {
        val id = StationIdentifier(arr(0), arr(1))
        val tempData = new TemperatureData(LocalDate.of(year, arr(2).toInt, arr(3).toInt), (arr(4).toDouble - 32) * 5 / 9 )
        Some((id, tempData))
      }
    }

    val stationsRDD: RDD[String] = fileToRDD(stationsFile)
    val temperatureRDD: RDD[String] = fileToRDD(temperaturesFile)

    val stations: RDD[(StationIdentifier, Location)] = stationsRDD.flatMap(parseStations)
    val temperature: RDD[(StationIdentifier, TemperatureData)] = temperatureRDD.flatMap(parseTemperature)
    val resRDD: RDD[(LocalDate, Location, Temperature)] =
      stations.join(temperature).map({
        case (_, (loc: Location, td: TemperatureData)) => (td.localDate, loc, td.temperature)
      })

    resRDD.collect()
  }

  /**
    * @param records A sequence containing triplets (date, location, temperature)
    * @return A sequence containing, for each location, the average temperature over the year.
    */
  def locationYearlyAverageRecords(records: Iterable[(LocalDate, Location, Temperature)]): Iterable[(Location, Temperature)] = {
    val recordsRDD: RDD[(LocalDate, Location, Temperature)] = sc.parallelize(records.toSeq)
    val resRDD: RDD[(Location, Temperature)] = recordsRDD.map({case (_, loc, t) => (loc, t)})
      .aggregateByKey((0, 0.0D))({
        case (acc, t) => (acc._1 + 1, acc._2 + t)
      }, {
        case (a, b) => (a._1 + b._1, a._2 + b._2)
      })
      .mapValues({
        case(nYear, totTemp) => totTemp / nYear
      })
    resRDD.collect()
  }

}
