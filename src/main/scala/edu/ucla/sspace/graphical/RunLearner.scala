package edu.ucla.sspace.graphical

import edu.ucla.sspace.graphical.gibbs.{GibbsNaiveBayes,FiniteGaussianMixtureModel}

import scalala.tensor.dense.DenseVector

import scala.io.Source

import java.io.PrintWriter


object RunLearner {
    def main(args:Array[String]) {
        if (args.size != 4) {
            printf("usage: RunLearner <learnerType> <data.mat> <numClusters> <outFile>\n")
            System.exit(1)
        }

        val data = Source.fromFile(args(1)).getLines.map( line => 
                DenseVector[Double](line.split("\\s+").map(_.toDouble)).t
        ).toList

        val nTrials = 200
        val k = args(2).toInt
        val learner = args(0) match {
            case "nb" => new GibbsNaiveBayes(
                nTrials, 
                DenseVector[Double](Array.fill(k)(1d))t,
                DenseVector[Double](Array.fill(2)(1d)).t)
            case "gmm" => new FiniteGaussianMixtureModel(nTrials, 5)
            case "km" => new FiniteGaussianMixtureModel(nTrials, 1, true)
            case "vdpmm" => new varbayes.DirichletProcessMixtureModel(nTrials, 1)
            case "gdpmm" => new gibbs.DirichletProcessMixtureModel(nTrials, 1)
        }

        val assignments = learner.train(data.toList, k)
        val w = new PrintWriter(args(3))
        w.println("X Y Group")
        for ( (d, l) <- data.zip(assignments))
            w.println("%f %f %d".format(d(0), d(1), l))
        w.close
    }
}
