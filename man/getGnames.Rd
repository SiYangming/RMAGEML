\name{getGnames}
\alias{getGnames}
\title{Creation of a Gnames marrayInfo object}
\description{
This function creates a marrayInfo object containing the database identifiers of the features present on the array.
}
\usage{
getGnames( mageOM, arrayID = "none", DED = "none", db = "none", package = NULL )
}

\arguments{
  \item{mageOM}{Reference to MAGE Object Model}
  \item{arrayID}{Array identifier}
  \item{DED}{the DesignElement Dimension}
  \item{db}{database from which identifiers should be retrieved}
  \item{package}{the package to which you want to export to, either limma, marray or eset}
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
data<-system.file("MAGEMLdata", package="RMAGEML")
#To obtain a marrayInfo object containing the database identifiers of the features present on the array.#
mageom<-importMAGEOM(directory=data)
getGnames(mageom, DED="DED:707", package = "marray")
}
}
\keyword{methods}
