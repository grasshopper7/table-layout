
This project combines the code of the [easytable](https://github.com/vandeseer/easytable) 
and [pdfbox-layout](https://github.com/ralfstuckert/pdfbox-layout) repositories.


The [easytable](https://github.com/vandeseer/easytable) code is based on version 0.8.2. 
The credit for this code belongs to the [original author](https://github.com/vandeseer). 
The following changes are made to the easytable code:

*   org.vandeseer.easytable.split package for dividing rows with row span of 1. 
*   Access modifications in multiple classes to enable the split logic.

The reason for creating a separate easytable based repository is that the row splitting
scope is limited to cells with row span value of 1.


The [pdfbox-layout](https://github.com/ralfstuckert/pdfbox-layout) code has been extracted 
from version 1.0.0 jar. The credit for this code belongs to the 
[original author](https://github.com/ralfstuckert/pdfbox-layout). 

The reason for creating a separate pdfbox-layout based repository is that the code base 
is no longer actively maintained. The latest jar version 1.0.0, available in jitpack 
repository is reporting a build error.
