\name{importMAGEML}
\alias{importMAGEML}
\title{import MAGEML documents}
\description{
This function imports MAGEML documents and creates a marrayRaw object.
}
\usage{
importMAGEML(directory = ".", package = "marray", arrayID = "none", DED = "none", QTD = "none", name.Rf="none", name.Rb="none", name.Gf="none" ,name.Gb =
"none", derivQT = "none", db = "none")
}

\arguments{
  \item{directory}{Directory which contains the MAGEML files that need to be imported}
  \item{package}{name of package to which the MAGEML should be imported}
  \item{arrayID}{Array identifier}	
  \item{DED}{the DesignElement Dimension}
  \item{QTD}{the Quantitation Type Dimension}
  \item{name.Rf}{the name of the red foreground intensity}
  \item{name.Rb}{the name of the red background intensity}
  \item{name.Gf}{the name of the green foreground intensity}
  \item{name.Gb}{the name of the green background intensity}
  \item{derivQT}{the name of the derived QuantitationType}
  \item{db}{database identifier which should be retrieved}
}

\references{Spellman PT, Miller M, Stewart J, Troup C, Sarkans U, Chervitz S, Bernhart D, Sherlock G, Ball C, Lepage M, Swiatek M, Marks WL, Goncalves J, Markel S, Iordan D, Shojatalab M, Pizarro A, White J, Hubley R, Deutsch E, Senger M, Aronow BJ, Robinson A, Bassett D, Stoeckert CJ Jr and Brazma A.
Design and implementation of microarray gene expression markup language (MAGE-ML).
Genome Biol. 2002 Aug 23;3(9):RESEARCH0046.
}

\author{Steffen Durinck, \url{http://www.esat.kuleuven.ac.be/~sdurinck}\cr 
Joke Allemeersch, \url{http://www.esat.kuleuven.ac.be/~jallemee}.}

\seealso{\code{\link{getGnames}},\code{\link{getArrayLayout}}, \code{\link{makeMarrayRaw}}}

\examples{
if(interactive()){
data<-system.file("MAGEMLdata", package="RMAGEML")
#will return marrayRaw object.#
importMAGEML(directory = data, package = "marray", arrayID = "A-MEXP-14", DED = "DED:707", QTD = "QTD:707", name.Rf = "QT:F635 Mean", name.Rb = "QT:B635 Median", name.Gf = "QT:F532 Mean" ,name.Gb = "QT:B532 Median")
}
}
\keyword{methods}
