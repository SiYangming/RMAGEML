.packageName <- "RMAGEML"


##############################################################################
##                                                                          ##
## This is an R Package for handling MAGE-ML documents in Bioconductor.     ##
##                                                                          ##
##@Authors  Steffen Durinck, Joke Allemeersch, Vincent Carey                ##
## ESAT-SCD KULeuven                                                        ##
##                                                                          ##
##############################################################################


.checkJVM <- function() {
	if (!exists("..JVMAlive")) stop("No live JVM; run createJVM(opts)")
	if (!(..JVMAlive)) stop("A dead JVM is present; cannot restart")
	invisible(NULL)
	}

setClass("JavaVMRef", representation(jvmptr="externalptr",
	optString="character"))

#setMethod("show", "JavaVMRef", function(object) {
#	.checkJVM()
#	cat("arji package JavaVM reference, ")
#	cat("invoked with options:\n ", object@optString, "\n")
#	cat("address: ")
#	print(object@jvmptr)
#	cat("\n")
#	})
#if(!isGeneric(".jvmptr"))
#	setGeneric(".jvmptr", function(x)standardGeneric(".jvmptr"))
#setMethod(".jvmptr", "JavaVMRef", function(x) x@jvmptr)

###Virtual Machine methods

.createJVM <- function(globalize=TRUE, RobjName="..rmagemlJVM") {
  if (exists("..JVMAlive")) {
    if (..JVMAlive) {
      return(NULL)
    }
    else {
      stop("A dead JVM is noted in .GlobalEnv; cannot restart.")
    }
  }
  
  #setting up classpath
  
  dir1<-system.file("classes", package="RMAGEML")
  dir2<-system.file("jaxp", package="RMAGEML")
  dir3<-system.file("MAGEstk.jar", package="RMAGEML")
  if(.Platform$OS.type == "windows"){
    classpath <- paste("-Djava.class.path=",paste(dir1,paste(dir2,"lib/endorsed/xercesImpl.jar", sep="/" ),dir3, sep=";"),sep="")
  }
  else{
    classpath <- paste("-Djava.class.path=",paste(dir1,paste(dir2,"lib/endorsed/xercesImpl.jar", sep="/" ),dir3, sep=":"),sep="")
 }
  jvmptr <- .Call("arji_createJVM", classpath, PACKAGE="RMAGEML")
  if (!is.null(jvmptr)) assign("..JVMAlive", TRUE, env=.GlobalEnv)
  jvmobj <- new("JavaVMRef", jvmptr=jvmptr, optString=classpath)
  writeLines("- Java Virtual Machine is running -")
  if (globalize) {
    assign(RobjName, jvmobj, env=.GlobalEnv)
    invisible(NULL)
  }
  else jvmobj
}

.destroyJVM <- function(jvmref=..rmagemlJVM) {
	if (!exists("..JVMAlive") || !(..JVMAlive)) {
		warning("No live JVM is present")
		return(NULL)
	}
   if (!is(jvmref, "JavaVMRef")) stop("did not receive instance of JavaVMRef")
   ans <- .Call("arji_destroyJavaVM", ..rmagemlJVM@jvmptr, PACKAGE="RMAGEML")
   if (is.null(ans)) assign("..JVMAlive", FALSE, env=.GlobalEnv)
   "JVM destroyed"
}


importMAGEML <- function( directory = ".", package = "marray", arrayID = "none", DED = "none", QTD = "none", name.Rf="none", name.Rb="none", name.Gf="none" ,name.Gb = "none", derivQT = "none", db = "none"){
  
  if ( package != "marray" && package !="limma" && package != "eset"){
    writeLines( "The current MAGEML package only imports MAGEML to the marray and limma packages of BioConductor" )
  }
  if( package == "marray" ){
   mageOM <- importMAGEOM( directory );
  .Call("getVoidVoid","init", mageOM, ..rmagemlJVM@jvmptr, PACKAGE="RMAGEML");  
   Gnames <- getGnames( mageOM, arrayID = arrayID, DED = DED, db = db, package = package )
   Layout <- getArrayLayout( mageOM, arrayID = arrayID, DED = DED )
   writeLines( "making Layout and Gnames objects" )
   raw <- makeMarrayRaw( mageOM, Layout,Gnames,directory, arrayID = arrayID, DED = DED, QTD = QTD, name.Rf = name.Rf, name.Rb = name.Rb, name.Gf=name.Gf ,name.Gb = name.Gb )
  .Call("destroyOM", mageOM, ..rmagemlJVM@jvmptr, PACKAGE="RMAGEML");   
   return(raw)
 }
 if (package == "limma"){
   mageOM <- importMAGEOM( directory );
   .Call("getVoidVoid","init", mageOM, ..rmagemlJVM@jvmptr, PACKAGE="RMAGEML");  
  
   genes <- getArrayLayoutLimma( mageOM, arrayID = arrayID, DED = DED, db = db)
   RG <- makeRG( mageOM = mageOM, genes = genes, directory = directory, arrayID = arrayID, DED = DED, QTD = QTD, name.Rf = name.Rf, name.Rb = name.Rb, name.Gf = name.Gf ,name.Gb = name.Gb)
   .Call("destroyOM", mageOM, ..rmagemlJVM@jvmptr, PACKAGE="RMAGEML");   
   return(RG)
 }
  if (package == "eset"){
    mageOM <- importMAGEOM( directory );
    .Call("getVoidVoid","init", mageOM, ..rmagemlJVM@jvmptr, PACKAGE="RMAGEML");
    genes <- getGnames( mageOM, arrayID = arrayID, DED = DED, db = db, package = package )
    eset <- makeEset( mageOM = mageOM, genes = genes, directory = directory, arrayID = arrayID,DED = DED, QTD = QTD, derivQT = derivQT)
    .Call("destroyOM", mageOM, ..rmagemlJVM@jvmptr, PACKAGE="RMAGEML");   
    return(eset)
  } 
}


####################################################
#
#import MAGEML to marray package
#
####################################################


getGnames <- function( mageOM , arrayID = "none", DED = "none", db = "none", package = NULL){
  
  .Call("getVoidVoid","init", mageOM, ..rmagemlJVM@jvmptr, PACKAGE="RMAGEML");
  .Call("importFeatures", package , arrayID, DED, db,mageOM, ..rmagemlJVM@jvmptr, PACKAGE="RMAGEML")
  databaseEntry <- .Call("getVoidArrayStr","getBioSequenceID",mageOM ,..rmagemlJVM@jvmptr, PACKAGE="RMAGEML")
  names <- .Call("getVoidArrayStr","getBioSequenceNames",mageOM,..rmagemlJVM@jvmptr, PACKAGE="RMAGEML")
  db <- .Call("getVoidStr","getBioSeqDB",mageOM,..rmagemlJVM@jvmptr, PACKAGE="RMAGEML")
  mInfo <- new( "marrayInfo" )
  maInfo <- as.data.frame( databaseEntry )
  names(maInfo) <- c( "Identification" )
  mInfo@maLabels <- names
  mInfo@maInfo <- maInfo
  mInfo@maNotes <- c( paste("Identifiers refer to database:",db) )
  return( mInfo )
}

getArrayLayout<-function(mageOM, arrayID = "none", DED = "none"){

  .Call("getVoidVoid","init", mageOM, ..rmagemlJVM@jvmptr, PACKAGE="RMAGEML"); 
  layout <- .Call("getGridDimensions", arrayID, DED, mageOM, ..rmagemlJVM@jvmptr, PACKAGE="RMAGEML");
  ngr <- layout[4]
  ngc <- layout[3]
  nsr <- layout[1]
  nsc <- layout[2]
  nspots <- as.integer( ngr ) * as.integer( ngc ) * as.integer( nsr ) * as.integer( nsc )

  mlayout <- new("marrayLayout", maNgr = as.integer( ngr ),
                 maNgc = as.integer( ngc ),
                 maNsr = as.integer( nsr ),
                 maNsc = as.integer( nsc ),
                 maNspots = nspots,maNotes="")
  return( mlayout )
}


makeMarrayRaw <- function(mageOM, layout, gnames, directory = ".", arrayID = "none", DED="none", QTD = "none", name.Rf="none", name.Rb="none", name.Gf="none" ,name.Gb = "none"){
  .Call("getVoidVoid","init", mageOM, ..rmagemlJVM@jvmptr, PACKAGE="RMAGEML"); 
  quantitationTypes <- .Call("selectQTypes", QTD, name.Rf, name.Rb, name.Gf,name.Gb, mageOM,..rmagemlJVM@jvmptr, PACKAGE="RMAGEML")
  fileNames<-.Call("getExternalData","marray", arrayID, DED, mageOM, ..rmagemlJVM@jvmptr, PACKAGE="RMAGEML")

  if(fileNames[1] == "none"){
   stop("You selected incompatible QuantitationType and DesignElementDimensions...please try again using a different QTD - DED combination")
 }
 
 fullnames<-file.path(directory,fileNames)
 Gf<-Gb<-Rf<-Rb<-W<- NULL
 names(Gb)<-names(Rf)<-names(Rb)<-names(W)<-NULL
 fileLabels<-NULL
 for ( f in fileNames ){
    writeLines( paste("Reading ",f) )
    dat<-read.table(file=file.path(directory,f),header=FALSE,sep="\t")
    Gf<-cbind(Gf,as.numeric(dat[[quantitationTypes[1]]]))
    Gb<-cbind(Gb,as.numeric(dat[[quantitationTypes[2]]]))
    Rf<-cbind(Rf,as.numeric(dat[[quantitationTypes[3]]]))
    Rb<-cbind(Rb,as.numeric(dat[[quantitationTypes[4]]]))
  }
  targets<-.Call("getVoidArrayStr","getTargets", mageOM, ..rmagemlJVM@jvmptr, PACKAGE="RMAGEML");
  nrrow<-length( targets )
  TargetRG<-matrix(data = NA, nrow = nrrow, ncol = 2)
  colnames( TargetRG )<-c("Cy3","Cy5")
  TargetRG<-as.data.frame( TargetRG )
  rownames <- rep("U",nrrow)
  for (i in 1:nrrow){

     hybInfo<-strsplit(targets[i],"\\$")
#     count <- 0;
#     ok <- FALSE;
#     while(count < 2 && !ok ){
#     	if(hybInfo[[1]][4] != "Cy5" && hybInfo[[1]][4] != "Cy3" && hybInfo[[1]][4] != "cy5" && hybInfo[[1]][4] != "cy3"){ #either no cy's present or bad bit
#      	x <- charToRaw(hybInfo[[1]][4])
#      	hybInfo[[1]][4] <- rawToChar(x[1:length(x)-1])
#     	}
#       else{
#        ok <- TRUE
#       }
#       count = count+1;
#     }
     	
     if(hybInfo[[1]][4] != "Cy5" && hybInfo[[1]][4] != "Cy3" && hybInfo[[1]][4] != "cy5" && hybInfo[[1]][4] != "cy3"){ #No cy's present
      stop("No cy3 and cy5 present or very bad encoding");
     }

     if (strsplit(hybInfo[[1]][2], split="")[[1]][3]==3){
        TargetRG[i,1] <- hybInfo[[1]][1]
     }
     if (strsplit(hybInfo[[1]][2], split="")[[1]][3]==5){
        TargetRG[i,2]<-hybInfo[[1]][1]
     }
     if (strsplit(hybInfo[[1]][4], split="")[[1]][3]==3){
        TargetRG[i,1] <- hybInfo[[1]][3]
     }
     if (strsplit(hybInfo[[1]][4], split="")[[1]][3]==5){
         TargetRG[i,2]<-hybInfo[[1]][3]
     }
   }
   target<-new("marrayInfo",maLabels=fileNames,maInfo=TargetRG,maNotes="Description of the targets")
   mraw<-new("marrayRaw", maRf=Rf, maRb=Rb, maGf=Gf, maGb=Gb, maLayout=layout, maGnames=gnames, maTargets=target)
 return( mraw )
}

####################################################
#
#import MAGEML to limma package
#
####################################################

getArrayLayoutLimma <- function(mageOM, arrayID = "none", DED = "none", db = "none"){

  .Call("getVoidVoid","init", mageOM, ..rmagemlJVM@jvmptr, PACKAGE="RMAGEML");
  .Call("importFeatures", "limma" , arrayID, DED, db,mageOM, ..rmagemlJVM@jvmptr, PACKAGE="RMAGEML")
  ID <- .Call("getVoidArrayStr","getBioSequenceID",mageOM ,..rmagemlJVM@jvmptr, PACKAGE="RMAGEML")
  NAME <- .Call("getVoidArrayStr","getBioSequenceNames",mageOM,..rmagemlJVM@jvmptr, PACKAGE="RMAGEML")
 
  ROW <- .Call("getVoidArrayInt","getRows", mageOM, ..rmagemlJVM@jvmptr, PACKAGE="RMAGEML");
  COLUMN <- .Call("getVoidArrayInt","getColumns", mageOM, ..rmagemlJVM@jvmptr, PACKAGE="RMAGEML")
  BLOCK <- .Call("getVoidArrayInt","getZones",mageOM, ..rmagemlJVM@jvmptr, PACKAGE="RMAGEML")
  genes <- as.data.frame(list(Block=BLOCK,Row=ROW,Column=COLUMN,ID=ID,Name= NAME))
  return( genes )

}

makeRG <- function(mageOM,genes, directory = ".", QTD = "none", arrayID = "none", DED="none", name.Rf="none", name.Rb="none", name.Gf="none" ,name.Gb = "none"){

  .Call("getVoidVoid","init", mageOM, ..rmagemlJVM@jvmptr, PACKAGE="RMAGEML");
  ngenes <- length( genes$ID )
  quantitationTypes <- .Call("selectQTypes", QTD, name.Rf, name.Rb, name.Gf,name.Gb, mageOM,..rmagemlJVM@jvmptr, PACKAGE="RMAGEML")
  fileNames<-.Call("getExternalData","limma", arrayID, DED, mageOM, ..rmagemlJVM@jvmptr, PACKAGE="RMAGEML")
  
  if(fileNames[1] == "none"){
    stop("You selected incompatible QuantitationType and DesignElementDimensions...please try again using a different QTD - DED combination")
  }

 nslides <- length( fileNames )
 fullnames<-file.path(directory,fileNames)
 Gf<-Gb<-Rf<-Rb<-W<- NULL
 names(Gb)<-names(Rf)<-names(Rb)<-names(W)<-NULL
 fileLabels<-NULL
 Y <- matrix(0,ngenes,nslides)
 colnames(Y) <- fileNames
 RG <- list(R=Y,G=Y,Rb=Y,Gb=Y, genes=genes)

 for (i in 1: nslides){
    writeLines(paste("Reading ",fileNames[i]))
    dat<-read.table(file=file.path(directory,fileNames[i]),header=FALSE,sep="\t")
    RG$G[,i]<-as.numeric(dat[,quantitationTypes[1]])
    RG$Gb[,i]<-as.numeric(dat[,quantitationTypes[2]])
    RG$R[,i]<-as.numeric(dat[,quantitationTypes[3]])
    RG$Rb[,i]<-as.numeric(dat[,quantitationTypes[4]])
  }

 RGL <- new("RGList", RG)
 return( RGL )

}

makeEset <- function(mageOM, genes, directory = ".", QTD = "none", arrayID = "none", DED="none", derivQT = "none"){
 ngenes <- length( genes@maLabels )
 .Call("getVoidVoid","init", mageOM, ..rmagemlJVM@jvmptr, PACKAGE="RMAGEML");

 quantitationTypes <- .Call("selectQTypesDeriv", QTD, derivQT, mageOM, ..rmagemlJVM@jvmptr, PACKAGE="RMAGEML")
 fileNames <- .Call("getExternalData","limma", arrayID, DED, mageOM, ..rmagemlJVM@jvmptr, PACKAGE="RMAGEML")

 if(fileNames[1] == "none"){
   stop("You selected incompatible QuantitationType and DesignElementDimensions...please try again using a different QTD - DED combination")
 }
 fullnames<-file.path(directory,fileNames)
 fileLabels<-NULL
 nslides <- length(fileNames)
 Y <- matrix(0,ngenes,nslides)
 colnames(Y) <- fileNames
 rownames(Y) <- genes@maInfo$Identification
 for (i in 1: nslides){
    writeLines(paste("Reading ",fileNames[i]))
    dat<-read.table(file=file.path(directory,fileNames[i]),header=FALSE,sep="\t")
    Y[,i]<-as.numeric(dat[,quantitationTypes])
  }
 eset <- new("ExpressionSet", exprs = Y)
 return(eset)
  
}


###############################################
#
#Import the object model and obtain a reference
#to it.
#
###############################################


importMAGEOM<-function(directory = "."){
  .createJVM();
  dir=strsplit(directory,"/")
  if(dir[[1]][1]=="."|| dir[[1]][1]=="~") stop("Please specify the full path to the directory containing the MAGE-ML documents (do not use ~ or .).")
  writeLines("parsing MAGEML files")
  mageOM<-.Call("newRMAGESTK",directory,..rmagemlJVM@jvmptr, PACKAGE="RMAGEML")
  return( mageOM )
}



################################################
#
#writing MAGEML from BioConductor
#
#addNormToMAGEML is still work in progress
#and is not yet functional enough to be employed
#
################################################

addNormToMAGEML <- function(mageOM = NULL, norm = NULL, outputDirectory = ".", externalDataFiles = NULL, protocolID = NULL, protocol = "none", date ="NA", qtID = NULL, qtName = NULL,qtDescription = NULL, qtScale = NULL, qtDataType = NULL, qtDimID = NULL,transformationID = NULL, arrayID="none", DED = "none", BADIDs = NULL, derivedBioAssayIDs = NULL, derivedBioAssayDataIDs = NULL, rawDataFiles = NULL ){
  
  if ( is.null( externalDataFiles )){ #check as well if they end with txt!!
    stop("No filenames for output derivedFile found, please read the documentation and specify using parameter derivedFiles")
  }

  if ( is.null( protocolID )){
    stop("No protocol idenitifier found, please read the documentation and  specify using parameter protocolID")
  }
  
  if ( is.null( transformationID )){
    stop("No transformation idenitifier found, please read the documentation and  specify using parameter transformationID")
  }

  if ( is.null( protocol )){
    stop("No protocol found, please read the documentation and specify using parameter protocol")
  }

  if ( is.null( qtID )){
    stop("No QuantitationTypes identifiers found, please read the documentation and specify using parameter qtIDs")
  }

  if ( is.null( qtName ) && !is.null( qtID )){
    warning("No QuantitationTypes names found, continuing without names for QuantitationTypes")
  }
  if ( is.null( qtDescription )){
    stop("No Description of QuantitationTypes found, please read the documentation and specify using parameter qtDescription")
  }
 
  if ( is.null( qtScale )){
    stop("No QuantitationType scales found, please read the documentation and specify using parameter qtScales")
  }

  if ( is.null( qtDataType )){
    stop("No QuantitationType DataTypes found, please read the documentation and specify using parameter qtDataTypes")
  }

  if ( is.null( qtDimID )){
    stop("No QuantitationTypeDimension identifier found, please read the documentation and specify using parameter qtDimID")
  }

  if ( is.null( BADIDs )){
    stop("No BioAssayDimension identifier found, please read the documentation and specify using parameter BAD")
  }

  if ( is.null( derivedBioAssayIDs )){
    stop("No derivedBioAssay identifier found, please read the documentation and specify using parameter derivedBioAssayID")
  }


  if ( is.null( derivedBioAssayDataIDs )){
    stop("No derivedBioAssayData identifier found, please read the documentation and specify using parameter derivedBioAssayDataIDs")
  }

  clas <- class( norm )
  if( attributes( clas )$package == "marray"){
    if( clas[1] == "marrayNorm" ){

      numberOfHybs <- length( norm@maM[1,] )
      numberOfFeatures <- length( norm@maM[,1] )
 
      if( length( externalDataFiles ) != numberOfHybs ){
        stop( "number of files in 'files' list should equal number of columns in maM slot of norm object" )
      }
     qtdimsize <- as.integer(1)
      .Call("createNewQTDim", qtdimsize, mageOM,..rmagemlJVM@jvmptr, PACKAGE="RMAGEML")
      .Call("addQuantitationTypeInfo", qtID, qtName, qtDescription, qtScale, qtDataType,"derived", mageOM,..rmagemlJVM@jvmptr, PACKAGE="RMAGEML")
      for (i in 1: length(externalDataFiles)){
        BAD <- BADIDs[ i ]
        derivedBioAssayID <- derivedBioAssayIDs[ i ]
        derivedBioAssayDataID <- derivedBioAssayDataIDs[ i ]
        file <- externalDataFiles[ i ]
        sizeMBA <- as.integer(1)
        .Call("createNewMBASet", sizeMBA, mageOM, ..rmagemlJVM@jvmptr, PACKAGE="RMAGEML")
        rawVal <- as.character(rawDataFiles[i])
        .Call( "setMBA",  rawVal, mageOM, ..rmagemlJVM@jvmptr, PACKAGE="RMAGEML")
       .Call( "updateMAGEML", file, protocolID, protocol, date, qtDimID, transformationID, numberOfFeatures,arrayID, DED, BAD, derivedBioAssayID, derivedBioAssayDataID, mageOM, ..rmagemlJVM@jvmptr, PACKAGE="RMAGEML")
      }                                   #write the external data files

      for( i in 1:numberOfHybs ){
        writeLines(paste("writing ExternalData file:", externalDataFiles[i], sep = " "))
        write.table( norm@maM[,i], file = paste(outputDirectory,externalDataFiles[i],sep = "/"),sep = "\t", col.names = FALSE, row.names = FALSE, quote = FALSE )
      }
    }
    else{
      stop( "MAGEML write error: your marray object was not of class marrayNorm" )
    }
  }

  else{
    if( attributes( clas )$package == "limma" ){

      numberOfHybs <- length( norm$M[1,] )
      numberOfFeatures <- length( norm$M[,1] )

      if( length( externalDataFiles ) != numberOfHybs )
        stop( "number of files in 'files' list should equal number of columns in maM slot of norm object" )

      qtdimsize <- as.integer(1)
      .Call("createNewQTDim", qtdimsize, mageOM,..rmagemlJVM@jvmptr, PACKAGE="RMAGEML")
      .Call("addQuantitationTypeInfo", qtID, qtName, qtDescription, qtScale, qtDataType,"derived", mageOM,..rmagemlJVM@jvmptr, PACKAGE="RMAGEML")
      for (i in 1: length(externalDataFiles)){
        BAD <- BADIDs[ i ]
        derivedBioAssayID <- derivedBioAssayIDs[ i ]
        derivedBioAssayDataID <- derivedBioAssayDataIDs[ i ]
        file <- externalDataFiles[ i ]
        sizeMBA <- as.integer(1)
        .Call("createNewMBASet", sizeMBA, mageOM, ..rmagemlJVM@jvmptr, PACKAGE="RMAGEML")
        rawVal <- as.character(rawDataFiles[i])
        .Call( "setMBA",  rawVal, mageOM, ..rmagemlJVM@jvmptr, PACKAGE="RMAGEML")
        .Call( "updateMAGEML", file, protocolID, protocol, date, qtDimID, transformationID, numberOfFeatures,arrayID, DED, BAD, derivedBioAssayID, derivedBioAssayDataID, mageOM, ..rmagemlJVM@jvmptr, PACKAGE="RMAGEML")
           }                                   #write the external data files

      for( i in 1:numberOfHybs ){
        writeLines(paste("writing ExternalData file:", externalDataFiles[i], sep = " "))
        write.table( norm$M[,i], file = paste(outputDirectory,externalDataFiles[i],sep = "/"),sep = "\t", col.names = FALSE, row.names = FALSE, quote = FALSE )
      }
    }
    else{
      warning( "MAGEML write error: your R object was not a limma or marray object" )
    }
  }
}


########################################
#
#Adding a new derived data file to
#an existing document
#NOTE: this method is more general than
#addNormToMAGEML
#
########################################

addDerivedData <- function(mageOM = NULL, data = NULL, outputDirectory = ".", externalDataFile = NULL, protocolID = NULL, protocol = "none", date = "NA", qtIDs = NULL, qtNames = NULL, qtDescriptions = NULL, qtScales = NULL, qtDataTypes = NULL, qtTypes = NULL, qtDimID = NULL, transformationID = NULL, arrayID = "none", DED = "none", BAD = NULL, derivedBioAssayID = NULL, derivedBioAssayDataID = NULL, rawDataFiles = NULL ){
  
  numberOfFeatures <- 0
                                        #some checks to see if the necessary parameters are present and valid

  if ( is.null( externalDataFile )){ #check as well if they end with txt!!
    stop("No filenames for output derivedFile found, please read the documentation and specify using parameter derivedFiles")  }

  if ( is.null( protocolID )){
    stop("No protocol idenitifier found, please read the documentation and  specify using parameter protocolID")
  }
  
  if ( is.null(qtTypes)){
    stop("No QuantitationType specification found, please read the documentation and add argument qtTypes with values either derived or specialized")
  }
  if ( is.null( transformationID )){
    stop("No transformation idenitifier found, please read the documentation and  specify using parameter protocolID")
  }
  if ( is.null( protocol )){
    stop("No protocol found, please read the documentation and specify using parameter protocol")
  }

  if ( is.null( qtIDs )){
    stop("No QuantitationTypes identifiers found, please read the documentation and specify using parameter qtIDs")
  }

  if ( is.null( qtNames ) && !is.null( qtIDs )){
    warning("No QuantitationTypes names found, continuing without names for QuantitationTypes")
  }
  if ( is.null( qtDescriptions )){      
    stop("No Description of QuantitationTypes found, please read the documentation and specify using parameter qtDescription")
  }
 
  if ( is.null( qtScales )){
    stop("No QuantitationType scales found, please read the documentation and specify using parameter qtScales")
  }

  if ( is.null( qtDataTypes )){
    stop("No QuantitationType DataTypes found, please read the documentation and specify using parameter qtDataTypes")
  }

  if ( is.null( qtDimID )){
    stop("No QuantitationTypeDimension identifier found, please read the documentation and specify using parameter qtDimID")
  }

  if ( is.null( BAD )){
    stop("No BioAssayDimension identifier found, please read the documentation and specify using parameter BAD")
  }

  if ( is.null( derivedBioAssayID )){
    stop("No derivedBioAssay identifier found, please read the documentation and specify using parameter derivedBioAssayID")
  }


  if ( is.null( derivedBioAssayDataID )){
    stop("No derivedBioAssayData identifier found, please read the documentation and specify using parameter derivedBioAssayDataIDs")
  }

  if (is(data, "numeric") || is(data, "matrix")) {
    if(is(data, "numeric")) {
      numberOfFeatures <- length( data )
    }
    if(is(data, "matrix")) {
      numberOfFeatures <- length( data[,1] )
    }

                                        #Add stuff to MAGEML files
    sizeQTDim <- length(qtIDs)
    .Call("createNewQTDim", sizeQTDim, mageOM,..rmagemlJVM@jvmptr, PACKAGE="RMAGEML")
   
    for( i in 1: length(qtIDs)){
      qtID <- qtIDs[ i ]
      qtName <- qtNames[ i ]
      qtScale <- qtScales[ i ]
      qtDataType <- qtDataTypes[ i ]
      qtDescription <- qtDescriptions[ i ]
      qtType <- qtTypes[ i ]
      if(qtType == "derived" || qtType == "specialized"){
        .Call("addQuantitationTypeInfo", qtID, qtName, qtDescription, qtScale, qtDataType,qtType, mageOM,..rmagemlJVM@jvmptr, PACKAGE="RMAGEML")
      }
      else{
        stop("MAGEML update failure:  wrong qtType was given, only values 'derived' or 'specialized' are allowed")
      }
    }

    size <- length( rawDataFiles )
    .Call("createNewMBASet", size, mageOM, ..rmagemlJVM@jvmptr, PACKAGE="RMAGEML")
  
    for( i in 1:length( rawDataFiles )){
      rawVal <- as.character(rawDataFiles[i])
      .Call( "setMBA",  rawVal, mageOM, ..rmagemlJVM@jvmptr, PACKAGE="RMAGEML")
     }
    .Call( "updateMAGEML", externalDataFile, protocolID, protocol, date, qtDimID, transformationID, numberOfFeatures,arrayID, DED, BAD, derivedBioAssayID, derivedBioAssayDataID, mageOM, ..rmagemlJVM@jvmptr, PACKAGE="RMAGEML")

                                        #write the external data files

    writeLines( paste( "writing ExternalData file:", externalDataFile, sep = " " ))
    write.table( data, file = paste( outputDirectory, externalDataFile, sep = "/" ), sep = "\t", col.names = FALSE, row.names = FALSE, quote = FALSE )
  }
  else{
    warning("update not supported for this data type, use addNormToMAGEML() if you want to add limma or marray object to MAGEML or create a matrix with the appropriate values first and try this function again")
  }
}


######################################################
#
#After updating MAGEML this function can be used to
#create a real MAGEML document
#
#####################################################

writeMAGEML <- function ( mageOM = NULL, directory = ".", file = NULL){

  magemlOutFile <- paste( directory, file, sep = "/" )
  writeLines( paste( "writing MAGEML file:", magemlOutFile, sep=" " ))
  .Call("writeMAGEML", file, directory, mageOM, ..rmagemlJVM@jvmptr, PACKAGE="RMAGEML")

}

#########################################################
#
#Functions to get information other
#than the information needed to build raw
#object to normalise.
#
#########################################################

getQuantitationTypes <- function ( mageOM = NULL, QTD = "none"){
  
   if(is.null(mageOM)){
    stop("RMAGEML Error:  Import MAGE Object Model first using the function importMAGEOM")
  }
 
  quantitationTypes <- .Call("getQuantitationTypes", QTD, mageOM, ..rmagemlJVM@jvmptr, PACKAGE="RMAGEML");
  return( quantitationTypes )

}


#retrieve identifier of organization

getOrganization <- function ( mageOM = NULL ){

  if(is.null(mageOM)){
    stop("RMAGEML Error:  Import MAGE Object Model first using the function importMAGEOM")
  }
  organization <- .Call("getVoidStr","getOrganization",mageOM, ..rmagemlJVM@jvmptr, PACKAGE="RMAGEML");
 return( organization )
}


getSubmitterAddress <- function ( mageOM = NULL ){

  if(is.null(mageOM)){
    stop("RMAGEML Error:  Import MAGE Object Model first using the function importMAGEOM")
  }
  address <- .Call("getVoidStr","getSubmitterAddress",mageOM, ..rmagemlJVM@jvmptr, PACKAGE="RMAGEML");
 return( address )
}

#retrieve number of features for a specific Design Element Dimension

getNumberOfFeatures <- function ( mageOM = NULL, DED = "none"){
  
  if(is.null(mageOM)){
    stop("RMAGEML Error:  Import MAGE Object Model first using the function importMAGEOM")
  }
  writeLines("in getNumberOfFeatures")
  .Call("getVoidVoid","init", mageOM, ..rmagemlJVM@jvmptr, PACKAGE="RMAGEML");
  writeLines("called void void")
  featuresInDED <- .Call("getSizeDED", DED, mageOM, ..rmagemlJVM@jvmptr, PACKAGE="RMAGEML");
  writeLines("called getSizeDED")
  return( featuresInDED )
}


#retieve array accession No of arrays that were used

getArrayID <- function( mageOM = NULL ){
  
  if(is.null(mageOM)){
    stop("RMAGEML Error:  Import MAGE Object Model first using the function importMAGEOM")
  }
  arrayIDs <- .Call("getVoidArrayStr","getArrayIDs",mageOM, ..rmagemlJVM@jvmptr, PACKAGE="RMAGEML");
  return(arrayIDs)
  
}

#retrieve Description of a QuantitationType

getQTypeDescription <- function(mageOM = NULL, QTypeID = ""){
  
  if(is.null(mageOM)){
    stop("RMAGEML Error:  Import MAGE Object Model first using the function importMAGEOM")
  }
  description = .Call("getQuantitationTypeDescription", QTypeID, mageOM, ..rmagemlJVM@jvmptr, PACKAGE="RMAGEML")
  return(description)
}


#retrieves QuantitationTypeDimensions

getQTDimensions <- function(mageOM = NULL){
  
  if(is.null(mageOM)){
    stop("RMAGEML Error:  Import MAGE Object Model first using the function importMAGEOM")
  }
  dim <- .Call("getVoidArrayStr","getQuantitationTypeDimensions",mageOM, ..rmagemlJVM@jvmptr, PACKAGE="RMAGEML");
  return(dim)
}

reset <- function(mageOM = NULL){
 	
  if(is.null(mageOM)){
    stop("RMAGEML Error:  Import MAGE Object Model first using the function importMAGEOM")
  }
  
  .Call("getVoidVoid","resetSelections", mageOM, ..rmagemlJVM@jvmptr, PACKAGE="RMAGEML");

}
