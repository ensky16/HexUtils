#include "stdio.h"
#include "string.h"

/****************************
* write a bin file
*
****************************/

  
FILE * writeBinFileInit(char * fileName) 
{
    FILE *file = fopen(fileName, "wb");
    if (file == NULL)
        return NULL;
    else 
		return file; 
}

int writeBinFileUpdte(FILE * file, unsigned char * inputData, int inputDataLen)
{
	int i=0;
	for(i=0; i<inputDataLen; i++) 
	{	     
	    fwrite(inputData+i, sizeof(unsigned char), 1, file);
	 
    }
}
 
 
int writeBinFileFinal(FILE * filePointer)
{
 	 fclose(filePointer);
}
 



