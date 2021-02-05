\name{getArrayID}
\alias{getArrayID}
\title{Get  ArrayID's}
\description{This function retrieves a list of ID's of the arrays that were used in the experiment.}
\usage{getArrayID( mageOM = NULL )}

\arguments{
  \item{mageOM}{R reference to MAGE Object Model}
}

\references{Spellman PT, Miller M, Stewart J, Troup C, Sarkans U, Chervitz S, Bernhart D, Sherlock G, Ball C, Lepage M, Swiatek M, Marks WL, Goncalves J, Markel S, Iordan D, Shojatalab M, Pizarro A, White J, Hubley R, Deutsch E, Senger M, Aronow BJ, Robinson A, Bassett D, Stoeckert CJ Jr and Brazma A.
Design and implementation of microarray gene expression markup language (MAGE-ML).
Genome Biol. 2002 Aug 23;3(9):RESEARCH0046.
}

\author{Steffen Durinck, \url{http://www.esat.kuleuven.ac.be/~sdurinck}\cr 
  Joke Allemeersch, \url{http://www.esat.kuleuven.ac.be/~jallemee}.}

\seealso{\code{\link{importMAGEOM}}}

\examples{
if(interactive()){
data <- system.file( "MAGEMLdata", package = "RMAGEML" )
mageom <- importMAGEOM( directory = data )
getArrayID( mageOM = mageom )
}
}
\keyword{methods}

