package Models

import java.time.{LocalDate, LocalTime}

import scala.beans.BeanProperty

case class Highscore(@BeanProperty name: String,
                     @BeanProperty date: LocalDate,
                     @BeanProperty time: LocalTime)
