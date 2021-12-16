package observatory

/**
  * 4th milestone: value-added information
  */
object Manipulation extends ManipulationInterface {
  
  class Grid {
    
    private var temps: Array[Temperature] = new Array[Temperature](360*180)

    private def address(location: Location): Int = {
      val x = location.lon + 180
      val y = location.lat + 89
      (y * 360 + x).toInt
    }

    def set(location: Location, temp: Temperature): Unit = {
      temps(address(location)) = temp
    }

    def get(location: Location): Temperature = {
      temps(address(location))
    }

    def precompute(temps: Iterable[(Location, Temperature)]): Unit = {
      for {
        lat <- Range(90, -90, -1)
        lon <- -180 until 180
      } set(Location(lat, lon), Visualization.predictTemperature(temps, Location(lat, lon)))
    }

    def merge(that: Grid): Grid = {
      temps.indices.foreach(i => this.temps(i) += that.temps(i))
      this
    }

    def /=(denominator: Double): Grid = {
      temps = temps.map(_ / denominator)
      this
    }

    def -=(that: GridLocation => Temperature): Grid = {
      for {
        lat <- Range(90, -90, -1)
        lon <- -180 until 180
      } set(Location(lat, lon), get(Location(lat, lon)) - that(GridLocation(lat, lon)))
      this
    }
  }

  
  /**
    * @param temperatures Known temperatures
    * @return A function that, given a latitude in [-89, 90] and a longitude in [-180, 179],
    *         returns the predicted temperature at this location
    */
  def makeGrid(temperatures: Iterable[(Location, Temperature)]): GridLocation => Temperature = {
    val grid = new Grid()
    grid.precompute(temperatures)
    (gl: GridLocation) => grid.get(Location(gl.lat, gl.lon))
  }

  /**
    * @param temperaturess Sequence of known temperatures over the years (each element of the collection
    *                      is a collection of pairs of location and temperature)
    * @return A function that, given a latitude and a longitude, returns the average temperature at this location
    */
  def average(temperaturess: Iterable[Iterable[(Location, Temperature)]]): GridLocation => Temperature = {
    val grid = temperaturess.par
      .map({ case (i) => {
        val grid = new Grid
        grid.precompute(i)
        grid
      }})
      .reduce((a: Grid, b: Grid) => a.merge(b))
    grid /= temperaturess.size
    (gl: GridLocation) => grid.get(Location(gl.lat, gl.lon))
  }

  /**
    * @param temperatures Known temperatures
    * @param normals A grid containing the “normal” temperatures
    * @return A grid containing the deviations compared to the normal temperatures
    */
  def deviation(temperatures: Iterable[(Location, Temperature)], normals: GridLocation => Temperature): GridLocation => Temperature = {
    val grid = new Grid()
    grid.precompute(temperatures)
    grid -= normals
    (gl: GridLocation) => grid.get(Location(gl.lat, gl.lon))
  }
  
}
