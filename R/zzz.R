
# need to do something to ensure destruction of JVM
# when package is unloaded

.onLoad <- function(libname,pkgname)
 {
 msg <- paste("\n*** Deprecation warning ***:\nThe package '%s' is",
   "deprecated and will not be supported\nafter Bioconductor",
    "release 2.9.\n\n")
 packageStartupMessage(sprintf(msg, pkgname))
 library.dynam("RMAGEML",pkgname,libname)
 require(methods)
 .Call("rmageml_init", PACKAGE="RMAGEML")	 
 if(.Platform$OS.type == "windows" && require(Biobase) && interactive() && .Platform$GUI ==  "Rgui"){
    addVigs2WinMenu("RMAGEML")
  }
}
