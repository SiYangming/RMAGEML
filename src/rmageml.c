#include <stdio.h>

#include <R.h>
#include <Rdefines.h>
#include <Rinternals.h>

#include <jni.h>

#define PATH_SEPARATOR ';' /* define it to be ':' on Solaris */

#define ARJI_CUR_JNI_VERSION JNI_VERSION_1_2

static SEXP JNIEnv_type_tag;
static SEXP JavaVM_type_tag;
static SEXP JavaClass_type_tag;
static SEXP JavaObject_type_tag;
static SEXP JavaMethod_type_tag;
static SEXP JavaMethodID_type_tag;
static SEXP JavaFieldID_type_tag;
static SEXP jvalueptr_type_tag;
static SEXP Dead_JavaVM_type_tag;

#define CHECK_JavaVM(s) do { \
    if (TYPEOF(s) != EXTPTRSXP || \
        R_ExternalPtrTag(s) != JavaVM_type_tag) \
        error("bad JavaVM reference"); \
    if ( (JavaVM *)R_ExternalPtrAddr(s) == (JavaVM *)NULL ) \
	error("JavaVM pointer is null"); \
} while (0)

SEXP rmageml_init(void)
{
	JNIEnv_type_tag = install("JNIENV_TYPE_TAG");
	JavaVM_type_tag = install("JAVAVM_TYPE_TAG");
	JavaClass_type_tag = install("JAVACLASS_TYPE_TAG");
	JavaObject_type_tag = install("JAVAOBJECT_TYPE_TAG");
	JavaMethodID_type_tag = install("JAVAMETHODID_TYPE_TAG");
	JavaFieldID_type_tag = install("JAVAFIELDID_TYPE_TAG");
	Dead_JavaVM_type_tag = install("DEAD_JAVAVM_TYPE_TAG");
	jvalueptr_type_tag = install("JVALUEPTR_TYPE_TAG");
	return R_NilValue;
}

SEXP arji_createJVM(SEXP optstr) {
	JNIEnv *env;
	JavaVM *jvm;
	jint res;
	SEXP retptr;
	JavaVMInitArgs vm_args;
	JavaVMOption options[2];

/* set up the JVM configuration */
        char *optionStr =
	  (char *) R_alloc(length(STRING_ELT(optstr, 0)) + 1,
			   sizeof(char)); /* freed on return */
	strcpy(optionStr, CHAR(STRING_ELT(optstr, 0)));
	options[0].optionString =  optionStr;
        options[1].optionString = "-Xmx256m";
	vm_args.version = 0x00010002;
	vm_args.options = options;
	vm_args.nOptions = 2;
	vm_args.ignoreUnrecognized = JNI_TRUE;

/* create JVM and collect the JNIenvironment */
	res = JNI_CreateJavaVM( &jvm, (void**)&env, &vm_args);
	if (res < 0) {
		Rprintf("Can't create Java VM\n");
		return(R_NilValue);
		}

/* return an R external pointer to the jvm; you can */
/* get the JNIenv from it */
        retptr = R_MakeExternalPtr( jvm, JavaVM_type_tag, R_NilValue );
	return(retptr);
	}

SEXP arji_destroyJavaVM(SEXP JavaVMRef) {
	JavaVM *thisjvm;
	CHECK_JavaVM(JavaVMRef);
    	thisjvm = (JavaVM *)R_ExternalPtrAddr(JavaVMRef);
	(*thisjvm)->DestroyJavaVM(thisjvm);
        printf("JVM destroyed\n");
	return(R_NilValue);
	}
SEXP destroyOM(SEXP rmagestkptr,SEXP jvmptr){
  JNIEnv *env;
  JavaVM *jvm;
  jobject rmagestk;
 
  CHECK_JavaVM(jvmptr);
  jvm = (JavaVM *)R_ExternalPtrAddr(jvmptr);
  ((*jvm)->GetEnv(jvm, (void **) &env, ARJI_CUR_JNI_VERSION));
  rmagestk = (jobject) R_ExternalPtrAddr(rmagestkptr);
  (*env) -> DeleteGlobalRef(env, rmagestk);
}

SEXP newRMAGESTK(SEXP directory,SEXP jvmptr){
  JNIEnv *env;
  JavaVM *jvm;
  jclass cls; 
  jobject rmagestk;
  SEXP retObj;
  jmethodID mid;
  jstring jstr;
  
  /* verify the passed ref to JVM and collect JNI env */ 
  CHECK_JavaVM(jvmptr);
  jvm = (JavaVM *)R_ExternalPtrAddr(jvmptr);
  ((*jvm)->GetEnv(jvm, (void **) &env, ARJI_CUR_JNI_VERSION));

  /*now find the class and pass back a reference */
  cls = (*env)->FindClass(env, "MAGEML/RMAGESTK");
  mid = (*env)->GetMethodID(env, cls, "<init>","(Ljava/lang/String;)V");
  jstr = (*env)->NewStringUTF(env, CHAR(STRING_ELT(directory,0))); 
  rmagestk = (*env)->NewObject(env,cls,mid,jstr);
  /*  (*env)->ReleaseStringUTFChars(env, jstr, directory); */
  retObj = R_MakeExternalPtr( rmagestk, JavaObject_type_tag, R_NilValue );
  return(retObj);
}

SEXP importFeatures(SEXP package, SEXP arrayID, SEXP DED, SEXP db, SEXP rmagestkptr, SEXP jvmptr){
  JNIEnv *env;
  JavaVM *jvm;
  jclass cls; 
  jobject rmagestk;
  jmethodID mid;
  jstring jpackage;
  jstring jarrayID;
  jstring jDED;
  jstring jdb;

  CHECK_JavaVM(jvmptr);
  jvm = (JavaVM *)R_ExternalPtrAddr(jvmptr);
  ((*jvm)->GetEnv(jvm, (void **) &env, ARJI_CUR_JNI_VERSION));

  rmagestk = (jobject) R_ExternalPtrAddr(rmagestkptr);
  
  cls = (*env)->GetObjectClass(env, rmagestk);
  mid = (*env)->GetMethodID(env, cls,"importFeatures", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Z");
  jpackage = (*env)->NewStringUTF(env, CHAR(STRING_ELT(package,0)));
  jarrayID = (*env)->NewStringUTF(env, CHAR(STRING_ELT(arrayID,0)));
  jDED = (*env)->NewStringUTF(env, CHAR(STRING_ELT(DED,0)));
  jdb = (*env)->NewStringUTF(env, CHAR(STRING_ELT(db,0)));

  (*env)->CallObjectMethod(env, rmagestk, mid, jpackage,jarrayID,jDED,jdb);
  /*  (*env)->ReleaseStringUTFChars(env,jpackage);
  (*env)->ReleaseStringUTFChars(env,jarrayID);
  (*env)->ReleaseStringUTFChars(env,jDED);
  (*env)->ReleaseStringUTFChars(env,jdb);*/

  return(R_NilValue);
}

SEXP getGridDimensions(SEXP arrayID, SEXP DED, SEXP rmagestkptr, SEXP jvmptr){
  JNIEnv *env;
  JavaVM *jvm;
  jclass cls; 
  jobject rmagestk;
  jmethodID mid;
  jintArray result;
  jstring jarrayID;
  jstring jDED;
  jint i = 0;
  
  SEXP ret;

  CHECK_JavaVM(jvmptr);
  jvm = (JavaVM *)R_ExternalPtrAddr(jvmptr);
  ((*jvm)->GetEnv(jvm, (void **) &env, ARJI_CUR_JNI_VERSION));

  rmagestk = (jobject) R_ExternalPtrAddr(rmagestkptr);
  
  cls = (*env)->GetObjectClass(env, rmagestk);
  mid = (*env)->GetMethodID(env, cls, "getGridDimensions", "(Ljava/lang/String;Ljava/lang/String;)[I");
  
  jarrayID = (*env)->NewStringUTF(env, CHAR(STRING_ELT(arrayID,0)));
  jDED = (*env)->NewStringUTF(env, CHAR(STRING_ELT(DED,0)));
  
  result  = (*env)->CallObjectMethod(env, rmagestk, mid, jarrayID, jDED);
  /*  (*env)->ReleaseStringUTFChars(env,jDED);
      (*env)->ReleaseStringUTFChars(env,jarrayID);*/
 
  jint buf[4];

  (*env)->GetIntArrayRegion(env, result,0,4,buf);

  PROTECT(ret = allocVector(INTSXP, 4));
   
  for(i = 0; i < 4; i++){
    INTEGER(ret)[i]= buf[i]; 
  }
  UNPROTECT(1);
  
  return(ret);
}


SEXP selectQTypesDeriv(SEXP QTD, SEXP deriv, SEXP rmagestkptr, SEXP jvmptr){
  JNIEnv *env;
  JavaVM *jvm;
  jclass cls; 
  jobject rmagestk;
  jmethodID mid;
  jintArray result;
  jstring jQTD;
  jstring jderiv;
  SEXP ret;

  CHECK_JavaVM(jvmptr);
  jvm = (JavaVM *)R_ExternalPtrAddr(jvmptr);
  ((*jvm)->GetEnv(jvm, (void **) &env, ARJI_CUR_JNI_VERSION));

  rmagestk = (jobject) R_ExternalPtrAddr(rmagestkptr);
  cls = (*env)->GetObjectClass(env, rmagestk);
  mid = (*env)->GetMethodID(env, cls, "selectQTypesDeriv", "(Ljava/lang/String;Ljava/lang/String;)I");
  jQTD = (*env)->NewStringUTF(env, CHAR(STRING_ELT(QTD,0)));
  jderiv = (*env)->NewStringUTF(env, CHAR(STRING_ELT(deriv,0)));
  
  result  = (*env)->CallObjectMethod(env, rmagestk, mid, jQTD, jderiv);
  jint buf[1];

  (*env)->GetIntArrayRegion(env, result,0,1,buf);

  PROTECT(ret = allocVector(INTSXP, 1));
  INTEGER(ret)[0]= buf[0]; 
  UNPROTECT(1);
  
  return(ret);
}


SEXP getSizeDED(SEXP DED, SEXP rmagestkptr, SEXP jvmptr){
  JNIEnv *env;
  JavaVM *jvm;
  jclass cls; 
  jobject rmagestk;
  jmethodID mid;
  jintArray result;
  jstring jDED;
  SEXP ret;

  printf("in getSizeDED\n");
  CHECK_JavaVM(jvmptr);
  jvm = (JavaVM *)R_ExternalPtrAddr(jvmptr);
  ((*jvm)->GetEnv(jvm, (void **) &env, ARJI_CUR_JNI_VERSION));
  printf("did JVM check\n");
  rmagestk = (jobject) R_ExternalPtrAddr(rmagestkptr);
  printf("got rmagestk\n");

  cls = (*env)->GetObjectClass(env, rmagestk);
  if(cls == 0){
    printf("no such class found getSizeDED\n");
  } 
  printf("got class\n");

  mid = (*env)->GetMethodID(env, cls, "getSizeDED", "(Ljava/lang/String;)I");
  if(mid==0){
    printf("no such method found getSizeDED\n");
  }
  printf("got method\n");

  jDED = (*env)->NewStringUTF(env, CHAR(STRING_ELT(DED,0)));
  
  result  = (*env)->CallObjectMethod(env, rmagestk, mid, jDED);
  /*  (*env)->ReleaseStringUTFChars(env,jDED);*/
  jint buf[1];

  (*env)->GetIntArrayRegion(env, result,0,1,buf);
  printf("got the array region\n");

  PROTECT(ret = allocVector(INTSXP, 1));
  INTEGER(ret)[0]= buf[0]; 
  UNPROTECT(1);
  
  return(ret);
}

SEXP selectQTypes(SEXP QTD, SEXP rf, SEXP rb, SEXP gf, SEXP gb, SEXP rmagestkptr, SEXP jvmptr){
  JNIEnv *env;
  JavaVM *jvm;
  jclass cls; 
  jobject rmagestk;
  jmethodID mid;
  jintArray result;
  jstring jQTD;
  jstring jrb;
  jstring jrf;
  jstring jgf;
  jstring jgb;
  jint i = 0;
  SEXP ret;

  CHECK_JavaVM(jvmptr);
  jvm = (JavaVM *)R_ExternalPtrAddr(jvmptr);
  ((*jvm)->GetEnv(jvm, (void **) &env, ARJI_CUR_JNI_VERSION));

  rmagestk = (jobject) R_ExternalPtrAddr(rmagestkptr);
  cls = (*env)->GetObjectClass(env, rmagestk);
  
  mid = (*env)->GetMethodID(env, cls, "selectQTypes", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)[I");
  
  jQTD = (*env)->NewStringUTF(env, CHAR(STRING_ELT(QTD,0)));
  jrf = (*env)->NewStringUTF(env, CHAR(STRING_ELT(rf,0)));
  jrb = (*env)->NewStringUTF(env, CHAR(STRING_ELT(rb,0)));
  jgf = (*env)->NewStringUTF(env, CHAR(STRING_ELT(gf,0)));
  jgb = (*env)->NewStringUTF(env, CHAR(STRING_ELT(gb,0)));
  
  result  = (*env)->CallObjectMethod(env, rmagestk, mid, jQTD, jrf, jrb, jgf, jgb);

  /*  (*env)->ReleaseStringUTFChars(env,jQTD);
  (*env)->ReleaseStringUTFChars(env,jrf);
  (*env)->ReleaseStringUTFChars(env,jrb);
  (*env)->ReleaseStringUTFChars(env,jgf);
  (*env)->ReleaseStringUTFChars(env,jgb);*/
  
  jint buf[4];

  (*env)->GetIntArrayRegion(env, result,0,4,buf);

  PROTECT(ret = allocVector(INTSXP, 4));
   
  for(i = 0; i < 4; i++){
    INTEGER(ret)[i]= buf[i]; 
  }
  UNPROTECT(1);
  
  return(ret);
}



SEXP getExternalData(SEXP package, SEXP arrayID, SEXP DED, SEXP rmagestkptr, SEXP jvmptr){
  JNIEnv *env;
  JavaVM *jvm;
  jclass cls; 
  jobject rmagestk;
  jmethodID mid;
  jobjectArray result;
  jstring jarrayID;
  jstring jDED;
  jstring jpackage;
  jsize alen;
  int j = 0;
  
  SEXP ret;
 
  CHECK_JavaVM(jvmptr);
  jvm = (JavaVM *)R_ExternalPtrAddr(jvmptr);
  ((*jvm)->GetEnv(jvm, (void **) &env, ARJI_CUR_JNI_VERSION));

  rmagestk = (jobject) R_ExternalPtrAddr(rmagestkptr);
  cls = (*env)->GetObjectClass(env, rmagestk);
  
  mid = (*env)->GetMethodID(env, cls, "getExternalData", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)[Ljava/lang/String;");
  jarrayID = (*env)->NewStringUTF(env, CHAR(STRING_ELT(arrayID,0)));
  jDED = (*env)->NewStringUTF(env, CHAR(STRING_ELT(DED,0)));
  jpackage = (*env)->NewStringUTF(env, CHAR(STRING_ELT(package,0)));
 
  result  = (*env)->CallObjectMethod(env, rmagestk, mid, jpackage, jarrayID, jDED);
  /*  (*env)->ReleaseStringUTFChars(env,jarrayID);
  (*env)->ReleaseStringUTFChars(env,jDED);
  (*env)->ReleaseStringUTFChars(env,jpackage); */
  alen = (*env)->GetArrayLength(env, result);
  PROTECT(ret = allocVector(STRSXP, alen));
 
  for(j = 0; j < alen; j++){
    jstring jstr = (*env)->GetObjectArrayElement(env, result, j);
    const char *str = (*env)->GetStringUTFChars(env, jstr, 0);
    SET_STRING_ELT(ret, j, mkChar((char *) str)); 
    (*env)->ReleaseStringUTFChars(env, jstr, str);
  }

  UNPROTECT(1);

  return(ret);
}


SEXP getQuantitationTypes(SEXP QTD, SEXP rmagestkptr, SEXP jvmptr){
  JNIEnv *env;
  JavaVM *jvm;
  jclass cls; 
  jobject rmagestk;
  jmethodID mid;
  jobjectArray result;
  jstring jQTD;
  jsize alen;
  int j = 0;
  
  SEXP ret;
 
  CHECK_JavaVM(jvmptr);
  jvm = (JavaVM *)R_ExternalPtrAddr(jvmptr);
  ((*jvm)->GetEnv(jvm, (void **) &env, ARJI_CUR_JNI_VERSION));

  rmagestk = (jobject) R_ExternalPtrAddr(rmagestkptr);
  
  cls = (*env)->GetObjectClass(env, rmagestk);
  mid = (*env)->GetMethodID(env, cls, "getQuantitationTypes", "(Ljava/lang/String;)[Ljava/lang/String;");
  jQTD = (*env)->NewStringUTF(env, CHAR(STRING_ELT(QTD,0)));
  result  = (*env)->CallObjectMethod(env, rmagestk, mid, jQTD);
  /* (*env)->ReleaseStringUTFChars(env,jQTD);*/

  alen = (*env)->GetArrayLength(env, result);
  PROTECT(ret = allocVector(STRSXP, alen));
 
  for(j = 0; j < alen; j++){
    jstring jstr = (*env)->GetObjectArrayElement(env, result, j);
    const char *str = (*env)->GetStringUTFChars(env, jstr, 0);
    SET_STRING_ELT(ret, j, mkChar((char *) str));
    (*env)->ReleaseStringUTFChars(env, jstr, str); 
  }

  UNPROTECT(1);

  return(ret);
}


SEXP getQTypeDescription(SEXP QTypeID, SEXP rmagestkptr, SEXP jvmptr){
  JNIEnv *env;
  JavaVM *jvm;
  jclass cls; 
  jobject rmagestk;
  jmethodID mid;
  jobjectArray result;
  jstring jQTypeID;
  SEXP ret;

  CHECK_JavaVM(jvmptr);
  jvm = (JavaVM *)R_ExternalPtrAddr(jvmptr);
  ((*jvm)->GetEnv(jvm, (void **) &env, ARJI_CUR_JNI_VERSION));

  rmagestk = (jobject) R_ExternalPtrAddr(rmagestkptr);
  cls = (*env)->GetObjectClass(env, rmagestk);

  jQTypeID = (*env)->NewStringUTF(env, CHAR(STRING_ELT(QTypeID,0)));
  mid = (*env)->GetMethodID(env, cls, "getQuantitationTypeDescription", "()Ljava/lang/String;");
  result  = (*env)->CallObjectMethod(env, rmagestk, mid, jQTypeID);
  /* (*env)->ReleaseStringUTFChars(env,jQTypeID);*/
 
  const char *str = (*env)->GetStringUTFChars(env, result, 0);
  PROTECT(ret = allocVector(STRSXP, 1));
  SET_STRING_ELT(ret, 0, mkChar((char *) str));
  (*env)->ReleaseStringUTFChars(env, jQTypeID, str);

  UNPROTECT(1);
  return(ret);
}


/*update functions*/

SEXP createNewQTDim(SEXP qtdimsize, SEXP rmagestkptr, SEXP jvmptr){
  JNIEnv *env;
  JavaVM *jvm;
  jclass cls; 
  jobject rmagestk;
  jmethodID mid;
  jint jqtdimsize;
 
  CHECK_JavaVM(jvmptr);
  jvm = (JavaVM *)R_ExternalPtrAddr(jvmptr);
  ((*jvm)->GetEnv(jvm, (void **) &env, ARJI_CUR_JNI_VERSION));

  rmagestk = (jobject) R_ExternalPtrAddr(rmagestkptr);  
  cls = (*env)->GetObjectClass(env, rmagestk);
  mid = (*env)->GetMethodID(env, cls, "createNewQTDim", "(I)V");
  qtdimsize = coerceVector(qtdimsize, INTSXP);
  jqtdimsize = (jint) INTEGER(qtdimsize)[0];
  (*env)->CallObjectMethod(env, rmagestk, mid, jqtdimsize);
  return(R_NilValue);
}

SEXP addQuantitationTypeInfo(SEXP qtID, SEXP qtName, SEXP qtDescription, SEXP qtScale, SEXP qtDataType, SEXP derived, SEXP rmagestkptr, SEXP jvmptr){
  JNIEnv *env;
  JavaVM *jvm;
  jclass cls; 
  jobject rmagestk;
  jmethodID mid;
  jstring jqtID;
  jstring jqtName;
  jstring jqtDescription;
  jstring jqtScale;
  jstring jqtDataType;
  jstring jderived;
 
  CHECK_JavaVM(jvmptr);
  jvm = (JavaVM *)R_ExternalPtrAddr(jvmptr);
  ((*jvm)->GetEnv(jvm, (void **) &env, ARJI_CUR_JNI_VERSION));

  rmagestk = (jobject) R_ExternalPtrAddr(rmagestkptr);
  cls = (*env)->GetObjectClass(env, rmagestk);
  
  mid = (*env)->GetMethodID(env, cls, "addQuantitationTypeInfo", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V");
  jqtID = (*env)->NewStringUTF(env, CHAR(STRING_ELT(qtID,0)));
  jqtName = (*env)->NewStringUTF(env, CHAR(STRING_ELT(qtName,0)));
  jqtDescription = (*env)->NewStringUTF(env, CHAR(STRING_ELT(qtDescription,0)));
  jqtScale = (*env)->NewStringUTF(env, CHAR(STRING_ELT(qtScale,0)));
  jqtDataType = (*env)->NewStringUTF(env, CHAR(STRING_ELT(qtDataType,0)));
  jderived = (*env)->NewStringUTF(env, CHAR(STRING_ELT(derived,0)));

 (*env)->CallObjectMethod(env, rmagestk, mid, jqtID, jqtName, jqtDescription, jqtScale, jqtDataType, jderived);
 /* 
  (*env)->ReleaseStringUTFChars(env,jqtID);
  (*env)->ReleaseStringUTFChars(env,jqtName);
  (*env)->ReleaseStringUTFChars(env,jqtDescription);
  (*env)->ReleaseStringUTFChars(env,jqtScale);
  (*env)->ReleaseStringUTFChars(env,jDataType);
  (*env)->ReleaseStringUTFChars(env,jderived);
 */
  return(R_NilValue);
}

SEXP createNewMBASet(SEXP sizeMBA, SEXP rmagestkptr, SEXP jvmptr){
  JNIEnv *env;
  JavaVM *jvm;
  jclass cls; 
  jobject rmagestk;
  jmethodID mid;
  jint jsizeMBA;
 
  CHECK_JavaVM(jvmptr);
  jvm = (JavaVM *)R_ExternalPtrAddr(jvmptr);
  ((*jvm)->GetEnv(jvm, (void **) &env, ARJI_CUR_JNI_VERSION));

  rmagestk = (jobject) R_ExternalPtrAddr(rmagestkptr);
  cls = (*env)->GetObjectClass(env, rmagestk);
  mid = (*env)->GetMethodID(env, cls, "createNewMBASet", "(I)V");
  sizeMBA = coerceVector(sizeMBA, INTSXP);
  jsizeMBA = (jint) INTEGER(sizeMBA)[0];
 
  (*env)->CallObjectMethod(env, rmagestk, mid, jsizeMBA);
  
  return(R_NilValue);
}


SEXP setMBA(SEXP mba, SEXP rmagestkptr, SEXP jvmptr){
  JNIEnv *env;
  JavaVM *jvm;
  jclass cls; 
  jobject rmagestk;
  jmethodID mid;
  jstring jmba;
 
  CHECK_JavaVM(jvmptr);
  jvm = (JavaVM *)R_ExternalPtrAddr(jvmptr);
  ((*jvm)->GetEnv(jvm, (void **) &env, ARJI_CUR_JNI_VERSION));

  rmagestk = (jobject) R_ExternalPtrAddr(rmagestkptr);
  cls = (*env)->GetObjectClass(env, rmagestk);
  mid = (*env)->GetMethodID(env, cls, "setMBA", "(Ljava/lang/String;)V");
  jmba = (*env)->NewStringUTF(env, CHAR(STRING_ELT(mba,0)));
 (*env)->CallObjectMethod(env, rmagestk, mid, jmba);
  
  return(R_NilValue);
}

SEXP updateMAGEML(SEXP file, SEXP protocolID, SEXP protocol, SEXP date, SEXP qtDimID, SEXP transformationID, SEXP numberOfFeatures, SEXP arrayID, SEXP DED, SEXP BAD, SEXP derivedBioAssayID, SEXP derivedBioAssayDataID, SEXP rmagestkptr, SEXP jvmptr){
  JNIEnv *env;
  JavaVM *jvm;
  jclass cls; 
  jobject rmagestk;
  jmethodID mid;
  jstring jfile;
  jstring jprotocolID;
  jstring jprotocol;
  jstring jdate;
  jstring jqtDimID;
  jstring jtransformationID;
  jint jnumberOfFeatures;
  jstring jarrayID;
  jstring jDED;
  jstring jBAD;
  jstring jderivedBioAssayID;
  jstring jderivedBioAssayDataID;
 
 
  CHECK_JavaVM(jvmptr);
  jvm = (JavaVM *)R_ExternalPtrAddr(jvmptr);
  ((*jvm)->GetEnv(jvm, (void **) &env, ARJI_CUR_JNI_VERSION));

  rmagestk = (jobject) R_ExternalPtrAddr(rmagestkptr);
  cls = (*env)->GetObjectClass(env, rmagestk);
  mid = (*env)->GetMethodID(env, cls, "updateMAGEML", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V");
 
  jfile = (*env)->NewStringUTF(env, CHAR(STRING_ELT(file,0)));
  jprotocolID = (*env)->NewStringUTF(env, CHAR(STRING_ELT(protocolID,0)));
  jprotocol = (*env)->NewStringUTF(env, CHAR(STRING_ELT(protocol,0)));
  jdate = (*env)->NewStringUTF(env, CHAR(STRING_ELT(date,0)));
  jqtDimID = (*env)->NewStringUTF(env, CHAR(STRING_ELT(qtDimID,0)));
  jtransformationID = (*env)->NewStringUTF(env, CHAR(STRING_ELT(transformationID,0)));
  jarrayID = (*env)->NewStringUTF(env, CHAR(STRING_ELT(arrayID,0)));
  jDED = (*env)->NewStringUTF(env, CHAR(STRING_ELT(DED,0)));
  jBAD = (*env)->NewStringUTF(env, CHAR(STRING_ELT(BAD,0)));
  jderivedBioAssayID = (*env)->NewStringUTF(env, CHAR(STRING_ELT(derivedBioAssayID,0)));
  jderivedBioAssayDataID = (*env)->NewStringUTF(env, CHAR(STRING_ELT(derivedBioAssayDataID,0)));
  jnumberOfFeatures = (jint) INTEGER(numberOfFeatures);

 (*env)->CallObjectMethod(env, rmagestk, mid, jfile, jprotocolID, jprotocol, jdate, jqtDimID, jtransformationID, jnumberOfFeatures, jarrayID, jDED, jBAD, jderivedBioAssayID, jderivedBioAssayDataID);
  
 /*  (*env)->ReleaseStringUTFChars(env,jfile);
  (*env)->ReleaseStringUTFChars(env,jprotocolID);
  (*env)->ReleaseStringUTFChars(env,jprotocol);
  (*env)->ReleaseStringUTFChars(env,jdate);
  (*env)->ReleaseStringUTFChars(env,jqtDimID);
  (*env)->ReleaseStringUTFChars(env,jtransformationID);
  (*env)->ReleaseStringUTFChars(env,jarrayID);
  (*env)->ReleaseStringUTFChars(env,jDED);
  (*env)->ReleaseStringUTFChars(env,jBAD);
  (*env)->ReleaseStringUTFChars(env,jderivedBioAssayID);*/

  return(R_NilValue);
}


SEXP writeMAGEML(SEXP file, SEXP directory, SEXP rmagestkptr, SEXP jvmptr){
  JNIEnv *env;
  JavaVM *jvm;
  jclass cls; 
  jobject rmagestk;
  jmethodID mid;
  jstring jfile;
  jstring jdirectory;
 
  CHECK_JavaVM(jvmptr);
  jvm = (JavaVM *)R_ExternalPtrAddr(jvmptr);
  ((*jvm)->GetEnv(jvm, (void **) &env, ARJI_CUR_JNI_VERSION));

  rmagestk = (jobject) R_ExternalPtrAddr(rmagestkptr);
  cls = (*env)->GetObjectClass(env, rmagestk);
  mid = (*env)->GetMethodID(env, cls, "writeMAGEML","(Ljava/lang/String;Ljava/lang/String;)V");
  jfile = (*env)->NewStringUTF(env, CHAR(STRING_ELT(file,0)));
  jdirectory = (*env)->NewStringUTF(env, CHAR(STRING_ELT(directory,0)));
  (*env)->CallObjectMethod(env, rmagestk, mid, jfile, jdirectory);
  /*  (*env)->ReleaseStringUTFChars(env,jfile);
      (*env)->ReleaseStringUTFChars(env,jdirectory);*/

  return(R_NilValue);
}


/*general functions*/

SEXP getVoidArrayInt(SEXP jmethod, SEXP rmagestkptr, SEXP jvmptr){
  JNIEnv *env;
  JavaVM *jvm;
  jclass cls; 
  jobject rmagestk;
  jmethodID mid;
  jintArray result;
  jsize alen;
  jint i = 0;
  SEXP ret;

  CHECK_JavaVM(jvmptr);
  jvm = (JavaVM *)R_ExternalPtrAddr(jvmptr);
  ((*jvm)->GetEnv(jvm, (void **) &env, ARJI_CUR_JNI_VERSION));

  rmagestk = (jobject) R_ExternalPtrAddr(rmagestkptr);
  cls = (*env)->GetObjectClass(env, rmagestk);
  mid = (*env)->GetMethodID(env, cls, CHAR(STRING_ELT(jmethod,0)), "()[I");
  result  = (*env)->CallObjectMethod(env, rmagestk, mid);
  alen = (*env)->GetArrayLength(env, result);
  jint buf[alen];

  (*env)->GetIntArrayRegion(env, result,0,alen,buf);

  PROTECT(ret = allocVector(INTSXP, alen));
   
  for(i = 0; i < alen; i++){
    INTEGER(ret)[i]= buf[i]; 
  }
  UNPROTECT(1);
  
  return(ret);
}


SEXP getVoidArrayStr(SEXP jmethod, SEXP rmagestkptr, SEXP jvmptr){
  JNIEnv *env;
  JavaVM *jvm;
  jclass cls; 
  jobject rmagestk;
  jmethodID mid;
  jobjectArray result;
  jsize alen;
  int j = 0;
  SEXP ret;
  
  CHECK_JavaVM(jvmptr);
  jvm = (JavaVM *)R_ExternalPtrAddr(jvmptr);
  ((*jvm)->GetEnv(jvm, (void **) &env, ARJI_CUR_JNI_VERSION));

  rmagestk = (jobject) R_ExternalPtrAddr(rmagestkptr);
  cls = (*env)->GetObjectClass(env, rmagestk);
  mid = (*env)->GetMethodID(env, cls, CHAR(STRING_ELT(jmethod,0)), "()[Ljava/lang/String;");
  result  = (*env)->CallObjectMethod(env, rmagestk, mid);
  alen = (*env)->GetArrayLength(env, result);
  PROTECT(ret = allocVector(STRSXP, alen));
 
  for(j = 0; j < alen; j++){
    jstring jstr = (*env)->GetObjectArrayElement(env, result, j);
    const char *str = (*env)->GetStringUTFChars(env, jstr, 0);
    SET_STRING_ELT(ret, j, mkChar((char *) str)); 
   (*env)->ReleaseStringUTFChars(env, jstr, str);

  }

  UNPROTECT(1);

  return(ret);
}


SEXP getVoidStr(SEXP jmethod, SEXP rmagestkptr, SEXP jvmptr){
  JNIEnv *env;
  JavaVM *jvm;
  jclass cls; 
  jobject rmagestk;
  jmethodID mid;
  jobjectArray result;
  SEXP ret;

  CHECK_JavaVM(jvmptr);
  jvm = (JavaVM *)R_ExternalPtrAddr(jvmptr);
  ((*jvm)->GetEnv(jvm, (void **) &env, ARJI_CUR_JNI_VERSION));

  rmagestk = (jobject) R_ExternalPtrAddr(rmagestkptr);
  cls = (*env)->GetObjectClass(env, rmagestk);
  mid = (*env)->GetMethodID(env, cls, CHAR(STRING_ELT(jmethod,0)), "()Ljava/lang/String;");
  result  = (*env)->CallObjectMethod(env, rmagestk, mid);
  const char *str = (*env)->GetStringUTFChars(env, result, 0);

  PROTECT(ret = allocVector(STRSXP, 1));
  SET_STRING_ELT(ret, 0, mkChar((char *) str));
  UNPROTECT(1);
  return(ret);
}

SEXP getVoidVoid(SEXP jmethod, SEXP rmagestkptr, SEXP jvmptr){
  JNIEnv *env;
  JavaVM *jvm;
  jclass cls; 
  jobject rmagestk;
  jmethodID mid;
 
  CHECK_JavaVM(jvmptr);
  jvm = (JavaVM *)R_ExternalPtrAddr(jvmptr);
  ((*jvm)->GetEnv(jvm, (void **) &env, ARJI_CUR_JNI_VERSION));

  rmagestk = (jobject) R_ExternalPtrAddr(rmagestkptr);
  cls = (*env)->GetObjectClass(env, rmagestk);
  mid = (*env)->GetMethodID(env, cls, CHAR(STRING_ELT(jmethod,0)), "()V");
  (*env)->CallObjectMethod(env, rmagestk, mid);
  
  return(R_NilValue);
}
