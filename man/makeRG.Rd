\name{makeRG}
\alias{makeRG}
\title{Creation of a RGList object}
\description{
This function creates a RGList object.
}
\usage{
makeRG(mageOM, genes, directory = ".", QTD = "none", arrayID = "none", DED = "none",  name.Rf = "none", name.Rb = "none", name.Gf = "none" ,name.Gb = "none")
}

\arguments{
  \item{mageOM}{Reference to MAGE Object Model}
  \item{directory}{Directory which contains the MAGEML files that need to be imported}
  \item{genes}{the Gnames marrayInfo object e.g. created by getGnames}
  \item{QTD}{the Quantitation Type Dimension}
  \item{arrayID}{Array identifier}
  \item{DED}{the Design Element Dimension}
  \item{name.Rf}{the name of the red foreground intensity}
  \item{name.Rb}{the name of the red background intensity}
  \item{name.Gf}{the name of the green foreground intensity}
  \item{name.Gb}{the name of the green background intensity}

}

\references{Spellman PT, Miller M, Stewart J, Troup C, Sarkans U, Chervitz S, Bernhart D, Sherlock G, Ball C, Lepage M, Swiatek M, Marks WL, Goncalves J, Markel S, Iordan D, Shojatalab M, Pizarro A, White J, Hubley R, Deutsch E, Senger M, Aronow BJ, Robinson A, Bassett D, Stoeckert CJ Jr and Brazma A.
Design and implementation of microarray gene expression markup language (MAGE-ML).
Genome Biol. 2002 Aug 23;3(9):RESEARCH0046.
}

\author{Steffen Durinck, \url{http://www.esat.kuleuven.ac.be/~sdurinck}\cr 
Joke Allemeersch, \url{http://www.esat.kuleuven.ac.be/~jallemee}.}

\seealso{\code{\link{importMAGEOM}}, \code{\link{getArrayLayoutLimma}}}

\examples{
if(interactive()){
data<-system.file("MAGEMLdata", package="RMAGEML")
#To obtain a marrayInfo object containing the database identifiers of the features present on the array.#
mageom<-importMAGEOM(directory=data)
genes<-getArrayLayoutLimma(mageom, arrayID="A-MEXP-14", DED = "DED:707")
makeRG(mageOM=mageom, genes = genes, directory=data, arrayID = "A-MEXP-14", DED = "DED:707", QTD = "QTD:707", name.Rf = "QT:F635 Mean", name.Rb = "QT:B635 Median", name.Gf = "QT:F532 Mean" ,name.Gb = "QT:B532 Median")
}
}
\keyword{methods}
