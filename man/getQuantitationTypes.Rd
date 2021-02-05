\name{getQuantitationTypes}
\alias{getQuantitationTypes}
\title{Retrieves QuantitationTypes from an experiment}
\description{
This function function retrieves QuantitationTypes from an experiment.
}
\usage{
getQuantitationTypes( mageOM = NULL , QTD = "none" )
}

\arguments{
  \item{mageOM}{ R reference to MAGE object model }
  \item{QTD}{ QuantitationTypeDimension:  If not specified and there is more than one QTD present, selection pop-up menus will appear. }
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
getQuantitationTypes( mageOM = mageom , QTD = "none")
}
}
\keyword{methods}
