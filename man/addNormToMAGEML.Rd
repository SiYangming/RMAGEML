\name{addNormToMAGEML}
\alias{addNormToMAGEML}
\title{Adding a norm object to an existing MAGEML document}
\description{
This function adds normalised data object obtained with limma or marray package to a MAGEML document.}
\usage{
addNormToMAGEML(mageOM = NULL, norm = NULL, outputDirectory = ".",
externalDataFiles = NULL, protocolID = NULL, protocol = "none", date="NA", qtID =
NULL, qtName = NULL, qtDescription = NULL, qtScale = NULL, qtDataType = NULL, qtDimID =
NULL,transformationID = NULL, arrayID = "none", DED = "none", BADIDs = NULL, derivedBioAssayIDs = NULL, derivedBioAssayDataIDs = NULL, rawDataFiles = NULL)
}

\arguments{
  \item{norm}{Normalised limma (MAList) or marray (marrayNorm) object}
  \item{mageOM}{Reference to MAGE Object Model, generated by
    importMAGEOM() method.}
  \item{outputDirectory}{Directory where updated MAGEML document will
    be written to}
  \item{externalDataFiles}{List of names for external data files
    associated with a DerivedBioAssayData object.  Each filename should
    end with .txt as this is the standard output file format}
  \item{protocolID}{ID you want to give to the protocol, e.g. P-normalised-1}
  \item{protocol}{Describiption of the used protocol}
  \item{date}{Date when protocol was applied}
  \item{qtID}{Identifier used for the QuantitationType (the normalised
    value)}
  \item{qtName}{Name for the QuantitationType}
  \item{qtDescription}{Description of the QuantitationType}
  \item{qtScale}{Scale of the QuantitationType, e.g. linear}
  \item{qtDataType}{DataType of the QuantitationType e.g. scalar}
  \item{qtDimID}{A new QuantitationTypeDimension will be generated
    you'll have to specify it's identifier}
  \item{transformationID}{Identifier for the applied transformation e.g.
    TFM:1}
  \item{arrayID}{Array Identifier}
  \item{DED}{ DesignElementDimension corresponding to the features that
    are present in the normalised object, if only one dimension present
    in original MAGEML file then this parameter should not be specified}
  \item{BADIDs}{List of BioAssayData identifier}
  \item{derivedBioAssayIDs}{List of identifiers for the DerivedBioAssay e.g. DBA-1}
  \item{derivedBioAssayDataIDs}{List of identifiers for the DerivedBioAssayData}
  \item{rawDataFiles}{raw data filenames}
}

\references{Spellman PT, Miller M, Stewart J, Troup C, Sarkans U, Chervitz S, Bernhart D, Sherlock G, Ball C, Lepage M, Swiatek M, Marks WL, Goncalves J, Markel S, Iordan D, Shojatalab M, Pizarro A, White J, Hubley R, Deutsch E, Senger M, Aronow BJ, Robinson A, Bassett D, Stoeckert CJ Jr and Brazma A.
Design and implementation of microarray gene expression markup language (MAGE-ML).
Genome Biol. 2002 Aug 23;3(9):RESEARCH0046.}

\author{Steffen Durinck, \url{http://www.esat.kuleuven.ac.be/~sdurinck}\cr 
  Joke Allemeersch, \url{http://www.esat.kuleuven.ac.be/~jallemee}.}

\seealso{\code{\link{importMAGEOM}},\code{\link{writeMAGEML}}}

\examples{
#library(RMAGEML)
#raw<- importMAGEML(directory = "/home/steffen/data/MEXP-14")
#norm<-maNorm(raw)
#mageom <- importMAGEOM(directory = "/home/steffen/data/MEXP-14")
#outputDirectory <- "/home/steffen/XMLout"
#externalDataFile <- "deriv_test.txt"
#protocolID <- "P-CAGE-test"
#protocol <- "This is a test protocol! Applied maNorm."
#qtID <- c("esat.kuleuven.ac.be:quantT-1-test", "esat.kuleuven.ac.be:quantT-2-test")
#qtName <- c("quantitation Name 1", "quantitation Name 2")
#qtScale <- c("linear","linear")
#
#qtDataType <- c("scalar","scalar")
#qtDimID <- "esat.kuleuven.ac.be:QTD-test"
#BADIDs <- c("esat.kuleuven.ac.be:BAD-test1","esat.kuleuven.ac.be:BAD-test2")
#derivedBioAssayIDs <- c("esat.kuleuven.ac.be:DBA-test1","esat.kuleuven.ac.be:DBA-test2")
#derivedBioAssayDataIDs <- c("esat.kuleuven.ac.be:DBD-test1", "esat.kuleuven.ac.be:DBD-test2")
#tfm<-"TFM-1"
#addNormToMAGEML(mageOM = mageom, norm = norm, outputDirectory = outputDirectory, externalDataFile = externalDataFile, protocolID = protocolID, protocol = protocol,  qtID = qtID, qtName = qtName, qtDescription=NULL, qtScale = qtScale, qtDataType = qtDataType, qtDimID = qtDimID,transformationID=tfm, arrayID="none", DED = "none", BADIDs = BADIDs, derivedBioAssayIDs = derivedBioAssayIDs, derivedBioAssayDataIDs = derivedBioAssayDataIDs)

}
\keyword{methods}
