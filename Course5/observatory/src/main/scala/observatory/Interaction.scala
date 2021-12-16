package observatory

import com.sksamuel.scrimage.{Image, Pixel}
import scala.math._

/**
  * 3rd milestone: interactive visualization
  */
object Interaction extends InteractionInterface {

  /**
    * @param tile Tile coordinates
    * @return The latitude and longitude of the top-left corner of the tile, as per http://wiki.openstreetmap.org/wiki/Slippy_map_tilenames
    */
  def tileLocation(tile: Tile): Location = {
    val lat = Visualization.radToDeg(atan(sinh(Pi - (tile.y * 2 * Pi) / pow(2, tile.zoom))))
    val lon = (tile.x * 360) / pow(2, tile.zoom) - 180
    Location(lat, lon)
  }

  /**
    * @param temperatures Known temperatures
    * @param colors Color scale
    * @param tile Tile coordinates
    * @return A 256×256 image showing the contents of the given tile
    */
  def tile(temperatures: Iterable[(Location, Temperature)], colors: Iterable[(Temperature, Color)], tile: Tile): Image = {
    val offX = tile.x * 256
    val offY = tile.y * 256
    val offZ = tile.zoom
    val coords = for {
      i <- 0 until 256
      j <- 0 until 256
    } yield (i, j)

    val pixels = coords.par
      .map({case (y, x) => Tile(x + offX, y + offY, 8 + offZ)})
      .map(tileLocation)
      .map(Visualization.predictTemperature(temperatures, _))
      .map(Visualization.interpolateColor(colors, _))
      .map(color => Pixel(color.red, color.green, color.blue, 127))
      .toArray

    Image(256, 256, pixels)
  }

  /**
    * Generates all the tiles for zoom levels 0 to 3 (included), for all the given years.
    * @param yearlyData Sequence of (year, data), where `data` is some data associated with
    *                   `year`. The type of `data` can be anything.
    * @param generateImage Function that generates an image given a year, a zoom level, the x and
    *                      y coordinates of the tile and the data to build the image from
    */
  def generateTiles[Data](
    yearlyData: Iterable[(Year, Data)],
    generateImage: (Year, Tile, Data) => Unit
  ): Unit = {
    for {
      (year, data) <- yearlyData
      zoom <- 0 to 3
      y <- 0 until pow(2, zoom).toInt
      x <- 0 until pow(2, zoom).toInt
    } 
    generateImage(year, Tile(x, y, zoom), data)
  }

}
