package jukaiScala.keras


/**
  * Created by kenta-yoshinaga on 2016/12/28.
  */

import breeze.linalg.DenseMatrix

object Relu extends Functor{

  override def functorName = "Relu"

  override final def convert(data: DenseMatrix[Double]): DenseMatrix[Double] = data.map(x => if(x > 0.0) x else 0.0)

  def apply(x: DenseMatrix[Double]) = this.convert(x)
}
