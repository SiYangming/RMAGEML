\name{writeMAGEML}
\alias{writeMAGEML}
\title{write MAGEML documents}
\description{
This function writes MAGEML documents from MAGE Object references.
}
\usage{
writeMAGEML( mageOM = NULL, directory = ".", file = NULL)
}

\arguments{
  
  \item{mageOM}{Reference to MAGE Object Model}
  \item{directory}{Directory to which the MAGEML file should be written to}
  \item{file}{Filename.  Note should have extension xml e.g. update.xml}
}

\references{Spellman PT, Miller M, Stewart J, Troup C, Sarkans U, Chervitz S, Bernhart D, Sherlock G, Ball C, Lepage M, Swiatek M, Marks WL, Goncalves J, Markel S, Iordan D, Shojatalab M, Pizarro A, White J, Hubley R, Deutsch E, Senger M, Aronow BJ, Robinson A, Bassett D, Stoeckert CJ Jr and Brazma A.
Design and implementation of microarray gene expression markup language (MAGE-ML).
Genome Biol. 2002 Aug 23;3(9):RESEARCH0046.
}

\author{Steffen Durinck, \url{http://www.esat.kuleuven.ac.be/~sdurinck}\cr 
Joke Allemeersch, \url{http://www.esat.kuleuven.ac.be/~jallemee}.}

\seealso{\code{\link{importMAGEOM}},\code{\link{writeMAGEML}},\code{\link{addDerivedData}},\code{\link{addNormToMAGEML}}}

\examples{
#data<-system.file("MAGEMLdata", package="RMAGEML")
#mageom<-importMAGEOM(directory = data)
#writeMAGEML(mageOM = mageom, directory = ".", file = "updatedMAGEML.xml")
}
\keyword{methods}

