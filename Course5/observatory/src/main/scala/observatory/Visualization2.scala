package observatory

import com.sksamuel.scrimage.{Image, Pixel}
import scala.math._
/**
  * 5th milestone: value-added information visualization
  */
object Visualization2 extends Visualization2Interface {

  val power = 6
  val height = pow(2, power).toInt
  val width = pow(2, power).toInt

  /**
    * @param point (x, y) coordinates of a point in the grid cell
    * @param d00 Top-left value
    * @param d01 Bottom-left value
    * @param d10 Top-right value
    * @param d11 Bottom-right value
    * @return A guess of the value at (x, y) based on the four known values, using bilinear interpolation
    *         See https://en.wikipedia.org/wiki/Bilinear_interpolation#Unit_Square
    */
  def bilinearInterpolation(
    point: CellPoint,
    d00: Temperature, 
    d01: Temperature,
    d10: Temperature,
    d11: Temperature
  ): Temperature = {
    val x0 = point.x * (d10 - d00) + d00 
    val x1 = point.x * (d11 - d01) + d01
    point.y * (x1 - x0) + x0
  }

  /**
    * @param grid Grid to visualize
    * @param colors Color scale to use
    * @param tile Tile coordinates to visualize
    * @return The image of the tile at (x, y, zoom) showing the grid using the given color scale
    */
  def visualizeGrid(
    grid: GridLocation => Temperature,
    colors: Iterable[(Temperature, Color)],
    tile: Tile
  ): Image = {

    def getBiInterpolation(location: Location): Temperature = {
      val lat = location.lat.toInt
      val lon = location.lon.toInt
      val d00 = grid(GridLocation(lat, lon))
      val d01 = grid(GridLocation(lat + 1, lon))
      val d10 = grid(GridLocation(lat, lon + 1))
      val d11 = grid(GridLocation(lat + 1, lon + 1))
      bilinearInterpolation(CellPoint(location.lon - lon, location.lat - lat), d00, d01, d10, d11)
    }

    val offX = tile.x * width
    val offY = tile.y * height
    val offZ = tile.zoom
    val coords = for {
      i <- 0 until height
      j <- 0 until width
    } yield (i, j)

    val pixels = coords.par
      .map({case (y, x) => Tile(x + offX, y + offY, power + offZ)})
      .map(Interaction.tileLocation)
      .map(getBiInterpolation)
      .map(Visualization.interpolateColor(colors, _))
      .map(color => Pixel(color.red, color.green, color.blue, 127))
      .toArray

    Image(width, height, pixels)
  }

}
