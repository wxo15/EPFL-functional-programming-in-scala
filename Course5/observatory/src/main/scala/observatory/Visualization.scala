package observatory

import com.sksamuel.scrimage.{Image, Pixel}
import scala.math._

/**
  * 2nd milestone: basic visualization
  */
object Visualization extends VisualizationInterface {

  /**
    * @param temperatures Known temperatures: pairs containing a location and the temperature at this location
    * @param location Location where to predict the temperature
    * @return The predicted temperature at `location`
    */
  
  def degToRad(x: Double): Double = x * Pi / 180

  def predictTemperature(temperatures: Iterable[(Location, Temperature)], location: Location): Temperature = {
    def relativeDist(a: Location, b: Location): Double = {
      if (a == b) 0
      else if (a.lat == - b.lat && abs(a.lon - b.lon) % 360 == 180) Pi
      else {
        val dLambda = degToRad(a.lon - b.lon)
        val (phi1, phi2) = (degToRad(a.lat), degToRad(b.lat))

        acos(sin(phi1) * sin(phi2) + cos(phi1) * cos(phi2) * cos(dLambda))
      }
    }
    def averageTemperatures(input: Iterable[(Double, Temperature)]): Temperature = {
      type WeightedTemperature = (Temperature, Double, Int) // weighted temp, total weight, count of infinite
      def seqOp(wt: WeightedTemperature, k: (Double, Temperature)): WeightedTemperature = {
        val distance: Double = k._1
        if (wt._2.isInfinite) {
          if (abs(distance) < 1e-6) (wt._1 + k._2, Double.PositiveInfinity, wt._3 + 1) else wt
        } else {
          if (abs(distance) < 1e-6) (k._2, Double.PositiveInfinity, 1)
          else {
            val w = 1.0 / pow(distance, 2)
            (wt._1 + w * k._2, wt._2 + w, 0)
          }
        }
      }

      def combOp(a: WeightedTemperature, b: WeightedTemperature): WeightedTemperature = {
        if (a._2.isInfinite && b._2.isInfinite) (a._1 + b._1, Double.PositiveInfinity, a._3 + b._3)
        else if (a._2.isInfinite) a
        else if (b._2.isInfinite) b
        else (a._1 + b._1, a._2 + b._2, a._3 + b._3)
      }

      def toTemperature(a: WeightedTemperature): Temperature = {
        if (a._2.isInfinite) a._1 / a._3
        else if (a._2 != 0) a._1 / a._2
        else 0
      }

      toTemperature(input.aggregate((0.0, 0.0, 0))(seqOp, combOp))
    }
    
    averageTemperatures(temperatures.map {
      case (loc, t) => (relativeDist(loc, location), t)
    })
  }

  /**
    * @param points Pairs containing a value and its associated color
    * @param value The value to interpolate
    * @return The color that corresponds to `value`, according to the color scale defined by `points`
    */
  def interpolateColor(points: Iterable[(Temperature, Color)], value: Temperature): Color = {
    points.find(_._1 == value) match {
      case Some((_, c)) => c 
      case _ => {
        val minVal = points.minBy(_._1)
        val maxVal = points.maxBy(_._1)
        if (minVal._1 > value) minVal._2
        else if (maxVal._1 < value) maxVal._2
        else {
          val (smaller, bigger) = points.partition(_._1 < value)
          val a = smaller.maxBy(_._1)
          val b = bigger.minBy(_._1)
          val wa = 1 / abs(a._1 - value)
          val wb = 1 / abs(b._1 - value)
          def interp(x: Int, y: Int): Int =
                  ((wa * x + wb * y) / (wa + wb)).round.toInt
          val ca = a._2
          val cb = b._2
          Color(interp(ca.red, cb.red), interp(ca.green, cb.green), interp(ca.blue, cb.blue))
        } 
      } 
    }
  }

  /**
    * @param temperatures Known temperatures
    * @param colors Color scale
    * @return A 360×180 image where each pixel shows the predicted temperature at its location
    */
  def visualize(temperatures: Iterable[(Location, Temperature)], colors: Iterable[(Temperature, Color)]): Image = {
    
    def visualize2(temperatures: Iterable[(Location, Temperature)], colors: Iterable[(Temperature, Color)],
                w: Int, h: Int): Image = {

      def coordinateToLoc(x: Int, y: Int): Location = {
        Location(90 - y * (180.0 / h), x * (360.0 / w) - 180)
      }
      
      val pixels: Array[Pixel] = new Array[Pixel](w * h)

      for ( y <- (0 until h).par; x <- (0 until w).par) {
        val location = coordinateToLoc(x, y)
        val temperature = predictTemperature(temperatures, location)
        val color = interpolateColor(colors, temperature)

        pixels(y * w + x) = Pixel(color.red, color.green, color.blue, 255)
      }

      Image(w, h, pixels)
    }
    visualize2(temperatures, colors, 360, 180)
  }

}

