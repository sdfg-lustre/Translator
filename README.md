<!--The timed synchronous dataflow graph <b>SDFG</b> model is a graphical model of computation that allows concurrency between processes.
Lustre is a declarative synchronous language developed to model and verify reactive systems. -->
<pre> 
This application transformes a <b>timed SDFG</b> model to <b> Lustre</b> models. <br>
Three Lustre models are produced :
   -  <b>cycle-by-cycle model</b>: it progresses in time cycle-by-cycle. <br>
   -  <b>merged-cycles model</b>: it progresses in time in larger steps.<br>
   -  <b>untimed model</b>: a lustre model for the untimed SDFG <br>
The formal verification tool <a href="https://kind2-mc.github.io/kind2/"> Kind 2 </a> is used.



*********<b>Contents of the Directory </b>:
   -Examples :  various SDFG models 
         Each example contains three files: the xml description, its lustre model ready to check, and a  list of user-defined properties
 
   -src: contains the source java codes of the two translators
       -SDFG2LUSTRE_cycle_cycle
       -SDFG2LUSTRE_mergedCycles

    -jar: the compiled codes of the two SDFG2Lustre translators
        -SDFG2LUSTRE_cycle_by_cycle.jar 
        -SDFG2LUSTRE_merged_cycles.jar



*********<b>Quick start</b>: verification of a ready-for-use example (verification of the Lustre model of the TDMA spec: TDMA_verified.lus):

1. install the Doker and the Kind2-Docker (tested on ubuntu 16.04):
   >sudo apt install docker.io
   >sudo systemctl start docker
   >sudo docker pull kind2/kind2

2. clone the project from github:
   > git  clone https://github.com/sdfg-lustre/ENISoNOCCS 

3. checking the properties of the examples/TDMA_/TDMA_cycle_by_cycle_verif
   >sudo docker run -v /absolute path to /ENISoNOCCS/Examples/TDMA_/:/lus kind2/kind2 --timeout_wall 9000 /lus/TDMA_verified.lus



*********<b>Steps toward the verify of an SDFG description</b>:

1.  Describe the SDFG into an xml file (SDFGmodel.xml)
2.  Translate the xml to a lustre model:
  >java -jar SDFG2LUSTRE_cycle_cycle.jar SDFGmodel  //without .xml extension
it produces two Lustre files named SDFGmodel_cycle_cycle.lus and SDFGmodel_untimed.lus

 or 
  >java -jar SDFG2LUSTRE_merged_cycles.jar SDFGmodel //without the .xml extension
it produces a Lustre file named SDFGmodel_merged_cycles.lus

3. Edit the lustre model and insert the user-defined properties and assertions

4. launch the model checker:
   > sudo docker run -v /absolute path to the Lustre model/:/lus kind2/kind2   --timeout_wall 90  /lus/SDFG2LUSTRE_cycle_cycle.lus

</pre> 
