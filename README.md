CANopenMWE2CanFestival
======================

Generating c/h code with Modeling Workflow Engine 2 for CanFestival (CANopen Stack) based on XDD/EDS Files from CANeds Objectdictionary description.

Setup
=====

1. Just create or load your existing Objectdictionary in CANeds Tools
2. Export to XDD and save it instead of xdd ending with xml at the end. You can also open the XML File with the 
  CANeds Tools and modify the settings as you like.
3. Than whether you create with eclipse your own runnable jar files or use the pre packaged ones in runnable folder.
4. You have the choice between command line or gui based interface. 
  
  * CLI

    * use the script 
    
    > xml2canfestival path_to_model/name.xml

    in case you have pdflatex installed and basic packages you will get a pdf table of the objectdictionary

  * Gui

    * load the XDD/XML file and go to Edit->Generate to start the process after that you will have a c and h file for CanFestival. Furthermore you will get a tex file which includes a generated latex documentation of your objectdictionary.
