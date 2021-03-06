package edu.ucla.sspace.graphical

//import scalala.library.Library.Axis.{Horizontal,Vertical}
//import scalala.tensor.dense.DenseMatrix
import breeze.linalg.DenseVector

import scala.util.Random
import scala.math.{E,pow,abs,exp,log,max}


object Util {
    val epsilon = 1.1920929e-07

    def argmax(data: Seq[Double]) = 
        data.zipWithIndex.max._2

    /*
    def logNormalize(m: DenseMatrix[Double]) = {
        val bestComponent = max(m, Horizontal)
        val z = m.mapTriples( (r,c,v) => v - bestComponent(r) )
        val out = sum(z.mapValues(exp), Vertical).mapValues(log)
        val w = z.mapTriples( (r,c,v) => exp(v - out(r)) + epsilon)
        val sums = sum(w, Vertical)
        w.mapTriples( (r,c,v) => v / sums(r) ).toDense
    }

    */
    def determiniate(sigma: DenseVector[Double]) =
        abs(sigma.valuesIterator.product) 

    def norm(v: DenseVector[Double]) = v / v.sum

    def unit(v: DenseVector[Double]) = v / v.norm(2)

    def sampleUnormalizedLogMultinomial(logProbs: Array[Double]) : Int = {
        val s = logProbs.foldLeft(0d)( (sum, lp) => addLog(sum, lp))
        var cut = Random.nextDouble()
        for ( (lp, i) <- logProbs.zipWithIndex ) {
            cut -= exp(lp - s)
            if (cut < 0)
                return i
        }
        return 0
    }

    def addLog(x: Double, y: Double) : Double = {
        if (x == 0)
            return y
        if (y == 0)
            return x
        if (x-y > 16)
            return x
        if (x > y) 
            return x + log(1 + exp(y-x))
        if (y-x > 16)
            return y
        return y + log(1 + exp(x-y))
    }
}
