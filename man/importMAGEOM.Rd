\name{importMAGEOM}
\alias{importMAGEOM}
\title{import MAGEOM of MAGEML documents}
\description{
This function imports the MAGE Object Model MAGEML documents and creates a reference to this object.
}
\usage{
importMAGEOM(directory = ".")
}

\arguments{
  \item{directory}{Directory which contains the MAGEML files that need to be imported}
}

\references{Spellman PT, Miller M, Stewart J, Troup C, Sarkans U, Chervitz S, Bernhart D, Sherlock G, Ball C, Lepage M, Swiatek M, Marks WL, Goncalves J, Markel S, Iordan D, Shojatalab M, Pizarro A, White J, Hubley R, Deutsch E, Senger M, Aronow BJ, Robinson A, Bassett D, Stoeckert CJ Jr and Brazma A.
Design and implementation of microarray gene expression markup language (MAGE-ML).
Genome Biol. 2002 Aug 23;3(9):RESEARCH0046.
}

\author{Steffen Durinck, \url{http://www.esat.kuleuven.ac.be/~sdurinck}\cr 
Joke Allemeersch, \url{http://www.esat.kuleuven.ac.be/~jallemee}.}

\seealso{\code{\link{getGnames}},\code{\link{getArrayLayout}}}

\examples{
if(interactive()){
data<-system.file("MAGEMLdata", package="RMAGEML")
#will return a reference to the MAGE Object Model.#
importMAGEOM(directory=data)
}
}
\keyword{methods}
